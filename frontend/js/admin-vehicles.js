(function () {
  // --- 1. CẤU HÌNH & BIẾN ---
  const API_URL = "http://localhost:8080/vehicles"; // Đổi port nếu backend bạn khác
  
  // Các element trong HTML
  const qs = (selector) => document.querySelector(selector);
  const qsa = (selector) => document.querySelectorAll(selector);

  const inputSearch = qs("#vehicle-search");
  const categoryBtns = qsa(".category-btn");
  const tableBody = qs("#vehicles-table-body");
  
  // Elements cho Modal Thêm mới
  const modal = qs("#add-vehicle-modal");
  const btnAddOpen = qs("#btn-add-vehicle"); // Nút mở modal
  const btnAddClose = qs("#btn-cancel-add"); // Nút hủy
  const btnAddCloseX = qs("#btn-close-modal-x"); // Nút X
  const formAdd = qs("#add-vehicle-form"); // Form

  // Elements cho Phân trang
  const btnPrev = qs("#btn-prev-page");
  const btnNext = qs("#btn-next-page");
  const labelCurrent = qs("#page-current");
  const labelTotal = qs("#page-total");

  // Trạng thái (State) của trang
  let state = {
    vehicles: [],
    currentPage: 0,   // Trang hiện tại (bắt đầu từ 0)
    totalPages: 1,    // Tổng số trang
    limit: 12,        // 12 xe mỗi trang
    search: "",       // Từ khóa tìm kiếm
    category: "all"   // Bộ lọc loại xe
  };

  // --- 2. HÀM TẢI DỮ LIỆU TỪ SERVER ---
  function fetchVehicles() {
    // Tạo đường dẫn có tham số: ?page=0&size=12&search=...
    const params = new URLSearchParams();
    params.append("page", state.currentPage);
    params.append("size", state.limit);
    
    if (state.search) params.append("search", state.search);
    if (state.category && state.category !== "all") {
        params.append("vehicleType", state.category);
    }

    // Gọi API
    fetch(`${API_URL}?${params.toString()}`)
      .then((response) => {
          if (!response.ok) throw new Error("Lỗi kết nối Server");
          return response.json();
      })
      .then((data) => {
        // Backend trả về: content (list xe), totalPages (tổng trang)
        // Lưu ý: Tùy backend trả về dạng Page của Spring Boot hay List thường
        if (data.content) {
            // Nếu trả về dạng Page chuẩn Spring
            state.vehicles = data.content;
            state.totalPages = data.totalPages;
        } else if (data.vehicles) {
             // Nếu trả về dạng Map custom (như code Controller mình gửi lúc đầu)
             state.vehicles = data.vehicles;
             state.totalPages = data.totalPages;
        } else {
            // Trường hợp dự phòng (List thường)
            state.vehicles = Array.isArray(data) ? data : [];
            state.totalPages = 1;
        }
        
        renderTable();
        renderPagination();
      })
      .catch((err) => console.error("Lỗi tải xe:", err));
  }

  // --- 3. HÀM HIỂN THỊ BẢNG (RENDER) ---
  function renderTable() {
    if (!tableBody) return;
    
    if (state.vehicles.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="5" class="text-center py-4">Không tìm thấy xe nào</td></tr>`;
        return;
    }

    tableBody.innerHTML = state.vehicles
      .map((v) => `
        <tr class="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-800 transition">
          <td class="px-6 py-4">
             <div class="flex items-center gap-3">
                <img src="${v.imageUrl || 'https://via.placeholder.com/50'}" class="h-10 w-10 rounded object-cover" alt="Ảnh xe">
                <div>
                  <div class="font-medium text-gray-900 dark:text-white">${v.name}</div>
                  <div class="text-xs text-gray-500">${v.licensePlate || 'Chưa có biển'}</div>
                </div>
             </div>
          </td>
          <td class="px-6 py-4 text-gray-600 dark:text-gray-300">${v.brand || '-'}</td>
          <td class="px-6 py-4">
              <span class="inline-flex items-center rounded-md bg-gray-100 px-2 py-1 text-xs font-medium text-gray-600 ring-1 ring-inset ring-gray-500/10 dark:bg-gray-800 dark:text-gray-400">
                  ${v.vehicleType}
              </span>
          </td>
          <td class="px-6 py-4 font-semibold text-primary">
              ${v.pricePerDay ? v.pricePerDay.toLocaleString() : 0} đ
          </td>
          <td class="px-6 py-4">
              <span class="inline-flex items-center rounded-full px-2 py-1 text-xs font-medium ${v.status === 'AVAILABLE' ? 'bg-green-50 text-green-700 ring-1 ring-inset ring-green-600/20' : 'bg-red-50 text-red-700 ring-1 ring-inset ring-red-600/20'}">
                  ${v.status === 'AVAILABLE' ? 'Sẵn sàng' : 'Đang thuê'}
              </span>
          </td>
        </tr>
      `).join("");
  }

  // --- 4. HÀM HIỂN THỊ PHÂN TRANG ---
  function renderPagination() {
      if (labelCurrent) labelCurrent.innerText = state.currentPage + 1;
      if (labelTotal) labelTotal.innerText = state.totalPages > 0 ? state.totalPages : 1;
      
      // Disable nút nếu đang ở trang đầu hoặc cuối
      if (btnPrev) btnPrev.disabled = state.currentPage === 0;
      if (btnNext) btnNext.disabled = state.currentPage >= state.totalPages - 1;
  }

  // --- 5. XỬ LÝ SỰ KIỆN (EVENTS) ---

  // 5.1 Xử lý Modal Thêm mới (Đã sửa lỗi nút Hủy)
  // --- XỬ LÝ ĐÓNG/MỞ MODAL (Code cứng - Chắc chắn chạy) ---
  
  // 1. Hàm đóng/mở
  function toggleModal(show) {
    const modal = document.getElementById("add-vehicle-modal");
    if (!modal) {
        console.error("Không tìm thấy Modal có id='add-vehicle-modal'");
        return;
    }
    
    if (show) {
        modal.classList.remove("hidden");
    } else {
        modal.classList.add("hidden");
        // Reset form khi đóng để lần sau mở ra nó trống trơn
        const form = document.getElementById("add-vehicle-form");
        if(form) form.reset(); 
    }
  }

// 2. Bắt sự kiện click toàn trang (Fix lỗi nút không ăn)
  document.addEventListener("click", function(e) {
      const target = e.target;

    // Nếu bấm nút MỞ (btn-add-vehicle)
      if (target.closest("#btn-add-vehicle")) {
          toggleModal(true);
      }

    // Nếu bấm nút HỦY (btn-cancel-add)
      if (target.closest("#btn-cancel-add")) {
          e.preventDefault(); // Chặn load lại trang
          toggleModal(false);
      }

    // Nếu bấm nút X ở góc (btn-close-modal-x)
      if (target.closest("#btn-close-modal-x")) {
          e.preventDefault();
          toggleModal(false);
      }

    // Nếu bấm ra vùng đen mờ bên ngoài (modal-overlay)
      if (target.classList.contains("backdrop-blur-sm") || target.id === "add-vehicle-modal") {
          toggleModal(false);
      }
  });

  // 5.2 Xử lý Submit Form Thêm
  if (formAdd) {
      formAdd.addEventListener("submit", function (e) {
          e.preventDefault();
          const formData = new FormData(formAdd);
          const data = Object.fromEntries(formData.entries());

          fetch(API_URL, {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify(data)
          })
          .then(res => {
              if (res.ok) {
                  alert("Thêm xe thành công!");
                  toggleModal(false);
                  formAdd.reset();
                  fetchVehicles(); // Tải lại danh sách
              } else {
                  alert("Có lỗi xảy ra khi thêm xe!");
              }
          })
          .catch(err => console.error(err));
      });
  }

  // 5.3 Xử lý Chuyển trang (Next/Prev)
  if (btnPrev) {
      btnPrev.addEventListener("click", () => {
          if (state.currentPage > 0) {
              state.currentPage--;
              fetchVehicles();
          }
      });
  }

  if (btnNext) {
      btnNext.addEventListener("click", () => {
          if (state.currentPage < state.totalPages - 1) {
              state.currentPage++;
              fetchVehicles();
          }
      });
  }

  // 5.4 Xử lý Tìm kiếm (Debounce)
  let timeoutSearch;
  if (inputSearch) {
      inputSearch.addEventListener("input", (e) => {
          clearTimeout(timeoutSearch);
          timeoutSearch = setTimeout(() => {
              state.search = e.target.value.trim();
              state.currentPage = 0; // Reset về trang 1
              fetchVehicles();
          }, 500);
      });
  }

  // 5.5 Xử lý Lọc theo loại (Category)
  categoryBtns.forEach(btn => {
      btn.addEventListener("click", (e) => {
          // Đổi màu nút active
          categoryBtns.forEach(b => {
              b.classList.remove("bg-primary", "text-white");
              b.classList.add("bg-gray-100", "text-gray-600");
          });
          e.currentTarget.classList.remove("bg-gray-100", "text-gray-600");
          e.currentTarget.classList.add("bg-primary", "text-white");
          
          // Cập nhật state
          state.category = e.currentTarget.dataset.category || "all";
          state.currentPage = 0;
          fetchVehicles();
      });
  });

  // --- 6. KHỞI CHẠY LẦN ĐẦU ---
  fetchVehicles();


// --- CODE XỬ LÝ AVATAR & ĐĂNG XUẤT (Dán vào cuối file JS) ---

  const API_AUTH = "http://localhost:8080/auth";
  // Lấy userId từ lúc đăng nhập (nếu chưa có thì tạm để 1 để test)
  const userId = localStorage.getItem("userId") || 1; 

  // 1. Hàm tải Avatar từ Server
  function loadUserAvatar() {
      const img = document.querySelector("#header-avatar");
      if(!img) return;

      // Gọi API lấy thông tin user (Cần update Backend sau bước này)
      fetch(API_AUTH + "/" + userId)
          .then(res => {
              if(res.ok) return res.json();
              throw new Error("Chưa có API lấy user");
          })
          .then(user => {
              if (user && user.avatarUrl) {
                  img.src = user.avatarUrl;
              }
          })
          .catch(err => {
              console.log("Dùng avatar mặc định. " + err);
          });
  }

  // 2. Hàm click để đổi Avatar
  window.changeAvatar = function() {
      let newUrl = prompt("Dán link ảnh avatar mới của bạn vào đây:");
      if (newUrl && newUrl.trim() !== "") {
          // Cập nhật giao diện ngay lập tức để thấy thay đổi
          document.querySelector("#header-avatar").src = newUrl;
          
          // Gửi lên server lưu lại (Sẽ làm ở bước tiếp theo)
          fetch(API_AUTH + "/update/" + userId, {
              method: "PUT",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({ avatarUrl: newUrl })
          }).then(res => {
              if(res.ok) alert("Đã lưu ảnh mới vào hệ thống!");
              else console.log("Cần cập nhật Backend để lưu ảnh vĩnh viễn");
          });
      }
  };

  // 3. Xử lý nút Đăng xuất
  const btnLogout = document.querySelector("#btn-logout");
  if(btnLogout) {
      btnLogout.addEventListener("click", (e) => {
          e.preventDefault();
          if(confirm("Bạn muốn đăng xuất?")) {
              localStorage.clear(); // Xóa thông tin đăng nhập
              window.location.href = "login.html"; // Chuyển về trang login (tạo sau)
          }
      });
  }

  // Chạy tải avatar khi vào trang
  loadUserAvatar();
})();