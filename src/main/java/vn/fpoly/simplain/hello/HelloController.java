package vn.fpoly.simplain.hello;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller API hello (code mẫu cho cả nhóm copy theo).
 *
 * <p>Thử trên Swagger: /docs -&gt; nhóm "Xin chào".
 */
@Tag(name = "Xin chào", description = "API mẫu cho thành viên mới làm quen")
@RestController
@RequestMapping("/api/hello")
public class HelloController {

    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    /**
     * Lấy lời chào mặc định.
     *
     * @return lời chào mặc định
     */
    @Operation(summary = "Lấy lời chào mặc định")
    @GetMapping
    public HelloResponse getHello() {
        return helloService.getDefaultGreeting();
    }

    /**
     * Chào theo tên trên đường dẫn.
     *
     * @param name tên người nhận lấy từ URL
     * @return lời chào theo tên
     */
    @Operation(summary = "Chào theo tên trên đường dẫn")
    @GetMapping("/name/{name}")
    public HelloResponse greetByPath(
            @Parameter(description = "Tên người nhận", example = "An")
            @PathVariable String name) {
        return helloService.greetByName(name);
    }

    /**
     * Chào theo tên trong JSON.
     *
     * @param request JSON chứa trường "name"
     * @return lời chào theo tên
     */
    @Operation(summary = "Chào theo tên trong JSON")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HelloResponse greetByJson(@Valid @RequestBody HelloRequest request) {
        return helloService.greetByName(request.name());
    }
}
