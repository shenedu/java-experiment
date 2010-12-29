package me.shenfeng.luence;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.xml.sax.SAXException;

public class SaxWikipediaIndexBuilder extends BaseWikipediaIndexBuilder {

    private static Logger logger = Logger
            .getLogger(SaxWikipediaIndexBuilder.class);
    private long count = 0;
    private IndexWriter mWriter;
    private static final int WORKER_COUNT = Runtime.getRuntime()
            .availableProcessors();
    private ExecutorService mThreadPool = Executors
            .newFixedThreadPool(WORKER_COUNT);
    private BlockingQueue<WikipediaItem> mQueues = new ArrayBlockingQueue<WikipediaItem>(
            WORKER_COUNT * 2);
    private Runnable mIndexer = new Runnable() {
        @Override
        public void run() {
            try {
                while (true) {
                    WikipediaItem item = mQueues.take();
                    addDoc(item);
                }
            } catch (InterruptedException e) {
                logger.info(e.getMessage(), e);
            }
        }
    };

    @Override
    public void process(File wikipediapath)
            throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        WikipediaHandler handler = new WikipediaHandler(this);
        for (int i = 0; i < WORKER_COUNT; ++i) {
            mThreadPool.submit(mIndexer);
        }
        parser.parse(wikipediapath, handler);
        mThreadPool.shutdownNow();
    }

    @Override
    public void onItem(WikipediaItem item) {

        count++;
        if (count % 100000 == 0) {
            logger.info(count);
        }

        try {
            mQueues.put(item);
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    private void addDoc(WikipediaItem item) {
        Document doc = new Document();
        if (item.getTitle() != null)
            doc.add(new Field(TITLE, item.getTitle(), Store.YES, Index.ANALYZED));
        if (item.getText() != null)
            doc.add(new Field(TEXT, item.getText(), Store.NO, Index.ANALYZED));
        if (item.getComment() != null)
            doc.add(new Field(COMMENT, item.getComment(), Store.NO,
                    Index.ANALYZED));
        try {
            mWriter.addDocument(doc);
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(Directory directory) {
        try {
            mWriter = new IndexWriter(directory, new StandardAnalyzer(
                    Version.LUCENE_30), MaxFieldLength.UNLIMITED);
            // mWriter.setMergeFactor(2000);
            // mWriter.setRAMBufferSizeMB(500);
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (LockObtainFailedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        mWriter.close();
    }
}
