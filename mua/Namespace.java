package mua;

import java.util.HashMap;

public class Namespace {
    private HashMap<String, Value> variables = new HashMap<>();

    public Namespace() {
        // this.initNamespace();
    }

    @SuppressWarnings("unchecked")
    public Namespace(Namespace n) {  // 拷贝构造函数
        this.variables = (HashMap<String, Value>)n.getVariables().clone();
    }

    public void addNamespaceWithReplacement(Namespace n) {
        if (n == null) {
            return;
        }
        HashMap<String, Value> nVariables = n.getVariables();
        nVariables.forEach((k, v) -> {  // putWithReplacement
            this.add(k, v);
        });
    }

    public void addNamespaceIfAbsent(Namespace n) {
        if (n == null) {
            return;
        }
        HashMap<String, Value> nVariables = n.getVariables();
        nVariables.forEach((k, v) -> {  // putIfAbsent
            if (this.exist(k) == false) {
                this.add(k, v);
            }
        });
    }

    public void initNamespace() {
        // this.add("pi", new Value(Type.NUMBER, "3.14159"));
    }

    public void add(String s, Value v) {
        this.variables.put(s, v);  // 会覆盖旧的值
    }

    public Value remove(String s) {
        if (this.variables.containsKey(s)) {
            Value ret = this.variables.get(s);
            this.variables.remove(s);
            return ret;
        }
        return null;
    }

    public Value get(String s) {
        if (this.getVariables().containsKey(s)) {
            return this.getVariables().get(s);
        }

        for (Namespace namespace : Main.contextNamespaceStack) {
            if (namespace.getVariables().containsKey(s)) {
                return namespace.getVariables().get(s);
            }
        }

        return Main.globalNamespace.getVariables().get(s);
    }

    public boolean exist(String s) {
        if (this.getVariables().containsKey(s)) {
            return true;
        }

        for (Namespace namespace : Main.contextNamespaceStack) {
            if (namespace.getVariables().containsKey(s)) {
                return true;
            }
        }

        return Main.globalNamespace.getVariables().containsKey(s);
    }

    public Value eraseAll() {
        this.variables.clear();
        return new Value(Type.BOOL, "true");
    }

    public Value postAll() {
        String ret = null;
        for (String name : this.variables.keySet()) {
            if (ret == null) {
                ret = name;
            } else {
                ret = ret + " " + name;
            }
        }
        return new Value(Type.LIST, ret);
    }

    public HashMap<String, Value> getVariables() {
        return variables;
    }

    public void printAllVarialbes(String s) {
        this.variables.forEach((k, v) -> {
            System.out.println(k + ": " + v);
        });
    }

    public void printAllVarialbesWithStack() {
        System.out.println("------------ " + "DEBUG" + " ------------");
        this.variables.forEach((k, v) -> {
            System.out.println(k);
        });
        System.out.println();
        for (Namespace namespace : Main.contextNamespaceStack) {
            namespace.variables.forEach((k, v) -> {
                System.out.println(k);
            });
            System.out.println();
        }
        System.out.println();
        Main.globalNamespace.variables.forEach((k, v) -> {
            System.out.println(k);
        });
        System.out.println("--------- printAllVarialbes END --------");
    }
}
