package com.crawler.app.sql;

import java.sql.Connection;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientPoolByHost {

    private static final Logger log = LoggerFactory.getLogger(ClientPoolByHost.class);
    public static final int DEFAULT_MAX_ACTIVE = 20;
    public static final long DEFAULT_MAX_WAITTIME_WHEN_EXHAUSTED = -1L;
    public static final int DEFAULT_MAX_IDLE = 5;
    private ClientObjectFactory clientFactory;
    private int maxActive;
    private int maxIdle;
    private long maxWaitTimeWhenExhausted;
    private GenericObjectPool pool;

    public ClientPoolByHost(String driver, String url, String user, String password) {
        this.clientFactory = new ClientObjectFactory(driver, url, user, password);
        this.maxActive = 20;
        this.maxIdle = 20;
        this.maxWaitTimeWhenExhausted = -1L;
        this.pool = createPool();

        this.pool.setMinEvictableIdleTimeMillis(50000L);
        this.pool.setTimeBetweenEvictionRunsMillis(55000L);
    }

    public void close() {
        try {
            this.pool.close();
        } catch (Exception e) {
            log.error("Unable to close pool", e);
        }
    }

    private GenericObjectPool createPool() {
        GenericObjectPoolFactory poolFactory = new GenericObjectPoolFactory(this.clientFactory, this.maxActive, (byte) 1, this.maxWaitTimeWhenExhausted, this.maxIdle);

        GenericObjectPool p = (GenericObjectPool) poolFactory.createPool();
        p.setTestOnBorrow(true);
        p.setTestWhileIdle(true);
        p.setMaxIdle(-1);
        return p;
    }

    public Connection borrowClient() {
        Connection client = null;
        try {
            client = (Connection) this.pool.borrowObject();
        } catch (Exception e) {
            log.error("Uncaught exception", e);
        }
        return client;
    }

    public void returnObject(Connection client) {
        try {
            this.pool.returnObject(client);
        } catch (Exception e) {
            log.error("Uncaught exception", e);
        }
    }

    public void invalidClient(Connection client) {
        try {
            this.pool.invalidateObject(client);
        } catch (Exception e) {
            log.error("Uncaught exception", e);
        }
    }
}