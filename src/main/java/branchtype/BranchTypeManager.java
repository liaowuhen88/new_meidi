package branchtype;


import branch.BranchManager;
import database.DB;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
  
public class BranchTypeManager {
	
	 protected static Log logger = LogFactory.getLog(BranchTypeManager.class);
	
	 
	 public static boolean  save(String  c){
		 Connection conn = DB.getInstance().getConn();
		String sql = "insert into mdbranchtype(id,bname) values (null, ?)";
		PreparedStatement pstmt = DB.prepare(conn, sql);
		try {
			pstmt.setString(1, c); 
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DB.close(pstmt);
			DB.close(conn);
		}
	   return true;
	}
	
	 public static boolean  update(String  c ,String bid ){
		 Connection conn = DB.getInstance().getConn();
			String sql = "update mdbranchtype set bname = ? where id = " + bid ;
			PreparedStatement pstmt = DB.prepare(conn, sql);
			try { 
				pstmt.setString(1, c);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DB.close(pstmt);
				DB.close(conn);
			}
		   return true;
		}
	  
	 
	 public static boolean  update(int  statues ,String bid ){
		 Connection conn = DB.getInstance().getConn();
			String sql = "update mdbranchtype set statues = ? where id = " + bid ;
			PreparedStatement pstmt = DB.prepare(conn, sql);
			try { 
				pstmt.setInt(1, statues);  
				pstmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DB.close(pstmt);
				DB.close(conn);
			}
		   return true;
		}
	 

		public static List<BranchType> getLocate() {
			List<BranchType> users = new ArrayList<BranchType>();
			Connection conn = DB.getInstance().getConn();
			String sql = "select * from mdbranchtype";
			Statement stmt = DB.getStatement(conn);
			ResultSet rs = DB.getResultSet(stmt, sql);
			try {  
				while (rs.next()) { 
					BranchType g = getBranchFromRs(rs);
					users.add(g);
				}  
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DB.close(rs);
				DB.close(stmt);
				DB.close(conn);
			}
			return users;
		}
 
		
		public static BranchType getLocate(int id) {
			BranchType g = new BranchType();
			Connection conn = DB.getInstance().getConn();
			String sql = "select * from mdbranchtype where id = " + id ;
			Statement stmt = DB.getStatement(conn);
			ResultSet rs = DB.getResultSet(stmt, sql);
			try {  
				while (rs.next()) { 
					g = getBranchFromRs(rs);
				}  
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DB.close(rs);
				DB.close(stmt);
				DB.close(conn);
			} 
			return g;
		}
		
		
		public static boolean delete(String str ) {
			String ids = "(" + str + ")";
			boolean b = false;
			int count = BranchManager.getcount(ids);
			if(count > 0){
				return false ;
			}

			Connection conn = DB.getInstance().getConn();
			String sql = "delete from mdbranchtype where id in " + ids;
logger.info(sql);
			Statement stmt = DB.getStatement(conn);
			try {
				DB.executeUpdate(stmt, sql);
				b = true;
			} finally {
				DB.close(stmt);
				DB.close(conn);
			}  
			return b;
		}
		

		private static BranchType getBranchFromRs(ResultSet rs){
			BranchType branch= new BranchType();
			try {   
				branch.setId(rs.getInt("id"));  
				branch.setName(rs.getString("bname"));
				branch.setStatues(rs.getInt("statues")); 
				branch.setIsSystem(rs.getInt("isSystem"));
			} catch (SQLException e) {
				e.printStackTrace();
			}	
			return branch ;
		}	
		
		public static String getNameById(int id){
			return getLocate(id).getName();
		}
		
}
