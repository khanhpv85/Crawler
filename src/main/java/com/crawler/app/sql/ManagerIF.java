package com.crawler.app.sql;

import java.sql.Connection;

public abstract interface ManagerIF {

    public abstract Connection borrowClient();

    public abstract void returnClient(Connection paramConnection);

    public abstract void invalidClient(Connection paramConnection);
}

