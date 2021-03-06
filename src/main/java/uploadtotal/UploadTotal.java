package uploadtotal;

import change.BranchTypeChange;
import utill.StringUtill;

public class UploadTotal {
   private int id ;
   private String name;
   private String type ;
   private String realtype;
   private String branchname;
   private String realbranchname;
   private int count ;
   private double totalcount;
   private double tatalbreakcount;
   private String pos ;
   
    
   
   
public String getPos() {
	return pos;
}
public void setPos(String pos) {
	this.pos = pos;
}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public String getBranchname() {
	return branchname;
}
public void setBranchname(String branchname) {
	this.branchname = branchname;
}
public int getCount() {
	return count;
}
public void setCount(int count) {
	this.count = count;
}
public double getTotalcount() {
	return totalcount;
}
public void setTotalcount(double totalcount) {
	this.totalcount = totalcount;
}
public double getTatalbreakcount() {
	return tatalbreakcount;
} 
public void setTatalbreakcount(double tatalbreakcount) {
	this.tatalbreakcount = tatalbreakcount;
}
public String getRealtype() { 
	realtype = BranchTypeChange.getinstance().getMap().get(type);
	if(StringUtill.isNull(realtype)){
		realtype =  type; 
	}
	return realtype;
}
public void setRealtype(String realtype) {
	this.realtype = realtype;
}  
public String getRealbranchname() { 
	realbranchname = BranchTypeChange.getinstance().getMap().get(branchname);
	if(StringUtill.isNull(realbranchname)){
		realbranchname = branchname;
	}
	return realbranchname;
} 
public void setRealbranchname(String realbranchname) {
	this.realbranchname = realbranchname;
}
   
   
   
   
}
