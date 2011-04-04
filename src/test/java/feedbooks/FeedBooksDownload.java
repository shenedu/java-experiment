package feedbooks;

// this is the first step of download public books of feedbooks

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class FeedBooksDownload {
    

    public static void main(String[] args) throws IOException {

        String uriTemplate = "http://www.feedbooks.com/books/top.atom?page=%d&range=month";
        String fileTemplate = "/tmp/feedbooks/pop/%d-public.opds";
        for (int i = 1; i < 169; i++) {
            URL url = new URL(String.format(uriTemplate, i));
            File file =  new File(String.format(fileTemplate, i));
            Downloader.download(url, file);
            System.out.println(url);
        }
    }
    
    
}
