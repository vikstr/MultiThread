import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FixedThreadPool implements ThreadPool {
    private int n_threads;
    private final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();
    private  final Object lock = new Object();
    private final List<Thread> threads;
    public FixedThreadPool(int n_threads){
        this.n_threads = n_threads;
        threads = new ArrayList<>(n_threads);
        for (int i=0;i<n_threads;i++){
            threads.add(new FixedThread("Number is" + i));
        }
    }
    @Override
    public  void start(){
        for(int i=0;i<n_threads;i++){
            threads.get(i).start();
        }
    }
    public synchronized void execute(Runnable runnable)
    {
        synchronized (lock){
            tasks.add(runnable);
            lock.notify();
        }
    }

    private class FixedThread extends Thread {
        public FixedThread(String s) {
            super(s);
        }
        @Override
        public void run(){
            while (true){
                Runnable task;
                synchronized (lock){
                    while (tasks.isEmpty()){
                        try{
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    task = tasks.poll();
                }
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
