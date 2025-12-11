package com.example.myprojecttcz.model;

import java.util.Arrays;

public class MaarachImun {
    protected Drill2v[] maarach;

    public MaarachImun(Drill2v[] maarach) {
        this.maarach = maarach;
    }

    public MaarachImun() {
    }

    public Drill2v[] getMaarach() {
        return maarach;
    }

    public void setMaarach(Drill2v[] maarach) {
        this.maarach = maarach;
    }

    @Override
    public String toString() {
        return "MaarachImun{" +
                "maarach=" + Arrays.toString(maarach) +
                '}';
    }
}
