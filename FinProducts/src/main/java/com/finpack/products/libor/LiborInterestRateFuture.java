package com.finpack.products.libor;

import com.finpack.schedule.DayCountTypes;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public class LiborInterestRateFuture {
    LocalDate lastTradingDate,lastSettlementDate,endOfInterestRatePeriod ;
    DayCountTypes dayCountType;
    double contractSize;

    public LiborInterestRateFuture(LocalDate lastTradingDate, DayCountTypes dayCountType,double contractSize){
        this.lastTradingDate = lastTradingDate;
        this.dayCountType = dayCountType;
        this.contractSize = contractSize;
        this.lastSettlementDate = lastTradingDate.plusDays(2);
        this.endOfInterestRatePeriod = lastTradingDate.plusMonths(3);
    }
    /*Calculate implied futures rate from the futures price*/
    public double futuresRate(LocalDate settlementDate, double futuresPrice){
        return (100.0 - futuresPrice) / 100.0;
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
