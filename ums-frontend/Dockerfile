# ---------- Stage 1: Build the Angular app ----------
FROM node:20-alpine AS build

WORKDIR /app

# Install dependencies first (better layer caching)
COPY package.json package-lock.json ./
RUN npm ci

# Copy the rest of the source and build for production
COPY . .
RUN npm run build -- --configuration production

# ---------- Stage 2: Serve with Nginx ----------
FROM nginx:1.27-alpine AS runtime

# Remove default nginx static assets
RUN rm -rf /usr/share/nginx/html/*

# Angular's "application" builder outputs to dist/<project>/browser
COPY --from=build /app/dist/ums-frontend/browser /usr/share/nginx/html

# Custom Nginx template: SPA fallback + reverse proxy to backend API.
# The official nginx image auto-renders *.template files in
# /etc/nginx/templates into /etc/nginx/conf.d at container startup,
# substituting any ${VAR} using the container's environment.
COPY nginx.conf.template /etc/nginx/templates/default.conf.template

# Defaults — override with `docker run -e BACKEND_HOST=... -e BACKEND_PORT=...`
# or via docker-compose environment settings.
ENV BACKEND_HOST=backend
ENV BACKEND_PORT=8080

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
