package orderproduct;

import branch.BranchManager;
import order.Order;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import product.Product;
import product.ProductService;
import utill.DoubleUtill;
import wilson.upload.UploadOrder;

import java.util.ArrayList;
import java.util.List;

public class OrderProductService {
	public static boolean flag = false ;
	protected static Log logger = LogFactory.getLog(BranchManager.class);

	//取Order中statues = 1的有多个个
	public static int getTotalNum(Order o){
		int result  = 0;
		OrderProduct op;
		List<OrderProduct> opList = o.getOrderProduct();
	
		for(int i = 0 ; i < opList.size() ; i ++){
			op = opList.get(i);
			if(op.getStatues() == 0){
				result += op.getCount();
			}
		}
		return result;
	}
	
	//Order o = > 123.2
	public static Double getTotalPrice(Order o){
		Double result  = 0.0;
		OrderProduct op;
		List<OrderProduct> opList = o.getOrderProduct();
	
		for(int i = 0 ; i < opList.size() ; i ++){
			op = opList.get(i);
			if(op.getStatues() == 0){
				Product p = ProductService.getIDmap().get(Integer.parseInt(op.getSendType()));
				double t =  p.getStockprice() * op.getCount();
				/*if(t == 0.0){
					return 0.0;
				}else{
					result += t;
				}*/ 
				if(t != 0.0){
					result += t;
				}
			}
		}
		return result;
	}
	
	public static List<OrderProduct> getOrderProductFromList(List<OrderProduct> input,int id){
		List<OrderProduct> result  = new ArrayList<OrderProduct>();
		if(input == null){
			return result;
		}
		for(int i = 0 ; i < input.size() ; i ++ ){
			if(input.get(i).getId() == id){
				result.add(input.get(i));
			}
		}
		return result;
	}
	
	//Order o   =>  67:1:123.2,381:1:155.0
	public static String getSendTypeAndCountAndPrice(Order o,UploadOrder uo) throws Exception{
		return getSendTypeAndCountAndPrice(o,uo,true);
	}
	
	public static String getSendTypeAndCountAndPrice(Order o,UploadOrder uo,boolean alwaysGetData) throws Exception{
		return getSendTypeAndCountAndPrice(o,uo,true,false);
	}
	
	//Order o   =>  MXG15-22:1:123.2,SS15T:2:155.0
	public static String getSendTypeAndCountAndPrice(Order o,UploadOrder uo,boolean id,boolean alwayGetData) throws Exception{
		if(!o.isDiangma()){
			if(alwayGetData){
				String output = "";
				String type_tmp = o.getSendType(0, "");
				if(id){ 
				    logger.info(type_tmp); 
					type_tmp = ProductService.gettypemap().get(type_tmp).getId() + "";
					
				}
				String num_tmp = uo.getNum()+"";
				String price_tmp = DoubleUtill.getdoubleTwo(uo.getSalePrice());
				
						
				output += type_tmp + ":" + num_tmp + ":" + price_tmp + ",";
				
				if(output.endsWith(",")){
					output = output.substring(0,output.length()-1);
				}
				
				return output;
			}else{
				return "";
			}
			
		}
		
		
		OrderProduct op = new OrderProduct();
		List<OrderProduct> opList = new ArrayList<OrderProduct>();
		String result = "";
		opList = o.getOrderProduct();
		
		Double totalPrice_db = getTotalPrice(o);
		int num = getTotalNum(o);
		String tmpPrice = "0.0";
		
		Product p = new Product();
		
		
		
		if(id){
			for(int i = 0 ; i < opList.size() ; i ++){
				op = opList.get(i);
				if(op.getStatues() == 0){
					p = ProductService.getIDmap().get(Integer.parseInt(op.getSendType()));
					if(totalPrice_db == 0){
						tmpPrice = DoubleUtill.getdoubleTwo(uo.getSalePrice()* uo.getNum()/num);
					}else{
						tmpPrice = DoubleUtill.getdoubleTwo(p.getStockprice() * uo.getSalePrice() * uo.getNum() / totalPrice_db);
					}
					
					result += op.getSendType() + ":" + op.getCount() + ":" + tmpPrice +  ",";
				}
			}
		}else{
			for(int i = 0 ; i < opList.size() ; i ++){
				op = opList.get(i);
				if(op.getStatues() == 0){
					p = ProductService.getIDmap().get(Integer.parseInt(op.getSendType()));
					if(totalPrice_db == 0){   
						tmpPrice = DoubleUtill.getdoubleTwo(uo.getSalePrice()* uo.getNum()/num);
					}else{
						tmpPrice = DoubleUtill.getdoubleTwo(p.getStockprice() * uo.getSalePrice() * uo.getNum() / totalPrice_db);
					}
					result += p.getType() + ":" + op.getCount() + ":" + tmpPrice +  ",";
				}
			}
		}

		
		if(result.endsWith(",")){
			result = result.substring(0,result.length()-1);
		}
		return result;
		
	}
	
	//input   =>  1000:3,MXG15-22:1,SS15T:2
	public static String getPrice(String input){
		String result = "";
		try{
			String priceSTR = input.split(",")[0].split(":")[0];
			String numSTR = input.split(",")[0].split(":")[1];
			Product p = new Product();
			
			Double uptotalPrice = Double.parseDouble(priceSTR);
			int uptotolNum = Integer.parseInt(numSTR);
			Double dbtotalPrice = 0.0;
			int dbtotalNum = 0;
			
			boolean isFind = true;
			for(int i = 1 ; i < input.split(",").length ; i ++){
				String type = input.split(",")[i].split(":")[0];
				int num = Integer.parseInt(input.split(",")[i].split(":")[1]);
				
				Double d = ProductService.gettypemap().get(type).getStockprice() * num ;
				
				dbtotalPrice += d ;
				dbtotalNum += num;
			}
			 
			if(dbtotalPrice == 0.0){
				isFind = false;
			}
			
			if(isFind){
				//算
				for(int i = 1 ; i < input.split(",").length ; i ++){
					String type = input.split(",")[i].split(":")[0];
					
					result += String.format("%.2f",ProductService.gettypemap().get(type).getStockprice() / dbtotalPrice  *  uptotalPrice * uptotolNum) + ",";
				}
				
			}else{
				//均分
				for(int i = 1 ; i < input.split(",").length ; i ++){
					
					result += String.format("%.2f", uptotalPrice * uptotolNum  / dbtotalNum) + ",";
				}
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
		
		return result;
	}
}
