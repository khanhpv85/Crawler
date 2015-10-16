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

//import com.sleepycat.je.txn.LockerFactory;
import com.crawler.app.crawler.CrawlConfig;
import com.crawler.app.crawler.CrawlController;
import com.crawler.app.fetcher.PageFetcher;
import com.crawler.app.robotstxt.RobotstxtConfig;
import com.crawler.app.robotstxt.RobotstxtServer;
/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
*/
/**
 * @author Yasser Ganjisaffar [lastname at gmail dot com]
 */
public class JellyfishCrawlSiteMNMNController {
  //private static Logger logger = LoggerFactory.getLogger(JellyfishCrawlSiteCBController.class);

  public static void main(String[] args) throws Exception {
	  try {
		  //logger.info("Start...: ");
		  /*
	    if (args.length != 2) {
	      logger.info("Needed parameters: ");
	      logger.info("\t rootFolder (it will contain intermediate crawl data)");
	      logger.info("\t numberOfCralwers (number of concurrent threads)");
	      return;
	    }
	*/
	    /*
	     * crawlStorageFolder is a folder where intermediate crawl data is
	     * stored.
	     */
	    String crawlStorageFolder = "D:/Java/my-app/storage";//args[0];
	
	    /*
	     * numberOfCrawlers shows the number of concurrent threads that should
	     * be initiated for crawling.
	     */
	    //int numberOfCrawlers = Integer.parseInt(args[1]);
	    int numberOfCrawlers = 1;
	    
	    CrawlConfig config = new CrawlConfig();
	
	    config.setCrawlStorageFolder(crawlStorageFolder);
	    
	    config.setIncludeHttpsPages(true);
	
	    /*
	     * Be polite: Make sure that we don't send more than 1 request per
	     * second (1000 milliseconds between requests).
	     */
	    config.setPolitenessDelay(1000);
	
	    /*
	     * You can set the maximum crawl depth here. The default value is -1 for
	     * unlimited depth
	     */
	    config.setMaxDepthOfCrawling(-1);//( use -1 for unlimited depth )
	
	    /*
	     * You can set the maximum number of pages to crawl. The default value
	     * is -1 for unlimited number of pages
	     */
	    config.setMaxPagesToFetch(-1);//( use -1 for unlimited pages )
	
	    /**
	     * Do you want crawler4j to crawl also binary data ?
	     * example: the contents of pdf, or the metadata of images etc
	     */
	    config.setIncludeBinaryContentInCrawling(false);
	
	    /*
	     * Do you need to set a proxy? If so, you can use:
	     * config.setProxyHost("proxyserver.example.com");
	     * config.setProxyPort(8080);
	     *
	     * If your proxy also needs authentication:
	     * config.setProxyUsername(username); config.getProxyPassword(password);
	     */
	
	    /*
	     * This config parameter can be used to set your crawl to be resumable
	     * (meaning that you can resume the crawl from a previously
	     * interrupted/crashed crawl). Note: if you enable resuming feature and
	     * want to start a fresh crawl, you need to delete the contents of
	     * rootFolder manually.
	     */
	    config.setResumableCrawling(false);
	    
	    /*
	     * Overwrite user ddagent
	     */
	    config.setUserAgentString("Test");
	
	    /*
	     * Instantiate the controller for this crawl.
	     */
	    PageFetcher pageFetcher = new PageFetcher(config);
	    RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
	    // by me
	    //robotstxtConfig.setEnabled(false);
	    RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
	    // by me    
	    CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
	
	    /*
	     * For each crawl, you need to add some seed urls. These are the first
	     * URLs that are fetched and then the crawler starts following links
	     * which are found in these pages
	     */
	    //controller.addSeed("http://careerbuilder.vn/");
	    /*
	    * 7 
	    * http://part.mynavi.jp/kyusyu/hour_1-2-6/kodawari_24-45/?sortType=default&pageCount=50&select=area&pageNo=1
	    */
	    /*
	     * 6
	     * http://part.mynavi.jp/shikoku/hour_1-2-6/kodawari_24-45/?sortType=default&pageCount=50&select=area&pageNo=1
	     */
	    
	    /*
	     * 5
	     * http://part.mynavi.jp/hokuriku/hour_1-2-6/kodawari_24-45/?sortType=default&pageCount=50&select=area&pageNo=1
	     */
	    
	    /*
	     * 4
	     * http://part.mynavi.jp/hokkaido/hour_1-2-6/kodawari_24-45/?sortType=default&pageCount=50&select=area&pageNo=1
	     */
	    
	    /*
	     * 3
	     * http://part.mynavi.jp/tokai/hour_1-2-6/kodawari_24-45/?sortType=default&pageCount=50&select=area&pageNo=1
			http://part.mynavi.jp/tokai/hour_1-2-6/kodawari_24-45/?sortType=default&pageCount=50&select=area&pageNo=2
	     */
	    
	    /*
	     * 2
	     * http://part.mynavi.jp/kansai/hour_1-2-6/kodawari_24-45/?sortType=default&pageCount=50&select=area&pageNo=1
			http://part.mynavi.jp/kansai/hour_1-2-6/kodawari_24-45/?sortType=default&pageCount=50&select=area&pageNo=2
			http://part.mynavi.jp/kansai/hour_1-2-6/kodawari_24-45/?sortType=default&pageCount=50&select=area&pageNo=3
	
	     */
	    
	    controller.addSeed("http://monngonmoingay.com");
	    /*
	    
	    for(int j=1;j<=15;j++){    
	    	String urlCrawl = "http://part.mynavi.jp/kanto/hour_1-2-6/kodawari_24-45/?sortType=default&pageCount=50&select=area&pageNo="+j;
	    	controller.addSeed(urlCrawl);
	    }*/
		
	    /*
	     * Start the crawl. This is a blocking operation, meaning that your code
	     * will reach the line after this only when crawling is finished.
	     */
	    controller.start(JellyfishCrawlerSiteMNMN.class, numberOfCrawlers);
	  } catch (Exception ex) {
		  //System.out.println("\n Fail I : " + i);
		  System.out.println("\n Ex : " + ex);
	  }
  } 
}