package product;

import database.DB;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import utill.DBUtill;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
	
	public class ProductManager {
		 protected static Log logger = LogFactory.getLog(ProductManager.class);
		
		 public static List<Product> getProduct(String id) {
			List<Product> categorys = new ArrayList<Product>();
			 Connection conn = DB.getInstance().getConn();
			String sql = "select * from mdproduct where categoryID = "+ id + " and pstatues = 0";
			Statement stmt = DB.getStatement(conn);
			ResultSet rs = DB.getResultSet(stmt, sql);
			try {
				while (rs.next()) {
					Product p = new Product();
					p.setId(rs.getInt("id"));
					p.setCategoryID(rs.getInt("categoryID"));
					p.setType(rs.getString("ptype"));
					p.setName(rs.getString("name"));
					categorys.add(p);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DB.close(rs);
				DB.close(stmt);
				DB.close(conn);
			}
			return categorys;
		}
		
		 public static List<Product> getProductList() {
				List<Product> categorys = new ArrayList<Product>();
			 Connection conn = DB.getInstance().getConn();
				String sql = "select * from mdproduct where pstatues = 0";
				Statement stmt = DB.getStatement(conn);
				ResultSet rs = DB.getResultSet(stmt, sql);
				try {
					while (rs.next()) {
						Product p = new Product();
						p.setId(rs.getInt("id"));
						p.setCategoryID(rs.getInt("categoryID"));
						p.setType(rs.getString("ptype"));
						p.setName(rs.getString("name"));
						categorys.add(p);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					DB.close(rs);
					DB.close(stmt);
					DB.close(conn);
				}
				return categorys;
			}
		 
		 public static List<String> getProduct(int id) {
				List<String> categorys = new ArrayList<String>();
			 Connection conn = DB.getInstance().getConn();
				String sql = "select * from mdproduct where categoryID = "+ id + " and pstatues = 0";
				Statement stmt = DB.getStatement(conn);
				ResultSet rs = DB.getResultSet(stmt, sql);
				try {
					while (rs.next()) {
						categorys.add(rs.getString("ptype"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					DB.close(rs);
					DB.close(stmt);
					DB.close(conn);
				}
				return categorys;
			}
		 
		 public static Product getProductbyname(String name) {
			    Product p = null;
			 Connection conn = DB.getInstance().getConn();
				String sql = "select * from mdproduct where ptype = '"+ name+"'";
logger.info(sql); 
				Statement stmt = DB.getStatement(conn);
				ResultSet rs = DB.getResultSet(stmt, sql);
				try {
					while (rs.next()) { 
						p = new Product();
						p.setId(rs.getInt("id"));
						p.setCategoryID(rs.getInt("categoryID"));
						p.setType(rs.getString("ptype"));
						p.setName(rs.getString("name"));
						
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					DB.close(rs);
					DB.close(stmt);
					DB.close(conn);
				} 
				return p; 
			}
		 
		public static List<Product> getProduct(String id,int statues) {
			List<Product> categorys = new ArrayList<Product>();
			Connection conn = DB.getInstance().getConn();
			String sql = "select * from mdproduct where pstatues = "+statues+" and categoryID = "+ id;
logger.info(sql);  
			Statement stmt = DB.getStatement(conn);
			ResultSet rs = DB.getResultSet(stmt, sql);
			try {
				while (rs.next()) {
					Product p = getOrderStatuesFromRs(rs);
					categorys.add(p);
				} 
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DB.close(rs);
				DB.close(stmt);
				DB.close(conn);
			}
			return categorys;
		}
		
	
		
		public static HashMap<Integer,ArrayList<Product>> getProduct() {
			HashMap<Integer,ArrayList<Product>> map = new HashMap<Integer,ArrayList<Product>>();
			Connection conn = DB.getInstance().getConn();
			String sql = "select * from mdproduct where pstatues = 0";
			Statement stmt = DB.getStatement(conn);
			ResultSet rs = DB.getResultSet(stmt, sql);
			try {
				while (rs.next()) {
					int categoryID = rs.getInt("categoryID");
					ArrayList<Product> list = map.get(categoryID);
					if(list == null){
						list = new ArrayList<Product>();
						map.put(categoryID, list);
					}
					Product p = new Product();
					p.setId(rs.getInt("id"));
					p.setCategoryID(categoryID);
					p.setType(rs.getString("ptype"));
					p.setName(rs.getString("name"));
					list.add(p);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DB.close(rs);
				DB.close(stmt);
				DB.close(conn);
			}
			return map;
		} 
		
		public static HashMap<String,ArrayList<Product>> getProductstr() {
			HashMap<String,ArrayList<Product>> map = new HashMap<String,ArrayList<Product>>();
			Connection conn = DB.getInstance().getConn();
			String sql = "select * from mdproduct where pstatues = 0";
			Statement stmt = DB.getStatement(conn);
			ResultSet rs = DB.getResultSet(stmt, sql);
			try {
				while (rs.next()) {
					int categoryID = rs.getInt("categoryID");
					ArrayList<Product> list = map.get(categoryID+"");
					if(list == null){ 
						list = new ArrayList<Product>();
						map.put(categoryID+"", list);
					}
					Product p = new Product();
					p.setId(rs.getInt("id")); 
					p.setCategoryID(categoryID);
					p.setType(rs.getString("ptype"));
					p.setName(rs.getString("name"));
					list.add(p);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DB.close(rs);
				DB.close(stmt);
				DB.close(conn);
			}
			return map;
		} 
		
		public static HashMap<String,Product> getProductType() {  
			HashMap<String,Product> map = new HashMap<String,Product>();
			Connection conn = DB.getInstance().getConn();
			String sql = "select * from mdproduct where pstatues = 0";
			Statement stmt = DB.getStatement(conn);
			ResultSet rs = DB.getResultSet(stmt, sql);
			try {
				while (rs.next()) {
					Product p = getOrderStatuesFromRs(rs);
					map.put(p.getType(), p);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DB.close(rs);
				DB.close(stmt);
				DB.close(conn);
			}
			return map;
		} 
		
		
		public static HashMap<Integer,Product> getProductID() {   
			HashMap<Integer,Product> map = new HashMap<Integer,Product>();
			Connection conn = DB.getInstance().getConn();
			String sql = "select * from mdproduct";
			Statement stmt = DB.getStatement(conn);
			ResultSet rs = DB.getResultSet(stmt, sql);
			try {
				while (rs.next()) { 
					Product p = getOrderStatuesFromRs(rs);
					map.put(p.getId(), p);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DB.close(rs);
				DB.close(stmt);
				DB.close(conn);
			}
			return map;
		} 
		
		public static List<String> getProductlist() { 
			List<String> list = new ArrayList<String>();
			Connection conn = DB.getInstance().getConn();
			String sql = "select * from mdproduct where pstatues = 0";
			Statement stmt = DB.getStatement(conn); 
			ResultSet rs = DB.getResultSet(stmt, sql);
			try {  
				while (rs.next()) {
					int categoryID = rs.getInt("categoryID");
					Product p = new Product(); 
					p.setId(rs.getInt("id")); 
					p.setCategoryID(categoryID);
					p.setType(rs.getString("ptype"));
					p.setName(rs.getString("name"));
					list.add(p.getType());   
				}  
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DB.close(rs);
				DB.close(stmt);
				DB.close(conn);
			} 
			return list; 
		}
		
		
		
		
		public static HashMap<String,ArrayList<String>> getProductName() {
			HashMap<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
			Connection conn = DB.getInstance().getConn();
			String sql = "select * from mdproduct where pstatues = 0  ";
			 
			Statement stmt = DB.getStatement(conn);
			ResultSet rs = DB.getResultSet(stmt, sql);
			try { 
				while (rs.next()) {
					String categoryID = rs.getInt("categoryID")+"";
					ArrayList<String> list = map.get(categoryID+"");
					if(list == null){
						list = new ArrayList<String>();
						map.put(categoryID+"", list);
					}
					String pName = rs.getString("ptype");
					list.add(pName);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DB.close(rs);
				DB.close(stmt);
				DB.close(conn);
			}
			return map;
		}
		
		public static List<String> getAllProductName() {
			List<String> result = new ArrayList<String>();
			Connection conn = DB.getInstance().getConn();
			String sql = "select * from mdproduct where pstatues = 0  ";
			 
			Statement stmt = DB.getStatement(conn);
			ResultSet rs = DB.getResultSet(stmt, sql);
			try { 
				while (rs.next()) {
					String pName = rs.getString("ptype");
					result.add(pName);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DB.close(rs);
				DB.close(stmt);
				DB.close(conn);
			}
			return result;
		}
		
		public static HashMap<String,ArrayList<String>> getProductName(int statues) {
			HashMap<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
			Connection conn = DB.getInstance().getConn();
			String sql = "select * from mdproduct where pstatues = 0";
			Statement stmt = DB.getStatement(conn);
			ResultSet rs = DB.getResultSet(stmt, sql);
			try {
				while (rs.next()) {
					String categoryID = rs.getInt("categoryID")+"";
					ArrayList<String> list = map.get(categoryID+"");
					if(list == null){
						list = new ArrayList<String>();
						map.put(categoryID+"", list);
					}
					String pName = rs.getString("ptype");
					list.add(pName);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DB.close(rs);
				DB.close(stmt);
				DB.close(conn);
			}
			return map;
		}
		public static boolean delete(String str ) {
			String ids = "(" + str + ")";
			boolean b = false;
			Connection conn = DB.getInstance().getConn();
			String sql = "update  mdproduct set pstatues = 1 where  id in " + ids; 
			Statement stmt = DB.getStatement(conn);
			ProductService.flag = true ;
			try {
				DB.executeUpdate(stmt, sql);
				ProductService.flag = true ;
				b = true;
			} finally {
				DB.close(stmt);
				DB.close(conn);
			}
			return b;
		}

		
		public static void save(String type,String id ,double size){
			String sql = ""; 
			Product p = getProductbyname(type); 
			if(null == p){
				sql = "insert into mdproduct(id, name, ptype,categoryID,pstatues,size) VALUES (null, null,'"+type+"','"+id+"',0,'"+size+"')";
			}else { 
				sql = "update mdproduct set pstatues = 0 where id = "+ p.getId();
			} 
			//System.out.println("*****"+id);
			DBUtill.sava(sql);
			ProductService.flag = true ;
		}
		 
		public static void update(String type,String id,double size,double stockprice ){
			Connection conn = DB.getInstance().getConn();
			String sql = "update mdproduct set ptype = ? , size = ? ,stockprice = ? where id = ?";
			PreparedStatement pstmt = DB.prepare(conn, sql);
			try {
				pstmt.setString(1, type);
				pstmt.setDouble(2, size); 
				pstmt.setDouble(3, stockprice); 
				pstmt.setString(4, id); 
logger.info(pstmt); 				
				pstmt.executeUpdate();
				ProductService.flag = true ;
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DB.close(pstmt);
				DB.close(conn);
			}
		 
		}
		 
		 public static Product getOrderStatuesFromRs(ResultSet rs){
			 Product p = new Product();  
				try { 
					p.setId(rs.getInt("id"));  
					p.setCategoryID(rs.getInt("categoryID"));
					p.setType(rs.getString("ptype"));   
					p.setName(rs.getString("name"));   
					p.setStatues(rs.getInt("pstatues"));
					p.setSize(rs.getDouble("size"));    
					p.setStockprice(rs.getDouble("stockprice"));
				} catch (SQLException e) {  
					e.printStackTrace();
				} 
				return p;
		   }
		  
		 
 
	}


