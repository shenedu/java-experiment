package inaction;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class Indexer {

    public static List<File> listRecursive(File folder, FileFilter filter) {
        List<File> files = new ArrayList<File>();
        File[] fs = folder.listFiles();
        for (File f : fs) {
            if (f.isFile() && filter.accept(f))
                files.add(f);
            else
                listRecursive(files, f, filter);
        }
        return files;
    }

    private static void listRecursive(List<File> files, File folder,
            FileFilter filter) {
        File[] fs = folder.listFiles();
        if (fs != null) {
            for (File f : fs) {
                if (f.isFile() && filter.accept(f))
                    files.add(f);
                else if (f.isDirectory())
                    listRecursive(files, f, filter);
            }
        }
    }

    private static Logger logger = Logger.getLogger(Indexer.class);
    private static final String INDEX_DIR = "/tmp/index";
    public static final FileFilter CLJ_FILE_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.getName().toLowerCase().endsWith(".clj");
        }
    };

    @Test
    public void testSimpleIndexer() throws IOException {
        FSDirectory fs = FSDirectory.open(new File(INDEX_DIR));
        IndexWriter indexWriter = new IndexWriter(fs, new StandardAnalyzer(
                Version.LUCENE_30), true, MaxFieldLength.UNLIMITED);
        indexWriter.setInfoStream(System.out);
        // indexWriter.setUseCompoundFile(false);
        File src = new File("/home/feng/workspace/onycloud/");
        List<File> javas = listRecursive(src, CLJ_FILE_FILTER);
        long start = System.currentTimeMillis();
        for (File file : javas) {
            logger.info("add file " + file);
            Document document = new Document();
            document.add(new Field("content", new FileReader(file)));
            document.add(new Field("filename", file.getName(), Store.YES,
                    Index.NOT_ANALYZED));
            document.add(new Field("fullpath", file.getAbsolutePath(),
                    Store.YES, Index.NOT_ANALYZED));
            indexWriter.addDocument(document);
        }
        logger.info("index " + javas.size() + " files, takes "
                + (System.currentTimeMillis() - start) + " ms");
        indexWriter.close();
        fs.close();
    }

    @Test
    public void testSimpleSearcher() throws IOException, ParseException {
        Directory directory = FSDirectory.open(new File(INDEX_DIR));
        IndexSearcher searcher = new IndexSearcher(directory);
        QueryParser parser = new QueryParser(Version.LUCENE_30, "content",
                new StandardAnalyzer(Version.LUCENE_30));
        Query query = parser.parse("database");
        long start = System.currentTimeMillis();
        TopDocs topDocs = searcher.search(query, 2);
        logger.info("find " + topDocs.totalHits + " in "
                + (System.currentTimeMillis() - start) + " ms");
        for (ScoreDoc doc : topDocs.scoreDocs) {
            Document d = searcher.doc(doc.doc);
            logger.info("path: " + d.get("fullpath"));
        }
        searcher.close();
        directory.close();

    }
}
