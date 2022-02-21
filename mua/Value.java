package mua;

import java.util.HashSet;
import java.util.Scanner;

import mua.util.OperationPerformer;

public class Value {
    public Type type;
    public String data;
    // TO*DO: optimize to nan-boxing or something

    public Value(Type type, String data) {
        this.type = type;
        this.data = data;
    }

    public Value(Value v) {
        this.type = v.type;
        this.data = v.data;
        this.contextNamespace = v.contextNamespace;
    }

    // -------------- ADDED in p3: 仅函数会用到这些 --------------    

    public Namespace contextNamespace;

    // 其实是“是否为含有两个表的表”
    public boolean isFunction() {
        final String functionRegex = "\\[.*\\]\\s*\\[.*\\]";
        if (this.type == Type.LIST && this.data.trim().matches(functionRegex)) {
            return true;
        }
        return false;
    }

    public void saveContext(Namespace namespace) {
        // v is a function
        // 获取v中的所有“名字”
        HashSet<String> varNames = this.getVarNamesInFunctionCode();

        this.contextNamespace = new Namespace();
        for (String varName : varNames) {
            Value valueTobeBinded = namespace.get(varName);
            if (valueTobeBinded != null) {  // 现在就可以绑定
                this.contextNamespace.add(varName, new Value(valueTobeBinded));  // 拷贝然后绑定
            }
        }
    }

    // 读到一个并非在全局空间中定义的函数，就要保存函数中代码部分用到的变量名
    // 即便不调用也要这样。
    // 只能想到暴力做法……
    private HashSet<String> getVarNamesInFunctionCode() {
        HashSet<String> ret = new HashSet<>();

        Value codes = OperationPerformer.last(this);
        Scanner wordScanner = new Scanner(codes.data);
        while (wordScanner.hasNext()) {
            String word = wordScanner.next();
            String badCharsRegex = "[:\\[\\]\"]";  // 过滤掉 :, ", [, ]
            word = word.replaceAll(badCharsRegex, "");
            if (word.length() == 0) {  // 不要空串
                continue;
            }
            ret.add(word);
        }
        wordScanner.close();
        return ret;
    }

    // -------------- END OF ADDED in p3 -------------- 

    @Override
    public String toString() {
        return data;
    }

    public boolean isTrue() {
        return data.equals("true") || data.equals("1");
    }

    public boolean isFalse() {
        return data.equals("false") || data.equals("0");
    }

    public static void main(String[] args) {
        Value v = new Value(Type.LIST, "[g] x[ ]");
        boolean flag = v.isFunction();
        System.out.println(flag);
    }
}
