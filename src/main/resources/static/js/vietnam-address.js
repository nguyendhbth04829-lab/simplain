/**
 * Bộ dữ liệu Địa giới hành chính Việt Nam (Tỉnh/Thành phố -> Quận/Huyện -> Phường/Xã) (US13).
 * Dùng cho Dropdown cấp bậc liên kết độc lập, không phụ thuộc internet/mạng ngoài.
 */
const DULIEU_HANH_CHINH_VN = [
    {
        name: "Hà Nội",
        districts: [
            { name: "Ba Đình", wards: ["Cống Vị", "Điện Biên", "Đội Cấn", "Giảng Võ", "Kim Mã", "Liễu Giai", "Ngọc Hà", "Ngọc Khánh", "Nguyễn Trung Trực", "Phúc Xá", "Quán Thánh", "Thành Công", "Trúc Bạch", "Vĩnh Phúc"] },
            { name: "Cầu Giấy", wards: ["Dịch Vọng", "Dịch Vọng Hậu", "Mai Dịch", "Nghĩa Đô", "Nghĩa Tân", "Quan Hoa", "Trung Hòa", "Yên Hòa"] },
            { name: "Đống Đa", wards: ["Cát Linh", "Hàng Bột", "Khâm Thiên", "Khương Thượng", "Kim Liên", "Láng Hạ", "Láng Thượng", "Nam Đồng", "Ngã Tư Sở", "Ô Chợ Dừa", "Phương Liên", "Phương Mai", "Quang Trung", "Quốc Tử Giám", "Thịnh Quang", "Thổ Quan", "Trung Liệt", "Trung Phụng", "Trung Tự", "Văn Chương", "Văn Miếu"] },
            { name: "Hai Bà Trưng", wards: ["Bách Khoa", "Bạch Đằng", "Bạch Mai", "Cầu Dền", "Đồng Nhân", "Đồng Tâm", "Lê Đại Hành", "Minh Khai", "Nguyễn Du", "Phạm Đình Hổ", "Phố Huế", "Quỳnh Lôi", "Quỳnh Mai", "Thanh Lương", "Thanh Nhàn", "Trương Định", "Vĩnh Tuy"] },
            { name: "Hoàn Kiếm", wards: ["Chương Dương", "Cửa Đông", "Cửa Nam", "Đồng Xuân", "Hàng Bạc", "Hàng Bài", "Hàng Bồ", "Hàng Bông", "Hàng Buồm", "Hàng Đào", "Hàng Gai", "Hàng Mã", "Hàng Trống", "Lý Thái Tổ", "Phan Chu Trinh", "Phúc Tân", "Trần Hưng Đạo", "Tràng Tiền"] },
            { name: "Thanh Xuân", wards: ["Hạ Đình", "Khương Đình", "Khương Mai", "Khương Trung", "Kim Giang", "Nhân Chính", "Phương Liệt", "Thanh Xuân Bắc", "Thanh Xuân Nam", "Thanh Xuân Trung", "Thượng Đình"] },
            { name: "Hoàng Mai", wards: ["Đại Kim", "Định Công", "Giáp Bát", "Hoàng Liệt", "Hoàng Văn Thụ", "Lĩnh Nam", "Mai Động", "Tân Mai", "Thanh Trì", "Thịnh Liệt", "Trần Phú", "Tương Mai", "Vĩnh Hưng", "Yên Sở"] },
            { name: "Long Biên", wards: ["Bồ Đề", "Cự Khối", "Đức Giang", "Gia Thụy", "Giang Biên", "Long Biên", "Ngọc Lâm", "Ngọc Thụy", "Phúc Đồng", "Phúc Lợi", "Sài Đồng", "Thạch Bàn", "Thượng Thanh", "Việt Hưng"] },
            { name: "Tây Hồ", wards: ["Bưởi", "Nhật Tân", "Phú Thượng", "Quảng An", "Thụy Khuê", "Tứ Liên", "Xuân La", "Yên Phụ"] },
            { name: "Nam Từ Liêm", wards: ["Cầu Diễn", "Đại Mỗ", "Mễ Trì", "Mỹ Đình 1", "Mỹ Đình 2", "Phú Đô", "Phương Canh", "Tây Mỗ", "Trung Văn", "Xuân Phương"] },
            { name: "Bắc Từ Liêm", wards: ["Cổ Nhuế 1", "Cổ Nhuế 2", "Đông Ngạc", "Đức Thắng", "Liên Mạc", "Minh Khai", "Phú Diễn", "Phúc Diễn", "Tây Tựu", "Thụy Phương", "Thượng Cát", "Xuân Đỉnh", "Xuân Tảo"] },
            { name: "Hà Đông", wards: ["Biên Giang", "Dương Nội", "Đồng Mai", "Hà Cầu", "Kiến Hưng", "La Khê", "Mộ Lao", "Nguyễn Trãi", "Phú La", "Phú Lãm", "Phú Lương", "Phúc La", "Quang Trung", "Vạn Phúc", "Văn Quán", "Yên Nghĩa", "Yết Kiêu"] },
            { name: "Sơn Tây", wards: ["Lê Lợi", "Ngô Quyền", "Phú Thịnh", "Quang Trung", "Sơn Lộc", "Trung Hưng", "Trung Sơn Trầm", "Xuân Khanh"] },
            { name: "Gia Lâm", wards: ["Trâu Quỳ", "Yên Viên", "Bát Tràng", "Cổ Bi", "Đa Tốn", "Đặng Xá", "Kiêu Kỵ", "Ninh Hiệp", "Phù Đổng"] },
            { name: "Đông Anh", wards: ["Đông Anh", "Bắc Hồng", "Cổ Loa", "Dục Tú", "Hải Bối", "Kim Chung", "Mai Lâm", "Nam Hồng", "Tiên Dương", "Vân Hà", "Vân Nội", "Vĩnh Ngọc", "Xuân Canh"] },
            { name: "Sóc Sơn", wards: ["Sóc Sơn", "Bắc Sơn", "Hiền Ninh", "Mai Đình", "Minh Phú", "Minh Trí", "Nam Sơn", "Phù Linh", "Quang Tiến", "Tân Hưng"] },
            { name: "Thanh Trì", wards: ["Văn Điển", "Đại Áng", "Đông Mỹ", "Duyên Hà", "Hữu Hòa", "Liên Ninh", "Ngọc Hồi", "Ngũ Hiệp", "Tả Thanh Oai", "Tam Hiệp", "Tân Triều", "Thanh Liệt", "Tứ Hiệp", "Vĩnh Quỳnh", "Yên Mỹ"] },
            { name: "Hoài Đức", wards: ["Trạm Trôi", "An Khánh", "An Thượng", "Cát Quế", "Đắc Sở", "Di Trạch", "Đông La", "Đức Giang", "Đức Thượng", "Kim Chung", "La Phù", "Lại Yên", "Song Phương", "Sơn Đồng", "Vân Canh", "Vân Côn", "Yên Sở"] }
        ]
    },
    {
        name: "TP. Hồ Chí Minh",
        districts: [
            { name: "Quận 1", wards: ["Bến Nghé", "Bến Thành", "Cầu Kho", "Cầu Ông Lãnh", "Cô Giang", "Đa Kao", "Nguyễn Cư Trinh", "Nguyễn Thái Bình", "Phạm Ngũ Lão", "Tân Định"] },
            { name: "Quận 3", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 9", "Phường 10", "Phường 11", "Phường 12", "Phường 13", "Phường 14", "Võ Thị Sáu"] },
            { name: "Quận 4", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 6", "Phường 8", "Phường 9", "Phường 10", "Phường 13", "Phường 14", "Phường 15", "Phường 16", "Phường 18"] },
            { name: "Quận 5", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12", "Phường 13", "Phường 14"] },
            { name: "Quận 6", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12", "Phường 13", "Phường 14"] },
            { name: "Quận 7", wards: ["Bình Thuận", "Phú Mỹ", "Phú Thuận", "Tân Hưng", "Tân Kiểng", "Tân Phong", "Tân Phú", "Tân Quy", "Tân Thuận Đông", "Tân Thuận Tây"] },
            { name: "Quận 8", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12", "Phường 13", "Phường 14", "Phường 15", "Phường 16"] },
            { name: "Quận 10", wards: ["Phường 1", "Phường 2", "Phường 4", "Phường 5", "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12", "Phường 13", "Phường 14", "Phường 15"] },
            { name: "Quận 11", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12", "Phường 13", "Phường 14", "Phường 15", "Phường 16"] },
            { name: "Quận 12", wards: ["An Phú Đông", "Đông Hưng Thuận", "Hiệp Thành", "Tân Chánh Hiệp", "Tân Hưng Thuận", "Tân Thới Hiệp", "Tân Thới Nhất", "Thạnh Lộc", "Thạnh Xuân", "Thới An", "Trung Mỹ Tây"] },
            { name: "Bình Thạnh", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 5", "Phường 6", "Phường 7", "Phường 11", "Phường 12", "Phường 13", "Phường 14", "Phường 15", "Phường 17", "Phường 19", "Phường 21", "Phường 22", "Phường 24", "Phường 25", "Phường 26", "Phường 27", "Phường 28"] },
            { name: "Gò Vấp", wards: ["Phường 1", "Phường 3", "Phường 4", "Phường 5", "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12", "Phường 13", "Phường 14", "Phường 15", "Phường 16", "Phường 17"] },
            { name: "Phú Nhuận", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 13", "Phường 15", "Phường 17"] },
            { name: "Tân Bình", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12", "Phường 13", "Phường 14", "Phường 15"] },
            { name: "Tân Phú", wards: ["Hiệp Tân", "Hòa Thạnh", "Phú Thạnh", "Phú Thọ Hòa", "Phú Trung", "Sơn Kỳ", "Tân Quý", "Tân Sơn Nhì", "Tân Thành", "Tân Thới Hòa", "Tây Thạnh"] },
            { name: "Bình Tân", wards: ["An Lạc", "An Lạc A", "Bình Hưng Hòa", "Bình Hưng Hòa A", "Bình Hưng Hòa B", "Bình Trị Đông", "Bình Trị Đông A", "Bình Trị Đông B", "Tân Tạo", "Tân Tạo A"] },
            { name: "TP. Thủ Đức", wards: ["An Khánh", "An Lợi Đông", "An Phú", "Bình Chiểu", "Bình Thọ", "Hiệp Bình Chánh", "Hiệp Bình Phước", "Hiệp Phú", "Linh Chiểu", "Linh Đông", "Linh Trung", "Linh Xuân", "Long Bình", "Long Phước", "Long Thạnh Mỹ", "Long Trường", "Phú Hữu", "Phước Bình", "Phước Long A", "Phước Long B", "Tam Bình", "Tam Phú", "Tăng Nhơn Phú A", "Tăng Nhơn Phú B", "Thạnh Mỹ Lợi", "Thảo Điền", "Thủ Thiêm", "Trường Thạnh", "Trường Thọ"] },
            { name: "Bình Chánh", wards: ["Tân Túc", "An Phú Tây", "Bình Chánh", "Bình Hưng", "Bình Lợi", "Đa Phước", "Hưng Long", "Lê Minh Xuân", "Phạm Văn Hai", "Phong Phú", "Quy Đức", "Tân Kiên", "Tân Nhựt", "Tân Quý Tây", "Vĩnh Lộc A", "Vĩnh Lộc B"] },
            { name: "Hóc Môn", wards: ["Hóc Môn", "Bà Điểm", "Đông Thạnh", "Nhị Bình", "Tân Hiệp", "Tân Thới Nhì", "Tân Xuân", "Thới Tam Thôn", "Trung Chánh", "Xuân Thới Đông", "Xuân Thới Sơn", "Xuân Thới Thượng"] }
        ]
    },
    {
        name: "Đà Nẵng",
        districts: [
            { name: "Hải Châu", wards: ["Bình Hiên", "Bình Thuận", "Hải Châu 1", "Hải Châu 2", "Hòa Cường Bắc", "Hòa Cường Nam", "Hòa Thuận Đông", "Hòa Thuận Tây", "Nam Dương", "Phước Ninh", "Thạch Thang", "Thanh Bình", "Thuận Phước"] },
            { name: "Thanh Khê", wards: ["An Khê", "Chính Gián", "Hòa Khê", "Tam Thuận", "Tân Chính", "Thạc Gián", "Thanh Khê Đông", "Thanh Khê Tây", "Vĩnh Trung", "Xuân Hà"] },
            { name: "Sơn Trà", wards: ["An Hải Bắc", "An Hải Đông", "An Hải Tây", "Mân Thái", "Nại Hiên Đông", "Phước Mỹ", "Thọ Quang"] },
            { name: "Ngũ Hành Sơn", wards: ["Hòa Hải", "Hòa Quý", "Khuê Mỹ", "Mỹ An"] },
            { name: "Liên Chiểu", wards: ["Hòa Hiệp Bắc", "Hòa Hiệp Nam", "Hòa Khánh Bắc", "Hòa Khánh Nam", "Hòa Minh"] },
            { name: "Cẩm Lệ", wards: ["Hòa An", "Hòa Phát", "Hòa Thọ Đông", "Hòa Thọ Tây", "Hòa Xuân", "Khuê Trung"] },
            { name: "Hòa Vang", wards: ["Hòa Bắc", "Hòa Châu", "Hòa Khương", "Hòa Liên", "Hòa Nhơn", "Hòa Ninh", "Hòa Phong", "Hòa Phú", "Hòa Phước", "Hòa Sơn", "Hòa Tiến"] }
        ]
    },
    {
        name: "Hải Phòng",
        districts: [
            { name: "Hồng Bàng", wards: ["Hoàng Văn Thụ", "Minh Khai", "Phan Bội Châu", "Quán Toan", "Sở Dầu", "Thượng Lý", "Trại Chuối"] },
            { name: "Ngô Quyền", wards: ["Cầu Đất", "Cầu Tre", "Đằng Giang", "Đông Khê", "Đồng Quốc Bình", "Gia Viên", "Lạc Viên", "Lạch Tray", "Lê Lợi", "Máy Chai", "Máy Tơ", "Vạn Mỹ"] },
            { name: "Lê Chân", wards: ["An Biên", "An Dương", "Cát Dài", "Đông Hải", "Dư Hàng", "Dư Hàng Kênh", "Hàng Kênh", "Hồ Nam", "Kênh Dương", "Lam Sơn", "Niệm Nghĩa", "Nghĩa Xá", "Trần Nguyên Hãn", "Vĩnh Niệm"] },
            { name: "Hải An", wards: ["Cát Bi", "Đằng Hải", "Đằng Lâm", "Đông Hải 1", "Đông Hải 2", "Nam Hải", "Thành Tô", "Tràng Cát"] },
            { name: "Kiến An", wards: ["Bắc Sơn", "Đồng Hòa", "Lãm Hà", "Nam Sơn", "Ngọc Sơn", "Phù Liễn", "Quán Trữ", "Trần Thành Ngọ", "Văn Đẩu"] },
            { name: "Thủy Nguyên", wards: ["Núi Đèo", "Minh Đức", "An Lư", "Dương Quan", "Hoa Động", "Hoàng Động", "Kênh Giang", "Lâm Động", "Lập Lễ", "Lưu Kiếm", "Tam Hưng", "Thiên Hương", "Thủy Đường", "Thủy Sơn", "Trung Hà"] },
            { name: "An Dương", wards: ["An Dương", "An Đồng", "An Hòa", "An Hưng", "Bắc Sơn", "Đại Bản", "Đặng Cương", "Đồng Thái", "Hồng Phong", "Hồng Thái", "Lê Lợi", "Lê Thiện", "Nam Sơn", "Quốc Tuấn", "Tân Tiến"] }
        ]
    },
    {
        name: "Cần Thơ",
        districts: [
            { name: "Ninh Kiều", wards: ["An Bình", "An Cư", "An Hòa", "An Khánh", "An Nghiệp", "Cái Khế", "Hưng Lợi", "Tân An", "Thới Bình", "Xuân Khánh"] },
            { name: "Bình Thủy", wards: ["An Thới", "Bình Thủy", "Bùi Hữu Nghĩa", "Long Hòa", "Long Tuyền", "Thới An Đông", "Trà An", "Trà Nóc"] },
            { name: "Cái Răng", wards: ["Ba Láng", "Hưng Phú", "Hưng Thạnh", "Lê Bình", "Phú Thứ", "Tân Phú", "Yên Đỗ"] },
            { name: "Ô Môn", wards: ["Châu Văn Liêm", "Long Hưng", "Phước Thới", "Thới An", "Thới Hòa", "Thới Long"] },
            { name: "Thốt Nốt", wards: ["Tân Hưng", "Tân Lộc", "Thạnh Hòa", "Thốt Nốt", "Thới Thuận", "Trung Kiên", "Trung Nhứt"] }
        ]
    },
    {
        name: "Bình Dương",
        districts: [
            { name: "Thủ Dầu Một", wards: ["Chánh Mỹ", "Chánh Nghĩa", "Định Hòa", "Hiệp An", "Hiệp Thành", "Hòa Phú", "Phú Cường", "Phú Hòa", "Phú Lợi", "Phú Mỹ", "Phú Tân", "Phú Thọ", "Tân An", "Tương Bình Hiệp"] },
            { name: "Dĩ An", wards: ["An Bình", "Bình An", "Bình Thắng", "Dĩ An", "Đông Hòa", "Tân Bình", "Tân Đông Hiệp"] },
            { name: "Thuận An", wards: ["An Phú", "An Thạnh", "Bình Chuẩn", "Bình Hòa", "Bình Nhâm", "Hưng Định", "Lái Thiêu", "Thuận Giao", "Vĩnh Phú"] },
            { name: "Bến Cát", wards: ["An Điền", "An Tây", "Chánh Phú Hòa", "Hòa Lợi", "Mỹ Phước", "Tân Định", "Thới Hòa"] },
            { name: "Tân Uyên", wards: ["Bạch Đằng", "Khánh Bình", "Phú Chánh", "Tân Hiệp", "Tân Phước Khánh", "Tân Vĩnh Hiệp", "Thái Hòa", "Thạnh Hội", "Thạnh Phước", "Uyên Hưng", "Vĩnh Tân"] }
        ]
    },
    {
        name: "Đồng Nai",
        districts: [
            { name: "Biên Hòa", wards: ["An Bình", "An Hòa", "Bình Đa", "Bửu Hòa", "Bửu Long", "Hiệp Hòa", "Hóa An", "Hòa Bình", "Hố Nai", "Long Bình", "Long Bình Tân", "Phước Tân", "Quang Vinh", "Quyết Thắng", "Tam Hiệp", "Tam Hòa", "Tam Phước", "Tân Biên", "Tân Hạnh", "Tân Hiệp", "Tân Hòa", "Tân Mai", "Tân Phong", "Tân Tiến", "Tân Vạn", "Thanh Bình", "Thống Nhất", "Trảng Dài", "Trung Dũng"] },
            { name: "Long Khánh", wards: ["Bảo Vinh", "Bàu Sen", "Phú Bình", "Suối Tre", "Xuân An", "Xuân Bình", "Xuân Hòa", "Xuân Lập", "Xuân Tân", "Xuân Thanh", "Xuân Trung"] },
            { name: "Long Thành", wards: ["Long Thành", "An Phước", "Bàu Cạn", "Bình An", "Bình Sơn", "Cẩm Đường", "Lộc An", "Long An", "Long Đức", "Phước Bình", "Phước Thái", "Tam An", "Tân Hiệp"] },
            { name: "Nhơn Trạch", wards: ["Hiệp Phước", "Đại Phước", "Long Tân", "Long Thọ", "Phú Đông", "Phú Hữu", "Phú Hội", "Phú Thạnh", "Phước An", "Phước Khánh", "Phước Thiền", "Vĩnh Thanh"] },
            { name: "Trảng Bom", wards: ["Trảng Bom", "An Viễn", "Bắc Sơn", "Bình Minh", "Cây Gáo", "Đông Hòa", "Đồi 61", "Giang Điền", "Hố Nai 3", "Hưng Thịnh", "Quảng Tiến", "Sông Thao", "Sông Trầu", "Tây Hòa", "Thanh Bình", "Trung Hòa"] }
        ]
    },
    {
        name: "Bắc Ninh",
        districts: [
            { name: "TP. Bắc Ninh", wards: ["Đại Phúc", "Đáp Cầu", "Hạp Lĩnh", "Khắc Niệm", "Khúc Xuyên", "Kim Chân", "Kinh Bắc", "Nam Sơn", "Ninh Xá", "Phong Khê", "Suối Hoa", "Thị Cầu", "Tiền An", "Vạn An", "Vân Dương", "Vệ An", "Võ Cường", "Vũ Ninh"] },
            { name: "Từ Sơn", wards: ["Châu Khê", "Đình Bảng", "Đồng Kỵ", "Đông Ngàn", "Đồng Nguyên", "Hương Mạc", "Phù Chẩn", "Phù Khê", "Tam Sơn", "Tân Hồng", "Trang Hạ", "Tương Giang"] },
            { name: "Tiên Du", wards: ["Lim", "Cảnh Hưng", "Đại Đồng", "Hoàn Sơn", "Lạc Vệ", "Liên Bão", "Nội Duệ", "Phật Tích", "Phú Lâm", "Tân Chi", "Tri Phương", "Việt Đoàn"] },
            { name: "Yên Phong", wards: ["Chờ", "Dũng Liệt", "Đông Phong", "Đông Thọ", "Đông Tiến", "Hòa Tiến", "Long Châu", "Mai Đình", "Tam Đa", "Tam Giang", "Thụy Hòa", "Trung Nghĩa", "Văn Môn", "Yên Phụ", "Yên Trung"] },
            { name: "Quế Võ", wards: ["Phố Mới", "Bằng An", "Bồng Lai", "Cách Bi", "Chi Lăng", "Đại Xuân", "Nhân Hòa", "Phương Liễu", "Phượng Mao", "Quế Tân", "Việt Hùng"] }
        ]
    },
    {
        name: "Quảng Ninh",
        districts: [
            { name: "Hạ Long", wards: ["Bạch Đằng", "Bãi Cháy", "Cao Xanh", "Cao Thắng", "Đại Yên", "Giếng Đáy", "Hà Khánh", "Hà Khẩu", "Hà Lầm", "Hà Phong", "Hà Trung", "Hà Tu", "Hồng Gai", "Hồng Hà", "Hồng Hải", "Hùng Thắng", "Trần Hưng Đạo", "Tuần Châu", "Việt Hưng", "Yết Kiêu"] },
            { name: "Cẩm Phả", wards: ["Cẩm Bình", "Cẩm Đông", "Cẩm Phú", "Cẩm Sơn", "Cẩm Tây", "Cẩm Thạch", "Cẩm Thành", "Cẩm Thịnh", "Cẩm Thủy", "Cẩm Trung", "Cửa Ông", "Mông Dương", "Quang Hanh"] },
            { name: "Uông Bí", wards: ["Bắc Sơn", "Nam Khê", "Phương Đông", "Phương Nam", "Quang Trung", "Thanh Sơn", "Trưng Vương", "Vàng Danh", "Yên Thanh"] },
            { name: "Móng Cái", wards: ["Bình Ngọc", "Hải Hòa", "Hải Sơn", "Hải Yên", "Ka Long", "Ninh Dương", "Trà Cổ", "Trần Phú"] }
        ]
    },
    {
        name: "Thừa Thiên Huế",
        districts: [
            { name: "TP. Huế", wards: ["An Cựu", "An Đông", "An Hòa", "An Tây", "Hương An", "Hương Hồ", "Hương Long", "Hương Sơ", "Kim Long", "Phú Hội", "Phú Hậu", "Phú Nhuận", "Phú Thượng", "Phước Vĩnh", "Phường Đúc", "Tây Lộc", "Thuận Hòa", "Thuận Lộc", "Thuận Thành", "Trường An", "Vĩnh Ninh", "Vỹ Dạ", "Xuân Phú"] },
            { name: "Hương Thủy", wards: ["Phú Bài", "Thủy Châu", "Thủy Dương", "Thủy Lương", "Thủy Phương"] },
            { name: "Hương Trà", wards: ["Hương Chữ", "Hương Văn", "Hương Vân", "Tứ Hạ"] },
            { name: "Phú Vang", wards: ["Thuận An", "Phú Đa", "Phú An", "Phú Diên", "Phú Dương", "Phú Hải", "Phú Mỹ", "Phú Thuận"] }
        ]
    },
    {
        name: "Khánh Hòa",
        districts: [
            { name: "Nha Trang", wards: ["Lộc Thọ", "Ngọc Hiệp", "Phước Hải", "Phước Hòa", "Phước Long", "Phước Tân", "Phước Tiến", "Phương Sài", "Phương Sơn", "Tân Lập", "Vạn Thạnh", "Vạn Thắng", "Vĩnh Hải", "Vĩnh Hòa", "Vĩnh Phước", "Vĩnh Thọ", "Vĩnh Trường", "Vĩnh Nguyên", "Xương Huân"] },
            { name: "Cam Ranh", wards: ["Ba Ngòi", "Cam Linh", "Cam Lộc", "Cam Lợi", "Cam Nghĩa", "Cam Phú", "Cam Phúc Bắc", "Cam Phúc Nam", "Cam Thuận"] },
            { name: "Ninh Hòa", wards: ["Ninh Đa", "Ninh Diêm", "Ninh Giang", "Ninh Hà", "Ninh Hiệp", "Ninh Thủy"] }
        ]
    },
    {
        name: "Lâm Đồng",
        districts: [
            { name: "Đà Lạt", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12", "Tà Nung", "Trạm Hành", "Xuân Thọ", "Xuân Trường"] },
            { name: "Bảo Lộc", wards: ["B'Lao", "Lộc Phát", "Lộc Sơn", "Lộc Tiến", "Phường 1", "Phường 2"] },
            { name: "Đức Trọng", wards: ["Liên Nghĩa", "Bình Thạnh", "Đà Loan", "Đa Quyn", "Hiệp An", "Hiệp Thạnh", "Liên Hiệp", "Ninh Gia", "Ninh Loan", "Phú Hội", "Tà Hine", "Tà Năng", "Tân Hội", "Tân Thành"] }
        ]
    },
    {
        name: "Bà Rịa - Vũng Tàu",
        districts: [
            { name: "Vũng Tàu", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12", "Nguyễn An Ninh", "Rạch Dừa", "Thắng Nhất", "Thắng Nhì", "Thắng Tam"] },
            { name: "Bà Rịa", wards: ["Kim Dinh", "Long Hương", "Long Tâm", "Long Toàn", "Phước Hưng", "Phước Hiệp", "Phước Nguyên", "Phước Trung"] },
            { name: "Phú Mỹ", wards: ["Hắc Dịch", "Mỹ Xuân", "Phú Mỹ", "Phước Hòa", "Tân Phước"] }
        ]
    },
    {
        name: "Nghệ An",
        districts: [
            { name: "TP. Vinh", wards: ["Bến Thủy", "Cửa Nam", "Đội Cung", "Đông Vĩnh", "Hà Huy Tập", "Hồng Sơn", "Hưng Bình", "Hưng Dũng", "Hưng Phúc", "Lê Lợi", "Lê Mao", "Quán Bàu", "Quang Trung", "Trung Đô", "Trường Thi"] },
            { name: "Cửa Lò", wards: ["Nghi Hải", "Nghi Hòa", "Nghi Hương", "Nghi Tân", "Nghi Thu", "Nghi Thủy", "Thu Thủy"] },
            { name: "Thái Hòa", wards: ["Hòa Hiếu", "Long Sơn", "Quang Phong", "Quang Tiến"] }
        ]
    },
    {
        name: "Thanh Hóa",
        districts: [
            { name: "TP. Thanh Hóa", wards: ["Ba Đình", "Điện Biên", "Đông Cương", "Đông Hương", "Đông Lĩnh", "Đông Sơn", "Đông Thọ", "Đông Vệ", "Hàm Rồng", "Lam Sơn", "Nam Ngạn", "Ngọc Trạo", "Phú Sơn", "Quảng Cát", "Quảng Đông", "Quảng Hưng", "Quảng Phú", "Quảng Tâm", "Quảng Thành", "Quảng Thắng", "Quảng Thịnh", "Tào Xuyên", "Tân Sơn", "Trường Thi"] },
            { name: "Sầm Sơn", wards: ["Bắc Sơn", "Quảng Châu", "Quảng Cư", "Quảng Thọ", "Quảng Tiến", "Quảng Vinh", "Trung Sơn", "Trường Sơn"] },
            { name: "Bỉm Sơn", wards: ["Ba Đình", "Bắc Sơn", "Đông Sơn", "Lam Sơn", "Ngọc Trạo", "Phú Sơn"] }
        ]
    },
    {
        name: "Nam Định",
        districts: [
            { name: "TP. Nam Định", wards: ["Bà Triệu", "Cửa Bắc", "Cửa Nam", "Hạ Long", "Lộc Hạ", "Lộc Hòa", "Lộc Vượng", "Năng Tĩnh", "Ngô Quyền", "Nguyễn Du", "Phan Đình Phùng", "Quang Trung", "Thống Nhất", "Trần Đăng Ninh", "Trần Hưng Đạo", "Trần Quang Khải", "Trần Tế Xương", "Trường Thi", "Văn Miếu", "Vị Hoàng", "Vị Xuyên"] },
            { name: "Giao Thủy", wards: ["Ngô Đồng", "Quất Lâm", "Giao An", "Giao Châu", "Giao Hà", "Giao Hải", "Giao Hương", "Giao Lạc", "Giao Long", "Giao Nhân", "Giao Phong", "Giao Tân", "Giao Thanh", "Giao Thiện", "Giao Thịnh", "Giao Tiến", "Giao Xuân", "Giao Yến"] },
            { name: "Hải Hậu", wards: ["Yên Định", "Cồn", "Thịnh Long", "Hải An", "Hải Anh", "Hải Bắc", "Hải Châu", "Hải Cường", "Hải Đông", "Hải Đường", "Hải Giang", "Hải Hà", "Hải Hòa", "Hải Hưng", "Hải Lộc", "Hải Minh", "Hải Nam", "Hải Ninh", "Hải Phong", "Hải Phú", "Hải Phúc", "Hải Phương", "Hải Quang", "Hải Sơn", "Hải Tân", "Hải Tây", "Hải Thanh", "Hải Toàn", "Hải Triều", "Hải Trung", "Hải Vân", "Hải Xuân"] }
        ]
    },
    {
        name: "Hải Dương",
        districts: [
            { name: "TP. Hải Dương", wards: ["Ái Quốc", "Bình Hàn", "Cẩm Thượng", "Hải Tân", "Lê Thanh Nghị", "Nam Đồng", "Ngọc Châu", "Nguyễn Trãi", "Nhị Châu", "Phạm Ngũ Lão", "Quang Trung", "Tân Bình", "Tân Hưng", "Thạch Khôi", "Thanh Bình", "Trần Hưng Đạo", "Trần Phú", "Tứ Minh", "Việt Hòa"] },
            { name: "Chí Linh", wards: ["An Lạc", "Bến Tắm", "Cổ Thành", "Cộng Hòa", "Đồng Lạc", "Hoàng Tân", "Hoàng Tiến", "Phả Lại", "Sao Đỏ", "Tân Dân", "Thái Học", "Văn An", "Văn Đức"] },
            { name: "Kinh Môn", wards: ["An Lưu", "An Phụ", "An Sinh", "Duy Tân", "Hiến Thành", "Hiệp An", "Hiệp Sơn", "Long Xuyên", "Minh Tân", "Phú Thứ", "Phạm Thái", "Tân Dân", "Thất Hùng", "Thái Thịnh"] }
        ]
    },
    {
        name: "Thái Bình",
        districts: [
            { name: "TP. Thái Bình", wards: ["Bồ Xuyên", "Đề Thám", "Hoàng Diệu", "Kỳ Bá", "Lê Hồng Phong", "Phú Khánh", "Quang Trung", "Tiền Phong", "Trần Hưng Đạo", "Trần Lãm"] },
            { name: "Vũ Thư", wards: ["Vũ Thư", "Bách Thuận", "Đồng Thanh", "Dũng Nghĩa", "Duy Nhất", "Hiệp Hòa", "Hòa Bình", "Hồng Phong", "Minh Khai", "Minh Quang", "Nguyên Xá", "Phúc Thành", "Song An", "Tam Quang", "Tân Hòa", "Tân Lập", "Tân Phong", "Tự Tân", "Việt Hùng", "Việt Thuận", "Vũ Đoài", "Vũ Hội", "Vũ Tiến", "Vũ Vân", "Vũ Vinh", "Xuân Hòa"] },
            { name: "Đông Hưng", wards: ["Đông Hưng", "Đông Á", "Đông Các", "Đông Cường", "Đông Động", "Đông Giang", "Đông Hà", "Đông Hợp", "Đông Huy", "Đông Kinh", "Đông La", "Đông Lĩnh", "Đông Phong", "Đông Phương", "Đông Quan", "Đông Quang", "Đông Sơn", "Đông Tân", "Đông Vinh", "Đông Xuân"] }
        ]
    },
    {
        name: "Vĩnh Phúc",
        districts: [
            { name: "Vĩnh Yên", wards: ["Định Trung", "Đồng Tâm", "Hội Hợp", "Khai Quang", "Liên Bảo", "Ngô Quyền", "Tích Sơn", "Thanh Trù"] },
            { name: "Phúc Yên", wards: ["Đồng Xuân", "Hùng Vương", "Nam Viêm", "Phúc Thắng", "Tiền Châu", "Trưng Nhị", "Trưng Trắc", "Xuân Hòa"] },
            { name: "Bình Xuyên", wards: ["Hương Canh", "Bá Hiến", "Đạo Đức", "Gia Khánh", "Thanh Lãng"] }
        ]
    },
    {
        name: "Thái Nguyên",
        districts: [
            { name: "TP. Thái Nguyên", wards: ["Cam Giá", "Chùa Hang", "Đồng Bẩm", "Đồng Quang", "Gia Sàng", "Hoàng Văn Thụ", "Hương Sơn", "Phan Đình Phùng", "Phú Xá", "Quan Triều", "Quang Trung", "Quang Vinh", "Tân Lập", "Tân Long", "Tân Thành", "Tân Thịnh", "Thắng Lợi", "Thị Thịnh", "Tích Lương", "Trung Thành", "Túc Duyên"] },
            { name: "Sông Công", wards: ["Bách Quang", "Cải Đan", "Châu Sơn", "Lương Châu", "Lương Sơn", "Mỏ Chè", "Phố Cò", "Thắng Lợi"] },
            { name: "Phổ Yên", wards: ["Ba Hàng", "Bắc Sơn", "Đắc Sơn", "Đông Cao", "Đồng Tiến", "Hồng Tiến", "Nam Tiến", "Tân Hương", "Tân Phú", "Thuận Thành", "Tiên Phong", "Trung Thành"] }
        ]
    },
    {
        name: "Tiền Giang",
        districts: [
            { name: "Mỹ Tho", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Tân Long"] },
            { name: "Gò Công", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Long Chánh", "Long Hưng", "Long Thuận"] }
        ]
    },
    {
        name: "Kiên Giang",
        districts: [
            { name: "Rạch Giá", wards: ["An Bình", "An Hòa", "Rạch Sỏi", "Vĩnh Bảo", "Vĩnh Hiệp", "Vĩnh Lạc", "Vĩnh Lợi", "Vĩnh Quang", "Vĩnh Thanh", "Vĩnh Thanh Vân", "Vĩnh Thông"] },
            { name: "Hà Tiên", wards: ["Bình San", "Đông Hồ", "Mỹ Đức", "Pháo Đài", "Tô Châu"] },
            { name: "Phú Quốc", wards: ["Dương Đông", "An Thới", "Bãi Thơm", "Cửa Cạn", "Cửa Dương", "Dương Tơ", "Gành Dầu", "Hàm Ninh", "Thổ Châu"] }
        ]
    },
    {
        name: "An Giang",
        districts: [
            { name: "Long Xuyên", wards: ["Bình Đức", "Bình Khánh", "Đông Xuyên", "Mỹ Bình", "Mỹ Hòa", "Mỹ Long", "Mỹ Phước", "Mỹ Quý", "Mỹ Thạnh", "Mỹ Thới"] },
            { name: "Châu Đốc", wards: ["Châu Phú A", "Châu Phú B", "Núi Sam", "Vĩnh Mỹ", "Vĩnh Nguơn"] }
        ]
    },
    {
        name: "Bắc Giang",
        districts: [
            { name: "TP. Bắc Giang", wards: ["Dĩnh Kế", "Đa Mai", "Hoàng Văn Thụ", "Lê Lợi", "Mỹ Độ", "Ngô Quyền", "Song Mai", "Thọ Xương", "Trần Nguyên Hãn", "Trần Phú", "Xương Giang"] },
            { name: "Việt Yên", wards: ["Bích Động", "Nếnh", "Hồng Thái", "Ninh Sơn", "Quang Châu", "Tăng Tiến", "Tự Lạn", "Vân Trung"] }
        ]
    },
    {
        name: "Hưng Yên",
        districts: [
            { name: "TP. Hưng Yên", wards: ["An Tảo", "Hiến Nam", "Hồng Châu", "Lam Sơn", "Lê Lợi", "Minh Khai", "Quang Trung"] },
            { name: "Mỹ Hào", wards: ["Bần Yên Nhân", "Bạch Sam", "Dị Sử", "Minh Đức", "Nhân Hòa", "Phan Đình Phùng", "Phùng Chí Kiên"] },
            { name: "Văn Giang", wards: ["Văn Giang", "Cửu Cao", "Liên Nghĩa", "Long Hưng", "Mễ Sở", "Nghĩa Trụ", "Phụng Công", "Thắng Lợi", "Văn Phúc", "Xuân Quan", "Ecopark"] }
        ]
    },
    {
        name: "Hà Nam",
        districts: [
            { name: "Phủ Lý", wards: ["Châu Sơn", "Hai Bà Trưng", "Lam Hạ", "Lê Hồng Phong", "Lương Khánh Thiện", "Minh Khai", "Quang Trung", "Thanh Châu", "Thanh Tuyền", "Trần Hưng Đạo"] },
            { name: "Duy Tiên", wards: ["Bạch Thượng", "Châu Giang", "Đồng Văn", "Duy Hải", "Duy Minh", "Hòa Mạc", "Hoàng Đông", "Tiên Nội", "Yên Bắc"] }
        ]
    },
    {
        name: "Ninh Bình",
        districts: [
            { name: "TP. Ninh Bình", wards: ["Bích Đào", "Đông Thành", "Nam Bình", "Nam Thành", "Ninh Khánh", "Ninh Phong", "Ninh Phúc", "Ninh Sơn", "Ninh Tiến", "Phúc Thành", "Tân Thành", "Thanh Bình", "Vân Giang"] },
            { name: "Tam Điệp", wards: ["Bắc Sơn", "Nam Sơn", "Tân Bình", "Tây Sơn", "Trung Sơn", "Yên Bình"] },
            { name: "Hoa Lư", wards: ["Thiên Tôn", "Ninh An", "Ninh Giang", "Ninh Hải", "Ninh Hòa", "Ninh Khang", "Ninh Mỹ", "Ninh Thắng", "Ninh Vân", "Ninh Xuân", "Ninh Vân"] }
        ]
    },
    {
        name: "Hà Tĩnh",
        districts: [
            { name: "TP. Hà Tĩnh", wards: ["Bắc Hà", "Đại Nài", "Hà Huy Tập", "Nam Hà", "Nguyễn Du", "Tân Giang", "Thạch Linh", "Thạch Quý", "Trần Phú", "Văn Yên"] },
            { name: "Hồng Lĩnh", wards: ["Bắc Hồng", "Đậu Liêu", "Đức Thuận", "Nam Hồng", "Trung Lương"] },
            { name: "Kỳ Anh", wards: ["Hưng Trí", "Kỳ Long", "Kỳ Liên", "Kỳ Phương", "Kỳ Thịnh", "Kỳ Trinh"] }
        ]
    },
    {
        name: "Quảng Bình",
        districts: [
            { name: "Đồng Hới", wards: ["Bắc Lý", "Bắc Nghĩa", "Đồng Hải", "Đồng Phú", "Đồng Sơn", "Đức Ninh Đông", "Hải Đình", "Hải Thành", "Nam Lý", "Phú Hải"] },
            { name: "Ba Đồn", wards: ["Ba Đồn", "Quảng Long", "Quảng Phong", "Quảng Phúc", "Quảng Thọ", "Quảng Thuận"] }
        ]
    },
    {
        name: "Quảng Trị",
        districts: [
            { name: "Đông Hà", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Đông Giang", "Đông Lễ", "Đông Lương", "Đông Thanh"] },
            { name: "Quảng Trị", wards: ["Phường 1", "Phường 2", "Phường 3", "An Đôn"] }
        ]
    },
    {
        name: "Quảng Nam",
        districts: [
            { name: "Tam Kỳ", wards: ["An Mỹ", "An Phú", "An Sơn", "An Xuân", "Hòa Hương", "Hòa Thuận", "Phước Hòa", "Tân Thạnh", "Trường Xuân"] },
            { name: "Hội An", wards: ["Cẩm An", "Cẩm Châu", "Cẩm Nam", "Cẩm Phô", "Cửa Đại", "Minh An", "Sơn Phong", "Tân An", "Thanh Hà"] },
            { name: "Điện Bàn", wards: ["Điện An", "Điện Dương", "Điện Nam Bắc", "Điện Nam Đông", "Điện Nam Trung", "Điện Ngọc", "Vĩnh Điện"] }
        ]
    },
    {
        name: "Quảng Ngãi",
        districts: [
            { name: "TP. Quảng Ngãi", wards: ["Chánh Lộ", "Lê Hồng Phong", "Nghĩa Chánh", "Nghĩa Dũng", "Nghĩa Lộ", "Quảng Phú", "Trần Hưng Đạo", "Trần Phú", "Trương Quang Trọng"] }
        ]
    },
    {
        name: "Bình Định",
        districts: [
            { name: "Quy Nhơn", wards: ["Bùi Thị Xuân", "Đống Đa", "Ghềnh Ráng", "Hải Cảng", "Lê Hồng Phong", "Lê Lợi", "Ngô Mây", "Nguyễn Văn Cừ", "Nhơn Bình", "Nhơn Phú", "Quang Trung", "Thị Nại", "Trần Hưng Đạo", "Trần Phú", "Trần Quang Diệu"] },
            { name: "An Nhơn", wards: ["Bình Định", "Đập Đá", "Nhơn Hòa", "Nhơn Hưng", "Nhơn Thành"] },
            { name: "Hoài Nhơn", wards: ["Bồng Sơn", "Hoài Đức", "Hoài Hảo", "Hoài Hương", "Hoài Tân", "Hoài Thanh", "Hoài Thanh Tây", "Hoài Xuân", "Tam Quan", "Tam Quan Bắc", "Tam Quan Nam"] }
        ]
    },
    {
        name: "Phú Yên",
        districts: [
            { name: "Tuy Hòa", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Phú Đông", "Phú Lâm", "Phú Thạnh"] },
            { name: "Sông Cầu", wards: ["Xuân Đài", "Xuân Phú", "Xuân Thành", "Xuân Yên"] },
            { name: "Đông Hòa", wards: ["Hòa Hiệp Bắc", "Hòa Hiệp Nam", "Hòa Hiệp Trung", "Hòa Vinh", "Hòa Xuân Tây"] }
        ]
    },
    {
        name: "Ninh Thuận",
        districts: [
            { name: "Phan Rang - Tháp Chàm", wards: ["Bảo An", "Đài Sơn", "Đạo Long", "Đô Vinh", "Đông Hải", "Kinh Dinh", "Mỹ Bình", "Mỹ Đông", "Mỹ Hải", "Mỹ Hương", "Phủ Hà", "Phước Mỹ", "Tấn Tài", "Thanh Sơn", "Văn Hải"] }
        ]
    },
    {
        name: "Bình Thuận",
        districts: [
            { name: "Phan Thiết", wards: ["Bình Hưng", "Đức Long", "Đức Nghĩa", "Đức Thắng", "Hàm Tiến", "Hưng Long", "Lạc Đạo", "Mũi Né", "Phú Hài", "Phú Tài", "Phú Thủy", "Phú Trinh", "Thanh Hải", "Xuân An"] },
            { name: "La Gi", wards: ["Bình Tân", "Phước Hội", "Phước Lộc", "Tân An", "Tân Thiện"] }
        ]
    },
    {
        name: "Kon Tum",
        districts: [
            { name: "TP. Kon Tum", wards: ["Duy Tân", "Lê Lợi", "Ngô Mây", "Nguyễn Trãi", "Quang Trung", "Quyết Thắng", "Thắng Lợi", "Thống Nhất", "Trần Hưng Đạo", "Trường Chinh"] }
        ]
    },
    {
        name: "Gia Lai",
        districts: [
            { name: "Pleiku", wards: ["Chi Lăng", "Diên Hồng", "Đống Đa", "Hoa Lư", "Hội Phú", "Hội Thương", "Ia Kring", "Phù Đổng", "Tây Sơn", "Thắng Lợi", "Thống Nhất", "Trà Bá", "Yên Đỗ", "Yên Thế"] },
            { name: "An Khê", wards: ["An Bình", "An Phú", "An Phước", "An Tân", "Tây Sơn"] },
            { name: "Ayun Pa", wards: ["Cheo Reo", "Đoàn Kết", "Hòa Bình", "Sông Bờ"] }
        ]
    },
    {
        name: "Đắk Lắk",
        districts: [
            { name: "Buôn Ma Thuột", wards: ["Ea Tam", "Khánh Xuân", "Tân An", "Tân Hòa", "Tân Lập", "Tân Lợi", "Tân Thành", "Tân Tiến", "Thắng Lợi", "Thành Công", "Thành Nhất", "Thống Nhất", "Tự An"] },
            { name: "Buôn Hồ", wards: ["An Bình", "An Lạc", "Đoàn Kết", "Đạt Hiếu", "Thiện An", "Thống Nhất", "Bình Tân"] }
        ]
    },
    {
        name: "Đắk Nông",
        districts: [
            { name: "Gia Nghĩa", wards: ["Nghĩa Đức", "Nghĩa Phú", "Nghĩa Tân", "Nghĩa Thành", "Nghĩa Trung", "Quảng Thành"] }
        ]
    },
    {
        name: "Tây Ninh",
        districts: [
            { name: "TP. Tây Ninh", wards: ["Hiệp Ninh", "Ninh Sơn", "Ninh Thạnh", "Phường 1", "Phường 2", "Phường 3", "Phường 4"] },
            { name: "Trảng Bàng", wards: ["An Hòa", "An Tịnh", "Gia Bình", "Gia Lộc", "Trảng Bàng"] },
            { name: "Hòa Thành", wards: ["Hiệp Tân", "Long Hoa", "Long Thành Bắc", "Long Thành Trung"] }
        ]
    },
    {
        name: "Bình Phước",
        districts: [
            { name: "Đồng Xoài", wards: ["Tân Bình", "Tân Đồng", "Tân Phú", "Tân Thiện", "Tân Xuân", "Tiến Thành"] },
            { name: "Bình Long", wards: ["An Lộc", "Hưng Chiến", "Phú Đức", "Phú Thịnh"] },
            { name: "Chơn Thành", wards: ["Chơn Thành", "Hưng Long", "Minh Hưng", "Minh Long", "Minh Thành", "Thành Tâm"] }
        ]
    },
    {
        name: "Long An",
        districts: [
            { name: "Tân An", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 6", "Phường 7", "Khánh Hậu", "Tân Khánh"] },
            { name: "Kiến Tường", wards: ["Phường 1", "Phường 2", "Phường 3"] },
            { name: "Bến Lức", wards: ["Bến Lức", "An Thạnh", "Bình Đức", "Long Hiệp", "Lương Bình", "Lương Hòa", "Mỹ Yên", "Nhựt Chánh", "Thạnh Đức", "Thạnh Hòa", "Thạnh Lợi", "Thạnh Phú"] }
        ]
    },
    {
        name: "Bến Tre",
        districts: [
            { name: "TP. Bến Tre", wards: ["An Hội", "Phú Khương", "Phú Tân", "Phường 4", "Phường 5", "Phường 6", "Phường 7", "Phường 8"] }
        ]
    },
    {
        name: "Trà Vinh",
        districts: [
            { name: "TP. Trà Vinh", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 6", "Phường 7", "Phường 8", "Phường 9"] },
            { name: "Duyên Hải", wards: ["Phường 1", "Phường 2"] }
        ]
    },
    {
        name: "Vĩnh Long",
        districts: [
            { name: "TP. Vĩnh Long", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 8", "Phường 9", "Tân Hòa", "Tân Hội", "Tân Ngãi", "Trường An"] },
            { name: "Bình Minh", wards: ["Cái Vồn", "Đông Thuận", "Thành Phước"] }
        ]
    },
    {
        name: "Đồng Tháp",
        districts: [
            { name: "Cao Lãnh", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 6", "Phường 11", "Hòa Thuận", "Mỹ Phú"] },
            { name: "Sa Đéc", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 4", "An Hòa", "Tân Quy Đông"] },
            { name: "Hồng Ngự", wards: ["An Bình A", "An Bình B", "An Lạc", "An Lộc", "An Thạnh"] }
        ]
    },
    {
        name: "Hậu Giang",
        districts: [
            { name: "Vị Thanh", wards: ["Phường 1", "Phường 3", "Phường 4", "Phường 5", "Phường 7"] },
            { name: "Ngã Bảy", wards: ["Hiệp Lợi", "Hiệp Thành", "Lái Hiếu", "Ngã Bảy"] }
        ]
    },
    {
        name: "Sóc Trăng",
        districts: [
            { name: "TP. Sóc Trăng", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Phường 10"] },
            { name: "Vĩnh Châu", wards: ["Khánh Hòa", "Phường 1", "Phường 2", "Vĩnh Phước"] },
            { name: "Ngã Năm", wards: ["Phường 1", "Phường 2", "Phường 3"] }
        ]
    },
    {
        name: "Bạc Liêu",
        districts: [
            { name: "TP. Bạc Liêu", wards: ["Phường 1", "Phường 2", "Phường 3", "Phường 5", "Phường 7", "Phường 8", "Nhà Mát"] },
            { name: "Giá Rai", wards: ["Hộ Phòng", "Phường 1", "Láng Tròn"] }
        ]
    },
    {
        name: "Cà Mau",
        districts: [
            { name: "TP. Cà Mau", wards: ["Phường 1", "Phường 2", "Phường 4", "Phường 5", "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Tân Thành", "Tân Xuyên"] }
        ]
    },
    {
        name: "Tuyên Quang",
        districts: [
            { name: "TP. Tuyên Quang", wards: ["An Tường", "Đội Cấn", "Hưng Thành", "Minh Xuân", "Mỹ Lâm", "Nông Tiến", "Phan Thiết", "Tân Hà", "Tân Quang", "Ỷ La"] }
        ]
    },
    {
        name: "Hà Giang",
        districts: [
            { name: "TP. Hà Giang", wards: ["Minh Khai", "Ngọc Hà", "Nguyễn Trãi", "Quang Trung", "Trần Phú"] }
        ]
    },
    {
        name: "Cao Bằng",
        districts: [
            { name: "TP. Cao Bằng", wards: ["Đề Thám", "Duyệt Trung", "Hòa Chung", "Hợp Giang", "Ngọc Xuân", "Sông Bằng", "Sông Hiến", "Tân Giang"] }
        ]
    },
    {
        name: "Bắc Kạn",
        districts: [
            { name: "TP. Bắc Kạn", wards: ["Đức Xuân", "Huyền Tụng", "Nguyễn Thị Minh Khai", "Phùng Chí Kiên", "Sông Cầu", "Xuất Hóa"] }
        ]
    },
    {
        name: "Lạng Sơn",
        districts: [
            { name: "TP. Lạng Sơn", wards: ["Chi Lăng", "Đông Kinh", "Hoàng Văn Thụ", "Tam Thanh", "Vĩnh Trại"] }
        ]
    },
    {
        name: "Lào Cai",
        districts: [
            { name: "TP. Lào Cai", wards: ["Bắc Cường", "Bắc Lệnh", "Bình Minh", "Cốc Lếu", "Duyên Hải", "Kim Tân", "Lào Cai", "Nam Cường", "Pom Hán", "Xuân Tăng"] },
            { name: "Sa Pa", wards: ["Cầu Mây", "Hàm Rồng", "Phan Si Păng", "Sa Pa", "Sa Pả"] }
        ]
    },
    {
        name: "Yên Bái",
        districts: [
            { name: "TP. Yên Bái", wards: ["Đồng Tâm", "Hồng Hà", "Hợp Minh", "Minh Tân", "Nam Cường", "Nguyễn Phúc", "Nguyễn Thái Học", "Yên Ninh", "Yên Thịnh"] },
            { name: "Nghĩa Lộ", wards: ["Cầu Thia", "Pú Trạng", "Tân An", "Trung Tâm"] }
        ]
    },
    {
        name: "Điện Biên",
        districts: [
            { name: "TP. Điện Biên Phủ", wards: ["Him Lam", "Mường Thanh", "Nam Thanh", "Noong Bua", "Tân Thanh", "Thanh Bình", "Thanh Trường"] },
            { name: "Mường Lay", wards: ["Na Lay", "Sông Đà"] }
        ]
    },
    {
        name: "Lai Châu",
        districts: [
            { name: "TP. Lai Châu", wards: ["Đoàn Kết", "Đông Phong", "Quyết Thắng", "Quyết Tiến", "Tân Phong"] }
        ]
    },
    {
        name: "Sơn La",
        districts: [
            { name: "TP. Sơn La", wards: ["Chiềng An", "Chiềng Cơi", "Chiềng Lề", "Chiềng Sinh", "Quyết Tâm", "Quyết Thắng", "Tô Hiệu"] }
        ]
    },
    {
        name: "Hòa Bình",
        districts: [
            { name: "TP. Hòa Bình", wards: ["Chăm Mát", "Dân Chủ", "Đồng Tiến", "Hữu Nghị", "Kỳ Sơn", "Phương Lâm", "Quỳnh Lâm", "Tân Hòa", "Tân Thịnh", "Thái Bình", "Thống Nhất", "Thịnh Minh", "Trung Minh"] }
        ]
    },
    {
        name: "Phú Thọ",
        districts: [
            { name: "Việt Trì", wards: ["Bạch Hạc", "Bến Gót", "Dữu Lâu", "Gia Cẩm", "Minh Nông", "Minh Phương", "Nông Trang", "Tân Dân", "Thanh Miếu", "Tiên Cát", "Vân Cơ", "Vân Phú"] },
            { name: "Phú Thọ", wards: ["Âu Cơ", "Hùng Vương", "Phong Châu", "Thanh Vinh"] }
        ]
    }
];

/**
 * Hàm khởi tạo logic Dropdown Tỉnh/Thành -> Quận/Huyện -> Phường/Xã cho 1 cụm dropdown.
 * @param {string} idTinh ID của thẻ <select> Tỉnh/Thành
 * @param {string} idHuyen ID của thẻ <select> Quận/Huyện
 * @param {string} idXa ID của thẻ <select> Phường/Xã
 */
function khoiTaoDropdownDiaChi(idTinh, idHuyen, idXa) {
    const elTinh = document.getElementById(idTinh);
    const elHuyen = document.getElementById(idHuyen);
    const elXa = document.getElementById(idXa);

    if (!elTinh || !elHuyen || !elXa) return;

    // 1. Nạp danh sách Tỉnh/Thành phố
    elTinh.innerHTML = '<option value="">--- Chọn Tỉnh / Thành phố ---</option>';
    DULIEU_HANH_CHINH_VN.forEach(t => {
        const opt = document.createElement('option');
        opt.value = t.name;
        opt.textContent = t.name;
        elTinh.appendChild(opt);
    });

    // 2. Khi thay đổi Tỉnh/Thành -> Nạp Quận/Huyện
    elTinh.addEventListener('change', function() {
        const tenTinh = this.value;
        napQuanHuyen(elTinh, elHuyen, elXa, tenTinh, null, null);
    });

    // 3. Khi thay đổi Quận/Huyện -> Nạp Phường/Xã
    elHuyen.addEventListener('change', function() {
        const tenTinh = elTinh.value;
        const tenHuyen = this.value;
        napPhuongXa(elXa, tenTinh, tenHuyen, null);
    });
}

function napQuanHuyen(elTinh, elHuyen, elXa, tenTinh, huyenMacDinh, xaMacDinh) {
    elHuyen.innerHTML = '<option value="">--- Chọn Quận / Huyện ---</option>';
    elXa.innerHTML = '<option value="">--- Chọn Phường / Xã ---</option>';

    if (!tenTinh) return;

    const tinh = DULIEU_HANH_CHINH_VN.find(t => t.name.toLowerCase() === tenTinh.toLowerCase());
    if (tinh && tinh.districts) {
        tinh.districts.forEach(d => {
            const opt = document.createElement('option');
            opt.value = d.name;
            opt.textContent = d.name;
            if (huyenMacDinh && d.name.toLowerCase() === huyenMacDinh.toLowerCase()) {
                opt.selected = true;
            }
            elHuyen.appendChild(opt);
        });
    }

    // Nếu giá trị cũ không nằm trong danh sách định sẵn, tự động bổ sung
    if (huyenMacDinh && !Array.from(elHuyen.options).some(o => o.value.toLowerCase() === huyenMacDinh.toLowerCase())) {
        const opt = document.createElement('option');
        opt.value = huyenMacDinh;
        opt.textContent = huyenMacDinh;
        opt.selected = true;
        elHuyen.appendChild(opt);
    }

    if (huyenMacDinh) {
        napPhuongXa(elXa, tenTinh, huyenMacDinh, xaMacDinh);
    }
}

function napPhuongXa(elXa, tenTinh, tenHuyen, xaMacDinh) {
    elXa.innerHTML = '<option value="">--- Chọn Phường / Xã ---</option>';

    if (!tenTinh || !tenHuyen) return;

    const tinh = DULIEU_HANH_CHINH_VN.find(t => t.name.toLowerCase() === tenTinh.toLowerCase());
    if (tinh && tinh.districts) {
        const district = tinh.districts.find(d => d.name.toLowerCase() === tenHuyen.toLowerCase());
        if (district && district.wards) {
            district.wards.forEach(w => {
                const opt = document.createElement('option');
                opt.value = w;
                opt.textContent = w;
                if (xaMacDinh && w.toLowerCase() === xaMacDinh.toLowerCase()) {
                    opt.selected = true;
                }
                elXa.appendChild(opt);
            });
        }
    }

    // Nếu giá trị cũ không nằm trong danh sách định sẵn, tự động bổ sung
    if (xaMacDinh && !Array.from(elXa.options).some(o => o.value.toLowerCase() === xaMacDinh.toLowerCase())) {
        const opt = document.createElement('option');
        opt.value = xaMacDinh;
        opt.textContent = xaMacDinh;
        opt.selected = true;
        elXa.appendChild(opt);
    }
}
