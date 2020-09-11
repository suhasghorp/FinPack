package com.finpack.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.stream.IntStream;

public class DateUtils {

        public static LocalDate addTenor(LocalDate dt, String tenor) throws Exception{
            tenor = tenor.toUpperCase();
            String periodType = tenor.substring(tenor.length() - 1);
            int numOfPeriods = Integer.parseInt(tenor.substring(0, tenor.length()-1));
            LocalDate newDate = dt;
            switch (periodType){
                case "D":
                    return newDate.plusDays(1 * numOfPeriods);
                case "W":
                    return newDate.plusDays(7 * numOfPeriods);
                case "M":
                    return newDate.plusMonths(numOfPeriods);
                case "Y":
                    return newDate.plusYears(numOfPeriods);
                default:
                    throw new Exception("Unknown Tenor");
            }
        }

        public static LocalDate addWorkdays(LocalDate dt, int numDays){
            LocalDate newDate = dt;
            while (numDays > 0) {
                newDate = newDate.plusDays(1);
                if (newDate.getDayOfWeek() != DayOfWeek.SATURDAY || newDate.getDayOfWeek() != DayOfWeek.SUNDAY){
                    numDays = numDays - 1;
                }
            }
            return newDate;
        }
        public static LocalDate nextIMMDate(LocalDate dt) throws Exception {
            int y = dt.getYear();
            int m = dt.getMonthValue();
            int d = dt.getDayOfMonth();

            int y_imm = y;
            int m_imm = 0;

            if (m == 12 && d >= thirdWednesdayOfMonth(m, y)) {
                m_imm = 3;
                y_imm = y + 1;
            } else if (m == 10 || m == 11 || m == 12){
                m_imm = 12;
            } else if (m == 9 && d >= thirdWednesdayOfMonth(m, y)){
                m_imm = 12;
            } else if (m == 7 || m == 8 || m == 9){
                m_imm = 9;
            } else if (m == 6 && d >= thirdWednesdayOfMonth(m, y)){
                m_imm = 9;
            } else if (m == 4 || m == 5 || m == 6){
                m_imm = 6;
            } else if (m == 3 && d >= thirdWednesdayOfMonth(m, y)){
                m_imm = 6;
            } else if (m == 1 || m == 2 || m == 3){
                m_imm = 3;
            }
            int d_imm = thirdWednesdayOfMonth(m_imm, y_imm);
            LocalDate immDate = LocalDate.of(y_imm, m_imm, d_imm);
            return immDate;
        }

        /*For a specific month and year this returns the day number of the
            3rd Wednesday by scanning through dates in the third week.*/
        public static int thirdWednesdayOfMonth(int m, int y) throws Exception{
            int d_start = 14;
            int d_end = 21;
            for (int d = d_start; d < d_end; d++) {
                if (LocalDate.of(y, m, d).getDayOfWeek() == DayOfWeek.WEDNESDAY)
                    return d;
            }
            //Should never reach this line but just to be defensive
            throw new Exception("Third Wednesday not found");
        }

}
