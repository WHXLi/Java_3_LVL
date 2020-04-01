package com.company;

public class Main {

    public static void main(String[] args) {
        MyArray<?> array = new MyArray<>("Первый", 2);
        array.print();

        array.change(0,1);
        array.print();

        array.toArrayList();
        array.print();

        Box <Apple> boxApples = new Box<>();
        boxApples.addFruits(new Apple(), 5);
        boxApples.showFruits(boxApples);
    }
}
