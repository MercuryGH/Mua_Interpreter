package mua.util;

import java.util.Scanner;

import mua.Main;
import mua.Namespace;
import mua.Value;

public class Function {
    public static Value invoke(String s, Scanner in, Namespace namespace) {
        //Debug
        // namespace.printAllVarialbes();

        Value functionDef = namespace.get(s);
        if (functionDef == null) {
            System.out.printf("Function named '%s' not found!!\n", s);
            return null;
        }

        Scanner funcDefScanner = new Scanner(functionDef.data);
        Namespace newNamespace = new Namespace();

        Value params = CommandOperator.getNextArg(funcDefScanner, newNamespace);
        Value codes = CommandOperator.getNextArg(funcDefScanner, newNamespace);
        boolean hasContextNamespace = functionDef.contextNamespace != null;
        // assert params.type == Type.LIST;
        // assert codes.type == Type.LIST;

        if (params != null && OperationPerformer.isempty(params).isFalse()) {  // fix bug
            // 传参
            String[] paramList = params.data.split(" ");
            for (String param : paramList) {
                Value v = CommandOperator.getNextArg(in, namespace);
                newNamespace.add(param, v);
            }
        }

        if (hasContextNamespace == true) {
            Main.contextNamespaceStack.push(functionDef.contextNamespace);
        }

        funcDefScanner = null;

        // 读指令
        if (codes != null && OperationPerformer.isempty(codes).isFalse()) {
            Scanner funcScanner = new Scanner(codes.data);
            CommandOperator.run(funcScanner, newNamespace);
        }
        Value retVal = newNamespace.get("__return_value");  // namespace怎么处理返回值的问题

        if (hasContextNamespace == true) {
            Main.contextNamespaceStack.pop();
        }

        // namespace.printAllVarialbesWithStack();
        return retVal;
    }
}
