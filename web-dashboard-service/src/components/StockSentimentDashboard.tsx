import React, { useState, useEffect } from 'react';
import {API_URL} from '../config/API';
import {
    ComposedChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer, Line,
} from 'recharts';
import {Search, Calendar, RefreshCw, TrendingUp} from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { useAuthFetch } from '../hooks/useAuthFetch';
import { Messages } from '../messages/eng';


interface SentimentData {
    date: number;
    sentiment: string;
    confidence: number;
    ticker: string;
    id: number;
}

interface SentimentResponse {
    results: SentimentData[];
    creditsRemaining: number;
}

// Update the TIME_PERIODS constant
const TIME_PERIODS: { [key: string]: { days: number; label: string } } = {
    '7D': { days: 7, label: Messages.SEVEN_DAYS },
    '14D': { days: 14, label: Messages.FOURTEEN_DAYS },
    '1M': { days: 30, label: Messages.ONE_MONTH },
    '3M': { days: 90, label: Messages.THREE_MONTHS },
};

// Update the GROUPING_OPTIONS constant
const GROUPING_OPTIONS = [
    { label: Messages.ONE_HOUR, value: '1H' },
    { label: Messages.TWO_HOURS, value: '2H' },
    { label: Messages.THREE_HOURS, value: '3H' },
    { label: Messages.ONE_DAY, value: '1D' },
    { label: Messages.TWO_DAYS, value: '2D' },
    { label: Messages.THREE_DAYS, value: '3D' },
    { label: Messages.ONE_WEEK, value: '7D' },
    { label: Messages.TWO_WEEKS, value: '14D' },
    { label: Messages.ONE_MONTH_OPTION, value: '30D' },
];

