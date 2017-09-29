package utill;

import database.DB;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class CookieUtill {
	protected static Log logger = LogFactory.getLog(DB.class);

	public static String isRight(Cookie Cookies[],String sear){
    	String str = "";
		if (Cookies == null) {
			logger.info("还没有cookie值");
		}else {
    					for(int i=0;i < Cookies.length;i++){
							try {
								String name = URLDecoder.decode(Cookies[i].getName(),"utf-8");
								if(name.equals(sear)){
									str = URLDecoder.decode(Cookies[i].getValue(),"utf-8");
								}
								
							} catch (UnsupportedEncodingException e) {
								Cookies[i].setValue(null);
								return "";
							}
    			         }
    			}  
       return str ; 
    }
}