package com.finpack.curves;

import com.finpack.interfaces.IRCurve;
import com.finpack.schedule.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DiscountCurve implements IRCurve {
    LocalDate curveDate;
    List<Double> times, values;
    List<LocalDate> dates;
    InterpolationTypes method;
    //public DiscountCurve(){}
    public DiscountCurve(LocalDate curveDate, List<?> times, List<Double> values, InterpolationTypes method) throws Exception {
        this.curveDate = curveDate;

        if (times.size() > 0 ) {
            if (times.get(0) instanceof Double) {
                this.times = (List<Double>) times;
            } else if (times.get(0) instanceof LocalDate) {
                final List l = times;
                this.dates = (List<LocalDate>) times;
                for (int i = 0; i < dates.size(); i++) {
                    //final List l = times;
                    l.set(i, ChronoUnit.DAYS.between(curveDate, dates.get(i)) / 365.242);
                }
                this.times = l;
            }
        } else {
            this.times = (List<Double>) times;
        }

        this.values = values;
        this.method = method;
    }


    @Override
    public double df(double dt){
        return Interpolator.interpolate(dt, times,values,method);
    }

    public double df(LocalDate date){
        double dt = ChronoUnit.DAYS.between(curveDate,date)/365.242;
        return df(dt);
    }

    @Override
    public LocalDate getCurveDate() {
        return curveDate;
    }

    @Override
    public List<Double> getTimes() {
        return this.times;
    }

    @Override
    public List<Double> getValues() {
        return values;
    }

    @Override
    public List<LocalDate> getDates() {
        return dates;
    }



    public double zeroRate(double dt, CompoundingFrequencyTypes freq) {
        double df = df(dt);
        if (freq == CompoundingFrequencyTypes.SIMPLE_INTEREST){
            return ((1.0/df) - 1.0)/dt;
        } else if (freq == CompoundingFrequencyTypes.CONTINOUS_INTEREST){
            return -Math.log(df) / dt;
        } else {
            double f = 0.0;
            switch(freq){
                case ONE:
                    f = 1.0;
                case TWO:
                    f = 2.0;
                case THREE:
                    f = 3.0;
                case FOUR:
                    f = 4.0;
                case SIX:
                    f = 6.0;
                case TWELVE:
                    f = 12.0;
            }
            return (Math.pow(df,(-1.0/dt/f)) - 1) * f;
        }
    }
    public double zeroRate(LocalDate date, CompoundingFrequencyTypes freq){
        double dt = ChronoUnit.DAYS.between(curveDate,date)/365.242;
        return zeroRate(dt,freq);
    }

    public double fwd(double dt){
        //Calculate the continuous forward rate at the forward date
        double infinitesimal = 0.000001;
        double df1 = df(dt);
        double df2 = df(dt + infinitesimal);
        return Math.log(df1/df2)/dt;
    }
    public double fwd(LocalDate date){
        double dt = ChronoUnit.DAYS.between(curveDate,date)/365.242;
        return fwd(dt);
    }
    public DiscountCurve bump(double bumpSize) throws Exception {
        List<Double> clonedTimes = new ArrayList<Double>(times);
        List<Double> clonedValues = new ArrayList<Double>(values);

        for (int i = 0; i < times.size(); i++)
            clonedValues.set(i, values.get(i) * Math.exp(-bumpSize*times.get(i)));

        return new DiscountCurve(curveDate, clonedTimes, clonedValues, method);
    }
    public double fwdRate(LocalDate date1, LocalDate date2, DayCountTypes dayCountType) throws Exception {
        if (date1.isBefore(curveDate))
            throw new Exception("date1 is before curve date");
        if (date2.isBefore(date1)){
            throw new Exception("date2 cannot be before date1");
        }
        double yearFrac = new DayCount(dayCountType).yearFrac(date1,date2, Optional.empty());
        double df1 = df(date1);
        double df2 = df(date2);
        return (df1 / df2 - 1.0) / yearFrac;
    }
    public void print(){
        System.out.println("TIMES,DISCOUNT FACTOR");
        for (int i = 0; i < times.size();i++){
            System.out.println(times.get(i) + " ----- " +  values.get(i));
        }
    }
}
