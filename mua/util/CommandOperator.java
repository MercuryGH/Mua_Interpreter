package mua.util;

import java.util.Scanner;

import mua.Main;
import mua.Namespace;
import mua.OpType;
import mua.Type;
import mua.Value;

public final class CommandOperator {
    public static boolean debugPin = false;

    public static void run(Scanner in, Namespace namespace) {
        while (in.hasNext()) {
            CommandOperator.read(in, namespace);
        }
    }

    public static Value read(Scanner in, Namespace namespace) {
        String indicator = in.next();
        switch (indicator) {
            // Those are operations that can be an indicator
            case "make":
                Value v1 = getNextArg(in, namespace);
                Value v2 = getNextArg(in, namespace);
                return OperationPerformer.make(v1, v2, namespace);

            case "print":
                Value v = getNextArg(in, namespace);
                return OperationPerformer.print(v);

            case "erase":
                v = getNextArg(in, namespace);
                return OperationPerformer.erase(v, namespace);

            case "run":
                v = getNextArg(in, namespace);
                return OperationPerformer.handleRun(v, namespace);

            case "if":
                v1 = getNextArg(in, namespace);
                v2 = getNextArg(in, namespace);
                Value v3 = getNextArg(in, namespace);
                return OperationPerformer.handleIf(v1, v2, v3, namespace);

            case "return":
                debugPin = true;
                v = getNextArg(in, namespace);
                return OperationPerformer.handleReturn(in, namespace, v);

            case "export":
                v = getNextArg(in, namespace);
                return OperationPerformer.export(v, namespace);

            case "save":
                v = getNextArg(in, namespace);
                return OperationPerformer.save(v, namespace);

            case "load":
                v = getNextArg(in, namespace);
                return OperationPerformer.load(v, namespace);

            case "erall":
                return OperationPerformer.erall(namespace);

            case "poall":
                return OperationPerformer.poall(namespace);

            default:
                // System.out.println(indicator);
                return Function.invoke(indicator, in, namespace);
        }
    }

