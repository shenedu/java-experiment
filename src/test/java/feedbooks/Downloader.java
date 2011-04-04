package feedbooks;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class Downloader {

    public static void download(URL url, File dest) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.addRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.10) Gecko/20100922 Ubuntu/10.10 (maverick) Firefox/3.6.10");
        con.addRequestProperty("Accept-Encoding", "gzip,deflate");
        con.addRequestProperty("Cache-Control", "max-age=0");
        con.addRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        con.connect();
        boolean gzipResponce = false;
        Map<String, List<String>> headers = con.getHeaderFields();
        List<String> list = headers.get("Content-Encoding");
        if (list != null && list.size() > 0) {
            gzipResponce = list.get(0).toLowerCase().contains("gzip");
        }

        InputStream stream = con.getInputStream();
        if (gzipResponce) {
            stream = new GZIPInputStream(stream);
        }

        if (!dest.getParentFile().exists()) {
            if (!dest.getParentFile().mkdirs())
                throw new RuntimeException("Could not mkdir dir for " + dest);
        }

        File tmp = new File(dest.getAbsolutePath().trim() + ".tmp");

        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(tmp, false));

        byte[] buffer = new byte[8912];
        int read = -1;
        while ((read = stream.read(buffer)) != -1) {
            bos.write(buffer, 0, read);
        }
        bos.close();
        tmp.renameTo(dest);
    }
}
