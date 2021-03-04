package io.abiton.fire;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public class Fire {

    public static void fire(Class target) throws Exception {

        InputStreamReader input = new InputStreamReader(System.in);
        Object instance = target.newInstance();
        while (true) {
            BufferedReader reader = new BufferedReader(input);
            String name = reader.readLine();
            String[] split = name.split(" ");
            if (split.length >= 1) {
                if (split[0].equals("/exit")) {
                    break;
                }
                if (split[0].equals("/help")) {
                    printHelp(target);
                    continue;
                }
                //TODO support tab for auto completion
                System.out.println(invokeMethod(instance, split[0], Arrays.copyOfRange(split, 1, split.length)));
            }
        }
        System.out.println("Moriturus te saluto.");
    }

    private static void printHelp(Class target) {
        for (Method method : target.getMethods()) {
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

    private static Object invokeMethod(Object obj, String methodName, String[] args) {
        Method[] methods = obj.getClass().getMethods();
        for (Method method : methods) {
            //FIXME should not use parameters().length
            if (method.getName().equals(methodName) && method.getParameters().length == args.length) {
                try {
                    Object[] typedArgs = typeArgs(method.getParameters(), args);
                    return method.invoke(obj, typedArgs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static Object[] typeArgs(Parameter[] parameters, String[] args) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Object[] typeArgs = new Object[args.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> type = parameters[i].getType();
            if (type.isPrimitive()) {
                switch (type.getName()) {
                    case "byte":
                        typeArgs[i] = Byte.parseByte(args[i]);
                        break;
                    case "short":
                        typeArgs[i] = Short.parseShort(args[i]);
                        break;
                    case "int":
                        typeArgs[i] = Integer.parseInt(args[i]);
                        break;
                    case "char":
                        typeArgs[i] = args[i].charAt(0);
                        break;
                    case "long":
                        typeArgs[i] = Long.parseLong(args[i]);
                        break;
                    case "float":
                        typeArgs[i] = Float.parseFloat(args[i]);
                        break;
                    case "double":
                        typeArgs[i] = Double.parseDouble(args[i]);
                        break;
                    case "boolean":
                        typeArgs[i] = Boolean.parseBoolean(args[i]);
                        break;
                }
            } else {
                typeArgs[i] = nonPrimitiveTypeArgs(type, args[i]);
            }
        }
        return typeArgs;
    }
    //TODO support recursive non-primitive-type args
    private static Object nonPrimitiveTypeArgs(Class<?> type, String arg) throws IllegalAccessException, InvocationTargetException, InstantiationException {

        if (type.getClass().equals(String.class)) {
            return arg;
        } else {
            String[] split = arg.split(",");
            Constructor<?>[] constructors = type.getConstructors();
            for (Constructor<?> constructor : constructors) {
                //FIXME should not use parameter length
                if (constructor.getParameterCount() == split.length) {
                    return constructor.newInstance(typeArgs(constructor.getParameters(), split));
                }
            }
            return null;
        }
    }
}
