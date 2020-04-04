package com.company;

public abstract class Fruit {
    protected String name;
    protected float weight;

    public String getName() {
        return name;
    }
    public float getWeight() { return weight; }

    public Fruit (String name, Float weight){
        this.name = name;
        this.weight = weight;
    }
}
