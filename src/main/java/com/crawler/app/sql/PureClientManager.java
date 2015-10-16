package com.crawler.app.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
//import org.apache.log4j.Logger;

public class PureClientManager
        implements ManagerIF {

    private static final Lock createLock_ = new ReentrantLock();
    private static Map<String, PureClientManager> INSTANCES = new HashMap();
    //private static Logger logger_ = Logger.getLogger(PureClientManager.class);
    private String driver;
    private String url;
    private String user;
    private String password;

    public static ManagerIF getInstance(String driver, String url, String user, String password) {
        String key = driver + ":" + url + ":" + user + ":" + password;
        if (!INSTANCES.containsKey(key)) {
            createLock_.lock();
            try {
                if (!INSTANCES.containsKey(key)) {
                    INSTANCES.put(key, new PureClientManager(driver, url, user, password));
                }
            } finally {
                createLock_.unlock();
            }
        }
        return (ManagerIF) INSTANCES.get(key);
    }

    public PureClientManager(String driver, String url, String user, String password) {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Connection borrowClient() {
        try {
            Class.forName(this.driver);
            return DriverManager.getConnection(this.url + "user=" + this.user + "&password=" + this.password);
        } catch (Exception ex) {
        }
        return null;
    }

    public void returnClient(Connection client) {
        try {
            client.close();
        } catch (Exception ex) {
        }
    }

    public void invalidClient(Connection client) {
        try {
            client.close();
        } catch (Exception ex) {
        }
    }
}