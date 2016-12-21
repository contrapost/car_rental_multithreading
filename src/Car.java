/**
 * Created by Alexander on 21.01.2016.
 *
 */
public class Car {

    private String regNum;

    public Car(String regNum) { this.regNum = regNum; }

    public String getRegNum() {
        return regNum;
    }

    @Override
    public String toString(){ return this.getRegNum(); }
}