    public static Value getNextArg(Scanner in, Namespace namespace) {
        if (in.hasNext() == false) {
            return null;
        }

        String s = in.next();
        Type inferedType = TypeInferer.infer(s);

        switch (inferedType) {
            case NUMBER:
                return new Value(Type.NUMBER, s);
            case BOOL:
                return new Value(Type.BOOL, s);
            case LIST:
                Value v = new Value(Type.LIST, OperationHelper.getListLiteral(in, s));
                if (v.isFunction() && namespace != Main.globalNamespace) {
                    v.saveContext(namespace); // 提前绑定！！！
                }
                return v;
            case WORD:
                return new Value(Type.WORD, s.substring(1));
            default:
        }

        if (s.charAt(0) == ':') {
            String name = s.substring(1);
            return namespace.get(name);
        }

        // TO*DO: better design pattern (NOT switch-case)
        if (Judger.isArithOp(s)) {
            // try {
            OpType opType;
            switch (s) {
                case "add":
                    opType = OpType.ADD;
                    break;
                case "sub":
                    opType = OpType.SUB;
                    break;
                case "mul":
                    opType = OpType.MUL;
                    break;
                case "div":
                    opType = OpType.DIV;
                    break;
                case "mod":
                    opType = OpType.MOD;
                    break;
                default:
                    opType = OpType.ADD;
            }
            Value v1 = getNextArg(in, namespace);
            Value v2 = getNextArg(in, namespace);
            return OperationPerformer.arithOp(opType, v1, v2);
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
        }

        // "first", "butfirst", "butlast", "save", "load",
        // "export", "return", "print", "thing", "not",
        // "erase", "run", "isname", "isnumber", "isword",
        // "islist", "isbool", "isempty"

        // unaryOp. This is bad design (copy & paste)
        // TO*DO: Refactor to ENUM
        if (Judger.isUnaryOp(s)) {
            Value v = getNextArg(in, namespace);
            if (s.equals("isname")) {
                return OperationPerformer.isname(v, namespace);
            }
            if (s.equals("isempty")) {
                return OperationPerformer.isempty(v);
            }
            if (s.equals("isnumber")) {
                return OperationPerformer.isnumber(v);
            }
            if (s.equals("isword")) {
                return OperationPerformer.isword(v);
            }
            if (s.equals("islist")) {
                return OperationPerformer.islist(v);
            }
            if (s.equals("isbool")) {
                return OperationPerformer.isbool(v);
            }
            if (s.equals("not")) {
                return OperationPerformer.not(v);
            }
            if (s.equals("thing")) {
                return OperationPerformer.thing(v, namespace);
            }
            if (s.equals("print")) {
                return OperationPerformer.print(v);
            }
            if (s.equals("run")) {
                return OperationPerformer.handleRun(v, namespace);
            }
            if (s.equals("erase")) {
                return OperationPerformer.erase(v, namespace);
            }
            if (s.equals("return")) {
                return OperationPerformer.handleReturn(in, namespace, v);
            }
            if (s.equals("first")) {
                return OperationPerformer.first(v);
            }
            if (s.equals("butfirst")) {
                return OperationPerformer.butfirst(v);
            }
            if (s.equals("butlast")) {
                return OperationPerformer.butlast(v);
            }
            if (s.equals("save")) {
                return OperationPerformer.save(v, namespace);
            }
            if (s.equals("load")) {
                return OperationPerformer.load(v, namespace);
            }
            if (s.equals("export")) {
                return OperationPerformer.export(v, namespace);
            }
            if (s.equals("random")) {
                return OperationPerformer.random(v);
            }
            if (s.equals("int")) {
                return OperationPerformer.handleInt(v);
            }
            if (s.equals("sqrt")) {
                return OperationPerformer.sqrt(v);
            }
        }

        // "sentence", "make", "add", "list", "word"
        // "join", "sub", "mul", "div", "mod", "eq",
        // "gt", "lt", "and", "or"

        // binaryOp. Refactor needed.
        if (Judger.isBinaryOp(s)) {
            Value v1 = getNextArg(in, namespace);
            Value v2 = getNextArg(in, namespace);
            if (s.equals("eq")) {
                return OperationPerformer.eq(v1, v2);
            }
            if (s.equals("gt")) {
                return OperationPerformer.gt(v1, v2);
            }
            if (s.equals("lt")) {
                return OperationPerformer.lt(v1, v2);
            }
            if (s.equals("and")) {
                return OperationPerformer.and(v1, v2);
            }
            if (s.equals("or")) {
                return OperationPerformer.or(v1, v2);
            }
            if (s.equals("make")) {
                return OperationPerformer.make(v1, v2, namespace);
            }
            if (s.equals("word")) {
                return OperationPerformer.word(v1, v2);
            }
            if (s.equals("join")) {
                return OperationPerformer.join(v1, v2);
            }
            if (s.equals("sentence")) {
                return OperationPerformer.sentence(v1, v2);
            }
            if (s.equals("list")) {
                return OperationPerformer.list(v1, v2);
            }
        }

        // no-nary op
        if (s.equals("read")) {
            return OperationPerformer.read(in);
        }
        if (s.equals("readlist")) {
            return OperationPerformer.readlist(in);
        }
        if (s.equals("erall")) {
            return OperationPerformer.erall(namespace);
        }
        if (s.equals("poall")) {
            return OperationPerformer.poall(namespace);
        }

        // ternary op
        if (s.equals("if")) {
            Value v1 = getNextArg(in, namespace);
            Value v2 = getNextArg(in, namespace);
            Value v3 = getNextArg(in, namespace);
            return OperationPerformer.handleIf(v1, v2, v3, namespace);
        }

        // if (debugPin == true && s.equals("g")) {
        // Value functionDef = namespace.get(s);
        // System.out.println(functionDef);
        // while (true);
        // }

        return Function.invoke(s, in, namespace);
    }
}
