import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

class ScalableThreadPoolTest {
    @Test
    public void test1(){
    List<Runnable> workers = new ArrayList<>();
        for(int i=0; i < 100; i++) {
            int finalI = i;
            Runnable runnable = () -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Runnable running " + finalI + " " + Thread.currentThread());};
            workers.add(runnable);
        }
        ScalableThreadPool threadPool = new ScalableThreadPool(10, 55);
        threadPool.start();
        System.out.println("It's done");
        workers.forEach(c -> {
            ThreadLocalRandom.current().nextInt(20, 1000 + 1);
            threadPool.execute(c);
        });
    }
}