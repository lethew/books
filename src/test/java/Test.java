import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<? extends Fruit> list = new ArrayList<Apple>();
//        list.a
//        list.add(new Apple());
    }
}

class Fruit{

}

class Apple extends Fruit{

}
