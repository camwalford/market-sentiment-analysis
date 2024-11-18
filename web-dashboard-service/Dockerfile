# Stage 1: Build the React application
FROM node:18 AS build

# Set working directory
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

ARG REACT_APP_API_URL
# Build the application
RUN REACT_APP_API_URL=${REACT_APP_API_URL:-http://localhost:8080} npm run build


FROM nginx:alpine

# Copy the built React app from the build stage to Nginx's default HTML folder
COPY --from=build /app/build /usr/share/nginx/html

# Expose port 80, nginx's default port
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
