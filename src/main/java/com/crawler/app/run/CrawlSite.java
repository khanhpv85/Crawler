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
import com.crawler.app.storage.DownloadImage;
import com.crawler.app.storage.MysqlCrawler;
import com.crawler.app.url.WebURL;
import com.crawler.app.common.StringUtils;
import com.sun.syndication.feed.atom.Link;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * @author Yasser Ganjisaffar [lastname at gmail dot com]
 */
public class CrawlSite extends WebCrawler {
	private Boolean status_read_xml = false;
	/* Job Img */
	private String jobImgUrl = "";
	private String jobImgQuery = "";
	private String jobImageFormatAttr = "";
	private int jobImagePosition = -1;
	private String JobImageSelectPosition = "";
	/* Job url */
	private String joburl_url = "";
	private String jobUrlQuery = "";
	private String jobUrlFormatAttr = "";
	private int jobUrlPosition = -1;
	private String JobUrlSelectPosition = "";
	/* Job Name */
	private String jobNameQuery = "";
	private String jobNameFormatData = "";
	private int jobNamePosition = -1;
	private String JobNameSelectPosition = "";
	/* Job Location */
	private String jobLocationQuery = "";
	private String jobLocationFormatData = "";
	private int jobLocationPosition = -1;
	private String JobLocationSelectPosition = "";
	/* Job Location Near */
	private String locationNearQuery = "";
	private String locationNearFormatData = "";
	private int locationNearPosition = -1;
	private String LocationNearSelectPosition = "";
	/* Job Salary */
	private String JobSalaryQuery = "";
	private String jobSalaryFormatData = "";
	private int jobSalaryPosition = -1;
	private String JobSalarySelectPosition = "";
	/* Job Description */
	private String JobDescriptionQuery = "";
	private String jobDescriptionFormatData = "";
	private int jobDescriptionPosition = -1;
	private String JobDescriptionSelectPosition = "";
	/* Job DetailShort */
	private String JobDetailShortQuery = "";
	private String jobDetailShortFormatData = "";
	private int jobDetailShortPosition = -1;
	private String JobDetailShortSelectPosition = "";
	/* Job Detail */
	private String JobDetailQuery = "";
	private String jobDetailFormatData = "";
	private int jobDetailPosition = -1;
	private String JobDetailSelectPosition = "";
	/* Job Detail Img */
	private String jobDetailImgUrl = "";
	private String jobDetailImgQuery = "";
	private String jobDetailImageFormatAttr = "";
	private int jobDetailImagePosition = -1;
	private String JobDetailImageSelectPosition = "";
	/* Job Expire */
	private String JobExpireQuery = "";
	private String jobExpireFormatData = "";
	private int jobExpirePosition = -1;
	private String JobExpireSelectPosition = "";
	/* Job Company */
	private String JobCompanyQuery = "";
	private String jobCompanyFormatData = "";
	private int jobCompanyPosition = -1;
	private String JobCompanySelectPosition = "";
	/* Job Type */
	private String JobTypeUrl = "";
	private String JobTypeQuery = "";
	private String jobTypeFormatAttr = "";
	private String jobTypeFormatData = "";
	private int jobTypePosition = -1;
	private String JobTypeSelectPosition = "";
	/* Job Address */
	private String JobAddressQuery = "";
	private String jobAddressFormatData = "";
	private int jobAddressPosition = -1;
	private String JobAddressSelectPosition = "";
	/* Job Career */
	private String JobCareerQuery = "";
	private String jobCareerFormatData = "";
	private int jobCareerPosition = -1;
	private String JobCareerSelectPosition = "";

	/* config database */
	private String host = "";
	private String port = "";
	private String dbName = "";
	private String dbUser = "";
	private String dbPwd = "";
	private String tagConnection = "databaseConnection";
	public static String tag_size = "";
	public static int siteIDXML = 0;
	
	/*query select body*/
	private String bodySelect = "";

