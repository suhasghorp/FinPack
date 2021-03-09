package com.finpack.products.libor;

import com.finpack.interfaces.IRCurve;
import com.finpack.schedule.DayCount;
import com.finpack.schedule.DayCountTypes;
import com.finpack.utils.DateUtils;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class LiborInterestRateFuture {
    public double futuresRate;
    public double discountFactor, distance;
    public LocalDate lastTradingDate,lastSettlementDate,endOfInterestRatePeriod ;
    DayCountTypes dayCountType;
    double convexity;

    public LiborInterestRateFuture(LocalDate lastTradingDate, DayCountTypes dayCountType,double futuresPrice, double convexity){
        this.lastTradingDate = lastTradingDate;
        this.dayCountType = dayCountType;
        this.convexity = convexity;
        this.lastSettlementDate = lastTradingDate.plusDays(2);
        try {
            this.endOfInterestRatePeriod = DateUtils.nextIMMDate(lastSettlementDate);//lastTradingDate.plusMonths(3);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        this.futuresRate = 1.0 - (futuresPrice/100.0) + (convexity/100);//(100.0 - futuresPrice) / 100.0;
    }
    public double maturityDf(IRCurve curve, LiborInterestRateFuture prevFuture) throws Exception {
        //double accFactor = new DayCount(dayCountType).yearFrac(lastTradingDate, endOfInterestRatePeriod, Optional.empty());
        //if this future lies between 2 deposits



        distance = ChronoUnit.DAYS.between(curve.getCurveDate(), lastSettlementDate)/360.0;
        LocalDate date2 = curve.getDates().get(curve.getDates().size() - 1);
        LocalDate date1 = curve.getDates().get(curve.getDates().size() - 2);
        double df2 = curve.getValues().get(curve.getValues().size() - 1);
        double df1 = curve.getValues().get(curve.getValues().size() - 2);
        if (lastSettlementDate.isBefore(date2) && lastSettlementDate.isAfter(date1)){
            long diff1 = ChronoUnit.DAYS.between(date1, lastSettlementDate);
            long diff2 = ChronoUnit.DAYS.between(date1, date2);
            double temp1 = ((double)diff1 / diff2) * (Math.log(df2) - Math.log(df1));
            double temp2 = Math.log(df1) + temp1;
            discountFactor = Math.exp(temp2);
            return discountFactor;
        } else {
            discountFactor = prevFuture.discountFactor / (1 + prevFuture.futuresRate * (distance - prevFuture.distance));
            return discountFactor;
        }


    /*Calculate implied futures rate from the futures price*/
    //public double futuresRate(LocalDate settlementDate, double futuresPrice){
        //return (100.0 - futuresPrice) / 100.0;
    }
    /*Calculation of the convexity adjustment between FRAs and interest
        rate futures using the Hull-White model as described in technical note. '''
    Technical note
    http://www-2.rotman.utoronto.ca/~hull/TechnicalNotes/TechnicalNote1.pdf*/

    public double convexity(LocalDate settlementDate, double volatility, double a){
        double t1 = ChronoUnit.DAYS.between(settlementDate,lastTradingDate) / 365.242;
        double t2 = ChronoUnit.DAYS.between(settlementDate,endOfInterestRatePeriod)/ 365.242;

        //Hull White model for short rate dr = (theta(t)-ar) dt + sigma * dz
        //This reduces to Ho-Lee when a = 0 so to avoid divergences I provide
        //this numnerical limit
        double c = 0.0;
        if (Math.abs(a) > 1e-10) {
            double bt1t2 = (1.0 - Math.exp(-a * (t2 - t1))) / a;
            double b0t1 = (1.0 - Math.exp(-a * t1)) / a;
            double term = bt1t2 * (1.0 - Math.exp(-2.0 * a * t1)) + 2.0 * a * Math.pow(b0t1, 2);
            c = bt1t2 * term * Math.pow(volatility, 2) / (t2 - t1) / 4.0 / a;
            term = (t2 - t1) * 2.0 * a * t1 + 2.0 * a * t1;
            c = (t2 - t1) * term * Math.pow(volatility, 2) / (t2 - t1) / 4.0 / a;
        } else {
            c = t1 * t2 * Math.pow(volatility,2) /2.0;
        }

        return c;
    }
}
