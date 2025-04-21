package kangnamUni.TimOp.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kangnamUni.TimOp.domain.DayOfWeekEnum;
import kangnamUni.TimOp.domain.Lecture;
import kangnamUni.TimOp.dto.LectureFilterDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


import static kangnamUni.TimOp.domain.QLecture.lecture;
import static kangnamUni.TimOp.domain.QLectureTime.lectureTime;

@Slf4j
public class LectureRepositoryCustomImpl implements LectureRepositoryCustom{
    @PersistenceContext //EntityManager를 빈으로 주입할 때 사용하는 어노테이션
    private EntityManager entityManager;

    @Override
    public List<Lecture> findLectures(LectureFilterDTO filterDTO) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        BooleanExpression predicate = createPredicate(filterDTO);
        return queryFactory
                .selectFrom(lecture)
                .join(lecture.lectureTimes, lectureTime)
                .where(predicate)
                .fetch();
    }
    private BooleanExpression createPredicate(LectureFilterDTO filterDTO) {

        if(filterDTO.getAvailability().equals("empty")){

        }

        BooleanExpression predicate = lecture.isNotNull();

        List<String> days = filterDTO.getDays();
        List<String> startTimes = filterDTO.getStartTimes();
        List<String> endTimes = filterDTO.getEndTimes();

        if(days != null && startTimes != null && endTimes != null){
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            for (int i = 0; i < days.size(); i++){
                DayOfWeekEnum dayOfWeekEnum = DayOfWeekEnum.valueOf(days.get(i));
                LocalTime startTime = LocalTime.parse(startTimes.get(i));
                LocalTime endTime = LocalTime.parse(endTimes.get(i));
                booleanBuilder.or(
                        lectureTime.dayOfWeek.eq(dayOfWeekEnum)
                                .and(lectureTime.startTime.goe(startTime))
                                .and(lectureTime.endTime.loe(endTime))
                );
            }
            predicate = predicate.and(booleanBuilder);
        }
        if (filterDTO.getMajor() != null) {
            predicate = predicate.and(lecture.major.eq(filterDTO.getMajor()));
        }
        if (filterDTO.getGrade() != null && !filterDTO.getGrade().isEmpty()) {
            //체크박스 학년 다중선택시 그 리스트 안에 있는경우 true
            predicate = predicate.and(lecture.grade.in(filterDTO.getGrade()));
        }
        if (filterDTO.getCredit() != null && !filterDTO.getCredit().isEmpty()) {
            predicate = predicate.and(lecture.credit.in(filterDTO.getCredit()));
        }
        if (filterDTO.getLiberalArts() != null && !filterDTO.getLiberalArts().isEmpty()) {
            predicate = predicate.and(lecture.liberalArts.in(filterDTO.getLiberalArts()));
        }

        return predicate;
    }
}
