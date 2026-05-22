package com.example.pr29.domains.models;

public class Day {
    public String Name;
    public Integer Temp;
    public String Condition;
    public Day (String name, int temp, String condition) {

        this.Name = name;
        this.Temp = temp;
        this.Condition = condition;

    }

    public String getTemp() {

        return this.Temp + "*";

    }
}
