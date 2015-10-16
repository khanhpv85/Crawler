package com.crawler.app.sql;

import java.util.HashMap;
import java.util.Map;

public class SampleMySql {

	public static void insert(){
		try{
			DbMySql mysql = new DbMySql("host", "port", "dbName", "dbUser", "dbPwd");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("field", 0);
			mysql.executeStatement("insert into table_a(field) values(${field})", params);			
		}catch(Exception ex){
			ex.printStackTrace();						
		}
	}
}
