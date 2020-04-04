package com.company;

import java.util.ArrayList;
import java.util.Arrays;

public class Box<T extends Fruit> {
    private ArrayList<T> container;

    //КОНСТРУКТОР ПУСТОЙ КОРОБКИ
    public Box() {
        this.container = new ArrayList<>();
    }

    //КОНСТРУКТОР КОРОБКИ С ВОЗМОЖНОСТЬЮ ДОБАВЛЕНИЯ
    public Box(T... fruits) {
        this.container = new ArrayList<>(Arrays.asList(fruits));
    }

    public void addFruits(T fruit, int value) {
        for (int i = 0; i < value; i++) {
            container.add(fruit);
        }
    }

    public void showFruits() {
        System.out.println("------------------------------------------");
        for (int i = 0; i < container.size(); i++) {
            System.out.println(container.get(i).getName());
        }
        System.out.println("------------------------------------------");
    }

    public float getWeight(){
        float weight = 0.0f;
        for (T fruit : container) {
            weight += fruit.getWeight();
        }
        return weight;
    }

    public boolean checkEquals(Box <?> another){
        return Math.abs(this.getWeight() - another.getWeight()) < 0.001;
    }

    public void transfer(Box<? super T> another){
        if (another == this){
            return;
        }
        another.container.addAll(this.container);
        this.container.clear();
    }
}
