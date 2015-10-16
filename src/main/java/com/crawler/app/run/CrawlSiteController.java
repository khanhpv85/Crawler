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

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.sleepycat.je.txn.LockerFactory;
import com.crawler.app.crawler.CrawlConfig;
import com.crawler.app.crawler.CrawlController;
import com.crawler.app.crawler.Page;
import com.crawler.app.crawler.exceptions.PageBiggerThanMaxSizeException;
import com.crawler.app.fetcher.PageFetchResult;
import com.crawler.app.fetcher.PageFetcher;
import com.crawler.app.robotstxt.RobotstxtConfig;
import com.crawler.app.robotstxt.RobotstxtServer;
import com.crawler.app.url.WebURL;

import org.apache.http.HttpStatus;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yasser Ganjisaffar [lastname at gmail dot com]
 */
public class CrawlSiteController {
	private static Logger logger = LoggerFactory
			.getLogger(CrawlSiteController.class);
	private static PageFetcher pageFetcher;
	public static String pathXmlFile = "D:\\/Java\\/CrawlConfig.xml";
	/*declare read config pagenumber end*/
	private static String totalProductSelect="";
	private static int totalProductIndexBegin= -1;
	private static int totalProductIndexEnd= -1;
	private static String numberProductInPageSelect="";
	private static String regex="";
	private static int regexFromIndexBegin=-1;
	private static int regexFromIndexEnd=-1;
	private static int regexToIndexBegin=-1;
	private static int regexToIndexEnd=-1;
	private static int totalProductPosition = -1;
	private static String totalProductSelectPosition = "";
	private static int numberProductInPagePosition = -1;
	private static String numberProductInPageSelectPosition = "";
	private static int timeOut = 5000;
	private static String decimalFormatTotalProduct = "";
	private static String pageNumberSelect = "";
	private static int pageNumberIndexBegin = -1;
	private static int pageNumberIndexEnd = -1;
	
	public static Boolean checkWebSite(URL url) {
		HttpURLConnection connection;
		int code = 0;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			code = connection.getResponseCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (code == 200) {
			return true;
		} else {
			return false;
		}

	}	
	
	public static void ReadConfigPageNumberEnd(org.w3c.dom.Element eElement){
		totalProductSelect = eElement.getElementsByTagName("TotalProductSelect").item(0).getTextContent();
		if(!eElement.getElementsByTagName("TotalProductIndexBegin").item(0).getTextContent().isEmpty()){
			totalProductIndexBegin= Integer.parseInt(eElement.getElementsByTagName("TotalProductIndexBegin").item(0).getTextContent());
		}
		if (!eElement.getElementsByTagName("TotalProductIndexEnd").item(0).getTextContent().isEmpty()){
			totalProductIndexEnd= Integer.parseInt(eElement.getElementsByTagName("TotalProductIndexEnd").item(0).getTextContent());
		}
		numberProductInPageSelect=eElement.getElementsByTagName("NumberProductInPageSelect").item(0).getTextContent();
		if (!eElement.getElementsByTagName("TotalProductPosition").item(0).getTextContent().isEmpty()){
			totalProductPosition=Integer.parseInt(eElement.getElementsByTagName("TotalProductPosition").item(0).getTextContent());
		}
		totalProductSelectPosition=eElement.getElementsByTagName("TotalProductSelectPosition").item(0).getTextContent();
		//regex= eElement.getElementsByTagName("regex").item(0).getTextContent();
		if(!eElement.getElementsByTagName("regexFromIndexBegin").item(0).getTextContent().isEmpty()){
			regexFromIndexBegin=Integer.parseInt(eElement.getElementsByTagName("regexFromIndexBegin").item(0).getTextContent());
		}
		if (!eElement.getElementsByTagName("regexFromIndexEnd").item(0).getTextContent().isEmpty()){
			regexFromIndexEnd=Integer.parseInt(eElement.getElementsByTagName("regexFromIndexEnd").item(0).getTextContent());
		}
		if (!eElement.getElementsByTagName("regexToIndexBegin").item(0).getTextContent().isEmpty()){
			regexToIndexBegin=Integer.parseInt(eElement.getElementsByTagName("regexToIndexBegin").item(0).getTextContent());
		}
		if (!eElement.getElementsByTagName("regexToIndexEnd").item(0).getTextContent().isEmpty()){
			regexToIndexEnd=Integer.parseInt(eElement.getElementsByTagName("regexToIndexEnd").item(0).getTextContent());
		}
		if (!eElement.getElementsByTagName("NumberProductInPagePosition").item(0).getTextContent().isEmpty()){
			numberProductInPagePosition=Integer.parseInt(eElement.getElementsByTagName("NumberProductInPagePosition").item(0).getTextContent());
		}
		numberProductInPageSelectPosition=eElement.getElementsByTagName("NumberProductInPageSelectPosition").item(0).getTextContent();
		pageNumberSelect=eElement.getElementsByTagName("PageNumberSelect").item(0).getTextContent();
		if (!eElement.getElementsByTagName("PageNumberIndexBegin").item(0).getTextContent().isEmpty()){
			pageNumberIndexBegin=Integer.parseInt(eElement.getElementsByTagName("PageNumberIndexBegin").item(0).getTextContent());
		}
		if (!eElement.getElementsByTagName("PageNumberIndexEnd").item(0).getTextContent().isEmpty()){
			pageNumberIndexEnd=Integer.parseInt(eElement.getElementsByTagName("PageNumberIndexEnd").item(0).getTextContent());
		}
	}
	
