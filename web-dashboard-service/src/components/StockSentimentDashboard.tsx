import React, { useState, useEffect } from 'react';
import {
    ComposedChart,
    Bar,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer,
} from 'recharts';
import { Search, Calendar } from 'lucide-react';

// Define the type for the data structure
interface SentimentData {
    date: string;
    fullDate: string;
    positive: number;
    negative: number;
    neutral: number;
    neutralLine: number;
}

// More realistic data generator with proper date handling
const generateSampleData = (ticker: string, days: number): SentimentData[] => {
    const data: SentimentData[] = [];
    const endDate = new Date();
    const startDate = new Date();
    startDate.setDate(startDate.getDate() - days + 1);

    for (let date = new Date(startDate); date <= endDate; date.setDate(date.getDate() + 1)) {
        // Create more correlated data
        const baseValue = Math.sin(date.getDate() / 7) * 20 + 60; // Creates a wave pattern
        const positive = Math.floor(baseValue + Math.random() * 15);
        const negative = Math.floor((80 - baseValue) + Math.random() * 15);
        const neutral = Math.floor(Math.random() * 20 + 40);
        const netSentiment = positive + negative; // Net sentiment calculation

        data.push({
            date: date.toLocaleDateString('en-US', {
                month: 'short',
                day: 'numeric',
            }),
            fullDate: date.toISOString(),
            positive,
            negative: -negative,
            neutral,
            neutralLine: netSentiment, // Update to represent net sentiment
        });
    }
    return data;
};

const TIME_PERIODS: { [key: string]: { days: number; label: string } } = {
    '7D': { days: 7, label: '7 Days' },
    '14D': { days: 14, label: '14 Days' },
    '1M': { days: 30, label: '1 Month' },
    '3M': { days: 90, label: '3 Months' },
};

const StockSentimentDashboard: React.FC = () => {
    const [ticker, setTicker] = useState<string>('AAPL');
    const [timePeriod, setTimePeriod] = useState<string>('7D');
    const [data, setData] = useState<SentimentData[]>([]);
    const [isLoading, setIsLoading] = useState<boolean>(false);

    // Effect to update data when time period changes
    useEffect(() => {
        updateData();
    }, [timePeriod]);

    const updateData = () => {
        setIsLoading(true);
        // Simulate API call delay
        setTimeout(() => {
            const newData = generateSampleData(ticker, TIME_PERIODS[timePeriod].days);
            setData(newData);
            setIsLoading(false);
        }, 500);
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        updateData();
    };

    interface CustomTooltipProps {
        active?: boolean;
        payload?: any;
        label?: string;
    }

    const CustomTooltip: React.FC<CustomTooltipProps> = ({ active, payload, label }) => {
        if (active && payload && payload.length) {
            return (
                <div className="bg-white p-4 border rounded shadow">
                    <p className="text-sm font-bold mb-2">{label}</p>
                    <p className="text-sm text-green-600 flex justify-between">
                        <span>Positive:</span> <span className="ml-4 font-semibold">{payload[0].value}%</span>
                    </p>
                    <p className="text-sm text-red-600 flex justify-between">
                        <span>Negative:</span> <span className="ml-4 font-semibold">{Math.abs(payload[1].value)}%</span>
                    </p>
                    <p className="text-sm text-gray-600 flex justify-between">
                        <span>Net Sentiment:</span> <span className="ml-4 font-semibold">{payload[2].value}%</span>
                    </p>
                </div>
            );
        }
        return null;
    };

    return (
        <div className="p-6 max-w-6xl mx-auto space-y-6">
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
                            <Search className="absolute right-3 top-2.5 text-gray-400" size={20} />
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
                                {Object.entries(TIME_PERIODS).map(([key, { label }]) => (
                                    <option key={key} value={key}>{label}</option>
                                ))}
                            </select>
                            <Calendar className="absolute right-3 top-2.5 text-gray-400" size={20} />
                        </div>
                    </div>

                    <div className="flex items-end">
                        <button
                            type="submit"
                            disabled={isLoading}
                            className={`px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed
                ${isLoading ? 'animate-pulse' : ''}`}
                        >
                            {isLoading ? 'Loading...' : 'Analyze'}
                        </button>
                    </div>
                </form>

                <div className="h-[500px] w-full">
                    <ResponsiveContainer width="100%" height="100%">
                        <ComposedChart
                            data={data}
                            margin={{ top: 20, right: 30, left: 50, bottom: 20 }}
                        >
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis
                                dataKey="date"
                                tick={{ fontSize: 12 }}
                                interval={'preserveStartEnd'}
                            />
                            <YAxis
                                domain={[-100, 100]}
                                ticks={[-75, -50, -25, 0, 25, 50, 75, 100]}
                                tickFormatter={(value: number) => `${Math.abs(value)}%`}
                            />
                            <Tooltip content={<CustomTooltip />} />
                            <Legend />
                            <Bar
                                dataKey="positive"
                                fill="rgba(74, 222, 128, 0.6)"
                                name="Positive Sentiment"
                            />
                            <Bar
                                dataKey="negative"
                                fill="rgba(248, 113, 113, 0.6)"
                                name="Negative Sentiment"
                            />
                            <Line
                                type="monotone"
                                dataKey="neutralLine"
                                stroke="#000"
                                strokeWidth={2}
                                dot={false}
                                name="Net Sentiment"
                            />
                        </ComposedChart>
                    </ResponsiveContainer>
                </div>

                <div className="mt-4">
                    <h2 className="text-lg font-semibold mb-2">Summary</h2>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        {['Positive', 'Negative', 'Neutral'].map((type) => (
                            <div key={type} className="bg-gray-50 p-4 rounded-lg">
                                <h3 className="text-sm font-medium text-gray-500">{type} Sentiment Average</h3>
                                <p className="text-2xl font-bold">
                                    {Math.round(
                                        data.reduce((acc, curr) => {
                                            const value = type === 'Negative'
                                                ? Math.abs(curr[type.toLowerCase() as keyof SentimentData] as number)
                                                : curr[type === 'Neutral' ? 'neutralLine' : type.toLowerCase() as keyof SentimentData] as number;
                                            return acc + value;
                                        }, 0) / Math.max(data.length, 1)
                                    )}%
                                </p>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default StockSentimentDashboard;
