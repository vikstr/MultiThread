import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class MyExecutionManager implements ExecutionManager {
    @Override
    public Contex execute(Runnable callback, Runnable... tasks){
        return new MyContext(callback, tasks);
    }
}
