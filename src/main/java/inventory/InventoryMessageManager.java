package inventory;

import database.DB;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import product.ProductService;
import user.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InventoryMessageManager {
	protected static Log logger = LogFactory.getLog(InventoryMessageManager.class);
	   
	public static List<String> save(int id, Inventory inve) { 
         
		   List<InventoryMessage> orders = inve.getInventory() ;
		   
		   List<String> sqls = new ArrayList<String>();
           
			 for(int i=0;i<orders.size();i++){ 
 
				InventoryMessage order = orders.get(i); 
				String sql = "insert into  inventorymessage (id, productId ,categoryId,count, inventoryId,anlycount,realString,paperString)" + 
	                         "  values ( null, '"+order.getProductId()+"', '"+order.getCategoryId()+"',"+order.getCount()+", "+id+","+order.getAnlycount()+",'"+order.getRealString()+"','"+order.getPaperString()+"')";
			    logger.info(sql);   
				sqls.add(sql);  
			}  
			 return sqls; 
	   }
	
	
	public static List<InventoryMessage> getInventoryID(User user ,int id){
		List<InventoryMessage> listm = new ArrayList<InventoryMessage>(); 
		       String sql = "";    
			   sql = "select * from  inventorymessage where inventoryId = "+ id ;
	logger.info(sql);
		Connection conn = DB.getInstance().getConn();
				Statement stmt = DB.getStatement(conn);
				ResultSet rs = DB.getResultSet(stmt, sql);
				try {     
					while (rs.next()) {  
						InventoryMessage orders = getCategoryFromRs(rs); 
						listm.add(orders); 
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally { 
					DB.close(stmt);
					DB.close(rs); 
					DB.close(conn);
				 }
				return listm; 
		 }
	 
	
	public static String delete(int id) {
		String sql = "delete from inventorymessage where inventoryId = " + id;
        return sql ;
	}
	
	public static String deletenybranchid(int id) {
		String sql = "delete from inventorymessage where inventoryId in (select id from inventory where inbranchid = " + id + " and intype = 2) ";
        return sql ; 
	}
	 
	private static InventoryMessage getCategoryFromRs(ResultSet rs){
		InventoryMessage c = new InventoryMessage();
		try {    
			c.setId(rs.getInt("id")); 
			c.setCount(rs.getInt("count"));  
			c.setInventoryId(rs.getInt("inventoryId"));
			c.setProductId(rs.getString("productId")); 
			c.setCategoryId(rs.getInt("categoryId")); 
			
			c.setProductname(ProductService.getIDmap().get(Integer.valueOf(c.getProductId())).getType()); 
		    c.setAnlycount(rs.getInt("anlycount"));
		    c.setRealString(rs.getString("realString"));
		    c.setPaperString(rs.getString("paperString"));
		} catch (SQLException e) { 
			e.printStackTrace();
		}	
		return c ;
	}
	
	
}
