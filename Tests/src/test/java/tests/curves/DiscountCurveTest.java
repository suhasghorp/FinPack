package curves;

import com.finpack.curves.DiscountCurve;
import com.finpack.schedule.InterpolationTypes;
import com.finpack.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class DiscountCurveTest {
    @Test
    void testDiscountCurve() throws Exception {
        LocalDate startDate = LocalDate.of(2018, 1, 1);
        double[] t = TestUtils.linspace(0, 10.0, 10);
        double rate = 0.05;
        List<Double> values = DoubleStream.of(t).map(x -> Math.exp(-rate * x)).boxed().collect(Collectors.toList());
        List<Double> times = DoubleStream.of(t).boxed().collect(Collectors.toList());
        DiscountCurve curve = new DiscountCurve(startDate,times,values, InterpolationTypes.FLAT_FORWARDS);
        for (double x : TestUtils.linspace(0, 10, 21)) {
            double df = curve.df(x);
            System.out.println("time: " + x + "  disc factor: " + df);
        }
    }
    @Test
    void testDiscountCurveFromFile() throws Exception {
        List<LocalDate> dates = new ArrayList<>();
        List<Double> rates = new ArrayList<>();
        LocalDate valuationDate = LocalDate.of(2020, 9, 11);
        dates.add(valuationDate);
        rates.add(1.0);
        URL url = this.getClass().getResource("/USD_OIS_0911.csv");
        File file = new File(url.getFile());
        List<List<String>> records = new ArrayList<>();
        try (Scanner scanner = new Scanner(file)){
            while (scanner.hasNextLine()) {
                List<String> values = new ArrayList<String>();
                try (Scanner rowScanner = new Scanner(scanner.nextLine())){
                    rowScanner.useDelimiter(",");
                    while (rowScanner.hasNext()) {
                        values.add(rowScanner.next());
                    }
                }
                records.add(values);
            }
        }
        for (List<String> line : records){
            LocalDate date = LocalDate.parse(line.get(0), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            double df = Double.parseDouble(line.get(1));
            dates.add(date);
            rates.add(df);
        }
        DiscountCurve curve = new DiscountCurve(valuationDate,dates,rates, InterpolationTypes.FLAT_FORWARDS);
        for (double x : TestUtils.linspace(0, 10, 21)) {
            double df = curve.df(x);
            System.out.println("time: " + x + "  disc factor: " + df);
        }
    }
}

