package product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductService {
   public static boolean flag = true ;
   public static Map<String,Product> typemap ;  // 
   public static Map<Integer,Product> idmap ; 
   public static List<String> list ;
   public static List<Product> listall ;
   public static HashMap<String,ArrayList<String>> typeName;
   public static List<String> typeNameList;
   
   public static List<String> getTypeNameList(){
	   init();
	   if(typeNameList == null ){
		   typeNameList = ProductManager.getAllProductName();
		 } 
		return typeNameList;
   }

  public static Map<String, Product> gettypemap() {
	  init();
	 if(typemap == null ){
		typemap = ProductManager.getProductType();
	 } 
	return typemap;
}
 
  public static HashMap<String,ArrayList<String>> gettypeName() {
	  init();
	 if(typeName == null){
		 typeName = ProductManager.getProductName();
	 } 
	return typeName;
}
  
  public static List<String> getlist(int id) {
	  init();
		 if(typeName == null){
			 typeName = ProductManager.getProductName();
		 }  
		return typeName.get(id+"");
	}
	 
  public static List<Product> getlistall() {
	   init();
		 if(listall == null){ 
			 listall = ProductManager.getProductList();
		 }  
		return listall;
	}
  
public static Map<Integer, Product> getIDmap() {
	init();
	if(idmap == null ){
		idmap = ProductManager.getProductID();
	}  
	return idmap;
}

	public static void init(){
		if(flag){
			typeNameList = ProductManager.getAllProductName();
			typemap = ProductManager.getProductType();
			typeName = ProductManager.getProductName();
			idmap = ProductManager.getProductID();
			listall = ProductManager.getProductList();
		}
		flag = false ;
	}
}
