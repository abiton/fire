package io.abiton.fire;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ParamEval {

    public Object evalParam(Object target, List<Method> methods, List<Token> tokens) throws Exception {

        final List<Method> possibleMethods = methods.stream()
                .filter(method -> method.getParameters().length == tokens.size())
                .collect(Collectors.toList());
        final Optional<Method> method = findExactMethod(possibleMethods, tokens);
        if (method.isPresent()) {
            List params = getParam(method.get().getParameters(), tokens);
            return method.get().invoke(target, params.toArray());
        }
        throw new IllegalArgumentException();
    }

    private List getParam(Parameter[] parameters, List<Token> tokens) throws Exception {

        List params = new ArrayList(tokens.size());
        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            final Token token = tokens.get(i);
            final Class<?> type = parameter.getType();
            if (type.isPrimitive()) {
                params.add(primitiveParam((Primitive) token, type));
            } else if (type == String.class) {
                params.add(((Primitive) token).value());
            } else {
                params.add(readObject((Obj) token));
            }
        }
        return params;
    }

    public Object evalConstructor(Constructor[] methods, List<Token> tokens) throws Exception {

        final List<Constructor> possibleMethods = Arrays.stream(methods)
                .filter(method -> method.getParameters().length == tokens.size())
                .collect(Collectors.toList());
        final Optional<Constructor> method = findExactConstructor(possibleMethods, tokens);
        if (method.isPresent()) {
            List params = getParam(method.get().getParameters(), tokens);
            if (params.isEmpty()) {
                return method.get().newInstance();
            } else {
                return method.get().newInstance(params.toArray());
            }
        }
        throw new IllegalArgumentException();
    }

    private Optional<Constructor> findExactConstructor(List<Constructor> possibleMethods, List<Token> tokens) {
        return possibleMethods.stream().filter(method -> {
            final Parameter[] parameters = method.getParameters();
            return checkMatch(tokens, parameters);
        }).findFirst();

    }

    private boolean checkMatch(List<Token> tokens, Parameter[] parameters) {
        try {
            for (int i = 0; i < parameters.length; i++) {
                final Token token = tokens.get(i);
                final Parameter parameter = parameters[i];
                if (!checkParameter(parameter, token)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Object readObject(Obj token) throws Exception {
        final Class<?> aClass = Class.forName(token.getName());
        final Constructor<?>[] constructors = aClass.getConstructors();
        return evalConstructor(constructors, token.value());
    }

    private Object primitiveParam(Primitive token, Class<?> type) {
        String value = token.value();
        switch (type.getName()) {
            case "byte":
                return Byte.parseByte(value);
            case "short":
                return Short.parseShort(value);
            case "int":
                return Integer.parseInt(value);
            case "char":
                return value.charAt(0);
            case "long":
                return Long.parseLong(value);
            case "float":
                return Float.parseFloat(value);
            case "double":
                return Double.parseDouble(value);
            case "boolean":
                return Boolean.parseBoolean(value);
        }
        return value;
    }

    private Optional<Method> findExactMethod(List<Method> possibleMethods, List<Token> tokens) {
        return possibleMethods.stream().filter(method -> {
            final Parameter[] parameters = method.getParameters();
            return checkMatch(tokens, parameters);
        }).findFirst();

    }

    private boolean checkParameter(Parameter parameter, Token token) throws Exception {
        final Class<?> type = parameter.getType();
        if (token instanceof Primitive && (type.isPrimitive() || type == String.class)) {
            return true;
        } else if (token instanceof Obj && type.isAssignableFrom(Class.forName(((Obj) token).getName()))) {
            return true;
        }
        return false;
    }

}
