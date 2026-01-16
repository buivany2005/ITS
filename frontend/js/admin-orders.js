(function () {
  const exportBtn = document.getElementById("btn-export");
  const searchInput = document.getElementById("order-search");
  const dateFrom = document.getElementById("date-from");
  const dateTo = document.getElementById("date-to");
  const statusTabs = Array.from(document.querySelectorAll(".status-tab"));
  const orderRows = Array.from(
    document.querySelectorAll("tbody tr[data-order-id]")
  );
  const statusSelects = Array.from(
    document.querySelectorAll(".order-status-select")
  );
  const viewButtons = Array.from(document.querySelectorAll(".btn-view"));
  const pagePrev = document.getElementById("btn-page-prev");
  const pageNext = document.getElementById("btn-page-next");
  const pageButtons = Array.from(document.querySelectorAll(".page-btn"));

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
        t.classList.remove("text-primary", "border-primary")
      );
      tab.classList.add("text-primary", "border-primary");
      filterRows({ status });
    });
  });

  statusSelects.forEach((sel) => {
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
          }
        );
        if (!res.ok) throw new Error("Server error");
        // optionally show toast
      } catch (err) {
        alert("Không thể cập nhật trạng thái: " + err.message);
      }
    });
  });

  viewButtons.forEach((btn) => {
    btn.addEventListener("click", () => {
      const id = btn.getAttribute("data-order-id");
      // navigate to detail page (placeholder)
      window.location.href =
        "../use/chi_tiet_xe.html?orderId=" + encodeURIComponent(id);
    });
  });

  if (exportBtn) {
    exportBtn.addEventListener("click", async () => {
      try {
        const res = await fetch("/api/admin/orders/export");
        if (!res.ok) throw new Error("Export failed");
        const blob = await res.blob();
        const url = URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = "orders.xlsx";
        document.body.appendChild(a);
        a.click();
        a.remove();
        URL.revokeObjectURL(url);
      } catch (err) {
        alert("Không thể xuất báo cáo: " + (err.message || err));
      }
    });
  }

  // Pagination simple handlers: navigate by updating query param (placeholder)
  pageButtons.forEach((btn) => {
    btn.addEventListener("click", () => {
      const p = btn.getAttribute("data-page");
      const url = new URL(window.location.href);
      url.searchParams.set("page", p);
      window.location.href = url.toString();
    });
  });
  if (pagePrev)
    pagePrev.addEventListener("click", () => {
      window.history.back();
    });
  if (pageNext)
    pageNext.addEventListener("click", () => {
      window.history.forward();
    });
})();
