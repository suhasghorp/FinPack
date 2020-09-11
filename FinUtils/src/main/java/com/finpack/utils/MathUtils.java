package com.finpack.utils;

import java.util.List;

public class MathUtils {

    public static boolean checkMonotonicity(List<Double> x){
        for (int i = 1; i < x.size(); i++){
            if (x.get(i) <= x.get(i-1))
                return false;
        }
        return true;
    }
}
