package csstricks;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CountDownLatchTest {

	private static class MyRunnable implements Runnable {

		private CountDownLatch latch;

		public MyRunnable(CountDownLatch latch) {
			this.latch = latch;
		}

		@Override
		public void run() {
			try {
				TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			latch.countDown();
//			latch.
			System.out.println("over");
		}

	}

	public static void main(String[] args) throws InterruptedException {

		int SIZE = 3;
		CountDownLatch latch = new CountDownLatch(SIZE);
		for (int i = 0; i < SIZE; i++) {
			new Thread(new MyRunnable(latch)).start();
		}

		latch.await();
		System.out.println("all finished");

	}
}
