package com.example.alarmjava20;

import java.util.List;

public class DaysOfWeek {
    public static final int SUNDAY = 1;
    public static final int MONDAY = 2;
    public static final int TUESDAY = 3;
    public static final int WEDNESDAY = 4;
    public static final int THURSDAY = 5;
    public static final int FRIDAY = 6;
    public static final int SATURDAY = 7;

    public static final int DAYS_IN_WEEK = 7;

    public static String getDayName(int day) {
        switch (day) {
            case SUNDAY:
                return "Вс";
            case MONDAY:
                return "Пн";
            case TUESDAY:
                return "Вт";
            case WEDNESDAY:
                return "Ср";
            case THURSDAY:
                return "Чт";
            case FRIDAY:
                return "Пт";
            case SATURDAY:
                return "Сб";
            default:
                return "";
        }
    }
    public static int calculateDaysToAdd(int currentDay, List<Integer> selectedDays) {
        for (int i = 1; i <= DAYS_IN_WEEK; i++) {
            int nextDay = (currentDay + i) % (DAYS_IN_WEEK + 1);
            if (selectedDays.contains(nextDay)) {
                return i;
            }
        }
        return 0;
    }
}
