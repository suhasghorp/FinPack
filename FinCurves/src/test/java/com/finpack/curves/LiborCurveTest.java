package com.finpack.curves;

import com.finpack.products.libor.LiborDeposit;
import com.finpack.products.libor.LiborFRA;
import com.finpack.products.libor.LiborSwap;
import com.finpack.schedule.*;
import com.finpack.utils.DateUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LiborCurveTest {
    @Test
    void testLiborCurveWithDepositsAndSwaps() throws Exception{
        LocalDate valuationDate = LocalDate.of(2019, 9, 18);

        DayCountTypes depoDCCType = DayCountTypes.THIRTY_E_360_ISDA;
        List<LiborDeposit> depos = new ArrayList<>();
        List<LiborFRA> fras = new ArrayList<>();
        List<LiborSwap> swaps = new ArrayList<>();

        int spotDays = 2;
        LocalDate settlementDate = DateUtils.addWorkdays(valuationDate,spotDays);

        double depositRate = 0.050;
        LocalDate maturityDate = settlementDate.plusMonths(1);
        LiborDeposit depo = new LiborDeposit(settlementDate, maturityDate, depositRate, depoDCCType, 1_000_000,
                CalendarTypes.WEEKEND,DayAdjustTypes.MODIFIED_FOLLOWING);
        depos.add(depo);

        maturityDate = settlementDate.plusMonths(2);
        depo = new LiborDeposit(settlementDate, maturityDate, depositRate, depoDCCType, 1_000_000,
                CalendarTypes.WEEKEND,DayAdjustTypes.MODIFIED_FOLLOWING);
        depos.add(depo);

        maturityDate = settlementDate.plusMonths(3);
        depo = new LiborDeposit(settlementDate, maturityDate, depositRate, depoDCCType, 1_000_000,
                CalendarTypes.WEEKEND,DayAdjustTypes.MODIFIED_FOLLOWING);
        depos.add(depo);

        maturityDate = settlementDate.plusMonths(6);
        depo = new LiborDeposit(settlementDate, maturityDate, depositRate, depoDCCType, 1_000_000,
                CalendarTypes.WEEKEND,DayAdjustTypes.MODIFIED_FOLLOWING);
        depos.add(depo);

        maturityDate = settlementDate.plusMonths(9);
        depo = new LiborDeposit(settlementDate, maturityDate, depositRate, depoDCCType, 1_000_000,
                CalendarTypes.WEEKEND,DayAdjustTypes.MODIFIED_FOLLOWING);
        depos.add(depo);

        maturityDate = settlementDate.plusMonths(12);
        depo = new LiborDeposit(settlementDate, maturityDate, depositRate, depoDCCType, 1_000_000,
                CalendarTypes.WEEKEND,DayAdjustTypes.MODIFIED_FOLLOWING);
        depos.add(depo);

        DayCountTypes fixedDCCType = DayCountTypes.ACT_365_ISDA;
        FrequencyTypes fixedFreqType = FrequencyTypes.SEMI_ANNUAL;

        double swapRate = 0.05;
        maturityDate = settlementDate.plusMonths(24);
        LiborSwap swap = new LiborSwap(settlementDate, maturityDate, swapRate, fixedFreqType, fixedDCCType, 1_000_000, 0.0, FrequencyTypes.QUARTERLY,
                DayCountTypes.THIRTY_360, true, CalendarTypes.WEEKEND, DayAdjustTypes.FOLLOWING, DateGenRuleTypes.BACKWARD);
        swaps.add(swap);

        maturityDate = settlementDate.plusMonths(36);
        swap = new LiborSwap(settlementDate, maturityDate, swapRate, fixedFreqType, fixedDCCType, 1_000_000, 0.0, FrequencyTypes.QUARTERLY,
                DayCountTypes.THIRTY_360, true, CalendarTypes.WEEKEND, DayAdjustTypes.FOLLOWING, DateGenRuleTypes.BACKWARD);
        swaps.add(swap);

        maturityDate = settlementDate.plusMonths(48);
        swap = new LiborSwap(settlementDate, maturityDate, swapRate, fixedFreqType, fixedDCCType, 1_000_000, 0.0, FrequencyTypes.QUARTERLY,
                DayCountTypes.THIRTY_360, true, CalendarTypes.WEEKEND, DayAdjustTypes.FOLLOWING, DateGenRuleTypes.BACKWARD);
        swaps.add(swap);

        maturityDate = settlementDate.plusMonths(60);
        swap = new LiborSwap(settlementDate, maturityDate, swapRate, fixedFreqType, fixedDCCType, 1_000_000, 0.0, FrequencyTypes.QUARTERLY,
                DayCountTypes.THIRTY_360, true, CalendarTypes.WEEKEND, DayAdjustTypes.FOLLOWING, DateGenRuleTypes.BACKWARD);
        swaps.add(swap);

        maturityDate = settlementDate.plusMonths(72);
        swap = new LiborSwap(settlementDate, maturityDate, swapRate, fixedFreqType, fixedDCCType, 1_000_000, 0.0, FrequencyTypes.QUARTERLY,
                DayCountTypes.THIRTY_360, true, CalendarTypes.WEEKEND, DayAdjustTypes.FOLLOWING, DateGenRuleTypes.BACKWARD);
        swaps.add(swap);

        maturityDate = settlementDate.plusMonths(84);
        swap = new LiborSwap(settlementDate, maturityDate, swapRate, fixedFreqType, fixedDCCType, 1_000_000, 0.0, FrequencyTypes.QUARTERLY,
                DayCountTypes.THIRTY_360, true, CalendarTypes.WEEKEND, DayAdjustTypes.FOLLOWING, DateGenRuleTypes.BACKWARD);
        swaps.add(swap);

        maturityDate = settlementDate.plusMonths(96);
        swap = new LiborSwap(settlementDate, maturityDate, swapRate, fixedFreqType, fixedDCCType, 1_000_000, 0.0, FrequencyTypes.QUARTERLY,
                DayCountTypes.THIRTY_360, true, CalendarTypes.WEEKEND, DayAdjustTypes.FOLLOWING, DateGenRuleTypes.BACKWARD);
        swaps.add(swap);

        maturityDate = settlementDate.plusMonths(108);
        swap = new LiborSwap(settlementDate, maturityDate, swapRate, fixedFreqType, fixedDCCType, 1_000_000, 0.0, FrequencyTypes.QUARTERLY,
                DayCountTypes.THIRTY_360, true, CalendarTypes.WEEKEND, DayAdjustTypes.FOLLOWING, DateGenRuleTypes.BACKWARD);
        swaps.add(swap);

        maturityDate = settlementDate.plusMonths(120);
        swap = new LiborSwap(settlementDate, maturityDate, swapRate, fixedFreqType, fixedDCCType, 1_000_000, 0.0, FrequencyTypes.QUARTERLY,
                DayCountTypes.THIRTY_360, true, CalendarTypes.WEEKEND, DayAdjustTypes.FOLLOWING, DateGenRuleTypes.BACKWARD);
        swaps.add(swap);

        maturityDate = settlementDate.plusMonths(132);
        swap = new LiborSwap(settlementDate, maturityDate, swapRate, fixedFreqType, fixedDCCType, 1_000_000, 0.0, FrequencyTypes.QUARTERLY,
                DayCountTypes.THIRTY_360, true, CalendarTypes.WEEKEND, DayAdjustTypes.FOLLOWING, DateGenRuleTypes.BACKWARD);
        swaps.add(swap);

        maturityDate = settlementDate.plusMonths(144);
        swap = new LiborSwap(settlementDate, maturityDate, swapRate, fixedFreqType, fixedDCCType, 1_000_000, 0.0, FrequencyTypes.QUARTERLY,
                DayCountTypes.THIRTY_360, true, CalendarTypes.WEEKEND, DayAdjustTypes.FOLLOWING, DateGenRuleTypes.BACKWARD);
        swaps.add(swap);

        maturityDate = settlementDate.plusMonths(180);
        swap = new LiborSwap(settlementDate, maturityDate, swapRate, fixedFreqType, fixedDCCType, 1_000_000, 0.0, FrequencyTypes.QUARTERLY,
                DayCountTypes.THIRTY_360, true, CalendarTypes.WEEKEND, DayAdjustTypes.FOLLOWING, DateGenRuleTypes.BACKWARD);
        swaps.add(swap);

        maturityDate = settlementDate.plusMonths(240);
        swap = new LiborSwap(settlementDate, maturityDate, swapRate, fixedFreqType, fixedDCCType, 1_000_000, 0.0, FrequencyTypes.QUARTERLY,
                DayCountTypes.THIRTY_360, true, CalendarTypes.WEEKEND, DayAdjustTypes.FOLLOWING, DateGenRuleTypes.BACKWARD);
        swaps.add(swap);

        maturityDate = settlementDate.plusMonths(300);
        swap = new LiborSwap(settlementDate, maturityDate, swapRate, fixedFreqType, fixedDCCType, 1_000_000, 0.0, FrequencyTypes.QUARTERLY,
                DayCountTypes.THIRTY_360, true, CalendarTypes.WEEKEND, DayAdjustTypes.FOLLOWING, DateGenRuleTypes.BACKWARD);
        swaps.add(swap);

        maturityDate = settlementDate.plusMonths(360);
        swap = new LiborSwap(settlementDate, maturityDate, swapRate, fixedFreqType, fixedDCCType, 1_000_000, 0.0, FrequencyTypes.QUARTERLY,
                DayCountTypes.THIRTY_360, true, CalendarTypes.WEEKEND, DayAdjustTypes.FOLLOWING, DateGenRuleTypes.BACKWARD);
        swaps.add(swap);

        LiborCurve liborCurve = new LiborCurve("USD_LIBOR",
                settlementDate,
                depos,
                fras,
                swaps, InterpolationTypes.FLAT_FORWARDS);
        liborCurve.buildCurve();
        double df = liborCurve.df(settlementDate);

        for (LiborDeposit deposit : depos) {
            df = liborCurve.df(deposit.maturityDate);
            System.out.println(deposit.maturityDate + " -- " + df);
        }
        for (LiborSwap s : swaps) {
            df = liborCurve.df(s.maturityDate);
            System.out.println(s.maturityDate + " -- " + df);
        }

    }
}
