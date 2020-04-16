import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class MainClass {
    //КОЛИЧЕСТВО МАШИН
    public static final int CARS_COUNT = 4;

    //СЧЁТЧИКИ ПОТОКОВ
    public static CountDownLatch cdlReady = new CountDownLatch(CARS_COUNT);
    public static CountDownLatch cdlFinish = new CountDownLatch(CARS_COUNT);
    //БАРЬЕР
    public static CyclicBarrier cbStart = new CyclicBarrier(CARS_COUNT);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10));
        }
        for (Car car : cars) {
            new Thread(car).start();
        }
        //ЖДЁМ ПОКА ВСЕ ПОДГОТОВЯТСЯ
        Car.cdlReady.await();
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
        //ЖДЁМ ПОКА ВСЕ ЗАКОНЧАТ ГОНКУ
        Car.cdlFinish.await();
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
        System.out.println(Car.getWinner());
    }
}
