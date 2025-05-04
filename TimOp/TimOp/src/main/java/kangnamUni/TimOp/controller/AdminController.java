package kangnamUni.TimOp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kangnamUni.TimOp.Service.WebScraper;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Tag(name = "관리자", description = "관리자용 웹 스캐리핑 실행 API")
@RestController
public class AdminController {
    private final ApplicationContext context;

    public AdminController(ApplicationContext context){
        this.context = context;
    }

    @Operation(
            summary = "웹 스크래핑 실행",
            description = "입력한 연도 학기에 대한 수강정보를 스크래핑합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "스크래핑 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 학기입력"),
                    @ApiResponse(responseCode = "500", description = "스크래핑 실패")
            }

    )
    @PostMapping("/admin/scraper/run")
    public ResponseEntity<?> runScraper(@Parameter(description = "년도", example = "2025") @RequestParam("year") int year,
                                        @Parameter(description = "학기", example = "2학기") @RequestParam("semester") String semester){
        WebScraper webScraper = context.getBean(WebScraper.class);
        Set<String> validSemester = Set.of("1학기", "하계학기", "2학기", "동계학기");
        if (!validSemester.contains(semester)){
            return ResponseEntity.badRequest().body("지원하지 않는 학기 입니다.");
        }
        try {
            webScraper.scrape(year, semester);
            return ResponseEntity.ok("스크래핑 완료: " + year + "년 " + semester);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("스크래핑 중 오류 발생");
        }
    }
}

