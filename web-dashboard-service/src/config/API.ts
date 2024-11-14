
console.log("API URL:", process.env.REACT_APP_API_URL); // Should output the API URL or "undefined" if not set

const API_URL = process.env.REACT_APP_API_URL || "http://localhost:8080/api";

export default API_URL;