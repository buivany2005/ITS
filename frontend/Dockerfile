# Nginx Dockerfile for static frontend

FROM nginx:alpine

# Remove default nginx config
RUN rm /etc/nginx/conf.d/default.conf

# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/

# Copy frontend files
COPY . /usr/share/nginx/html/

# Create cache directory
RUN mkdir -p /var/cache/nginx/client_temp

# Expose ports
EXPOSE 80 443

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost/home/index.html || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
