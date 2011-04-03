package me.shenfeng.luence;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.store.Directory;
import org.xml.sax.SAXException;

public interface WikipediaIndexBuilder extends Closeable {

    public void onItem(WikipediaItem item);

    public void process(File wikipediapath)
            throws ParserConfigurationException, SAXException, IOException;

    void init(Directory directory);
}
