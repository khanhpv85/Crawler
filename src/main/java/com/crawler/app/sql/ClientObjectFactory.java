package com.crawler.app.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.commons.pool.PoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientObjectFactory implements PoolableObjectFactory {

    private static final Logger log = LoggerFactory.getLogger(ClientObjectFactory.class);
    private String driver;
    private String url;
    private String user;
    private String password;

    public ClientObjectFactory(String driver, String url, String user, String password) {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public void activateObject(Object arg0)
            throws Exception {
    }

    @Override
    public void destroyObject(Object obj)
            throws Exception {
        if (obj == null) {
            return;
        }
        Connection client = (Connection) obj;
        client.close();
    }

    @Override
    public Object makeObject()
            throws Exception {
        Class.forName(this.driver);
        Connection client = DriverManager.getConnection(this.url + "user=" + this.user + "&password=" + this.password);
        return client;
    }

    @Override
    public void passivateObject(Object arg0)
            throws Exception {
    }

    @Override
    public boolean validateObject(Object obj) {
        boolean result = true;
        try {
            Connection client = (Connection) obj;
            if (obj == null) {
                return result;
            }
            result = (result) && (!client.isClosed());
        } catch (SQLException ex) {
            result = false;
        }
        return result;
    }
}