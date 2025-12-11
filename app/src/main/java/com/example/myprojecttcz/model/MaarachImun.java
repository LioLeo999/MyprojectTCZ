package com.example.myprojecttcz.model;

import java.util.ArrayList;
import java.util.Arrays;

public class MaarachImun {
    String name;
    protected ArrayList<Drill2v> drills;

    public MaarachImun() {
    }

    public MaarachImun(String name, ArrayList<Drill2v> maarach) {
        this.name = name;
        this.drills = maarach;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Drill2v> getMaarach() {
        return drills;
    }

    public void setMaarach(ArrayList<Drill2v> maarach) {
        this.drills = maarach;
    }

    @Override
    public String toString() {
        return "MaarachImun{" +
                "name='" + name + '\'' +
                ", maarach=" + drills +
                '}';
    }
}
