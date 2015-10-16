package com.crawler.app.storage;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.crawler.app.sql.DbMySql;

public class MysqlCrawler {

	public  String host = "127.0.0.1";
	public  String port = "3306";
	public  String dbName = "crawler";
	public  String dbUser = "root";
	public  String dbPwd = "123456";
	DbMySql mysql =null;
	
	private static MysqlCrawler instance =null;
	
	public static MysqlCrawler getInstance() {
        if (instance == null) {
            instance = new MysqlCrawler();
        }
        return instance;
    }
	
	public MysqlCrawler(String host, String port,String dbName,String dbUser,String dbPwd){
		this.host = host;
		this.port = port;
		this.dbName = dbName;
		this.dbUser = dbUser;
		this.dbPwd = dbPwd;
		mysql = new DbMySql(host, port, dbName, dbUser, dbPwd);
	}
	
	public MysqlCrawler(){
		
	}
	
	public static MysqlCrawler createConn(String host, String port,String dbName,String dbUser,String dbPwd) {
        if (instance == null) {
            instance = new MysqlCrawler (host,port,dbName,dbUser,dbPwd);
        }
        return instance;
    }
	
	public Boolean checkJobUrlHRContents(String jobUrl) {
		String sql = "select * from jfhr_contents_vietnamwork where job_url like '" + jobUrl
				+ "'";
		try {
			List<Map<String, Object>> result = mysql
					.executeStatement(sql, null);
			if (result.size() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return true;
		}

	}
	
	public Boolean checkNewsUrl(String jobUrl) {
		String sql = "select * from news where news_title_seo like '" + jobUrl + "'";
		System.out.println(sql);
		try {
			List<Map<String, Object>> result = mysql.executeStatement(sql, null);
			if (result.size() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return true;
		}

	}
	
	public Boolean insertNewsContent(
			String news_title, 
			String news_title_seo, 
			String news_meta, 
			String news_description, 
			String news_tag,
			String news_pic, 
			String pic_note, 
			String news_subcontent, 
			String news_content,
			int type, 
			int status, 
			int kind, 
			String source, 
			String author, 
			int user_posted,
			int user_activated, 
			int cate_id, 
			String list_productid_relation
			){
		Date date = new Date();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("news_title", news_title);
		params.put("news_title_seo", news_title_seo);
		params.put("news_meta", news_meta);
		params.put("news_description", news_description);
		params.put("news_tag", news_tag);
		params.put("news_pic", news_pic);
		params.put("pic_note", pic_note);
		params.put("news_subcontent", news_subcontent);
		params.put("news_content", news_content);
		params.put("type", type);
		params.put("status", status);
		params.put("kind", kind);
		params.put("postdate", date);
		params.put("activedate", date);
		params.put("source", source);
		params.put("author", author);
		params.put("user_posted", user_posted);
		params.put("user_activated", user_activated);
		params.put("cate_id", cate_id);
		params.put("list_productid_relation", list_productid_relation);

		String sqlUpdate = "" +
				"insert into " +
				"news(" +
				"		news_title," +
				"		news_title_seo," +
				"		news_meta," +
				"		news_description," +
				"		news_tag," +
				"		news_pic," +
				"		pic_note," +
				"		news_subcontent," +
				"		news_content," +
				"		type," +
				"		status," +
				"		kind," +
				"		source," +
				"		author," +
				"		user_posted," +
				"		user_activated," +
				"		cate_id," +
				"		list_productid_relation" +
				"	) " +
				"	values" +
				"	(" +
				"		${news_title}," +
				"		${news_title_seo}," +
				"		${news_meta}," +
				"		${news_description}," +
				"		${news_tag}," +
				"		${news_pic}," +
				"		${pic_note}," +
				"		${news_subcontent}," +
				"		${news_content}," +
				"		${type}," +
				"		${status}," +
				"		${kind}," +
				"		${source}," +
				"		${author}," +
				"		${user_posted}," +
				"		${user_activated}," +
				"		${cate_id}," +
				"		${list_productid_relation}" +
				"	)";
		
		//System.out.println("sql" + sqlUpdate);
		
		try{
			return mysql.executeUpdate(sqlUpdate, params);
		}catch(Exception ex){
			ex.printStackTrace(System.out);	
		}
		return false;
	}
	
	public Boolean insertURL(String url, String title, String content){
		Date date = new Date();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("url", url);
		params.put("title", title);
		params.put("content", content);
		params.put("date", date);

		String sqlUpdate = "" +
				"insert into " +
				"urls(" +
				"		url," +
				"		title," +
				"		content" +
				"	) " +
				"	values" +
				"	(" +
				"		${url}," +
				"		${title}," +
				"		${content}" +
				"	)";
		
		//System.out.println("sql" + sqlUpdate);
		
		try{
			return mysql.executeUpdate(sqlUpdate, params);
		}catch(Exception ex){
			ex.printStackTrace(System.out);	
		}
		return false;
	}
	
	public Boolean insertContents(Integer siteID, Integer provinceID, String jobUrl, String jobName, String jobDetail
								, String jobCompany, String jobCareer, String jobSalary
								, String jobExpire, String jobLocation, String jobAddress
								, String jobImage, String jobType){
		Date date = new Date();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("site_id", siteID);
		params.put("province_id", provinceID);
		params.put("job_url", jobUrl);
		params.put("job_name", jobName);
		params.put("job_detail", jobDetail);
		params.put("job_company", jobCompany);
		params.put("job_career", jobCareer);
		params.put("job_salary", jobSalary);
		params.put("job_expire", jobExpire);
		params.put("job_location", jobLocation);
		params.put("job_address", jobAddress);
		params.put("job_image", jobImage);
		params.put("job_type", jobType);
		params.put("created", date);

		String sqlUpdate = "" +
				"insert into " +
				"jfhr_contents(" +
				"		site_id," +
				"		province_id," +
				"		job_url," +
				"		job_name," +
				"		job_detail," +
				"		job_company," +
				"		job_career," +
				"		job_salary," +
				"		job_expire," +
				"		job_location," +
				"		job_address," +
				"		job_image," +
				"		job_type," +
				"		created" +
				"	) " +
				"	values" +
				"	(" +
				"		${site_id}," +
				"		${province_id}," +
				"		${job_url}," +
				"		${job_name}," +
				"		${job_detail}," +
				"		${job_company}," +
				"		${job_career}," +
				"		${job_salary}," +
				"		${job_expire}," +
				"		${job_location}," +
				"		${job_address}," +
				"		${job_image}," +
				"		${job_type}," +
				"		${created}" +
				"	)";
		
		//System.out.println("sql" + sqlUpdate);
		
		try{
			return mysql.executeUpdate(sqlUpdate, params);
		}catch(Exception ex){
			ex.printStackTrace(System.out);	
		}
		return false;
	}
	
	public Boolean insertJFHRContents(Integer siteID
			, String jobUrl
			, String jobName			
			, String jobLocation
			, String companyName
			, String companyAddress
			, String companyPhone
			, String companyContact
			, String companyWebsite){
			Date date = new Date();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("site_id", siteID);
			params.put("job_url", jobUrl);
			params.put("job_name", jobName);
			params.put("job_location", jobLocation);
			params.put("company_name", companyName);			
			params.put("company_address", companyAddress);
			params.put("company_phone", companyPhone);
			params.put("company_contact", companyContact);
			params.put("company_website", companyWebsite);
			params.put("created", date);
			
			String sqlUpdate = "" +
			"insert into " +
			"jfhr_contents(" +
			"		site_id," +
			"		job_url," +
			"		job_name," +
			"		job_location," +
			"		company_name," +		
			"		company_address," +
			"		company_phone," +
			"		company_contact," +
			"		company_website," +
			"		created" +
			"	) " +
			"	values" +
			"	(" +
			"		${site_id}," +
			"		${job_url}," +
			"		${job_name}," +
			"		${job_location}," +
			"		${company_name}," +
			"		${company_address}," +
			"		${company_phone}," +
			"		${company_contact}," +
			"		${company_website}," +
			"		${created}" +
			"	)";
			
			//System.out.println("sql" + sqlUpdate);
			
			try{
			return mysql.executeUpdate(sqlUpdate, params);
			}catch(Exception ex){
			ex.printStackTrace(System.out);	
			}
			return false;
			}
	
	public boolean insertDetail(
			Date calcDate,
			String gameCode, 
			String idClassification, 
			String timing, 
			String timeValue, 
			long totalAccount, 
			long totalAccountAllClassification, 
			float totalBehavior, 
			float totalBehaviorAllClassification, 
			String status) {
		
		Date date = new Date();
		Map<String, Object> params = new HashMap<String, Object>();
		
//		| IdBehaviorDetail               | int(11)     | NO   | PRI | NULL    | auto_increment |
//		| GameCode                       | varchar(5)  | NO   | MUL | NULL    |                |
//		| CalculateBy                    | varchar(20) | YES  |     | NULL    |                |
//		| CalculateValue                 | varchar(20) | YES  |     | NULL    |                |
//		| IdClassification               | varchar(30) | NO   |     | NULL    |                |
//		| Status                         | varchar(20) | NO   |     | NULL    |                |
//		| AccountTotal                   | int(11)     | YES  |     | NULL    |                |
//		| AccountTotalAllClassification  | float       | YES  |     | NULL    |                |
//		| BehaviorTotal                  | double      | YES  |     | NULL    |                |
//		| BehaviorTotalAllClassification | float       | YES  |     | NULL    |                |
//		| CreatedDate                    | datetime    | NO   |     | NULL    |                |
//		| CreatedBy                      | varchar(10) | NO   |     | NULL    |                |

		
		params.put("GameCode", gameCode);
		params.put("CalculateBy", timing);
		params.put("CalculateValue", timeValue);
		params.put("CalculateDate", calcDate);
		
		params.put("IdClassification", idClassification);
		params.put("Status", status);
		
		params.put("AccountTotal", totalAccount);
		params.put("AccountTotalAllClassification", totalAccountAllClassification );
		params.put("BehaviorTotal", totalBehavior);
		params.put("BehaviorTotalAllClassification", totalBehaviorAllClassification);
		
		params.put("CreatedDate", date);
		params.put("CreatedBy", "ubsystem");
		String sqlUpdate = "" +
				"insert into RC_Behavior_Detail" +
				"(" +
				"	GameCode," +//com.crawler.app.crawler
				"	IdClassification," +
				"	CalculateBy," +
				"	CalculateValue," +
				"	AccountTotal," +
				"	AccountTotalAllClassification," +
				"	BehaviorTotal," +
				"	BehaviorTotalAllClassification," +
				"	Status," +
				"	CreatedDate," +
				"	CreatedBy," +
				"	CalculateDate" +
				") " +
				"values" +
				"(" +
				"	${GameCode}," +
				"	${IdClassification}," +
				"	${CalculateBy}," +
				"	${CalculateValue}," +
				"	${AccountTotal}," +
				"	${AccountTotalAllClassification}," +
				"	${BehaviorTotal}," +
				"	${BehaviorTotalAllClassification}," +
				"	${Status}," +
				"	${CreatedDate}," +
				"	${CreatedBy}," +
				"	${CalculateDate}" +
				")";
		
		try {
			return mysql.executeUpdate(sqlUpdate, params);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}
	
	public boolean insertDetailGrade(
			Date calcDate,
			String gameCode, 
			String IdGrade, 
			String preIdGrade, 
			String timing, 
			String timeValue, 
			long totalAccount, 			
			String status) {
		
		Date date = new Date();
		Map<String, Object> params = new HashMap<String, Object>();
		
//		+-----------------------+-------------+------+-----+---------+----------------+
//		| Field                 | Type        | Null | Key | Default | Extra          |
//		+-----------------------+-------------+------+-----+---------+----------------+
//		| IdBehaviorDetailGrade | int(11)     | NO   | PRI | NULL    | auto_increment |
//		| GameCode              | varchar(5)  | NO   | MUL | NULL    |                |
//		| CalculateBy           | varchar(20) | YES  |     | NULL    |                |
//		| CalculateValue        | varchar(20) | YES  |     | NULL    |                |
//		| IdGrade               | varchar(20) | NO   |     | NULL    |                |
//		| Status                | varchar(20) | NO   |     | NULL    |                |
//		| IdGradePrevious       | varchar(20) | YES  |     | NULL    |                |
//		| AccountTotal          | double      | YES  |     | NULL    |                |
//		| CreatedDate           | datetime    | NO   |     | NULL    |                |
//		| CreatedBy             | varchar(10) | NO   |     | NULL    |                |
//		| CalculateDate         | date        | YES  |     | NULL    |                |
//		+-----------------------+-------------+------+-----+---------+----------------+

		params.put("GameCode", gameCode);
		params.put("CalculateBy", timing);
		params.put("CalculateValue", timeValue);
		params.put("CalculateDate", calcDate);
		params.put("IdGrade", IdGrade);
		params.put("IdGradePrevious", preIdGrade);
		params.put("Status", status);
		params.put("AccountTotal", totalAccount);		
		params.put("CreatedDate", date);
		params.put("CreatedBy", "ubsystem");
		String sqlUpdate = "" +
				"insert into RC_Behavior_Detail_Grade" +
				"(" +
				"	GameCode," +
				"	IdGrade," +
				"	IdGradePrevious," +
				"	CalculateBy," +
				"	CalculateValue," +
				"	AccountTotal," +				
				"	Status," +
				"	CreatedDate," +
				"	CreatedBy," +
				"	CalculateDate" +
				") " +
				"values" +
				"(" +
				"	${GameCode}," +
				"	${IdGrade}," +
				"	${IdGradePrevious}," +
				"	${CalculateBy}," +
				"	${CalculateValue}," +
				"	${AccountTotal}," +				
				"	${Status}," +
				"	${CreatedDate}," +
				"	${CreatedBy}," +
				"	${CalculateDate}" +
				")";
		
		try {
			return mysql.executeUpdate(sqlUpdate, params);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}
	
	public Boolean updateTotalBehavior(String gameCode, String timing, String timeValue, long totalAccount, float totalBehavior){
		Map<String, Object> params = new HashMap<String, Object>();		
		params.put("GameCode", gameCode);
		params.put("CalculateBy", timing);
		params.put("CalculateValue", timeValue);
		params.put("AccountTotalAllClassification", totalAccount);
		params.put("BehaviorTotalAllClassification", totalBehavior);
		String sqlUpdate = "" +
				"update RC_Behavior " +
				"set " +
				"	AccountTotalAllClassification=${AccountTotalAllClassification}, " +
				"	BehaviorTotalAllClassification=${BehaviorTotalAllClassification} " +
				"where " +
				"GameCode=${GameCode} and " +
				"CalculateBy=${CalculateBy} and " +
				"CalculateValue=${CalculateValue}";
		try{
			return mysql.executeUpdate(sqlUpdate, params);
		}catch(Exception ex){
			ex.printStackTrace(System.out);			
		}
		return false;
	}
	
	public Boolean updateTotalBehaviorDetail(String gameCode, String timing, String timeValue, long totalAccount, float totalBehavior){
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("GameCode", gameCode);
		params.put("CalculateBy", timing);
		params.put("CalculateValue", timeValue);		
		params.put("AccountTotalAllClassification", totalAccount);
		params.put("BehaviorTotalAllClassification", totalBehavior);
		String sqlUpdate = "" +
				"update RC_Behavior_Detail " +
				"set " +
				"	AccountTotalAllClassification=${AccountTotalAllClassification}, " +
				"	BehaviorTotalAllClassification=${BehaviorTotalAllClassification} " +
				"where " +
				"GameCode=${GameCode} and " +
				"CalculateBy=${CalculateBy} and " +
				"CalculateValue=${CalculateValue}";
		try{
			return mysql.executeUpdate(sqlUpdate, params);
		}catch(Exception ex){
			ex.printStackTrace(System.out);			
			
		}
		return false;
	}
	/*
	public Boolean insert(Map<String, Object> params){
		Date date = new Date();
		params.put("CreatedDate", date);
		params.put("CreatedBy", "ubsystem");
		String sqlUpdate = "insert into RC_Behavior(GameCode,IdClassification,CalculateBy,CalculateValue,AccountTotal,AccountTotalPercent,BehaviorTotal,BehaviorTotalPercent,CreatedDate,CreatedBy) values(${GameCode},${IdClassification},${CalculateBy},${CalculateValue},${AccountTotal},${AccountTotalPercent},${BehaviorTotal},${BehaviorTotalPercent},${CreatedDate},${CreatedBy})";
		try{
			return mysql.executeUpdate(sqlUpdate, params);
		}catch(Exception ex){
			ex.printStackTrace();			
			
		}
		return false;
	}
	*/
}
