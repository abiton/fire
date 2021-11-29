package io.abiton.fire;

public class Bar {
    private String a;
    private int count = 0;

    public Bar(String a, int count) {
        this.a = a;
        this.count = count;
    }

    public Bar(Bar that) {
        this.a = that.a;
        this.count = that.count;
    }

    public int inc(int i) {
        this.count += i;
        return count;
    }

    @Override
    public String toString() {
        return "io.abiton.fire.Bar{" +
                " a='" + a + '\'' +
                ", count=" + count +
                '}';
    }

}
