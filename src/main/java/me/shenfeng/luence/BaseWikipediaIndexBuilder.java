package me.shenfeng.luence;

import java.text.SimpleDateFormat;

public abstract class BaseWikipediaIndexBuilder implements
        WikipediaIndexBuilder {

    static final SimpleDateFormat FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ssZ");
    static final String PAGE = "page";
    static final String TITLE = "title";
    static final String TIMESTAMP = "timestamp";
    static final String TEXT = "text";
    static final String COMMENT = "comment";

}
