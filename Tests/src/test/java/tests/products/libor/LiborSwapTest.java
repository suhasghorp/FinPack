package products.libor;

import com.finpack.curves.DiscountCurve;
import com.finpack.curves.LiborCurve;
import com.finpack.interfaces.IRCurve;
import com.finpack.products.libor.LiborDeposit;
import com.finpack.products.libor.LiborFRA;
import com.finpack.products.libor.LiborInterestRateFuture;
import com.finpack.products.libor.LiborSwap;
import com.finpack.schedule.*;
import com.finpack.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class LiborSwapTest {
    /*@Test
    void testLiborSwap() throws Exception {
        LocalDate startDate = LocalDate.of(2017, 12, 27);
        LocalDate endDate = LocalDate.of(2067, 12, 27);

        double fixedCoupon = 0.015;
        FrequencyTypes fixedFreqType = FrequencyTypes.ANNUAL;
        DayCountTypes fixedDayCountType = DayCountTypes.THIRTY_360;

        double floatSpread = 0.0;
        FrequencyTypes floatFreqType = FrequencyTypes.SEMI_ANNUAL;
        DayCountTypes floatDayCountType = DayCountTypes.ACT_360;
        double firstFixing = -0.00268;

        CalendarTypes swapCalendarType = CalendarTypes.WEEKEND;
        DayAdjustTypes busDayAdjustType = DayAdjustTypes.FOLLOWING;
        DateGenRuleTypes dateGenRuleType = DateGenRuleTypes.BACKWARD;

        boolean payFixedFlag = false;
        double notional = 10.0 * 1_000_000;

        LiborSwap swap = new LiborSwap(startDate,
                endDate,
                fixedCoupon,
                fixedFreqType,
                fixedDayCountType,
                notional,
                floatSpread,
                floatFreqType,
                floatDayCountType,
                payFixedFlag,
                swapCalendarType,
                busDayAdjustType,
                dateGenRuleType);

        LocalDate valuationDate = LocalDate.of(2018, 11, 30);
        LocalDate settlementDate = valuationDate.plusDays(2);
        IRCurve liborCurve = buildLiborCurve(valuationDate);
        double v = swap.value(settlementDate, liborCurve, liborCurve, Optional.of(firstFixing), 0.0);
        //swap.printFixedLeg(valuationDate);
        //swap.printFloatLeg(valuationDate);
        //v_bbg = 388147.0;
        Assertions.assertEquals(393065.4673215106,v, 1.0);
    }*/

    @Test
    void testLiborSwapFromFile() throws Exception {
        LocalDate startDate = LocalDate.of(2019, 12, 13);
        LocalDate endDate = LocalDate.of(2029, 12, 13);

        double fixedCoupon = 0.01753;
        FrequencyTypes fixedFreqType = FrequencyTypes.SEMI_ANNUAL;
        DayCountTypes fixedDayCountType = DayCountTypes.THIRTY_360;

        double floatSpread = 0.0;
        FrequencyTypes floatFreqType = FrequencyTypes.QUARTERLY;
        DayCountTypes floatDayCountType = DayCountTypes.ACT_360;

        double firstFixing = 0.00299;

        CalendarTypes swapCalendarType = CalendarTypes.US;
        DayAdjustTypes busDayAdjustType = DayAdjustTypes.MODIFIED_FOLLOWING;
        DateGenRuleTypes dateGenRuleType = DateGenRuleTypes.BACKWARD;

        boolean payFixedFlag = true;
        double notional = 750_000;

        LiborSwap swap = new LiborSwap(startDate,
                endDate,
                fixedCoupon,
                fixedFreqType,
                fixedDayCountType,
                notional,
                floatSpread,
                floatFreqType,
                floatDayCountType,
                payFixedFlag,
                swapCalendarType,
                busDayAdjustType,
                dateGenRuleType);
        /*Now perform a valuation after the swap has seasoned but with the
    same curve being used for discounting and working out the implied
    future Libor rates*/
        LocalDate valuationDate = LocalDate.of(2020, 9, 11);
        LocalDate settlementDate = valuationDate.plusDays(2);
        IRCurve discountCurve = buildDiscountCurveFromFile(valuationDate);
        IRCurve indexCurve = buildIndexCurveFromFile(valuationDate);
        double v = swap.value(settlementDate, discountCurve, indexCurve, Optional.of(firstFixing), 0.0);
        System.out.println(v);
        //swap.printFixedLeg(valuationDate);
        //swap.printFloatLeg(valuationDate);
        //v_bbg = 388147.0;
        //Assertions.assertEquals(393065.4673215106,v, 1.0);
    }

    IRCurve buildIndexCurveFromFile(LocalDate valuationDate) throws Exception {
        List<LocalDate> dates = new ArrayList<>();
        List<Double> rates = new ArrayList<>();
        dates.add(valuationDate);
        rates.add(1.0);
        URL url = this.getClass().getResource("/USD_LIBOR_3M_0911.csv");
        File file = new File(url.getFile());
        List<List<String>> records = new ArrayList<>();
        try (Scanner scanner = new Scanner(file)){
            while (scanner.hasNextLine()) {
                List<String> values = new ArrayList<String>();
                try (Scanner rowScanner = new Scanner(scanner.nextLine())){
                    rowScanner.useDelimiter(",");
                    while (rowScanner.hasNext()) {
                        values.add(rowScanner.next());
                    }
                }
                records.add(values);
            }
        }
        for (List<String> line : records){
            LocalDate date = LocalDate.parse(line.get(0), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            double df = Double.parseDouble(line.get(1));
            dates.add(date);
            rates.add(df);
        }
        DiscountCurve curve = new DiscountCurve(valuationDate,dates,rates, InterpolationTypes.FLAT_FORWARDS);
        return curve;
    }

    IRCurve buildDiscountCurveFromFile(LocalDate valuationDate) throws Exception {
        List<LocalDate> dates = new ArrayList<>();
        List<Double> rates = new ArrayList<>();
        dates.add(valuationDate);
        rates.add(1.0);
        URL url = this.getClass().getResource("/USD_OIS_0911.csv");
        File file = new File(url.getFile());
        List<List<String>> records = new ArrayList<>();
        try (Scanner scanner = new Scanner(file)){
            while (scanner.hasNextLine()) {
                List<String> values = new ArrayList<String>();
                try (Scanner rowScanner = new Scanner(scanner.nextLine())){
                    rowScanner.useDelimiter(",");
                    while (rowScanner.hasNext()) {
                        values.add(rowScanner.next());
                    }
                }
                records.add(values);
            }
        }
        for (List<String> line : records){
            LocalDate date = LocalDate.parse(line.get(0), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            double df = Double.parseDouble(line.get(1));
            dates.add(date);
            rates.add(df);
        }
        DiscountCurve curve = new DiscountCurve(valuationDate,dates,rates, InterpolationTypes.FLAT_FORWARDS);
        return curve;
    }

    IRCurve buildLiborCurve(LocalDate valuationDate) throws Exception {
        LocalDate settlementDate = valuationDate.plusDays(2);
        DayCountTypes dcType = DayCountTypes.ACT_360;

        List<LiborDeposit> depos = new ArrayList<>();
        List<LiborFRA> fras = new ArrayList<>();
        List<LiborInterestRateFuture> futures = new ArrayList<>();
        List<LiborSwap> swaps = new ArrayList<>();

        LocalDate maturityDate = settlementDate.plusMonths(6);
        LiborDeposit depo1 = new LiborDeposit(settlementDate, maturityDate, -0.00251, dcType);
        depos.add(depo1);

        //Series of 1M futures
        LocalDate startDate = DateUtils.nextIMMDate(settlementDate);
        LocalDate endDate = startDate.plusMonths(1);
        LiborFRA fra = new LiborFRA(startDate, endDate, -0.0023, true, dcType);
        fras.add(fra);

        startDate = startDate.plusMonths(1);
        endDate = startDate.plusMonths(1);
        fra = new LiborFRA(startDate, endDate, -0.00234, true, dcType);
        fras.add(fra);

        startDate = startDate.plusMonths(1);
        endDate = startDate.plusMonths(1);
        fra = new LiborFRA(startDate, endDate, -0.00225, true, dcType);
        fras.add(fra);

        startDate = startDate.plusMonths(1);
        endDate = startDate.plusMonths(1);
        fra = new LiborFRA(startDate, endDate, -0.00226, true, dcType);
        fras.add(fra);

        startDate = startDate.plusMonths(1);
        endDate = startDate.plusMonths(1);
        fra = new LiborFRA(startDate, endDate, -0.00219, true, dcType);
        fras.add(fra);

        startDate = startDate.plusMonths(1);
        endDate = startDate.plusMonths(1);
        fra = new LiborFRA(startDate, endDate, -0.00213, true, dcType);
        fras.add(fra);

        startDate = startDate.plusMonths(1);
        endDate = startDate.plusMonths(1);
        fra = new LiborFRA(startDate, endDate, -0.00186, true, dcType);
        fras.add(fra);

        startDate = startDate.plusMonths(1);
        endDate = startDate.plusMonths(1);
        fra = new LiborFRA(startDate, endDate, -0.00189, true, dcType);
        fras.add(fra);

        startDate = startDate.plusMonths(1);
        endDate = startDate.plusMonths(1);
        fra = new LiborFRA(startDate, endDate, -0.00175, true, dcType);
        fras.add(fra);

        startDate = startDate.plusMonths(1);
        endDate = startDate.plusMonths(1);
        fra = new LiborFRA(startDate, endDate, -0.00143, true, dcType);
        fras.add(fra);

        startDate = startDate.plusMonths(1);
        endDate = startDate.plusMonths(1);
        fra = new LiborFRA(startDate, endDate, -0.00126, true, dcType);
        fras.add(fra);

        startDate = startDate.plusMonths(1);
        endDate = startDate.plusMonths(1);
        fra = new LiborFRA(startDate, endDate, -0.00126, true, dcType);
        fras.add(fra);

        FrequencyTypes fixedFreq = FrequencyTypes.ANNUAL;
        dcType = DayCountTypes.THIRTY_360;

        maturityDate = settlementDate.plusMonths(24);
        LiborSwap swap1 =  new LiborSwap(settlementDate, maturityDate, -0.001506, fixedFreq, dcType);
        swaps.add(swap1);

        maturityDate = settlementDate.plusMonths(36);
        LiborSwap swap2 =  new LiborSwap(settlementDate, maturityDate, -0.000185, fixedFreq, dcType);
        swaps.add(swap2);

        maturityDate = settlementDate.plusMonths(48);
        LiborSwap swap3 =  new LiborSwap(
                settlementDate,
                maturityDate,
                0.001358,
                fixedFreq,
                dcType);
        swaps.add(swap3);

        maturityDate = settlementDate.plusMonths(60);
        LiborSwap swap4 =  new LiborSwap(
                settlementDate,
                maturityDate,
                0.0027652,
                fixedFreq,
                dcType);
        swaps.add(swap4);

        maturityDate = settlementDate.plusMonths(72);
        LiborSwap swap5 =  new LiborSwap(
                settlementDate,
                maturityDate,
                0.0041539,
                fixedFreq,
                dcType);
        swaps.add(swap5);

        maturityDate = settlementDate.plusMonths(84);
        LiborSwap swap6 =  new LiborSwap(
                settlementDate,
                maturityDate,
                0.0054604,
                fixedFreq,
                dcType);
        swaps.add(swap6);

        maturityDate = settlementDate.plusMonths(96);
        LiborSwap swap7 =  new LiborSwap(
                settlementDate,
                maturityDate,
                0.006674,
                fixedFreq,
                dcType);
        swaps.add(swap7);

        maturityDate = settlementDate.plusMonths(108);
        LiborSwap swap8 =  new LiborSwap(
                settlementDate,
                maturityDate,
                0.007826,
                fixedFreq,
                dcType);
        swaps.add(swap8);

        maturityDate = settlementDate.plusMonths(120);
        LiborSwap swap9 =  new LiborSwap(
                settlementDate,
                maturityDate,
                0.008821,
                fixedFreq,
                dcType);
        swaps.add(swap9);

        maturityDate = settlementDate.plusMonths(132);
        LiborSwap swap10 =  new LiborSwap(
                settlementDate,
                maturityDate,
                0.0097379,
                fixedFreq,
                dcType);
        swaps.add(swap10);

        maturityDate = settlementDate.plusMonths(144);
        LiborSwap swap11 =  new LiborSwap(
                settlementDate,
                maturityDate,
                0.0105406,
                fixedFreq,
                dcType);
        swaps.add(swap11);

        maturityDate = settlementDate.plusMonths(180);
        LiborSwap swap12 =  new LiborSwap(
                settlementDate,
                maturityDate,
                0.0123927,
                fixedFreq,
                dcType);
        swaps.add(swap12);

        maturityDate = settlementDate.plusMonths(240);
        LiborSwap swap13 =  new LiborSwap(
                settlementDate,
                maturityDate,
                0.0139882,
                fixedFreq,
                dcType);
        swaps.add(swap13);

        maturityDate = settlementDate.plusMonths(300);
        LiborSwap swap14 =  new LiborSwap(
                settlementDate,
                maturityDate,
                0.0144972,
                fixedFreq,
                dcType);
        swaps.add(swap14);

        maturityDate = settlementDate.plusMonths(360);
        LiborSwap swap15 =  new LiborSwap(
                settlementDate,
                maturityDate,
                0.0146081,
                fixedFreq,
                dcType);
        swaps.add(swap15);

        maturityDate = settlementDate.plusMonths(420);
        LiborSwap swap16 =  new LiborSwap(
                settlementDate,
                maturityDate,
                0.01461897,
                fixedFreq,
                dcType);
        swaps.add(swap16);

        maturityDate = settlementDate.plusMonths(480);
        LiborSwap swap17 =  new LiborSwap(
                settlementDate,
                maturityDate,
                0.014567455,
                fixedFreq,
                dcType);
        swaps.add(swap17);

        maturityDate = settlementDate.plusMonths(540);
        LiborSwap swap18 =  new LiborSwap(
                settlementDate,
                maturityDate,
                0.0140826,
                fixedFreq,
                dcType);
        swaps.add(swap18);

        maturityDate = settlementDate.plusMonths(600);
        LiborSwap swap19 =  new LiborSwap(
                settlementDate,
                maturityDate,
                0.01436822,
                fixedFreq,
                dcType);
        swaps.add(swap19);

        LiborCurve liborCurve = new LiborCurve("USD", settlementDate, depos, fras, futures, swaps, InterpolationTypes.FLAT_FORWARDS);
        liborCurve.buildCurve();

        //Check calibration
        for (LiborDeposit depo : depos) {
            double v = depo.value(settlementDate, liborCurve);
            //System.out.println("DEPO VALUE:" + depo.maturityDate +  "  "+ v);
        }

        for (LiborFRA f : fras) {
            double v = f.value(settlementDate, liborCurve);
            //System.out.println("FRA VALUE:" + f.maturityDate + "  " + v);
        }

        for (LiborSwap swap : swaps) {
            double v = swap.value(settlementDate, liborCurve, liborCurve, Optional.empty(), 1_000_000.0);
            //System.out.println("SWAP VALUE:" + swap.maturityDate + "  " + v);
        }

        return liborCurve;
    }
}
