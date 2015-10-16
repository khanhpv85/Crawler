package com.crawler.app.sql;

import com.google.common.base.Strings;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Database {

    protected ManagerIF dbmanager = null;
    protected String _driver;
    public static final String DBDRIVER_MSSQL = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public static final String DBDRIVER_MYSQL = "com.mysql.jdbc.Driver";
    public static final String DBDRIVER_PGSQL = "org.postgresql.Driver";
    public static final String DBDRIVER_HSQLDB = "org.hsqldb.jdbc.JDBCDriver";
    public static final Pattern STRING_PARAM_PATTERN = Pattern.compile("\\$\\{([^\\}]+)\\}");

    public Database(String driver, String host, String port, String dbname, String user, String password) {
        this._driver = driver;
        String url = buildUrlConnection(_driver, host, port, dbname, true);
        this.dbmanager = ClientManager.getInstance(driver, url, user, password);
    }

    private String buildUrlConnection(String databaseDriver,
            String address, String port, String databaseName,
            boolean sendStringParametersAsUnicode) {
        if (databaseDriver == null || address == null || databaseName == null
                || port == null) {
            return null;
        }
        String url = "";
        if (databaseDriver.equals(DBDRIVER_MSSQL)) {
            url += "jdbc:sqlserver://" + address + ":" + port + ";database="
                    + databaseName;
            if (!sendStringParametersAsUnicode) {
                url += ";sendStringParametersAsUnicode=false";
            }
            return url;
        }
        if (databaseDriver.equals(DBDRIVER_PGSQL)) {
            url += "jdbc:postgresql://" + address
                    + ("".equals(port) ? "" : ":" + port) + "/" + databaseName;
            return url;
        }
        if (databaseDriver.equals(DBDRIVER_MYSQL)) {
            url += "jdbc:mysql://" + address
                    + ("".equals(port) ? "" : ":" + port) + "/" + databaseName + "?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8&";
            return url;
        }
        return url;
    }

    protected Connection getConnection() {
        Connection conn = this.dbmanager.borrowClient();;
        return conn;
    }

    protected void releaseConnection(Connection conn) {
        this.dbmanager.returnClient(conn);
    }

    protected void invalidConnection(Connection conn) {
        this.dbmanager.invalidClient(conn);
    }

    protected List<String> listParamByIndex(String input) {
        List<String> result = new LinkedList<>();
        Matcher matcher = STRING_PARAM_PATTERN
                .matcher(input);
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
    }

    protected PreparedStatement prepareStatement(Connection conn,
            String sql, List<?> params) throws SQLException {
        if (conn == null || Strings.isNullOrEmpty(sql)) {
            return null;
        }

        PreparedStatement stmt = conn.prepareStatement(sql);
        if (params != null) {
            int index = 1;
            for (Object param : params) {
                if (param instanceof String) {
                    stmt.setString(index, (String) param);
                } else if (param instanceof Integer) {
                    stmt.setInt(index, (Integer) param);
                } else if (param instanceof Long) {
                    stmt.setLong(index, (Long) param);
                } else if (param instanceof Float) {
                    stmt.setFloat(index, (Float) param);
                } else if (param instanceof Double) {
                    stmt.setDouble(index, (Double) param);
                } else {
                    stmt.setObject(index, param);
                }
                index++;
            }
        }
        params.clear();
        return stmt;
    }

    protected PreparedStatement prepareStatement(Connection conn,
            String sql, Map<String, ?> params, boolean useNullForMissingParams)
            throws SQLException {
        if (conn == null || Strings.isNullOrEmpty(sql)) {
            return null;
        }

        List<String> paramByIndex = listParamByIndex(sql);
        List<Object> paramValueByIndex = new LinkedList<>();
        for (String paramName : paramByIndex) {
            if (params.containsKey(paramName)) {
                paramValueByIndex.add(params.get(paramName));
            } else {
                if (useNullForMissingParams) {
                    paramValueByIndex.add(null);
                } else {
                    throw new IllegalArgumentException(
                            "Missing value for param " + paramName);
                }
            }
        }
        String cleanSql = STRING_PARAM_PATTERN.matcher(sql).replaceAll("?");
        if(cleanSql==null || "".equals(cleanSql)){
            cleanSql = sql;
        }
        paramByIndex.clear();
        
        return prepareStatement(conn, cleanSql, paramValueByIndex);
    }

    protected Map<String, Object> buildResultMapFromResultSet(ResultSet rs,
            int numColumns, String[] columnNames, Integer[] columnTypes)
            throws SQLException {
        if (rs == null) {
            return null;
        }
        if (!rs.next()) {
            return new HashMap<>();
        }
        Map<String, Object> result = new HashMap<>();
        for (int i = 1; i <= numColumns; i++) {
            String columnName = columnNames[i - 1];
            Integer columnType = columnTypes[i - 1];
            Object columnValue;
            switch (columnType) {
                case Types.CHAR:
                case Types.VARCHAR:
                    columnValue = rs.getString(i);
                    break;
                case Types.TINYINT:
                case Types.SMALLINT:
                    columnValue = rs.getShort(i);
                    break;
                case Types.INTEGER:
                    columnValue = rs.getInt(i);
                    break;
                case Types.BIGINT:
                    columnValue = rs.getLong(i);
                    break;
                default:
                    columnValue = rs.getObject(i);
            }
            result.put(columnName, columnValue);
        }
        return result;
    }

    protected List<Map<String, Object>> buildResultListFromResultSet(
            ResultSet rs) throws SQLException {
        if (rs == null) {
            return null;
        }
        List<Map<String, Object>> result = new ArrayList<>();
        ResultSetMetaData rsMetaData = rs.getMetaData();
        List<String> columnNames = new ArrayList<>();
        List<Integer> columnTypes = new ArrayList<>();
        int numColumns = rsMetaData.getColumnCount();
        for (int i = 1; i <= numColumns; i++) {
            columnNames.add(rsMetaData.getColumnName(i));
            columnTypes.add(rsMetaData.getColumnType(i));
        }
        Map<String, Object> entry = buildResultMapFromResultSet(rs, numColumns,
                columnNames.toArray(new String[0]), columnTypes
                .toArray(new Integer[0]));
        while (entry != null && !entry.isEmpty()) {
            result.add(entry);
            entry = buildResultMapFromResultSet(rs, numColumns, columnNames
                    .toArray(new String[0]), columnTypes
                    .toArray(new Integer[0]));
        }
        rs.close();
        return result;
    }

    public List<Map<String, Object>> executeStatement(String sql, Map<String, ?> params) throws SQLException {
        PreparedStatement ps;
        List<Map<String, Object>> dbResult = null;
        Connection conn = getConnection();
        if(conn!=null){
            ps = prepareStatement(conn, sql, params, true);
	        try {

	            if (ps.execute()) {
	                dbResult = buildResultListFromResultSet(ps.getResultSet());
	            }
	        } catch (SQLException ex) {
	            invalidConnection(conn);
	            //System.err.println(ps.g);
	            throw ex;
	        } finally {
	        	if(ps!=null){
	        		ps.close();
	        	}
	            releaseConnection(conn);
	        }
        }
        return dbResult;
    }
    public Boolean executeUpdate(String sql, Map<String, ?> params) throws SQLException {
        PreparedStatement ps;
       
        Connection conn = getConnection();
        
        if(conn!=null){
        	ps = prepareStatement(conn, sql, params, true);
	        try {
	            if (ps.executeUpdate()>0) {
	            	return true;
	            }
	            
	        } catch (SQLException ex) {
	            invalidConnection(conn);
	            //System.err.println(ps.g);
	            throw ex;
	        } finally {
	        	if(ps!=null){
	        		ps.close();
	        	}
	            releaseConnection(conn);
	        }
        }
        return false;
    }
    
    public Boolean executeUpdate(Connection conn, String sql, Map<String, ?> params) throws SQLException {
        PreparedStatement ps;
              
        if(conn!=null){
        	ps = prepareStatement(conn, sql, params, true);
	        try {
	            ps = prepareStatement(conn, sql, params, true);
	            if (ps.executeUpdate()>0) {
	            	return true;
	            }
	            
	        } catch (SQLException ex) {
//	            invalidConnection(conn);
	            //System.err.println(ps.g);
	            throw ex;
	        } finally {
	        	if(ps!=null){
	        		ps.close();
	        	}
//	            releaseConnection(conn);
	        }
        }
        return false;
    }
    public Connection getDbConnection(){
    	return getConnection();
    }
}