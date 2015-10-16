package com.crawler.app.sql;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
//import org.apache.log4j.Logger;

public class ClientManager implements ManagerIF {

    private final ClientPoolByHost commentClientPoolByHost;
    private static final Lock createLock_ = new ReentrantLock();
    private static final Map<String, ClientManager> instances = new HashMap();
    //private static final Logger logger_ = Logger.getLogger(ClientManager.class);

    public static ManagerIF getInstance(String driver, String url, String user, String password) {
        String key = driver + ":" + url + ":" + user + ":" + password;
        if (!instances.containsKey(key)) {
            createLock_.lock();
            try {
                if (!instances.containsKey(key)) {
                    instances.put(key, new ClientManager(driver, url, user, password));
                }
            } finally {
                createLock_.unlock();
            }
        }
        return (ManagerIF) instances.get(key);
    }

    public ClientManager(String driver, String url, String user, String password) {
        this.commentClientPoolByHost = new ClientPoolByHost(driver, url, user, password);
    }

    @Override
    public Connection borrowClient() {
        Connection client = this.commentClientPoolByHost.borrowClient();

        return client;
    }

    @Override
    public void returnClient(Connection client) {
        this.commentClientPoolByHost.returnObject(client);
    }

    @Override
    public void invalidClient(Connection client) {
        this.commentClientPoolByHost.invalidClient(client);
    }
}