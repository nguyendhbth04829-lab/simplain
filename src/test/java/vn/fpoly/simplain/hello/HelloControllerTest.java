package vn.fpoly.simplain.hello;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Kiểm thử endpoint hello.
 * Chạy bằng: .\mvnw.cmd -Dtest=HelloControllerTest test
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Kiểm thử API hello")
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/hello trả về lời chào mặc định")
    void getDefaultHello() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xin chào, Simplain!"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("GET /api/hello/name/{name} trả về lời chào theo tên")
    void greetByPath() throws Exception {
        mockMvc.perform(get("/api/hello/name/{name}", "An"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xin chào, An!"));
    }

    @Test
    @DisplayName("POST /api/hello với tên có dấu trả về 201 + lời chào đúng")
    void greetByJson() throws Exception {
        String body = "{\"name\":\"Cả lớp\"}";

        mockMvc.perform(post("/api/hello")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", containsString("Cả lớp")));
    }

    @Test
    @DisplayName("POST /api/hello với tên trống bị từ chối 400")
    void rejectBlankName() throws Exception {
        String body = "{\"name\":\"\"}";

        mockMvc.perform(post("/api/hello")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
