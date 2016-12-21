import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Alexander on 23.01.2016.
 * Application that contains the info about cars, instantiate CarRentalAgency object, interacts with user and
 * starts threads with Customer runnable objects instantiated by user's input.
 */
public class CarRentalApplication {

    public static void main(String[] args) {

        //ArrayList with Car objects that Car rental Agency disposes
        //Extra cars can be added/deleted to the array list here.
        ArrayList<Car> cars = new ArrayList<>();
        //region Populate cars with Car-objects.
        cars.add(new Car("AB12345"));
        cars.add(new Car("CD23456"));
        cars.add(new Car("EF34567"));
        cars.add(new Car("GH45678"));
        cars.add(new Car("IJ56789"));
        //endregion

        CarRentalAgency carRental = new CarRentalAgency(cars);
        CountDownLatch startSignal = new CountDownLatch(5);

        // Asks user about number of customers (default value is 10).
        String textNumber = JOptionPane.showInputDialog(null, "Insert number of customers", 10);
        if (textNumber == null) { System.exit(0); }
        int numberOfCustomers = Integer.parseInt(textNumber);

        // Initialize customers as threads and starts executing threads when the number of threads are 5.
        for (int i = 1; i < numberOfCustomers + 1; i++) {
            String name = JOptionPane.showInputDialog(null, "Type the name of customer # " + i + ":");
            if (name == null) { System.exit(0); }
            new Thread(new Customer(name, carRental, startSignal)).start();
            startSignal.countDown();
        }

        //Asks user to close the app
        JOptionPane.showMessageDialog(null, "Time to close the agency?", "Car Rental App.", JOptionPane.QUESTION_MESSAGE);
        System.exit(0);
    }
}