	public static Boolean tryParseIntByString(String value){
		try{
			Integer.parseInt(value);
			return true;
		}catch(NumberFormatException ex){
			return false;
		}
	}
	
	public static int getPageNumberEnd(String url){
		try {
			Connection.Response response =

						Jsoup.connect(url)
                    //enable for error urls
                    .ignoreHttpErrors(true)
                    //MAXIMUN TIME
                    .timeout(timeOut)
                    //This is to prevent producing garbage by attempting to parse a JPEG binary image
                    .ignoreContentType(true)
                    .execute();

            int status = response.statusCode();
            //after done
            if(status == 200){
                org.jsoup.nodes.Document doc = response.parse();
                if (!pageNumberSelect.isEmpty()){
                	int pageNumber =-1;
                	String strPageNumber =  doc.select(pageNumberSelect).text();
                	if (pageNumberIndexBegin > -1 && pageNumberIndexEnd > -1 ){
                		String strSplit = "0";
                		for ( int i=pageNumberIndexBegin;i<=pageNumberIndexEnd;i++){
	                		String ch = String.valueOf(strPageNumber.charAt(i));
	                		if (tryParseIntByString(ch)){
	                			strSplit += ch;
	                		}
	                	}
                		pageNumber = Integer.parseInt(strSplit);
                	}else{
                		pageNumber = Integer.parseInt(strPageNumber);
                	}
                	return pageNumber;
                	
                }else{
                	// get pagenumber with total product/numberproduct in page
	                String strTotalProduct = "";
	                if (totalProductPosition > -1){
	                	if (totalProductSelectPosition.isEmpty()){
	                		strTotalProduct = doc.select(totalProductSelect).get(totalProductPosition).text();
	                	}else{
	                		strTotalProduct = doc.select(totalProductSelect).get(totalProductPosition).select(totalProductSelectPosition).text();
	                	}
	                }else{
	                	strTotalProduct = doc.select(totalProductSelect).text().toString().trim();
	                }
	                int totalProduct = -1;
	                int i;
	                // get totalproduct
	                if(totalProductIndexBegin <0){
	                	String strNumberTotalProduct = "0";
	                	for(i=0;i<strTotalProduct.length();i++){
	                		String ch = String.valueOf(strTotalProduct.charAt(i));
	                		if(ch.isEmpty() || ch.equals(" ")){
	                			break;
	                		}
	                		if (tryParseIntByString(ch)){
	                			strNumberTotalProduct += ch;
	                		}
	                	}
	                	totalProduct = Integer.parseInt(strNumberTotalProduct);
	                }else{
	                	String strSplit= "0";
	                	for (i=totalProductIndexBegin;i<=totalProductIndexEnd;i++){
	                		String ch = String.valueOf(strTotalProduct.charAt(i));
	                		if (tryParseIntByString(ch)){
	                			strSplit += ch;
	                		}
	                	}
	                	totalProduct = Integer.parseInt(strSplit);
	                }
	                // get number product in page
	                int numberPage = -1;
	                String strNumberProductInPage = "";
	                if (numberProductInPagePosition > -1){
	                	if (numberProductInPageSelectPosition.isEmpty()){
	                		strNumberProductInPage = doc.select(numberProductInPageSelect).get(numberProductInPagePosition).text();
	                	}else{
	                		strNumberProductInPage = doc.select(numberProductInPageSelect).get(numberProductInPagePosition).select(numberProductInPageSelectPosition).text();
	                	}
	                }else{
	                	strNumberProductInPage = doc.select(numberProductInPageSelect).text();
	                }
	                
	                if (regexFromIndexEnd < 0 && regexToIndexEnd < 0){
	                	String strSplit = "0";
	                	for(i=0;i<strNumberProductInPage.length();i++){
	                		String ch = String.valueOf(strNumberProductInPage.charAt(i));
	                		if(ch.isEmpty() || ch.equals(" ")){
	                			break;
	                		}
	                		if (tryParseIntByString(ch)){
	                			strSplit += ch;
	                		}
	                	}
	                	 int numberProductInPage = Integer.parseInt(strSplit);
	                     if (totalProduct > -1 && numberProductInPage > 0 ){
	                    	 if ((totalProduct % numberProductInPage)==0){
	                    		 numberPage= totalProduct / numberProductInPage;
	                    	 }else if (totalProduct > numberProductInPage){
	                    		 numberPage= totalProduct / numberProductInPage + 1;
	                    	 }
	                     }
	                     return numberPage;
	                }else
	                {
	                	//String[] arrStrNumberProductInPage = strNumberProductInPage.split(regex);
	                	//String strNumberProductFrom = arrStrNumberProductInPage[0];
	                	String strTotalProductReplace = String.valueOf(totalProduct);
	                	if (!decimalFormatTotalProduct.isEmpty()){
	                		DecimalFormat formatter = new DecimalFormat(decimalFormatTotalProduct);
	                		strTotalProductReplace = formatter.format(totalProduct);
	                	}
	                	strNumberProductInPage = strNumberProductInPage.replace(strTotalProductReplace,"");
	                	int numberFrom = -1;
	                    String strSplitFrom = "0";
	                    for (i = regexFromIndexBegin ; i<=regexFromIndexEnd ; i++){
	                    	String ch = String.valueOf(strNumberProductInPage.charAt(i));
	                		if (tryParseIntByString(ch)){
	                			strSplitFrom += ch;
	                		}
	                    }
	                    numberFrom = Integer.parseInt(strSplitFrom);
	                    
	                   // String strNumberProductTo = arrStrNumberProductInPage[1];
	                    int numberTo = -1;
	                    String strSplitTo = "0";
	                    for (i = regexToIndexBegin ; i <= regexToIndexEnd; i++){
	                    	String ch = String.valueOf(strNumberProductInPage.charAt(i));
	                		if (tryParseIntByString(ch)){
	                			strSplitTo += ch;
	                		}
	                    }
	                    numberTo = Integer.parseInt(strSplitTo);
	                    int numberProductInPage = numberTo - numberFrom + 1;
	                    if (totalProduct > -1 && numberProductInPage > 0 ){
	                    	if ((totalProduct % numberProductInPage)==0){
	                    		numberPage= totalProduct / numberProductInPage;
		                   	 }else if (totalProduct > numberProductInPage){
		                   		numberPage= totalProduct / numberProductInPage + 1;
		                   	 }
	                    }
	                    return numberPage;
	                }
                }
                
            }else{
            	return -1;
            }
            
        }
        catch (SocketTimeoutException se){

            System.out.println("getContentOnly: SocketTimeoutException");
            System.out.println(se.getMessage());
            return -1;
        }

        catch (Exception e) {

            System.out.println("getContentOnly: Exception");
            e.printStackTrace();
            return -1;
        }
		
	}
	
