import org.junit.jupiter.api.Test;

import javax.security.auth.callback.Callback;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;

class MyExecutionManagerTest {
    private class MyRunnable implements Runnable {

        private String s;

        MyRunnable(String s) {
            this.s = s;
        }

        @Override
        public void run() {
            System.out.println(s);
            try {
                this.wait(1000);
            } catch (Exception e) {
            }
        }

        @Test
        public void test1() {
            ExecutionManager manager = new MyExecutionManager();
            Collection<Runnable> tasks = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                tasks.add(new MyRunnable(Math.random() + ""));
            }
            Runnable[] a = new Runnable[tasks.size()];
            manager.execute(() -> System.out.println("Done"), a);
        }
    }
}