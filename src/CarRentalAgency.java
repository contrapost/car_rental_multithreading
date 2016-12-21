import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Alexander on 21.01.2016.
 * Contains info about rented cars and manages the safe process of renting / returning of cars by several threads.
 */
public class CarRentalAgency {

    private static Lock lock = new ReentrantLock();
    private static Condition waitForCar = lock.newCondition();
    private HashMap<Car, String> rentedCarLog;

    public CarRentalAgency(ArrayList<Car> cars) {
        rentedCarLog = new HashMap<>(cars.size());
        fillRentedCarLog(cars);
        printStatus();
    }

    public void rent(Customer customer) {
        lock.lock();
        try {
            //noinspection StatementWithEmptyBody
            while (hasNoAvailable()) {
                System.out.println(customer + " is waiting for a car.");
                waitForCar.await();
            }

            for (Car car : rentedCarLog.keySet()) {
                if ((rentedCarLog.get(car)).equals("available")) {
                    rentedCarLog.replace(car, customer.getName());
                    System.out.println(customer + " has rented car with registration number " + car + ".");
                    printStatus();
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void returnCar(Customer customer) {
        lock.lock();
        try {
            //noinspection Convert2streamapi
            for (Car car : rentedCarLog.keySet())
                if (customer.getName().equals(rentedCarLog.get(car))) {
                    rentedCarLog.replace(car, "available");
                    waitForCar.signalAll();
                    System.out.println(customer + " has returned car with registration number " + car + ".");
                    printStatus();
                }
        } finally {
            lock.unlock();
        }
    }

    //Returns copy of the log of the rented cars
    public HashMap<Car, String> getRentedCarLog() {
        HashMap<Car, String> temp = new HashMap<>(rentedCarLog.size());
        temp.putAll(rentedCarLog);
        return temp;
    }

    private void printStatus() {
        String line = "**************************************************";
        System.out.println("\n" + line + "RENTAL CAR STATUS" + line);
        for (Car car : rentedCarLog.keySet()) System.out.print(car + " - " + rentedCarLog.get(car) + "\t\t");
        System.out.println("\n" + line + "*******END*******" + line + "\n");
    }

    private void fillRentedCarLog(ArrayList<Car> cars) {
        for (Car car : cars) { rentedCarLog.put(car, "available"); }
    }

    private boolean hasNoAvailable() {  return !rentedCarLog.containsValue("available");  }

}
