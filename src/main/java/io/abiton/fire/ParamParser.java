package io.abiton.fire;

import java.util.*;

public class ParamParser {

    public static List<Token> read(String input) {
        if (input == null || input.length() == 0) return Collections.emptyList();
        return read(new StringBuilder(input));
    }

    private static List<Token> read(StringBuilder input) {

        List<Token> result = new LinkedList<>();
        while (input.length() > 0) {
            final char c = input.charAt(0);
            if (c == ' ') {
                input.deleteCharAt(0);
            }
            result.addAll(readParams(input));
        }

        return result;
    }

    private static List<Token> readArray(StringBuilder input) {
        while (input.length() > 0) {
            final char c = input.charAt(0);
            input.deleteCharAt(0);
            if (c == ' ') continue;
            if (c == '[') {
                Array obj = new Array();
                obj.value().addAll(readParams(input));
                return Arrays.asList(obj);
            }
            if (c == ']') {
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }

    private static List<Token> readObject(StringBuilder input) {

        while (input.length() > 0) {
            final char c = input.charAt(0);
            input.deleteCharAt(0);
            if (c == ' ') continue;
            if (c == '(') {
                final int index = input.indexOf(":");
                Obj obj = new Obj(input.substring(0, index));
                input.delete(0, index + 1);
                obj.value().addAll(readParams(input));
                return Arrays.asList(obj);
            }
            if (c == ')') {
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }

    private static List<Token> readParams(StringBuilder input) {

        List<Token> params = new LinkedList<>();
        StringBuilder alreadyRead = new StringBuilder();
        while (input.length() > 0) {
            final char c = input.charAt(0);
            if (c == ',') {
                if (alreadyRead.length() > 0) {
                    params.add(new Primitive(alreadyRead.toString()));
                    alreadyRead.setLength(0);
                }
                input.deleteCharAt(0);
            } else if (c == ' ') {
                input.deleteCharAt(0);
            } else if (isPrimitive(c)) {
                alreadyRead.append(c);
                input.deleteCharAt(0);
            } else {
                if (alreadyRead.length() > 0) {
                    params.add(new Primitive(alreadyRead.toString()));
                    alreadyRead.setLength(0);
                }
                if (c == ')' || c == ']'){
                    input.deleteCharAt(0);
                    return params;
                }
                if (c == '(') {
                    params.addAll(readObject(input));
                }
                if (c == '[') {
                    params.addAll(readArray(input));
                }
            }
        }
        if (alreadyRead.length() > 0) {
            params.add(new Primitive(alreadyRead.toString()));
            alreadyRead.setLength(0);
        }
        return params;
    }

    private static boolean isArray(char c) {
        return c == '[' || c == ']';
    }

    private static boolean isObject(char c) {
        return c == '(' || c == ')';
    }

    private static boolean isPrimitive(char c) {
        return !isArray(c) && !isObject(c);
    }
}

interface Token {

}

class Primitive implements Token {
    private String value;

    public Primitive(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "Primitive(" +
                "'" + value + '\'' +
                ')';
    }
}

class Obj implements Token {
    private String name;
    private List<Token> value = new LinkedList<>();

    public Obj(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Token> value() {
        return value;
    }

    @Override
    public String toString() {
        return "Obj{" + name + ":" +
                value +
                '}';
    }
}

class Array implements Token {
    private List<Token> value = new LinkedList<>();

    public List<Token> value() {
        return value;
    }

    @Override
    public String toString() {
        return "Array[" +
                "" + value +
                ']';
    }
}