const StockSentimentDashboard: React.FC = () => {
    const [ticker, setTicker] = useState<string>('AAPL');
    const [timePeriod, setTimePeriod] = useState<string>('7D');
    const [grouping, setGrouping] = useState<string>('1D');
    const [data, setData] = useState<SentimentData[]>([]);
    const [chartData, setChartData] = useState<any[]>([]);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [lastUpdated, setLastUpdated] = useState<Date | null>(null);
    const { auth, updateUserData } = useAuth();
    const { fetchWithAuth } = useAuthFetch();

    const [showTrendlines, setShowTrendlines] = useState({
        positive: false,
        neutral: false,
        negative: false
    });

    useEffect(() => {
        transformDataForChart();
    }, [data, grouping]);

    const formatDate = (dateString: string): string => {
        const date = new Date(dateString);
        return date.toLocaleString('en-US', {
            month: 'short',
            day: 'numeric',
            hour: 'numeric',
            minute: '2-digit',
            hour12: true
        });
    };

    const fetchSentiment = async (ticker: string, days: number): Promise<SentimentData[]> => {
        const toDate = new Date();
        const fromDate = new Date();
        fromDate.setDate(fromDate.getDate() - days + 1);

        try {
            const response = await fetchWithAuth(`${API_URL}/sentiment/analyze`, {
                method: 'POST',
                body: JSON.stringify({
                    ticker,
                    fromDate: fromDate.toISOString(),
                    toDate: toDate.toISOString(),
                }),
            });

            const responseData: SentimentResponse = await response.json();

            if (auth.user && responseData.creditsRemaining !== auth.user.credits) {
                updateUserData({ credits: responseData.creditsRemaining });
            }

            return responseData.results;
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
            setLastUpdated(new Date());
        } catch (error) {
            console.error('Failed to update data:', error);
        } finally {
            setIsLoading(false);
        }
    };

    const parseGroupingInterval = (grouping: string): number => {
        const unit = grouping.slice(-1);
        const value = parseInt(grouping.slice(0, -1), 10);
        if (unit === 'H') return value * 60 * 60 * 1000;
        if (unit === 'D') return value * 24 * 60 * 60 * 1000;
        return 24 * 60 * 60 * 1000;
    };

    const getGroupedDate = (timestamp: number, interval: number): string => {
        const date = new Date(Math.floor(timestamp / interval) * interval);
        return date.toISOString();
    };

    const calculateMovingAverage = (data: any[], key: string, period: number = 3) => {
        return data.map((item, index) => {
            if (index < period - 1) return { ...item, [`${key}Trend`]: item[key] };

            const sum = data
                .slice(index - period + 1, index + 1)
                .reduce((acc, curr) => acc + curr[key], 0);

            return {
                ...item,
                [`${key}Trend`]: Number((sum / period).toFixed(2))
            };
        });
    };

    const transformDataForChart = () => {
        const days = TIME_PERIODS[timePeriod].days;
        const endDate = new Date();
        const startDate = new Date();
        startDate.setDate(endDate.getDate() - days + 1);

        const dateRange = [];
        const interval = parseGroupingInterval(grouping);
        for (let d = new Date(startDate); d <= endDate; d.setTime(d.getTime() + interval)) {
            dateRange.push(new Date(d).toISOString());
        }

        const groupedData = data.reduce((acc, item) => {
            const date = getGroupedDate(item.date * 1000, interval);
            if (!acc[date]) {
                acc[date] = { date: formatDate(date), positive: 0, neutral: 0, negative: 0 };
            }
            const sentimentKey = item.sentiment as 'positive' | 'neutral' | 'negative';
            acc[date][sentimentKey] += 1;
            return acc;
        }, {} as Record<string, { date: string; positive: number; neutral: number; negative: number }>);

        let completeChartData = dateRange.map(date => {
            const formattedDate = getGroupedDate(new Date(date).getTime(), interval);
            return groupedData[formattedDate] || {
                date: formatDate(formattedDate),
                positive: 0,
                neutral: 0,
                negative: 0
            };
        });

        // Calculate moving averages for each sentiment
        completeChartData = calculateMovingAverage(completeChartData, 'positive');
        completeChartData = calculateMovingAverage(completeChartData, 'neutral');
        completeChartData = calculateMovingAverage(completeChartData, 'negative');

        setChartData(completeChartData);
    };

    const TrendlineToggle: React.FC<{
        sentiment: 'positive' | 'neutral' | 'negative',
        color: string
    }> = ({ sentiment, color }) => {
        const getButtonClasses = () => {
            const baseClasses = "inline-flex items-center gap-2 px-3 py-1.5 rounded-md text-sm transition-colors";

            if (showTrendlines[sentiment]) {
                switch (sentiment) {
                    case 'positive':
                        return `${baseClasses} bg-green-600 text-white`;
                    case 'neutral':
                        return `${baseClasses} bg-gray-600 text-white`;
                    case 'negative':
                        return `${baseClasses} bg-red-600 text-white`;
                }
            }

            return `${baseClasses} bg-gray-100 text-gray-700 hover:bg-gray-200`;
        };

        return (
            <button
                onClick={() => setShowTrendlines(prev => ({
                    ...prev,
                    [sentiment]: !prev[sentiment]
                }))}
                className={getButtonClasses()}
            >
                <TrendingUp size={14} />
                {sentiment.charAt(0).toUpperCase() + sentiment.slice(1)} Trend
            </button>
        );
    };

    const CustomTooltip: React.FC<{ active?: boolean; payload?: any; label?: string }> = ({ active, payload, label }) => {
        if (active && payload && payload.length) {
            const total = payload.reduce((sum: number, item: any) => sum + item.value, 0);
            return (
                <div className="bg-white p-4 border rounded-lg shadow-lg">
                    <p className="text-sm font-bold mb-2">{label}</p>
                    {payload.map((entry: any, index: number) => (
                        <p
                            key={index}
                            className={`text-sm flex justify-between items-center mb-1 ${
                                entry.name.startsWith('Positive') ? 'text-green-600' :
                                    entry.name.startsWith('Negative') ? 'text-red-600' : 'text-gray-600'
                            }`}
                        >
                            <span>{entry.name}:</span>
                            <span className="ml-4 font-semibold">
                                {entry.value} ({((entry.value / total) * 100).toFixed(1)}%)
                            </span>
                        </p>
                    ))}
                    <div className="mt-2 pt-2 border-t">
                        <p className="text-sm font-semibold flex justify-between">
                            <span>Total:</span>
                            <span>{total}</span>
                        </p>
                    </div>
                </div>
            );
        }
        return null;
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        updateData();
    };

    return (
        <div className="p-6 max-w-8xl mx-auto space-y-6">
            <div className="bg-white rounded-lg shadow-md overflow-hidden">
                <div className="px-6 py-4 flex flex-row items-center justify-between border-b">
                    <h2 className="text-2xl font-bold">{Messages.SENTIMENT_DASHBOARD_TITLE}</h2>

                    {lastUpdated && (
                        <div className="flex items-center gap-2">
                            <span
                                className="inline-flex items-center rounded-full px-2 py-1 text-xs font-medium bg-gray-100 text-gray-800">
                                {Messages.LAST_UPDATED_LABEL} {lastUpdated.toLocaleString()}
                            </span>
                            <button
                                onClick={updateData}
                                className="p-1 hover:bg-gray-100 rounded-full transition-colors"
                                title={Messages.REFRESH_DATA_TOOLTIP}
                            >
                                <RefreshCw size={16} className={`text-gray-500 ${isLoading ? 'animate-spin' : ''}`}/>
                            </button>
                        </div>
                    )}
                </div>

                <div className="p-6">
                    <form onSubmit={handleSubmit} className="flex flex-wrap gap-4 mb-6">
                        <div className="flex-1 min-w-[200px]">
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                {Messages.STOCK_TICKER_LABEL}
                            </label>
                            <div className="relative">
                                <input
                                    type="text"
                                    value={ticker}
                                    onChange={(e) => setTicker(e.target.value.toUpperCase())}
                                    className="w-full px-4 py-2 border rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                    placeholder={Messages.STOCK_TICKER_PLACEHOLDER}
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
                                className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                            >
                                {isLoading ? 'Analyzing...' : 'Analyze'}
                            </button>
                        </div>
                    </form>
                    <div className="mb-4 flex flex-wrap gap-2">
                        <TrendlineToggle sentiment="positive" color="green-600"/>
                        <TrendlineToggle sentiment="neutral" color="gray-600"/>
                        <TrendlineToggle sentiment="negative" color="red-600"/>
                    </div>

                    <div className="h-[700px] w-full">
                        {chartData.length > 0 ? (
                            <ResponsiveContainer width="100%" height="100%">
                                <ComposedChart data={chartData} margin={{top: 20, right: 30, left: 50, bottom: 20}}>
                                    <CartesianGrid strokeDasharray="3 3"/>
                                    <XAxis
                                        dataKey="date"
                                        tick={{fontSize: 12}}
                                        angle={-45}
                                        textAnchor="end"
                                        height={60}
                                    />
                                    <YAxis allowDecimals={false}/>
                                    <Tooltip content={<CustomTooltip/>}/>
                                    <Legend/>
                                    <Bar dataKey="positive" fill="rgba(74, 222, 128, 0.6)" name={Messages.POSITIVE_SENTIMENT}/>
                                    <Bar dataKey="neutral" fill="rgba(156, 163, 175, 0.6)" name={Messages.NEUTRAL_SENTIMENT}/>
                                    <Bar dataKey="negative" fill="rgba(248, 113, 113, 0.6)" name={Messages.NEGATIVE_SENTIMENT}/>

                                    {showTrendlines.positive && (
                                        <Line
                                            type="monotone"
                                            dataKey="positiveTrend"
                                            stroke="#22c55e"
                                            strokeWidth={2}
                                            dot={false}
                                            name="Positive Trend"
                                        />
                                    )}
                                    {showTrendlines.neutral && (
                                        <Line
                                            type="monotone"
                                            dataKey="neutralTrend"
                                            stroke="#4b5563"
                                            strokeWidth={2}
                                            dot={false}
                                            name="Neutral Trend"
                                        />
                                    )}
                                    {showTrendlines.negative && (
                                        <Line
                                            type="monotone"
                                            dataKey="negativeTrend"
                                            stroke="#dc2626"
                                            strokeWidth={2}
                                            dot={false}
                                            name="Negative Trend"
                                        />
                                    )}
                                </ComposedChart>
                            </ResponsiveContainer>
                        ) : (
                            <div className="h-full flex items-center justify-center">
                                <div className="text-center text-gray-500">
                                    <p className="text-lg mb-2">{Messages.NO_DATA_TITLE}</p>
                                    <p className="text-sm">{Messages.NO_DATA_DESCRIPTION}</p>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </div>

        </div>

    )
        ;
};

export default StockSentimentDashboard;