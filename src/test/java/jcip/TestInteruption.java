package jcip;

public class TestInteruption implements Runnable {

    public void run() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException consumed) {
            Thread.currentThread().interrupt();
        }
    }
}
