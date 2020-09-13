package schedule;

import com.finpack.schedule.DayCount;
import com.finpack.schedule.DayCountTypes;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DayCountTest {
    @Test
    void testDayCount(){
        System.out.println("DAY_COUNT_METHOD" + "START" + "END" + "ALPHA");

        for (DayCountTypes types : DayCountTypes.values()){
            LocalDate startDate = LocalDate.of(2019, 1, 1);
            final LocalDate[] nextDate = {startDate};
            int numDays = 10;
            DayCount dayCount = new DayCount(types);
            try {
                IntStream.range(0, numDays).forEachOrdered(n -> {
                    nextDate[0] = nextDate[0].plusDays(7);
                    try {
                        double alpha = dayCount.yearFrac(startDate, nextDate[0], Optional.of(nextDate[0]));
                        System.out.println(dayCount + " : " + DateTimeFormatter.ofPattern("MM/dd/yyyy").format(startDate) + " : " +
                                DateTimeFormatter.ofPattern("MM/dd/yyyy").format(nextDate[0]) + " : " +alpha);
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                });
            } catch (Exception ex){
                ex.printStackTrace();
            }


        }


    }
}
