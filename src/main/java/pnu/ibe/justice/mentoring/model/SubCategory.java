package pnu.ibe.justice.mentoring.model;


import lombok.Getter;

@Getter
public enum SubCategory {
    CURRICULUM_1("1차 커리큘럼"),
    WEEK_1_REPORT("1주차 보고서"),
    WEEK_2_REPORT("2주차 보고서"),
    WEEK_3_REPORT("3주차 보고서"),
    WEEK_4_REPORT("4주차 보고서"),
    WEEK_5_REPORT("5주차 보고서"),
    WEEK_6_REPORT("6주차 보고서"),
    MIDTERM_REPORT("중간 보고서"),
    CURRICULUM_2("2차 커리큘럼"),
    WEEK_7_REPORT("7주차 보고서"),
    WEEK_8_REPORT("8주차 보고서"),
    WEEK_9_REPORT("9주차 보고서"),
    WEEK_10_REPORT("10주차 보고서"),
    WEEK_11_REPORT("11주차 보고서"),
    WEEK_12_REPORT("12주차 보고서"),
    FINAL_REPORT("최종 보고서"),
    PRESENTATION("발표 보고서");

    private final String displayName;

    SubCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
    public static SubCategory fromName(String name) {
        for (SubCategory category : values()) {
            if (category.name().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return null; // 또는 기본값을 반환할 수 있습니다.
    }
}
