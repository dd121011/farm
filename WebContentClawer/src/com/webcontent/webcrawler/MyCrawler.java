package com.webcontent.webcrawler;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.webcontent.task.crawlerTask;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" 
                                                      + "|png|tiff?|mid|mp2|mp3|mp4"
                                                      + "|wav|avi|mov|mpeg|ram|m4v|pdf" 
                                                      + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    /**
     * You should implement this function to specify whether
     * the given url should be crawled or not (based on your
     * crawling logic).
     */
    @Override
    public boolean shouldVisit(WebURL url) {
            String href = url.getURL().toLowerCase();
            return !FILTERS.matcher(href).matches() && href.startsWith("http://www.njl6.com/");
    }

    /**
     * This function is called when a page is fetched and ready 
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) {          
            String url = page.getWebURL().getURL();
           // if(url.startsWith("http://www.njl6.com/sell/14/132.html"))
            //System.out.println("URL: " + url);
           // String text = url.substring(url.indexOf("sell")+5);
            
            if (page.getParseData() instanceof HtmlParseData) {
                    HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                    String text = htmlParseData.getText();
                    String html = htmlParseData.getHtml();
                    List<WebURL> links = htmlParseData.getOutgoingUrls();
                    Pattern p = Pattern.compile("http://www.njl6.com/sell/+\\d+/+\\d+.html");
                    Matcher m = p.matcher(url);
                    if(m.matches())
                    {
                    	System.out.println("∂¡»°url="+url);
                    	new crawlerTask(url,html).start();
                    }
            }
           
            
    }
}
