package com.finpack.curves;

import java.util.List;
import java.util.stream.Collectors;

public class Interpolator {

    public static List<Double> pinterpolate(List<Double> t, List<Double> times, List<Double> dfs, InterpolationTypes method){
        return t.stream().parallel().map(x -> interpolate(x, times, dfs, method)).collect(Collectors.toList());
    }

    public static double interpolate(double t, List<Double> times, List<Double> dfs, InterpolationTypes method) {
        double small = 1e-10;
        int numPoints = times.size();

        if (t == times.get(0))
            return dfs.get(0);

        int i = 0;
        while (times.get(i) < t && i < numPoints - 1)
            i = i + 1;

        if (t > times.get(i))
            i = numPoints;

        double yvalue = 0.0;

        //linear interpolation of y(x)

        if (method == InterpolationTypes.LINEAR_ZERO_RATES) {

            if (i == 1) {
                double r1 = -Math.log(dfs.get(i)) / times.get(i);
                double r2 = -Math.log(dfs.get(i)) / times.get(i);
                double dt = times.get(i) - times.get(i - 1);
                double rvalue = ((times.get(i) - t) * r1 + (t - times.get(i - 1) * r2)) / dt;
                yvalue = Math.exp(-rvalue * t);
            } else if (i == 0) {
                double r1 = -Math.log(dfs.get(dfs.size() - 1)) / times.get(times.size() - 1);
                double r2 = -Math.log(dfs.get(i) / times.get(i));
                double dt = times.get(i) - times.get(times.size() - 1);
                double rvalue = ((times.get(i) - t) * r1 + (t - times.get(times.size() - 1)) * r2) / dt;
                yvalue = Math.exp(-rvalue * t);
            } else if (i < numPoints) {
                double r1 = -Math.log(dfs.get(i - 1)) / times.get(i - 1);
                double r2 = -Math.log(dfs.get(i)) / times.get(i);
                double dt = times.get(i) - times.get(i - 1);
                double rvalue = ((times.get(i) - t) * r1 + (t - times.get(i - 1)) * r2) / dt;
                yvalue = Math.exp(-rvalue * t);
            } else {
                double r1 = -Math.log(dfs.get(i - 1)) / times.get(i - 1);
                double r2 = -Math.log(dfs.get(i - 1)) / times.get(i - 1);
                double dt = times.get(i - 1) - times.get(i - 2);
                double rvalue = ((times.get(i - 1) - t) * r1 + (t - times.get(i - 2)) * r2) / dt;
                yvalue = Math.exp(-rvalue * t);
            }
        } else if (method == InterpolationTypes.FLAT_FORWARDS) {
            //linear interpolation of log(y(x)) which means the linear interpolation of
            //continuously compounded zero rates in the case of discount curves
            //This is also FLAT FORWARDS
            if (i == 1) {
                double rt1 = -Math.log(dfs.get(i - 1));
                double rt2 = -Math.log(dfs.get(i));
                double dt = times.get(i) - times.get(i - 1);
                double rtvalue = ((times.get(i) - t) * rt1 + (t - times.get(i - 1)) * rt2) / dt;
                yvalue = Math.exp(-rtvalue);
            } else if (i == 0) {
                double rt1 = -Math.log(dfs.get(dfs.size() - 1));
                double rt2 = -Math.log(dfs.get(i));
                double dt = times.get(i) - times.get(times.size() - 1);
                double rtvalue = ((times.get(i) - t) * rt1 + (t - times.get(times.size() - 1)) * rt2) / dt;
                yvalue = Math.exp(-rtvalue);
            } else if (i < numPoints) {
                double rt1 = -Math.log(dfs.get(i - 1));
                double rt2 = -Math.log(dfs.get(i));
                double dt = times.get(i) - times.get(i - 1);
                double rtvalue = ((times.get(i) - t) * rt1 + (t - times.get(i - 1)) * rt2) / dt;
                yvalue = Math.exp(-rtvalue);
            } else {
                double rt1 = -Math.log(dfs.get(i - 2));
                double rt2 = -Math.log(dfs.get(i - 1));
                double dt = times.get(i - 1) - times.get(i - 2);
                double rtvalue = ((times.get(i - 1) - t) * rt1 + (t - times.get(i - 2)) * rt2) / dt;
                yvalue = Math.exp(-rtvalue);
            }
        } else if (method == InterpolationTypes.LINEAR_FORWARDS) {
            if (i == 1) {
                double y2 = -Math.log(Math.abs(dfs.get(i)) + small);
                yvalue = t * y2 / (times.get(i) + small);
                yvalue = Math.exp(-yvalue);
            } else if (i == 0) {
                double fwd1 = -Math.log(dfs.get(dfs.size() - 1) / dfs.get(dfs.size() - 2)) / (times.get(times.size() - 1) - times.get(times.size() - 2));
                double fwd2 = -Math.log(dfs.get(i) / dfs.get(dfs.size() - 1)) / (times.get(i) - times.get(times.size() - 1));
                double dt = times.get(i) - times.get(times.size() - 1);
                double fwd = ((times.get(i) - t) * fwd1 + (t - times.get(times.size() - 1)) * fwd2) / dt;
                yvalue = dfs.get(dfs.size() - 1) * Math.exp(-fwd * (t - times.get(times.size() - 1)));

            } else if (i < numPoints) {
                double fwd1 = -Math.log(dfs.get(i - 1) / dfs.get(i - 2)) / (times.get(i - 1) - times.get(i - 2));
                double fwd2 = -Math.log(dfs.get(i) / dfs.get(i - 1)) / (times.get(i) - times.get(i - 1));
                double dt = times.get(i) - times.get(i - 1);
                double fwd = ((times.get(i) - t) * fwd1 + (t - times.get(i - 1)) * fwd2) / dt;
                yvalue = dfs.get(i - 1) * Math.exp(-fwd * (t - times.get(i - 1)));
            } else {
                double fwd = -Math.log(dfs.get(i - 1) / dfs.get(i - 2)) / (times.get(i - 1) - times.get(i - 2));
                yvalue = dfs.get(i - 1) * Math.exp(-fwd * (t - times.get(i - 1)));
            }
        }
        return yvalue;
    }

}
