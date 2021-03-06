package utill;

import group.Group;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtill {
	protected static Log logger = LogFactory.getLog(StringUtill.class);
    public static boolean isNull(String str){
    	boolean flag = false ;
    	if("".equals(str) || "null".equals(str) || null == str || "undefined".equals(str) || "NULL".equals(str)){
    		flag = true ;
    	}
    	return flag ;
    }

	public static Set<String> GetSetByObject(String object)
	{
		JSONArray jsonArray = null;
		Set<String> set = new HashSet();
		if (object != null)
		{
			jsonArray = JSONArray.fromObject(object);
			System.out.println(jsonArray);
			for (int i = 0; i < jsonArray.size(); i++)
			{
				JSONObject jsObj = jsonArray.getJSONObject(i);

				set.addAll(jsObj.keySet());
			}
		}
		return set;
	}
    public static String GetJson(String[] list) {  
        // java 转数组  
    	 
        if(list != null){
        	JSONArray jsonArray = JSONArray.fromObject(list);  
            String s = jsonArray.toString(); 
            return s ; 
        }
        
       return "" ;  
    } 
    
    public static String GetJson(List list){ 
    	   
    	if(null != list){
    	 JSONArray jsonArray = JSONArray.fromObject(list);
    	 //logger.info(list.toString());
       	 return jsonArray.toString();
    	}  
    	 return "[]"; 
    }
    
    public static String GetJson(Object object){ 
 	   
    	if(null != object){
         if(object instanceof Collection){  
        	 JSONArray jsObj = JSONArray.fromObject(object); 
        	 return jsObj.toString(); 
         }else {
        	 JSONObject jsObj = JSONObject.fromObject(object);
        	 //logger.info(list.toString());
           	 return jsObj.toString(); 
         }
    	 
    	} 
    	 return "";
    }
    
    public static String GetSqlInGroup(List<Group> listg){
    	String str = "(";
    	
    	if(listg == null || listg.size() == 0){
    		return " ( -1 ) ";
    	}else {
    	
    	
		for(int i=0;i<listg.size();i++){
			Group g = listg.get(i);
			str += g.getId()+",";
		} 
		str = str.substring(0, str.length()-1);
		str += ")";
    	}
    	return str ;
    	
    }
    public static String GetJson(Map map){
   // logger.info(map.toString()); 
    	JSONObject jsObj = JSONObject.fromObject(map);  
   	 return jsObj.toString();
   }
    // 获取字母不包含汉子
    public static String getStringNocn(String str){
    	 str = str.replace("(", "").replace(")", "").replace("（","").replace("）", "").replaceAll("\\s*", "");
    	 String regEx = "[\\u4e00-\\u9fa5]"; 

		 Pattern p = Pattern.compile(regEx);
		 Matcher m = p.matcher(str);  
         String s =m.replaceAll("").trim(); 
		return s; 
		  
    } 
     
    // 获取字母不包含数字 
    public static  String getNumbers(String content) {  
    	String regEx="[^0-9]";   
    	Pattern p = Pattern.compile(regEx);   
    	Matcher m = p.matcher(content);   
        return  m.replaceAll("").trim();   
    }  
    
    
    /*public static String getStringreal(String str){
   	    str = str.replace("(", "").replace(")", "").replace("（","").replace("）", "").replaceAll("\\s*", "");
   	 
		return str;  
		  
   } */
    
    
    public static String UUID(int num){
    	String s = UUID.randomUUID().toString();
    	if(num > 36){
    		num = 36;
    	}
    	if(num <= 0){
    		num = 8;
    	}
    	return s.substring(0,num);
    }
    
    public static String shortUUID(){
    	return UUID(8);
    }
    
    public static void main(String args[]){
    	String str1 = "MRO201-4（智能型）净水机";
    	String str2 = "空壳MRO201-4(豪华型）美的净水机 空壳样机";
    	System.out.println(getStringNocn(str1));
    	System.out.println(getStringNocn(str2)); 
    	System.out.print(getStringNocn(str2).equals(getStringNocn(str1)));  
    }
}
