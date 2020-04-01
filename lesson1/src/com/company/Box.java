package com.company;

import java.util.ArrayList;

public class Box<T> {
    private ArrayList<Fruit> fruits;

    public Box() {
        fruits = new ArrayList<>();
    }

    public void addFruits(Fruit fruit, int value){
        for (int i = 0; i < value; i++) {
            fruits.add(fruit);
        }
    }

    public void showFruits(Box box){
        for (int i = 0; i < fruits.size(); i++) {
            System.out.println(box.fruits.get(i));
        }
    }

}