	public static String hashHTML(String input){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] enc = md.digest();
            String md5Sum = new sun.misc.BASE64Encoder().encode(enc);
            return md5Sum;

        } catch (NoSuchAlgorithmException nsae) {

            System.out.println(nsae.getMessage());
            return null;
        }
       
       
    }
	
	public static void main(String[] args) throws Exception {
		logger.info("Start...: ");
		/*
		 * if (args.length != 2) { logger.info("Needed parameters: ");
		 * logger.info
		 * ("\t rootFolder (it will contain intermediate crawl data)");
		 * logger.info("\t numberOfCralwers (number of concurrent threads)");
		 * return; }
		 */
		/*
		 * crawlStorageFolder is a folder where intermediate crawl data is
		 * stored.
		 */
		String crawlStorageFolder = "D:\\/Java\\/storage";//"/crawler4j/storage";// args[0];

		/*
		 * numberOfCrawlers shows the number of concurrent threads that should
		 * be initiated for crawling.
		 */
		// int numberOfCrawlers = Integer.parseInt(args[1]);
		int numberOfCrawlers = 1;

		CrawlConfig config = new CrawlConfig();

		config.setCrawlStorageFolder(crawlStorageFolder);

		/*
		 * Be polite: Make sure that we don't send more than 1 request per
		 * second (1000 milliseconds between requests).
		 */
		config.setPolitenessDelay(1000);
		// config.setFollowRedirects(false);

		/*
		 * You can set the maximum crawl depth here. The default value is -1 for
		 * unlimited depth
		 */
		config.setMaxDepthOfCrawling(1);// ( use -1 for unlimited depth )

		/*
		 * You can set the maximum number of pages to crawl. The default value
		 * is -1 for unlimited number of pages
		 */
		config.setMaxPagesToFetch(-1);// ( use -1 for unlimited pages )

		/**
		 * Do you want crawler4j to crawl also binary data ? example: the
		 * contents of pdf, or the metadata of images etc
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
		config.setUserAgentString("Crawler");

		/*
		 * Instantiate the controller for this crawl.
		 */
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		// by me
		robotstxtConfig.setEnabled(false);
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig,
				pageFetcher);
		// by me
		CrawlController controller = new CrawlController(config, pageFetcher,
				robotstxtServer);

		/*
		 * For each crawl, you need to add some seed urls. These are the first
		 * URLs that are fetched and then the crawler starts following links
		 * which are found in these pages
		 */
		// controller.addSeed("http://careerbuilder.vn/");
		try {
			
			String tag_size = "site102";
			int sizeIDXML = -1;
			String provinceYESNO, linkCrawlerBegin, linkCrawlerPage;
			int pageNumberBegin = -1, pageNumberEnd =-1 , pageLoopInit = -1, pageLoop =-1;
			
			File fXmlFile = new File(pathXmlFile);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
		
			org.w3c.dom.NodeList nList = doc
					.getElementsByTagName(tag_size);
			org.w3c.dom.Node nNode = nList.item(0);
			if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
				org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
				sizeIDXML = Integer.parseInt(eElement.getAttribute("id"));
				String pageDefine = eElement.getElementsByTagName("pageDefine").item(0).getTextContent();
				// read config
				ReadConfigPageNumberEnd(eElement);
				
				// if define then get define value to using
				if (!pageDefine.isEmpty() && pageDefine.toUpperCase().equals("YES")) {		
					org.w3c.dom.NodeList nListOnewebsite = eElement
							.getElementsByTagName("website");
					org.w3c.dom.Element eElementOnewebsite = (org.w3c.dom.Element) nListOnewebsite
							.item(0);
					linkCrawlerBegin = eElementOnewebsite
							.getElementsByTagName("linkCrawlerBegin").item(0)
							.getTextContent();
					linkCrawlerPage = eElementOnewebsite
							.getElementsByTagName("linkCrawlerPage").item(0)
							.getTextContent();
					int pageNumberTotal = Integer.parseInt(eElementOnewebsite
							.getElementsByTagName("pageNumberTotal").item(0)
							.getTextContent());
					pageNumberBegin = Integer.parseInt(eElementOnewebsite
							.getElementsByTagName("pageNumberBegin").item(0)
							.getTextContent());
					pageLoopInit = Integer.parseInt(eElementOnewebsite
								.getElementsByTagName("pageLoopInit").item(0)
								.getTextContent());
					pageLoop = Integer.parseInt(eElementOnewebsite
								.getElementsByTagName("pageLoop").item(0)
								.getTextContent());
					
					if (!linkCrawlerBegin.isEmpty()){
						controller.addSeed(linkCrawlerBegin);
						//pageNumberEnd = getPageNumberEnd(linkCrawlerBegin);
						if (pageNumberTotal > 1){
							int i=0;
							for (; pageNumberTotal >= pageNumberBegin; pageNumberBegin++) {
								String convertlinkCrawlerPage = linkCrawlerPage.replace("%s", String.valueOf(pageLoopInit));
								controller.addSeed(convertlinkCrawlerPage);
								pageLoopInit += pageLoop;
								i++;
								System.out.println(i);
							}
						}
					}
				} else {				
					provinceYESNO = eElement.getElementsByTagName("provinceYESNO")
							.item(0).getTextContent();		
					
					if (!provinceYESNO.isEmpty()
							&& provinceYESNO.toUpperCase().equals("YES")) {
						// have sevent province
						org.w3c.dom.NodeList nListProvince = eElement
								.getElementsByTagName("province");
						for (int index = 0; index < nListProvince.getLength(); index++) {
							org.w3c.dom.Element eElementProvince = (org.w3c.dom.Element) nListProvince
									.item(index);
							linkCrawlerBegin = eElementProvince
									.getElementsByTagName("linkCrawlerBegin")
									.item(0).getTextContent();
							linkCrawlerPage = eElementProvince
									.getElementsByTagName("linkCrawlerPage")
									.item(0).getTextContent();
							if(!eElementProvince
									.getElementsByTagName("pageNumberBegin")
									.item(0).getTextContent().isEmpty()){
								pageNumberBegin = Integer.parseInt(eElementProvince
										.getElementsByTagName("pageNumberBegin")
										.item(0).getTextContent());
							}
							if (!eElementProvince
									.getElementsByTagName("pageLoopInit").item(0)
									.getTextContent().isEmpty()){
								pageLoopInit = Integer.parseInt(eElementProvince
										.getElementsByTagName("pageLoopInit").item(0)
										.getTextContent());
							}
							if(!eElementProvince
									.getElementsByTagName("pageLoop").item(0)
									.getTextContent().isEmpty()){
								pageLoop = Integer.parseInt(eElementProvince
										.getElementsByTagName("pageLoop").item(0)
										.getTextContent());
							}
							if (!linkCrawlerBegin.isEmpty()){
								controller.addSeed(linkCrawlerBegin);
								pageNumberEnd = getPageNumberEnd(linkCrawlerBegin);
								if (pageNumberEnd > 1){
									for (; pageNumberBegin <= pageNumberEnd; pageNumberBegin++) {
										String convertlinkCrawlerPage = linkCrawlerPage.replace("%s", String.valueOf(pageLoopInit));
										controller.addSeed(convertlinkCrawlerPage);
										pageLoopInit += pageLoop;
									}
								}
							}
	
						}
					} else if (!provinceYESNO.isEmpty()
							&& provinceYESNO.toUpperCase().equals("NO")) {
						// don't have sevent province
						org.w3c.dom.NodeList nListOnewebsite = eElement
								.getElementsByTagName("website");
						org.w3c.dom.Element eElementOnewebsite = (org.w3c.dom.Element) nListOnewebsite
								.item(0);
						// read config of pagenumber end
						linkCrawlerBegin = eElementOnewebsite
								.getElementsByTagName("linkCrawlerBegin").item(0)
								.getTextContent();
						linkCrawlerPage = eElementOnewebsite
								.getElementsByTagName("linkCrawlerPage").item(0)
								.getTextContent();
						if(!eElementOnewebsite
								.getElementsByTagName("pageNumberBegin")
								.item(0).getTextContent().isEmpty()){
							pageNumberBegin = Integer.parseInt(eElementOnewebsite
									.getElementsByTagName("pageNumberBegin")
									.item(0).getTextContent());
						}
						if (!eElementOnewebsite
								.getElementsByTagName("pageLoopInit").item(0)
								.getTextContent().isEmpty()){
							pageLoopInit = Integer.parseInt(eElementOnewebsite
									.getElementsByTagName("pageLoopInit").item(0)
									.getTextContent());
						}
						if(!eElementOnewebsite
								.getElementsByTagName("pageLoop").item(0)
								.getTextContent().isEmpty()){
							pageLoop = Integer.parseInt(eElementOnewebsite
									.getElementsByTagName("pageLoop").item(0)
									.getTextContent());
						}
						if (!linkCrawlerBegin.isEmpty()){
							controller.addSeed(linkCrawlerBegin);
							pageNumberEnd = getPageNumberEnd(linkCrawlerBegin);
							if (pageNumberEnd >1){
								for (; pageNumberBegin <= pageNumberEnd; pageNumberBegin++) {
									String convertlinkCrawlerPage = linkCrawlerPage.replace("%s", String.valueOf(pageLoopInit));
									controller.addSeed(convertlinkCrawlerPage);
									pageLoopInit += pageLoop;
								}
							}
						}
					}
				}
				
				CrawlSite.tag_size = tag_size;
				CrawlSite.siteIDXML = sizeIDXML;
				controller
						.start(CrawlSite.class, numberOfCrawlers);
					
			}
		} catch (Exception ex) {
			System.out.print("can't read config xml, review xml file !!");
			System.out.print(ex.getMessage());
		}
		
	}
}