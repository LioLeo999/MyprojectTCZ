package com.example.myprojecttcz.model;

import java.util.Arrays;

public class Drill {

    protected String id;
    protected String name;
    protected String shot[];
    protected String time;
    protected String level;
    protected String minmaxplayers;

    public Drill(String id, String name, String[] shot, String time, String level, String minmaxplayers) {
        this.id = id;
        this.name = name;
        this.shot = shot;
        this.time = time;
        this.level = level;
        this.minmaxplayers = minmaxplayers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getShot() {
        return shot;
    }

    public void setShot(String shot) {
        this.shot = new String[]{shot};
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMinmaxplayers() {
        return minmaxplayers;
    }

    public void setMinmaxplayers(String minmaxplayers) {
        this.minmaxplayers = minmaxplayers;
    }

    @Override
    public String toString() {
        return "Drill{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", shot=" + Arrays.toString(shot) +
                ", time='" + time + '\'' +
                ", level='" + level + '\'' +
                ", minmaxplayers='" + minmaxplayers + '\'' +
                '}';
    }
}
