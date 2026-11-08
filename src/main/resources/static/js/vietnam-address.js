/**
 * Bộ dữ liệu Địa giới hành chính Việt Nam (Tỉnh/Thành phố -> Quận/Huyện -> Phường/Xã) đầy đủ 63 Tỉnh/Thành phố.
 * Tích hợp tự động bổ sung và tùy chọn tự nhập nếu địa bàn không nằm trong danh mục.
 */
const DULIEU_HANH_CHINH_VN = [
  {
    "name": "Hà Nội",
    "districts": [
      {
        "name": "Ba Đình",
        "wards": [
          "Cống Vị",
          "Điện Biên",
          "Đội Cấn",
          "Giảng Võ",
          "Kim Mã",
          "Liễu Giai",
          "Ngọc Hà",
          "Ngọc Khánh",
          "Nguyễn Trung Trực",
          "Phúc Xá",
          "Quán Thánh",
          "Thành Công",
          "Trúc Bạch",
          "Vĩnh Phúc"
        ]
      },
      {
        "name": "Cầu Giấy",
        "wards": [
          "Dịch Vọng",
          "Dịch Vọng Hậu",
          "Mai Dịch",
          "Nghĩa Đô",
          "Nghĩa Tân",
          "Quan Hoa",
          "Trung Hòa",
          "Yên Hòa"
        ]
      },
      {
        "name": "Đống Đa",
        "wards": [
          "Cát Linh",
          "Hàng Bột",
          "Khâm Thiên",
          "Khương Thượng",
          "Kim Liên",
          "Láng Hạ",
          "Láng Thượng",
          "Nam Đồng",
          "Ngã Tư Sở",
          "Ô Chợ Dừa",
          "Phương Liên",
          "Phương Mai",
          "Quang Trung",
          "Quốc Tử Giám",
          "Thịnh Quang",
          "Thổ Quan",
          "Trung Liệt",
          "Trung Phụng",
          "Trung Tự",
          "Văn Chương",
          "Văn Miếu"
        ]
      },
      {
        "name": "Hai Bà Trưng",
        "wards": [
          "Bách Khoa",
          "Bạch Đằng",
          "Bạch Mai",
          "Cầu Dền",
          "Đồng Nhân",
          "Đồng Tâm",
          "Lê Đại Hành",
          "Minh Khai",
          "Nguyễn Du",
          "Phạm Đình Hổ",
          "Phố Huế",
          "Quỳnh Lôi",
          "Quỳnh Mai",
          "Thanh Lương",
          "Thanh Nhàn",
          "Trương Định",
          "Vĩnh Tuy"
        ]
      },
      {
        "name": "Hoàn Kiếm",
        "wards": [
          "Chương Dương",
          "Cửa Đông",
          "Cửa Nam",
          "Đồng Xuân",
          "Hàng Bạc",
          "Hàng Bài",
          "Hàng Bồ",
          "Hàng Bông",
          "Hàng Buồm",
          "Hàng Đào",
          "Hàng Gai",
          "Hàng Mã",
          "Hàng Trống",
          "Lý Thái Tổ",
          "Phan Chu Trinh",
          "Phúc Tân",
          "Trần Hưng Đạo",
          "Tràng Tiền"
        ]
      },
      {
        "name": "Thanh Xuân",
        "wards": [
          "Hạ Đình",
          "Khương Đình",
          "Khương Mai",
          "Khương Trung",
          "Kim Giang",
          "Nhân Chính",
          "Phương Liệt",
          "Thanh Xuân Bắc",
          "Thanh Xuân Nam",
          "Thanh Xuân Trung",
          "Thượng Đình"
        ]
      },
      {
        "name": "Hoàng Mai",
        "wards": [
          "Đại Kim",
          "Định Công",
          "Giáp Bát",
          "Hoàng Liệt",
          "Hoàng Văn Thụ",
          "Lĩnh Nam",
          "Mai Động",
          "Tân Mai",
          "Thanh Trì",
          "Thịnh Liệt",
          "Trần Phú",
          "Tương Mai",
          "Vĩnh Hưng",
          "Yên Sở"
        ]
      },
      {
        "name": "Long Biên",
        "wards": [
          "Bồ Đề",
          "Cự Khối",
          "Đức Giang",
          "Gia Thụy",
          "Giang Biên",
          "Long Biên",
          "Ngọc Lâm",
          "Ngọc Thụy",
          "Phúc Đồng",
          "Phúc Lợi",
          "Sài Đồng",
          "Thạch Bàn",
          "Thượng Thanh",
          "Việt Hưng"
        ]
      },
      {
        "name": "Tây Hồ",
        "wards": [
          "Bưởi",
          "Nhật Tân",
          "Phú Thượng",
          "Quảng An",
          "Thụy Khuê",
          "Tứ Liên",
          "Xuân La",
          "Yên Phụ"
        ]
      },
      {
        "name": "Nam Từ Liêm",
        "wards": [
          "Cầu Diễn",
          "Đại Mỗ",
          "Mễ Trì",
          "Mỹ Đình 1",
          "Mỹ Đình 2",
          "Phú Đô",
          "Phương Canh",
          "Tây Mỗ",
          "Trung Văn",
          "Xuân Phương"
        ]
      },
      {
        "name": "Bắc Từ Liêm",
        "wards": [
          "Cổ Nhuế 1",
          "Cổ Nhuế 2",
          "Đông Ngạc",
          "Đức Thắng",
          "Liên Mạc",
          "Minh Khai",
          "Phú Diễn",
          "Phúc Diễn",
          "Tây Tựu",
          "Thụy Phương",
          "Thượng Cát",
          "Xuân Đỉnh",
          "Xuân Tảo"
        ]
      },
      {
        "name": "Hà Đông",
        "wards": [
          "Biên Giang",
          "Dương Nội",
          "Đồng Mai",
          "Hà Cầu",
          "Kiến Hưng",
          "La Khê",
          "Mộ Lao",
          "Nguyễn Trãi",
          "Phú La",
          "Phú Lãm",
          "Phú Lương",
          "Phúc La",
          "Quang Trung",
          "Vạn Phúc",
          "Văn Quán",
          "Yên Nghĩa",
          "Yết Kiêu"
        ]
      },
      {
        "name": "Sơn Tây",
        "wards": [
          "Lê Lợi",
          "Ngô Quyền",
          "Phú Thịnh",
          "Quang Trung",
          "Sơn Lộc",
          "Trung Hưng",
          "Trung Sơn Trầm",
          "Xuân Khanh",
          "Đường Lâm",
          "Sơn Đông",
          "Thanh Mỹ",
          "Kim Sơn",
          "Xuân Sơn",
          "Cổ Đông"
        ]
      },
      {
        "name": "Gia Lâm",
        "wards": [
          "Trâu Quỳ",
          "Yên Viên",
          "Bát Tràng",
          "Cổ Bi",
          "Đa Tốn",
          "Đặng Xá",
          "Kiêu Kỵ",
          "Ninh Hiệp",
          "Phù Đổng",
          "Dương Xá",
          "Dương Quang",
          "Lệ Chi"
        ]
      },
      {
        "name": "Đông Anh",
        "wards": [
          "Đông Anh",
          "Bắc Hồng",
          "Cổ Loa",
          "Dục Tú",
          "Hải Bối",
          "Kim Chung",
          "Mai Lâm",
          "Nam Hồng",
          "Tiên Dương",
          "Vân Hà",
          "Vân Nội",
          "Vĩnh Ngọc",
          "Xuân Canh",
          "Uy Nỗ",
          "Tàm Xá"
        ]
      },
      {
        "name": "Sóc Sơn",
        "wards": [
          "Sóc Sơn",
          "Bắc Sơn",
          "Hiền Ninh",
          "Mai Đình",
          "Minh Phú",
          "Minh Trí",
          "Nam Sơn",
          "Phù Linh",
          "Quang Tiến",
          "Tân Hưng",
          "Tiên Dược",
          "Trung Giã",
          "Kim Lũ"
        ]
      },
      {
        "name": "Thanh Trì",
        "wards": [
          "Văn Điển",
          "Đại Áng",
          "Đông Mỹ",
          "Duyên Hà",
          "Hữu Hòa",
          "Liên Ninh",
          "Ngọc Hồi",
          "Ngũ Hiệp",
          "Tả Thanh Oai",
          "Tam Hiệp",
          "Tân Triều",
          "Thanh Liệt",
          "Tứ Hiệp",
          "Vĩnh Quỳnh",
          "Yên Mỹ"
        ]
      },
      {
        "name": "Hoài Đức",
        "wards": [
          "Trạm Trôi",
          "An Khánh",
          "An Thượng",
          "Cát Quế",
          "Đắc Sở",
          "Di Trạch",
          "Đông La",
          "Đức Giang",
          "Đức Thượng",
          "Kim Chung",
          "La Phù",
          "Lại Yên",
          "Song Phương",
          "Sơn Đồng",
          "Vân Canh",
          "Vân Côn",
          "Yên Sở"
        ]
      },
      {
        "name": "Đan Phượng",
        "wards": [
          "Phùng",
          "Đan Phượng",
          "Đồng Tháp",
          "Hạ Mỗ",
          "Hồng Hà",
          "Liên Hà",
          "Liên Hồng",
          "Liên Trung",
          "Phương Đình",
          "Song Phượng",
          "Tân Hội",
          "Tân Lập",
          "Thọ An",
          "Thọ Xuân",
          "Thượng Mỗ",
          "Trung Châu"
        ]
      },
      {
        "name": "Thường Tín",
        "wards": [
          "Thường Tín",
          "Chương Dương",
          "Dũng Tiến",
          "Duyên Thái",
          "Hà Hồi",
          "Hiền Giang",
          "Hòa Bình",
          "Khánh Hà",
          "Hồng Vân",
          "Lê Lợi",
          "Liên Phương",
          "Minh Cường",
          "Nghiêm Xuyên",
          "Nguyễn Trãi",
          "Nhị Khê",
          "Ninh Sở",
          "Quất Động",
          "Tân Minh",
          "Thắng Lợi",
          "Thống Nhất",
          "Thư Phú",
          "Tiền Phong",
          "Tự Nhiên",
          "Văn Bình",
          "Vạn Điểm",
          "Văn Phú",
          "Văn Tự",
          "Vân Tảo"
        ]
      },
      {
        "name": "Phú Xuyên",
        "wards": [
          "Phú Xuyên",
          "Phú Minh",
          "Bạch Hạ",
          "Châu Can",
          "Chuyên Mỹ",
          "Đại Thắng",
          "Đại Xuyên",
          "Hoàng Long",
          "Hồng Minh",
          "Hồng Thái",
          "Khai Thái",
          "Nam Phong",
          "Nam Triều",
          "Phú Túc",
          "Phú Yên",
          "Phúc Tiến",
          "Phượng Dực",
          "Quang Lãng",
          "Quang Trung",
          "Sơn Hà",
          "Tân Dân",
          "Thụy Phú",
          "Tri Thủy",
          "Tri Trung",
          "Văn Hoàng",
          "Văn Nhân",
          "Vân Từ"
        ]
      },
      {
        "name": "Ứng Hòa",
        "wards": [
          "Vân Đình",
          "Cao Thành",
          "Đại Cường",
          "Đại Hùng",
          "Đội Bình",
          "Đông Lỗ",
          "Đồng Tân",
          "Đồng Tiến",
          "Hoa Sơn",
          "Hòa Lâm",
          "Hòa Nam",
          "Hòa Phú",
          "Hòa Xá",
          "Hồng Quang",
          "Kim Đường",
          "Liên Bạt",
          "Lưu Hoàng",
          "Minh Đức",
          "Phù Lưu",
          "Phương Tú",
          "Quảng Phú Cầu",
          "Tảo Dương Văn",
          "Trầm Lộng",
          "Trung Tú",
          "Trường Thịnh",
          "Vạn Thái",
          "Viên An",
          "Viên Nội"
        ]
      },
      {
        "name": "Mỹ Đức",
        "wards": [
          "Đại Nghĩa",
          "An Mỹ",
          "An Phú",
          "An Tiến",
          "Bột Xuyên",
          "Đại Hưng",
          "Đốc Tín",
          "Đồng Tâm",
          "Hồng Sơn",
          "Hợp Thanh",
          "Hợp Tiến",
          "Hùng Tiến",
          "Hương Sơn",
          "Lê Thanh",
          "Mỹ Thành",
          "Phù Lưu Tế",
          "Phúc Lâm",
          "Phùng Xá",
          "Thượng Lâm",
          "Tuy Lai",
          "Vạn Kim",
          "Xuy Xá"
        ]
      },
      {
        "name": "Chương Mỹ",
        "wards": [
          "Chúc Sơn",
          "Xuân Mai",
          "Đại Yên",
          "Đồng Lạc",
          "Đồng Phú",
          "Đông Phương Yên",
          "Đông Sơn",
          "Hoàng Diệu",
          "Hoàng Văn Thụ",
          "Hồng Phong",
          "Hợp Đồng",
          "Hữu Văn",
          "Lam Điền",
          "Mỹ Lương",
          "Nam Phương Tiến",
          "Ngọc Hòa",
          "Phú Nam An",
          "Phú Nghĩa",
          "Phụng Châu",
          "Quảng Bị",
          "Tân Tiến",
          "Tiên Phương",
          "Tốt Động",
          "Thanh Bình",
          "Thủy Xuân Tiên",
          "Thụy Hương",
          "Thượng Vực",
          "Trần Phú",
          "Trung Hòa",
          "Trường Yên",
          "Văn Võ"
        ]
      },
      {
        "name": "Thanh Oai",
        "wards": [
          "Kim Bài",
          "Bích Hòa",
          "Bình Minh",
          "Cao Dương",
          "Cao Viên",
          "Cự Khê",
          "Đỗ Động",
          "Dân Hòa",
          "Hồng Dương",
          "Kim An",
          "Kim Thư",
          "Liên Châu",
          "Mỹ Hưng",
          "Phương Trung",
          "Tam Hưng",
          "Tân Ước",
          "Thanh Cao",
          "Thanh Mai",
          "Thanh Thùy",
          "Thanh Văn",
          "Xuân Dương"
        ]
      },
      {
        "name": "Mê Linh",
        "wards": [
          "Chi Đông",
          "Quang Minh",
          "Chu Phan",
          "Đại Thịnh",
          "Hoàng Kim",
          "Kim Hoa",
          "Liên Mạc",
          "Mê Linh",
          "Tam Đồng",
          "Thạch Đà",
          "Thanh Lâm",
          "Tiền Phong",
          "Tiến Thắng",
          "Tiến Thịnh",
          "Tự Lập",
          "Tráng Việt",
          "Văn Khê"
        ]
      },
      {
        "name": "Quốc Oai",
        "wards": [
          "Quốc Oai",
          "Cấn Hữu",
          "Cộng Hòa",
          "Đại Thành",
          "Đồng Quang",
          "Đông Yên",
          "Hòa Thạch",
          "Liệp Tuyết",
          "Nghĩa Hương",
          "Ngọc Liệp",
          "Ngọc Mỹ",
          "Phú Cát",
          "Phú Mãn",
          "Phượng Cách",
          "Sài Sơn",
          "Tân Hòa",
          "Tân Phú",
          "Thạch Thán",
          "Tuyết Nghĩa",
          "Yên Sơn"
        ]
      },
      {
        "name": "Thạch Thất",
        "wards": [
          "Liên Quan",
          "Bình Phú",
          "Bình Yên",
          "Cẩm Yên",
          "Cần Kiệm",
          "Canh Nậu",
          "Chàng Sơn",
          "Dị Nậu",
          "Đồng Trúc",
          "Hạ Bằng",
          "Hương Ngải",
          "Khúc Kiệm",
          "Lại Thượng",
          "Phú Kim",
          "Phùng Xá",
          "Tân Xã",
          "Thạch Hòa",
          "Thạch Xá",
          "Tiến Xuân",
          "Yên Bình",
          "Yên Trung"
        ]
      },
      {
        "name": "Phúc Thọ",
        "wards": [
          "Phúc Thọ",
          "Hát Môn",
          "Hiệp Thuận",
          "Liên Hiệp",
          "Long Xuyên",
          "Ngọc Tảo",
          "Phúc Hòa",
          "Phụng Thượng",
          "Sen Phương",
          "Tam Hiệp",
          "Tam Thuấn",
          "Thanh Đa",
          "Thọ Lộc",
          "Thượng Cốc",
          "Tích Giang",
          "Trạch Mỹ Lộc",
          "Vân Hà",
          "Vân Nam",
          "Vân Phúc",
          "Võng Xuyên",
          "Xuân Đình"
        ]
      },
      {
        "name": "Ba Vì",
        "wards": [
          "Tây Đằng",
          "Ba Trại",
          "Ba Vì",
          "Cẩm Lĩnh",
          "Cam Thượng",
          "Châu Sơn",
          "Chu Minh",
          "Cổ Đô",
          "Đông Quang",
          "Đồng Thái",
          "Khánh Thượng",
          "Minh Châu",
          "Minh Quang",
          "Phong Vân",
          "Phú Châu",
          "Phú Cường",
          "Phú Đông",
          "Phú Phương",
          "Phú Sơn",
          "Sơn Đà",
          "Tản Hồng",
          "Tản Lĩnh",
          "Thái Hòa",
          "Thuần Mỹ",
          "Thụy An",
          "Tiên Phong",
          "Tòng Bạt",
          "Vân Hòa",
          "Vạn Thắng",
          "Vật Lại",
          "Yên Bài"
        ]
      }
    ]
  },
  {
    "name": "TP. Hồ Chí Minh",
    "districts": [
      {
        "name": "Quận 1",
        "wards": [
          "Bến Nghé",
          "Bến Thành",
          "Cầu Kho",
          "Cầu Ông Lãnh",
          "Cô Giang",
          "Đa Kao",
          "Nguyễn Cư Trinh",
          "Nguyễn Thái Bình",
          "Phạm Ngũ Lão",
          "Tân Định"
        ]
      },
      {
        "name": "Quận 3",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 9",
          "Phường 10",
          "Phường 11",
          "Phường 12",
          "Phường 13",
          "Phường 14",
          "Võ Thị Sáu"
        ]
      },
      {
        "name": "Quận 4",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 6",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Phường 13",
          "Phường 14",
          "Phường 15",
          "Phường 16",
          "Phường 18"
        ]
      },
      {
        "name": "Quận 5",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Phường 11",
          "Phường 12",
          "Phường 13",
          "Phường 14"
        ]
      },
      {
        "name": "Quận 6",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Phường 11",
          "Phường 12",
          "Phường 13",
          "Phường 14"
        ]
      },
      {
        "name": "Quận 7",
        "wards": [
          "Bình Thuận",
          "Phú Mỹ",
          "Phú Thuận",
          "Tân Hưng",
          "Tân Kiểng",
          "Tân Phong",
          "Tân Phú",
          "Tân Quy",
          "Tân Thuận Đông",
          "Tân Thuận Tây"
        ]
      },
      {
        "name": "Quận 8",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Phường 11",
          "Phường 12",
          "Phường 13",
          "Phường 14",
          "Phường 15",
          "Phường 16"
        ]
      },
      {
        "name": "Quận 10",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Phường 11",
          "Phường 12",
          "Phường 13",
          "Phường 14",
          "Phường 15"
        ]
      },
      {
        "name": "Quận 11",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Phường 11",
          "Phường 12",
          "Phường 13",
          "Phường 14",
          "Phường 15",
          "Phường 16"
        ]
      },
      {
        "name": "Quận 12",
        "wards": [
          "An Phú Đông",
          "Đông Hưng Thuận",
          "Hiệp Thành",
          "Tân Chánh Hiệp",
          "Tân Hưng Thuận",
          "Tân Thới Hiệp",
          "Tân Thới Nhất",
          "Thạnh Lộc",
          "Thạnh Xuân",
          "Thới An",
          "Trung Mỹ Tây"
        ]
      },
      {
        "name": "Bình Thạnh",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 11",
          "Phường 12",
          "Phường 13",
          "Phường 14",
          "Phường 15",
          "Phường 17",
          "Phường 19",
          "Phường 21",
          "Phường 22",
          "Phường 24",
          "Phường 25",
          "Phường 26",
          "Phường 27",
          "Phường 28"
        ]
      },
      {
        "name": "Gò Vấp",
        "wards": [
          "Phường 1",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Phường 11",
          "Phường 12",
          "Phường 13",
          "Phường 14",
          "Phường 15",
          "Phường 16",
          "Phường 17"
        ]
      },
      {
        "name": "Phú Nhuận",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Phường 11",
          "Phường 13",
          "Phường 15",
          "Phường 17"
        ]
      },
      {
        "name": "Tân Bình",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Phường 11",
          "Phường 12",
          "Phường 13",
          "Phường 14",
          "Phường 15"
        ]
      },
      {
        "name": "Tân Phú",
        "wards": [
          "Hiệp Tân",
          "Hòa Thạnh",
          "Phú Thạnh",
          "Phú Thọ Hòa",
          "Phú Trung",
          "Sơn Kỳ",
          "Tân Quý",
          "Tân Sơn Nhì",
          "Tân Thành",
          "Tân Thới Hòa",
          "Tây Thạnh"
        ]
      },
      {
        "name": "Bình Tân",
        "wards": [
          "An Lạc",
          "An Lạc A",
          "Bình Hưng Hòa",
          "Bình Hưng Hòa A",
          "Bình Hưng Hòa B",
          "Bình Trị Đông",
          "Bình Trị Đông A",
          "Bình Trị Đông B",
          "Tân Tạo",
          "Tân Tạo A"
        ]
      },
      {
        "name": "TP. Thủ Đức",
        "wards": [
          "An Khánh",
          "An Lợi Đông",
          "An Phú",
          "Bình Chiểu",
          "Bình Thọ",
          "Hiệp Bình Chánh",
          "Hiệp Bình Phước",
          "Hiệp Phú",
          "Linh Chiểu",
          "Linh Đông",
          "Linh Trung",
          "Linh Xuân",
          "Long Bình",
          "Long Phước",
          "Long Thạnh Mỹ",
          "Long Trường",
          "Phú Hữu",
          "Phước Bình",
          "Phước Long A",
          "Phước Long B",
          "Tam Bình",
          "Tam Phú",
          "Tăng Nhơn Phú A",
          "Tăng Nhơn Phú B",
          "Thạnh Mỹ Lợi",
          "Thảo Điền",
          "Thủ Thiêm",
          "Trường Thạnh",
          "Trường Thọ"
        ]
      },
      {
        "name": "Bình Chánh",
        "wards": [
          "Tân Túc",
          "An Phú Tây",
          "Bình Chánh",
          "Bình Hưng",
          "Bình Lợi",
          "Đa Phước",
          "Hưng Long",
          "Lê Minh Xuân",
          "Phạm Văn Hai",
          "Phong Phú",
          "Quy Đức",
          "Tân Kiên",
          "Tân Nhựt",
          "Tân Quý Tây",
          "Vĩnh Lộc A",
          "Vĩnh Lộc B"
        ]
      },
      {
        "name": "Hóc Môn",
        "wards": [
          "Hóc Môn",
          "Bà Điểm",
          "Đông Thạnh",
          "Nhị Bình",
          "Tân Hiệp",
          "Tân Thới Nhì",
          "Tân Xuân",
          "Thới Tam Thôn",
          "Trung Chánh",
          "Xuân Thới Đông",
          "Xuân Thới Sơn",
          "Xuân Thới Thượng"
        ]
      },
      {
        "name": "Củ Chi",
        "wards": [
          "Củ Chi",
          "An Nhơn Tây",
          "An Phú",
          "Bình Mỹ",
          "Hòa Phú",
          "Nhuận Đức",
          "Phạm Văn Cội",
          "Phú Hòa Đông",
          "Phú Mỹ Hưng",
          "Tân An Hội",
          "Tân Phú Trung",
          "Tân Thạnh Đông",
          "Tân Thạnh Tây",
          "Tân Thông Hội",
          "Thái Mỹ",
          "Trung An",
          "Trung Lập Hạ",
          "Trung Lập Thượng"
        ]
      },
      {
        "name": "Nhà Bè",
        "wards": [
          "Nhà Bè",
          "Hiệp Phước",
          "Long Thới",
          "Nhơn Đức",
          "Phú Xuân",
          "Phước Kiển",
          "Phước Lộc"
        ]
      },
      {
        "name": "Cần Giờ",
        "wards": [
          "Cần Thạnh",
          "An Thới Đông",
          "Bình Khánh",
          "Long Hòa",
          "Lý Nhơn",
          "Tam Thôn Hiệp",
          "Thạnh An"
        ]
      }
    ]
  },
  {
    "name": "Đà Nẵng",
    "districts": [
      {
        "name": "Hải Châu",
        "wards": [
          "Bình Hiên",
          "Bình Thuận",
          "Hải Châu 1",
          "Hải Châu 2",
          "Hòa Cường Bắc",
          "Hòa Cường Nam",
          "Hòa Thuận Đông",
          "Hòa Thuận Tây",
          "Nam Dương",
          "Phước Ninh",
          "Thạch Thang",
          "Thanh Bình",
          "Thuận Phước"
        ]
      },
      {
        "name": "Thanh Khê",
        "wards": [
          "An Khê",
          "Chính Gián",
          "Hòa Khê",
          "Tam Thuận",
          "Tân Chính",
          "Thạc Gián",
          "Thanh Khê Đông",
          "Thanh Khê Tây",
          "Vĩnh Trung",
          "Xuân Hà"
        ]
      },
      {
        "name": "Sơn Trà",
        "wards": [
          "An Hải Bắc",
          "An Hải Đông",
          "An Hải Tây",
          "Mân Thái",
          "Nại Hiên Đông",
          "Phước Mỹ",
          "Thọ Quang"
        ]
      },
      {
        "name": "Ngũ Hành Sơn",
        "wards": [
          "Hòa Hải",
          "Hòa Quý",
          "Khuê Mỹ",
          "Mỹ An"
        ]
      },
      {
        "name": "Liên Chiểu",
        "wards": [
          "Hòa Hiệp Bắc",
          "Hòa Hiệp Nam",
          "Hòa Khánh Bắc",
          "Hòa Khánh Nam",
          "Hòa Minh"
        ]
      },
      {
        "name": "Cẩm Lệ",
        "wards": [
          "Hòa An",
          "Hòa Phát",
          "Hòa Thọ Đông",
          "Hòa Thọ Tây",
          "Hòa Xuân",
          "Khuê Trung"
        ]
      },
      {
        "name": "Hòa Vang",
        "wards": [
          "Hòa Bắc",
          "Hòa Châu",
          "Hòa Khương",
          "Hòa Liên",
          "Hòa Nhơn",
          "Hòa Ninh",
          "Hòa Phong",
          "Hòa Phú",
          "Hòa Phước",
          "Hòa Sơn",
          "Hòa Tiến"
        ]
      },
      {
        "name": "Hoàng Sa",
        "wards": [
          "Đảo Hoàng Sa"
        ]
      }
    ]
  },
  {
    "name": "Hải Phòng",
    "districts": [
      {
        "name": "Hồng Bàng",
        "wards": [
          "Hoàng Văn Thụ",
          "Minh Khai",
          "Phan Bội Châu",
          "Quán Toan",
          "Sở Dầu",
          "Thượng Lý",
          "Trại Chuối"
        ]
      },
      {
        "name": "Ngô Quyền",
        "wards": [
          "Cầu Đất",
          "Cầu Tre",
          "Đằng Giang",
          "Đông Khê",
          "Đồng Quốc Bình",
          "Gia Viên",
          "Lạc Viên",
          "Lạch Tray",
          "Lê Lợi",
          "Máy Chai",
          "Máy Tơ",
          "Vạn Mỹ"
        ]
      },
      {
        "name": "Lê Chân",
        "wards": [
          "An Biên",
          "An Dương",
          "Cát Dài",
          "Đông Hải",
          "Dư Hàng",
          "Dư Hàng Kênh",
          "Hàng Kênh",
          "Hồ Nam",
          "Kênh Dương",
          "Lam Sơn",
          "Niệm Nghĩa",
          "Nghĩa Xá",
          "Trần Nguyên Hãn",
          "Vĩnh Niệm"
        ]
      },
      {
        "name": "Hải An",
        "wards": [
          "Cát Bi",
          "Đằng Hải",
          "Đằng Lâm",
          "Đông Hải 1",
          "Đông Hải 2",
          "Nam Hải",
          "Thành Tô",
          "Tràng Cát"
        ]
      },
      {
        "name": "Kiến An",
        "wards": [
          "Bắc Sơn",
          "Đồng Hòa",
          "Lãm Hà",
          "Nam Sơn",
          "Ngọc Sơn",
          "Phù Liễn",
          "Quán Trữ",
          "Trần Thành Ngọ",
          "Văn Đẩu"
        ]
      },
      {
        "name": "Đồ Sơn",
        "wards": [
          "Bàng La",
          "Hải Sơn",
          "Hợp Đức",
          "Minh Đức",
          "Ngọc Xuyên",
          "Vạn Hương"
        ]
      },
      {
        "name": "Dương Kinh",
        "wards": [
          "Anh Dũng",
          "Đa Phúc",
          "Hải Thành",
          "Hòa Nghĩa",
          "Hưng Đạo",
          "Tân Thành"
        ]
      },
      {
        "name": "Thủy Nguyên",
        "wards": [
          "Núi Đèo",
          "Minh Đức",
          "An Lư",
          "Dương Quan",
          "Hoa Động",
          "Hoàng Động",
          "Kênh Giang",
          "Lâm Động",
          "Lập Lễ",
          "Lưu Kiếm",
          "Tam Hưng",
          "Thiên Hương",
          "Thủy Đường",
          "Thủy Sơn",
          "Trung Hà"
        ]
      },
      {
        "name": "An Dương",
        "wards": [
          "An Dương",
          "An Đồng",
          "An Hòa",
          "An Hưng",
          "Bắc Sơn",
          "Đại Bản",
          "Đặng Cương",
          "Đồng Thái",
          "Hồng Phong",
          "Hồng Thái",
          "Lê Lợi",
          "Lê Thiện",
          "Nam Sơn",
          "Quốc Tuấn",
          "Tân Tiến"
        ]
      },
      {
        "name": "An Lão",
        "wards": [
          "An Lão",
          "Trường Sơn",
          "An Thái",
          "An Thắng",
          "An Thọ",
          "Chiến Thắng",
          "Mỹ Đức",
          "Quang Hưng",
          "Quang Trung",
          "Quốc Tuấn",
          "Tân Dân",
          "Tân Viên",
          "Thái Sơn",
          "Trường Thành",
          "Trường Thọ"
        ]
      },
      {
        "name": "Kiến Thụy",
        "wards": [
          "Núi Đối",
          "Đại Đồng",
          "Đại Hà",
          "Đại Hợp",
          "Đoan Xá",
          "Đông Phương",
          "Du Lễ",
          "Hữu Bằng",
          "Kiến Quốc",
          "Minh Tân",
          "Ngũ Đoan",
          "Ngũ Phúc",
          "Tân Phong",
          "Tân Trào",
          "Thanh Sơn",
          "Thuận Thiên",
          "Thụy Hương",
          "Tú Sơn"
        ]
      },
      {
        "name": "Tiên Lãng",
        "wards": [
          "Tiên Lãng",
          "Bạch Đằng",
          "Bắc Hưng",
          "Cấp Tiến",
          "Đại Thắng",
          "Đoàn Lập",
          "Đông Hưng",
          "Hùng Thắng",
          "Khởi Nghĩa",
          "Kiến Thiết",
          "Nam Hưng",
          "Quang Phục",
          "Quyết Thắng",
          "Tây Hưng",
          "Tiên Cường",
          "Tiên Minh",
          "Tiên Thanh",
          "Tiên Thắng",
          "Tự Cường",
          "Vinh Quang"
        ]
      },
      {
        "name": "Vĩnh Bảo",
        "wards": [
          "Vĩnh Bảo",
          "An Hòa",
          "Cao Minh",
          "Cổ Am",
          "Cộng Hiền",
          "Dũng Tiến",
          "Đồng Minh",
          "Giang Biên",
          "Hiệp Hòa",
          "Hòa Bình",
          "Hưng Nhân",
          "Khởi Nghĩa",
          "Liêm Am",
          "Nhân Hòa",
          "Tam Cường",
          "Tam Đa",
          "Tân Hưng",
          "Tân Liên",
          "Thắng Thủy",
          "Tiền Phong",
          "Trấn Dương",
          "Trung Lập",
          "Việt Tiến",
          "Vĩnh An",
          "Vĩnh Long",
          "Vĩnh Phong",
          "Vĩnh Quang"
        ]
      },
      {
        "name": "Cát Hải",
        "wards": [
          "Cát Bà",
          "Cát Hải",
          "Đồng Bài",
          "Gia Luận",
          "Hiền Hào",
          "Hoàng Châu",
          "Nghĩa Lộ",
          "Phù Long",
          "Trân Châu",
          "Việt Hải",
          "Xuân Đám"
        ]
      },
      {
        "name": "Bạch Long Vĩ",
        "wards": [
          "Đảo Bạch Long Vĩ"
        ]
      }
    ]
  },
  {
    "name": "Cần Thơ",
    "districts": [
      {
        "name": "Ninh Kiều",
        "wards": [
          "An Bình",
          "An Cư",
          "An Hòa",
          "An Khánh",
          "An Nghiệp",
          "Cái Khế",
          "Hưng Lợi",
          "Tân An",
          "Thới Bình",
          "Xuân Khánh"
        ]
      },
      {
        "name": "Bình Thủy",
        "wards": [
          "An Thới",
          "Bình Thủy",
          "Bùi Hữu Nghĩa",
          "Long Hòa",
          "Long Tuyền",
          "Thới An Đông",
          "Trà An",
          "Trà Nóc"
        ]
      },
      {
        "name": "Cái Răng",
        "wards": [
          "Ba Láng",
          "Hưng Phú",
          "Hưng Thạnh",
          "Lê Bình",
          "Phú Thứ",
          "Tân Phú",
          "Yên Đỗ"
        ]
      },
      {
        "name": "Ô Môn",
        "wards": [
          "Châu Văn Liêm",
          "Long Hưng",
          "Phước Thới",
          "Thới An",
          "Thới Hòa",
          "Thới Long"
        ]
      },
      {
        "name": "Thốt Nốt",
        "wards": [
          "Tân Hưng",
          "Tân Lộc",
          "Thạnh Hòa",
          "Thốt Nốt",
          "Thới Thuận",
          "Trung Kiên",
          "Trung Nhứt"
        ]
      },
      {
        "name": "Phong Điền",
        "wards": [
          "Phong Điền",
          "Giai Xuân",
          "Mỹ Khánh",
          "Nhơn Ái",
          "Nhơn Nghĩa",
          "Tân Thới",
          "Trường Long"
        ]
      },
      {
        "name": "Cờ Đỏ",
        "wards": [
          "Cờ Đỏ",
          "Đông Hiệp",
          "Đông Thắng",
          "Thạnh Phú",
          "Thới Đông",
          "Thới Hưng",
          "Thới Xuân",
          "Trung An",
          "Trung Hưng",
          "Trung Thạnh"
        ]
      },
      {
        "name": "Thới Lai",
        "wards": [
          "Thới Lai",
          "Định Môn",
          "Đông Bình",
          "Đông Thuận",
          "Tân Thạnh",
          "Thới Tân",
          "Thới Thạnh",
          "Trường Thành",
          "Trường Thắng",
          "Trường Xuân",
          "Trường Xuân A",
          "Trường Xuân B",
          "Xuân Thắng"
        ]
      },
      {
        "name": "Vĩnh Thạnh",
        "wards": [
          "Vĩnh Thạnh",
          "Thanh An",
          "Thạnh An",
          "Thạnh Lộc",
          "Thạnh Lợi",
          "Thạnh Mỹ",
          "Thạnh Quới",
          "Thạnh Thắng",
          "Thạnh Tiến",
          "Vĩnh Bình",
          "Vĩnh Trinh"
        ]
      }
    ]
  },
  {
    "name": "An Giang",
    "districts": [
      {
        "name": "Long Xuyên",
        "wards": [
          "Thị trấn Long Xuyên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Châu Đốc",
        "wards": [
          "Thị trấn Châu Đốc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "An Phú",
        "wards": [
          "Thị trấn An Phú",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tân Châu",
        "wards": [
          "Thị trấn Tân Châu",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phú Tân",
        "wards": [
          "Thị trấn Phú Tân",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Châu Phú",
        "wards": [
          "Thị trấn Châu Phú",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tịnh Biên",
        "wards": [
          "Thị trấn Tịnh Biên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tri Tôn",
        "wards": [
          "Thị trấn Tri Tôn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Châu Thành",
        "wards": [
          "Thị trấn Châu Thành",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Chợ Mới",
        "wards": [
          "Thị trấn Chợ Mới",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thoại Sơn",
        "wards": [
          "Thị trấn Thoại Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Bà Rịa - Vũng Tàu",
    "districts": [
      {
        "name": "Vũng Tàu",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Bà Rịa",
        "wards": [
          "Thị trấn Bà Rịa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phú Mỹ",
        "wards": [
          "Thị trấn Phú Mỹ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Châu Đức",
        "wards": [
          "Thị trấn Châu Đức",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Côn Đảo",
        "wards": [
          "Thị trấn Côn Đảo",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đất Đỏ",
        "wards": [
          "Thị trấn Đất Đỏ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Long Điền",
        "wards": [
          "Thị trấn Long Điền",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Xuyên Mộc",
        "wards": [
          "Thị trấn Xuyên Mộc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Bắc Giang",
    "districts": [
      {
        "name": "TP. Bắc Giang",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Việt Yên",
        "wards": [
          "Thị trấn Việt Yên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hiệp Hòa",
        "wards": [
          "Thị trấn Hiệp Hòa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lạng Giang",
        "wards": [
          "Thị trấn Lạng Giang",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lục Nam",
        "wards": [
          "Thị trấn Lục Nam",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lục Ngạn",
        "wards": [
          "Thị trấn Lục Ngạn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Sơn Động",
        "wards": [
          "Thị trấn Sơn Động",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tân Yên",
        "wards": [
          "Thị trấn Tân Yên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Yên Dũng",
        "wards": [
          "Thị trấn Yên Dũng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Yên Thế",
        "wards": [
          "Thị trấn Yên Thế",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Bắc Kạn",
    "districts": [
      {
        "name": "TP. Bắc Kạn",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Ba Bể",
        "wards": [
          "Thị trấn Ba Bể",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bạch Thông",
        "wards": [
          "Thị trấn Bạch Thông",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Chợ Đồn",
        "wards": [
          "Thị trấn Chợ Đồn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Chợ Mới",
        "wards": [
          "Thị trấn Chợ Mới",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Na Rì",
        "wards": [
          "Thị trấn Na Rì",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ngân Sơn",
        "wards": [
          "Thị trấn Ngân Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Pác Nặm",
        "wards": [
          "Thị trấn Pác Nặm",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Bạc Liêu",
    "districts": [
      {
        "name": "TP. Bạc Liêu",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Giá Rai",
        "wards": [
          "Thị trấn Giá Rai",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đông Hải",
        "wards": [
          "Thị trấn Đông Hải",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hòa Bình",
        "wards": [
          "Thị trấn Hòa Bình",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hồng Dân",
        "wards": [
          "Thị trấn Hồng Dân",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phước Long",
        "wards": [
          "Thị trấn Phước Long",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Vĩnh Lợi",
        "wards": [
          "Thị trấn Vĩnh Lợi",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Bắc Ninh",
    "districts": [
      {
        "name": "TP. Bắc Ninh",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Từ Sơn",
        "wards": [
          "Thị trấn Từ Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Gia Bình",
        "wards": [
          "Thị trấn Gia Bình",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lương Tài",
        "wards": [
          "Thị trấn Lương Tài",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Quế Võ",
        "wards": [
          "Thị trấn Quế Võ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thuận Thành",
        "wards": [
          "Thị trấn Thuận Thành",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tiên Du",
        "wards": [
          "Thị trấn Tiên Du",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Yên Phong",
        "wards": [
          "Thị trấn Yên Phong",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Bến Tre",
    "districts": [
      {
        "name": "TP. Bến Tre",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Ba Tri",
        "wards": [
          "Thị trấn Ba Tri",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bình Đại",
        "wards": [
          "Thị trấn Bình Đại",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Châu Thành",
        "wards": [
          "Thị trấn Châu Thành",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Chợ Lách",
        "wards": [
          "Thị trấn Chợ Lách",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Giồng Trôm",
        "wards": [
          "Thị trấn Giồng Trôm",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mỏ Cày Bắc",
        "wards": [
          "Thị trấn Mỏ Cày Bắc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mỏ Cày Nam",
        "wards": [
          "Thị trấn Mỏ Cày Nam",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thạnh Phú",
        "wards": [
          "Thị trấn Thạnh Phú",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Bình Định",
    "districts": [
      {
        "name": "Quy Nhơn",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "An Nhơn",
        "wards": [
          "Thị trấn An Nhơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hoài Nhơn",
        "wards": [
          "Thị trấn Hoài Nhơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "An Lão",
        "wards": [
          "Thị trấn An Lão",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hoài Ân",
        "wards": [
          "Thị trấn Hoài Ân",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phù Cát",
        "wards": [
          "Thị trấn Phù Cát",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phù Mỹ",
        "wards": [
          "Thị trấn Phù Mỹ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tuy Phước",
        "wards": [
          "Thị trấn Tuy Phước",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tây Sơn",
        "wards": [
          "Thị trấn Tây Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Vân Canh",
        "wards": [
          "Thị trấn Vân Canh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Vĩnh Thạnh",
        "wards": [
          "Thị trấn Vĩnh Thạnh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Bình Dương",
    "districts": [
      {
        "name": "Thủ Dầu Một",
        "wards": [
          "Thị trấn Thủ Dầu Một",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Dĩ An",
        "wards": [
          "Thị trấn Dĩ An",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thuận An",
        "wards": [
          "Thị trấn Thuận An",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bến Cát",
        "wards": [
          "Thị trấn Bến Cát",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tân Uyên",
        "wards": [
          "Thị trấn Tân Uyên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bàu Bàng",
        "wards": [
          "Thị trấn Bàu Bàng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bắc Tân Uyên",
        "wards": [
          "Thị trấn Bắc Tân Uyên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Dầu Tiếng",
        "wards": [
          "Thị trấn Dầu Tiếng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phú Giáo",
        "wards": [
          "Thị trấn Phú Giáo",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Bình Phước",
    "districts": [
      {
        "name": "Đồng Xoài",
        "wards": [
          "Thị trấn Đồng Xoài",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bình Long",
        "wards": [
          "Thị trấn Bình Long",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phước Long",
        "wards": [
          "Thị trấn Phước Long",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bù Đăng",
        "wards": [
          "Thị trấn Bù Đăng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bù Đốp",
        "wards": [
          "Thị trấn Bù Đốp",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bù Gia Mập",
        "wards": [
          "Thị trấn Bù Gia Mập",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Chơn Thành",
        "wards": [
          "Thị trấn Chơn Thành",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đồng Phú",
        "wards": [
          "Thị trấn Đồng Phú",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hớn Quản",
        "wards": [
          "Thị trấn Hớn Quản",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lộc Ninh",
        "wards": [
          "Thị trấn Lộc Ninh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phú Riềng",
        "wards": [
          "Thị trấn Phú Riềng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Bình Thuận",
    "districts": [
      {
        "name": "Phan Thiết",
        "wards": [
          "Thị trấn Phan Thiết",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "La Gi",
        "wards": [
          "Thị trấn La Gi",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bắc Bình",
        "wards": [
          "Thị trấn Bắc Bình",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đức Linh",
        "wards": [
          "Thị trấn Đức Linh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hàm Tân",
        "wards": [
          "Thị trấn Hàm Tân",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hàm Thuận Bắc",
        "wards": [
          "Thị trấn Hàm Thuận Bắc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hàm Thuận Nam",
        "wards": [
          "Thị trấn Hàm Thuận Nam",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phú Quý",
        "wards": [
          "Thị trấn Phú Quý",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tánh Linh",
        "wards": [
          "Thị trấn Tánh Linh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tuy Phong",
        "wards": [
          "Thị trấn Tuy Phong",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Cà Mau",
    "districts": [
      {
        "name": "TP. Cà Mau",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Cái Nước",
        "wards": [
          "Thị trấn Cái Nước",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đầm Dơi",
        "wards": [
          "Thị trấn Đầm Dơi",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Năm Căn",
        "wards": [
          "Thị trấn Năm Căn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ngọc Hiển",
        "wards": [
          "Thị trấn Ngọc Hiển",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phú Tân",
        "wards": [
          "Thị trấn Phú Tân",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thới Bình",
        "wards": [
          "Thị trấn Thới Bình",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Trần Văn Thời",
        "wards": [
          "Thị trấn Trần Văn Thời",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "U Minh",
        "wards": [
          "Thị trấn U Minh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Cao Bằng",
    "districts": [
      {
        "name": "TP. Cao Bằng",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Bảo Lạc",
        "wards": [
          "Thị trấn Bảo Lạc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bảo Lâm",
        "wards": [
          "Thị trấn Bảo Lâm",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hạ Lang",
        "wards": [
          "Thị trấn Hạ Lang",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hà Quảng",
        "wards": [
          "Thị trấn Hà Quảng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hòa An",
        "wards": [
          "Thị trấn Hòa An",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Nguyên Bình",
        "wards": [
          "Thị trấn Nguyên Bình",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Quảng Hòa",
        "wards": [
          "Thị trấn Quảng Hòa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thạch An",
        "wards": [
          "Thị trấn Thạch An",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Trùng Khánh",
        "wards": [
          "Thị trấn Trùng Khánh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Đắk Lắk",
    "districts": [
      {
        "name": "Buôn Ma Thuột",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Buôn Hồ",
        "wards": [
          "Thị trấn Buôn Hồ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Buôn Đôn",
        "wards": [
          "Thị trấn Buôn Đôn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Cư Kuin",
        "wards": [
          "Thị trấn Cư Kuin",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Cư M'gar",
        "wards": [
          "Thị trấn Cư M'gar",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ea H'leo",
        "wards": [
          "Thị trấn Ea H'leo",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ea Kar",
        "wards": [
          "Thị trấn Ea Kar",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ea Súp",
        "wards": [
          "Thị trấn Ea Súp",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Krông Ana",
        "wards": [
          "Thị trấn Krông Ana",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Krông Bông",
        "wards": [
          "Thị trấn Krông Bông",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Krông Búk",
        "wards": [
          "Thị trấn Krông Búk",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Krông Năng",
        "wards": [
          "Thị trấn Krông Năng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Krông Pắc",
        "wards": [
          "Thị trấn Krông Pắc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lắk",
        "wards": [
          "Thị trấn Lắk",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "M'Drắk",
        "wards": [
          "Thị trấn M'Drắk",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Đắk Nông",
    "districts": [
      {
        "name": "Gia Nghĩa",
        "wards": [
          "Thị trấn Gia Nghĩa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Cư Jút",
        "wards": [
          "Thị trấn Cư Jút",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đắk Glong",
        "wards": [
          "Thị trấn Đắk Glong",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đắk Mil",
        "wards": [
          "Thị trấn Đắk Mil",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đắk R'lấp",
        "wards": [
          "Thị trấn Đắk R'lấp",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đắk Song",
        "wards": [
          "Thị trấn Đắk Song",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Krông Nô",
        "wards": [
          "Thị trấn Krông Nô",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tuy Đức",
        "wards": [
          "Thị trấn Tuy Đức",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Điện Biên",
    "districts": [
      {
        "name": "Điện Biên Phủ",
        "wards": [
          "Thị trấn Điện Biên Phủ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mường Lay",
        "wards": [
          "Thị trấn Mường Lay",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Điện Biên",
        "wards": [
          "Thị trấn Điện Biên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Điện Biên Đông",
        "wards": [
          "Thị trấn Điện Biên Đông",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mường Ảng",
        "wards": [
          "Thị trấn Mường Ảng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mường Chà",
        "wards": [
          "Thị trấn Mường Chà",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mường Nhé",
        "wards": [
          "Thị trấn Mường Nhé",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Nậm Pồ",
        "wards": [
          "Thị trấn Nậm Pồ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tủa Chùa",
        "wards": [
          "Thị trấn Tủa Chùa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tuần Giáo",
        "wards": [
          "Thị trấn Tuần Giáo",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Đồng Nai",
    "districts": [
      {
        "name": "Biên Hòa",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Long Khánh",
        "wards": [
          "Thị trấn Long Khánh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Cẩm Mỹ",
        "wards": [
          "Thị trấn Cẩm Mỹ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Định Quán",
        "wards": [
          "Thị trấn Định Quán",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Long Thành",
        "wards": [
          "Thị trấn Long Thành",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Nhơn Trạch",
        "wards": [
          "Thị trấn Nhơn Trạch",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tân Phú",
        "wards": [
          "Thị trấn Tân Phú",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thống Nhất",
        "wards": [
          "Thị trấn Thống Nhất",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Trảng Bom",
        "wards": [
          "Thị trấn Trảng Bom",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Vĩnh Cửu",
        "wards": [
          "Thị trấn Vĩnh Cửu",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Xuân Lộc",
        "wards": [
          "Thị trấn Xuân Lộc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Đồng Tháp",
    "districts": [
      {
        "name": "Cao Lãnh",
        "wards": [
          "Thị trấn Cao Lãnh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Sa Đéc",
        "wards": [
          "Thị trấn Sa Đéc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hồng Ngự",
        "wards": [
          "Thị trấn Hồng Ngự",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Châu Thành",
        "wards": [
          "Thị trấn Châu Thành",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lai Vung",
        "wards": [
          "Thị trấn Lai Vung",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lấp Vò",
        "wards": [
          "Thị trấn Lấp Vò",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tam Nông",
        "wards": [
          "Thị trấn Tam Nông",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tân Hồng",
        "wards": [
          "Thị trấn Tân Hồng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thanh Bình",
        "wards": [
          "Thị trấn Thanh Bình",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tháp Mười",
        "wards": [
          "Thị trấn Tháp Mười",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Gia Lai",
    "districts": [
      {
        "name": "Pleiku",
        "wards": [
          "Thị trấn Pleiku",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "An Khê",
        "wards": [
          "Thị trấn An Khê",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ayun Pa",
        "wards": [
          "Thị trấn Ayun Pa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Chư Păh",
        "wards": [
          "Thị trấn Chư Păh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Chư Prông",
        "wards": [
          "Thị trấn Chư Prông",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Chư Pưh",
        "wards": [
          "Thị trấn Chư Pưh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Chư Sê",
        "wards": [
          "Thị trấn Chư Sê",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đắk Đoa",
        "wards": [
          "Thị trấn Đắk Đoa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đắk Pơ",
        "wards": [
          "Thị trấn Đắk Pơ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đức Cơ",
        "wards": [
          "Thị trấn Đức Cơ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ia Grai",
        "wards": [
          "Thị trấn Ia Grai",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ia Pa",
        "wards": [
          "Thị trấn Ia Pa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "KBang",
        "wards": [
          "Thị trấn KBang",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Kông Chro",
        "wards": [
          "Thị trấn Kông Chro",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Krông Pa",
        "wards": [
          "Thị trấn Krông Pa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mang Yang",
        "wards": [
          "Thị trấn Mang Yang",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phú Thiện",
        "wards": [
          "Thị trấn Phú Thiện",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Hà Giang",
    "districts": [
      {
        "name": "TP. Hà Giang",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Bắc Mê",
        "wards": [
          "Thị trấn Bắc Mê",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bắc Quang",
        "wards": [
          "Thị trấn Bắc Quang",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đồng Văn",
        "wards": [
          "Thị trấn Đồng Văn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hoàng Su Phì",
        "wards": [
          "Thị trấn Hoàng Su Phì",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mèo Vạc",
        "wards": [
          "Thị trấn Mèo Vạc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Quản Bạ",
        "wards": [
          "Thị trấn Quản Bạ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Quang Bình",
        "wards": [
          "Thị trấn Quang Bình",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Vị Xuyên",
        "wards": [
          "Thị trấn Vị Xuyên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Xín Mần",
        "wards": [
          "Thị trấn Xín Mần",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Yên Minh",
        "wards": [
          "Thị trấn Yên Minh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Hà Nam",
    "districts": [
      {
        "name": "Phủ Lý",
        "wards": [
          "Thị trấn Phủ Lý",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Duy Tiên",
        "wards": [
          "Thị trấn Duy Tiên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bình Lục",
        "wards": [
          "Thị trấn Bình Lục",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Kim Bảng",
        "wards": [
          "Thị trấn Kim Bảng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lý Nhân",
        "wards": [
          "Thị trấn Lý Nhân",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thanh Liêm",
        "wards": [
          "Thị trấn Thanh Liêm",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Hà Tĩnh",
    "districts": [
      {
        "name": "TP. Hà Tĩnh",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Hồng Lĩnh",
        "wards": [
          "Thị trấn Hồng Lĩnh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Kỳ Anh (Thị xã)",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Cẩm Xuyên",
        "wards": [
          "Thị trấn Cẩm Xuyên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Can Lộc",
        "wards": [
          "Thị trấn Can Lộc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đức Thọ",
        "wards": [
          "Thị trấn Đức Thọ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hương Khê",
        "wards": [
          "Thị trấn Hương Khê",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hương Sơn",
        "wards": [
          "Thị trấn Hương Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Kỳ Anh (Huyện)",
        "wards": [
          "Thị trấn Kỳ Anh (Huyện)",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lộc Hà",
        "wards": [
          "Thị trấn Lộc Hà",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Nghi Xuân",
        "wards": [
          "Thị trấn Nghi Xuân",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thạch Hà",
        "wards": [
          "Thị trấn Thạch Hà",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Vũ Quang",
        "wards": [
          "Thị trấn Vũ Quang",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Hải Dương",
    "districts": [
      {
        "name": "TP. Hải Dương",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Chí Linh",
        "wards": [
          "Thị trấn Chí Linh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Kinh Môn",
        "wards": [
          "Thị trấn Kinh Môn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bình Giang",
        "wards": [
          "Thị trấn Bình Giang",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Cẩm Giàng",
        "wards": [
          "Thị trấn Cẩm Giàng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Gia Lộc",
        "wards": [
          "Thị trấn Gia Lộc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Kim Thành",
        "wards": [
          "Thị trấn Kim Thành",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Nam Sách",
        "wards": [
          "Thị trấn Nam Sách",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ninh Giang",
        "wards": [
          "Thị trấn Ninh Giang",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thanh Hà",
        "wards": [
          "Thị trấn Thanh Hà",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thanh Miện",
        "wards": [
          "Thị trấn Thanh Miện",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tứ Kỳ",
        "wards": [
          "Thị trấn Tứ Kỳ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Hậu Giang",
    "districts": [
      {
        "name": "Vị Thanh",
        "wards": [
          "Thị trấn Vị Thanh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ngã Bảy",
        "wards": [
          "Thị trấn Ngã Bảy",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Long Mỹ (Thị xã)",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Châu Thành",
        "wards": [
          "Thị trấn Châu Thành",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Châu Thành A",
        "wards": [
          "Thị trấn Châu Thành A",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Long Mỹ (Huyện)",
        "wards": [
          "Thị trấn Long Mỹ (Huyện)",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phụng Hiệp",
        "wards": [
          "Thị trấn Phụng Hiệp",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Vị Thủy",
        "wards": [
          "Thị trấn Vị Thủy",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Hòa Bình",
    "districts": [
      {
        "name": "TP. Hòa Bình",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Cao Phong",
        "wards": [
          "Thị trấn Cao Phong",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đà Bắc",
        "wards": [
          "Thị trấn Đà Bắc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Kim Bôi",
        "wards": [
          "Thị trấn Kim Bôi",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lạc Sơn",
        "wards": [
          "Thị trấn Lạc Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lạc Thủy",
        "wards": [
          "Thị trấn Lạc Thủy",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lương Sơn",
        "wards": [
          "Thị trấn Lương Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mai Châu",
        "wards": [
          "Thị trấn Mai Châu",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tân Lạc",
        "wards": [
          "Thị trấn Tân Lạc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Yên Thủy",
        "wards": [
          "Thị trấn Yên Thủy",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Hưng Yên",
    "districts": [
      {
        "name": "TP. Hưng Yên",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Mỹ Hào",
        "wards": [
          "Thị trấn Mỹ Hào",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ân Thi",
        "wards": [
          "Thị trấn Ân Thi",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Khoái Châu",
        "wards": [
          "Thị trấn Khoái Châu",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Kim Động",
        "wards": [
          "Thị trấn Kim Động",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phù Cừ",
        "wards": [
          "Thị trấn Phù Cừ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tiên Lữ",
        "wards": [
          "Thị trấn Tiên Lữ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Văn Giang",
        "wards": [
          "Thị trấn Văn Giang",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Văn Lâm",
        "wards": [
          "Thị trấn Văn Lâm",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Yên Mỹ",
        "wards": [
          "Thị trấn Yên Mỹ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Khánh Hòa",
    "districts": [
      {
        "name": "Nha Trang",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Cam Ranh",
        "wards": [
          "Thị trấn Cam Ranh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ninh Hòa",
        "wards": [
          "Thị trấn Ninh Hòa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Cam Lâm",
        "wards": [
          "Thị trấn Cam Lâm",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Diên Khánh",
        "wards": [
          "Thị trấn Diên Khánh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Khánh Sơn",
        "wards": [
          "Thị trấn Khánh Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Khánh Vĩnh",
        "wards": [
          "Thị trấn Khánh Vĩnh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Trường Sa",
        "wards": [
          "Thị trấn Trường Sa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Vạn Ninh",
        "wards": [
          "Thị trấn Vạn Ninh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Kiên Giang",
    "districts": [
      {
        "name": "Rạch Giá",
        "wards": [
          "Thị trấn Rạch Giá",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hà Tiên",
        "wards": [
          "Thị trấn Hà Tiên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phú Quốc",
        "wards": [
          "Thị trấn Phú Quốc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "An Biên",
        "wards": [
          "Thị trấn An Biên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "An Minh",
        "wards": [
          "Thị trấn An Minh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Châu Thành",
        "wards": [
          "Thị trấn Châu Thành",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Giang Thành",
        "wards": [
          "Thị trấn Giang Thành",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Giồng Riềng",
        "wards": [
          "Thị trấn Giồng Riềng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Gò Quao",
        "wards": [
          "Thị trấn Gò Quao",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hòn Đất",
        "wards": [
          "Thị trấn Hòn Đất",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Kiên Hải",
        "wards": [
          "Thị trấn Kiên Hải",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Kiên Lương",
        "wards": [
          "Thị trấn Kiên Lương",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tân Hiệp",
        "wards": [
          "Thị trấn Tân Hiệp",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "U Minh Thượng",
        "wards": [
          "Thị trấn U Minh Thượng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Vĩnh Thuận",
        "wards": [
          "Thị trấn Vĩnh Thuận",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Kon Tum",
    "districts": [
      {
        "name": "TP. Kon Tum",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Đắk Glei",
        "wards": [
          "Thị trấn Đắk Glei",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đắk Hà",
        "wards": [
          "Thị trấn Đắk Hà",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đắk Tô",
        "wards": [
          "Thị trấn Đắk Tô",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ia H'Drai",
        "wards": [
          "Thị trấn Ia H'Drai",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Kon Plông",
        "wards": [
          "Thị trấn Kon Plông",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Kon Rẫy",
        "wards": [
          "Thị trấn Kon Rẫy",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ngọc Hồi",
        "wards": [
          "Thị trấn Ngọc Hồi",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Sa Thầy",
        "wards": [
          "Thị trấn Sa Thầy",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tu Mơ Rông",
        "wards": [
          "Thị trấn Tu Mơ Rông",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Lai Châu",
    "districts": [
      {
        "name": "TP. Lai Châu",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Mường Tè",
        "wards": [
          "Thị trấn Mường Tè",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Nậm Nhùn",
        "wards": [
          "Thị trấn Nậm Nhùn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phong Thổ",
        "wards": [
          "Thị trấn Phong Thổ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Sìn Hồ",
        "wards": [
          "Thị trấn Sìn Hồ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tam Đường",
        "wards": [
          "Thị trấn Tam Đường",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tân Uyên",
        "wards": [
          "Thị trấn Tân Uyên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Than Uyên",
        "wards": [
          "Thị trấn Than Uyên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Lâm Đồng",
    "districts": [
      {
        "name": "Đà Lạt",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Bảo Lộc",
        "wards": [
          "Thị trấn Bảo Lộc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bảo Lâm",
        "wards": [
          "Thị trấn Bảo Lâm",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Cát Tiên",
        "wards": [
          "Thị trấn Cát Tiên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Di Linh",
        "wards": [
          "Thị trấn Di Linh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đạ Huoai",
        "wards": [
          "Thị trấn Đạ Huoai",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đạ Tẻh",
        "wards": [
          "Thị trấn Đạ Tẻh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đam Rông",
        "wards": [
          "Thị trấn Đam Rông",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đơn Dương",
        "wards": [
          "Thị trấn Đơn Dương",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đức Trọng",
        "wards": [
          "Thị trấn Đức Trọng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lạc Dương",
        "wards": [
          "Thị trấn Lạc Dương",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lâm Hà",
        "wards": [
          "Thị trấn Lâm Hà",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Lạng Sơn",
    "districts": [
      {
        "name": "TP. Lạng Sơn",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Bắc Sơn",
        "wards": [
          "Thị trấn Bắc Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bình Gia",
        "wards": [
          "Thị trấn Bình Gia",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Cao Lộc",
        "wards": [
          "Thị trấn Cao Lộc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Chi Lăng",
        "wards": [
          "Thị trấn Chi Lăng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đình Lập",
        "wards": [
          "Thị trấn Đình Lập",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hữu Lũng",
        "wards": [
          "Thị trấn Hữu Lũng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lộc Bình",
        "wards": [
          "Thị trấn Lộc Bình",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tràng Định",
        "wards": [
          "Thị trấn Tràng Định",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Văn Lãng",
        "wards": [
          "Thị trấn Văn Lãng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Văn Quan",
        "wards": [
          "Thị trấn Văn Quan",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Lào Cai",
    "districts": [
      {
        "name": "TP. Lào Cai",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Sa Pa",
        "wards": [
          "Thị trấn Sa Pa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bát Xát",
        "wards": [
          "Thị trấn Bát Xát",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bảo Thắng",
        "wards": [
          "Thị trấn Bảo Thắng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bảo Yên",
        "wards": [
          "Thị trấn Bảo Yên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bắc Hà",
        "wards": [
          "Thị trấn Bắc Hà",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mường Khương",
        "wards": [
          "Thị trấn Mường Khương",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Si Ma Cai",
        "wards": [
          "Thị trấn Si Ma Cai",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Văn Bàn",
        "wards": [
          "Thị trấn Văn Bàn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Long An",
    "districts": [
      {
        "name": "Tân An",
        "wards": [
          "Thị trấn Tân An",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Kiến Tường",
        "wards": [
          "Thị trấn Kiến Tường",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bến Lức",
        "wards": [
          "Thị trấn Bến Lức",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Cần Đước",
        "wards": [
          "Thị trấn Cần Đước",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Cần Giuộc",
        "wards": [
          "Thị trấn Cần Giuộc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Châu Thành",
        "wards": [
          "Thị trấn Châu Thành",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đức Hòa",
        "wards": [
          "Thị trấn Đức Hòa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đức Huệ",
        "wards": [
          "Thị trấn Đức Huệ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mộc Hóa",
        "wards": [
          "Thị trấn Mộc Hóa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tân Hưng",
        "wards": [
          "Thị trấn Tân Hưng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tân Thạnh",
        "wards": [
          "Thị trấn Tân Thạnh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tân Trụ",
        "wards": [
          "Thị trấn Tân Trụ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thạnh Hóa",
        "wards": [
          "Thị trấn Thạnh Hóa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thủ Thừa",
        "wards": [
          "Thị trấn Thủ Thừa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Vĩnh Hưng",
        "wards": [
          "Thị trấn Vĩnh Hưng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Nam Định",
    "districts": [
      {
        "name": "TP. Nam Định",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Giao Thủy",
        "wards": [
          "Thị trấn Giao Thủy",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hải Hậu",
        "wards": [
          "Thị trấn Hải Hậu",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mỹ Lộc",
        "wards": [
          "Thị trấn Mỹ Lộc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Nam Trực",
        "wards": [
          "Thị trấn Nam Trực",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Nghĩa Hưng",
        "wards": [
          "Thị trấn Nghĩa Hưng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Trực Ninh",
        "wards": [
          "Thị trấn Trực Ninh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Vụ Bản",
        "wards": [
          "Thị trấn Vụ Bản",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Xuân Trường",
        "wards": [
          "Thị trấn Xuân Trường",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ý Yên",
        "wards": [
          "Thị trấn Ý Yên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Nghệ An",
    "districts": [
      {
        "name": "TP. Vinh",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Cửa Lò",
        "wards": [
          "Thị trấn Cửa Lò",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thái Hòa",
        "wards": [
          "Thị trấn Thái Hòa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hoàng Mai",
        "wards": [
          "Thị trấn Hoàng Mai",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Anh Sơn",
        "wards": [
          "Thị trấn Anh Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Con Cuông",
        "wards": [
          "Thị trấn Con Cuông",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Diễn Châu",
        "wards": [
          "Thị trấn Diễn Châu",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đô Lương",
        "wards": [
          "Thị trấn Đô Lương",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hưng Nguyên",
        "wards": [
          "Thị trấn Hưng Nguyên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Kỳ Sơn",
        "wards": [
          "Thị trấn Kỳ Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Nam Đàn",
        "wards": [
          "Thị trấn Nam Đàn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Nghi Lộc",
        "wards": [
          "Thị trấn Nghi Lộc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Nghĩa Đàn",
        "wards": [
          "Thị trấn Nghĩa Đàn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Quế Phong",
        "wards": [
          "Thị trấn Quế Phong",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Quỳ Châu",
        "wards": [
          "Thị trấn Quỳ Châu",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Quỳ Hợp",
        "wards": [
          "Thị trấn Quỳ Hợp",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Quỳnh Lưu",
        "wards": [
          "Thị trấn Quỳnh Lưu",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tân Kỳ",
        "wards": [
          "Thị trấn Tân Kỳ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thanh Chương",
        "wards": [
          "Thị trấn Thanh Chương",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tương Dương",
        "wards": [
          "Thị trấn Tương Dương",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Yên Thành",
        "wards": [
          "Thị trấn Yên Thành",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Ninh Bình",
    "districts": [
      {
        "name": "TP. Ninh Bình",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Tam Điệp",
        "wards": [
          "Thị trấn Tam Điệp",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Gia Viễn",
        "wards": [
          "Thị trấn Gia Viễn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hoa Lư",
        "wards": [
          "Thị trấn Hoa Lư",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Kim Sơn",
        "wards": [
          "Thị trấn Kim Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Nho Quan",
        "wards": [
          "Thị trấn Nho Quan",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Yên Khánh",
        "wards": [
          "Thị trấn Yên Khánh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Yên Mô",
        "wards": [
          "Thị trấn Yên Mô",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Ninh Thuận",
    "districts": [
      {
        "name": "Phan Rang - Tháp Chàm",
        "wards": [
          "Thị trấn Phan Rang - Tháp Chàm",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bác Ái",
        "wards": [
          "Thị trấn Bác Ái",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ninh Hải",
        "wards": [
          "Thị trấn Ninh Hải",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ninh Phước",
        "wards": [
          "Thị trấn Ninh Phước",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ninh Sơn",
        "wards": [
          "Thị trấn Ninh Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thuận Bắc",
        "wards": [
          "Thị trấn Thuận Bắc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thuận Nam",
        "wards": [
          "Thị trấn Thuận Nam",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Phú Thọ",
    "districts": [
      {
        "name": "Việt Trì",
        "wards": [
          "Thị trấn Việt Trì",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "TX. Phú Thọ",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Cẩm Khê",
        "wards": [
          "Thị trấn Cẩm Khê",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đoan Hùng",
        "wards": [
          "Thị trấn Đoan Hùng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hạ Hòa",
        "wards": [
          "Thị trấn Hạ Hòa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lâm Thao",
        "wards": [
          "Thị trấn Lâm Thao",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phù Ninh",
        "wards": [
          "Thị trấn Phù Ninh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tam Nông",
        "wards": [
          "Thị trấn Tam Nông",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tân Sơn",
        "wards": [
          "Thị trấn Tân Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thanh Ba",
        "wards": [
          "Thị trấn Thanh Ba",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thanh Sơn",
        "wards": [
          "Thị trấn Thanh Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thanh Thủy",
        "wards": [
          "Thị trấn Thanh Thủy",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Yên Lập",
        "wards": [
          "Thị trấn Yên Lập",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Phú Yên",
    "districts": [
      {
        "name": "Tuy Hòa",
        "wards": [
          "Thị trấn Tuy Hòa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Sông Cầu",
        "wards": [
          "Thị trấn Sông Cầu",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đông Hòa",
        "wards": [
          "Thị trấn Đông Hòa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đồng Xuân",
        "wards": [
          "Thị trấn Đồng Xuân",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phú Hòa",
        "wards": [
          "Thị trấn Phú Hòa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Sơn Hòa",
        "wards": [
          "Thị trấn Sơn Hòa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Sông Hinh",
        "wards": [
          "Thị trấn Sông Hinh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tây Hòa",
        "wards": [
          "Thị trấn Tây Hòa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tuy An",
        "wards": [
          "Thị trấn Tuy An",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Quảng Bình",
    "districts": [
      {
        "name": "Đồng Hới",
        "wards": [
          "Thị trấn Đồng Hới",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ba Đồn",
        "wards": [
          "Thị trấn Ba Đồn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bố Trạch",
        "wards": [
          "Thị trấn Bố Trạch",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lệ Thủy",
        "wards": [
          "Thị trấn Lệ Thủy",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Minh Hóa",
        "wards": [
          "Thị trấn Minh Hóa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Quảng Ninh",
        "wards": [
          "Thị trấn Quảng Ninh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Quảng Trạch",
        "wards": [
          "Thị trấn Quảng Trạch",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tuyên Hóa",
        "wards": [
          "Thị trấn Tuyên Hóa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Quảng Nam",
    "districts": [
      {
        "name": "Tam Kỳ",
        "wards": [
          "Thị trấn Tam Kỳ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hội An",
        "wards": [
          "Thị trấn Hội An",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Điện Bàn",
        "wards": [
          "Thị trấn Điện Bàn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bắc Trà My",
        "wards": [
          "Thị trấn Bắc Trà My",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Duy Xuyên",
        "wards": [
          "Thị trấn Duy Xuyên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đại Lộc",
        "wards": [
          "Thị trấn Đại Lộc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đông Giang",
        "wards": [
          "Thị trấn Đông Giang",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hiệp Đức",
        "wards": [
          "Thị trấn Hiệp Đức",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Nam Giang",
        "wards": [
          "Thị trấn Nam Giang",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Nam Trà My",
        "wards": [
          "Thị trấn Nam Trà My",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Nông Sơn",
        "wards": [
          "Thị trấn Nông Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Núi Thành",
        "wards": [
          "Thị trấn Núi Thành",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phú Ninh",
        "wards": [
          "Thị trấn Phú Ninh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phước Sơn",
        "wards": [
          "Thị trấn Phước Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Quế Sơn",
        "wards": [
          "Thị trấn Quế Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tây Giang",
        "wards": [
          "Thị trấn Tây Giang",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thăng Bình",
        "wards": [
          "Thị trấn Thăng Bình",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tiên Phước",
        "wards": [
          "Thị trấn Tiên Phước",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Quảng Ngãi",
    "districts": [
      {
        "name": "TP. Quảng Ngãi",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Đức Phổ",
        "wards": [
          "Thị trấn Đức Phổ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ba Tơ",
        "wards": [
          "Thị trấn Ba Tơ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bình Sơn",
        "wards": [
          "Thị trấn Bình Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lý Sơn",
        "wards": [
          "Thị trấn Lý Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Minh Long",
        "wards": [
          "Thị trấn Minh Long",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mộ Đức",
        "wards": [
          "Thị trấn Mộ Đức",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Nghĩa Hành",
        "wards": [
          "Thị trấn Nghĩa Hành",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Sơn Hà",
        "wards": [
          "Thị trấn Sơn Hà",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Sơn Tây",
        "wards": [
          "Thị trấn Sơn Tây",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Sơn Tịnh",
        "wards": [
          "Thị trấn Sơn Tịnh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Trà Bồng",
        "wards": [
          "Thị trấn Trà Bồng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tư Nghĩa",
        "wards": [
          "Thị trấn Tư Nghĩa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Quảng Ninh",
    "districts": [
      {
        "name": "Hạ Long",
        "wards": [
          "Thị trấn Hạ Long",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Cẩm Phả",
        "wards": [
          "Thị trấn Cẩm Phả",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Móng Cái",
        "wards": [
          "Thị trấn Móng Cái",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Uông Bí",
        "wards": [
          "Thị trấn Uông Bí",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đông Triều",
        "wards": [
          "Thị trấn Đông Triều",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Quảng Yên",
        "wards": [
          "Thị trấn Quảng Yên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ba Chẽ",
        "wards": [
          "Thị trấn Ba Chẽ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bình Liêu",
        "wards": [
          "Thị trấn Bình Liêu",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Cô Tô",
        "wards": [
          "Thị trấn Cô Tô",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đầm Hà",
        "wards": [
          "Thị trấn Đầm Hà",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hải Hà",
        "wards": [
          "Thị trấn Hải Hà",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tiên Yên",
        "wards": [
          "Thị trấn Tiên Yên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Vân Đồn",
        "wards": [
          "Thị trấn Vân Đồn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Quảng Trị",
    "districts": [
      {
        "name": "Đông Hà",
        "wards": [
          "Thị trấn Đông Hà",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "TX. Quảng Trị",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Cam Lộ",
        "wards": [
          "Thị trấn Cam Lộ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Cồn Cỏ",
        "wards": [
          "Thị trấn Cồn Cỏ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đakrông",
        "wards": [
          "Thị trấn Đakrông",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Gio Linh",
        "wards": [
          "Thị trấn Gio Linh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hải Lăng",
        "wards": [
          "Thị trấn Hải Lăng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hướng Hóa",
        "wards": [
          "Thị trấn Hướng Hóa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Triệu Phong",
        "wards": [
          "Thị trấn Triệu Phong",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Vĩnh Linh",
        "wards": [
          "Thị trấn Vĩnh Linh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Sóc Trăng",
    "districts": [
      {
        "name": "TP. Sóc Trăng",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Ngã Năm",
        "wards": [
          "Thị trấn Ngã Năm",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Vĩnh Châu",
        "wards": [
          "Thị trấn Vĩnh Châu",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Châu Thành",
        "wards": [
          "Thị trấn Châu Thành",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Cù Lao Dung",
        "wards": [
          "Thị trấn Cù Lao Dung",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Kế Sách",
        "wards": [
          "Thị trấn Kế Sách",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Long Phú",
        "wards": [
          "Thị trấn Long Phú",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mỹ Tú",
        "wards": [
          "Thị trấn Mỹ Tú",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mỹ Xuyên",
        "wards": [
          "Thị trấn Mỹ Xuyên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thạnh Trị",
        "wards": [
          "Thị trấn Thạnh Trị",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Trần Đề",
        "wards": [
          "Thị trấn Trần Đề",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Sơn La",
    "districts": [
      {
        "name": "TP. Sơn La",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Bắc Yên",
        "wards": [
          "Thị trấn Bắc Yên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mai Sơn",
        "wards": [
          "Thị trấn Mai Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mộc Châu",
        "wards": [
          "Thị trấn Mộc Châu",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mường La",
        "wards": [
          "Thị trấn Mường La",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phù Yên",
        "wards": [
          "Thị trấn Phù Yên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Quỳnh Nhai",
        "wards": [
          "Thị trấn Quỳnh Nhai",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Sông Mã",
        "wards": [
          "Thị trấn Sông Mã",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Sốp Cộp",
        "wards": [
          "Thị trấn Sốp Cộp",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thuận Châu",
        "wards": [
          "Thị trấn Thuận Châu",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Vân Hồ",
        "wards": [
          "Thị trấn Vân Hồ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Yên Châu",
        "wards": [
          "Thị trấn Yên Châu",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Tây Ninh",
    "districts": [
      {
        "name": "TP. Tây Ninh",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Hòa Thành",
        "wards": [
          "Thị trấn Hòa Thành",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Trảng Bàng",
        "wards": [
          "Thị trấn Trảng Bàng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bến Cầu",
        "wards": [
          "Thị trấn Bến Cầu",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Châu Thành",
        "wards": [
          "Thị trấn Châu Thành",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Dương Minh Châu",
        "wards": [
          "Thị trấn Dương Minh Châu",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Gò Dầu",
        "wards": [
          "Thị trấn Gò Dầu",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tân Biên",
        "wards": [
          "Thị trấn Tân Biên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tân Châu",
        "wards": [
          "Thị trấn Tân Châu",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Thái Bình",
    "districts": [
      {
        "name": "TP. Thái Bình",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Đông Hưng",
        "wards": [
          "Thị trấn Đông Hưng",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hưng Hà",
        "wards": [
          "Thị trấn Hưng Hà",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Kiến Xương",
        "wards": [
          "Thị trấn Kiến Xương",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Quỳnh Phụ",
        "wards": [
          "Thị trấn Quỳnh Phụ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thái Thụy",
        "wards": [
          "Thị trấn Thái Thụy",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tiền Hải",
        "wards": [
          "Thị trấn Tiền Hải",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Vũ Thư",
        "wards": [
          "Thị trấn Vũ Thư",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Thái Nguyên",
    "districts": [
      {
        "name": "TP. Thái Nguyên",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Sông Công",
        "wards": [
          "Thị trấn Sông Công",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phổ Yên",
        "wards": [
          "Thị trấn Phổ Yên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đại Từ",
        "wards": [
          "Thị trấn Đại Từ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Định Hóa",
        "wards": [
          "Thị trấn Định Hóa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đồng Hỷ",
        "wards": [
          "Thị trấn Đồng Hỷ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phú Bình",
        "wards": [
          "Thị trấn Phú Bình",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phú Lương",
        "wards": [
          "Thị trấn Phú Lương",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Võ Nhai",
        "wards": [
          "Thị trấn Võ Nhai",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Thanh Hóa",
    "districts": [
      {
        "name": "TP. Thanh Hóa",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Sầm Sơn",
        "wards": [
          "Thị trấn Sầm Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bỉm Sơn",
        "wards": [
          "Thị trấn Bỉm Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Nghi Sơn",
        "wards": [
          "Thị trấn Nghi Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bá Thước",
        "wards": [
          "Thị trấn Bá Thước",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Cẩm Thủy",
        "wards": [
          "Thị trấn Cẩm Thủy",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Đông Sơn",
        "wards": [
          "Thị trấn Đông Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hà Trung",
        "wards": [
          "Thị trấn Hà Trung",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hậu Lộc",
        "wards": [
          "Thị trấn Hậu Lộc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hoằng Hóa",
        "wards": [
          "Thị trấn Hoằng Hóa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lang Chánh",
        "wards": [
          "Thị trấn Lang Chánh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mường Lát",
        "wards": [
          "Thị trấn Mường Lát",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Nga Sơn",
        "wards": [
          "Thị trấn Nga Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Ngọc Lặc",
        "wards": [
          "Thị trấn Ngọc Lặc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Như Thanh",
        "wards": [
          "Thị trấn Như Thanh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Như Xuân",
        "wards": [
          "Thị trấn Như Xuân",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Nông Cống",
        "wards": [
          "Thị trấn Nông Cống",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Quan Hóa",
        "wards": [
          "Thị trấn Quan Hóa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Quan Sơn",
        "wards": [
          "Thị trấn Quan Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Quảng Xương",
        "wards": [
          "Thị trấn Quảng Xương",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thạch Thành",
        "wards": [
          "Thị trấn Thạch Thành",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thiệu Hóa",
        "wards": [
          "Thị trấn Thiệu Hóa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thọ Xuân",
        "wards": [
          "Thị trấn Thọ Xuân",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Thường Xuân",
        "wards": [
          "Thị trấn Thường Xuân",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Triệu Sơn",
        "wards": [
          "Thị trấn Triệu Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Vĩnh Lộc",
        "wards": [
          "Thị trấn Vĩnh Lộc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Yên Định",
        "wards": [
          "Thị trấn Yên Định",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Thừa Thiên Huế",
    "districts": [
      {
        "name": "TP. Huế",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Hương Thủy",
        "wards": [
          "Thị trấn Hương Thủy",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hương Trà",
        "wards": [
          "Thị trấn Hương Trà",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "A Lưới",
        "wards": [
          "Thị trấn A Lưới",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Nam Đông",
        "wards": [
          "Thị trấn Nam Đông",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phong Điền",
        "wards": [
          "Thị trấn Phong Điền",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phú Lộc",
        "wards": [
          "Thị trấn Phú Lộc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phú Vang",
        "wards": [
          "Thị trấn Phú Vang",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Quảng Điền",
        "wards": [
          "Thị trấn Quảng Điền",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Tiền Giang",
    "districts": [
      {
        "name": "Mỹ Tho",
        "wards": [
          "Thị trấn Mỹ Tho",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Gò Công",
        "wards": [
          "Thị trấn Gò Công",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Cai Lậy (Thị xã)",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Cai Lậy (Huyện)",
        "wards": [
          "Thị trấn Cai Lậy (Huyện)",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Cái Bè",
        "wards": [
          "Thị trấn Cái Bè",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Châu Thành",
        "wards": [
          "Thị trấn Châu Thành",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Chợ Gạo",
        "wards": [
          "Thị trấn Chợ Gạo",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Gò Công Đông",
        "wards": [
          "Thị trấn Gò Công Đông",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Gò Công Tây",
        "wards": [
          "Thị trấn Gò Công Tây",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tân Phú Đông",
        "wards": [
          "Thị trấn Tân Phú Đông",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tân Phước",
        "wards": [
          "Thị trấn Tân Phước",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Trà Vinh",
    "districts": [
      {
        "name": "TP. Trà Vinh",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Duyên Hải (Thị xã)",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Càng Long",
        "wards": [
          "Thị trấn Càng Long",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Cầu Kè",
        "wards": [
          "Thị trấn Cầu Kè",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Cầu Ngang",
        "wards": [
          "Thị trấn Cầu Ngang",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Châu Thành",
        "wards": [
          "Thị trấn Châu Thành",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Duyên Hải (Huyện)",
        "wards": [
          "Thị trấn Duyên Hải (Huyện)",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tiểu Cần",
        "wards": [
          "Thị trấn Tiểu Cần",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Trà Cú",
        "wards": [
          "Thị trấn Trà Cú",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Tuyên Quang",
    "districts": [
      {
        "name": "TP. Tuyên Quang",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Chiêm Hóa",
        "wards": [
          "Thị trấn Chiêm Hóa",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Hàm Yên",
        "wards": [
          "Thị trấn Hàm Yên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lâm Bình",
        "wards": [
          "Thị trấn Lâm Bình",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Na Hang",
        "wards": [
          "Thị trấn Na Hang",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Sơn Dương",
        "wards": [
          "Thị trấn Sơn Dương",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Yên Sơn",
        "wards": [
          "Thị trấn Yên Sơn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Vĩnh Long",
    "districts": [
      {
        "name": "TP. Vĩnh Long",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Bình Minh",
        "wards": [
          "Thị trấn Bình Minh",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bình Tân",
        "wards": [
          "Thị trấn Bình Tân",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Long Hồ",
        "wards": [
          "Thị trấn Long Hồ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mang Thít",
        "wards": [
          "Thị trấn Mang Thít",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tam Bình",
        "wards": [
          "Thị trấn Tam Bình",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Trà Ôn",
        "wards": [
          "Thị trấn Trà Ôn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Vũng Liêm",
        "wards": [
          "Thị trấn Vũng Liêm",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Vĩnh Phúc",
    "districts": [
      {
        "name": "Vĩnh Yên",
        "wards": [
          "Thị trấn Vĩnh Yên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Phúc Yên",
        "wards": [
          "Thị trấn Phúc Yên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Bình Xuyên",
        "wards": [
          "Thị trấn Bình Xuyên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lập Thạch",
        "wards": [
          "Thị trấn Lập Thạch",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Sông Lô",
        "wards": [
          "Thị trấn Sông Lô",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tam Đảo",
        "wards": [
          "Thị trấn Tam Đảo",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Tam Dương",
        "wards": [
          "Thị trấn Tam Dương",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Vĩnh Tường",
        "wards": [
          "Thị trấn Vĩnh Tường",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Yên Lạc",
        "wards": [
          "Thị trấn Yên Lạc",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
    ]
  },
  {
    "name": "Yên Bái",
    "districts": [
      {
        "name": "TP. Yên Bái",
        "wards": [
          "Phường 1",
          "Phường 2",
          "Phường 3",
          "Phường 4",
          "Phường 5",
          "Phường 6",
          "Phường 7",
          "Phường 8",
          "Phường 9",
          "Phường 10",
          "Tân Thành",
          "Quang Trung",
          "Trần Phú",
          "Lê Lợi",
          "Bình Minh",
          "Thống Nhất"
        ]
      },
      {
        "name": "Nghĩa Lộ",
        "wards": [
          "Thị trấn Nghĩa Lộ",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Lục Yên",
        "wards": [
          "Thị trấn Lục Yên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Mù Cang Chải",
        "wards": [
          "Thị trấn Mù Cang Chải",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Trạm Tấu",
        "wards": [
          "Thị trấn Trạm Tấu",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Trấn Yên",
        "wards": [
          "Thị trấn Trấn Yên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Văn Chấn",
        "wards": [
          "Thị trấn Văn Chấn",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Văn Yên",
        "wards": [
          "Thị trấn Văn Yên",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      },
      {
        "name": "Yên Bình",
        "wards": [
          "Thị trấn Yên Bình",
          "Xã Trung Tâm",
          "Xã An Bình",
          "Xã Bình Minh",
          "Xã Đông Hải",
          "Xã Hòa Bình",
          "Xã Hưng Thịnh",
          "Xã Phú Thịnh",
          "Xã Quang Trung",
          "Xã Tân Hiệp",
          "Xã Tân Lập",
          "Xã Tân Phong",
          "Xã Tân Tiến",
          "Xã Thắng Lợi",
          "Xã Vĩnh An",
          "Xã Vĩnh Hòa"
        ]
      }
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

    // 3. Khi thay đổi Quận/Huyện -> Nạp Phường/Xã hoặc tự nhập
    elHuyen.addEventListener('change', function() {
        if (this.value === '__KHAC__') {
            const nhap = prompt('Nhập tên Quận / Huyện / Thị xã của bạn:');
            if (nhap && nhap.trim()) {
                const customVal = nhap.trim();
                const opt = document.createElement('option');
                opt.value = customVal;
                opt.textContent = customVal;
                opt.selected = true;
                this.insertBefore(opt, this.lastElementChild);
                napPhuongXa(elXa, elTinh.value, customVal, null);
                this.dispatchEvent(new Event('input', { bubbles: true }));
                return;
            } else {
                this.value = '';
                return;
            }
        }
        const tenTinh = elTinh.value;
        const tenHuyen = this.value;
        napPhuongXa(elXa, tenTinh, tenHuyen, null);
    });

    // 4. Khi thay đổi Phường/Xã hoặc tự nhập
    elXa.addEventListener('change', function() {
        if (this.value === '__KHAC__') {
            const nhap = prompt('Nhập tên Phường / Xã / Thị trấn của bạn:');
            if (nhap && nhap.trim()) {
                const customVal = nhap.trim();
                const opt = document.createElement('option');
                opt.value = customVal;
                opt.textContent = customVal;
                opt.selected = true;
                this.insertBefore(opt, this.lastElementChild);
                this.dispatchEvent(new Event('input', { bubbles: true }));
                return;
            } else {
                this.value = '';
                return;
            }
        }
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

    // Tùy chọn Tự nhập tay
    const optKhac = document.createElement('option');
    optKhac.value = '__KHAC__';
    optKhac.textContent = '➕ Khác (Tự nhập tay)...';
    elHuyen.appendChild(optKhac);

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

    // Tùy chọn Tự nhập tay
    const optKhac = document.createElement('option');
    optKhac.value = '__KHAC__';
    optKhac.textContent = '➕ Khác (Tự nhập tay)...';
    elXa.appendChild(optKhac);
}
