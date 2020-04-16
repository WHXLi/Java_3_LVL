public class ABC {
    //СОЗДАЕМ МОНИТОР
    static Object mon = new Object();
    //ПЕРЕМЕННАЯ С СИМВОЛОМ, КОТОРЫЙ ВИДИТ КАЖДЫЙ ПОТОК
    static volatile char symbol;
    //КОЛИЧЕСТВО ЦИКЛОВ РАБОТЫ
    static int howLong = 5;

    public static void main(String[] args) {
        new Thread(()->{
            printABC('A','B');
        }).start();
        new Thread(()->{
            printABC('B','C');
        }).start();
        new Thread(()->{
            printABC('C','A');
        }).start();
    }

    private static void printABC(char mySymbol, char newSymbol){
        try {
            //ВОЗМОЖНОСТЬ ВВЕСТИ НЕОБХОДИМЫЙ ДЛЯ ПЕЧАТИ СИМВОЛ
            symbol = mySymbol;
            for (int i = 0; i < howLong; i++) {
                synchronized (mon) {
                    //ПОКА ОБЩИЙ ДЛЯ ВСЕХ ПОТОКОВ СИМВОЛ НЕ СООТВЕТСВУЕТ МОЕМУ ВВЕДЕННОМУ СИМВОЛУ МОНИТОР ОЖИДАЕТ
                    while (symbol != mySymbol) {
                        mon.wait();
                    }
                    System.out.print(symbol);
                    //МЕНЯЕМ ОБЩИЙ ДЛЯ ВСЕХ ПОТОКОВ СИМВОЛ НА ВВЕДЕННЫЙ, ЧТОБЫ ДРУГИЕ ПОТОКИ ВКЛЮЧИЛИСЬ В РАБОТУ
                    symbol = newSymbol;
                    //БУДИМ ПОТОКИ
                    mon.notifyAll();
                }
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
