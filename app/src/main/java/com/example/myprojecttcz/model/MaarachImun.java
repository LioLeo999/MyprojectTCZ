package com.example.myprojecttcz.model;

import java.util.Arrays;

public class MaarachImun {
    protected Drill[] maarach;

    public MaarachImun(Drill[] maarach) {
        this.maarach = maarach;
    }

    public MaarachImun() {
    }

    public Drill[] getMaarach() {
        return maarach;
    }

    public void setMaarach(Drill[] maarach) {
        this.maarach = maarach;
    }

    @Override
    public String toString() {
        return "MaarachImun{" +
                "maarach=" + Arrays.toString(maarach) +
                '}';
    }
}
