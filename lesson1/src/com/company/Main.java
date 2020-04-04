package com.company;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        String[] arr = {"1", "2", "3"};
        swap(arr,0,2);
        ArrayList<String> arrayList = arrayToList(arr);
        System.out.println("Массив: " + arrayList + "\n");

        System.out.println("Коробка с яблоками.");
        Box <Apple> appleBox = new Box<>();
        appleBox.addFruits(new Apple(), 5);
        System.out.println("Масса коробки: " + appleBox.getWeight());
        appleBox.showFruits();

        System.out.println("Коробка с апельсинами.");
        Box <Orange> orangeBox = new Box<>();
        orangeBox.addFruits(new Orange(),5);
        System.out.println("Масса коробки: " + orangeBox.getWeight());
        orangeBox.showFruits();

        System.out.println("Равенство коробок: " + appleBox.checkEquals(orangeBox));

        Box <Orange> orangeBox2 = new Box<>();
        orangeBox.transfer(orangeBox2);
        System.out.println("Старая коробка с апельсинами.");
        orangeBox.showFruits();
        System.out.println("Новая коробка с апельсинами.");
        orangeBox2.showFruits();
    }

    //КОНВЕРТИРУЕМ МАССИВ В ARRAY LIST
    public static <T> ArrayList<T> arrayToList (T[] arr) {
        return new ArrayList<>(Arrays.asList(arr));
    }

    //МЕНЯЕМ МЕСТАМИ ЭЛЕМЕНТЫ МАССИВА
    public static <T> void swap(T[] arr, int toChange, int change){
        T object = arr[toChange];
        arr[toChange] = arr[change];
        arr[change] = object;
    }
}
