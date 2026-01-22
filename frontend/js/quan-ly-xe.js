// User Order Management page - Load data from API
(function () {
  // State
  let allOrders = [];
  let currentFilter = "all"; // all, PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED
  let currentPage = 1;
  const ordersPerPage = 5;

  // DOM Elements
  const orderTableBody = document.querySelector("tbody");
  const filterButtons = document.querySelectorAll("[data-filter]");

  // Initialize
  async function init() {
    console.log("Initializing order management page...");

    // Check if user is logged in
    const user = localStorage.getItem("user");
    if (!user) {
      alert("Vui lòng đăng nhập để xem đơn thuê của bạn!");
      window.location.href = "../login/login.html";
      return;
    }

    // Update user avatar and name in header
    try {
      const userData = JSON.parse(user);
      if (userData.fullName) {
        const firstLetter = userData.fullName.charAt(0).toUpperCase();
        const avatarElements = document.querySelectorAll(
          '#user-avatar, [id="user-avatar"]',
        );
        avatarElements.forEach((el) => {
          if (el) el.textContent = firstLetter;
        });

        // Update user name in sidebar
        const userNameElements = document.querySelectorAll(
          '#user-name, [id="user-name"]',
        );
        userNameElements.forEach((el) => {
          if (el) el.textContent = userData.fullName;
        });
      }
    } catch (e) {
      console.error("Error updating avatar and name:", e);
    }

    // Attach filter button listeners
    if (filterButtons && filterButtons.length > 0) {
      filterButtons.forEach((btn) => {
        btn.addEventListener("click", handleFilterClick);
      });
    }

    // Load orders
    await loadOrders();
  }

  // Load orders from API
  async function loadOrders() {
    try {
      console.log("Loading user orders...");
      const data = await window.api.getUserOrders();
      allOrders = data || [];
      console.log("Orders loaded:", allOrders);
      renderOrders(allOrders);
      updateFilterCounts();
    } catch (error) {
      console.error("Error loading orders:", error);
      showError("Không thể tải danh sách đơn thuê. Vui lòng thử lại sau.");
    }
  }

  // Handle filter button click
  function handleFilterClick(e) {
    const filter = e.target.getAttribute("data-filter");
    currentFilter = filter;
    currentPage = 1; // Reset to first page when filter changes

    // Update active button style
    filterButtons.forEach((btn) => {
      btn.classList.remove("bg-primary", "text-white");
      btn.classList.add(
        "bg-gray-100",
        "text-gray-700",
        "dark:bg-gray-800",
        "dark:text-gray-300",
      );
    });
    e.target.classList.remove(
      "bg-gray-100",
      "text-gray-700",
      "dark:bg-gray-800",
      "dark:text-gray-300",
    );
    e.target.classList.add("bg-primary", "text-white");

    // Filter and render
    const filtered = filterOrders();
    renderOrders(filtered);
  }

  // Filter orders by status
  function filterOrders() {
    if (currentFilter === "all") {
      return allOrders;
    }
    return allOrders.filter((order) => order.status === currentFilter);
  }

  // Render orders to table with pagination
  function renderOrders(orders) {
    if (!orderTableBody) {
      console.warn("Order table body not found");
      return;
    }

    if (orders.length === 0) {
      orderTableBody.innerHTML = `
        <tr>
          <td colspan="5" class="px-6 py-12 text-center text-gray-500">
            <span class="material-symbols-outlined text-5xl mb-4 block">inbox</span>
            <p class="text-lg font-medium">Không có đơn thuê nào</p>
            <p class="text-sm mt-2">Hãy đặt xe để bắt đầu</p>
          </td>
        </tr>
      `;
      document.getElementById("pagination-container").style.display = "none";
      return;
    }

    // Show pagination
    document.getElementById("pagination-container").style.display = "flex";

    // Calculate pagination
    const totalPages = Math.ceil(orders.length / ordersPerPage);
    const startIndex = (currentPage - 1) * ordersPerPage;
    const endIndex = startIndex + ordersPerPage;
    const paginatedOrders = orders.slice(startIndex, endIndex);

    orderTableBody.innerHTML = paginatedOrders
      .map((order) => {
        const statusBadgeClass = getStatusBadgeClass(order.status);
        const statusText = getStatusText(order.status);
        const dateFrom = formatDate(order.dateFrom);
        const dateTo = formatDate(order.dateTo);

        return `
        <tr class="hover:bg-gray-50/50 dark:hover:bg-gray-800/20 transition-colors border-b border-gray-100 dark:border-gray-800">
          <td class="px-6 py-4">
            <div>
              <p class="font-bold text-[#111418] dark:text-white">#ORD-${order.id}</p>
              <p class="text-xs text-gray-500 dark:text-gray-400">${dateFrom} 08:00</p>
            </div>
          </td>
          <td class="px-6 py-4">
            <div>
              <p class="font-bold text-[#111418] dark:text-white">${order.vehicleName || "N/A"}</p>
              <p class="text-xs text-gray-500 dark:text-gray-400">${order.vehicleType || "N/A"}</p>
            </div>
          </td>
          <td class="px-6 py-4">
            <span class="px-3 py-1 rounded-full text-xs font-bold ${statusBadgeClass}">
              ${statusText}
            </span>
          </td>
          <td class="px-6 py-4">
            <p class="font-bold text-[#111418] dark:text-white">${formatPrice(order.totalPrice || 0)}</p>
            <p class="text-xs text-gray-500 dark:text-gray-400">${order.totalDays || 0} ngày</p>
          </td>
          <td class="px-6 py-4">
            <div class="flex gap-2">
              <button 
                class="text-primary hover:bg-primary/10 px-3 py-2 rounded transition-colors text-xs font-bold"
                onclick="viewOrderDetail(${order.id})"
              >
                Xem chi tiết
              </button>
              ${
                order.status === "PENDING"
                  ? `
                <button 
                  class="text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20 px-3 py-2 rounded transition-colors text-xs font-bold"
                  onclick="cancelOrder(${order.id})"
                >
                  Hủy
                </button>
              `
                  : ""
              }
            </div>
          </td>
        </tr>
      `;
      })
      .join("");

    // Update pagination
    renderPagination(orders.length);
  }

  // Render pagination controls
  function renderPagination(totalOrders) {
    const totalPages = Math.ceil(totalOrders / ordersPerPage);
    const startIndex = (currentPage - 1) * ordersPerPage + 1;
    const endIndex = Math.min(currentPage * ordersPerPage, totalOrders);

    // Update info text
    const paginationInfo = document.getElementById("pagination-info");
    if (paginationInfo) {
      paginationInfo.textContent = `Hiển thị ${startIndex} - ${endIndex} của ${totalOrders} đơn hàng`;
    }

    // Render pagination buttons
    const paginationButtons = document.getElementById("pagination-buttons");
    if (!paginationButtons) return;

    let buttonsHTML = "";

    // Previous button
    buttonsHTML += `
      <button
        onclick="changePage(${currentPage - 1})"
        class="w-8 h-8 flex items-center justify-center rounded-lg border border-[#dbe0e6] dark:border-slate-700 bg-white dark:bg-slate-900 text-gray-400 disabled:opacity-50"
        ${currentPage === 1 ? "disabled" : ""}
      >
        <span class="material-symbols-outlined text-sm">chevron_left</span>
      </button>
    `;

    // Page number buttons
    for (let i = 1; i <= totalPages; i++) {
      if (
        totalPages <= 5 ||
        i === 1 ||
        i === totalPages ||
        (i >= currentPage - 1 && i <= currentPage + 1)
      ) {
        buttonsHTML += `
          <button
            onclick="changePage(${i})"
            class="w-8 h-8 flex items-center justify-center rounded-lg ${i === currentPage ? "bg-primary text-white" : "border border-[#dbe0e6] dark:border-slate-700 bg-white dark:bg-slate-900 text-gray-700 dark:text-gray-300"} text-sm font-bold"
          >
            ${i}
          </button>
        `;
      } else if (i === currentPage - 2 || i === currentPage + 2) {
        buttonsHTML += '<span class="px-2 text-gray-400">...</span>';
      }
    }

    // Next button
    buttonsHTML += `
      <button
        onclick="changePage(${currentPage + 1})"
        class="w-8 h-8 flex items-center justify-center rounded-lg border border-[#dbe0e6] dark:border-slate-700 bg-white dark:bg-slate-900 text-gray-700 dark:text-gray-300"
        ${currentPage === totalPages ? "disabled" : ""}
      >
        <span class="material-symbols-outlined text-sm">chevron_right</span>
      </button>
    `;

    paginationButtons.innerHTML = buttonsHTML;
  }

  // Change page
  window.changePage = function (page) {
    const filtered = filterOrders();
    const totalPages = Math.ceil(filtered.length / ordersPerPage);

    if (page < 1 || page > totalPages) return;

    currentPage = page;
    renderOrders(filtered);
  };

  // Update filter button counts
  function updateFilterCounts() {
    const counts = {
      all: allOrders.length,
      PENDING: allOrders.filter((o) => o.status === "PENDING").length,
      CONFIRMED: allOrders.filter((o) => o.status === "CONFIRMED").length,
      IN_PROGRESS: allOrders.filter((o) => o.status === "IN_PROGRESS").length,
      COMPLETED: allOrders.filter((o) => o.status === "COMPLETED").length,
      CANCELLED: allOrders.filter((o) => o.status === "CANCELLED").length,
    };

    filterButtons.forEach((btn) => {
      const filter = btn.getAttribute("data-filter");
      const countSpan = btn.querySelector(".count");
      if (countSpan) {
        countSpan.textContent = `(${counts[filter]})`;
      }
    });
  }

  // Get status badge styling
  function getStatusBadgeClass(status) {
    const classes = {
      PENDING:
        "bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400",
      CONFIRMED:
        "bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400",
      IN_PROGRESS:
        "bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400",
      COMPLETED:
        "bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400",
      CANCELLED: "bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400",
    };
    return classes[status] || classes.PENDING;
  }

  // Get status text in Vietnamese
  function getStatusText(status) {
    const texts = {
      PENDING: "Đang xử lý",
      CONFIRMED: "Đã xác nhận",
      IN_PROGRESS: "Đang thuê",
      COMPLETED: "Đã trả xe",
      CANCELLED: "Đã hủy",
    };
    return texts[status] || "Không xác định";
  }

  // Format date
  function formatDate(dateStr) {
    if (!dateStr) return "N/A";
    const date = new Date(dateStr);
    return date.toLocaleDateString("vi-VN", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
    });
  }

  // Format price
  function formatPrice(price) {
    return new Intl.NumberFormat("vi-VN").format(price) + "đ";
  }

  // Show error message
  function showError(message) {
    if (orderTableBody) {
      orderTableBody.innerHTML = `
        <tr>
          <td colspan="5" class="px-6 py-12 text-center text-red-500">
            <span class="material-symbols-outlined text-5xl mb-4 block">error_outline</span>
            <p class="text-lg font-medium">${message}</p>
          </td>
        </tr>
      `;
    }
  }

  // View order detail
  window.viewOrderDetail = function (orderId) {
    const order = allOrders.find((o) => o.id === orderId);
    if (!order) {
      alert("Đơn hàng không tồn tại!");
      return;
    }

    // Populate modal with order data
    document.getElementById("detail-order-id").textContent = `#ORD-${order.id}`;
    document.getElementById("detail-vehicle-name").textContent =
      order.vehicleName || "N/A";
    document.getElementById("detail-vehicle-type").textContent =
      order.vehicleType || "N/A";
    document.getElementById("detail-date-from").textContent = formatDate(
      order.dateFrom,
    );
    document.getElementById("detail-date-to").textContent = formatDate(
      order.dateTo,
    );
    document.getElementById("detail-total-days").textContent =
      `${order.totalDays || 0} ngày`;
    document.getElementById("detail-total-price").textContent = formatPrice(
      order.totalPrice || 0,
    );
    document.getElementById("detail-base-price").textContent = formatPrice(
      order.totalPrice || 0,
    );
    document.getElementById("detail-final-price").textContent = formatPrice(
      order.totalPrice || 0,
    );
    document.getElementById("detail-created-date").textContent = formatDate(
      order.dateFrom,
    );

    // Update status badge
    const statusBadge = document.getElementById("detail-status-badge");
    statusBadge.className = `px-4 py-2 rounded-full text-sm font-bold ${getStatusBadgeClass(order.status)}`;
    statusBadge.textContent = getStatusText(order.status);

    // Show modal
    document.getElementById("order-detail-modal").classList.remove("hidden");
    document.body.style.overflow = "hidden";
  };

  // Close order detail modal
  window.closeOrderDetail = function () {
    document.getElementById("order-detail-modal").classList.add("hidden");
    document.body.style.overflow = "auto";
  };

  // Handle logout
  const logoutBtn = document.getElementById("btn-logout");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", function () {
      if (confirm("Bạn có chắc chắn muốn đăng xuất?")) {
        localStorage.removeItem("user");
        window.location.href = "../login/login.html";
      }
    });
  }

  // Cancel order
  window.cancelOrder = async function (orderId) {
    if (!confirm("Bạn có chắc chắn muốn hủy đơn này?")) {
      return;
    }

    try {
      await window.api.cancelOrder(orderId);
      alert("Hủy đơn thành công!");
      await loadOrders();
    } catch (error) {
      console.error("Error cancelling order:", error);
      alert("Không thể hủy đơn. Vui lòng thử lại sau.");
    }
  };

  // Start on page load
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
