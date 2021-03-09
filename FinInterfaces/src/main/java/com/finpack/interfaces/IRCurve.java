package com.finpack.interfaces;

import java.time.LocalDate;
import java.util.List;

public interface IRCurve {
    double df(double dt);
    double df(LocalDate dt);
    LocalDate getCurveDate();
    List<Double> getTimes();
    List<Double> getValues();
    List<LocalDate> getDates();

}
