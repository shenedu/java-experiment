package me.shenfeng.luence;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.lucene.store.MMapDirectory;
import org.xml.sax.SAXException;

/**
 * Hello world!
 * 
 */
public class App {
    private static final Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) throws ParserConfigurationException,
            SAXException, IOException {
        logger.info("start");
        WikipediaIndexBuilder builder = new SaxWikipediaIndexBuilder();
        File wiki = new File(
                "/home/feng/Downloads/apache-mahout-examples/wikipedia/enwiki-20070527-pages-articles.xml");
        builder.init(new MMapDirectory(new File("/tmp/index")));

        builder.process(wiki);

        builder.close();

        logger.info("end");
    }
}
