package schedule;

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
        FrequencyTypes frequencyType = FrequencyTypes.SEMI_ANNUAL;
        CalendarTypes calendarType = CalendarTypes.WEEKEND;
        DayAdjustTypes dayAdjustType = DayAdjustTypes.FOLLOWING;
        DateGenRuleTypes dateGenRuleType = DateGenRuleTypes.BACKWARD;

        try {
            Schedule schedule = new Schedule(
                    d1,
                    d2,
                    frequencyType,
                    calendarType,
                    dayAdjustType,
                    dateGenRuleType);
            System.out.println("SEMI-ANNUAL FREQUENCY");
            for (LocalDate dt : schedule.adjustedDates)
                System.out.println(DateTimeFormatter.ofPattern("MM/dd/yyyy").format(dt));

            System.out.println("======================================");

            frequencyType = FrequencyTypes.QUARTERLY;
            schedule = new Schedule(
                    d1,
                    d2,
                    frequencyType,
                    calendarType,
                    dayAdjustType,
                    dateGenRuleType);
            System.out.println("QUARTERLY FREQUENCY");
            for (LocalDate dt : schedule.adjustedDates)
                System.out.println(DateTimeFormatter.ofPattern("MM/dd/yyyy").format(dt));


        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
