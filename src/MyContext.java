import javax.naming.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class MyContext implements Contex {
    private int interruptedTasks = 0;
    private int completedTasks = 0;
    private int failedTasks = 0;
    private boolean isfinished = false;
    public volatile boolean completed = false;
    Runnable[] tasks;
    private final ThreadPoolExecutor executor;
    List<Future> objects = new ArrayList<>();
    public Runnable callback;
    public MyContext(Runnable callback, Runnable... tasks){
        this.executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        this.tasks = tasks;
        for (Runnable task : tasks) {
            try {
                objects.add(executor.submit(task));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        this.callback = callback;
        new Thread(()->execute()).start();
    }

    private void execute() {
        executor.shutdown();
        try {
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RejectedExecutionException();
        }
        if(!completed){
            synchronized ((executor)) {
                this.isfinished = true;
                objects.forEach(c -> {
                    try {
                        c.get(1, TimeUnit.MILLISECONDS);
                        this.completedTasks++;
                    } catch (InterruptedException e) {
                        this.interruptedTasks++;
                        interrupt();
                    } catch (ExecutionException e) {
                        this.failedTasks++;
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                });
                this.completed = true;
                executor.shutdownNow();
                new Thread(callback).start();
            }
        }
        this.completed = true;

    }
    @Override
    public synchronized int getCompletedTaskCount(){
        return this.completedTasks;
    }
    public synchronized int getFailedTaskCount()
    {
        return this.failedTasks;
    }
    public synchronized int getInterruptedTaskCount(){
        return this.interruptedTasks;
    }
    public synchronized void interrupt(){
        executor.shutdown();
    }
    public synchronized boolean isFinished(){
        return this.isfinished;
    }
}
