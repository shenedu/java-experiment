package csstricks;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class Download {
    private static Logger logger = Logger.getLogger(Download.class);

    public static void main(String[] args) throws IOException {
        Download manager = new Download();
        // String
        manager.download(new URL(
                "http://files/java/eclipse-jee-helios-SR1-linux-gtk.tar.gz"),
                new File("/tmp/what.gif"));
    }

    public Set<String> getURLs(URL url) throws IOException {
        InputStream stream = url.openConnection().getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        // System.out.println(sb.toString());
        Set<String> urls = new HashSet<String>();

        Pattern p = Pattern.compile("\\shref=\"(.+?)\"");
        Matcher matcher = p.matcher(sb);
        while (matcher.find()) {
            String u = matcher.group(1);
            urls.add(u);
        }
        return urls;
    }

    public long getLength(URL url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("HEAD");
        return Long.parseLong(con.getHeaderField("Content-Length"));
    }

    private static final DecimalFormat df = new DecimalFormat("##.##");

    private String percent(long total, long finished) {
        double p = (double) finished / total * 100;
        return df.format(p);
    }

    private static String format(double d) {
        return df.format(d);
    }

    public void download(URL url, File dest) throws IOException {
        logger.info(url + "-->" + dest);
        for (int i = 0; i < 30; ++i) {
            try {
                HttpURLConnection con = (HttpURLConnection) url
                        .openConnection();
                long readed = 0; // bytes this time download
                long totalLength = getLength(url); // total length of the
                                                   // resource
                long downloaded = 0; // already downloaded before this try
                double lastPercent = 0.0;
                if (dest.exists()) {
                    downloaded = dest.length();
                    if (totalLength == downloaded) {
                        logger.info("already finished, exits");
                        return;
                    } else if (totalLength < downloaded) {
                        logger.error(downloaded + " > " + totalLength
                                + "; error!");
                        return;
                    }
                    con.addRequestProperty("range", "bytes=" + downloaded + "-"
                            + totalLength);
                    logger.info("resume download; " + downloaded + "/"
                            + totalLength + "; "
                            + percent(totalLength, downloaded));
                    lastPercent = (double) downloaded / totalLength;
                }
                con.connect();

                Map<String, List<String>> headers = con.getHeaderFields();
                for (Entry<String, List<String>> e : headers.entrySet()) {
                    logger.info(e.getKey() + ": " + e.getValue());
                }

                InputStream stream = con.getInputStream();
                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(dest, true));
                long startTime = System.currentTimeMillis();
                long lastReadPosition = readed;
                long lastTime = startTime;

                byte[] buffer = new byte[8912 * 2];
                int read = -1;

                while ((read = stream.read(buffer)) != -1) {
                    readed += read;
                    bos.write(buffer, 0, read);
                    double percent = (double) (readed + downloaded)
                            / totalLength;
                    if (percent - lastPercent > 0.01) {
                        long currentTime = System.currentTimeMillis();
                        String lastSpeed = format((double) (readed - lastReadPosition)
                                / (currentTime - lastTime) * 1000 / 1024)
                                + "k/s";

                        String speed = format((double) readed
                                / (currentTime - startTime) * 1000 / 1024)
                                + "k/s";

                        String leftTime = format((double) (totalLength - readed - downloaded)
                                / readed * (currentTime - startTime) / 1000)
                                + "s";

                        String p = format((double) (readed + downloaded)
                                / totalLength * 100)
                                + "%";

                        logger.info(p + " all/current [" + speed + "/"
                                + lastSpeed + "] in [" + leftTime + "] "
                                + (readed + downloaded) + "/" + totalLength
                                + "-->" + dest.getName());

                        lastTime = currentTime;
                        lastReadPosition = readed;
                        lastPercent = percent;
                    }
                }
                if ((readed + downloaded) == totalLength) {
                    logger.info("finished download!--->" + dest);
                }
                bos.close();
                break;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
