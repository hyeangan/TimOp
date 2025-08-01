package kangnamUni.TimOp.repository;

import kangnamUni.TimOp.domain.Lecture;
import kangnamUni.TimOp.dto.LectureFilterDTO;

import java.util.List;

public interface LectureRepositoryCustom {
    List<Lecture> findLectures(LectureFilterDTO filterDTO);
    List<Lecture> findByTitleStartWith(String keyword);
}
