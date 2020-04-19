package com.company;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//ОБЛАСТЬ ВИДИМОСТИ
@Retention(RetentionPolicy.RUNTIME) //ВО ВРЕМЯ ВЫПОЛНЕНИЯ ПРОГРАММЫ АННОТАЦИЯ ВИДНА
//ДЛЯ ЧЕГО НУЖНА АННОТАЦИЯ
@Target(ElementType.METHOD) //НУЖНА ДЛЯ МЕТОДОВ

public @interface Test {
    //У ТЕСТОВ БЕЗ УКАЗАННЫХ ПРИОРИТЕТОВ ОН БУДЕТ ОДИНАКОВЫМ
    int priority() default 1;
}
