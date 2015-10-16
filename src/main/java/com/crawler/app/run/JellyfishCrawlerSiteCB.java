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
public class JellyfishCrawlerSiteCB extends WebCrawler {

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

	  //return !BINARY_FILES_EXTENSIONS.matcher(href).matches() && href.startsWith("http://jobs.jobstreet.com/vn/jobs/") && href.endsWith("jd");
	  return !BINARY_FILES_EXTENSIONS.matcher(href).matches() && href.startsWith("http://careerbuilder.vn/");
	  //return !BINARY_FILES_EXTENSIONS.matcher(href).matches() && href.startsWith("http://jobs.jobstreet.com/vn/jobs/");
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
	if (href.startsWith("http://careerbuilder.vn/vi/tim-viec-lam") && href.endsWith(".html")) {
	    if (page.getParseData() instanceof HtmlParseData) {
	      HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
	      String text = htmlParseData.getText();
	      String html = htmlParseData.getHtml();
	      String title = htmlParseData.getTitle();
	      
	      Document doc = Jsoup.parse(html, "UTF-8");
	      //doc.outputSettings().escapeMode(EscapeMode.xhtml);
	      Element body = doc.body();
		  
	      Elements listDetail = body.select("section div[class=MyJobLeft]");  
	      String jobUrl = url;
	      String jobName = listDetail.select("h1").html();      
	      String companyName = listDetail.select("div[class=tit_company]").html();
	      String jobLocation = listDetail.select("div[class=box2Detail] ul[class=DetailJobNew] p[class=fl_left] b[itemprop=jobLocation] a").html();	
	      String companyAddress = listDetail.select("div[class=box1Detail] p[class=TitleDetailNew] label[itemprop=addressLocality]").html();
	      String companyContact = listDetail.select("div[class=box1Detail] p[class=TitleDetailNew] label strong").html();
	      String companyPhone = listDetail.select("div[class=col-lg-6 col-md-6 col-sm-12] p[id=company_contact]").html();
	      String companyWebsite = listDetail.select("div[class=col-lg-6 col-md-6 col-sm-12] p a[id=company_website]").html();
	      
	      if (listDetail.isEmpty() || jobName.isEmpty()) {
	    	  listDetail = body.select("div[id=main_content] div[id=main_content_right]");
	    	  jobName = listDetail.select("h1 p").html(); 
	    	  companyName = listDetail.select("div[class=intro_company] div[class=title_into] p").html();
	    	  jobLocation = listDetail.select("div[class=intro_job] ul[class=left_380] p[itemprop=jobLocation] a").html();
	    	  if (listDetail.isEmpty() || jobName.isEmpty()) {
	    		  listDetail = body.select("div[id=main_content] div[class=content_right]");    		
	    		  jobName = listDetail.select("h1").html();
	    		  companyName = listDetail.select("div[class=intro_company] div[class=title_into] p[class=title_comp]").html();
	        	  Elements gCompanyWebList = listDetail.select("div[class=intro_company] div[class=title_into] p");
	        	  if (!gCompanyWebList.isEmpty() && gCompanyWebList.size() > 1)
	        		  companyWebsite = gCompanyWebList.get(1).html();
	        	  jobLocation = listDetail.select("div[class=intro_job] ul[class=left_380] p[itemprop=jobLocation] a").html();
	        	  
	
	    	  }
	      }      
	      jobName = listDetail.select("h1 a").html(); 
		  if (jobName.isEmpty())
			  jobName = listDetail.select("h1 p").html();
		  if (jobName.isEmpty())
			  jobName = listDetail.select("h1").html();
	  
	      System.out.println("\n Title : " + jobName);
	      try {			  
			  Integer siteID = 3;
			  //String companyWebsite = "";
			  /*
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