import React, { useState, useEffect } from 'react';
import API_URL from '../config/API';

import {
    ComposedChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer,
} from 'recharts';
import { Search, Calendar } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { useAuthFetch } from './useAuthFetch';

interface SentimentData {
    date: number;
    sentiment: string; // 'positive', 'negative', 'neutral'
    confidence: number;
    ticker: string;
    id: number;
}

interface SentimentResponse {
    results: SentimentData[];
    creditsRemaining: number;
}


const TIME_PERIODS: { [key: string]: { days: number; label: string } } = {
    '7D': { days: 7, label: '7 Days' },
    '14D': { days: 14, label: '14 Days' },
    '1M': { days: 30, label: '1 Month' },
    '3M': { days: 90, label: '3 Months' },
};

const GROUPING_OPTIONS = [
    { label: '1 Hour', value: '1H' },
    { label: '2 Hours', value: '2H' },
    { label: '3 Hours', value: '3H' },
    { label: '1 Day', value: '1D' },
    { label: '2 Days', value: '2D' },
    { label: '3 Days', value: '3D' },
    { label: '1 Week', value: '7D' },
    { label: '2 Weeks', value: '14D' },
    { label: '1 Month', value: '30D' },
];

const StockSentimentDashboard: React.FC = () => {
    const [ticker, setTicker] = useState<string>('AAPL');
    const [timePeriod, setTimePeriod] = useState<string>('7D');
    const [grouping, setGrouping] = useState<string>('1D'); // Default to 1 day grouping
    const [data, setData] = useState<SentimentData[]>([]);
    const [chartData, setChartData] = useState<any[]>([]);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const { auth, deductCredits, setCredits } = useAuth();
    const fetchWithAuth = useAuthFetch();

    useEffect(() => {
        transformDataForChart();
    }, [data, grouping]);

    const fetchSentiment = async (
        ticker: string,
        days: number
    ): Promise<SentimentData[]> => {
        const toDate = new Date();
        const fromDate = new Date();
        fromDate.setDate(fromDate.getDate() - days + 1);

        const fromDateUTC = fromDate.toISOString();
        const toDateUTC = toDate.toISOString();

        try {
            const response = await fetchWithAuth(`${API_URL}/sentiment`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    ticker,
                    fromDate: fromDateUTC,
                    toDate: toDateUTC,
                }),
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || 'Failed to fetch sentiment data');
            }

            const responseData: SentimentResponse = await response.json();

            // Update credits in AuthContext
            setCredits(responseData.creditsRemaining);

            const sentimentData: SentimentData[] = responseData.results.map((item) => ({
                date: item.date,
                sentiment: item.sentiment,
                confidence: item.confidence,
                ticker: item.ticker,
                id: item.id,
            }));

            return sentimentData;
        } catch (error) {
            console.error('Error fetching sentiment data:', error);
            throw error;
        }
    };

    const updateData = async () => {
        setIsLoading(true);
        try {
            const newData = await fetchSentiment(ticker, TIME_PERIODS[timePeriod].days);
            setData(newData);
        } catch (error) {
            console.error('Failed to update data:', error);
        } finally {
            setIsLoading(false);
        }
    };

    const transformDataForChart = () => {
        const days = TIME_PERIODS[timePeriod].days;
        const endDate = new Date();
        const startDate = new Date();
        startDate.setDate(endDate.getDate() - days + 1);

        // Generate all timestamps based on the grouping interval
        const dateRange = [];
        const interval = parseGroupingInterval(grouping);
        for (let d = new Date(startDate); d <= endDate; d.setTime(d.getTime() + interval)) {
            dateRange.push(new Date(d).toISOString());
        }

        // Group data by the specified interval
        const groupedData = data.reduce((acc, item) => {
            const date = getGroupedDate(item.date * 1000, interval);

            if (!acc[date]) {
                acc[date] = { date, positive: 0, neutral: 0, negative: 0 };
            }

            const sentimentKey = item.sentiment as 'positive' | 'neutral' | 'negative';
            acc[date][sentimentKey] += 1;
            return acc;
        }, {} as Record<string, { date: string; positive: number; neutral: number; negative: number }>);

        // Fill in missing dates with zero values for all sentiment categories
        const completeChartData = dateRange.map(date => {
            const formattedDate = getGroupedDate(new Date(date).getTime(), interval);
            return groupedData[formattedDate] || { date: formattedDate, positive: 0, neutral: 0, negative: 0 };
        });

        setChartData(completeChartData);
    };

