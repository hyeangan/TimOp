package kangnamUni.TimOp.Service;

import kangnamUni.TimOp.domain.Lecture;
import kangnamUni.TimOp.domain.Member;
import kangnamUni.TimOp.domain.Timetable;
import kangnamUni.TimOp.repository.LectureRepository;
import kangnamUni.TimOp.repository.MemberRepository;
import kangnamUni.TimOp.repository.TimetableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Timetable createTimetableForMember(Long memberId, String name) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 중복 이름 검사
        boolean exists = timetableRepository.existsByMemberAndName(member, name);
        if (exists) {
            throw new RuntimeException("시간표 이름이 중복됩니다.");
        }

        Timetable newTimetable = new Timetable();
        newTimetable.setMember(member);
        newTimetable.setName(name); // 설정한 시간표 이름 추가
        return timetableRepository.save(newTimetable);
    }
    public void deleteTimetableForMember(Long memberId, String timetableName) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Timetable timetable = timetableRepository.findByNameAndMember(timetableName, member)
                .orElseThrow(() -> new RuntimeException("Timetable not found"));

        timetableRepository.delete(timetable);
    }
    //개인 시간표에 강의 추가
    // member에 시간표 이름으로 시간표 조회하고 시간표 list에 너어야해
    //리턴 home
    public void addLectureToTimetable(Long memberId, String timetableName, Long lectureId){
        //Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("Member not found"));
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if(optionalMember.isPresent()){
            Member member = optionalMember.get();
            Optional<Timetable> optionalTimetable = timetableRepository.findByNameAndMember(timetableName, member);
            if(optionalTimetable.isPresent()){
                Timetable timetable = optionalTimetable.get();

                Optional<Lecture> optionalLecture = lectureRepository.findById(lectureId);
                if(optionalLecture.isPresent()){
                    Lecture lecture = optionalLecture.get();
                    timetable.addLecture(lecture);
                }
                else{
                    System.out.println("lecture not found");
                }
            }

        }
        else{
            System.out.println("User not found");
        }

    }
}
