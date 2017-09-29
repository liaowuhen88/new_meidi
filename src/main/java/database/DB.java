package database;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyVetoException;
import java.sql.*;

public class DB {
    //使用单利模式创建数据库连接池
    protected static Log logger = LogFactory.getLog(DB.class);
    private static ComboPooledDataSource dataSource;
    private static DB instance;

    private DB() throws SQLException, PropertyVetoException {
        dataSource = new ComboPooledDataSource();

        dataSource.setUser("liaowuhen");     //用户名
        dataSource.setPassword("liaowuhen"); //密码
        dataSource.setJdbcUrl("jdbc:mysql://liaowuhen.gotoftp3.com:3306/liaowuhen");//数据库地址

        try {
            dataSource.setDriverClass("com.mysql.jdbc.Driver");
        } catch (PropertyVetoException e) {
            logger.error("error", e);
        }
        dataSource.setInitialPoolSize(5); //初始化连接数
        dataSource.setMinPoolSize(5);//最小连接数
        dataSource.setMaxPoolSize(10);//最大连接数
        dataSource.setMaxStatements(50);//最长等待时间
        dataSource.setMaxIdleTime(60);//最大空闲时间，单位毫秒

    }

    public static final DB getInstance() {
        if (instance == null) {
            try {
                instance = new DB();
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        return instance;
    }

    public static PreparedStatement prepare(Connection conn, String sql) {
        PreparedStatement pstmt = null;
        try {
            if (conn != null) {
                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pstmt;
    }

    public static PreparedStatement prepare(Connection conn, String sql, int autoGenereatedKeys) {
        PreparedStatement pstmt = null;
        try {
            if (conn != null) {
                pstmt = conn.prepareStatement(sql, autoGenereatedKeys);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pstmt;
    }

    public static Statement getStatement(Connection conn) {
        Statement stmt = null;
        try {
            if (conn != null) {
                stmt = conn.createStatement();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stmt;
    }

    public static ResultSet getResultSet(Statement stmt, String sql) {
        ResultSet rs = null;
        try {
            if (stmt != null) {
                rs = stmt.executeQuery(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public static int executeUpdate(Statement stmt, String sql) {
        int i = 0;
        try {
            if (stmt != null) {
                i = stmt.executeUpdate(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    public static void close(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized final Connection getConn() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("error", e);
        }
        return conn;
    }
}
