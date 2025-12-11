package com.example.myprojecttcz.model;

import java.util.ArrayList;

public class Maarchim {
    protected ArrayList<MaarachImun> maarachImuns;

    public Maarchim(ArrayList<MaarachImun> maarachImuns) {
        this.maarachImuns = maarachImuns;
    }

    public Maarchim() {
    }

    public ArrayList<MaarachImun> getMaarachImuns() {
        return maarachImuns;
    }

    public void setMaarachImuns(ArrayList<MaarachImun> maarachImuns) {
        this.maarachImuns = maarachImuns;
    }

    @Override
    public String toString() {
        return "Maarchim{" +
                "maarachImuns=" + maarachImuns +
                '}';
    }
}
