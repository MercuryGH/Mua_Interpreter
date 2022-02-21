package mua.util;

import mua.Type;

public final class TypeInferer {

    public static Type infer(String s) {
        final String numberRegex = "^-?[0-9]+(.[0-9]+)?$";
        if (s.matches(numberRegex)) {
            return Type.NUMBER;
        } else if (s.charAt(0) == '"') {
            return Type.WORD;
        } else if (s.charAt(0) == '[') {
            return Type.LIST;
        } else if (s.equals("true") || s.equals("false")) {
            return Type.BOOL;
        } else {
            return Type.UNDEF;
        }
    }

}