	private final static Pattern BINARY_FILES_EXTENSIONS = Pattern
			.compile(".*\\.(bmp|gif|jpe?g|png|tiff?|pdf|ico|xaml|pict|rif|pptx?|ps"
					+ "|mid|mp2|mp3|mp4|wav|wma|au|aiff|flac|ogg|3gp|aac|amr|au|vox"
					+ "|avi|mov|mpe?g|ra?m|m4v|smil|wm?v|swf|aaf|asf|flv|mkv"
					+ "|zip|rar|gz|7z|aac|ace|alz|apk|arc|arj|dmg|jar|lzip|lha)"
					+ "(\\?.*)?$"); // For url Query parts ( URL?q=... )

	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */

	public Boolean readXmlConfigDatabase() {
		if (status_read_xml == false) {
			try {
				File fXmlFile = new File(CrawlSiteController.pathXmlFile);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
				org.w3c.dom.NodeList nList = doc
						.getElementsByTagName(tagConnection);
				org.w3c.dom.Node nNode = nList.item(0);
				if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
					host = eElement.getElementsByTagName("host").item(0)
							.getTextContent();
					port = eElement.getElementsByTagName("port").item(0)
							.getTextContent();
					dbName = eElement.getElementsByTagName("dbName").item(0)
							.getTextContent();
					dbUser = eElement.getElementsByTagName("dbUser").item(0)
							.getTextContent();
					dbPwd = eElement.getElementsByTagName("dbPassword").item(0)
							.getTextContent();
				}
				return true;

			} catch (Exception e) {
				e.printStackTrace();
				System.out.print("can not load xml file");
				return false;
			}
		}
		return true;
	}

	public Boolean ReadXmlConfig() {
		if (status_read_xml == false) {
			try {
				File fXmlFile = new File(CrawlSiteController.pathXmlFile);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
				org.w3c.dom.NodeList nList = doc
						.getElementsByTagName(this.tag_size);
				org.w3c.dom.Node nNode = nList.item(0);
				if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
					/* body select */
					bodySelect = eElement.getElementsByTagName("bodySelect")
							.item(0).getTextContent();
					/* Job Img */
					jobImgUrl = eElement.getElementsByTagName("JobImage_Url")
							.item(0).getTextContent();
					jobImgQuery = eElement
							.getElementsByTagName("JobImageSelect").item(0)
							.getTextContent();
					jobImageFormatAttr = eElement
							.getElementsByTagName("JobImageFormatAttr").item(0)
							.getTextContent();
					if (!eElement.getElementsByTagName("JobImagePosition")
							.item(0).getTextContent().isEmpty()) {
						jobImagePosition = Integer.parseInt(eElement
								.getElementsByTagName("JobImagePosition")
								.item(0).getTextContent());
					}
					JobImageSelectPosition = eElement
							.getElementsByTagName("JobImageSelectPosition")
							.item(0).getTextContent();
					/* Job url */
					joburl_url = eElement.getElementsByTagName("JobUrl_Url")
							.item(0).getTextContent();
					jobUrlQuery = eElement.getElementsByTagName("JobUrlSelect")
							.item(0).getTextContent();
					jobUrlFormatAttr = eElement
							.getElementsByTagName("JobUrlFormatAttr").item(0)
							.getTextContent();
					if (!eElement.getElementsByTagName("JobUrlPosition")
							.item(0).getTextContent().isEmpty()) {
						jobUrlPosition = Integer.parseInt(eElement
								.getElementsByTagName("JobUrlPosition").item(0)
								.getTextContent());
					}
					JobUrlSelectPosition = eElement
							.getElementsByTagName("JobUrlSelectPosition")
							.item(0).getTextContent();
					/* Job name */
					jobNameQuery = eElement
							.getElementsByTagName("JobNameSelect").item(0)
							.getTextContent();
					jobNameFormatData = eElement
							.getElementsByTagName("JobNameFormatData").item(0)
							.getTextContent();
					if (!eElement.getElementsByTagName("JobNamePosition")
							.item(0).getTextContent().isEmpty()) {
						jobNamePosition = Integer.parseInt(eElement
								.getElementsByTagName("JobNamePosition")
								.item(0).getTextContent());
					}
					JobNameSelectPosition = eElement
							.getElementsByTagName("JobNameSelectPosition")
							.item(0).getTextContent();
					/* Job location */
					jobLocationQuery = eElement
							.getElementsByTagName("JobLocationSelect").item(0)
							.getTextContent();
					jobLocationFormatData = eElement
							.getElementsByTagName("JobLocationFormatData")
							.item(0).getTextContent();
					if (!eElement.getElementsByTagName("JobLocationPosition")
							.item(0).getTextContent().isEmpty()) {
						jobLocationPosition = Integer.parseInt(eElement
								.getElementsByTagName("JobLocationPosition")
								.item(0).getTextContent());
					}
					JobLocationSelectPosition = eElement
							.getElementsByTagName("JobLocationSelectPosition")
							.item(0).getTextContent();
					/* Job location near */
					locationNearQuery = eElement
							.getElementsByTagName("LocationNearSelect").item(0)
							.getTextContent();
					locationNearFormatData = eElement
							.getElementsByTagName("LocationNearFormatData")
							.item(0).getTextContent();
					if (!eElement.getElementsByTagName("LocationNearPosition")
							.item(0).getTextContent().isEmpty()) {
						locationNearPosition = Integer.parseInt(eElement
								.getElementsByTagName("LocationNearPosition")
								.item(0).getTextContent());
					}
					LocationNearSelectPosition = eElement
							.getElementsByTagName("LocationNearSelectPosition")
							.item(0).getTextContent();
					/* Job Salary */
					JobSalaryQuery = eElement
							.getElementsByTagName("JobSalarySelect").item(0)
							.getTextContent();
					jobSalaryFormatData = eElement
							.getElementsByTagName("JobSalaryFormatData")
							.item(0).getTextContent();
					if (!eElement.getElementsByTagName("JobSalaryPosition")
							.item(0).getTextContent().isEmpty()) {
						jobSalaryPosition = Integer.parseInt(eElement
								.getElementsByTagName("JobSalaryPosition")
								.item(0).getTextContent());
					}
					JobSalarySelectPosition = eElement
							.getElementsByTagName("JobSalarySelectPosition")
							.item(0).getTextContent();
					/* Job Description */
					JobDescriptionQuery = eElement
							.getElementsByTagName("JobDescriptionSelect").item(0)
							.getTextContent();
					jobDescriptionFormatData = eElement
							.getElementsByTagName("JobDescriptionFormatData")
							.item(0).getTextContent();
					if (!eElement.getElementsByTagName("JobDescriptionPosition")
							.item(0).getTextContent().isEmpty()) {
						jobDescriptionPosition = Integer.parseInt(eElement
								.getElementsByTagName("JobDescriptionPosition")
								.item(0).getTextContent());
					}
					JobDescriptionSelectPosition = eElement
							.getElementsByTagName("JobDescriptionSelectPosition")
							.item(0).getTextContent();
					/* Job DetailShort */
					JobDetailShortQuery = eElement
							.getElementsByTagName("JobDetailShortSelect").item(0)
							.getTextContent();
					jobDetailShortFormatData = eElement
							.getElementsByTagName("JobDetailShortFormatData")
							.item(0).getTextContent();
					if (!eElement.getElementsByTagName("JobDetailShortPosition")
							.item(0).getTextContent().isEmpty()) {
						jobDetailShortPosition = Integer.parseInt(eElement
								.getElementsByTagName("JobDetailShortPosition")
								.item(0).getTextContent());
					}
					JobDetailShortSelectPosition = eElement
							.getElementsByTagName("JobDetailShortSelectPosition")
							.item(0).getTextContent();
					/* Job Detail */
					JobDetailQuery = eElement
							.getElementsByTagName("JobDetailSelect").item(0)
							.getTextContent();
					jobDetailFormatData = eElement
							.getElementsByTagName("JobDetailFormatData")
							.item(0).getTextContent();
					if (!eElement.getElementsByTagName("JobDetailPosition")
							.item(0).getTextContent().isEmpty()) {
						jobDetailPosition = Integer.parseInt(eElement
								.getElementsByTagName("JobDetailPosition")
								.item(0).getTextContent());
					}
					JobDetailSelectPosition = eElement
							.getElementsByTagName("JobDetailSelectPosition")
							.item(0).getTextContent();
					/* Job Detail Img */
					jobDetailImgUrl = eElement.getElementsByTagName("JobDetailImage_Url")
							.item(0).getTextContent();
					jobDetailImgQuery = eElement
							.getElementsByTagName("JobDetailImageSelect").item(0)
							.getTextContent();
					jobDetailImageFormatAttr = eElement
							.getElementsByTagName("JobDetailImageFormatAttr").item(0)
							.getTextContent();
					if (!eElement.getElementsByTagName("JobDetailImagePosition")
							.item(0).getTextContent().isEmpty()) {
						jobDetailImagePosition = Integer.parseInt(eElement
								.getElementsByTagName("JobDetailImagePosition")
								.item(0).getTextContent());
					}
					JobDetailImageSelectPosition = eElement
							.getElementsByTagName("JobDetailImageSelectPosition")
							.item(0).getTextContent();
					/* Job Expire */
					JobExpireQuery = eElement
							.getElementsByTagName("JobExpireSelect").item(0)
							.getTextContent();
					jobExpireFormatData = eElement
							.getElementsByTagName("JobExpireFormatData")
							.item(0).getTextContent();
					if (!eElement.getElementsByTagName("JobExpirePosition")
							.item(0).getTextContent().isEmpty()) {
						jobExpirePosition = Integer.parseInt(eElement
								.getElementsByTagName("JobExpirePosition")
								.item(0).getTextContent());
					}
					JobExpireSelectPosition = eElement
							.getElementsByTagName("JobExpireSelectPosition")
							.item(0).getTextContent();
					/* Job Company */
					JobCompanyQuery = eElement
							.getElementsByTagName("JobCompanySelect").item(0)
							.getTextContent();
					jobCompanyFormatData = eElement
							.getElementsByTagName("JobCompanyFormatData")
							.item(0).getTextContent();
					if (!eElement.getElementsByTagName("JobCompanyPosition")
							.item(0).getTextContent().isEmpty()) {
						jobCompanyPosition = Integer.parseInt(eElement
								.getElementsByTagName("JobCompanyPosition")
								.item(0).getTextContent());
					}
					JobCompanySelectPosition = eElement
							.getElementsByTagName("JobCompanySelectPosition")
							.item(0).getTextContent();
					/* Job type */
					JobTypeUrl = eElement.getElementsByTagName("JobType_Url")
							.item(0).getTextContent();
					JobTypeQuery = eElement
							.getElementsByTagName("JobTypeSelect").item(0)
							.getTextContent();
					jobTypeFormatAttr = eElement
							.getElementsByTagName("JobTypeFormatAttr").item(0)
							.getTextContent();
					jobTypeFormatData = eElement
							.getElementsByTagName("JobTypeFormatData").item(0)
							.getTextContent();
					if (!eElement.getElementsByTagName("JobTypePosition")
							.item(0).getTextContent().isEmpty()) {
						jobTypePosition = Integer.parseInt(eElement
								.getElementsByTagName("JobTypePosition")
								.item(0).getTextContent());
					}
					JobTypeSelectPosition = eElement
							.getElementsByTagName("JobTypeSelectPosition")
							.item(0).getTextContent();
					/* Job Address */
					JobAddressQuery = eElement
							.getElementsByTagName("JobAddressSelect").item(0)
							.getTextContent();
					jobAddressFormatData = eElement
							.getElementsByTagName("JobAddressFormatData")
							.item(0).getTextContent();
					if (!eElement.getElementsByTagName("JobAddressPosition")
							.item(0).getTextContent().isEmpty()) {
						jobAddressPosition = Integer.parseInt(eElement
								.getElementsByTagName("JobAddressPosition")
								.item(0).getTextContent());
					}
					JobAddressSelectPosition = eElement
							.getElementsByTagName("JobAddressSelectPosition")
							.item(0).getTextContent();
					/* Job Career */
					JobCareerQuery = eElement
							.getElementsByTagName("JobCareerSelect").item(0)
							.getTextContent();
					jobCareerFormatData = eElement
							.getElementsByTagName("JobCareerFormatData")
							.item(0).getTextContent();
					if (!eElement.getElementsByTagName("JobCareerPosition")
							.item(0).getTextContent().isEmpty()) {
						jobCareerPosition = Integer.parseInt(eElement
								.getElementsByTagName("JobCareerPosition")
								.item(0).getTextContent());
					}
					JobCareerSelectPosition = eElement
							.getElementsByTagName("JobCareerSelectPosition")
							.item(0).getTextContent();
				}
				return true;

			} catch (Exception e) {
				e.printStackTrace();
				System.out.print("can not load xml file");
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean shouldVisit(Page page, WebURL url) {
		return false;
		// String href = url.getURL().toLowerCase();

		// return !BINARY_FILES_EXTENSIONS.matcher(href).matches() &&
		// href.startsWith("http://careerbuilder.vn/vi") && href.endsWith("jd");
		// return !BINARY_FILES_EXTENSIONS.matcher(href).matches() &&
		// href.startsWith("http://careerbuilder.vn/vi") &&
		// href.endsWith(".html");
		// return !BINARY_FILES_EXTENSIONS.matcher(href).matches() &&
		// href.startsWith("http://jobsearch.living.jp/kyujin/");
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	
	public org.jsoup.nodes.Element convertUrlToDocument(String url){
		try{
			
			Connection.Response response =

					Jsoup.connect(url)
                //enable for error urls
                .ignoreHttpErrors(true)
                //MAXIMUN TIME
                .timeout(50000)
                //This is to prevent producing garbage by attempting to parse a JPEG binary image
                .ignoreContentType(true)
                .execute();

        int status = response.statusCode();
        //after done
        if(status == 200){
            org.jsoup.nodes.Document doc = response.parse();
            Element body = doc.body();
            return body;
         }else{
        	 return null;
         }
		}catch (SocketTimeoutException se){

            System.out.println("getContentOnly: SocketTimeoutException");
            System.out.println(se.getMessage());
            return null;
        }

        catch (Exception e) {

            System.out.println("getContentOnly: Exception");
            e.printStackTrace();
            return null;
        }
	}
	
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		// logger.info("URL: ", url);
		if (ReadXmlConfig() && readXmlConfigDatabase()) {
			status_read_xml = true;
		} else {
			return;
		}
		
		System.out.println("\n URL visit: " + url);

		if (page.getParseData() instanceof HtmlParseData) {
			
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			String title = htmlParseData.getTitle();
			Document doc = Jsoup.parse(html, "UTF-8");
			Element body = doc.body();
			Elements listDetail = body.select(bodySelect);
			Integer i = 0;
			Integer siteID = siteIDXML;
			Integer provinceID = 1;
			MysqlCrawler.createConn(host, port, dbName, dbUser, dbPwd);
			String jobImage, jobUrl, aJobName, cJobLocation = null, cLocationNear = "", bJobCompany = "", dJobCareer, eJobSalary, gJobDescription, gJobDetailShort, gJobDetail, jobDetailImage, jobDetailImageName, hJobExpire = null;
			for (Element detail : listDetail) {
				i++;
				try {
					jobImage = "";
					/* job img */
					
					if (!jobImgQuery.isEmpty()) {
						if (jobImagePosition > -1) {
							if (jobImagePosition < detail.select(jobImgQuery).size()){
								if (!detail.select(jobImgQuery)
										.get(jobImagePosition)
										.attr(jobImageFormatAttr).isEmpty()) {
									if (!jobImgUrl.isEmpty()) {
										if (JobImageSelectPosition.isEmpty()){
											jobImage = jobImgUrl
													+ detail.select(jobImgQuery)
															.get(jobImagePosition)
															.attr(jobImageFormatAttr);
										}else
										{
											jobImage = jobImgUrl
													+ detail.select(jobImgQuery)
															.get(jobImagePosition).select(JobImageSelectPosition)
															.attr(jobImageFormatAttr);
										}
									} else {
										if (JobImageSelectPosition.isEmpty()){
											jobImage = detail.select(jobImgQuery)
													.get(jobImagePosition)
													.attr(jobImageFormatAttr);
										}else{
											jobImage = detail.select(jobImgQuery)
													.get(jobImagePosition).select(JobImageSelectPosition)
													.attr(jobImageFormatAttr);
										}
									}
								}
							}
						} else {
							if (!detail.select(jobImgQuery)
									.attr(jobImageFormatAttr).isEmpty()) {
								if (!jobImgUrl.isEmpty()) {
									jobImage = jobImgUrl
											+ detail.select(jobImgQuery)
													.first()
													.attr(jobImageFormatAttr);
								} else {
									jobImage = detail.select(jobImgQuery)
											.first().attr(jobImageFormatAttr);
								}
							}
						}
					}
					/* job url */
					jobUrl = "";					
					if (!jobUrlQuery.isEmpty()) {
						if (jobUrlPosition > -1) {
							if(jobUrlPosition < detail.select(jobUrlQuery).size()){
								if (!joburl_url.isEmpty()) {
									if (JobUrlSelectPosition.isEmpty()){
										jobUrl = joburl_url
												+ detail.select(jobUrlQuery)
														.get(jobUrlPosition)
														.attr(jobUrlFormatAttr);
									}else{
										jobUrl = joburl_url
												+ detail.select(jobUrlQuery)
														.get(jobUrlPosition).select(JobUrlSelectPosition)
														.attr(jobUrlFormatAttr);
									}
								} else {
									if (JobUrlSelectPosition.isEmpty()){
										jobUrl = detail.select(jobUrlQuery)
												.get(jobUrlPosition)
												.attr(jobUrlFormatAttr);
									}else{
										jobUrl = detail.select(jobUrlQuery)
												.get(jobUrlPosition).select(JobUrlSelectPosition)
												.attr(jobUrlFormatAttr);
									}
								}
							}
						} else {
							if (!joburl_url.isEmpty()) {
								jobUrl = joburl_url
										+ detail.select(jobUrlQuery).first()
												.attr(jobUrlFormatAttr);
							} else {
								jobUrl = detail.select(jobUrlQuery).first()
										.attr(jobUrlFormatAttr);
							}
						}
					}
					
					
					// change
					org.jsoup.nodes.Element detailJobUrl= convertUrlToDocument(jobUrl);
					//System.out.print(detailJobUrl);
					//System.exit(1);
					/* job location */
					if (!jobLocationQuery.isEmpty()) {
							if (jobLocationFormatData.toUpperCase().equals(
									"TEXT")) {
								cJobLocation = detailJobUrl.select(jobLocationQuery)
										.text();
							} else if (jobLocationFormatData.toUpperCase()
									.equals("HTML")) {
								cJobLocation = detailJobUrl.select(jobLocationQuery)
										.html();
							}
					}
					
					/* job name */
					aJobName = "";
					if (jobNameFormatData.toUpperCase().equals("TEXT")) {
						aJobName = detailJobUrl.select(jobNameQuery).text();
					} else if (jobNameFormatData.toUpperCase().equals(
							"HTML")) {
						aJobName = detailJobUrl.select(jobNameQuery).html();
					}

					/* job description */
					gJobDescription = "";
					if (!JobDescriptionQuery.isEmpty()) {
							if (jobDescriptionFormatData.toUpperCase()
									.equals("TEXT")) {
								gJobDescription = detailJobUrl.select(JobDescriptionQuery)
										.text();
							} else if (jobDescriptionFormatData.toUpperCase()
									.equals("HTML")) {
								gJobDescription = detailJobUrl.select(JobDescriptionQuery)
										.html();
							}
					}
					/* job detail short */
					gJobDetailShort = "";
					if (!JobDetailShortQuery.isEmpty()) {
							if (jobDetailShortFormatData.toUpperCase()
									.equals("TEXT")) {
								gJobDetailShort = detailJobUrl.select(JobDetailShortQuery)
										.text();
							} else if (jobDetailShortFormatData.toUpperCase()
									.equals("HTML")) {
								gJobDetailShort = detailJobUrl.select(JobDetailShortQuery)
										.html();
							}
					}
					/* job detail */
					gJobDetail = "";
					if (!JobDetailQuery.isEmpty()) {
							if (jobDetailFormatData.toUpperCase()
									.equals("TEXT")) {
								gJobDetail = detailJobUrl.select(JobDetailQuery)
										.text();
							} else if (jobDetailFormatData.toUpperCase()
									.equals("HTML")) {
								gJobDetail = detailJobUrl.select(JobDetailQuery)
										.html();
							}
					}
					/* job detail img*/		
					jobDetailImage = ""; jobDetailImageName = "";
					if (!jobDetailImgQuery.isEmpty()) {
						if (jobDetailImagePosition > -1) {
							if (jobDetailImagePosition < detailJobUrl.select(jobDetailImgQuery).size()){
								if (!detailJobUrl.select(jobDetailImgQuery)
										.get(jobDetailImagePosition)
										.attr(jobDetailImageFormatAttr).isEmpty()) {
									if (!jobDetailImgUrl.isEmpty()) {
										if (JobDetailImageSelectPosition.isEmpty()){
											jobDetailImage = jobDetailImgUrl
													+ detailJobUrl.select(jobDetailImgQuery)
															.get(jobDetailImagePosition)
															.attr(jobDetailImageFormatAttr);
										}else
										{
											jobDetailImage = jobDetailImgUrl
													+ detailJobUrl.select(jobDetailImgQuery)
															.get(jobDetailImagePosition).select(JobDetailImageSelectPosition)
															.attr(jobDetailImageFormatAttr);
										}
									} else {
										if (JobDetailImageSelectPosition.isEmpty()){
											jobDetailImage = detailJobUrl.select(jobDetailImgQuery)
													.get(jobDetailImagePosition)
													.attr(jobDetailImageFormatAttr);
										}else{
											jobDetailImage = detailJobUrl.select(jobDetailImgQuery)
													.get(jobDetailImagePosition).select(JobDetailImageSelectPosition)
													.attr(jobDetailImageFormatAttr);
										}
									}
								}
							}
						} else {
							if (!detailJobUrl.select(jobDetailImgQuery)
									.attr(jobDetailImageFormatAttr).isEmpty()) {
								if (!jobDetailImgUrl.isEmpty()) {
									jobDetailImage = jobDetailImgUrl
											+ detailJobUrl.select(jobDetailImgQuery)
													.first()
													.attr(jobDetailImageFormatAttr);
								} else {
									jobDetailImage = detailJobUrl.select(jobDetailImgQuery)
											.first().attr(jobDetailImageFormatAttr);
								}
							}
						}						
						if (!jobDetailImage.isEmpty()) {
							jobDetailImageName = DownloadImage.downloadImage(jobDetailImage, "D:\\/Java\\/storage");
						}
					}
					/* job location near */
					cLocationNear = "";
					if (!locationNearQuery.isEmpty()) {
							if (locationNearFormatData.toUpperCase().equals(
									"TEXT")) {
								cLocationNear = detailJobUrl
										.select(locationNearQuery).text();
							} else if (locationNearFormatData.toUpperCase()
									.equals("HTML")) {
								cLocationNear = detailJobUrl
										.select(locationNearQuery).html();
							}
					}
					/* job salary */
					eJobSalary = "";
					if (!JobSalaryQuery.isEmpty()) {
							if (jobSalaryFormatData.toUpperCase()
									.equals("TEXT")) {
								eJobSalary = detailJobUrl.select(JobSalaryQuery)
										.text();
							} else if (jobSalaryFormatData.toUpperCase()
									.equals("HTML")) {
								eJobSalary = detailJobUrl.select(JobSalaryQuery)
										.html();
							}
					}
					
					/* job expire */
					hJobExpire = "";
					if (!JobExpireQuery.isEmpty()) {
							if (jobExpireFormatData.toUpperCase()
									.equals("TEXT")) {
								hJobExpire = detailJobUrl.select(JobExpireQuery)
										.text();
							} else if (jobExpireFormatData.toUpperCase()
									.equals("HTML")) {
								hJobExpire = detailJobUrl.select(JobExpireQuery)
										.html();
							}
					}
					/* job company */
					bJobCompany = "";
					if (!JobCompanyQuery.isEmpty()) {
							if (jobCompanyFormatData.toUpperCase().equals(
									"TEXT")) {
								bJobCompany = detailJobUrl.select(JobCompanyQuery)
										.text();
							} else if (jobCompanyFormatData.toUpperCase()
									.equals("HTML")) {
								bJobCompany = detailJobUrl.select(JobCompanyQuery)
										.html();
							}
					}
					/* job type */
					String fJobType = "";
					if (!JobTypeQuery.isEmpty()) {
								if (jobTypeFormatData.toUpperCase().equals(
										"TEXT")) {
									fJobType = detailJobUrl.select(JobTypeQuery)
											.text();
								} else if (jobTypeFormatData.toUpperCase()
										.equals("HTML")) {
									fJobType = detailJobUrl.select(JobTypeQuery)
											.html();
								}
					}
					/* job address */
					String jobAddress = "";
					if (!JobAddressQuery.isEmpty()) {
							if (jobAddressFormatData.toUpperCase().equals(
									"TEXT")) {
								jobAddress = detailJobUrl.select(JobAddressQuery)
										.text();
							} else if (jobAddressFormatData.toUpperCase()
									.equals("HTML")) {
								jobAddress = detailJobUrl.select(JobAddressQuery)
										.html();
							}
					}
					dJobCareer = "";
					if (!JobCareerQuery.isEmpty()) {
							if (jobCareerFormatData.toUpperCase()
									.equals("TEXT")) {
								dJobCareer = detailJobUrl.select(JobCareerQuery)
										.text();
							} else if (jobCareerFormatData.toUpperCase()
									.equals("HTML")) {
								dJobCareer = detailJobUrl.select(JobCareerQuery)
										.html();
							}
					}

					System.out.println("\n Url : " + jobUrl);
					System.out.println("\n Image : " + jobImage);
					System.out.println("\n Title : " + aJobName);
					System.out.println("\n Title SEO : " + StringUtils.removeAccent(aJobName));
					//System.out.println("\n Location : " + cJobLocation + "\n"
					// + cLocationNear);
					System.out.println("\n jobDetailImageName : " + jobDetailImageName);
					// System.out.println("\n Detail : " + gJobDetail);
					// System.out.println("\n Salary : " + eJobSalary);
					// System.out.println("\n expire Date : " + hJobExpire);
					// System.out.println("\n Company : " + bJobCompany);
					// System.out.println("\n JobType : " + fJobType);
					//
					System.out.println("\n Full I : " + i);
					String news_title = aJobName;
					String news_title_seo = StringUtils.removeAccent(aJobName);
					String news_meta = aJobName;
					String news_description = gJobDescription;
					String news_tag = aJobName.replace(" ", ", ");
					String news_pic = jobDetailImageName;
					String pic_note = aJobName;
					String news_subcontent = "<p>"+gJobDescription+"</p>";
					String news_content = gJobDetailShort + "<p><img src='http://"+jobDetailImageName+"'></p>" + gJobDetail;
					int type = 4;
					int status = 0;
					int kind = 0;
					String source = "Theo http://monngonmoingay.com";
					String author = null;
					int user_posted = 0;
					int user_activated = 0;
					int cate_id = 43;
					String list_productid_relation = "13,28,30";
					
					if (!MysqlCrawler.getInstance().checkNewsUrl(news_title_seo)){
						MysqlCrawler.getInstance().insertNewsContent(
							news_title, 
							news_title_seo, 
							news_meta, 
							news_description, 
							news_tag,
							news_pic, 
							pic_note, 
							news_subcontent, 
							news_content,
							type, 
							status, 
							kind, 
							source, 
							author, 
							user_posted,
							user_activated, 
							cate_id, 
							list_productid_relation
							);
					}
					
					// System.exit(1);
				} catch (Exception ex) {
					System.out.println("\n Fail I : " + i);
					System.out.println("\n Ex : " + ex);
				}
			}			

		}

		/*
		 * Header[] responseHeaders = page.getFetchResponseHeaders(); if
		 * (responseHeaders != null) { logger.debug("Response headers:"); for
		 * (Header header : responseHeaders) { logger.debug("\t{}: {}",
		 * header.getName(), header.getValue()); } }
		 */
		logger.debug("=============");
	}
}