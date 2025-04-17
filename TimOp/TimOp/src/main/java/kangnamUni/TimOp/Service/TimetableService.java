package kangnamUni.TimOp.Service;

import jakarta.transaction.Transactional;
import kangnamUni.TimOp.domain.*;
import kangnamUni.TimOp.dto.LectureDTO;
import kangnamUni.TimOp.dto.LectureTimeDTO;
import kangnamUni.TimOp.dto.TimetableDTO;
import kangnamUni.TimOp.repository.LectureRepository;
import kangnamUni.TimOp.repository.MemberRepository;
import kangnamUni.TimOp.repository.TimetableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Transactional
    public Timetable deleteTimetableLecture(Long memberId, String timetableName, Long lectureId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Timetable timetable = timetableRepository.findByNameAndMember(timetableName, member)
                .orElseThrow(() -> new RuntimeException("Timetable not found"));
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new RuntimeException("Lecture not found"));
        // ManyToMany 관계에서 강의를 제거
        timetable.getLectures().remove(lecture);
        lecture.getTimetables().remove(timetable); // 반대 방향에서도 제거

        // 변경 사항 저장
        timetableRepository.save(timetable);
        lectureRepository.save(lecture);

        return timetable;
    }

    @Transactional
    public void deleteTimetableForMember(Long memberId, String timetableName) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Timetable timetable = timetableRepository.findByNameAndMember(timetableName, member)
                .orElseThrow(() -> new RuntimeException("Timetable not found"));

        member.removeTimetable(timetable); //orphanRemoval = true DELETE실행 변경감지로 save
        timetableRepository.delete(timetable); // manytomany에서 delete호출시 중간 관계가 자동 삭제됨
    }


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
        timetable.getLectures().add(lecture);
        timetableRepository.save(timetable);  // 시간표 변경 사항 저장
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

    //시간표 표시를 위한
    public List<TimetableDTO> getAllTimetablesInfo() {
        List<Timetable> timetables = timetableRepository.findAll();

        return timetables.stream()
                .map(timetable -> new TimetableDTO(
                        timetable.getId(),
                        timetable.getName(),
                        timetable.getLectures().stream()
                                .map(lecture -> new LectureDTO(
                                        lecture.getId(),
                                        lecture.getTitle(),
                                        lecture.getProfessor(),
                                        lecture.getLectureTimes().stream()
                                                .map(lt -> new LectureTimeDTO(
                                                        lt.getDayOfWeek().toString(),
                                                        lt.getStartTime(),
                                                        lt.getEndTime()))
                                                .collect(Collectors.toList())
                                ))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }
    public TimetableDTO convertTimetableDTO(Timetable timetable){
        TimetableDTO timetableDTO = new TimetableDTO(
                timetable.getId(),
                timetable.getName(),
                timetable.getLectures().stream()
                        .map(lecture -> new LectureDTO(
                                lecture.getId(),
                                lecture.getTitle(),
                                lecture.getProfessor(),
                                lecture.getLectureTimes().stream()
                                        .map(lt -> new LectureTimeDTO(
                                                lt.getDayOfWeek().toString(),
                                                lt.getStartTime(),
                                                lt.getEndTime()
                                        ))
                                        .collect(Collectors.toList())
                        ))
                        .collect(Collectors.toList())
        );
        return timetableDTO;
    }
    public List<LectureDTO> convertTimetableLectureDTOs(Timetable timetable){
        List<LectureDTO> lectureDTOs = timetable.getLectures().stream()
                .map(lecture -> new LectureDTO(
                        lecture.getId(),
                        lecture.getTitle(),
                        lecture.getProfessor(),
                        lecture.getLectureTimes().stream()
                                .map(lectureTime -> new LectureTimeDTO(lectureTime.getDayOfWeek().toString(),
                                        lectureTime.getStartTime(),
                                        lectureTime.getEndTime()))
                                .collect(Collectors.toList()) // ✅ `List<LectureTimeDTO>` 변환 후 저장
                ))
                .collect(Collectors.toList());
        return lectureDTOs;
    }
}