// Helper function to parse the grouping interval
    const parseGroupingInterval = (grouping: string): number => {
        const unit = grouping.slice(-1); // 'H' for hours, 'D' for days
        const value = parseInt(grouping.slice(0, -1), 10);

        if (unit === 'H') return value * 60 * 60 * 1000; // Convert hours to milliseconds
        if (unit === 'D') return value * 24 * 60 * 60 * 1000; // Convert days to milliseconds

        return 24 * 60 * 60 * 1000; // Default to 1 day in milliseconds
    };

// Helper function to get a formatted date for grouping
    const getGroupedDate = (timestamp: number, interval: number): string => {
        const date = new Date(Math.floor(timestamp / interval) * interval);
        return date.toISOString();
    };



    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        updateData();
    };

    const CustomTooltip: React.FC<{ active?: boolean; payload?: any; label?: string }> = ({ active, payload, label }) => {
        if (active && payload && payload.length) {
            return (
                <div className="bg-white p-4 border rounded shadow">
                    <p className="text-sm font-bold mb-2">{label}</p>
                    <p className="text-sm text-green-600 flex justify-between">
                        <span>Positive:</span> <span className="ml-4 font-semibold">{payload[0].value}</span>
                    </p>
                    <p className="text-sm text-gray-600 flex justify-between">
                        <span>Neutral:</span> <span className="ml-4 font-semibold">{payload[1].value}</span>
                    </p>
                    <p className="text-sm text-red-600 flex justify-between">
                        <span>Negative:</span> <span className="ml-4 font-semibold">{payload[2].value}</span>
                    </p>
                </div>
            );
        }
        return null;
    };

    return (
        <div className="p-6 max-w-8xl mx-auto space-y-8">
            <div className="bg-white rounded-lg shadow-md p-6">
                <h1 className="text-2xl font-bold mb-4">Stock Sentiment Analysis</h1>

                <form onSubmit={handleSubmit} className="flex flex-wrap gap-4 mb-6">
                    <div className="flex-1 min-w-[200px]">
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Stock Ticker
                        </label>
                        <div className="relative">
                            <input
                                type="text"
                                value={ticker}
                                onChange={(e) => setTicker(e.target.value.toUpperCase())}
                                className="w-full px-4 py-2 border rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                placeholder="Enter ticker symbol"
                            />
                            <Search className="absolute right-3 top-2.5 text-gray-400" size={20}/>
                        </div>
                    </div>

                    <div className="w-48">
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Time Period
                        </label>
                        <div className="relative">
                            <select
                                value={timePeriod}
                                onChange={(e) => setTimePeriod(e.target.value)}
                                className="w-full px-4 py-2 border rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500 appearance-none"
                            >
                                {Object.entries(TIME_PERIODS).map(([key, {label}]) => (
                                    <option key={key} value={key}>{label}</option>
                                ))}
                            </select>
                            <Calendar className="absolute right-3 top-2.5 text-gray-400" size={20}/>
                        </div>
                    </div>

                    <div className="w-48">
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Group By
                        </label>
                        <select
                            value={grouping}
                            onChange={(e) => setGrouping(e.target.value)}
                            className="w-full px-4 py-2 border rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500 appearance-none"
                        >
                            {GROUPING_OPTIONS.map(option => (
                                <option key={option.value} value={option.value}>
                                    {option.label}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="flex items-end">
                        <button
                            type="submit"
                            disabled={isLoading}
                            className={`px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed ${isLoading ? 'animate-pulse' : ''}`}
                        >
                            {isLoading ? 'Loading...' : 'Analyze'}
                        </button>
                    </div>
                </form>

                <div className="h-[700px] w-full">
                    {chartData.length > 0 ? (
                        <ResponsiveContainer width="100%" height="100%">
                            <ComposedChart data={chartData} margin={{top: 20, right: 30, left: 50, bottom: 20}}>
                                <CartesianGrid strokeDasharray="3 3"/>
                                <XAxis dataKey="date" tick={{fontSize: 12}}/>
                                <YAxis allowDecimals={false}/>
                                <Tooltip content={<CustomTooltip/>}/>
                                <Legend/>
                                <Bar dataKey="positive" fill="rgba(74, 222, 128, 0.6)" name="Positive Sentiment"/>
                                <Bar dataKey="neutral" fill="rgba(156, 163, 175, 0.6)" name="Neutral Sentiment"/>
                                <Bar dataKey="negative" fill="rgba(248, 113, 113, 0.6)" name="Negative Sentiment"/>
                            </ComposedChart>
                        </ResponsiveContainer>
                    ) : (
                        !isLoading && (
                            <div className="text-center text-gray-500">
                                No data available. Please adjust your query.
                            </div>
                        )
                    )}
                </div>
            </div>
        </div>
    );
};

export default StockSentimentDashboard;
