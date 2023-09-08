import java.util.LinkedList;
import java.util.Queue;

public class ThreadQueue extends Thread {
    private final int maxThreads;
    private final Queue<Runnable> threads;
    private int currentThreads;
    private boolean notStarted;

    public ThreadQueue(int maxThreads) {
        this.maxThreads = maxThreads;
        this.threads = new LinkedList<>();
        this.currentThreads = 0;
        this.notStarted = true;
    }

    @Override
    public void run() {
        while (currentThreads > 0 || threads.size() > 0 || notStarted) {
            if ((currentThreads < maxThreads) && (threads.size() > 0)) {
                Thread crawler = new Thread(threads.remove());
                crawler.start();
                currentThreads++;
            }
            synchronized (threads) {
                try {
                    if ((currentThreads >= maxThreads) || (threads.size() == 0)) {
                        threads.wait();
                    }
                } catch (InterruptedException e) {}
            }
        }
        synchronized (this) {
            this.notify();
        }
    }

    public void execute(Runnable crawler) {
        System.out.println("executing");
        synchronized (threads) {
            threads.add(crawler);
            threads.notify();
        }
        notStarted = false;
    }

    public synchronized void threadFinished() {
        System.out.println("finished");
        synchronized (threads) {
            currentThreads--;
            threads.notify();
        }
    }

    // public void shutdown() {
    //     // System.out.println("shutdown");
    //     this.open = false;
    // }

    public synchronized void awaitTermination() throws InterruptedException {
        System.out.println("awaiting the end");
        if (currentThreads > 0 || threads.size() > 0) {
            this.wait();
        }

        synchronized (threads) {
            threads.notify();
        }
        System.out.println("The end has come");
    }

    public static ThreadQueue newFixedThreadPool(int maxRunnables) {
        ThreadQueue tq = new ThreadQueue(maxRunnables);
        tq.start();
        return tq;
    }
}
