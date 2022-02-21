package mua.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import mua.Main;
import mua.Namespace;
import mua.OpType;
import mua.Type;
import mua.Value;

public final class OperationPerformer {
    public static Value make(Value v1, Value v2, Namespace namespace) {
        namespace.add(v1.data, v2);
        if (v2.isFunction() && namespace != Main.globalNamespace) {  // is function
            v2.saveContext(namespace);
        }
        return v2;
    }

    public static Value thing(Value v, Namespace namespace) {
        return namespace.get(v.data);
    }

    public static Value print(Value v) {
        System.out.println(v.data);
        return v;
    }

    public static Value read(Scanner in) {
        String readStr = in.next();
        Type type = TypeInferer.infer(readStr);
        if (type == Type.UNDEF) { // read 不能自动加 "，补上去
            type = Type.WORD;
        }
        return new Value(type, readStr);
    }

    public static Value arithOp(OpType opType, Value v1, Value v2) {
        Type resType = Type.NUMBER;

        // DEBUG:
        // System.out.println("DEBUG: ");
        // System.out.println((v1 == null ? "null" : v1.data + " ") + " " + (v2 == null ? "null" : v2.data));

        String data1 = v1.data;
        String data2 = v2.data;
        if (v1.type == Type.BOOL || v2.type == Type.BOOL) {
            resType = Type.BOOL;
            data1 = OperationHelper.bool2Num(v1.data);
            data2 = OperationHelper.bool2Num(v2.data);
        }

        double num1 = Double.valueOf(data1);
        double num2 = Double.valueOf(data2);
        double res = 0;

        switch (opType) {
        case ADD:
            res = num1 + num2;
            break;
        case SUB:
            res = num1 - num2;
            break;
        case MUL:
            res = num1 * num2;
            break;
        case DIV:
            res = num1 / num2;
            break;
        case MOD:
            res = num1 % num2;
            break;

        default:
        }

        String resData = null;
        if (resType == Type.BOOL) {
            resData = OperationHelper.num2Bool(res);
        } else {
            resData = String.valueOf(res);
        }

        return new Value(resType, resData);
    }

    public static Value erase(Value v, Namespace namespace) {
        return namespace.remove(v.data);
    }

    public static Value isname(Value v, Namespace namespace) {
        boolean res = namespace.exist(v.data);
        return new Value(Type.BOOL, String.valueOf(res));
    }

    public static Value isnumber(Value v) {
        final String numberRegex = "^-?[0-9]+(.[0-9]+)?$";
        boolean res;
        if (v.type == Type.NUMBER) {
            res = true;
        } else if (v.type == Type.WORD) {
            res = v.data.matches(numberRegex);
        } else {
            res = false;
        }
        return new Value(Type.BOOL, String.valueOf(res));
    }

    public static Value isword(Value v) {
        boolean res = v.type == Type.WORD;
        return new Value(Type.BOOL, String.valueOf(res));
    }

    public static Value islist(Value v) {
        boolean res = v.type == Type.LIST;
        return new Value(Type.BOOL, String.valueOf(res));
    }

    public static Value isbool(Value v) {
        boolean res = v.type == Type.BOOL;
        return new Value(Type.BOOL, String.valueOf(res));
    }

    public static Value isempty(Value v) {
        boolean res = v.data.length() == 0;
        return new Value(Type.BOOL, String.valueOf(res));
    }

    public static Value eq(Value v1, Value v2) {
        boolean res;
        if (v1.type != v2.type) {
            res = false;
        } else if (v1.type == Type.NUMBER) { // NUMBER
            double data1 = Double.parseDouble(v1.data);
            double data2 = Double.parseDouble(v2.data);
            res = data1 == data2;
        } else { // WORD
            res = (v1.data).equals(v2.data);
        }
        return new Value(Type.BOOL, String.valueOf(res));
    }

    public static Value gt(Value v1, Value v2) {
        boolean res;
        if (v1.type == Type.NUMBER && v2.type == Type.NUMBER) { // NUMBER
            double data1 = Double.parseDouble(v1.data);
            double data2 = Double.parseDouble(v2.data);
            res = data1 > data2;
        } else { // WORD
            res = v1.data.compareTo(v2.data) > 0;
        }
        return new Value(Type.BOOL, String.valueOf(res));
    }

    public static Value lt(Value v1, Value v2) {
        boolean res;
        if (v1.type == Type.NUMBER && v2.type == Type.NUMBER) { // NUMBER
            double data1 = Double.parseDouble(v1.data);
            double data2 = Double.parseDouble(v2.data);
            res = data1 < data2;
        } else { // WORD
            res = v1.data.compareTo(v2.data) < 0;
        }
        return new Value(Type.BOOL, String.valueOf(res));
    }

