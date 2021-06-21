package tests.schedule;

import com.finpack.schedule.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScheduleTest {
    @Test
    void testSchedule(){
        LocalDate d1 = LocalDate.of(2018, 6, 20);
        LocalDate d2 = LocalDate.of(2028, 6, 20);

        try {
            Schedule schedule = new Schedule.Builder(d1,d2)
                    .withFrequency(FrequencyTypes.SEMI_ANNUAL)
                    .withCalendar(CalendarTypes.WEEKEND)
                    .withDayAdjust(DayAdjustTypes.FOLLOWING)
                    .withDateGenRule(DateGenRuleTypes.BACKWARD).build();
            System.out.println("SEMI-ANNUAL FREQUENCY");
            for (LocalDate dt : schedule.getAdjustedDates())
                System.out.println(DateTimeFormatter.ofPattern("MM/dd/yyyy").format(dt));

            System.out.println("======================================");

            schedule = new Schedule.Builder(d1,d2)
                    .withFrequency(FrequencyTypes.QUARTERLY)
                    .withCalendar(CalendarTypes.WEEKEND)
                    .withDayAdjust(DayAdjustTypes.FOLLOWING)
                    .withDateGenRule(DateGenRuleTypes.BACKWARD).build();
            System.out.println("QUARTERLY FREQUENCY");
            for (LocalDate dt : schedule.getAdjustedDates())
                System.out.println(DateTimeFormatter.ofPattern("MM/dd/yyyy").format(dt));


        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
