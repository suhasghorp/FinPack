package com.finpack.schedule;

import com.finpack.utils.DateUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private final LocalDate startDate, endDate;
    private final FrequencyTypes frequencyTypes;
    private final CalendarTypes calendarTypes;
    private final DayAdjustTypes dayAdjustTypes;
    private final DateGenRuleTypes dateGenRuleTypes;
    private final List<LocalDate> adjustedDates = new ArrayList<>();

    public static class Builder {
        private final LocalDate startDate, endDate;
        private FrequencyTypes frequencyTypes = FrequencyTypes.SEMI_ANNUAL;
        private CalendarTypes calendarTypes = CalendarTypes.US;
        private DayAdjustTypes dayAdjustTypes = DayAdjustTypes.MODIFIED_FOLLOWING;
        private DateGenRuleTypes dateGenRuleTypes = DateGenRuleTypes.BACKWARD;
        public Builder(LocalDate startDate, LocalDate endDate){
            if (startDate.isAfter(endDate))
                throw new IllegalArgumentException("Start Date after End Date");
            this.startDate = startDate;
            this.endDate = endDate;
        }
        public Builder withFrequency(FrequencyTypes frequencyTypes){
            this.frequencyTypes = frequencyTypes;
            return this;
        }
        public Builder withCalendar(CalendarTypes calendarTypes){
            this.calendarTypes = calendarTypes;
            return this;
        }
        public Builder withDayAdjust(DayAdjustTypes dayAdjustTypes){
            this.dayAdjustTypes = dayAdjustTypes;
            return this;
        }
        public Builder withDateGenRule(DateGenRuleTypes dateGenRuleTypes){
            this.dateGenRuleTypes = dateGenRuleTypes;
            return this;
        }
        public Schedule build(){
            return new Schedule(this);
        }
    }

    private Schedule(Builder builder){
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.frequencyTypes = builder.frequencyTypes;
        this.calendarTypes = builder.calendarTypes;
        this.dayAdjustTypes = builder.dayAdjustTypes;
        this.dateGenRuleTypes = builder.dateGenRuleTypes;
    }

    public List<LocalDate> getAdjustedDates(){
        generate();
        return new ArrayList<>(adjustedDates);
    }

    public void generate(){
        int numOfMonths = 12/frequencyTypes.getFrequency();
        Calendar calendar = new Calendar(calendarTypes);
        List<LocalDate> unadjustedDates = new ArrayList<>();
        if (this.dateGenRuleTypes == DateGenRuleTypes.BACKWARD){
            LocalDate nextDate = endDate;
            int flowNum = 0;

            while (nextDate.isAfter(startDate)) {
                unadjustedDates.add(nextDate);
                nextDate = DateUtils.addWorkdays(nextDate.plusMonths(-numOfMonths),-2);
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
