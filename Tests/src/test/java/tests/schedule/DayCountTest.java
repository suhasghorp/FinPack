package tests.schedule;

import com.finpack.schedule.DayCount;
import com.finpack.schedule.DayCountTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

public class DayCountTest {
    @ParameterizedTest(name = "{index} - THIRTY_360_BOND - {2} - {3}")
    @CsvFileSource(resources = "/THIRTY_360_BOND.csv")
    void testDayCount_THIRTY_360_BOND(String results, String dayCountType, String start, String end, double dcf){
        DateTimeFormatter parser = new DateTimeFormatterBuilder().parseCaseInsensitive() .appendPattern("dd-MMM-yyyy").toFormatter(Locale.ENGLISH);
        LocalDate startDate = LocalDate.parse(start, parser);
        LocalDate endDate = LocalDate.parse(end, parser);
        DayCount dayCount = new DayCount(DayCountTypes.THIRTY_360_BOND);
        List<String> t = new ArrayList<>();
        try {
            Assertions.assertEquals(dcf,dayCount.yearFrac(startDate, endDate, Optional.of(endDate)),0.0001);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
