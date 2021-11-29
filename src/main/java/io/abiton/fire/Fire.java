package io.abiton.fire;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Fire {

    public static void fire(Class target) throws Exception {

        InputStreamReader input = new InputStreamReader(System.in);
        Object instance = target.getDeclaredConstructor().newInstance();
        while (true) {
            BufferedReader reader = new BufferedReader(input);
            String name = reader.readLine().trim();
            if (name.equals("/exit")) {
                break;
            }
            if (name.equals("/help")) {
                printHelp(target);
                continue;
            }
            if (name.equals("/refresh")) {
                instance = target.getDeclaredConstructor().newInstance();
                continue;
            }
            if (name.equals("")) continue;
            try {
                final int indexOf = name.indexOf(" ");
                if (indexOf < 0) {
                    System.out.println(invokeMethod(instance, name, null));
                } else {
                    String methodName = name.substring(0, indexOf).trim();
                    String paramStr = name.substring(indexOf).trim();
                    System.out.println(invokeMethod(instance, methodName, paramStr));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Moriturus te saluto.");
    }

    private static void printHelp(Class target) {
        for (Method method : target.getDeclaredMethods()) {
            System.out.println("Method name: " + method.getName());
            System.out.print("parameter type (");
            for (Parameter parameter : method.getParameters()) {
                System.out.print(parameter.getType().getCanonicalName());
                System.out.print(", ");
            }
            System.out.println(")");
            System.out.println();
        }
    }

    private static Object invokeMethod(Object obj, String methodName, String args) throws Exception {
        final List<Token> tokens = ParamParser.read(args);
        final ParamEval paramEval = new ParamEval();

        final List<Method> methods = Arrays.stream(obj.getClass().getMethods())
                .filter(method -> method.getName().equals(methodName))
                .collect(Collectors.toList());
        return paramEval.evalParam(obj, methods, tokens);
    }
}
