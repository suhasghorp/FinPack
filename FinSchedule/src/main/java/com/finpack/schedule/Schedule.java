package com.finpack.schedule;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Schedule {
    LocalDate startDate, endDate;
    FrequencyTypes frequencyTypes;
    CalendarTypes calendarTypes;
    DayAdjustTypes dayAdjustTypes;
    DateGenRuleTypes dateGenRuleTypes;
    List<LocalDate> adjustedDates = new ArrayList<>();

    public Schedule(LocalDate startDate, LocalDate endDate, FrequencyTypes frequencyTypes,
                    CalendarTypes calendarTypes,DayAdjustTypes dayAdjustTypes, DateGenRuleTypes dateGenRuleTypes )
                    throws Exception {
        if (startDate.isAfter(endDate))
            throw new Exception("Start Date after End Date");
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequencyTypes = frequencyTypes;
        this.calendarTypes = calendarTypes;
        this.dayAdjustTypes = dayAdjustTypes;
        this.dateGenRuleTypes = dateGenRuleTypes;
    }

    public List<LocalDate> generate(){
        int numOfMonths = 12/frequencyTypes.getFrequency();
        Calendar calendar = new Calendar(calendarTypes);
        List<LocalDate> unadjustedDates = new ArrayList<>();
        if (this.dateGenRuleTypes == DateGenRuleTypes.BACKWARD){
            LocalDate nextDate = endDate;
            int flowNum = 0;

            while (nextDate.isAfter(startDate)) {
                unadjustedDates.add(nextDate);
                nextDate = nextDate.plusMonths(-numOfMonths);
                flowNum += 1;
            }

            //Add on the Previous Coupon Date
            unadjustedDates.add(nextDate);
            flowNum += 1;

            //reverse order and holiday adjust dates
            for (int i = 0; i < flowNum; i++) {
                LocalDate dt = calendar.adjust(unadjustedDates.get(flowNum - i - 1), dayAdjustTypes);
                adjustedDates.add(dt);
            }
        } else if (this.dateGenRuleTypes == DateGenRuleTypes.FORWARD){
            LocalDate nextDate = startDate;
            int flowNum = 0;
            unadjustedDates.add(nextDate);
            flowNum = 1;

            while (nextDate.isBefore(endDate)) {
                unadjustedDates.add(nextDate);
                nextDate = nextDate.plusMonths(numOfMonths);
                flowNum = flowNum + 1;
            }

            for (int i = 1; i < flowNum; i++) {
                LocalDate dt = calendar.adjust(unadjustedDates.get(i), dayAdjustTypes);
                adjustedDates.add(dt);
            }
            adjustedDates.add(endDate);
        }
        return adjustedDates;
    }
    public void print(){
        System.out.println("START DATE:" + DateTimeFormatter.ofPattern("MM/dd/yyyy").format(startDate));
        System.out.println("END DATE:"+ DateTimeFormatter.ofPattern("MM/dd/yyyy").format(endDate));
        System.out.println("FREQUENCY:" + frequencyTypes);
        System.out.println("CALENDAR:"+ calendarTypes);
        System.out.println("BUSDAYRULE:"+dayAdjustTypes);
        System.out.println("DATEGENRULE:"+dateGenRuleTypes);
        System.out.println("");

        if (adjustedDates.size() > 0)
            System.out.println("PCD:"+ DateTimeFormatter.ofPattern("MM/dd/yyyy").format(adjustedDates.get(0)));

        if (adjustedDates.size() > 1)
            System.out.println("NCD:"+ DateTimeFormatter.ofPattern("MM/dd/yyyy").format(adjustedDates.get(1)));

        if (adjustedDates.size() > 2) {
            for (LocalDate dt: adjustedDates.subList(2, adjustedDates.size()))
                System.out.println("    " + DateTimeFormatter.ofPattern("MM/dd/yyyy").format(dt));
        }
    }
}
