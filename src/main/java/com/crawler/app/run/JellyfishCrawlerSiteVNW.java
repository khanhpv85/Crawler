/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.crawler.app.run;

import com.crawler.app.crawler.Page;
import com.crawler.app.crawler.WebCrawler;
import com.crawler.app.parser.ExtractedUrlAnchorPair;
import com.crawler.app.parser.HtmlParseData;
import com.crawler.app.parser.HtmlContentHandler;
import com.crawler.app.storage.MysqlCrawler;
import com.crawler.app.url.WebURL;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
/**
 * @author Yasser Ganjisaffar [lastname at gmail dot com]
 */
public class JellyfishCrawlerSiteVNW extends WebCrawler {

  private final static Pattern BINARY_FILES_EXTENSIONS =
        Pattern.compile(".*\\.(bmp|gif|jpe?g|png|tiff?|pdf|ico|xaml|pict|rif|pptx?|ps" +
        "|mid|mp2|mp3|mp4|wav|wma|au|aiff|flac|ogg|3gp|aac|amr|au|vox" +
        "|avi|mov|mpe?g|ra?m|m4v|smil|wm?v|swf|aaf|asf|flv|mkv" +
        "|zip|rar|gz|7z|aac|ace|alz|apk|arc|arj|dmg|jar|lzip|lha)" +
        "(\\?.*)?$"); // For url Query parts ( URL?q=... )

  /**
   * You should implement this function to specify whether the given url
   * should be crawled or not (based on your crawling logic).
   */
  @Override
  public boolean shouldVisit(Page page, WebURL url) {
	  //return false;
	  
	  String href = url.getURL().toLowerCase();

	  return !BINARY_FILES_EXTENSIONS.matcher(href).matches() && href.startsWith("http://www.vietnamworks.com/");// && href.endsWith("jd");
	  //return !BINARY_FILES_EXTENSIONS.matcher(href).matches() && href.startsWith("http://careerbuilder.vn/vi") && href.endsWith(".html");
	  //return !BINARY_FILES_EXTENSIONS.matcher(href).matches() && href.startsWith("http://jobsearch.living.jp/kyujin/");
  }

  /**
   * This function is called when a page is fetched and ready to be processed
   * by your program.
   */
  @Override
  public void visit(Page page) {
    String url = page.getWebURL().getURL();    
    //logger.info("URL: ", url);
    String host = "127.0.0.1";
	String port = "3306";
	String dbName = "crawler";
	String dbUser = "root";
	String dbPwd = "";
    MysqlCrawler.createConn(host, port, dbName, dbUser, dbPwd);    
    System.out.println("\n URL visit: " + url);
    
    String href = url.toLowerCase();
	if (href.startsWith("http://www.vietnamworks.com/") && (href.endsWith("jd") || href.endsWith("jv") || href.endsWith("jv/"))) {

	    if (page.getParseData() instanceof HtmlParseData) {
	      HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
	      String text = htmlParseData.getText();
	      String html = htmlParseData.getHtml();
	      String title = htmlParseData.getTitle();
	      
	      Document doc = Jsoup.parse(html, "UTF-8");
	      //doc.outputSettings().escapeMode(EscapeMode.xhtml);
	      Element body = doc.body();
	      //get meta description content
	      //String description = doc.select("meta[name=description]").get(0).attr("content");
		  //System.out.println("Meta description : " + description);
		  
		  //Element e = doc.getElementById("detail_copyB");
	      Element detail = body.select("section[id=content]").first();
	      //String aTitlePost = getTagValues(e.toString(), "<h3>", "</h3>");
		  
		  String jobUrl = url;//detail.select("h3[class=title] a").first().attr("abs:href");
		  String jobName = detail.select("div[class=job-header-info] h1").html();
		  String companyName = detail.select("span[class=company-name text-lg block] strong").html();
		  String companyAddress = detail.select("span[class=company-address block]").html();
		  String jobLocation = detail.select("p[class=work-location] span[itemprop=address] a").html();
		  String companyContact = detail.select("div[class=col-xs-12 col-md-8 col-lg-8 pull-left] p strong").html();// div[class=company-info] span[class=company-address block] p
		  
		  System.out.println("\n Title : " + jobName);
		  System.out.println("\n Contact : " + companyContact);
		  
		  try {			 
			  /*
			  Integer siteID = 2;
			  String companyPhone = "", companyWebsite = "";
			  MysqlCrawler.getInstance().insertJFHRContents(
					  siteID
					  , jobUrl
					  , jobName
					  , jobLocation
					  , companyName
					  , companyAddress
					  , companyPhone
					  , companyContact
					  , companyWebsite);
						*/
			  //System.exit(1);
		  } catch (Exception ex) {
			  //System.out.println("\n Fail I : " + i);
			  System.out.println("\n Ex : " + ex);
		  } 
		  	  
			  
		  
		  //String eCrawl2 = listTD.get(0);
		  //String eCrawl3 = listTD.get(1);
		  /*
		  System.out.println("\n Cate : " + bCate);
		  System.out.println("\n Title : " + aTitlePost);
		  System.out.println("\n Date : " + hDatePost);*/
		  //System.out.println("\n E : " + listTD.toString() + " --- " + eCrawl2 + "----" + eCrawl3);
		  //System.out.println("\n Count : " + doc.toString());
		  //System.out.println("\n Total Div: --" + listDetail.size());
		  //System.exit(1);
	      
	      //String content = htmlParseData.getBodyText();
	      //Set<WebURL> links = htmlParseData.getOutgoingUrls();
	
	      //logger.debug("Text length: {}", text.length());
	      //System.out.println("Text length: {}" + text);
	      
	      //System.out.println("\n Title: {}" + title);
	      
	      //logger.debug("Html: {}", html);
	      //System.out.println("Html: {}" + html);
	     
	      //logger.debug("Number of outgoing links: {}", links.size());
	      //System.out.println("Number of outgoing links: {}" + links.size());
	      
	      //final String str = "<tag>apple</tag><b>hello</b><tag>orange</tag><tag>pear</tag>";
	      //System.out.println("\n Matcher: {}" + Arrays.toString(getTagValues(html).toArray())); // Prints [apple, orange, pear]
	      
	      //MysqlCrawler.getInstance().insertURL(url, title, "");      
	      
	    }
	}

/*
    Header[] responseHeaders = page.getFetchResponseHeaders();
    if (responseHeaders != null) {
      logger.debug("Response headers:");
      for (Header header : responseHeaders) {
        logger.debug("\t{}: {}", header.getName(), header.getValue());
      }
    }
*/
    logger.debug("=============");
  }
  

  private static String getTagValues(final String str, String tagStart, String tagEnd) {
	  String pattern = tagStart+"(.+?)"+tagEnd;
	  Pattern TAG_REGEX = Pattern.compile(pattern);
      //final List<String> tagValues = new ArrayList<String>();
      final Matcher matcher = TAG_REGEX.matcher(str);
      if (matcher.find()) {
    	  return matcher.group(1).toString();
      } else
    	  return "";
      /*
      while (matcher.find()) {
          tagValues.add(matcher.group(1));
      }
      return tagValues;*/
  }
  
  private static List<String> getListTagValues(final String str, String tagStart, String tagEnd) {
	  String pattern = tagStart+"(.+?)"+tagEnd;
	  Pattern TAG_REGEX = Pattern.compile(pattern);
      final List<String> tagValues = new ArrayList<String>();
      final Matcher matcher = TAG_REGEX.matcher(str);
      while (matcher.find()) {
          tagValues.add(matcher.group(1));
      }
      return tagValues;
  }
}