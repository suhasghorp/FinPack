package com.finpack.schedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class Calendar {
    private static int[] easterMondayDay = new int[] {98, 90, 103, 95, 114, 106, 91, 111, 102, 87,
            107, 99, 83, 103, 95, 115, 99, 91, 111, 96, 87,
            107, 92, 112, 103, 95, 108, 100, 91,
            111, 96, 88, 107, 92, 112, 104, 88, 108, 100,
            85, 104, 96, 116, 101, 92, 112, 97, 89, 108,
            100, 85, 105, 96, 109, 101, 93, 112, 97, 89,
            109, 93, 113, 105, 90, 109, 101, 86, 106, 97,
            89, 102, 94, 113, 105, 90, 110, 101, 86, 106,
            98, 110, 102, 94, 114, 98, 90, 110, 95, 86,
            106, 91, 111, 102, 94, 107, 99, 90, 103, 95,
            115, 106, 91, 111, 103, 87, 107, 99, 84, 103,
            95, 115, 100, 91, 111, 96, 88, 107, 92, 112,
            104, 95, 108, 100, 92, 111, 96, 88, 108, 92,
            112, 104, 89, 108, 100, 85, 105, 96, 116, 101,
            93, 112, 97, 89, 109, 100, 85, 105, 97, 109,
            101, 93, 113, 97, 89, 109, 94, 113, 105, 90,
            110, 101, 86, 106, 98, 89, 102, 94, 114, 105,
            90, 110, 102, 86, 106, 98, 111, 102, 94, 114,
            99, 90, 110, 95, 87, 106, 91, 111, 103, 94,
            107, 99, 91, 103, 95, 115, 107, 91, 111, 103,
            88, 108, 100, 85, 105, 96, 109, 101, 93, 112,
            97, 89, 109, 93, 113, 105, 90, 109, 101, 86,
            106, 97, 89, 102, 94, 113, 105, 90, 110, 101,
            86, 106, 98, 110, 102, 94, 114, 98, 90, 110,
            95, 86, 106, 91, 111, 102, 94, 107, 99, 90,
            103, 95, 115, 106, 91, 111, 103, 87, 107, 99,
            84, 103, 95, 115, 100, 91, 111, 96, 88, 107,
            92, 112, 104, 95, 108, 100, 92, 111, 96, 88,
            108, 92, 112, 104, 89, 108, 100, 85, 105, 96,
            116, 101, 93, 112, 97, 89, 109, 100, 85, 105};
    private CalendarTypes calendarType;
    public Calendar(CalendarTypes calendarType){
        this.calendarType = calendarType;
    }
    public LocalDate adjust(LocalDate dt, DayAdjustTypes dayAdjustType){

        int m = dt.getMonthValue();
        if (dayAdjustType == DayAdjustTypes.NONE) {
            return dt;
        } else if (dayAdjustType == DayAdjustTypes.FOLLOWING){
            while (!isBusinessDay(dt))
                dt = dt.plusDays(1);
            return dt;
        } else if (dayAdjustType == DayAdjustTypes.MODIFIED_FOLLOWING) {
            // step forward until we find a business day
            while (!isBusinessDay(dt)) {
                dt = dt.plusDays(1);
            }
            // if the business day is in a different month look back
            // for previous first business day one day at a time I could speed this up by starting it at initial date
            if (dt.getMonthValue() != m) {
                while (!isBusinessDay(dt)) {
                    dt = dt.plusDays(-1);
                }
            }
            return dt;
        } else if (dayAdjustType == DayAdjustTypes.PRECEDING) {
            while (!isBusinessDay(dt)) {
                dt = dt.plusDays(-1);
            }
            return dt;
        } else if (dayAdjustType == DayAdjustTypes.MODIFIED_PRECEDING) {
            while (!isBusinessDay(dt)) {
                dt = dt.plusDays(-1);
            }

            //if the business day is in a different month look forward
            //for previous first business day one day at a time
            //I could speed this up by starting it at initial date
            if (dt.getMonthValue() != m) {
                while (!isBusinessDay(dt)) {
                    dt = dt.plusDays(1);
                }
            }
            return dt;
        }
        return LocalDate.MAX;
    }

    private int getExcelDate(LocalDate dt){
        return Period.between(dt, LocalDate.of(1900,1,1)).getDays();
    }
    private boolean isWeekend(LocalDate dt){
        return dt.getDayOfWeek() == DayOfWeek.SATURDAY || dt.getDayOfWeek() == DayOfWeek.SUNDAY;
    }
    private LocalDate easterMonday(int y) throws Exception {
        if (y > 2100)
            throw new Exception("we do not handle dates greater than 2100");
        int emDays = easterMondayDay[y - 1901];
        LocalDate startDate = LocalDate.of(y,1,1);
        return startDate.plusDays(emDays-1);
    }
    public boolean isBusinessDay(LocalDate dt){
        int y = dt.getYear();
        int m = dt.getMonthValue();
        int d = dt.getDayOfMonth();
        LocalDate startDate = LocalDate.of(dt.getYear(),1,1);
        int dd = getExcelDate(dt) - getExcelDate(startDate) + 1;
        DayOfWeek weekday = dt.getDayOfWeek();
        int em = easterMondayDay[dt.getYear() - 1901];

        if (this.calendarType == CalendarTypes.NONE)
            return true;
        if (isWeekend(dt))
            return false;
        if (this.calendarType == CalendarTypes.WEEKEND)
            return true;
        if (this.calendarType == CalendarTypes.UK){
            if (m == 1 && d == 1) //new years day
                return false;

            if (dd == em)  //Easter Monday
                return false;

            if (dd == em - 3)  // good friday
                return false;

            if (m == 5 && d <= 7 && weekday == DayOfWeek.MONDAY)
                return false;

            if (m == 5 && d >= 25 && weekday == DayOfWeek.MONDAY)
                return false;

            if (m == 8 && d > 24 && weekday == DayOfWeek.MONDAY)
                return false;

            if (m == 12 && d == 25)
                return false;

            if (m == 12 && d == 26)
                return false;

            if (m == 12 && d == 27 && weekday == DayOfWeek.MONDAY)
                return false;

            if (m == 12 && d == 27 && weekday == DayOfWeek.TUESDAY)
                return false;

            if (m == 12 && d == 28 && weekday == DayOfWeek.MONDAY)
                return false;

            if (m == 12 && d == 28 && weekday == DayOfWeek.TUESDAY)
                return false;

            return true;
        }
        if (this.calendarType == CalendarTypes.US){

            if (m == 1 && d == 1)
                return false;

            if (m == 1 && d >= 15 && d < 22 && weekday == DayOfWeek.MONDAY)
                return false;

            if (m == 2 && d >= 15 && d < 22 && weekday == DayOfWeek.MONDAY)
                return false;

            if (m == 5 && d >= 25 && d <= 31 && weekday == DayOfWeek.MONDAY)
                return false;

            if (m == 7 && d == 4)
                return false;

            if (m == 7 && d == 5 && weekday == DayOfWeek.MONDAY)
                return false;

            if (m == 7 && d == 3 && weekday == DayOfWeek.FRIDAY)
                return false;

            if (m == 9 && d >= 1 && d < 8 && weekday == DayOfWeek.MONDAY)
                return false;

            if (m == 10 && d >= 8 && d < 15 && weekday == DayOfWeek.MONDAY)
                return false;

            if (m == 11 && d == 11)
                return false;

            if (m == 11 && d == 12 && weekday == DayOfWeek.MONDAY)
                return false;

            if (m == 11 && d == 10 && weekday == DayOfWeek.FRIDAY)
                return false;

            if (m == 11 && d >= 22 && d < 29 && weekday == DayOfWeek.THURSDAY)
                return false;

            if (m == 12 && d == 25)
                return false;

            return true;
        }
        if (this.calendarType == CalendarTypes.TARGET){
            if (m == 1 && d == 1)
                return false;

            if (m == 5 && d == 1)
                return false;

            if (dd == em - 3)
                return false;

            if (dd == em)  // Easter monday holiday
                return false;

            if (m == 12 && d == 25)  // Xmas bank holiday
                return false;

            if (m == 12 && d == 26)  // Xmas bank holiday
                return false;

            return true;
        }
        return true;
    }

    public List<LocalDate> getHolidayList(int y){
        LocalDate startDate = LocalDate.of(y,1,1);
        LocalDate endDate = LocalDate.of(y+1,1,1);
        List<LocalDate> holidayList = new ArrayList<>();
        while (startDate.isBefore(endDate)) {
            if (!isBusinessDay(startDate) && !isWeekend(startDate))
                holidayList.add(startDate);
            startDate = startDate.plusDays(1);
        }
        return holidayList;
    }

}
