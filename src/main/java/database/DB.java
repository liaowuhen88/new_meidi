package database;

import com.alibaba.druid.pool.DruidDataSource;
import order.Order;
import order.OrderManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import utill.DBUtill;

import java.beans.PropertyVetoException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB {
    //使用单利模式创建数据库连接池
    protected static Log logger = LogFactory.getLog(DB.class);
    private static DB instance;
    private DruidDataSource dataSource;

    private DB() throws SQLException, PropertyVetoException {

        logger.info("DataSource");
        dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");

       /* dataSource.setUrl("jdbc:mysql://localhost:3306/sr_meidi");
        dataSource.setUsername("root");
        dataSource.setPassword("liaowuhen");
*/
        dataSource.setUrl("jdbc:mysql://localhost:3306/liaowuhendgNew");
        dataSource.setUsername("liaowuhen");
        dataSource.setPassword("liaowuhen");
        dataSource.setMaxActive(50);
        dataSource.setInitialSize(5);
        dataSource.setMaxWait(6000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeoutMillis(1000 * 5);
        dataSource.setLogAbandoned(true);
        //dataSource.setFilters("mergeStat");

    }

    public static void main(String[] args) {
        int i = 0;
        while (true) {
            List<Order> list = OrderManager.getCheckedDBOrders();

            List<String> sql = new ArrayList<String>();
            String sql1 = "insert into test1 (id,name )VALUES( null,'name');";
            String sql2 = "insert into test2 (id,name )VALUES( null,'name');";
            sql.add(sql1);
            sql.add(sql2);
            DBUtill.sava(sql);
            logger.info(list);
        }
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
            logger.error("error", e);
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
            logger.error("error", e);
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
            logger.error("error", e);
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
            logger.error("error", e);
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
            logger.error("error", e);
        }
        return i;
    }

    public static void close(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            logger.error("error", e);
        }
    }

    public static void close(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            logger.error("error", e);
        }
    }

    public static void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            logger.error("error", e);
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
