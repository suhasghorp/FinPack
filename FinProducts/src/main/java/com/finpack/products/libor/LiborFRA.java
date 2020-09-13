package com.finpack.products.libor;

import com.finpack.interfaces.IRCurve;
import com.finpack.interfaces.IRCurve;
import com.finpack.schedule.*;
import com.finpack.utils.DateUtils;

import java.time.LocalDate;
import java.util.Optional;

public class LiborFRA {

    public LocalDate settlementDate,maturityDate;
    public double fraRate;
    public boolean payFixedRate;
    public DayCountTypes dayCountType;
    public double notional;

    public void init(LocalDate settlementDate,
                     LocalDate maturityDate,
                     double fraRate,
                     boolean payFixedRate,
                     DayCountTypes dayCountType,
                     double notional,
                     CalendarTypes calendarType,
                     DayAdjustTypes dayAdjustType) throws Exception {
        if (settlementDate.isAfter(maturityDate))
            throw new Exception("Settlement date is after maturity date");
        Calendar cal = new Calendar(calendarType);
        this.settlementDate = cal.adjust(settlementDate,dayAdjustType);
        this.maturityDate = cal.adjust(maturityDate,dayAdjustType);
        this.fraRate = fraRate;
        this.payFixedRate = payFixedRate;
        this.dayCountType = dayCountType;
        this.notional = notional;
    }

    public LiborFRA(LocalDate settlementDate,
                    LocalDate maturityDate,
                    double fraRate,
                    boolean payFixedRate,
                    DayCountTypes dayCountType,
                    double notional,
                    CalendarTypes calendarType,
                    DayAdjustTypes dayAdjustType) throws Exception {
        init(settlementDate, maturityDate, fraRate, payFixedRate, dayCountType, notional, calendarType, dayAdjustType);
    }
    public LiborFRA(LocalDate settlementDate,
                    LocalDate maturityDate,
                    double fraRate,
                    boolean payFixedRate,
                    DayCountTypes dayCountType) throws Exception {
        init(settlementDate, maturityDate, fraRate, payFixedRate, dayCountType, 1_000_000.0, CalendarTypes.WEEKEND, DayAdjustTypes.MODIFIED_FOLLOWING);
    }
    public LiborFRA(LocalDate settlementDate,
                    String tenor,
                    double fraRate,
                    boolean payFixedRate,
                    DayCountTypes dayCountType,
                    double notional,
                    CalendarTypes calendarType,
                    DayAdjustTypes dayAdjustType) throws Exception {

        LocalDate maturityDate = DateUtils.addTenor(settlementDate,tenor);
        init(settlementDate, maturityDate, fraRate, payFixedRate, dayCountType, notional, calendarType, dayAdjustType);
    }
    /*Determine the maturity date discount factor needed to refit
        the FRA given the libor curve anbd the contract FRA rate.*/

    public double maturityDf(IRCurve curve) throws Exception {
        double df1 = curve.df(settlementDate);
        double accFactor = new DayCount(dayCountType).yearFrac(settlementDate, maturityDate, Optional.empty());
        return df1 / (1.0 + accFactor * fraRate);
    }
    public double value(LocalDate valueDate, IRCurve curve) throws Exception{
        if (valueDate.isAfter(maturityDate))
            throw new Exception("Start date after maturity date");

        double accFactor0 = new DayCount(dayCountType).yearFrac(settlementDate, maturityDate, Optional.empty());
        double df1 = curve.df(settlementDate);
        double df2 = curve.df(maturityDate);
        double liborFwd = (df1 / df2 - 1.0) / accFactor0;
        double v = accFactor0 * (liborFwd - fraRate) * df2;
        // Forward value the FRA to the value date
        double dfTovalueDate = curve.df(valueDate);
        v = v * notional / dfTovalueDate;
        if (payFixedRate)
            v = v * -1.0;
        return v;
    }
    public void print(){
        System.out.println("SETTLEMENT DATE:"+settlementDate);
        System.out.println("MATURITY DATE  :"+maturityDate);
        System.out.println("FRA RATE       :"+fraRate);
        System.out.println("PAY FIXED LEG  :"+payFixedRate);
        System.out.println("DAY COUNT TYPE :"+dayCountType);
    }
}
