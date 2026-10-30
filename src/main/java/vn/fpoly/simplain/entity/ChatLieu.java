package vn.fpoly.simplain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

/**
 * Thực thể Chất liệu vải [chat_lieu] dùng để quản lý danh mục chất liệu và liên kết với Sản phẩm.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_lieu")
public class ChatLieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Tên chất liệu vải không được để trống")
    @Size(min = 2, max = 100, message = "Tên chất liệu vải phải từ 2 đến 100 ký tự")
    @Nationalized
    @Column(name = "ten_chat_lieu", nullable = false, unique = true, length = 100, columnDefinition = "nvarchar(100)")
    private String tenChatLieu;

    @Size(max = 255, message = "Mô tả chất liệu không quá 255 ký tự")
    @Nationalized
    @Column(name = "mo_ta", length = 255, columnDefinition = "nvarchar(255)")
    private String moTa;

    @Builder.Default
    @Column(name = "kich_hoat", nullable = false)
    private Boolean kichHoat = true;

    @Transient
    private long soSanPhamSuDung;
}
