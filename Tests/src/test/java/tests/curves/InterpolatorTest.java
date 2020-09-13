package curves;

import com.finpack.curves.Interpolator;
import com.finpack.schedule.InterpolationTypes;
import com.finpack.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InterpolatorTest {

    @Test
    void testLinearZeroRates(){
        List<Double> xValues = Arrays.asList(new Double[] {0.25, 0.5, 0.75, 1.0, 2.0, 3.0, 5.0, 10.0});
        List<Double> yValues = new ArrayList<>();
        List<Double> yInterpValues = new ArrayList<>();
        double a = -0.1;
        double b = 0.002;
        for (double x : xValues) {
            double y = Math.exp(a * x + b * x * x);
            yValues.add(y);
        }

        double[]  xInterpolateValues = TestUtils.linspace(0.0, 12.0, 20);
        for (double x : xInterpolateValues) {
            double y_int = Interpolator.interpolate(x, xValues, yValues, InterpolationTypes.LINEAR_ZERO_RATES);
            yInterpValues.add(y_int);
            System.out.println("xinterp " + x + " yinterp " + y_int);
        }
    }

    @Test
    void testFlatForwards(){
        List<Double> xValues = Arrays.asList(new Double[] {0.25, 0.5, 0.75, 1.0, 2.0, 3.0, 5.0, 10.0});
        List<Double> yValues = new ArrayList<>();
        List<Double> yInterpValues = new ArrayList<>();
        double a = -0.1;
        double b = 0.002;
        for (double x : xValues) {
            double y = Math.exp(a * x + b * x * x);
            yValues.add(y);
        }

        double[]  xInterpolateValues = TestUtils.linspace(0.0, 12.0, 20);
        for (double x : xInterpolateValues) {
            double y_int = Interpolator.interpolate(x, xValues, yValues, InterpolationTypes.FLAT_FORWARDS);
            yInterpValues.add(y_int);
            System.out.println("xinterp " + x + " yinterp " + y_int);
        }
    }

    @Test
    void testLinearForwards(){
        List<Double> xValues = Arrays.asList(new Double[] {0.25, 0.5, 0.75, 1.0, 2.0, 3.0, 5.0, 10.0});
        List<Double> yValues = new ArrayList<>();
        List<Double> yInterpValues = new ArrayList<>();
        double a = -0.1;
        double b = 0.002;
        for (double x : xValues) {
            double y = Math.exp(a * x + b * x * x);
            yValues.add(y);
        }

        double[]  xInterpolateValues = TestUtils.linspace(0.0, 12.0, 20);
        for (double x : xInterpolateValues) {
            double y_int = Interpolator.interpolate(x, xValues, yValues, InterpolationTypes.LINEAR_FORWARDS);
            yInterpValues.add(y_int);
            System.out.println("xinterp " + x + " yinterp " + y_int);
        }
    }

    @Test
    void testParallelInterpolate(){
        List<Double> xValues = Arrays.asList(new Double[] {0.25, 0.5, 0.75, 1.0, 2.0, 3.0, 5.0, 10.0});
        List<Double> yValues = new ArrayList<>();
        List<Double> xInterpolateValues = new ArrayList<>();
        double a = -0.1;
        double b = 0.002;
        for (double x : xValues) {
            double y = Math.exp(a * x + b * x * x);
            yValues.add(y);
        }

        double[] temp = TestUtils.linspace(0.0, 12.0, 20);
        for (double d : temp){
            xInterpolateValues.add(d);
        }
        List<Double> yInterpValues = Interpolator.pinterpolate(xInterpolateValues, xValues, yValues, InterpolationTypes.LINEAR_FORWARDS);
        for (int i = 0; i < xInterpolateValues.size(); i++) {
            System.out.println("xinterp " + xInterpolateValues.get(i) + " yinterp " + yInterpValues.get(i));
        }
    }
}
