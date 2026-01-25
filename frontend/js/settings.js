document.addEventListener("DOMContentLoaded", function () {
  const form = document.querySelector("form");
  form.addEventListener("submit", function (e) {
    e.preventDefault();
    const password = form.querySelector("input[type='password']").value;
    alert("Mật khẩu mới: " + password);
    
    // Nếu muốn gửi về backend, bạn có thể dùng fetch như sau:
    /*
    fetch("http://localhost:8080/api/settings/change-password", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ password })
    }).then(res => {
      if (res.ok) alert("Đổi mật khẩu thành công!");
      else alert("Có lỗi xảy ra!");
    });
    */
  });
});
