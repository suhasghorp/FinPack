package com.finpack.curves;

import com.finpack.interfaces.IRCurve;
import com.finpack.products.libor.LiborDeposit;
import com.finpack.products.libor.LiborFRA;
import com.finpack.products.libor.LiborInterestRateFuture;
import com.finpack.products.libor.LiborSwap;
import com.finpack.schedule.InterpolationTypes;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LiborCurve implements IRCurve {
    String name;
    LocalDate curveDate;
    List<LiborDeposit> liborDeposits;
    public List<LiborFRA> liborFRAs;
    public List<LiborInterestRateFuture> liborFutures;
    public List<LiborSwap> liborSwaps;
    List<Double> times = new ArrayList<>();
    List<LocalDate> dates = new ArrayList<>();
    List<Double> values = new ArrayList<>();
    public List<Double> futDiscFactors = new ArrayList<>();
    InterpolationTypes method;

    public LiborCurve(String name, LocalDate curveDate, List<LiborDeposit> liborDeposits, List<LiborFRA> liborFRAs,
                      List<LiborInterestRateFuture> liborFutures, List<LiborSwap> liborSwaps, InterpolationTypes method) throws Exception {
        //super(curveDate, new ArrayList<Double>(), new ArrayList<Double>(),method);
        this.name = name;
        this.curveDate = curveDate;
        this.liborDeposits = liborDeposits;
        this.liborFRAs = liborFRAs;
        this.liborFutures = liborFutures;
        this.liborSwaps = liborSwaps;
        this.method = method;
    }
    public void buildCurve() throws Exception {

        double dfMat = 1.0;
        times.add(0.0);
        dates.add(curveDate);
        values.add(dfMat);
        for (LiborDeposit depo : liborDeposits) {
            double tmat = ChronoUnit.DAYS.between(curveDate,depo.maturityDate) / 365.242;
            dfMat = depo.maturityDf();
            times.add(tmat);
            dates.add(depo.maturityDate);
            values.add(dfMat);
        }
        for (LiborFRA fra : liborFRAs){
            double tmat = ChronoUnit.DAYS.between(curveDate,fra.maturityDate) / 365.242;
            dfMat = fra.maturityDf(this);
            times.add(tmat);
            dates.add(fra.maturityDate);
            values.add(dfMat);
        }
        int i = 0;
        for (i = 0; i < liborFutures.size(); i++){
            LiborInterestRateFuture fut = liborFutures.get(i);
            double tmat = ChronoUnit.DAYS.between(curveDate,fut.lastTradingDate) / 360.0;
            if (i > 0) {
                LiborInterestRateFuture prevFut = liborFutures.get(i-1);
                dfMat = fut.maturityDf(this, prevFut);
                times.add(tmat);
                dates.add(fut.lastSettlementDate);
                values.add(dfMat);
            } else {
                dfMat = fut.maturityDf(this, null);
                futDiscFactors.add(dfMat);
                times.add(times.size()-1, dfMat);
                dates.add(dates.size()-1,fut.lastSettlementDate);
                values.add(values.size()-1,dfMat);
            }
        }
        //handle last Future
        LiborInterestRateFuture lastFut = liborFutures.get(i-1);
        double distance = (ChronoUnit.DAYS.between(curveDate, lastFut.endOfInterestRatePeriod)/360.0) -
                (ChronoUnit.DAYS.between(curveDate, lastFut.lastSettlementDate)/360.0);
        double lastFutDiscfactor = lastFut.discountFactor/(1.0 + lastFut.futuresRate * distance);
        times.add(ChronoUnit.DAYS.between(curveDate, lastFut.endOfInterestRatePeriod)/360.0);
        dates.add(lastFut.endOfInterestRatePeriod);
        values.add(lastFutDiscfactor);


        for (LiborSwap swap : liborSwaps){
            LocalDate maturityDate = swap.maturityDate;
            double tmat = ChronoUnit.DAYS.between(curveDate,swap.maturityDate) / 360.0;
            times.add(tmat);
            dates.add(swap.maturityDate);
            values.add(lastFutDiscfactor);
            UnivariateFunction f = new MinFunction (this,curveDate,swap);
            UnivariateSolver solver = new BrentSolver(1e-15,1e-15,1e-15);
            double df = solver.solve(100,f,0.0,1.0);
            values.set(values.size() - 1,df);
        }
    }

    @Override
    public double df(double dt) {
        return Interpolator.interpolate(dt, times,values,method);
    }

    @Override
    public double df(LocalDate date) {
        double dt = ChronoUnit.DAYS.between(curveDate,date)/365.242;
        return df(dt);
    }

    @Override
    public LocalDate getCurveDate() {
        return curveDate;
    }

    @Override
    public List<Double> getTimes() {
        return times;
    }

    @Override
    public List<Double> getValues() {
        return values;
    }

    @Override
    public List<LocalDate> getDates() {
        return dates;
    }



    class MinFunction implements UnivariateFunction {
        LiborCurve curve;
        LocalDate valueDate;
        LiborSwap swap;
        public MinFunction(LiborCurve curve, LocalDate valueDate, LiborSwap swap){
            this.curve = curve;
            this.valueDate = valueDate;
            this.swap = swap;
        }
        @Override
        public double value(double df) {
            int numPoints = curve.times.size();
            curve.values.set(numPoints - 1,df);
            return swap.fixedLegValue(valueDate, curve, 1.0) - 1.0;
        }
    }
}
