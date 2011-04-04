package feedbooks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DownloadManager {
    private final int threadCount;
    private Queue<TaskInfo> tasks = new LinkedList<TaskInfo>();
    private ExecutorService exes;

    private synchronized TaskInfo getNewTask() {
        return tasks.poll();
    }

    private synchronized void retryTask(TaskInfo task) {
        tasks.add(task);
    }

    private synchronized int getSize() {
        return tasks.size();
    }

    private static class TaskInfo {
        File dest;
        URL url;

        public TaskInfo(File dest, URL url) {
            this.dest = dest;
            this.url = url;
        }
    }

    public DownloadManager(File meta, File destDir, int threadCount)
            throws IOException {
        this.threadCount = threadCount;
        exes = Executors.newFixedThreadPool(threadCount);
        BufferedReader br = new BufferedReader(new FileReader(meta));

        String line = null;
        while ((line = br.readLine()) != null) {
            String[] split = line.split("\t");
            String name = split[0];
            File dest = new File(destDir, name.split("\\.|-")[0] + "/" + name);
            URL url = new URL(split[1]);
            tasks.add(new TaskInfo(dest, url));
        }
        System.out.println("all tasks " + tasks.size());
        File[] files = destDir.listFiles();
        if (files != null) {
            for (File file : files) {
                File[] subs = file.listFiles();
                for (File sub : subs) {
                    for (Iterator ite = tasks.iterator(); ite.hasNext();) {
                        TaskInfo type = (TaskInfo) ite.next();
                        if (type.dest.getName().equals(sub.getName())) {
                            ite.remove();
                            System.out.println("already finished " + type.dest);
                        }
                    }
                }
            }
        }
        System.out.println(tasks.size() + " to do done!");

    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            TaskInfo task = getNewTask();
            while (task != null) {
                try {
                    Downloader.download(task.url, task.dest);
                    System.out.println("complete " + task.url + " ;remain "
                            + getSize());
                } catch (IOException e) {
                    retryTask(task);
                    System.err.println("retry " + task.url + " ;remain "
                            + getSize());
                } finally {
                    task = getNewTask();
                }
            }
        }
    };

    public void startDownload() {
        for (int i = 0; i < threadCount; i++) {
            exes.submit(run);
        }
    }

    public void waitFinish() throws InterruptedException {
        // exes.shutdown();
        exes.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
    }

    public static void main(String[] args) throws IOException,
            InterruptedException {
        int threadCount = 4;
        
        if (args.length == 1) {
            threadCount = Integer.parseInt(args[0]);
        }
        System.out.println(new File(".").getAbsolutePath());
        File list = new File("/tmp/meta.txt");
        File destdir = new File("/tmp/download/download");
        System.out.println("running at " + threadCount + " thread");
        DownloadManager manager = new DownloadManager(list, destdir,
                threadCount);
        manager.startDownload();
        manager.waitFinish();
    }
}
