package csstricks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DownloadManager {

	public static void main(String[] args) throws MalformedURLException,
			IOException, InterruptedException {
		BufferedReader br = new BufferedReader(new FileReader("/tmp/seed.txt"));
		String line = null;
		final Download d = new Download();
		final String template = "http://vnfiles.ign.com/ects/css-tricks/VideoCast-%s.m4v";
		ExecutorService execs = Executors.newFixedThreadPool(10);
		while ((line = br.readLine()) != null) {
			String index = line.split("-")[0];
			final URL url = new URL(String.format(template, index));
			final File dest = new File("/home/feng/Downloads/css-tricks/"
					+ line + ".m4v");
			execs.submit(new Runnable() {
				@Override
				public void run() {
					try {
						d.download(url, dest);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
		execs.shutdown();
		execs.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
	}

	public void start() throws MalformedURLException, IOException {
		final String prefix = "/video-screencasts/";

		Download d = new Download();
		Set<String> urLs = d.getURLs(new URL(
				"http://css-tricks.com/video-screencasts/"));
		urLs.addAll(d.getURLs(new URL(
				"http://css-tricks.com/video-screencasts-5/")));
		urLs.addAll(d.getURLs(new URL(
				"http://css-tricks.com/video-screencasts-4/")));
		urLs.addAll(d.getURLs(new URL(
				"http://css-tricks.com/video-screencasts-3/")));
		urLs.addAll(d.getURLs(new URL(
				"http://css-tricks.com/video-screencasts-2/")));

		for (Iterator iterator = urLs.iterator(); iterator.hasNext();) {
			String u = (String) iterator.next();
			if (!u.startsWith(prefix) || u.equals(prefix)) {
				iterator.remove();
			}
		}

		for (String u : urLs) {
			System.out.println(u);
		}

		System.out.println(urLs.size());
	}
}
