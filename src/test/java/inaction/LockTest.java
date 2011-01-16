package inaction;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;

public class LockTest {

    private Directory dir;

    @Before
    public void setUp() throws IOException {
        dir = FSDirectory.open(Files.createTempDir());
    }

    @Test
    public void testWriteLock() throws CorruptIndexException,
            LockObtainFailedException, IOException {
        IndexWriter writer1 = new IndexWriter(dir, new StandardAnalyzer(
                Version.LUCENE_30), MaxFieldLength.UNLIMITED);
        writer1.setInfoStream(System.out);
        IndexWriter writer2 = null;

        try {
            writer2 = new IndexWriter(dir, new StandardAnalyzer(
                    Version.LUCENE_30), MaxFieldLength.UNLIMITED);
            Assert.fail("we should never reach this point");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writer1.close();
            Assert.assertNull(writer2);
        }
    }
}
