package com.finpack.schedule;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class DayCount {
    private DayCountTypes dayCountType;
    int[] monthDaysNotLeapYear = new int[] {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    int[] monthDaysLeapYear = new int[] {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public DayCount(DayCountTypes dayCountType){
        this.dayCountType = dayCountType;
    }

    public String toString(){
        return dayCountType.name();
    }

    /**
     * Calculate the year fraction between dates dt1 and dt2 using the
     * specified day count convention.
     * @param dt1
     * @param dt2
     * @return double
     */
    public double yearFrac(LocalDate dt1, LocalDate dt2, Optional<LocalDate> dt3) throws Exception {
        double accFactor = Double.MIN_VALUE;
        int d1 = dt1.getDayOfMonth();
        int d2 = dt2.getDayOfMonth();
        int m1 = dt1.getMonthValue();
        int m2 = dt2.getMonthValue();
        int y1 = dt1.getYear();
        int y2 = dt2.getYear();
        if (this.dayCountType == DayCountTypes.THIRTY_360){
            //double dayDiff = 360.0 * (y2 - y1) + 30.0 * (m2 - m1) + (d2 - d1);
            double dayDiff = ChronoUnit.DAYS.between(dt1,dt2);
            accFactor = dayDiff / 360.0;
        } else if (this.dayCountType == DayCountTypes.THIRTY_360_BOND) {
            d1 = Math.min(d1, 30);
            if (d1 == 31 || d1 == 30)
                d2 = Math.min(d2, 30);
            double dayDiff = 360.0 * (y2 - y1) + 30.0 * (m2 - m1) + (d2 - d1);
            accFactor = dayDiff / 360.0;
        } else if (this.dayCountType == DayCountTypes.THIRTY_E_360) {
            d1 = Math.min(d1, 30);
            d2 = Math.min(d2, 30);
            double dayDiff = 360.0 * (y2 - y1) + 30.0 * (m2 - m1) + (d2 - d1);
            accFactor = dayDiff / 360.0;
        } else if (this.dayCountType == DayCountTypes.THIRTY_E_360_ISDA) {
            if (dt1.isLeapYear()) {
                if (d1 == monthDaysLeapYear[m1 - 1])
                    d1 = 30;
            } else {
                if (d1 == monthDaysNotLeapYear[m1 - 1])
                    d1 = 30;
            }
            if (dt2.isLeapYear()){
                if (d2 == monthDaysLeapYear[m2 - 1] && m2 != 2)
                    d2 = 30;
            } else {
                if (d2 == monthDaysNotLeapYear[m2 - 1] && m2 != 2)
                    d2 = 30;
            }
            double dayDiff = 360.0 * (y2 - y1) + 30.0 * (m2 - m1) + (d2 - d1);
            accFactor = dayDiff / 360.0;
        } else if (this.dayCountType == DayCountTypes.THIRTY_E_360_PLUS_ISDA) {
            d1 = Math.min(d1, 30);
            if (d2 == 31) {
                d2 = 1;
                m2 = m2 + 1;
            }
            double dayDiff = 360.0 * (y2 - y1) + 30.0 * (m2 - m1) + (d2 - d1);
            accFactor = dayDiff / 360.0;
        } else if (this.dayCountType == DayCountTypes.ACT_ACT_ISDA) {
            double denom1, denom2;
            if (dt1.isLeapYear())
                denom1 = 366.0;
            else denom1 = 365.0;

            if (dt2.isLeapYear())
                denom2 = 366.0;
            else denom2 = 365.0;

            if (y1 == y2)
                accFactor = ChronoUnit.DAYS.between(dt1,dt2) / denom1;
            else {
                long daysYear1 = ChronoUnit.DAYS.between(dt1, LocalDate.of(y1+1,1,1));
                long daysYear2 = ChronoUnit.DAYS.between(LocalDate.of(y1+1,1,1), dt2);
                accFactor = daysYear1 / denom1;
                accFactor = accFactor + (daysYear2 / denom2);
            }
        } else if (this.dayCountType == DayCountTypes.ACT_ACT_ICMA) {
            if (dt3.isPresent()){
                long num = ChronoUnit.DAYS.between(dt1, dt2);
                long den = ChronoUnit.DAYS.between(dt1, dt3.get());
                accFactor = num/den;
            } else {
                throw new Exception("ACT_ACT_ICMA requires three dates");
            }

        } else if (this.dayCountType == DayCountTypes.ACT_360) {
            accFactor = ChronoUnit.DAYS.between(dt1, dt2) / 360.0;
        } else if (this.dayCountType == DayCountTypes.ACT_365_FIXED) {
            accFactor = ChronoUnit.DAYS.between(dt1, dt2) / 365.0;
        } else if (this.dayCountType == DayCountTypes.ACT_365_LEAP) {
            double denom = 365.0;
            LocalDate temp = LocalDate.of(y1,2,28);
            LocalDate temp2 = LocalDate.of(y1,2,28);
            if (dt1.isLeapYear() && (dt1.isBefore(temp) || dt1.isEqual(temp)) && dt2.isAfter(temp)) {
                denom = 366.0;
            }
            temp = LocalDate.of(y2,2,28);
            if (dt2.isLeapYear() && (dt1.isBefore(temp) || dt1.isEqual(temp)) && dt2.isAfter(temp)) {
                denom = 366.0;
            }
            accFactor = ChronoUnit.DAYS.between(dt1, dt2)/ denom;
        } else if (this.dayCountType == DayCountTypes.ACT_365_ISDA) {
            double denom1,denom2;
            if (dt1.isLeapYear())
                denom1 = 366.0;
            else denom1 = 365.0;

            if (dt2.isLeapYear())
                denom2 = 366.0;
            else denom2 = 365.0;

            if (y1 == y2) {
                accFactor = ChronoUnit.DAYS.between(dt1, dt2) / denom1;
            } else {
                long daysYear1 = ChronoUnit.DAYS.between(dt1, LocalDate.of(y1 + 1, 1, 1));
                long daysYear2 = ChronoUnit.DAYS.between(LocalDate.of(y1 + 1, 1, 1),dt2);
                accFactor = daysYear1 / denom1;
                accFactor = accFactor + (daysYear2 / denom2);
            }
        }
        return accFactor;
    }
}
