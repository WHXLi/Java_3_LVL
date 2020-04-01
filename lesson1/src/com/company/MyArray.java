package com.company;

import java.util.ArrayList;
import java.util.Collections;

public class MyArray<T> {
    private T[] objects;
    private ArrayList <T> arrayList;

    @SafeVarargs
    public MyArray(T... objects) {
        System.out.println("---Массив---");
        this.objects = objects;
    }

    public void print(){
        if (arrayList == null){
            for (int i = 0; i < objects.length; i++) {
                System.out.println(i + ") " + objects[i] + " (Тип Т: " + objects.getClass().getSimpleName() + ")");
            }
        }else {
            for (int i = 0; i < arrayList.size(); i++) {
                System.out.println(i + ") " + arrayList.get(i) + " (Тип Т: " + arrayList.getClass().getSimpleName() + ")");
            }
        }
    }

    public void change(int toChange, int change){
        System.out.println("---Меняем местами---");
        T changingSlot = objects[toChange];
        objects[toChange] = objects [change];
        objects[change] = changingSlot;
    }

    public void toArrayList(){
        System.out.println("---Преобразуем в ArrayList---");
        arrayList = new ArrayList<>();
        Collections.addAll(arrayList, objects);
    }
}
