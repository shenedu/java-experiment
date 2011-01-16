package inaction;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IndexSearcherTest {

    private String[] books = { "linux performance tuning",
            "Git Community Book", "Lucene In Action",
            "JUnit in Action, Second Edition",
            "Professional.Android.2.Application.Development" };

    // private Directory directory = new RAMDirectory();
    private Directory directory;
    private IndexSearcher searcher;
    private Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
    private final String TITLE = "title";

    @Before
    public void setup() throws CorruptIndexException,
            LockObtainFailedException, IOException {
        directory = FSDirectory.open(new File("/tmp/index"));
        IndexWriter writer = new IndexWriter(directory, analyzer, true,
                MaxFieldLength.UNLIMITED);

        for (String book : books) {
            Document doc = new Document();
            doc.add(new Field(TITLE, book, Store.YES, Index.ANALYZED));
            writer.addDocument(doc);
        }
        writer.close();
        searcher = new IndexSearcher(directory);
    }

    @Test
    public void testTermQuerySearch() throws IOException {
        TermQuery query = new TermQuery(new Term(TITLE, "junit"));
        TopDocs topDocs = searcher.search(query, 1);
        Assert.assertEquals(1, topDocs.totalHits);
    }

    @After
    public void teardown() throws IOException {
        searcher.close();
        directory.close();
    }
}
