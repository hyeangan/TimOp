package kangnamUni.TimOp.Service;

import jakarta.transaction.Transactional;
import kangnamUni.TimOp.domain.Lecture;
import kangnamUni.TimOp.domain.Member;
import kangnamUni.TimOp.domain.QMember;
import kangnamUni.TimOp.domain.Timetable;
import kangnamUni.TimOp.repository.LectureRepository;
import kangnamUni.TimOp.repository.MemberRepository;
import kangnamUni.TimOp.repository.TimetableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static kangnamUni.TimOp.domain.QMember.member;

@Service
public class TimetableService {
    @Autowired
    TimetableRepository timetableRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private LectureRepository lectureRepository;

    public Timetable findByNameAndMember(String timetableName, Member member){
        Optional<Timetable> timetable = timetableRepository.findByNameAndMember(timetableName, member);
        return timetable.orElseThrow(() -> new RuntimeException("Lecture not found with id: " + timetableName));
    }
    public Timetable createTimetableForMember(Long memberId, String timetableName) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        // 중복 이름 검사
        boolean exists = timetableRepository.existsByMemberAndName(member, timetableName);
        if (exists) {
            throw new RuntimeException("시간표 이름이 중복됩니다.");
        }

        Timetable newTimetable = new Timetable();
        newTimetable.setName(timetableName); // 설정한 시간표 이름 추가

        member.addTimetable(newTimetable);
        memberRepository.save(member);
        return newTimetable;
    }
    public void deleteTimetableForMember(Long memberId, String timetableName) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Timetable timetable = timetableRepository.findByNameAndMember(timetableName, member)
                .orElseThrow(() -> new RuntimeException("Timetable not found"));
        member.removeTimetable(timetable);
        timetableRepository.delete(timetable);
    }
    @Transactional
    //개인 시간표에 강의 추가
    // member에 시간표 이름으로 시간표 조회하고 시간표 list에 너어야해
    //리턴 home
    public void addLectureToTimetable(Long memberId, String timetableName, Long lectureId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        Member member = optionalMember.get();

        Optional<Timetable> optionalTimetable = timetableRepository.findByNameAndMember(timetableName, member);
        if (optionalTimetable.isEmpty()) {
            throw new RuntimeException("Timetable not found");
        }
        Timetable timetable = optionalTimetable.get();

        Optional<Lecture> optionalLecture = lectureRepository.findById(lectureId);
        if (optionalLecture.isEmpty()) {
            throw new RuntimeException("Lecture not found");
        }
        Lecture lecture = optionalLecture.get();

        // 강의가 이미 추가되어 있는지 확인 (중복 방지)
        if (timetable.getLectures().contains(lecture)) {
            throw new RuntimeException("Lecture is already in the timetable.");
        }

        // 강의를 시간표에 추가
        timetable.addLecture(lecture);

        timetableRepository.save(timetable);  // 시간표 변경 사항 저장
        lectureRepository.save(lecture);      // 강의 변경 사항 저장
    }
    public List<Timetable> findByMemberId(Long memberId){
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if(optionalMember.isEmpty()){
            throw new RuntimeException("Member not found");
        }
        Member member = optionalMember.get();
        return timetableRepository.findByMember(member);
    }
    public boolean existsByName(String name, Long memberId){
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if(optionalMember.isEmpty()){
            throw new RuntimeException("Member not found");
        }
        Member member = optionalMember.get();
        return timetableRepository.existsByMemberAndName(member, name);
    }
}
