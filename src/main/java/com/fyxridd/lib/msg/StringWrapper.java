package com.fyxridd.lib.msg;

public class StringWrapper {
    private String s;

    public StringWrapper(String s) {
        this.s = s;
    }

    public String convert() {
        return s.replace("\u00A7", "\u00A6");
    }

    @Override
    public int hashCode() {
        return s.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return ((StringWrapper)obj).s.equals(s);
    }

    @Override
    public String toString() {
        return s;
    }
}
