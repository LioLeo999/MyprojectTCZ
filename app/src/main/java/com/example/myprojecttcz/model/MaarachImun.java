package com.example.myprojecttcz.model;

import java.util.ArrayList;
import java.util.Arrays;

public class MaarachImun {
    protected String id;
    protected String name;
    protected String description;
    protected ArrayList<String> drillsid;

    public MaarachImun() {
    }

    public MaarachImun(String id, String name, ArrayList<String> drills) {
        this.id = id;
        this.name = name;
        this.drillsid = drills;
    }

    public MaarachImun(String id, String name, String description, ArrayList<String> drillsid) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.drillsid = drillsid;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getDrillsid() {
        return drillsid;
    }

    public void setDrillsid(ArrayList<String> drillsid) {
        this.drillsid = drillsid;
    }


    @Override
    public String toString() {
        return "MaarachImun{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", drills=" + drillsid +
                '}';
    }
}
