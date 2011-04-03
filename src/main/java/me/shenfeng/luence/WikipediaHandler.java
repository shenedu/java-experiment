package me.shenfeng.luence;

import static me.shenfeng.luence.BaseWikipediaIndexBuilder.COMMENT;
import static me.shenfeng.luence.BaseWikipediaIndexBuilder.PAGE;
import static me.shenfeng.luence.BaseWikipediaIndexBuilder.TEXT;
import static me.shenfeng.luence.BaseWikipediaIndexBuilder.TIMESTAMP;
import static me.shenfeng.luence.BaseWikipediaIndexBuilder.TITLE;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WikipediaHandler extends DefaultHandler {

    private StringBuilder builder;
    private WikipediaItem currentItem;
    private WikipediaIndexBuilder indexBuilder;

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        builder.append(ch, start, length);
    }

    public WikipediaHandler(WikipediaIndexBuilder indexBuilder) {
        this.indexBuilder = indexBuilder;
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        super.endElement(uri, localName, qName);
        if (currentItem != null) {
            if (qName.equalsIgnoreCase(TITLE)) {
                currentItem.setTitle(builder.toString().trim());
            } else if (qName.equalsIgnoreCase(TIMESTAMP)) {
                currentItem.setTimestamp(builder.toString().trim());
            } else if (qName.equals(COMMENT)) {
                currentItem.setComment(builder.toString().trim());
            } else if (qName.equals(TEXT)) {
                currentItem.setText(builder.toString().trim());
            }
            builder.setLength(0);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        builder = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        super.startElement(uri, localName, qName, attributes);

        if (qName.equalsIgnoreCase(PAGE)) {
            if (currentItem != null) {
                indexBuilder.onItem(currentItem);
            }
            currentItem = new WikipediaItem();
        }
    }

}
