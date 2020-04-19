public class Methods {
    //ИЩЕМ 4 В МАССИВЕ
    private static boolean haveFour(int[] array) {
        boolean have4 = false;
        for (int i : array) {
            if (i == 4) {
                have4 = true;
                break;
            }
        }
        return have4;
    }

    //ВОЗВРАЩАЕМ НОВЫЙ МАССИВ
    public static int[] arrayReturner(int[] array) {
        int[] newArray = null;
        if (haveFour(array)) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == 4) {
                    newArray = new int[array.length - i - 1];
                    for (int j = i + 1, k = 0; j < array.length; j++, k++) {
                        if (array[j] == 4) {
                            continue;
                        }
                        newArray[k] = array[j];
                    }
                }
            }
            return newArray;
        } else throw new RuntimeException("В массиве нет числа 4");
    }

    //ПРОВЕРЯЕМ МАССИВ НА СОСТАВ 1 ИЛИ 4
    public static boolean haveOneAndFour(int[] array) {
        boolean one = false;
        boolean four = false;
        for (Integer a :
                array) {
            if (a != 1 && a != 4) return false;
            if (a == 1) one = true;
            if (a == 4) four = true;
        }
        return (one && four);
    }
}
