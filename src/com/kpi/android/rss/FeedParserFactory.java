package com.kpi.android.rss;


public abstract class FeedParserFactory {
	//static String feedUrl = "http://www.androidster.com/android_news.rss";
	//static String feedUrl = "http://habrahabr.ru/rss/index/";
	//static String feedUrl = "http://kpi.ua/rss.xml";
	static String feedUrl = "";
	
	public static FeedParser getParser(){
		return getParser(ParserType.ANDROID_SAX, feedUrl);
	}
	
	public static FeedParser getParser(ParserType type, String url){
		feedUrl=url;
		
		switch (type){
			case SAX:
				return new SaxFeedParser(feedUrl);
			case DOM:
				return new DomFeedParser(feedUrl);
			case ANDROID_SAX:
				return new AndroidSaxFeedParser(feedUrl);
			case XML_PULL:
				return new XmlPullFeedParser(feedUrl);
			default: return null;
		}
	}
}
