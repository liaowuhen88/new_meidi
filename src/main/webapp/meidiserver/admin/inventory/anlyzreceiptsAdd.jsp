<%@ page language="java" import="java.util.*,utill.*,product.*,comparator.*,order.*,net.sf.json.*,inventory.*,orderproduct.*,branch.*,branchtype.*,grouptype.*,category.*,group.*,user.*;" pageEncoding="UTF-8"  contentType="text/html;charset=utf-8"%>
<%
request.setCharacterEncoding("utf-8");
User user = (User)session.getAttribute("user");  
String inventoryid = request.getParameter("id");
Map<Integer,Branch> branchmap = BranchService.getMap();

HashMap<Integer,Category> mapc = CategoryManager.getCategoryMap();
 
Inventory inventory = InventoryManager.getInventoryID(user, Integer.valueOf(inventoryid));  

String inittime = inventory.getIntime(); 

String remark = inventory.getRemark();

StringUtill.isNull(remark);
JSONObject  resu = JSONObject.fromObject(remark);
 
String starttime = resu.getString("starttime"); 
String endtimeH = resu.getString("endtime"); 
String endtime = TimeUtill.dataAdd(endtimeH,1);
List<InventoryMessage> list = inventory.getInventory(); 

Map<String,Integer> listcount = InventoryBranchMessageManager.getMapAnalyze(inventory.getInbranchid()+"",starttime,endtime); 

Map<String,InventoryBranch> invnetorymap = InventoryBranchManager.getmapType(inventory.getInbranchid()+"", "");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>文员派工页</title>
<link rel="stylesheet" href="../../css/jquery-ui.css"/>
<script type="text/javascript" src="../../js/jquery-ui.js"></script>
 
<link rel="stylesheet" type="text/css" rev="stylesheet" href="../../style/css/bass.css" />
<style type="text/css">
.fixedHead { 
position:fixed;
}  
 
*{
    margin:0;
    padding:0;
}
#table{  
    width:800px;
    table-layout:fixed ;
}

#th{  
    background-color:white;
    position:absolute; 
    width:800px; 
    height:30px;
    top:0;
    left:0;
}

td{
 width:150px; 
}

#th td{
width:200px; 
}

#wrap{
    clear:both;
    position:relative;
    padding-top:30px;
    overflow:auto;
    height:500px;
}

</style>
</head>

<body style="scoll:no">
  
<!--   头部开始   --> 
<script type="text/javascript" src="../../js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="../../js/common.js"></script>
<script type="text/javascript" src="../../js/calendar.js"></script> 
<script type="text/javascript">

function inventory(inventory,type){
	 if(type == 0 || type == 1){
		 window.location.href='inventorysearch.jsp?id='+inventory;
		 //window.open('inventorysearch.jsp?id='+inventory, 'abc', 'resizable:yes;dialogWidth:600px;dialogHeight:800px;dialogTop:0px;dialogLeft:center;scroll:no');
	 }else {
		 window.location.href='dingdanDetail.jsp?id='+inventory;
		 //window.open('dingdanDetail.jsp?id='+inventory, 'abc', 'resizable:yes;dialogWidth:600px;dialogHeight:800px;dialogTop:0px;dialogLeft:center;scroll:no');
	 }
} 

function println(){
	 
	 window.print();

}
function checkedd(type){
	$("#add").val(type);
	$("#form").submit();
}
</script>
<div style="position:fixed;width:100%;height:70px;">
<div >
  <jsp:include flush="true" page="../head.jsp">
  <jsp:param name="" value="" />
  </jsp:include>     
</div > 
   </div>  

<div style="height:70px;"> 
</div> 
<div class="weizhi_head">现在位置：调拨单   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
门店：<%=BranchService.getMap().get(inventory.getInbranchid()).getLocateName() %>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   开始时间:<%=starttime%>---结束时间:<%=endtime %>
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

<a href="javascript:history.go(-1);"><font style="color:blue;font-size:20px;" >返回</font></a>  
</div>  
 
<div id="wrap"> 
     
<form action="InventoryServlet" method="post"  id="form">
<input type="hidden" name="method" value="updatesubscribe"/>
<input type="hidden" name="id" value="<%=inventoryid%>"/>
<input type="hidden" name="add" id="add"  value="" />

<table  cellspacing="1" id="table" >
		<tr id="th">  
     			<td align="center">名称</td>
     			<td align="center">型号</td> 
     			<td align="center">账面库存</td> 
     			<td align="center">实际库存</td>
     			<td align="center">出库数量</td> 
     			<td align="center">调拨数量</td>
     			<td align="center">确认数量</td>
     			
		</tr>      
            <% 
            InventoryMessageComparator c = new InventoryMessageComparator();
             Collections.sort(list, c); 
            
             Iterator<InventoryMessage> it = list.iterator();
             while(it.hasNext()){
            	 InventoryMessage in = it.next();
            	// int  Anlycount = (null == in.getAnlycount())?"":in.getAnlycount();
            	// System.out.println(in.getAnlycount()); 
             
             %>
            	   <tr id="<%=in.getId() %>" class="asc">   
        			  <td align="center"><%=mapc.get(in.getCategoryId()).getName() %> </td>
        			   <td align="center"><%=in.getProductname() %> </td>
        			  <%
        			  //System.out.println(in.getProductname());
        			  //System.out.println(StringUtill.GetJson(invnetorymap));
        			  %>   
        			     
        			   <td align="center"><%=null == invnetorymap.get(in.getProductname())?"0":invnetorymap.get(in.getProductname()).getPapercount() %> </td>
        			   <td align="center"><%=null == invnetorymap.get(in.getProductname())?"0":invnetorymap.get(in.getProductname()).getRealcount() %> </td>
        			    <td align="center"><%= null == listcount.get(in.getProductname())?"0":listcount.get(in.getProductname())*(-1) %> </td>
        			   <td align="center"> 
        			    <input type="hidden" name="product" value="<%=in.getId() %>" />  
        			    <% if(UserManager.checkPermissions(user, Group.dealSend)) {%>
        			    <input type="text" name="real<%=in.getId() %>"  id="<%=in.getId() %>"  value="<%=in.getRealString() %>"   style="border:0; width:100px" /> 
        			     <%}else{ %>
        			       <%=in.getRealString() %> 
        			     <% } %>
        			    </td>
        			   <td align="center"> 
        			   <% if(user.getBranch().equals(inventory.getInbranchid()+"")){ %>
        			  <input type="text" name="<%=in.getId() %>"  id="<%=in.getId() %>"  value="<%=in.getPaperString() %>" style="border:0; width:100px" /> 
        			  <% }else { %>
        			    <%=in.getPaperString() %>
        			  <% }%>
        			  </td>
                       </tr>
            	  
            	   
            <%  }%>
		       
	   <tr class="asc" >
		    <td align="center"  colspan=7>   
		    <% 
		   if(UserManager.checkPermissions(user, Group.inventoryreserve,"w")){ 
			    if(UserManager.checkPermissions(user, Group.dealSend)){ %>
			    <input type="button"  style="background-color:;font-size:20px;" value="打印" onclick="checkedd('outbranch')"/>
			    <input type="button"  style="background-color:;font-size:20px;" value="保存修改" onclick="checkedd('branch')"/>
			    <%  
			    }else { 
			    %> 
			     <input type="button"  style="background-color:;font-size:20px;" value="调货提交" onclick="checkedd('inbranch')"/>
			     <%
			    }
		   }
		     %>
		    </td>
       </tr> 			
</table> 
</form>
  </div>

</body>
</html>
