import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Alexander on 21.01.2016.
 *
 */
public class CarTest {

    @Test
    public void testGetRegNum(){
        String regNum = "AB75032";
        Car car = new Car(regNum);
        assertEquals(car.getRegNum(), regNum);
    }
}
