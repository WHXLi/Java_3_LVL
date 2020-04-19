package com.company;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws Exception {
        start(MyTest.class);
    }

    private static void start(Class testClass) {
        //ПОЛУЧАЕМ МЕТОДЫ ВНУТРИ КЛАССА
        Method[] methods = testClass.getDeclaredMethods();

        Method before = null;
        Method after = null;
        ArrayList<Method> methodsList = new ArrayList<>();

        //РАСПРЕДЕЛЯЕМ ПОЛУЧЕННЫЙ МАССИВ МЕТОДОВ
        if (!repeatBeforeAndAfter(methods)) {
            for (Method method : methods) {
                if (method.isAnnotationPresent(BeforeSuite.class)) {
                    before = method;
                } else if (method.isAnnotationPresent(AfterSuite.class)) {
                    after = method;
                } else if (method.isAnnotationPresent(Test.class)) {
                    //ОБЫЧНЫЕ ТЕСТЫ ЗАНОСЯТСЯ В ЛИСТ РАНЬШЕ
                    methodsList.add(method);
                }
            }

            //СОРТИРУЕМ ТЕСТЫ ПО ПРИОРИТЕТУ
            //СОРТИРУЮ "int" ПО КЛЮЧУ В ВИДЕ ПОЛЯ "priority"
            Comparator<Method> methodComparator = Comparator.comparingInt(method -> method.getAnnotation(Test.class).priority());
            //ПРИМЕНЯЮ КОМПАРАТОР К СОРТИРОВКЕ ЛИСТА, ДЕЛАЮ ОБРАТНЫЙ ПОРЯДОК
            methodsList.sort(methodComparator.reversed());
        }

        try {
            //ЕДИНИЧНЫЕ МЕТОДЫ ЗАНОСЯТСЯ В ЛИСТ ПОЗЖЕ
            methodsList.add(0,before);
            methodsList.add(after);

            //ВЫЗЫВАЕМ МЕТОДЫ
            for (Method method : methodsList) {
                method.invoke(null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //МЕТОД ПРОВЕРЯЮЩИЙ ПОВТОРЫ "Before" И "After"
    private static boolean repeatBeforeAndAfter(Method[] methods) {
        int beforeCount = 0;
        int afterCount = 0;

        for (Method method : methods) {
            if (method.isAnnotationPresent(BeforeSuite.class)) beforeCount++;
            if (method.isAnnotationPresent(AfterSuite.class)) afterCount++;
        }
        if (beforeCount > 1 || afterCount > 1) {
            throw new RuntimeException
                    ("Методы с аннотациями @BeforeSuite и @AfterSuite должны присутствовать в единственном экземпляре");
        } else return false;
    }
}
