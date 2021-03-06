package servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import company.CompanyManager;


import user.User;
import user.UserManager;
import user.UserService;

import utill.StringUtill;
import utill.TokenGen;


 
/**
 * 核心请求处理类 
 * 
 * @author liufeng
 * @date 2013-05-18
 */
public class RegistServlet extends HttpServlet {
	private static final long serialVersionUID = 4440739483644821986L;
	 protected static Log logger = LogFactory.getLog(RegistServlet.class);
	/**
	 * 确认请求来自微信服务器
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		Object object = new Object();
		  
		
		synchronized(object){
			saveUser(request,response);
		}
	}
	
	private void saveUser(HttpServletRequest request, HttpServletResponse response){
		TokenGen tokenGen=TokenGen.getInstance();
	    	int count = UserService.getMapId().size();
	    	int initcount = CompanyManager.getLocate().getUsercount();
	    	if(count >= initcount){
	    		request.getSession().setAttribute("message","对不起，您的用户数超过了系统所设置的最大用户数量"); 
	    	}else {
	    		String name = request.getParameter("username");
				String positions = request.getParameter("position");
				String branch = request.getParameter("branch");
			    String juese = request.getParameter("juese");
			    String date = request.getParameter("date"); 
			    
			    if("".equals(date)){
			    	date = null ;
			    }
			    String phone = request.getParameter("phone");
			    String password = request.getParameter("password");
			    String charge = request.getParameter("zhuguan");
			    
			    String location = request.getParameter("location");
			    User u = new User();       
			    u.setBranch(branch);  
			    u.setEntryTime(date); 
			    u.setPositions(positions);
			    u.setPhone(phone);
			    u.setUsertype(Integer.valueOf(juese));
			    u.setUsername(name);  
			    u.setUserpassword(password);
			    u.setStatues(0); 
			    u.setCharge(charge);    
			    u.setLocation(location); 
			    boolean flag = UserManager.save(u);
				// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
			   // System.out.println("RegistServlet"+str);
				if(flag){  
					request.getSession().setAttribute("message","注册成功,请联系管理员激活"); 
				}else {
					request.getSession().setAttribute("message","注册失败,存在相同的职员名");  
				}    
				tokenGen.resetToken(request);
	    	}
		 try { 
			response.sendRedirect("jieguo.jsp?type=zhuce");
		} catch (IOException e) {
			
			e.printStackTrace();
		} 
	}

	/**
	 * 处理微信服务器发来的消息
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 将请求、响应的编码均设置为UTF-8（防止中文乱码）
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
 
		// 调用核心业务类接收消息、处理消息
		doGet(request,response);
 
		// 响应消息
	}
    
}
