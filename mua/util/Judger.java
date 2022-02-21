package mua.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Judger {
    public static final List<String> ARITH_OPS = new ArrayList<String>(
            Arrays.asList("add", "sub", "mul", "div", "mod"));
    public static final List<String> TYPE_JUDGERS = new ArrayList<String>(
            Arrays.asList("isnumber", "isword", "islist", "isbool"));
    public static final List<String> COMPARE_OPS = new ArrayList<String>(Arrays.asList("eq", "gt", "lt"));

    public static final List<String> UNARY_OPS = new ArrayList<>(
            Arrays.asList("first", "butfirst", "butlast", "save", "load", "export", "return", "print", "thing", "not",
                    "erase", "run", "isname", "isnumber", "isword", "islist", "isbool", "isempty", "random", "int", "sqrt"));

    public static final List<String> BINARY_OPS = new ArrayList<>(Arrays.asList("sentence", "word", "make", "add",
            "list", "join", "sub", "mul", "div", "mod", "eq", "gt", "lt", "and", "or"));

    public static boolean isArithOp(String s) {
        return ARITH_OPS.contains(s);
    }

    public static boolean isTypeJudger(String s) {
        return TYPE_JUDGERS.contains(s);
    }

    public static boolean isCompareOp(String s) {
        return COMPARE_OPS.contains(s);
    }

    public static boolean isUnaryOp(String s) {
        return UNARY_OPS.contains(s);
    }

    public static boolean isBinaryOp(String s) {
        return BINARY_OPS.contains(s);
    }
}
