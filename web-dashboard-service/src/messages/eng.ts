// src/messages/eng.ts
export const Messages = {
    // Page titles
    ADMIN_DASHBOARD_TITLE: 'Admin Dashboard',
    USER_MANAGEMENT_TITLE: 'User Management',
    ENDPOINT_MANAGEMENT_TITLE: 'Endpoint Management',
    LOGIN_PAGE_TITLE: 'Sign in to your account',
    NOT_FOUND_TITLE: '404 - Page Not Found',

    // Table headers - User Management
    USER_TABLE_ID: 'ID',
    USER_TABLE_USERNAME: 'Username',
    USER_TABLE_EMAIL: 'Email',
    USER_TABLE_ROLE: 'Role',
    USER_TABLE_CREDITS: 'Credits',
    USER_TABLE_REQUESTS: 'Requests',
    USER_TABLE_ACTIONS: 'Actions',

    // Table headers - Endpoint Management
    ENDPOINT_TABLE_METHOD: 'METHOD',
    ENDPOINT_TABLE_URI: 'URI',
    ENDPOINT_TABLE_REQUESTS: 'Requests',

    // Form labels and placeholders
    USERNAME_PLACEHOLDER: 'Username',
    PASSWORD_PLACEHOLDER: 'Password',

    // Buttons
    DELETE_BUTTON: 'Delete',
    SIGN_IN_BUTTON: 'Sign in',
    TRY_AGAIN_BUTTON: 'Try again',
    LOGOUT_BUTTON: 'Logout',

    // Links
    SIGN_UP_LINK: "Don't have an account? Sign up",
    RETURN_TO_DASHBOARD: 'Return to Dashboard',

    // Loading and error states
    LOADING_MESSAGE: 'Loading...',
    ERROR_PREFIX: 'Error: ',
    UNEXPECTED_ERROR: 'An unexpected error occurred',
    GENERIC_ERROR_MESSAGE: 'Something went wrong',

    // Error pages
    NOT_FOUND_MESSAGE: "The page you're looking for doesn't exist or has been moved.",

    // Confirmation messages
    DELETE_USER_CONFIRMATION: 'Are you sure you want to delete this user?',

    // Error messages
    FETCH_USERS_ERROR: 'Failed to fetch users',
    FETCH_ENDPOINTS_ERROR: 'Failed to fetch endpoints',
    DELETE_USER_ERROR: 'Failed to delete user',

    // Header content
    CREDITS_LABEL: 'Credits: ',
    LOGO_TEXT: 'S',
    APP_TITLE: 'Stock Sentiment Analysis',

    // Registration page
    REGISTER_PAGE_TITLE: 'Sign up for an account',
    REGISTER_EMAIL_PLACEHOLDER: 'Email address',
    REGISTER_PASSWORD_PLACEHOLDER: 'Password',
    REGISTER_BUTTON: 'Sign up',
    REGISTER_FAILED: 'Registration failed',
    REGISTER_LOGIN_LINK: 'Already have an account? Sign in',

    // Form validation and errors
    REGISTRATION_GENERIC_ERROR: 'Registration failed',

    // Stock Sentiment Dashboard
    SENTIMENT_DASHBOARD_TITLE: 'Stock Sentiment Analysis',
    LAST_UPDATED_LABEL: 'Last updated:',
    REFRESH_DATA_TOOLTIP: 'Refresh data',

    // Form Labels
    STOCK_TICKER_LABEL: 'Stock Ticker',
    STOCK_TICKER_PLACEHOLDER: 'Enter ticker symbol',
    TIME_PERIOD_LABEL: 'Time Period',
    GROUP_BY_LABEL: 'Group By',

    // Time Periods
    SEVEN_DAYS: '7 Days',
    FOURTEEN_DAYS: '14 Days',
    ONE_MONTH: '1 Month',
    THREE_MONTHS: '3 Months',

    // Grouping Options
    ONE_HOUR: '1 Hour',
    TWO_HOURS: '2 Hours',
    THREE_HOURS: '3 Hours',
    ONE_DAY: '1 Day',
    TWO_DAYS: '2 Days',
    THREE_DAYS: '3 Days',
    ONE_WEEK: '1 Week',
    TWO_WEEKS: '2 Weeks',
    ONE_MONTH_OPTION: '1 Month',

    // Button Labels
    ANALYZE_BUTTON: 'Analyze',
    ANALYZING_BUTTON: 'Analyzing...',

    // Sentiment Labels
    POSITIVE_TREND: 'Positive Trend',
    NEUTRAL_TREND: 'Neutral Trend',
    NEGATIVE_TREND: 'Negative Trend',
    POSITIVE_SENTIMENT: 'Positive Sentiment',
    NEUTRAL_SENTIMENT: 'Neutral Sentiment',
    NEGATIVE_SENTIMENT: 'Negative Sentiment',

    // Chart Labels
    TOTAL_LABEL: 'Total:',

    // No Data Messages
    NO_DATA_TITLE: 'No data available',
    NO_DATA_DESCRIPTION: 'Try adjusting your query parameters or selecting a different time period',

    // Error Messages
    FETCH_SENTIMENT_ERROR: 'Error fetching sentiment data:',
    UPDATE_DATA_ERROR: 'Failed to update data:',

    // User Profile Page
    USER_PROFILE_TITLE: 'User Profile',
    USER_PROFILE_USERNAME_LABEL: 'Username',
    USER_PROFILE_EMAIL_LABEL: 'Email',
    USER_PROFILE_ROLE_LABEL: 'Role',
    USER_PROFILE_CREDITS_LABEL: 'Credits',
    USER_PROFILE_TOTAL_REQUESTS_LABEL: 'Total Requests',
    USER_PROFILE_LOADING: 'Loading...',
    USER_PROFILE_NO_DATA: 'No profile data available',
    USER_PROFILE_FETCH_ERROR: 'Failed to fetch user profile',

    // Auth Context Messages
    AUTH_CONTEXT_ERROR: 'useAuth must be used within AuthContext',
    SESSION_VALIDATION_ERROR: 'Session validation failed',
    LOGIN_ERROR: 'Login failed',
    REGISTRATION_ERROR: 'Registration failed',

    // Auth Debug Messages
    SESSION_VALIDATION_DEBUG: 'Session validation failed:',
    SESSION_VALIDATION_ERROR_DEBUG: 'Error during session validation:',
    LOGIN_DEBUG: 'handleLogin',

    // Auth Fetch Messages
    AUTH_FETCH_REQUEST_FAILED: 'Request failed',
    AUTH_FETCH_TOKEN_REFRESH_FAILED: 'Request failed after token refresh',
    AUTH_FETCH_SESSION_EXPIRED: 'Session expired',
    AUTH_FETCH_ERROR_PREFIX: 'Fetch error:',

    // API Error Messages
    API_DEFAULT_ERROR: 'An unexpected error occurred',

    // Loading Spinner
    PROCESSING: 'Processing...',
};