    public static Value and(Value v1, Value v2) {
        boolean num1 = Boolean.valueOf(v1.data);
        boolean num2 = Boolean.valueOf(v2.data);

        boolean res = num1 && num2;

        return new Value(Type.BOOL, String.valueOf(res));
    }

    public static Value or(Value v1, Value v2) {
        boolean num1 = Boolean.valueOf(v1.data);
        boolean num2 = Boolean.valueOf(v2.data);

        boolean res = num1 || num2;

        return new Value(Type.BOOL, String.valueOf(res));
    }

    public static Value not(Value v) {
        boolean num = Boolean.valueOf(v.data);
        boolean res = !num;
        return new Value(Type.BOOL, String.valueOf(res));
    }

    public static Value handleRun(Value v, Namespace namespace) {
        if (v.data.length() == 0 || v.data.length() == 1) {
            return v;
        }

        Scanner listScanner = new Scanner(v.data);
        Value ret = null;
        while (listScanner.hasNext()) {
            ret = CommandOperator.read(listScanner, namespace);
        }
        return ret;
    }

    public static Value handleIf(Value v1, Value v2, Value v3, Namespace namespace) {
        // assert v1.type == Type.BOOL
        if (v1.isTrue()) {
            return handleRun(v2, namespace);
        } else {
            return handleRun(v3, namespace);
        }
    }

    public static Value handleReturn(Scanner in, Namespace namespace, Value v) {
        namespace.add("__return_value", v);
        while (in.hasNext()) { // let PC moves to END_OF_FUNC
            in.nextLine();
        }
        return v;
    }

    public static Value export(Value v, Namespace namespace) {
        mua.Main.globalNamespace.add(v.data, namespace.get(v.data));
        return v;
    }

    public static Value readlist(Scanner in) {
        in.nextLine();
        String readStr = in.nextLine(); // [ a b c ]
        readStr = readStr.substring(1, readStr.length() - 1); // a b c
        return new Value(Type.LIST, readStr);
    }

    public static Value word(Value v1, Value v2) {
        return new Value(Type.WORD, v1.data + v2.data);
    }

    public static Value sentence(Value v1, Value v2) {
        return new Value(Type.LIST, v1.data + " " + v2.data);
    }

    public static Value list(Value v1, Value v2) {
        String data1 = v1.data;
        String data2 = v2.data;
        if (v1.type == Type.LIST) {
            data1 = "[" + data1 + "]";
        }
        if (v2.type == Type.LIST) {
            data2 = "[" + data2 + "]";
        }

        return new Value(Type.LIST, data1 + " " + data2);
    }

    public static Value join(Value v1, Value v2) {
        String res = v2.data;
        if (v2.type == Type.LIST) {
            res = "[" + res + "]";
        }
        if (isempty(v1).isFalse()) {
            res = v1.data + " " + res;
        }
        return new Value(Type.LIST, res);
    }

    public static Value first(Value v) {
        if (v.type != Type.LIST) { // WORD
            String res = v.data.substring(0, 1);
            return new Value(Type.WORD, res);
        } else { // LIST
            Scanner in = new Scanner(v.data);
            return OperationHelper.getNextListElement(in);
        }
    }

    public static Value last(Value v) {
        if (v.type != Type.LIST) {
            String res = v.data.substring(v.data.length() - 1);
            return new Value(Type.WORD, res);
        } else {
            Scanner in = new Scanner(v.data);
            return OperationHelper.getLastListElement(in);
        }
    }

    public static Value butfirst(Value v) {
        if (v.type != Type.LIST) {
            String res = v.data.substring(1);
            return new Value(Type.WORD, res);
        } else {
            Scanner in = new Scanner(v.data);
            Value cur = OperationHelper.getNextListElement(in); // 舍弃
            // String res = "";
            StringBuilder res = new StringBuilder();
            while (true) {
                cur = OperationHelper.getNextListElement(in);
                if (cur == null) {
                    break;
                }
                if (res.length() != 0) {
                    res.append(" ");
                }

                if (cur.type != Type.LIST) {
                    res.append(cur.data);
                } else {
                    res.append("[" + cur.data + "]");
                }
            }
            return new Value(Type.LIST, res.toString());
        }
    }

    public static Value butlast(Value v) {
        if (v.type != Type.LIST) {
            String res = v.data.substring(0, v.data.length() - 1);
            return new Value(Type.WORD, res);
        } else {
            Scanner in = new Scanner(v.data);
            Value cur = null;
            Value prev = null;
            StringBuilder res = new StringBuilder();
            while (true) {
                cur = OperationHelper.getNextListElement(in);
                if (cur == null) {
                    break;
                }
                if (res.length() != 0) {
                    res.append(" ");
                }

                if (prev != null) {
                    if (prev.type != Type.LIST) {
                        res.append(prev.data);
                    } else {
                        res.append("[" + prev.data + "]");
                    }
                }
                prev = new Value(cur);
            }
            return new Value(Type.LIST, res.toString());
        }
    }

