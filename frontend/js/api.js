// Lightweight API helpers for Frontend
window.api = (function () {
  const jsonHeaders = { "Content-Type": "application/json" };

  // Auto-detect API base URL based on environment
  const getBaseUrl = () => {
    const hostname = window.location.hostname;
    const port = window.location.port;

    // If running via Nginx on port 80 (port will be empty string), use relative path
    if (!port || port === "80" || port === "8080") {
      return "";
    }

    // If frontend is on dev server (5500, 8000, etc.), use localhost:8081 for backend
    if (hostname === "localhost" || hostname === "127.0.0.1") {
      return "http://localhost:8081";
    }

    // Default: use relative path
    return "";
  };

  const baseUrl = getBaseUrl();
  console.log("API Base URL:", baseUrl || "(relative path)");

  async function request(path, opts) {
    const url = baseUrl + path;
    console.log("Fetching:", url);

    // Thêm token vào Authorization header nếu có
    const user = localStorage.getItem("user");
    if (user) {
      try {
        const userData = JSON.parse(user);
        opts = opts || {};
        opts.headers = opts.headers || {};

        // Attach JWT if present (future-proof)
        if (userData.token) {
          opts.headers["Authorization"] = "Bearer " + userData.token;
        }

        // Always attach user id for backend to identify the requester
        if (userData.userId) {
          opts.headers["X-User-Id"] = userData.userId;
        }
      } catch (e) {
        console.log("Could not parse user data for token");
      }
    }

    const res = await fetch(url, opts);
    if (!res.ok) {
      const text = await res.text();
      throw new Error(text || res.statusText);
    }
    const contentType = res.headers.get("content-type") || "";
    if (contentType.includes("application/json")) return res.json();
    return res.text();
  }

  return {
    // Authentication
    login: async (credentials) => {
      return request("/api/auth/login", {
        method: "POST",
        headers: jsonHeaders,
        body: JSON.stringify(credentials),
      });
    },
    register: async (payload) => {
      return request("/api/auth/register", {
        method: "POST",
        headers: jsonHeaders,
        body: JSON.stringify(payload),
      });
    },
    logout: async () => {
      return request("/api/auth/logout", { method: "POST" });
    },

    // Vehicles
    searchVehicles: async (params) => {
      const qs = new URLSearchParams(params || {}).toString();
      return request("/api/vehicles?" + qs, { method: "GET" });
    },
    getVehicle: async (id) => {
      return request(`/api/vehicles/${id}`, { method: "GET" });
    },
    getVehiclesByType: async (type) => {
      return request(`/api/vehicles?vehicleType=${type}`, { method: "GET" });
    },

    // Cart
    addToCart: async (item) => {
      return request("/api/cart", {
        method: "POST",
        headers: jsonHeaders,
        body: JSON.stringify(item),
      });
    },
    getCart: async () => {
      return request("/api/cart", { method: "GET" });
    },
    removeFromCart: async (itemId) => {
      return request(`/api/cart/${itemId}`, { method: "DELETE" });
    },

    // Orders
    createOrder: async (orderData) => {
      return request("/api/orders", {
        method: "POST",
        headers: jsonHeaders,
        body: JSON.stringify(orderData),
      });
    },
    getUserOrders: async () => {
      return request("/api/orders/my-orders", { method: "GET" });
    },
    cancelOrder: async (orderId) => {
      return request(`/api/orders/${orderId}/cancel`, { method: "POST" });
    },

    // Newsletter
    subscribeNewsletter: async (email) => {
      return request("/api/newsletter", {
        method: "POST",
        headers: jsonHeaders,
        body: JSON.stringify({ email }),
      });
    },

    // User Profile
    getProfile: async () => {
      return request("/api/user/profile", { method: "GET" });
    },
    updateProfile: async (data) => {
      return request("/api/user/profile", {
        method: "PUT",
        headers: jsonHeaders,
        body: JSON.stringify(data),
      });
    },
  };
})();
