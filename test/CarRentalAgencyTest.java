import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Alexander on 21.01.2016.
 * Contains tests of general functions of the CarRentalAgency class and thread safety of the class.
 */
public class CarRentalAgencyTest {

    private CarRentalAgency carRental;

    @Before
    public void setUp() {
        ArrayList<Car> cars = new ArrayList<>();
        Car carA = mock(Car.class);
        Car carB = mock(Car.class);
        Car carC = mock(Car.class);
        cars.add(carA);
        cars.add(carB);
        cars.add(carC);
        carRental = new CarRentalAgency(cars);
    }


    @Test
    public void testCustomerRentsCar_CustomersNameIsRegistered() {
        Customer customer = mock(Customer.class);
        when(customer.getName()).thenReturn("Al");
        carRental.rent(customer);
        assertTrue(carRental.getRentedCarLog().containsValue("Al"));
    }

    @Test
    public void testCustomerReturnsCar_CustomersNameIsDeletedFromRegister() {
        Customer customer = mock(Customer.class);
        when(customer.getName()).thenReturn("Al");
        carRental.rent(customer);
        carRental.returnCar(customer);
        assertFalse(carRental.getRentedCarLog().containsValue("Al"));
    }

    @Test
    public void testMultipleThreadsRentCars_JustOneCarCanBeRentedByOneCustomer() throws InterruptedException {

        int numberOfRegistrationsOfCustomerA = 0;
        int numberOfRegistrationsOfCustomerB = 0;
        int numberOfRegistrationsOfCustomerC = 0;

        Customer customerA = new Customer("A", carRental, mock(CountDownLatch.class));
        Customer customerB = new Customer("B", carRental, mock(CountDownLatch.class));
        Customer customerC = new Customer("C", carRental, mock(CountDownLatch.class));

        Thread t1 = new Thread(() -> {
            carRental.rent(customerA);
        });
        Thread t2 = new Thread(() -> {
            carRental.rent(customerB);
        });
        Thread t3 = new Thread(() -> {
            carRental.rent(customerC);
        });

        Thread[] threads = {t1, t2, t3};

        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        Collection<String> names = carRental.getRentedCarLog().values();

        for (String s : names) {
            if (s.equals("A")) numberOfRegistrationsOfCustomerA++;
            if (s.equals("B")) numberOfRegistrationsOfCustomerB++;
            if (s.equals("C")) numberOfRegistrationsOfCustomerC++;
        }

        assertFalse(carRental.getRentedCarLog().containsValue("available"));
        assertTrue(numberOfRegistrationsOfCustomerA == 1);
        assertTrue(numberOfRegistrationsOfCustomerB == 1);
        assertTrue(numberOfRegistrationsOfCustomerC == 1);
    }

    @Test
    public void hugeNumberOfCustomersTryToRent() throws InterruptedException, NoSuchMethodException {
        int numberOfCustomers = 1000;
        ArrayList<Car> cars = new ArrayList<>();
        for (int i = 0; i < numberOfCustomers; i++) {
            Car car = new Car(i + "");
            cars.add(car);
        }
        CarRentalAgency carRental = new CarRentalAgency(cars);

        Thread[] threads = new Thread[numberOfCustomers];
        for (int i = 0; i < threads.length; i++) {
            String name = "Customer " + i;
            threads[i] = new Thread(() -> {
                carRental.rent(new Customer(name, carRental, mock(CountDownLatch.class)));
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        Collection<String> names = carRental.getRentedCarLog().values();
        HashSet<String> uniqueNames = new HashSet<>();
        //noinspection Convert2streamapi
        for (String s : names) {
            if (!s.equals("available")) uniqueNames.add(s);
        }

        assertFalse(carRental.getRentedCarLog().containsValue("available"));
        assertTrue(uniqueNames.size() == numberOfCustomers);
    }

    @Test
    public void testMultipleCarsReturned_AllCarsAreAvailable() throws InterruptedException {

        Customer customerA = new Customer("A", carRental, mock(CountDownLatch.class));
        Customer customerB = new Customer("B", carRental, mock(CountDownLatch.class));
        Customer customerC = new Customer("C", carRental, mock(CountDownLatch.class));

        carRental.rent(customerA);
        carRental.rent(customerB);
        carRental.rent(customerC);

        Thread t1 = new Thread(() -> {
            carRental.returnCar(customerA);
        });
        Thread t2 = new Thread(() -> {
            carRental.returnCar(customerB);
        });
        Thread t3 = new Thread(() -> {
            carRental.returnCar(customerC);
        });

        Thread[] threads = {t1, t2, t3};

        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        Collection<String> names = carRental.getRentedCarLog().values();
        int numberOfAvailables = 0;
        for(String s : names) {
            if(s.equals("available")) numberOfAvailables++;
        }
        assertTrue(numberOfAvailables == 3);
    }
}
