package mua;

import java.util.ArrayDeque;
import java.util.Scanner;

import mua.util.CommandOperator;

public class Main {
    public static Namespace globalNamespace = new Namespace();
    /**
     * localNamespace keeps track on current scope, 也用于限制程序在全局环境下不要绑定变量
     * 若程序运行在全局环境下，则localNamespace为null
     * 否则，localNamespace不为null，且内部存储在当前顶层作用域下的变量
     * 当函数调用或返回时，localNamespace都将发生变化，具体而言：
     * 1. 函数调用时，localNamespace = new Namespace(funcNamespace)
     * 2. 函数返回时，localNamespace = oldNamespace，其中oldNamespace为上次对localNamespace赋值前，localNamespace的值
     */
    // public static Namespace localNamespace;
    // 使用传参 Namespace namespace 来代替 localNamespace

    // Stack的遍历居然是与push次序相同的，很不好用，所以直接ArrayDeque了
    public static ArrayDeque<Namespace> contextNamespaceStack = new ArrayDeque<>();
    

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        globalNamespace.add("pi", new Value(Type.NUMBER, "3.14159"));
        // contextNamespaceStack.push(globalNamespace);
        CommandOperator.run(in, globalNamespace);
    }
}
