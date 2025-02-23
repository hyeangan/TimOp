package kangnamUni.TimOp.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeRangeConverter {
    public static Map<String, String> parseTimeSlots() {
        Map<String, String> timeSlots = new HashMap<>();
        timeSlots.put("1a", "09:00 - 09:25"); timeSlots.put("1b", "09:25 - 09:50"); timeSlots.put("1ab", "09:00 - 09:50");
        timeSlots.put("2a", "09:50 - 10:15"); timeSlots.put("2b", "10:25 - 10:50"); timeSlots.put("2ab", "09:50 - 10:50");
        timeSlots.put("3a", "10:50 - 11:15"); timeSlots.put("3b", "11:15 - 11:40"); timeSlots.put("3ab", "10:50 - 11:40");
        timeSlots.put("4a", "11:50 - 12:15"); timeSlots.put("4b", "12:15 - 12:40"); timeSlots.put("4ab", "11:50 - 12:40");
        timeSlots.put("5a", "12:40 - 13:05"); timeSlots.put("5b", "13:15 - 13:40"); timeSlots.put("5ab", "12:40 - 13:40");
        timeSlots.put("6a", "13:40 - 14:05"); timeSlots.put("6b", "14:05 - 14:30"); timeSlots.put("6ab", "13:40 - 14:30");
        timeSlots.put("7a", "14:40 - 15:05"); timeSlots.put("7b", "15:05 - 15:30"); timeSlots.put("7ab", "14:40 - 15:30");
        timeSlots.put("8a", "15:30 - 15:55"); timeSlots.put("8b", "16:05 - 16:30"); timeSlots.put("8ab", "15:30 - 16:30");
        timeSlots.put("9a", "16:30 - 16:55"); timeSlots.put("9b", "16:55 - 17:20"); timeSlots.put("9ab", "16:30 - 17:20");

        timeSlots.put("10a", "17:30 - 17:55"); timeSlots.put("10b", "17:55 - 18:20"); timeSlots.put("10ab", "17:30 - 18:20");
        timeSlots.put("11a", "18:20 - 18:45"); timeSlots.put("11b", "18:45 - 19:10"); timeSlots.put("11ab", "18:20 - 19:10");
        timeSlots.put("12a", "19:10 - 19:35"); timeSlots.put("12b", "19:35 - 20:00"); timeSlots.put("12ab", "19:10 - 20:00");
        timeSlots.put("13", "20:10 - 20:35"); timeSlots.put("13b", "20:35 - 21:00"); timeSlots.put("13ab", "20:10 - 21:00");
        timeSlots.put("14", "21:00 - 21:25"); timeSlots.put("14b", "21:25 - 21:50"); timeSlots.put("14ab", "21:00 - 21:50");
        timeSlots.put("15", "21:50 - 22:15"); timeSlots.put("15b", "22:15 - 22:40"); timeSlots.put("15ab", "21:50 - 22:40");

        return timeSlots;
    }
    //(주)월8a, (주)월8ab, (주)월8b, (주)화1ab2ab, (주)화3b4ab, (주)화3ab4a,수7ab8a, (주)화4ab5a,수5b6ab /(야)화111213 ababab *2개 쪼개지는거*아니면 다 ab이긴한듯 + 별도배정이나 시간없는거
    //(주)화4ab5a,수5b6ab -> ,가 있으면 스플릿 하고
    //나중에 스크래퍼에서 공백 별도지정 이런거 걸러야겠다
    public static Map<String, String> convertTimeRanges(String lectureTime) {
        String dayOfWeek;
        //주야 구분해서 야-> ab붙여서 진행 아래 그대로
        //lectureTime = lectureTime.substring(3); //(주) (야) 삭제 주야 , 구분까지 다 하고 보내


        Map<String, String> dayStartTimeEndTime = new HashMap<>(); //리턴 요일 시작시간 끝나는 시간
        Map<String, String> timeSlots = parseTimeSlots();
        List<String> lectureTimes = new ArrayList<>();

        dayOfWeek = Character.toString(lectureTime.charAt(0)); //요일 저장
        lectureTime = lectureTime.substring(1); // 요일 삭제
        String sliceTime = "";

        //다음 숫자가 나오거나 끝이거나 sliceTime이 ""가 아니라면  dictionaly에서 찾아서 넣고 아니면 sliceTime 에 넣고
        //
        for (int i = 0; i < lectureTime.length(); i++){
            char ch = lectureTime.charAt(i);
            if(i == lectureTime.length() - 1){
                sliceTime += ch;
                lectureTimes.add(timeSlots.get(sliceTime));
            }
            else if(sliceTime != "" && Character.isDigit(ch) && !Character.isDigit(sliceTime.charAt(sliceTime.length()-1))){
                System.out.println("더한 슬라이스 타임 값=" + sliceTime);
                lectureTimes.add(timeSlots.get(sliceTime));
                System.out.println("add");
                sliceTime = "" + ch;
            }
            else{
                sliceTime += ch;
            }
        }
        for (String s : lectureTimes){
            System.out.println("변환 lectureTime=" + s);
        }
        System.out.println("dayOfWeek=" + dayOfWeek);
        if(lectureTimes.size() == 1){
            String tmp = lectureTimes.get(0);
            dayStartTimeEndTime.put("day", dayOfWeek);
            dayStartTimeEndTime.put("startTime", tmp.substring(0, 5));
            dayStartTimeEndTime.put("endTime", tmp.substring(8));
        }
        else{
            String start = lectureTimes.get(0);
            String end = lectureTimes.get(lectureTimes.size()-1);
            dayStartTimeEndTime.put("day", dayOfWeek);
            dayStartTimeEndTime.put("startTime", start.substring(0,5));
            dayStartTimeEndTime.put("endTime", end.substring(8));
        }
        return dayStartTimeEndTime;
    }
}
