(function () {
  const loginBtn = document.getElementById("btn-login");
  const registerBtn = document.getElementById("btn-register");
  const searchBtn = document.getElementById("btn-search");
  const newsletterBtn = document.getElementById("btn-newsletter");
  const newsletterEmail = document.getElementById("newsletter-email");

  if (loginBtn)
    loginBtn.addEventListener("click", () => {
      window.location.href = "../login/login.html";
    });

  if (registerBtn)
    registerBtn.addEventListener("click", () => {
      window.location.href = "../login/login.html#signup";
    });

  if (searchBtn) {
    searchBtn.addEventListener("click", async () => {
      const widget = searchBtn.closest("section") || document;
      const selects = widget.querySelectorAll("select");
      const input = widget.querySelector(
        'input[type="text"], input[type="date"], input[type="email"]'
      );
      const type = selects[0] ? selects[0].value : "";
      const location = selects[1] ? selects[1].value : "";
      const date = input ? input.value : "";
      const params = { type, location, date };
      try {
        const results = await window.api.searchVehicles(params);
        // store results temporarily and navigate to list page
        try {
          sessionStorage.setItem("searchResults", JSON.stringify(results));
        } catch (e) {}
        const qs = new URLSearchParams(params).toString();
        window.location.href = "../use/danh_sach_xe.html?" + qs;
      } catch (err) {
        alert("Lỗi khi tìm kiếm: " + (err.message || err));
      }
    });
  }

  document.querySelectorAll('[data-add="promo"]').forEach((btn) => {
    btn.addEventListener("click", async () => {
      const item = { type: "promo", id: btn.getAttribute("data-id") || null };
      btn.disabled = true;
      try {
        await window.api.addToCart(item);
        btn.classList.add("bg-primary", "text-white");
      } catch (err) {
        btn.disabled = false;
        alert("Không thể thêm vào giỏ: " + (err.message || err));
      }
    });
  });

  if (newsletterBtn) {
    newsletterBtn.addEventListener("click", async (e) => {
      e.preventDefault();
      const email = newsletterEmail ? newsletterEmail.value.trim() : "";
      if (!email || !/^\S+@\S+\.\S+$/.test(email)) {
        alert("Vui lòng nhập email hợp lệ.");
        return;
      }
      try {
        await window.api.subscribeNewsletter(email);
        alert("Cảm ơn! Chúng tôi đã nhận email: " + email);
        newsletterEmail.value = "";
      } catch (err) {
        alert("Đăng ký thất bại: " + (err.message || err));
      }
    });
  }
})();
