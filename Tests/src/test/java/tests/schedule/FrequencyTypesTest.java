package schedule;

import com.finpack.schedule.FrequencyTypes;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class FrequencyTypesTest {
    @Test
    void testFrequencyTypes(){
        assertEquals(1, FrequencyTypes.ANNUAL.getFrequency());
        assertEquals(12, FrequencyTypes.MONTHLY.getFrequency());
    }

}
