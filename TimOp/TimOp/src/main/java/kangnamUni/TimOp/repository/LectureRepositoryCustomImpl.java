package kangnamUni.TimOp.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kangnamUni.TimOp.domain.Lecture;
import kangnamUni.TimOp.domain.LectureFilterDTO;

import java.util.ArrayList;
import java.util.List;

import static kangnamUni.TimOp.domain.QLecture.lecture;


public class LectureRepositoryCustomImpl implements LectureRepositoryCustom{
    @PersistenceContext //EntityManager를 빈으로 주입할 때 사용하는 어노테이션
    private EntityManager entityManager;

    @Override
    public List<Lecture> findLectures(LectureFilterDTO filterDTO) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        BooleanExpression predicate = createPredicate(filterDTO);
        return queryFactory
                .selectFrom(lecture)
                .where(predicate)
                .fetch();
    }
    private BooleanExpression createPredicate(LectureFilterDTO filterDTO) {
        BooleanExpression predicate = lecture.isNotNull();
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
