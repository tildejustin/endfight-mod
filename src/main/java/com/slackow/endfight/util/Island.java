package com.slackow.endfight.util;

public class Island implements Renameable {
    private String name;
    private long seed;

    public Island(long seed) {
        this.seed = seed;
    }

    public static Island valueOf(String s) {
        int i = s.indexOf(':');
        long seed = Long.parseLong(s.substring(0, i));
        Island island = new Island(seed);
        island.setName(s.substring(i + 1));
        return island;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    @Override
    public String toString() {
        return getSeed() + ":" + getName();
    }
}