    public static Value random(Value v) {
        Random random = new Random();
        double upperBound = Double.valueOf(v.data);
        double randomValue = random.nextDouble() * upperBound;
        return new Value(Type.NUMBER, String.valueOf(randomValue));
    }

    public static Value handleInt(Value v) {
        double originValue = Double.valueOf(v.data);
        double res = Math.floor(originValue);
        return new Value(Type.NUMBER, String.valueOf(res));
    }

    public static Value sqrt(Value v) {
        double originValue = Double.valueOf(v.data);
        double res = Math.sqrt(originValue);
        return new Value(Type.NUMBER, String.valueOf(res));
    }

    // save <word>：以源码形式保存当前命名空间在word文件中，返回文件名
    // load <word>：从word文件中装载内容，加入当前命名空间，返回true
    public static Value save(Value v, Namespace namespace) {
        String fileName = v.data;
        File file = new File(fileName);
        final String makeWordStatement = "make \"%s \"%s\n";
        final String makeListStatement = "make \"%s [%s]\n";
        final String makeNumBoolStatement = "make \"%s %s\n";

        try {
            file.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            HashMap<String, Value> varialbes = namespace.getVariables();

            for (String name : varialbes.keySet()) {
                StringBuilder line = new StringBuilder();
                Type type = varialbes.get(name).type;
                String data = varialbes.get(name).data;
                if (type == Type.WORD) {
                    line.append(String.format(makeWordStatement, name, data));
                } else if (type == Type.LIST) {
                    line.append(String.format(makeListStatement, name, data));
                } else {
                    line.append(String.format(makeNumBoolStatement, name, data));
                }
                out.write(line.toString());
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return v;
    }

    public static Value load(Value v, Namespace namespace) {
        String fileName = v.data;
        File file = new File(fileName);
        int fileLength = (int) file.length();

        byte[] fileContent = new byte[fileLength];
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            in.read(fileContent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String content = new String(fileContent);
        Scanner fileScanner = new Scanner(content);

        CommandOperator.run(fileScanner, namespace);

        return new Value(Type.BOOL, "true");
    }

    public static Value erall(Namespace namespace) {
        return namespace.eraseAll();
    }

    // 2021-12-16 p4 spec更新时，这个函数已经没用了
    public static Value poall(Namespace namespace) {
        return namespace.postAll();
    }

    // erase <name>：清除word所绑定的值，返回原绑定的值
    // isname <word>：返回word是否是一个名字，true/false
    // run <list>：运行list中的代码，返回list中执行的最后一个op的返回值
    // eq, gt, lt：<operator> <number|word> <number|word>
    // and, or：<operator> <bool> <bool>
    // not：not <bool>

    // if <bool> <list1>
    // <list2>：如果bool为真，则执行list1，否则执行list2。list均可以为空表，返回list1或list2执行后的结果。如果被执行的是空表，返回空表。如果被执行的表只有一项，且非OP，返回该项。
    // isnumber <value>：返回value是否是数字
    // isword <value>：返回value是否是字
    // islist <value>：返回value是否是表
    // isbool <value>：返回value是否是布尔量
    // isempty <word|list>: 返回word/list是否是空字/空列表

    // 字表处理
    // readlist：返回一个从标准输入读取的一行，构成一个表，行中每个以空格分隔的部分是list的一个元素，元素的类型为字
    // 用readlist读入的只可能是单层的表
    // word <word> <word|number|bool>：将两个word合并为一个word，第二个值可以是word、number或bool
    // sentence <value1> <value2>：将value1和value2合并成一个表，两个值的元素并列，value1的在value2的前面
    // list <value1> <value2>：将两个值合并为一个表，如果值为表，则不打开这个表
    // join <list> <value>：将value作为list的最后一个元素加入到list中（如果value是表，则整个value成为表的最后一个元素）
    // first <word|list>：返回word的第一个字符，或list的第一个元素
    // last <word|list>：返回word的最后一个字符，list的最后一个元素
    // butfirst <word|list>：返回除第一个元素外剩下的表，或除第一个字符外剩下的字
    // butlast <word|list>：返回除最后一个元素外剩下的表，或除最后一个字符外剩下的字
    // 数值计算
    // random <number>：返回[0,number)的一个随机数
    // int <number>: floor the int
    // sqrt <number>：返回number的平方根
    // 其他操作
    // save <word>：以源码形式保存当前命名空间在word文件中，返回文件名
    // load <word>：从word文件中装载内容，加入当前命名空间，返回true
    // erall：清除当前命名空间的全部内容，返回true
    // poall：返回当前命名空间的全部名字的list
    // 既有名字
    // 系统提供了一些常用的量，或可以由其他操作实现但是常用的操作，作为固有的名字。这些名字是可以被删除（erase）的。
    // pi：3.14159
}
