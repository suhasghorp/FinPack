package com.finpack.curves;

import com.finpack.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
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
        DiscountCurve curve = new DiscountCurve(startDate,times,values,InterpolationTypes.FLAT_FORWARDS);
        for (double x : TestUtils.linspace(0, 10, 21)) {
            double df = curve.df(x);
            System.out.println("time: " + x + "  disc factor: " + df);
        }
    }
}
