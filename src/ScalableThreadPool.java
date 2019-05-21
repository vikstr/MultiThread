import java.util.concurrent.ConcurrentLinkedQueue;

public class ScalableThreadPool implements ThreadPool {
    private final int min;
    private final int max;
    private final Object lock = new Object();
    private final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();
    private volatile int currentThread;
    private volatile int currentWorkedThreads;
    public ScalableThreadPool(int min,int max){
        this.max=max;
        this.min=min;
    }
    public class ScalableThread extends Thread {
        @Override
        public void run() {
            while (true) {
                synchronized (lock) {
                    if (tasks.isEmpty() && currentThread > min) {
                        currentThread--;
                        System.out.println("Performed:" + currentThread);
                        break;
                    }
                }
                Runnable task;
                synchronized (lock){
                    while (tasks.isEmpty()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    currentWorkedThreads++;
                    task = tasks.poll();
                    }
                task.run();
                currentWorkedThreads--;
            }
        }
    }
    @Override
    public void start(){
        for(int i =0;i<min;i++){
            ScalableThread thread = new ScalableThread();
            thread.start();
            currentThread++;
        }
    }
    public synchronized void execute(Runnable runnable)
    {
        synchronized (lock){
            tasks.add(runnable);
            if(currentWorkedThreads + tasks.size()>=currentThread && currentThread<max){
                currentThread++;
                new ScalableThreadPool.ScalableThread().start();
            } else lock.notify();
        }
    }
}
