package com.company;

public class MyTest {

    @BeforeSuite
    public static void before(){
        System.out.println("Подготовка к тесту");
    }

    @Test(priority = 10)
    public static void myTest1(){
        System.out.println("Мой тест: высокий приоритет");
    }
    @Test(priority = 5)
    public static void myTest2(){
        System.out.println("Мой тест: средний приоритет");
    }
    @Test(priority = 5)
    public static void myTest3(){
        System.out.println("Мой тест: средний приоритет");
    }
    @Test(priority = 2)
    public static void myTest4(){
        System.out.println("Мой тест: низкий приоритет");
    }
    @Test()
    public static void myTest5(){
        System.out.println("Мой тест: приоритет не указан");
    }
    @Test()
    public static void myTest6(){
        System.out.println("Мой тест: приоритет не указан");
    }
    @Test()
    public static void myTest7(){
        System.out.println("Мой тест: приоритет не указан");
    }

    @AfterSuite
    public static void after(){
        System.out.println("Конец теста");
    }

//    @AfterSuite
//    public static void after1(){
//        System.out.println("Конец теста");
//    }

}
