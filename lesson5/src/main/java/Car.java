import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Car implements Runnable {
    private static int CARS_COUNT;
    private static boolean winnerFound;
    private static String winner;

    public static String getWinner() {
        return winner;
    }

    //СЧЁТЧИКИ ПОТОКОВ
    public static CountDownLatch cdlReady;
    public static CountDownLatch cdlFinish;
    //БАРЬЕР
    public static CyclicBarrier cbStart;

    static {
        CARS_COUNT = 0;
        cdlReady = MainClass.cdlReady;
        cdlFinish = MainClass.cdlFinish;
        cbStart = MainClass.cbStart;
    }

    private Race race;
    private int speed;
    private String name;

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public Car(Race race, int speed) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }

    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int) (Math.random() * 800));
            //ОТНИМАЕМ ОТ СЧЁТЧИКА ПО ОКОНЧАНИЮ ПОДГОТОВКИ МАШИНЫ
            cdlReady.countDown();
            System.out.println(this.name + " готов");
            //ЖДЁМ ПОКА ВСЕ ВОЙДУТ В СОСТОЯНИЕ ГОТОВНОСТИ, ЧТОБЫ СБРОСИТЬ БАРЬЕР
            cbStart.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }
        //ОТНИМАЕМ ОТ СЧЁТЧИКА ПО ОКОНЧАНИЮ ЗАЕЗДА МАШИНЫ
        cdlFinish.countDown();
        //НАХОДИМ ПОБЕДИТЕЛЯ
        if (!winnerFound){
            winner = ">>> " + name + " победил в гонке!!!" + " <<<";
            winnerFound = true;
        }
    }
}