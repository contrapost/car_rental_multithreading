import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Alexander on 22.01.2016.
 * In according to the task customer waits 1-10 sec before trying to rent a car and returns the car
 * after 1-3 sec.
 */
public class Customer implements Runnable {

    private final CountDownLatch startSignal;
    private String name;
    private CarRentalAgency rentalAgency;

    public Customer(String name, CarRentalAgency rentalAgency, CountDownLatch startSignal) {
        this.name = name;
        this.rentalAgency = rentalAgency;
        this.startSignal = startSignal;
    }

    public String getName() { return name; }

    @Override
    public String toString() { return getName(); }

    @Override
    public void run() {
        Random rand = new Random();
        try {
            startSignal.await();
            //noinspection InfiniteLoopStatement
            while (true) {
                Thread.sleep((rand.nextInt(10) + 1) * 1000);
                rent();
                Thread.sleep((rand.nextInt(3) + 1) * 1000);
                returnCar();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void returnCar() { rentalAgency.returnCar(this); }

    private void rent() { rentalAgency.rent(this); }
}
