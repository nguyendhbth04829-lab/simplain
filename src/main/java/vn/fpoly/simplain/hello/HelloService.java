package vn.fpoly.simplain.hello;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

/**
 * Service tạo lời chào.
 * Controller chỉ gọi sang đây, không viết logic trực tiếp trong Controller.
 */
@Service
public class HelloService {

    /** Lời chào mặc định khi không có tên. */
    public static final String DEFAULT_MESSAGE = "Xin chào, Simplain!";

    /**
     * Lấy lời chào mặc định.
     *
     * @return kết quả chứa lời chào mặc định
     */
    public HelloResponse getDefaultGreeting() {
        return new HelloResponse(DEFAULT_MESSAGE, LocalDateTime.now());
    }

    /**
     * Tạo lời chào theo tên người dùng.
     * Nếu tên trống hoặc null thì trả về lời chào mặc định.
     *
     * @param name tên người nhận, có thể null hoặc trống
     * @return kết quả chứa câu chào hoàn chỉnh
     */
    public HelloResponse greetByName(String name) {
        String cleaned = name == null ? "" : name.trim();
        if (cleaned.isEmpty()) {
            return getDefaultGreeting();
        }
        return new HelloResponse("Xin chào, " + cleaned + "!", LocalDateTime.now());
    }
}
