package com.finpack.interfaces;

import java.time.LocalDate;

public interface IRCurve {
    double df(double dt);
    double df(LocalDate dt);
}
