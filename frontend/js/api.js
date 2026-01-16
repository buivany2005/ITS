// Lightweight API helpers for Frontend
window.api = (function () {
  const jsonHeaders = { "Content-Type": "application/json" };

  // Auto-detect API base URL based on environment
  const getBaseUrl = () => {
    // If running on localhost with a port, assume it's backend on 8081
    if (
      window.location.hostname === "localhost" ||
      window.location.hostname === "127.0.0.1"
    ) {
      // If frontend is on 8000, 5500, or similar dev server, use localhost:8081
      if (window.location.port && window.location.port !== "8080") {
        return "http://localhost:8081";
      }
    }
    // If running via Nginx on port 80, use relative path
    return "";
  };

  const baseUrl = getBaseUrl();

  async function request(path, opts) {
    const url = baseUrl + path;
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
    searchVehicles: async (params) => {
      const qs = new URLSearchParams(params || {}).toString();
      return request("/api/vehicles?" + qs, { method: "GET" });
    },
    addToCart: async (item) => {
      return request("/api/cart", {
        method: "POST",
        headers: jsonHeaders,
        body: JSON.stringify(item),
      });
    },
    subscribeNewsletter: async (email) => {
      return request("/api/newsletter", {
        method: "POST",
        headers: jsonHeaders,
        body: JSON.stringify({ email }),
      });
    },
  };
})();
