(function () {
  const exportBtn = document.getElementById("btn-export");
  const searchInput = document.getElementById("order-search");
  const dateFrom = document.getElementById("date-from");
  const dateTo = document.getElementById("date-to");
  const statusTabs = Array.from(document.querySelectorAll(".status-tab"));
  const orderRows = Array.from(
    document.querySelectorAll("tbody tr[data-order-id]"),
  );
  const statusSelects = Array.from(
    document.querySelectorAll(".order-status-select"),
  );
  const viewButtons = Array.from(document.querySelectorAll(".btn-view"));

  let orders = [];
  let currentPage = 0;
  const pageSize = 12;
  let totalPages = 0;
  let totalElements = 0;

  // Utility functions
  function formatDate(dateString) {
    if (!dateString) return "N/A";
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString("vi-VN");
    } catch (e) {
      return dateString;
    }
  }

  function formatCurrency(amount) {
    if (amount === null || amount === undefined) return "0 VND";
    try {
      return new Intl.NumberFormat("vi-VN").format(amount) + " VND";
    } catch (e) {
      return amount + " VND";
    }
  }

  function fetchOrders(status = "all", page = 0) {
    const params = new URLSearchParams();
    if (status && status !== "all") {
      // Map frontend status to backend enum
      const statusMap = {
        pending: "PENDING",
        active: "IN_PROGRESS",
        completed: "COMPLETED",
        cancelled: "CANCELLED",
      };
      const apiStatus = statusMap[status] || status.toUpperCase();
      params.set("status", apiStatus);
    }
    // include date range if set
    const fromVal = dateFrom && dateFrom.value ? dateFrom.value : null;
    const toVal = dateTo && dateTo.value ? dateTo.value : null;
    if (fromVal && toVal) {
      params.set("dateFrom", fromVal);
      params.set("dateTo", toVal);
    }
    params.set("page", page);
    params.set("size", pageSize);
    fetch("/api/admin/orders?" + params.toString())
      .then((r) => r.json())
      .then((data) => {
        orders = data.content || [];
        totalElements = data.totalElements || 0;
        totalPages = data.totalPages || 0;
        currentPage = page;
        renderTable();
        renderPagination();
        // Update counts after loading orders (ensures active tab count is correct quickly)
        updateStatusTabsCounts()
          .catch((e) => console.warn("update counts failed", e))
          .finally(() => {
            updateStatCards().catch((e) =>
              console.warn("update stats failed", e),
            );
          });
      })
      .catch((err) => console.error("fetchOrders error", err));
  }

  // Fetch counts for each status and update the tab labels
  const tabLabelMap = {
    all: "Tất cả",
    pending: "Chờ duyệt",
    active: "Đang thuê",
    completed: "Hoàn thành",
    cancelled: "Đã hủy",
  };

  const apiStatusMap = {
    pending: "PENDING",
    active: "IN_PROGRESS",
    completed: "COMPLETED",
    cancelled: "CANCELLED",
  };

  async function fetchCountForStatus(statusKey, opts = {}) {
    // opts: { from: 'YYYY-MM-DD', to: 'YYYY-MM-DD', ignoreDateInputs: boolean }
    try {
      const params = new URLSearchParams();
      // include explicit date range from opts, else use inputs unless ignored
      let fromVal = null;
      let toVal = null;
      if (opts.from && opts.to) {
        fromVal = opts.from;
        toVal = opts.to;
      } else if (!opts.ignoreDateInputs) {
        fromVal = dateFrom && dateFrom.value ? dateFrom.value : null;
        toVal = dateTo && dateTo.value ? dateTo.value : null;
      }
      if (fromVal && toVal) {
        params.set("dateFrom", fromVal);
        params.set("dateTo", toVal);
      }
      params.set("page", 0);
      params.set("size", 1);
      if (statusKey && statusKey !== "all") {
        const apiStatus = apiStatusMap[statusKey] || statusKey.toUpperCase();
        params.set("status", apiStatus);
      }
      const res = await fetch("/api/admin/orders?" + params.toString());
      if (!res.ok) throw new Error("Network error");
      const data = await res.json();
      return data.totalElements || 0;
    } catch (err) {
      console.warn("fetchCountForStatus error", err);
      return 0;
    }
  }

  async function updateStatusTabsCounts() {
    const promises = statusTabs.map((tab) =>
      fetchCountForStatus(tab.getAttribute("data-status")),
    );
    const counts = await Promise.all(promises);
    statusTabs.forEach((tab, idx) => {
      const key = tab.getAttribute("data-status");
      const label = tabLabelMap[key] || tab.textContent.split("(")[0].trim();
      const count = counts[idx] || 0;
      tab.textContent = count > 0 ? `${label} (${count})` : label;
    });
  }

  // Format integer counts with thousands separator
  function formatCount(n) {
    try {
      return new Intl.NumberFormat("vi-VN").format(n);
    } catch (e) {
      return String(n);
    }
  }

  // Update the top stat cards
  async function updateStatCards() {
    try {
      const totalP = document.getElementById("stat-total");
      const pendingP = document.getElementById("stat-pending");
      const activeP = document.getElementById("stat-active");
      const completedMonthP = document.getElementById("stat-completed-month");

      const now = new Date();
      const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
      const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0);
      const fmt = (d) => d.toISOString().slice(0, 10);

      const [total, pending, active, completedMonth] = await Promise.all([
        fetchCountForStatus("all", { ignoreDateInputs: true }),
        fetchCountForStatus("pending", { ignoreDateInputs: true }),
        fetchCountForStatus("active", { ignoreDateInputs: true }),
        fetchCountForStatus("completed", {
          from: fmt(firstDay),
          to: fmt(lastDay),
        }),
      ]);

      if (totalP) totalP.textContent = formatCount(total);
      if (pendingP) pendingP.textContent = formatCount(pending);
      if (activeP) activeP.textContent = formatCount(active);
      if (completedMonthP)
        completedMonthP.textContent = formatCount(completedMonth);
    } catch (err) {
      console.warn("updateStatCards error", err);
    }
  }

  function renderTable() {
    const tbody = document.querySelector("tbody");
    if (!tbody) return;

    // If no orders, show empty state
    if (orders.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="7" class="px-6 py-12 text-center text-sm text-gray-500">
            <div class="flex flex-col items-center">
              <span class="material-symbols-outlined text-4xl text-gray-300 mb-2">receipt_long</span>
              <p>Không có đơn hàng nào</p>
            </div>
          </td>
        </tr>
      `;
    } else {
      tbody.innerHTML = orders
        .map(
          (order) => `
        <tr data-order-id="${order.id}">
          <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">#${order.id}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${order.userName || "N/A"}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${order.vehicleName || "N/A"}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${formatDate(order.dateFrom)} - ${formatDate(order.dateTo)}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${formatCurrency(order.totalPrice)}</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <select class="order-status-select" data-order-id="${order.id}">
              <option value="PENDING" ${order.status === "PENDING" ? "selected" : ""}>Chờ duyệt</option>
              <option value="CONFIRMED" ${order.status === "CONFIRMED" ? "selected" : ""}>Đã duyệt</option>
              <option value="IN_PROGRESS" ${order.status === "IN_PROGRESS" ? "selected" : ""}>Đang thuê</option>
              <option value="COMPLETED" ${order.status === "COMPLETED" ? "selected" : ""}>Hoàn thành</option>
              <option value="CANCELLED" ${order.status === "CANCELLED" ? "selected" : ""}>Đã hủy</option>
            </select>
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
            <button class="btn-view text-indigo-600 hover:text-indigo-900" data-order-id="${order.id}">Xem</button>
          </td>
        </tr>
      `,
        )
        .join("");
    }

    // Re-attach handlers
    attachHandlers();
  }

  function renderPagination() {
    const paginationContainer = document.querySelector(".pagination-container");
    if (!paginationContainer) return;

    let html = `
      <p class="text-xs font-medium text-[#617589] dark:text-slate-400">
        Hiển thị ${currentPage * pageSize + 1} - ${Math.min((currentPage + 1) * pageSize, totalElements)} trong tổng số ${totalElements} đơn hàng
      </p>
      <div class="flex items-center gap-1">
    `;

    // Previous button
    html += `<button id="btn-page-prev" class="size-8 flex items-center justify-center rounded border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-400 hover:text-primary transition-colors ${currentPage === 0 ? "opacity-50 cursor-not-allowed" : ""}">
      <span class="material-symbols-outlined text-sm">chevron_left</span>
    </button>`;

    // Page buttons
    const startPage = Math.max(0, currentPage - 2);
    const endPage = Math.min(totalPages - 1, currentPage + 2);

    if (startPage > 0) {
      html += `<button data-page="0" class="page-btn size-8 flex items-center justify-center rounded bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 text-xs font-bold text-slate-600 dark:text-slate-400 hover:border-primary">1</button>`;
      if (startPage > 1) html += `<span class="px-1 text-slate-400">...</span>`;
    }

    for (let i = startPage; i <= endPage; i++) {
      const isActive = i === currentPage;
      html += `<button data-page="${i}" class="page-btn size-8 flex items-center justify-center rounded ${isActive ? "bg-primary text-white" : "bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 text-xs font-bold text-slate-600 dark:text-slate-400 hover:border-primary"}">${i + 1}</button>`;
    }

    if (endPage < totalPages - 1) {
      if (endPage < totalPages - 2)
        html += `<span class="px-1 text-slate-400">...</span>`;
      html += `<button data-page="${totalPages - 1}" class="page-btn size-8 flex items-center justify-center rounded bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 text-xs font-bold text-slate-600 dark:text-slate-400 hover:border-primary">${totalPages}</button>`;
    }

    // Next button
    html += `<button id="btn-page-next" class="size-8 flex items-center justify-center rounded border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-400 hover:text-primary transition-colors ${currentPage === totalPages - 1 ? "opacity-50 cursor-not-allowed" : ""}">
      <span class="material-symbols-outlined text-sm">chevron_right</span>
    </button>`;

    html += `</div>`;
    paginationContainer.innerHTML = html;

    // Attach event listeners
    document.getElementById("btn-page-prev")?.addEventListener("click", () => {
      if (currentPage > 0)
        fetchOrders(
          document
            .querySelector(".status-tab.text-primary")
            ?.getAttribute("data-status") || "all",
          currentPage - 1,
        );
    });
    document.getElementById("btn-page-next")?.addEventListener("click", () => {
      if (currentPage < totalPages - 1)
        fetchOrders(
          document
            .querySelector(".status-tab.text-primary")
            ?.getAttribute("data-status") || "all",
          currentPage + 1,
        );
    });
    document.querySelectorAll(".page-btn").forEach((btn) => {
      btn.addEventListener("click", () => {
        const page = parseInt(btn.getAttribute("data-page"));
        fetchOrders(
          document
            .querySelector(".status-tab.text-primary")
            ?.getAttribute("data-status") || "all",
          page,
        );
      });
    });
  }

  function attachHandlers() {
    const newStatusSelects = Array.from(
      document.querySelectorAll(".order-status-select"),
    );
    const newViewButtons = Array.from(document.querySelectorAll(".btn-view"));
    newStatusSelects.forEach((sel) => {
      sel.addEventListener("change", async (e) => {
        const orderId = sel.getAttribute("data-order-id");
        const newStatus = sel.value;
        try {
          const res = await fetch(
            "/api/admin/orders/" + encodeURIComponent(orderId) + "/status",
            {
              method: "PATCH",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({ status: newStatus }),
            },
          );
          if (!res.ok) throw new Error("Server error");
          // Update local order status
          const order = orders.find((o) => o.id == orderId);
          if (order) order.status = newStatus;
          // refresh counts because status change affects tab counts
          updateStatusTabsCounts()
            .catch((e) => console.warn("update counts failed", e))
            .finally(() => {
              updateStatCards().catch((e) =>
                console.warn("update stats failed", e),
              );
            });
        } catch (err) {
          alert("Không thể cập nhật trạng thái: " + err.message);
        }
      });
    });
    newViewButtons.forEach((btn) => {
      btn.addEventListener("click", () => {
        const id = btn.getAttribute("data-order-id");
        // navigate to detail page (placeholder)
        window.location.href =
          "../use/chi_tiet_xe.html?orderId=" + encodeURIComponent(id);
      });
    });
  }

  function filterRows({ query = "", status = "all", from, to } = {}) {
    const q = query.trim().toLowerCase();
    orderRows.forEach((row) => {
      const text = row.innerText.toLowerCase();
      let visible = true;
      if (q && !text.includes(q)) visible = false;
      if (status && status !== "all") {
        const sel = row.querySelector(".order-status-select");
        if (sel && sel.value !== status) visible = false;
      }
      // date filtering skipped for simplicity
      row.style.display = visible ? "" : "none";
    });
  }

  if (searchInput) {
    searchInput.addEventListener("input", (e) => {
      const q = e.target.value || "";
      filterRows({ query: q });
    });
  }

  statusTabs.forEach((tab) => {
    tab.addEventListener("click", (e) => {
      e.preventDefault();
      const status = tab.getAttribute("data-status");
      // highlight active
      statusTabs.forEach((t) =>
        t.classList.remove("text-primary", "border-primary"),
      );
      tab.classList.add("text-primary", "border-primary");
      fetchOrders(status, 0); // Reset to page 0
    });
  });

  // When date inputs change, refetch current status/page 0
  if (dateFrom) {
    dateFrom.addEventListener("change", () => {
      const activeStatus =
        document
          .querySelector(".status-tab.text-primary")
          ?.getAttribute("data-status") || "all";
      fetchOrders(activeStatus, 0);
    });
  }
  if (dateTo) {
    dateTo.addEventListener("change", () => {
      const activeStatus =
        document
          .querySelector(".status-tab.text-primary")
          ?.getAttribute("data-status") || "all";
      fetchOrders(activeStatus, 0);
    });
  }

  if (exportBtn) {
    exportBtn.addEventListener("click", async () => {
      try {
        const res = await fetch("/api/admin/orders/export");
        if (!res.ok) throw new Error("Export failed");
        const blob = await res.blob();
        const url = URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        // Prefer filename from Content-Disposition header when provided
        const cd = res.headers.get("content-disposition") || "";
        let filename = "orders.xlsx";
        const m =
          cd.match(/filename\*=(?:UTF-8'' )?([^;\n]+)/i) ||
          cd.match(/filename="?([^";]+)"?/i);
        if (m && m[1]) {
          try {
            filename = decodeURIComponent(m[1].replace(/"/g, ""));
          } catch (e) {
            filename = m[1].replace(/"/g, "");
          }
        } else {
          const ct = (res.headers.get("content-type") || "").toLowerCase();
          if (ct.includes("csv")) filename = "orders.csv";
          else if (ct.includes("openxmlformats-officedocument"))
            filename = "orders.xlsx";
        }
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        a.remove();
        URL.revokeObjectURL(url);
      } catch (err) {
        alert("Không thể xuất báo cáo: " + (err.message || err));
      }
    });
  }

  // Initial load
  fetchOrders();
})();
