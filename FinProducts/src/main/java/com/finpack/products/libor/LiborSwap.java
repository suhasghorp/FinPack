package com.finpack.products.libor;

import com.finpack.curves.DiscountCurve;
import com.finpack.curves.LiborCurve;
import com.finpack.schedule.*;
import com.finpack.utils.DateUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LiborSwap {
    public LocalDate startDate,maturityDate;
    public double fixedCoupon,notional,floatSpread;
    public FrequencyTypes fixedFreqType;
    public DayCountTypes fixedDayCountType;
    public FrequencyTypes floatFreqType;
    public DayCountTypes floatDayCountType;
    public boolean payFixedFlag;
    public CalendarTypes calendarType;
    public int fixedStartIndex, floatStartIndex;
    public DayAdjustTypes dayAdjustType;
    public DateGenRuleTypes dateGenRuleType;
    public List<LocalDate> adjustedFixedDates = new ArrayList<>();
    public List<LocalDate> adjustedFloatDates = new ArrayList<>();
    public List<Double> fixedYearFracs = new ArrayList<>();
    public List<Double> fixedFlows = new ArrayList<>();
    public List<Double> fixedDfs = new ArrayList<>();
    public List<Double> fixedFlowPVs = new ArrayList<>();
    public List<Double> floatYearFracs = new ArrayList<>();
    public List<Double> floatFlows = new ArrayList<>();
    public List<Double> floatDfs = new ArrayList<>();
    public List<Double> floatFlowPVs = new ArrayList<>();

    public void init(LocalDate startDate,
                     LocalDate maturityDate,
                     double fixedCoupon,
                     FrequencyTypes fixedFreqType,
                     DayCountTypes fixedDayCountType,
                     double notional,
                     double floatSpread,
                     FrequencyTypes floatFreqType,
                     DayCountTypes floatDayCountType,
                     boolean payFixedFlag,
                     CalendarTypes calendarType,
                     DayAdjustTypes dayAdjustType,
                     DateGenRuleTypes dateGenRuleType) throws Exception{
        this.startDate = startDate;
        this.maturityDate = maturityDate;
        this.fixedCoupon = fixedCoupon;
        this.fixedFreqType = fixedFreqType;
        this.fixedDayCountType = fixedDayCountType;
        this.notional = notional;
        this.floatSpread = floatSpread;
        this.floatFreqType = floatFreqType;
        this.floatDayCountType = floatDayCountType;
        this.payFixedFlag = payFixedFlag;
        this.calendarType = calendarType;
        this.dayAdjustType = dayAdjustType;
        this.dateGenRuleType = dateGenRuleType;
        generateFixedLegPaymentDates();
        generateFloatLegPaymentDates();
    }
    public LiborSwap(LocalDate startDate,
                     LocalDate maturityDate,
                     double fixedCoupon,
                     FrequencyTypes fixedFreqType,
                     DayCountTypes fixedDayCountType,
                     double notional,
                     double floatSpread,
                     FrequencyTypes floatFreqType,
                     DayCountTypes floatDayCountType,
                     boolean payFixedFlag,
                     CalendarTypes calendarType,
                     DayAdjustTypes dayAdjustType,
                     DateGenRuleTypes dateGenRuleType) throws Exception{
        init( startDate, maturityDate,fixedCoupon,fixedFreqType, fixedDayCountType, notional, floatSpread, floatFreqType,
        floatDayCountType, payFixedFlag, calendarType, dayAdjustType,dateGenRuleType);
    }
    public LiborSwap(LocalDate startDate,
                     LocalDate maturityDate,
                     double fixedCoupon,
                     FrequencyTypes fixedFreqType,
                     DayCountTypes fixedDayCountType) throws Exception{
        init( startDate, maturityDate,fixedCoupon,fixedFreqType, fixedDayCountType, 1_000_000.0, 0.0, FrequencyTypes.QUARTERLY,
                DayCountTypes.THIRTY_360, true, CalendarTypes.WEEKEND, DayAdjustTypes.MODIFIED_FOLLOWING, DateGenRuleTypes.BACKWARD);
    }
    public LiborSwap(LocalDate startDate,
                     String tenor,
                     double fixedCoupon,
                     FrequencyTypes fixedFreqType,
                     DayCountTypes fixedDayCountType,
                     double notional,
                     double floatSpread,
                     FrequencyTypes floatFreqType,
                     DayCountTypes floatDayCountType,
                     boolean payFixedFlag,
                     CalendarTypes calendarType,
                     DayAdjustTypes dayAdjustType,
                     DateGenRuleTypes dateGenRuleType) throws Exception {
        LocalDate maturityDate = DateUtils.addTenor(startDate,tenor);
        init( startDate, maturityDate,fixedCoupon,fixedFreqType, fixedDayCountType, notional, floatSpread, floatFreqType,
                floatDayCountType, payFixedFlag, calendarType, dayAdjustType,dateGenRuleType);
    }
    public void generateFixedLegPaymentDates() throws Exception {
        adjustedFixedDates = new Schedule(startDate,maturityDate,fixedFreqType,calendarType,dayAdjustType,dateGenRuleType).generate();
    }
    public void generateFloatLegPaymentDates() throws Exception {
        adjustedFloatDates = new Schedule(startDate,maturityDate,floatFreqType,calendarType,dayAdjustType,dateGenRuleType).generate();
    }
    public double fixedLegValue(LocalDate valueDate, DiscountCurve curve, double principal) {
        int startIndex = 0;
        while (adjustedFixedDates.get(startIndex).isBefore(valueDate))
            startIndex++;
        if (valueDate.isBefore(startDate)||valueDate.isEqual(startDate))
            startIndex = 1;
        fixedStartIndex = startIndex;
        double pv = 0.0;
        double df_discount = 0.0;
        LocalDate prevDt = adjustedFixedDates.get(startIndex - 1);
        List<LocalDate> futureFixedDates = adjustedFixedDates.subList(startIndex, adjustedFixedDates.size());
        for (LocalDate nextDt : futureFixedDates) {
            try {
                double alpha = new DayCount(fixedDayCountType).yearFrac(prevDt, nextDt, Optional.empty());
                df_discount = curve.df(nextDt);
                double flow = fixedCoupon * alpha;
                double flowPV = flow * df_discount;
                pv = pv + flowPV;
                prevDt = nextDt;

                fixedYearFracs.add(alpha);
                fixedFlows.add(flow);
                fixedDfs.add(df_discount);
                fixedFlowPVs.add(flow * df_discount);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
        pv = pv + (principal * df_discount);
        fixedFlowPVs.set(fixedFlowPVs.size()-1, fixedFlowPVs.get(fixedFlowPVs.size()-1) + (principal * df_discount));
        fixedFlows.set(fixedFlows.size()-1, fixedFlows.get(fixedFlows.size()-1) + principal);

        double z0 = curve.df(valueDate);
        pv = pv / z0;
        return pv;
    }
    /*Value the floating leg with payments from an index curve and
        discounting based on a supplied discount curve*/
    public double floatLegValue(LocalDate valueDate, LiborCurve discountCurve, LiborCurve indexCurve,Optional<Double> firstFixing,
                                double principal) throws Exception{
        int startIndex = 0;
        while (adjustedFloatDates.get(startIndex).isBefore(valueDate))
            startIndex++;
        if (valueDate.isBefore(startDate)||valueDate.isEqual(startDate))
            startIndex = 1;
        floatStartIndex = startIndex;
        double pv = 0.0;
        /*The first floating payment is usually already fixed so is
        not implied by the index curve.*/
        LocalDate prevDt = adjustedFloatDates.get(startIndex - 1);
        LocalDate nextDt = adjustedFloatDates.get(startIndex);
        double alpha = new DayCount(floatDayCountType).yearFrac(prevDt, nextDt,Optional.empty());
        double df1_index = 1.0;  //Cannot be previous date as that has past
        double df2_index = indexCurve.df(nextDt);
        double flow,libor;
        if (!firstFixing.isPresent()) {
            libor = (df1_index / df2_index - 1.0) / alpha;
            flow = libor * alpha;
        } else {
            flow = firstFixing.get() * alpha;
        }
        double df_discount = discountCurve.df(nextDt);
        pv = pv + (flow * df_discount);

        floatYearFracs.add(alpha);
        floatFlows.add(flow);
        floatDfs.add(df_discount);
        floatFlowPVs.add(flow * df_discount);

        prevDt = nextDt;
        df1_index = indexCurve.df(prevDt);

        List<LocalDate> futureFloatDates = adjustedFloatDates.subList(startIndex+1, adjustedFloatDates.size());
        for (LocalDate nextDate : futureFloatDates) {
            alpha = new DayCount(floatDayCountType).yearFrac(prevDt, nextDate, Optional.empty());
            df2_index = indexCurve.df(nextDate);
            flow = (df1_index / df2_index - 1.0);  //The accrual factors cancel
            df_discount = discountCurve.df(nextDate);
            pv += flow * df_discount;
            df1_index = df2_index;
            prevDt = nextDate;

            floatFlows.add(flow);
            floatYearFracs.add(alpha);
            floatDfs.add(df_discount);
            floatFlowPVs.add(flow * df_discount);
        }
        pv = pv + (principal * df_discount);
        floatFlowPVs.set(floatFlowPVs.size()-1, floatFlowPVs.get(floatFlowPVs.size()-1) + (principal * df_discount));
        floatFlows.set(floatFlows.size()-1, floatFlows.get(floatFlows.size()-1) + principal);
        double z0 = discountCurve.df(valueDate);
        pv = pv / z0;
        return pv;
    }
    /*Calculate the fixed leg coupon that makes the swap worth zero.
        If the valuation date is before the swap payments start then this
        is the forward swap rate as it starts in the future. The swap rate
        is then a forward swap rate and so we use a forward discount
        factor. If the swap fixed leg has begun then we have a spot
        starting swap.*/
    public double parCoupon(LocalDate valueDate, LiborCurve discountCurve){
        double pv_ONE = pv01(valueDate, discountCurve);
        double df0;
        if (valueDate.isBefore(startDate)) {
            df0 = discountCurve.df(startDate);
        }else {
            df0 = discountCurve.df(valueDate);
        }
        double dfT = discountCurve.df(maturityDate);
        double cpn = (df0 - dfT) / pv_ONE;
        return cpn;
    }
    /*Calculate the value of 1 basis point coupon on the fixed leg.*/
    public double pv01 (LocalDate valueDate, LiborCurve discountCurve){
        double pv01 = fixedLegValue(valueDate, discountCurve, 1.0);
        pv01 = pv01 / fixedCoupon;
        return pv01;
    }
    /*Value the interest rate swap on a value date given a single Libor
        discount curve. */
    public double value(LocalDate valueDate, LiborCurve discountCurve, LiborCurve indexCurve,Optional<Double> firstFixing,
                        double principal) throws Exception{
        double fixedLegValue = fixedLegValue(valueDate, discountCurve, principal);
        double floatLegValue = floatLegValue(valueDate, discountCurve, indexCurve, firstFixing, principal);
        double npv = fixedLegValue - floatLegValue;
        if (payFixedFlag)
            npv = npv * (-1.0);
        return npv * notional;
    }
    public void printFixedLeg(LocalDate valueDate){
        System.out.printf("START DATE:%s", startDate);
        System.out.printf("MATURITY DATE:%s", maturityDate);
        System.out.printf("COUPON:%s", fixedCoupon * 100);
        System.out.printf("FIXED LEG FREQUENCY:%s", fixedFreqType);
        System.out.printf("FIXED LEG DAY COUNT:%s", fixedDayCountType);
        System.out.printf("VALUATION DATE:%s\n", valueDate);

        System.out.printf("PAYMENT_DATE     YEAR_FRAC        FLOW         DF         DF*FLOW       CUM_PV\n");
        int numFlows = adjustedFixedDates.size();
        double totalPV = 0.0;

        for (int i = fixedStartIndex; i < numFlows; i++){
            LocalDate paymentDate = adjustedFixedDates.get(i);
            int iFlow = i - fixedStartIndex;
            double flow = fixedFlows.get(iFlow) * notional;
            double alpha = fixedYearFracs.get(iFlow);
            double df = fixedDfs.get(iFlow);
            double flowPV = fixedFlowPVs.get(iFlow) * notional;
            totalPV += flowPV;
            System.out.printf("%15s %10.7f %12.2f %12.6f %12.2f %12.2f\n" , paymentDate, alpha, flow, df, flowPV, totalPV);
        }
    }
    public void printFloatLeg(LocalDate valueDate){
        System.out.printf("START DATE:%s", startDate);
        System.out.printf("MATURITY DATE:%s", maturityDate);
        System.out.printf("SPREAD COUPON:%s", floatSpread * 100);
        System.out.printf("FLOAT LEG FREQUENCY:%s", floatFreqType);
        System.out.printf("FLOAT LEG DAY COUNT:%s", floatDayCountType);
        System.out.printf("VALUATION DATE:%s", valueDate);

        System.out.printf("PAYMENT_DATE     YEAR_FRAC        FLOW         DF         DF*FLOW       CUM_PV\n");
        int numFlows = adjustedFloatDates.size();
        double totalPV = 0.0;

        for (int i = floatStartIndex; i < numFlows; i++) {
            LocalDate paymentDate = adjustedFloatDates.get(i);
            int iFlow = i - floatStartIndex;
            double flow = floatFlows.get(iFlow) * notional;
            double alpha = floatYearFracs.get(iFlow);
            double df = floatDfs.get(iFlow);
            double flowPV = floatFlowPVs.get(iFlow) * notional;
            totalPV += flowPV;
            System.out.printf("%15s %10.7f %12.2f %12.6f %12.2f %12.2f\n" ,paymentDate, alpha, flow, df, flowPV, totalPV);
        }
    }
}
