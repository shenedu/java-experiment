package inaction;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IndexingTest {
    private static final String ID = "id";
    private static final String COUNTRY = "country";
    private static final String CONTENTS = "contents";
    private static final String CITY = "city";

    protected String[] ids = { "1", "2" };
    protected String[] countries = { "china", "china" }; // country
    protected String[] contents = { "beijing is a big city", // contents
            "Guangzhou is a big city too" };
    protected String[] cities = { "beijing", "guangzhou" };
    private Analyzer analyzer

    = new StandardAnalyzer(Version.LUCENE_30);
    private Directory directory;

    public int getHitCount(String fieldName, String searchString)
            throws CorruptIndexException, IOException {
        IndexSearcher searcher = new IndexSearcher(directory);
        TermQuery query = new TermQuery(new Term(fieldName, searchString));
        // QueryParser parser = new QueryParser(Version.LUCENE_30, fieldName,
        // analyzer);
        // Query query = null;
        // try {
        // query = parser.parse(searchString);
        // } catch (ParseException e) {
        // e.printStackTrace();
        // }
        // TermQuery t = new TermQuery(new Term(fieldName, searchString));
        TopDocs topDocs = searcher.search(query, 1);
        searcher.close();
        return topDocs.totalHits;
    }

    public IndexWriter getWriter() throws CorruptIndexException,
            LockObtainFailedException, IOException {
        IndexWriter indexWriter = new IndexWriter(directory,
                new WhitespaceAnalyzer(), MaxFieldLength.LIMITED);
        indexWriter.setInfoStream(System.out);
        return indexWriter;
    }

    @Before
    public void setUp() throws CorruptIndexException,
            LockObtainFailedException, IOException {
        // directory = new RAMDirectory();
        directory = FSDirectory.open(new File("/tmp/index"));
        IndexWriter indexWriter = new IndexWriter(directory,
                new WhitespaceAnalyzer(), true, MaxFieldLength.LIMITED);
        indexWriter.setInfoStream(System.out);
        for (int i = 0; i < ids.length; ++i) {
            Document document = new Document();
            document.add(new Field(ID, ids[i], Store.YES, Index.NOT_ANALYZED,
                    TermVector.WITH_POSITIONS_OFFSETS));
            Field country = new Field(COUNTRY, countries[i], Store.YES,
                    Index.NOT_ANALYZED);
            country.setBoost(1.2F);
            document.add(country);
            document.add(new Field(CONTENTS, contents[i], Store.NO,
                    Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS));
            document.add(new Field(CITY, cities[i], Store.YES,
                    Index.NOT_ANALYZED, TermVector.WITH_POSITIONS_OFFSETS));
            indexWriter.addDocument(document);
        }
        indexWriter.close();
    }

    @Test
    public void testDeleteAfterOptimize() throws CorruptIndexException,
            LockObtainFailedException, IOException {
        IndexWriter writer = getWriter();
        writer.deleteDocuments(new Term("id", "1"));
        writer.optimize();
        writer.commit();
        Assert.assertEquals(ids.length - 1, writer.maxDoc());
        Assert.assertEquals(ids.length - 1, writer.numDocs());
        writer.close();
    }

    @Test
    public void testDeleteBeforeOptimize() throws CorruptIndexException,
            LockObtainFailedException, IOException {
        IndexWriter writer = getWriter();
        writer.deleteDocuments(new Term("id", "1"));
        writer.commit();
        Assert.assertEquals(ids.length, writer.maxDoc());
        Assert.assertEquals(ids.length - 1, writer.numDocs());
        writer.close();
    }

    @Test
    public void testIndexReader() throws CorruptIndexException, IOException {
        IndexReader reader = IndexReader.open(directory);
//        Collection<IndexCommit> commits = IndexReader.listCommits(directory);

        Assert.assertEquals(ids.length, reader.maxDoc());
        Assert.assertEquals(ids.length, reader.numDocs());
        reader.close();
    }

    @Test
    public void testIndexWriter() throws CorruptIndexException,
            LockObtainFailedException, IOException {
        IndexWriter writer = getWriter();
        Assert.assertEquals(ids.length, writer.numDocs());
        writer.close();
    }

    @Test
    public void testUpdate() throws CorruptIndexException, IOException {
        final int index = 0;
        final String newCity = "new city";
        Assert.assertEquals(1, getHitCount(CITY, cities[index]));
        IndexWriter writer = getWriter();
        Document document = new Document();
        document.add(new Field(ID, ids[index], Store.YES, Index.NOT_ANALYZED));
        Field country = new Field(COUNTRY, countries[index], Store.YES,
                Index.NOT_ANALYZED, TermVector.WITH_POSITIONS_OFFSETS);
        country.setBoost(1.2F);
        document.add(country);
        document.add(new Field(CONTENTS, "this is an update content", Store.NO,
                Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS));

        document.add(new Field(CITY, newCity, Store.YES, Index.NOT_ANALYZED,
                TermVector.WITH_POSITIONS_OFFSETS));
        writer.updateDocument(new Term(CITY, cities[index]), document);
        writer.optimize();
        writer.close();

        Assert.assertEquals(0, getHitCount(CITY, cities[index]));
        Assert.assertEquals(1, getHitCount(CITY, newCity));
    }
}
