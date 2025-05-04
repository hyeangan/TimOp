package kangnamUni.TimOp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kangnamUni.TimOp.Service.LectureService;
import kangnamUni.TimOp.domain.Lecture;
import kangnamUni.TimOp.dto.LectureDTO;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "강의", description = "강의 조회 및 강의계획서 API")
@RestController
@RequestMapping("/lectures")
public class LectureController {

    private final ResourceLoader resourceLoader;
    private final LectureService lectureService;

    public LectureController(ResourceLoader resourceLoader,
                             LectureService lectureService) {
        this.resourceLoader = resourceLoader;
        this.lectureService = lectureService;
    }

    @Operation(
            summary = "강의 자동완성 검색",
            description = "입력된 키워드로 시작하는 강의를 찾아 리스트로 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LectureDTO.class)))
            }
    )
    @GetMapping
    public ResponseEntity<?> autoComplete(
            @Parameter(description = "검색할 강의 키워드", example = "컴퓨")
            @RequestParam("keyword") String keyword) {

        List<Lecture> lectures = lectureService.findByTitleStartWith(keyword);
        List<LectureDTO> lectureDTOs = new ArrayList<>();
        for (Lecture lecture : lectures) {
            lectureDTOs.add(new LectureDTO(lecture.getTitle()));
        }
        return ResponseEntity.ok(lectureDTOs);
    }

    @Operation(
            summary = "강의계획서 조회",
            description = "특정 강의 ID의 강의계획서 HTML 파일을 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "강의계획서 반환"),
                    @ApiResponse(responseCode = "404", description = "강의계획서 파일이 존재하지 않음")
            }
    )
    @GetMapping("/{lectureId}/syllabus")
    public ResponseEntity<?> getSyllabus(
            @Parameter(description = "강의 ID", example = "101")
            @PathVariable("lectureId") Long lectureId) {

        Lecture lecture = lectureService.findById(lectureId);
        String syllabus = lecture.getSyllabus();

        String location = String.format("classpath:static/%s.html", syllabus);
        Resource html = resourceLoader.getResource(location);

        if (!html.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }
}
