package kangnamUni.TimOp.domain;

public enum DayOfWeekEnum {
    월, 화, 수, 목, 금;

    public static DayOfWeekEnum fromString(String day) {
        switch (day) {
            case "월": return 월;
            case "화": return 화;
            case "수": return 수;
            case "목": return 목;
            case "금": return 금;
            default: throw new IllegalArgumentException("Invalid day: " + day);
        }
    }
}

