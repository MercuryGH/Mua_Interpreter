package mua.util;

import java.util.Scanner;

import mua.Type;
import mua.Value;

public final class OperationHelper {


    public static String getListLiteral(Scanner in, String s) {
        // TO*DO: rewrite it with regex
        while (isBracketBalance(s) == false) {
            s = s + " " + in.next();
        }
        if (isEmptyBracket(s)) {
            return "";
        }
        return s.substring(1, s.length() - 1);
    }

    public static String bool2Num(String s) {
        if (s.equals("true")) {
            return "1";
        } else if (s.equals("false")) {
            return "0";
        }
        return null;
    }

    public static String num2Bool(double s) {
        if (s == 0) { // BAD: double equals to 0
            return "false";
        } else {
            return "true";
        }
    }

    public static Value getNextListElement(Scanner in) {
        if (in.hasNext() == false) {
            return null;
        }

        String res = in.next();
        Type type = TypeInferer.infer(res);
        if (res.startsWith("[")) {
            res = getListLiteral(in, res);
            type = Type.LIST;
        } 

        if (type == Type.UNDEF) {  // fix bug
            type = Type.WORD;
        }

        return new Value(type, res);
    }

    public static Value getLastListElement(Scanner in) {
        Value ret = null;
        while (true) {
            Value v = getNextListElement(in);
            if (v == null) {
                break;
            } 
            ret = v;
        }
        return ret;
    }

    private static boolean isEmptyBracket(String s) {
        return s.split(" |\\[|\\]").length == 0;
    }

    private static boolean isBracketBalance(String s) {
        // only count equal num, not fully implmented.
        final char lb = '[';
        final char rb = ']';
        char[] cs = s.toCharArray();

        int cnt1 = 0;
        int cnt2 = 0;
        for (char c : cs) {
            if (c == lb) {
                cnt1++;
            } else if (c == rb) {
                cnt2++;
            }
        }

        return cnt1 == cnt2;
    }

}
