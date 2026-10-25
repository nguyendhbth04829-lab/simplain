package vn.fpoly.simplain.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử Giao diện Web Thymeleaf của Module 5.
 */
@SpringBootTest
@DisplayName("Kiểm thử Giao diện Web Thymeleaf - Module 5")
class OrderWebControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("GET /admin/orders - Trang danh sách đơn hàng trả về 200 OK")
    void testViewOrderList_Success() throws Exception {
        mockMvc.perform(get("/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/list"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attributeExists("skus"));
    }

    @Test
    @DisplayName("GET /orders/new - Trang tạo đơn hàng test trả về 200 OK")
    void testShowCreateOrderForm_Success() throws Exception {
        mockMvc.perform(get("/orders/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/create"))
                .andExpect(model().attributeExists("customers"))
                .andExpect(model().attributeExists("skus"));
    }

    @Test
    @DisplayName("GET /admin/rma - Trang danh sách RMA trả về 200 OK")
    void testViewRmaList_Success() throws Exception {
        mockMvc.perform(get("/admin/rma"))
                .andExpect(status().isOk())
                .andExpect(view().name("rma/list"))
                .andExpect(model().attributeExists("rmas"));
    }

    @Test
    @DisplayName("GET /rma/new - Trang gửi yêu cầu RMA test trả về 200 OK")
    void testShowCreateRmaForm_Success() throws Exception {
        mockMvc.perform(get("/rma/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("rma/create"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attributeExists("skus"));
    }
}
