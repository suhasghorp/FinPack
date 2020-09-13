package com.finpack.curves;

import com.finpack.products.libor.LiborDeposit;
import com.finpack.products.libor.LiborFRA;
import com.finpack.products.libor.LiborSwap;
import com.finpack.schedule.InterpolationTypes;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class LiborCurve extends DiscountCurve {
    String name;
    List<LiborDeposit> liborDeposits;
    List<LiborFRA> liborFRAs;
    List<LiborSwap> liborSwaps;

    public LiborCurve(String name, LocalDate curveDate, List<LiborDeposit> liborDeposits, List<LiborFRA> liborFRAs, List<LiborSwap> liborSwaps, InterpolationTypes method) throws Exception {
        super(curveDate, new ArrayList<Double>(), new ArrayList<Double>(),method);
        this.name = name;
        this.liborDeposits = liborDeposits;
        this.liborFRAs = liborFRAs;
        this.liborSwaps = liborSwaps;
    }
    public void buildCurve() throws Exception {

        double dfMat = 1.0;
        times.add(0.0);
        values.add(dfMat);
        for (LiborDeposit depo : liborDeposits) {
            double tmat = ChronoUnit.DAYS.between(curveDate,depo.maturityDate) / 365.242;
            dfMat = depo.maturityDf();
            times.add(tmat);
            values.add(dfMat);
        }
        for (LiborFRA fra : liborFRAs){
            double tmat = ChronoUnit.DAYS.between(curveDate,fra.maturityDate) / 365.242;
            dfMat = fra.maturityDf(this);
            times.add(tmat);
            values.add(dfMat);
        }
        for (LiborSwap swap : liborSwaps){
            LocalDate maturityDate = swap.maturityDate;
            double tmat = ChronoUnit.DAYS.between(curveDate,swap.maturityDate) / 365.242;
            times.add(tmat);
            values.add(dfMat);
            UnivariateFunction f = new MinFunction (this,curveDate,swap);
            UnivariateSolver solver = new BrentSolver(1e-15,1e-15,1e-15);
            double df = solver.solve(100,f,0.0,1.5);
            values.set(values.size() - 1,df);
        }
    }

    class MinFunction implements UnivariateFunction {
        DiscountCurve curve;
        LocalDate valueDate;
        LiborSwap swap;
        public MinFunction(DiscountCurve curve, LocalDate valueDate, LiborSwap swap){
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
