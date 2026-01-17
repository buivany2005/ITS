// User Order Management page handler
(function () {
  // DOM Elements
  const orderTabs = document.querySelectorAll(".order-tab, [data-status]");
  const orderList = document.querySelector(".order-list, .orders-container");
  const searchInput = document.getElementById("order-search");
  const btnCancelOrder = document.querySelectorAll(".btn-cancel-order");

  // State
  let orders = [];
  let currentStatus = "all";

  // Initialize
  async function init() {
    await loadOrders();
    attachEventListeners();
  }

  // Load user orders from API
  async function loadOrders() {
    try {
      // TODO: Replace with actual API call
      // const response = await fetch('http://localhost:8081/api/user/orders');
      // orders = await response.json();

      // Mock data for now
      orders = [
        {
          id: 1,
          vehicleName: "Honda Winner X",
          vehicleType: "XEMAY",
          dateFrom: "2026-01-15",
          dateTo: "2026-01-18",
          status: "confirmed",
          total: 450000,
        },
        {
          id: 2,
          vehicleName: "Toyota Vios",
          vehicleType: "OTO",
          dateFrom: "2026-01-20",
          dateTo: "2026-01-22",
          status: "pending",
          total: 1200000,
        },
      ];

      renderOrders(orders);
    } catch (err) {
      console.error("Error loading orders:", err);
      showError("Không thể tải danh sách đơn hàng");
    }
  }

  // Attach event listeners
  function attachEventListeners() {
    orderTabs.forEach((tab) => {
      tab.addEventListener("click", () => {
        const status = tab.dataset.status || "all";
        currentStatus = status;
        filterOrders(status);

        // Update active tab style
        orderTabs.forEach((t) =>
          t.classList.remove("border-primary", "text-primary")
        );
        tab.classList.add("border-primary", "text-primary");
      });
    });

    if (searchInput) {
      searchInput.addEventListener("input", (e) => {
        const query = e.target.value.toLowerCase();
        const filtered = orders.filter((order) =>
          order.vehicleName.toLowerCase().includes(query)
        );
        renderOrders(filtered);
      });
    }

    btnCancelOrder.forEach((btn) => {
      btn.addEventListener("click", handleCancelOrder);
    });
  }

  // Filter orders by status
  function filterOrders(status) {
    if (status === "all") {
      renderOrders(orders);
    } else {
      const filtered = orders.filter((order) => order.status === status);
      renderOrders(filtered);
    }
  }

  // Render orders
  function renderOrders(ordersToRender) {
    if (!orderList) return;

    if (ordersToRender.length === 0) {
      orderList.innerHTML = `
        <div class="text-center py-12">
          <span class="material-symbols-outlined text-6xl text-gray-300">inbox</span>
          <p class="text-gray-500 mt-4">Không có đơn hàng nào</p>
        </div>
      `;
      return;
    }

    orderList.innerHTML = ordersToRender
      .map((order) => createOrderCard(order))
      .join("");

    // Reattach cancel button handlers
    document.querySelectorAll(".btn-cancel-order").forEach((btn) => {
      btn.addEventListener("click", handleCancelOrder);
    });
  }

  // Create order card HTML
  function createOrderCard(order) {
    const statusLabels = {
      pending: { text: "Chờ xác nhận", color: "bg-yellow-100 text-yellow-800" },
      confirmed: { text: "Đã xác nhận", color: "bg-green-100 text-green-800" },
      completed: { text: "Hoàn thành", color: "bg-blue-100 text-blue-800" },
      cancelled: { text: "Đã hủy", color: "bg-red-100 text-red-800" },
    };

    const status = statusLabels[order.status] || statusLabels.pending;

    return `
      <div class="bg-white dark:bg-slate-900 rounded-xl border border-gray-200 dark:border-slate-800 p-6 mb-4">
        <div class="flex justify-between items-start">
          <div>
            <h3 class="font-bold text-lg">${order.vehicleName}</h3>
            <p class="text-sm text-gray-500">${order.dateFrom} - ${order.dateTo}</p>
          </div>
          <span class="px-3 py-1 rounded-full text-xs font-semibold ${status.color}">
            ${status.text}
          </span>
        </div>
        <div class="mt-4 flex justify-between items-center">
          <p class="text-lg font-bold text-primary">${formatPrice(order.total)}</p>
          <div class="flex gap-2">
            ${
              order.status === "pending"
                ? `
              <button class="btn-cancel-order text-red-500 hover:text-red-700 text-sm font-semibold" data-id="${order.id}">
                Hủy đơn
              </button>
            `
                : ""
            }
            <button class="btn-view-order text-primary hover:text-primary/80 text-sm font-semibold" data-id="${order.id}">
              Chi tiết
            </button>
          </div>
        </div>
      </div>
    `;
  }

  // Handle cancel order
  async function handleCancelOrder(e) {
    const orderId = e.currentTarget.dataset.id;
    if (!confirm("Bạn có chắc muốn hủy đơn hàng này?")) return;

    try {
      // TODO: Call API to cancel order
      // await fetch(`http://localhost:8081/api/user/orders/${orderId}/cancel`, { method: 'POST' });

      // Update local state
      const order = orders.find((o) => String(o.id) === String(orderId));
      if (order) {
        order.status = "cancelled";
      }

      filterOrders(currentStatus);
      alert("Đã hủy đơn hàng thành công");
    } catch (err) {
      alert("Không thể hủy đơn hàng: " + (err.message || err));
    }
  }

  // Format price
  function formatPrice(price) {
    return (
      new Intl.NumberFormat("vi-VN", {
        style: "decimal",
      }).format(price) + "đ"
    );
  }

  // Show error
  function showError(message) {
    if (orderList) {
      orderList.innerHTML = `
        <div class="text-center py-12">
          <p class="text-red-500">${message}</p>
        </div>
      `;
    }
  }

  // Start on page load
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
