package me.shenfeng.luence;

import java.io.File;
import java.io.IOException;

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

	@Override
	public void process(File wikipediapath)
			throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		WikipediaHandler handler = new WikipediaHandler(this);
		parser.parse(wikipediapath, handler);
	}

	@Override
	public void onItem(WikipediaItem item) {

		count++;
		if (count % 30000 == 0) {
			logger.info(count);
		}

		addDoc(item);
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
