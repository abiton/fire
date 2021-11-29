package io.abiton.fire;

import java.util.List;

public class Foo {
    int counter;
    Bar bar;
    List<Bar> list;

    public void increment(int a) {
        counter += a;
    }

    public int counter() {
        return counter;
    }

    public void setBar(Bar a) {
        this.bar = a;
    }

    public void setList(List<Bar> list) {
        this.list = list;
    }

    public void listAdd(Bar a) {
        this.list.add(a);
    }

    public List getList() {
        return list;
    }

    public Bar getBar() {
        return bar;
    }

    public int incBar(int i) {
        return this.bar.inc(i);
    }

    public String toString(String a) {
        return counter + a;
    }
}
