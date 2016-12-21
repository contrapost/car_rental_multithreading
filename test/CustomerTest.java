import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by Alexander on 21.01.2016.
 * Just a proforma test, was used in TDD at the first stage of development.
 */
public class CustomerTest {

    private String name = "Al";
    private Customer customer;

    @Before
    public void setUp(){
        customer = new Customer(name, mock(CarRentalAgency.class),
                mock(CountDownLatch.class));
    }

    @Test
    public void testGetName(){
        assertEquals(customer.getName(), name);
    }

}
