package com.finpack.products.libor;

import com.finpack.interfaces.IRCurve;
import com.finpack.interfaces.IRCurve;
import com.finpack.schedule.*;
import com.finpack.utils.DateUtils;

import java.time.LocalDate;
import java.util.Optional;

public class LiborDeposit {
    public LocalDate settlementDate,maturityDate;
    public double depositRate;
    public DayCountTypes dayCountType;
    public double notional;
    public CalendarTypes calendarType;
    public DayAdjustTypes dayAdjustType;

    public void init(LocalDate settlementDate,
                     LocalDate maturityDate,
                     double depositRate,
                     DayCountTypes dayCountType,
                     double notional,
                     CalendarTypes calendarType,
                     DayAdjustTypes dayAdjustType) throws Exception {
        if (settlementDate.isAfter(maturityDate))
            throw new Exception("Settlement date is after maturity date");
        this.settlementDate = settlementDate;
        this.maturityDate = maturityDate;
        this.depositRate = depositRate;
        this.dayCountType = dayCountType;
        this.notional = notional;
        this.calendarType = calendarType;
        this.dayAdjustType = dayAdjustType;
    }

    public LiborDeposit(LocalDate settlementDate,
                        LocalDate maturityDate,
                        double depositRate,
                        DayCountTypes dayCountType,
                        double notional,
                        CalendarTypes calendarType,
                        DayAdjustTypes dayAdjustType) throws Exception {
        init(settlementDate, maturityDate, depositRate, dayCountType, notional, calendarType, dayAdjustType);
    }
    public LiborDeposit(LocalDate settlementDate,
                        LocalDate maturityDate,
                        double depositRate,
                        DayCountTypes dayCountType) throws Exception{
        init(settlementDate, maturityDate, depositRate, dayCountType, 1_000_000, CalendarTypes.WEEKEND,
                DayAdjustTypes.MODIFIED_FOLLOWING);
    }

    public LiborDeposit(LocalDate settlementDate,
                        String tenor,
                        double depositRate,
                        DayCountTypes dayCountType,
                        double notional,
                        CalendarTypes calendarType,
                        DayAdjustTypes dayAdjustType) throws Exception {
        LocalDate maturityDate = DateUtils.addTenor(settlementDate,tenor);
        Calendar calendar = new Calendar(calendarType);
        maturityDate = calendar.adjust(maturityDate, dayAdjustType);
        init(settlementDate, maturityDate, depositRate, dayCountType, notional, calendarType, dayAdjustType);
    }

    /*Returns the maturity date discount factor that would allow the
        Libor curve to reprice the contractual market deposit rate. Note that
        this is a forward discount factor that starts on settlement date.*/
    public double maturityDf() throws Exception {
        double accFactor = new DayCount(dayCountType).yearFrac(settlementDate, maturityDate, Optional.empty());
        return 1.0 / (1.0 + accFactor * depositRate);
    }

    /*Determine the value of the Deposit given a Libor curve*/
    public double value(LocalDate valueDate, IRCurve curve) throws Exception{
        if (valueDate.isAfter(maturityDate))
            throw new Exception("Start date after maturity date");

        double accFactor = new DayCount(dayCountType).yearFrac(settlementDate, maturityDate, Optional.empty());
        double df = curve.df(maturityDate);
        double value =  (1.0 + accFactor * depositRate) * df * notional;
        double df_settlement = curve.df(settlementDate);
        return value / df_settlement;
    }

    public void print(){
        System.out.println("SETTLEMENT DATE:" + settlementDate);
        System.out.println("MATURITY DATE:" + maturityDate);
        System.out.println("DAY COUNT TYPE:"+ dayCountType);
        System.out.println("DEPOSIT RATE:"+ depositRate);
    }
}
