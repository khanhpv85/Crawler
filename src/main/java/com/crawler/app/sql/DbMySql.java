package com.crawler.app.sql;

import java.sql.Connection;
import java.sql.SQLException;
//import org.apache.log4j.Logger;
import org.slf4j.Logger;



public class DbMySql extends Database {
    //private static final Logger logger_ = Logger.getLogger(DbMySql.class);
    public DbMySql(String host, String port, String dbname, String user, String password) {
        super(DBDRIVER_MYSQL, host, port, dbname, user, password);
    }

    @Override
    protected Connection getConnection() {
        int retry = 3;
        boolean ret = false;
        Connection conn = null;
        while (ret == false) {
            try {
                conn = this.dbmanager.borrowClient();
                com.mysql.jdbc.Connection mysqlcon = (com.mysql.jdbc.Connection) conn;
                mysqlcon.ping();
                ret = true;
            } catch (SQLException ex) {
                //logger_.error(ex);
                ex.printStackTrace(System.out);
                if (conn != null) {
                    this.invalidConnection(conn);
                }
            } finally {
                retry--;
                if (retry <= 0) {
                    break;
                }
            }
        }
        return conn;
    }
    
}