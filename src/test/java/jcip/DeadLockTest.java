package jcip;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class DeadLockTest {

	private static class TestRunnale implements Runnable {

		private Object lock1;
		private Object lock2;
		private Random r = new Random();

		public TestRunnale(Object lock1, Object lock2) {
			this.lock1 = lock1;
			this.lock2 = lock2;
		}

		@Override
		public void run() {
			while (true) {
				synchronized (lock1) {
					try {
						TimeUnit.MILLISECONDS.sleep(r.nextInt(100));
						System.out.println(Thread.currentThread().getName()
								+ " success");
					} catch (InterruptedException ingore) {
					}
					synchronized (lock2) {
						try {
							TimeUnit.MILLISECONDS.sleep(r.nextInt(100));
							System.out.println(Thread.currentThread().getName()
									+ " success");
						} catch (InterruptedException ingore) {
						}
					}
				}
			}
		}

	}

	// this sure will deak lock. kill -3 {pid} to thread dump, or 
	// Ctrl+\ on linux and Ctrl+Break on windows
	public static void main(String[] args) throws InterruptedException {
		new DeadLockTest().testDeadLock();
	}

	@Test
	public void testDeadLock() throws InterruptedException {
		final Object lock1 = new Object();
		final Object lock2 = new Object();
		Thread t1 = new Thread(new TestRunnale(lock1, lock2));
		Thread t2 = new Thread(new TestRunnale(lock2, lock1));

		t1.start();
		t2.start();

		t1.join();
		t2.join();
	}
}
