package moe.yahvk.tfc_cuisine.math;

import java.util.Map;

public interface Expression {
    double eval(Map<String, Double> vars);
}