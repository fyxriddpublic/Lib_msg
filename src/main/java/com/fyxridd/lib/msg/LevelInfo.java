package com.fyxridd.lib.msg;

public class LevelInfo {
    private String type;
    private String name;
    private boolean prefix;

    public LevelInfo(String type, String name, boolean prefix) {
        this.type = type;
        this.name = name;
        this.prefix = prefix;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isPrefix() {
        return prefix;
    }
}
