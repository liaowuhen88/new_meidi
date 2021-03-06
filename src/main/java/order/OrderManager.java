package order;

import database.DB;
import gift.GiftManager;
import group.Group;
import orderPrint.OrderPrintln;
import orderPrint.OrderPrintlnManager;
import orderproduct.OrderProduct;
import orderproduct.OrderProductManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import user.User;
import user.UserManager;
import utill.DBUtill;
import utill.StringUtill;
import utill.TimeUtill;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class OrderManager {
    public static SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected static Log logger = LogFactory.getLog(OrderManager.class);

    public static int updateMessage(String phone1, String andate, String locations, String POS, String sailId, String check, String oid, String remark, String saledate, String diqu) {
        int flag = -1;
        List<String> sqls = new ArrayList<String>();

        if (StringUtill.isNull(andate)) {
            andate = null;
        } else {
            andate = "'" + andate + "'";
        }
        String sql = "update mdorder set phone1= '" + phone1 + "' , andate = " + andate + " , locateDetail = '" + locations + "', pos = '" + POS + "', sailId = '" + sailId + "' ,checked ='" + check + "' , remark = '" + remark + "' ,saledate = '" + saledate + "' ,locates = '" + diqu + "' where id = " + oid;

        String sql1 = "update mdorder set posRemark = 0 where pos != '" + POS + "' and id = " + oid;

        String sql2 = "update mdorder set checkedremark = 0 where checked != '" + check + "' and id = " + oid;

        String sql3 = "update mdorder set sailIdremark = 0 where sailId != '" + sailId + "' and id = " + oid;


        sqls.add(sql1);
        sqls.add(sql2);
        sqls.add(sql3);
        sqls.add(sql);
        DBUtill.sava(sqls);
        flag = 1;
        return flag;
    }

    public static int updateMessage(String phone1, String andate, String locations, String oid) {
        int flag = -1;
        Connection conn = DB.getInstance().getConn();
        if (StringUtill.isNull(andate)) {
            andate = null;
        }
        //insert into  mdgroup( id ,groupname, detail,statues, permissions, products) VALUES (null,?,?,?,?,?)";
        String sql = "update mdorder set phone1= ? , andate = ? where id = " + oid;
        PreparedStatement pstmt = DB.prepare(conn, sql);
        try {
            pstmt.setString(1, phone1);
            pstmt.setString(2, andate);

            logger.info(pstmt);
            flag = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(pstmt);
            DB.close(conn);
        }
        return flag;
    }

    public static String getDeliveryStatues(Order o) {
        return getDeliveryStatues(o.getDeliveryStatues(), o.getOderStatus());
    }

    public static String getDeliveryStatues(int statues, String oderStatus) {

        String str = "";
        String remark = "";
        if ((20 + "").equals(oderStatus)) {
            remark = "换货单";
        }
        // 0 表示未送货  1 表示正在送  2 送货成功
        if (0 == statues) {
            if ((20 + "").equals(oderStatus)) {
                str = "换货单";
            } else {
                str = "需派送";
            }
        } else if (1 == statues) {
            str = "已送货" + remark;

        } else if (2 == statues) {

            str = "已安装" + remark;

        } else if (3 == statues || 11 == statues || 13 == statues || 12 == statues) {
            str = "已退货";

        } else if (4 == statues) {

            str = "已送货退货 ";

        } else if (5 == statues) {

            str = "已安装退货";

        } else if (8 == statues) {

            str = "已自提 ";

        } else if (9 == statues) {

            str = "只安装(门店提货)";


        } else if (10 == statues) {

            str = "只安装(顾客已提) ";

        }


        return str;


    }

    public static String getOrderStatues(Order o) {
        int statues = Integer.valueOf(o.getOderStatus());
        String str = "";
        // 0 表示未送货  1 表示正在送  2 送货成功
        if (0 == statues) {
            str = "需配送";
        } else if (8 == statues) {
            str = "已自提 ";
        } else if (9 == statues) {
            str = "只安装(门店提货)";
        } else if (10 == statues) {
            str = "只安装(顾客已提) ";
        } else if (20 == statues) {
            str = "换货单";
        }
        return str;


    }

    public static Map<String, String> getDeliveryStatuesMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(0 + "", "需配送安装");
        map.put(1 + "", "已送货");
        map.put(8 + "", "已自提 ");
        map.put(9 + "", "只安装(门店提货)");
        map.put(10 + "", "只安装(顾客已提) ");
        map.put(-1 + "", "调拨单");
        map.put(20 + "", "换货单");

        return map;


    }

    // 确认厂送票已回
    public static int updateStatues(User user, String method, String statues, String id) {

        List<String> listsql = new ArrayList<String>();
        String[] listt = id.split(",");
        if (listt.length < 2) {
            for (int j = 0; j < listt.length; j++) {
                String idd = listt[j];
                List<OrderPrintln> list = OrderPrintlnManager.getOrderPrintlnbyOrderid(Integer.valueOf(idd));

                for (int i = 0; i < list.size(); i++) {
                    OrderPrintln o = list.get(i);
                    if (o.getType() == OrderPrintln.modify && o.getStatues() != 4 && !"tuihuo".equals(method) && !"print4".equals(method) && !"orderCome".equals(method) && !"orderGo".equals(method) && !"orderCharge".equals(method) && !"orderover".equals(method) && !"statuescallback".equals(method)) {
                        //logger.info(1);
                        return OrderPrintln.modify;
                    } else if (o.getType() == OrderPrintln.returns && o.getStatues() != 4 && !"tuihuo".equals(method) && !"print4".equals(method) && !"orderCome".equals(method) && !"orderGo".equals(method) && !"orderCharge".equals(method) && !"orderover".equals(method) && !"statuescallback".equals(method)) {
                        //logger.info(1);
                        return 20;
                    }
                }

            }
        }
        //logger.info(1);

        int flag = -1;

        if (id.startsWith(",")) {
            id = id.substring(1);
        }
        String ids = "(" + id + ")";
        String sql = "";
        logger.info(method);
        if (UserManager.checkPermissions(user, Group.callback, "w") && "wenyuancallback".equals(method)) {
            logger.info(1);
            sql = "update mdorder set wenyuancallback = " + statues + " where id in " + ids;
        } else if (UserManager.checkPermissions(user, Group.over, "w") && "orderCharge".equals(method)) {
            if (StringUtill.isNull(statues)) {
                statues = TimeUtill.getdateString();
            } else {
                if ("0".equals(statues)) {
                    statues = "";
                }
            }
            ///sql = "update mdorder set statues3 = "+statues+" where id in " + ids;
            sql = "update mdorder set statuesChargeSale = '" + statues + "' where id in " + ids;
        } else if (UserManager.checkPermissions(user, Group.come, "w") && "orderCome".equals(method)) {
            sql = "update mdorder set statues1 = " + statues + " where id in " + ids;
        } else if (UserManager.checkPermissions(user, Group.go, "w") && "orderGo".equals(method)) {
            sql = "update mdorder set statues2 = " + statues + " where id in " + ids;
        } else if (UserManager.checkPermissions(user, Group.send, "w") && "print4".equals(method)) {
            sql = "update mdorder set returnwenyuan = " + statues + " where id in " + ids;
        } else if (UserManager.checkPermissions(user, Group.sencondDealsend, "w")) {
            logger.info(method);
            if ("statuescallback".equals(method)) {
                sql = "update mdorder set statuescallback = " + statues + " where id in " + ids;
            } else if ("statuespaigong".equals(method)) {
                sql = "update mdorder set statuespaigong = " + statues + "  , chargeSendtime = '" + TimeUtill.gettime() + "' where id in " + ids;
            } else if ("statuesinstall".equals(method)) {
                sql = "update mdorder set statuesinstall = " + statues + "  , chargeInstalltime = '" + TimeUtill.gettime() + "' where id in " + ids;
            } else if ("statuesinstalled".equals(method)) {
                statues = 2 + "";
                sql = "update mdorder set statuesinstall = " + statues + " , chargeInstalltime = '" + TimeUtill.gettime() + "'  where id in " + ids;
            }
        }

        listsql.add(sql);
        logger.info(listsql.toString());
        // statuesinstalled  // statuescallback
        if (DBUtill.sava(listsql)) {
            flag = 1;
        }
        ;
        return flag;
    }

    // 第二次配单

    public static boolean getName(String method, String c, String branch) {
        boolean flag = false;
        Connection conn = DB.getInstance().getConn();
        String sql = "";
        if ("phone1".equals(method)) {
            //sql = "select * from mdorder where "+method+"  = '"+ c+ "'  and orderbranch = '"+branch+"' and  (TIMESTAMPDIFF(DAY,saledate,now()) <= 5)";
            sql = "select * from mdorder where " + method + "  = '" + c + "'  and orderbranch = '" + branch + "'";
        } else {
            sql = "select * from mdorder where " + method + "  = '" + c + "'  and orderbranch = '" + branch + "' ";
        }

        Statement stmt = DB.getStatement(conn);
        ResultSet rs = DB.getResultSet(stmt, sql);
        try {
            while (rs.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(rs);
            DB.close(stmt);
            DB.close(conn);
        }
        return flag;
    }

    // 判断是不是顶码
    public static boolean Check(int oid) {
        boolean flag = false;
        Connection conn = DB.getInstance().getConn();

        String sql = "select * from mdorderproduct where statues = 1 and orderid = " + oid;

        Statement stmt = DB.getStatement(conn);
        ResultSet rs = DB.getResultSet(stmt, sql);
        try {
            while (rs.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(rs);
            DB.close(stmt);
            DB.close(conn);
        }
        return flag;
    }

    public static Order getMaxOrder() {
        String sql = "select * from mdorder order by id desc limit 1 ";
        List<Order> orders = getOrdersBySql(sql);
        return getFromList(orders);

    }

    public static boolean getid(int id) {
        boolean flag = false;
        if (0 == id) {
            return flag;
        }

        Connection conn = DB.getInstance().getConn();
        Statement stmt = DB.getStatement(conn);
        String sql = "select id from mdorder where id = " + id;
        ResultSet rs = DB.getResultSet(stmt, sql);
        try {
            while (rs.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(stmt);
            DB.close(rs);
            DB.close(conn);
        }
        return flag;
    }

    public static synchronized int save(User user, Order order) {
        int flag = -1;
        List<String> sqls = new ArrayList<String>();

        //boolean isflag = OrderManager.getid(order.getId());

        int maxid;
        int daymark;
        int dayID;
        String daymarkk = "";
        Order oldorder = OrderManager.getOrderID(user, order.getId());

        if (oldorder != null) {
            // 判断是否已配工，已配工返回
            if (oldorder.getDealsendId() != 0) {
                return flag;
            }
            maxid = oldorder.getId();
            daymark = oldorder.getDayremark();
            dayID = oldorder.getDayID();
            order.setPrintlnid(oldorder.getPrintlnid());
            //order.setSubmitTime(oldorder.getSubmitTime());
            OrderPrintln oor = OrderPrintlnManager.getOrderStatues(user, oldorder.getId(), 0);
            if (oldorder.getPrintSatues() == 1) {
                if (oor != null && oor.getStatues() != 2) {
                    return flag;
                }
            }

            order.setOderStatus(oldorder.getOderStatus());
            List<String> listd = OrderManager.update(maxid);

            sqls.addAll(listd);

        } else {
            oldorder = OrderManager.getMaxOrder();
            if (oldorder == null) {
                maxid = 1;
                daymark = 1;
                dayID = TimeUtill.getdate();
            } else {
                maxid = oldorder.getId() + 1;
                daymark = oldorder.getDayremark() + 1;
                if (TimeUtill.isWee_hours(oldorder.getDayID())) {
                    daymark = 1;
                }
                dayID = TimeUtill.getdate();
            }
            if (daymark < 10) {
                daymarkk = "00" + daymark;
            } else if (9 < daymark && daymark < 100) {
                daymarkk = "0" + daymark;
            } else {
                daymarkk = daymark + "";
            }


            if (maxid == 0) {
                maxid = 1;
            }

            String printlnid = "";
            if (order.getOderStatus().equals(20 + "")) {
                 /*String sql1 = "insert into  mdorderupdateprint (id, message ,statues , orderid,mdtype ,pGroupId,uid,groupid)" +
                         "  values ( null, '换货申请', 0,"+order.getImagerUrl()+","+OrderPrintln.huanhuo+","+user.getUsertype()+","+user.getId()+","+user.getUsertype()+")";

		    	 sqls.add(sql1);*/

                printlnid = "H" + order.getPrintlnid();

                order.setPrintlnid(printlnid);
            } else if (order.getOderStatus().equals(30 + "")) {

                printlnid = "T" + order.getPrintlnid();

                order.setPrintlnid(printlnid);
            } else {
                order.setPrintlnid(order.getPrintlnid() + "-" + daymarkk);
            }
        }

        List<String> sqlp = OrderProductManager.save(maxid, order);
        if (order.getOderStatus().equals(30 + "")) {
            sqlp = OrderProductManager.saveTuihuo(maxid, order);
        }
        List<String> sqlg = GiftManager.save(maxid, order);

        sqls.addAll(sqlp);
        sqls.addAll(sqlg);

        if (!StringUtill.isNull(order.getOdate())) {
            order.setOdate("'" + order.getOdate() + "'");
        } else {
            order.setOdate(null);
        }


        String sql = "insert into  mdorder ( id ,andate , saledate ,pos, username, locates" +
                ", locateDetail, saleID , printSatues ,oderStatus,sailId,checked,phone1,phone2,remark," +
                "deliveryStatues,orderbranch,sendId,statues1,statues2,statues3,dealSendid,submittime,printlnid,dayremark,dayID,phoneRemark,sailIdremark,checkedremark,posRemark,imagerUrl) values " +
                "( " + maxid + ", " + order.getOdate() + ", '" + order.getSaleTime() + "', '" + order.getPos() + "', '" + order.getUsername() + "', '"
                + order.getLocate() + "', '" + order.getLocateDetail() + "'," + order.getSaleID() + ", " + order.getPrintSatues()
                + ", " + order.getOderStatus() + ", '" + order.getSailId() + "', '" + order.getCheck() + "', '" + order.getPhone1() + "','" + order.getPhone2() + "','" + order.getRemark() + "'," + order.getDeliveryStatues() + ",'" + order.getBranch() + "',0," + order.getStatues1() + "," + order.getStatues2() + "," + order.getStatues3() + "," + order.getDealsendId() + ",'" + order.getSubmitTime() + "','" + order.getPrintlnid() + "'," + daymark + "," + dayID + "," + order.getPhoneRemark() + "," + order.getSailidrecked() + "," + order.getReckedremark() + "," + order.getPosremark() + "," + order.getImagerUrl() + ")";

        sqls.add(sql);
        logger.info(sql);

        if (DBUtill.sava(sqls)) {
            flag = maxid;
        }
        ;

        return flag;

    }

    public static List<Order> getOrderlist(User user, int type, int statues, int num, int page, String sort, String search) {

        boolean f = UserManager.checkPermissions(user, Group.Manger);

        boolean flag = UserManager.checkPermissions(user, type);

        String str = "";
        if (num != -1) {
            str = "  limit " + ((page - 1) * num) + "," + num;
        }
        String sql = "";

        //logger.info(f);
        if (f) {
            if (Group.send == type) {
                if (Order.serach == statues) {
                    sql = "select * from  mdorder where  deliveryStatues in (0,9,10)   and printSatuesp = 1  or  installid = " + user.getId() + " and deliveryStatues in (1,10,9)  and printSatuesp = 1    order by id  desc";
                }
            } else if (Group.sale == type) {
                if (Order.serach == statues) {
                    sql = "select * from  mdorder where  1 =1 " + str + " order by id desc ";
                }
            } else if (Group.dealSend == type) {
                if (Order.orderDispatching == statues) {
                    // sql = "select * from  mdorder  where  (dealSendid = 0   and sendId = 0 and printSatues = 0  and deliveryStatues not in (3,4,5)  and  ( mdorder.id in (select orderid from mdorderproduct where salestatues in (0,1,2,3)))  or id in (select orderid from mdorderupdateprint where statues = 0 and mdtype in (0 ,1,2,4) ))  "+search+"  order by  "+sort+"  limit " + ((page-1)*num)+","+ page*num ;
                    sql = "select * from  mdorder  where  (dealSendid = 0   and sendId = 0 and printSatues = 0  and andate is not null  and deliveryStatues not in (3,4,5,11,12,13) or id in (select orderid from mdorderupdateprint where statues = 0 and mdtype in (0 ,1,2,4,10) ))  " + search + "  order by  " + sort + str;
                }
                if (Order.repareorderDispatching == statues) {
                    // sql = "select * from  mdorder  where  (dealSendid = 0   and sendId = 0 and printSatues = 0  and deliveryStatues not in (3,4,5)  and  ( mdorder.id in (select orderid from mdorderproduct where salestatues in (0,1,2,3)))  or id in (select orderid from mdorderupdateprint where statues = 0 and mdtype in (0 ,1,2,4) ))  "+search+"  order by  "+sort+"  limit " + ((page-1)*num)+","+ page*num ;
                    sql = "select * from  mdorder  where  (dealSendid = 0   and sendId = 0 and printSatues = 0  and andate is null  and orderStatus not in (8) and deliveryStatues not in (3,4,5,11,12,13))  " + search + "  order by  " + sort + str;
                } else if (Order.neworder == statues) {
                    sql = "select * from  mdorder  where  dealSendid = 0   and sendId = 0 and printSatues = 0  and deliveryStatues not in (3,4,5,11,12,13)  and mdorder.id in (select orderid from mdorderproduct where salestatues in (0,1,2,3))   " + search + "  order by " + sort + " ";
                } else if (Order.motify == statues) {
                    sql = "select * from  mdorder  where  id in (select orderid from mdorderupdateprint where statues = 0 and mdtype = 0 )   " + search + "  order by " + sort;
                } else if (Order.returns == statues) {
                    sql = "select * from  mdorder  where  id in (select orderid from mdorderupdateprint where statues = 0 and mdtype = 1 )   " + search + "  order by " + sort;
                } else if (Order.release == statues) {
                    sql = "select * from  mdorder  where  id in (select orderid from mdorderupdateprint where statues = 0 and mdtype = 2  )   " + search + "  order by " + sort;
                } else if (Order.orderPrint == statues) {
                    sql = "select * from  mdorder  where  dealSendid != 0   and sendId = 0 and printSatues = 0  and deliveryStatues != 3    " + search + "  order by " + sort + str;
                } else if (Order.serach == statues) {
                    sql = "select * from  mdorder  where 1 =1 " + search + " order by " + sort + str;
                } else if (Order.charge == statues) {
                    sql = "select * from  mdorder  where statues1 = 1 and statues2 = 1 and statues3 = 0  " + search + " order by " + sort + str;
                } else if (Order.callback == statues) {
                    sql = "select * from  mdorder where  deliveryStatues in (2)  and wenyuancallback = 0 " + search + " order by " + sort + str;
                } else if (Order.come == statues) {
                    sql = "select * from  mdorder  where  statues1 = 0  " + search + " order by " + sort + str;
                } else if (Order.go == statues) {
                    sql = "select * from  mdorder  where  statues1 = 1 and statues2 = 0  " + search + " order by " + sort + str;
                } else if (Order.over == statues) {
                    sql = "select * from  mdorder  where dealSendid != 0   and printSatues = 1  and deliveryStatues not in (0,3)  and statues4 = 0  and oderStatus not in (30) " + search + "  order by " + sort + str;
                } else if (Order.dingma == statues) {
                    sql = "select * from  mdorder  where  id  in (select orderid from mdorderproduct where statues = 1 ) and statuesdingma = 0  " + search + "  order by " + sort + str;
                } else if (Order.deliveryStatuesTuihuo == statues) {
                    sql = "select * from  mdorder where  deliveryStatues in (3,4,5,11,12,13) and statues1 = 1 and statues2 = 1 and categoryID is null  " + search + "  order by  " + sort + str;
                }
            } else if (Group.sencondDealsend == type) {
                if (Order.orderDispatching == statues) {
                    sql = "select * from  mdorder where  ( dealSendid != 0   and printSatues = 1   and  printSatuesp = 0 and sendId = 0  and  deliveryStatues in (0,9)  and mdorder.id not in (select orderid from mdorderupdateprint where statues = 2 and mdtype = 6 )  or (mdorder.id in (select orderid from mdorderupdateprint where mdtype in (3,6,7) and pGroupId = " + user.getUsertype() + " and statues = 0 )) ) " + search + "  order by " + sort + str;
                } else if (Order.dispatch == statues) {
                    sql = "select * from  mdorder where   dealSendid != 0   and printSatues = 1   and  printSatuesp = 0 and sendId = 0  and  deliveryStatues in (0,9) and mdorder.id not in (select orderid from mdorderupdateprint where statues = 2 and mdtype = 6 )  " + search + "  order by " + sort + str;
                } else if (Order.release == statues) {
                    sql = "select * from  mdorder where  mdorder.id  in (select orderid from mdorderupdateprint where mdtype in (3,4,5) and pGroupId = " + user.getUsertype() + " )      " + search + "  order by " + sort + str;
                } else if (Order.porderDispatching == statues) {
                    sql = "select * from  mdorder where  dealSendid != 0   and printSatues = 1    and  installid = 0 and  deliveryStatues in (1,10)  and statuesinstall = 0  and returnid = 0  or (mdorder.id in (select orderid from mdorderupdateprint where mdtype in (3,4,5) and pGroupId = " + user.getUsertype() + " and statues = 0 ))  " + search + " order by " + sort + str;
                } else if (Order.installonly == statues) {
                    sql = "select * from  mdorder where  dealSendid != 0   and printSatues = 1    and  installid = 0 and  deliveryStatues in (1,10)  and statuesinstall = 0  " + search + " order by " + sort + str;
                } else if (Order.orderPrint == statues) {
                    sql = "select * from  mdorder where  sendId != 0  and  deliveryStatues = 0  and printSatuesp = 0  or  returnid != 0 and  returnprintstatues = 0  " + search + " order by " + sort + str;
                } else if (Order.porderPrint == statues) {
                    sql = "select * from  mdorder where  installid != 0 and  deliveryStatues = 1   and printSatuesp = 0  and returnstatues = 0 " + search + "  order by " + sort + str;
                } else if (Order.serach == statues) {
                    sql = "select * from  mdorder where  dealSendid != 0  and printSatues = 1  " + search + "  order by " + sort + str;
                } else if (Order.callback == statues) {
                    sql = "select * from  mdorder where  deliveryStatues in (2)  and statuescallback = 0 " + search + " order by " + sort + str;
                } else if (Order.charge == statues) {
                    sql = "select * from  mdorder where  deliveryStatues in (2,5) and  statuesinstall = 0   and statuescallback = 1  and deliverytype = 2  and  statuesinstall = 0 and oderStatus not in (30) " + search + " order by " + sort + str;
                } else if (Order.chargeall == statues) {
                    sql = "select * from  mdorder where  deliveryStatues in (2,5) and  statuesinstall = 0   and statuescallback = 1   and deliverytype = 1  and statuesinstall  = 0 and oderStatus not in (30) " + search + " order by " + sort + str;
                } else if (Order.pcharge == statues) {
                    sql = "select * from  mdorder where  deliverytype = 2  and  deliveryStatues in (1,2,4,5)  and statuespaigong  = 0  and oderStatus not in (30) " + search + " order by " + sort + str;
                } else if (Order.orderquery == statues) {
                    sql = "select * from  mdorder where  ( deliveryStatues in (0,9,10) and sendid != 0  or  installid  != 0  and deliveryStatues in (1,10,9)  or returnid != 0  and returnstatues =0 ) and printSatuesp = 1   order by " + sort + str;
                    //returnid = "+user.getId() + " and returnstatues =0  and returnprintstatues = 1
                }
            }
        } else {
            if (flag && Group.send == type) {
                if (!StringUtill.isNull(search)) {
                    sql = "select * from  mdorder where ( sendId = " + user.getId() + " or  installid = " + user.getId() + " ) " + search;
                } else {
                    if (Order.serach == statues) { // 待送货
                        sql = "select * from  mdorder where  sendId = " + user.getId() + " and deliveryStatues in (0,9)   and printSatuesp = 1  or  installid = " + user.getId() + " and deliveryStatues in (1,10)  and printSatuesp = 1   " + search + "  order by " + sort + str;
                    } else if (Order.orderDispatching == statues) {   // 待安装
                        sql = "select * from  mdorder where  installid = " + user.getId() + " and deliveryStatues in (1,10)" + search + "  order by " + sort + str;
                    } else if (Order.over == statues) {  // 已送货
                        sql = "select * from  mdorder where  ( sendId = " + user.getId() + " )  and deliveryStatues in (1) " + search + "  order by " + sort + str;
                    } else if (Order.returns == statues) { // 已安装
                        sql = "select * from  mdorder where  (sendId = " + user.getId() + " and installid = 0  or  installid = " + user.getId() + " )  and deliveryStatues in (2,5)    " + search + "  order by " + sort + str;
                    }
                }

            } else if (flag && Group.sale == type) {
                if (!StringUtill.isNull(search)) {
                    sql = "select * from  mdorder where  orderbranch = " + user.getBranch() + search + "  order by " + sort + str;
                } else {
                    if (Order.serach == statues) {
                        sql = "select * from  mdorder where  orderbranch = '" + user.getBranch() + "' and deliveryStatues in (0,9,10) " + search + "  order by " + sort + str;
                    } else if (Order.orderDispatching == statues) {
                        sql = "select * from  mdorder where  orderbranch = '" + user.getBranch() + "' and deliveryStatues in (1)  " + search + "  order by " + sort + str;
                    } else if (Order.over == statues) {
                        sql = "select * from  mdorder where  orderbranch = '" + user.getBranch() + "' and deliveryStatues in (2)  " + search + "  order by " + sort + str;
                    } else if (Order.returns == statues) {
                        sql = "select * from  mdorder where  orderbranch = '" + user.getBranch() + "' and deliveryStatues in (3,4,5,11,12,13)  " + search + "  order by " + sort + str;
                    } else if (Order.come == statues) {
                        sql = "select * from  mdorder where  orderbranch = '" + user.getBranch() + "' and deliveryStatues in (8)  " + search + "  order by " + sort + str;
                    }
                }

            } else if (flag && Group.tuihuo == type) {
                if (Order.unquery == statues) {  //
                    sql = "select * from  mdorder where  (returnid = " + user.getId() + " ) and returnstatues = 0    order by id  desc";
                } else {
                    sql = "select * from  mdorder where  (returnid = " + user.getId() + " ) and returnstatues = 2    order by id  desc";
                }
            } else if (flag && Group.dealSend == type) {
                String sqlstr = " 1 = 1 and mdorder.saleID in (select id from mduser where mduser.usertype in (select groupid from mdrelategroup where pgroupid = " + user.getUsertype() + "))";
                String sqlop = "select orderid from mdorderupdateprint where  groupid in ( select groupid from  mdrelategroup where  pgroupid = '" + user.getUsertype() + "') and statues = 0  and mdtype in (0 ,1,2,4,10) ";
                String sqlopp = "select orderid from mdorderupdateprint where  groupid in ( select groupid from  mdrelategroup where  pgroupid = '" + user.getUsertype() + "')";


                if (Order.orderDispatching == statues) {
                    sql = "select * from mdorder where   ( " + sqlstr + "  and  dealSendid = 0   and sendId = 0   and andate is not null  and printSatues = 0 and deliveryStatues not in (3,4,5,11,12,13)  and ( mdorder.id in (select orderid from mdorderproduct where salestatues in (0,1,2,3)))  or id in (" + sqlop + "))  " + search + " order by  " + sort + str;
                } else if (Order.repareorderDispatching == statues) {
                    sql = "select * from mdorder where   ( " + sqlstr + "  and  dealSendid = 0   and sendId = 0    and andate is null  and printSatues = 0 and deliveryStatues not in (3,4,5,11,12,13)  and ( mdorder.id in (select orderid from mdorderproduct where salestatues in (1,2,3))))  " + search + " order by  " + sort + str;
                } else if (Order.neworder == statues) {
                    sql = "select * from mdorder where   " + sqlstr + "  and  dealSendid = 0   and printSatues = 0 and deliveryStatues not in (3,4,5,11,12,13)   and mdorder.id in (select orderid from mdorderproduct where salestatues in (0,1,2,3))  " + search + " order by  " + sort + str;
                } else if (Order.motify == statues) {
                    sql = "select * from mdorder where   " + sqlstr + "  and   id in ( " + sqlopp + "  and statues = 0  and mdtype = 0 )   " + search + " order by  " + sort + str;
                } else if (Order.huanhuo == statues) {
                    sql = "select * from mdorder where   " + sqlstr + "  and   id in (" + sqlopp + "  and statues = 0  and mdtype = 10 )   " + search + " order by  " + sort + str;
                } else if (Order.returns == statues) {
                    sql = "select * from mdorder where   " + sqlstr + " and   id in (" + sqlopp + "  and statues = 0  and mdtype = 1 )   " + search + " order by  " + sort + str;
                } else if (Order.release == statues) {
                    sql = "select * from mdorder where   " + sqlstr + " and   id in (" + sqlopp + "  and statues = 0  and mdtype = 2  )   " + search + " order by  " + sort + str;
                } else if (Order.orderPrint == statues) {
                    sql = "select * from mdorder where " + sqlstr + "  and  dealSendid != 0  and printSatues = 0  and sendId = 0  and  deliveryStatues = 0  " + search + "  order by " + sort + str;
                } else if (Order.charge == statues) {
                    sql = "select * from mdorder where " + sqlstr + "  and statues1 = 1 and statues2 = 1 and statuesChargeSale is null   and oderStatus not in (20)  " + search + "  order by " + sort + str;
                } else if (Order.callback == statues) {
                    sql = "select * from mdorder where " + sqlstr + "   and deliveryStatues in (2)   and wenyuancallback = 0  " + search + " order by " + sort + str;
                } else if (Order.come == statues) {
                    sql = "select * from mdorder where " + sqlstr + "    and statues1 = 0  and oderStatus not in (20)  " + search + " order by " + sort + str;
                } else if (Order.go == statues) {
                    sql = "select * from mdorder where " + sqlstr + "    and statues1 = 1 and statues2 = 0   and oderStatus not in (20)  " + search + " order by " + sort + str;
                } else if (Order.over == statues) {
                    if (!StringUtill.isNull(search) && search.contains("dealSendid")) {
                        str = "";
                    }
                    sql = "select * from  mdorder where  " + sqlstr + "   and printSatues = 1 and (sendId != 0 and deliverytype = 2 or sendId != 0 and deliverytype = 1 and wenyuancallback = 1 or  installid != 0 and wenyuancallback = 1  ) and deliveryStatues not in (0,3,8,9,10)  and statues4 = 0  and oderStatus not in (30) " + search + " order by " + sort + str;
                } else if (Order.serach == statues) {
                    sql = "select * from  mdorder where " + sqlstr + "  " + search + "  order by " + sort + str;
                    //sql = "select * from  mdorder where "+sqlstr+"  "+search+"  or (mdorder.id in (select orderid from mdorderupdateprint where mdtype = 3 and pGroupId = "+ user.getUsertype()+ " ))  order by "+sort+"  desc limit " + ((page-1)*num)+","+ page*num;
                } else if (Order.dingma == statues) {
                    sql = "select * from  mdorder  where   " + sqlstr + "   and id  in (select orderid from mdorderproduct where statues = 1 )  and statuesdingma = 0 " + search + "  order by " + sort + str;
                } else if (Order.deliveryStatuesTuihuo == statues) {
                    sql = "select * from  mdorder where  deliveryStatues in (3,4,5,11,12,13) and statues1 = 1 and statues2 = 1 and categoryID is null " + search + "  order by " + sort + str;
                }   // dispatch
            } else if (Group.sencondDealsend == type) {
                if (Order.orderDispatching == statues) {
                    sql = "select * from  mdorder where  dealSendid = " + user.getId() + "  and  ( printSatues = 1 and printSatuesp = 0  and sendId = 0  and  deliveryStatues in (0,9) and mdorder.id not in (select orderid from mdorderupdateprint where statues = 2 and mdtype = 6 )  or (mdorder.id in (select orderid from mdorderupdateprint where mdtype in (3,6,7,8)  and statues = 0 )) )  " + search + " order by " + sort + str;
                } else if (Order.release == statues) {
                    sql = "select * from  mdorder where  dealSendid = " + user.getId() + "  and  mdorder.id in (select orderid from mdorderupdateprint where mdtype in (3,4,5) and pGroupId = " + user.getUsertype() + " )  " + search + " order by " + sort + str;
                } else if (Order.dispatch == statues) {
                    sql = "select * from  mdorder where  dealSendid = " + user.getId() + "  and printSatues = 1 and printSatuesp = 0  and sendId = 0  and  deliveryStatues in (0,9)  and mdorder.id not in (select orderid from mdorderupdateprint where statues = 2 and mdtype = 6 )  " + search + " order by " + sort + str;
                } else if (Order.porderDispatching == statues) {
                    //sql = "select * from  mdorder where  dealSendid = "+user.getId()+"  and ( printSatues = 1 and printSatuesp = 0    and installid = 0 and  deliveryStatues in (1,10)  and statuesinstall = 0  and returnid = 0  or (mdorder.id in (select orderid from mdorderupdateprint where mdtype in (3,4,5) and (pGroupId = "+ user.getUsertype()+ "  or groupid = "+user.getUsertype()+"  )and statues = 0 )) )"  + " order by "+sort+str;
                    sql = "select * from  mdorder where  dealSendid = " + user.getId() + "  and ( printSatues = 1 and printSatuesp = 0    and installid = 0 and  deliveryStatues in (1,10)  and statuesinstall = 0  and returnid = 0  or (mdorder.id in (select orderid from mdorderupdateprint where mdtype in (3,4,5) and statues = 0 )) )" + " order by " + sort + str;
                } else if (Order.installonly == statues) {
                    sql = "select * from  mdorder where  dealSendid = " + user.getId() + "  and printSatues = 1 and printSatuesp = 0    and installid = 0 and  deliveryStatues in (1,10)  and statuesinstall = 0  " + " order by " + sort + str;
                } else if (Order.orderPrint == statues) {
                    sql = "select * from  mdorder where  (sendId != 0  and printSatuesp = 0   and  deliveryStatues = 0  or  returnid != 0 and  returnprintstatues = 0 ) and  dealSendid = " + user.getId() + "  " + search + "  order by " + sort + str;
                } else if (Order.porderPrint == statues) {
                    sql = "select * from  mdorder where  dealSendid = " + user.getId() + "  and  installid != 0 and  deliveryStatues = 1  and printSatuesp = 0  and returnstatues = 0 " + search + "  order by " + sort + str;
                } else if (Order.serach == statues) {
                    sql = "select * from  mdorder where  dealSendid = " + user.getId() + "  and printSatues = 1 " + search + "  order by " + sort + str;
                } else if (Order.callback == statues) {
                    sql = "select * from  mdorder where  dealSendid = " + user.getId() + " and  deliveryStatues in (2,5)  and statuescallback = 0 " + search + " order by " + sort + str;
                } else if (Order.charge == statues) { //and deliverytype = 1
                    sql = "select * from  mdorder where  dealSendid = " + user.getId() + " and   deliveryStatues in (2,5)  and statuescallback = 1  and deliverytype = 2  and  statuesinstall = 0  and oderStatus not in (30) " + search + "  order by " + sort + str;
                } else if (Order.chargeall == statues) { //
                    sql = "select * from  mdorder where  dealSendid = " + user.getId() + " and   deliveryStatues in (2,5)  and statuescallback = 1  and deliverytype = 1  and statuesinstall  = 0 and oderStatus not in (30)   " + search + "  order by " + sort + str;
                } else if (Order.pcharge == statues) {
                    sql = "select * from  mdorder where  dealSendid = " + user.getId() + " and   deliveryStatues in (1,2,4,5)  and deliverytype = 2  and statuespaigong  = 0 and oderStatus not in (30) " + search + "  order by " + sort + str;
                } else if (Order.orderquery == statues) {
                    sql = "select * from  mdorder where  dealSendid = " + user.getId() + "  and ( deliveryStatues in (0,9,10)   and sendid != 0  or  installid != 0  and deliveryStatues in (1,10,9)  or returnid != 0  and returnstatues =0  )      " + search + "  order by " + sort + str;
                }
            } else if (Group.aftersalerepare == type) {
                if (Order.aftersalerepare == statues) {
                    sql = "select * from mdorder where deliveryStatues in (2) and id in (select orderid from mdorderproduct where issubmit is null)" + search + "  order by " + sort + str;
                }
            }
        }
        List<Order> orders = getOrdersBySql(sql);

        return orders;
    }

    public static int getOrderlistcount(User user, int type, int statues, int num, int page, String sort, String search) {

        boolean f = UserManager.checkPermissions(user, Group.Manger);

        boolean flag = UserManager.checkPermissions(user, type);

        // boolean flagSearch = UserManager.checkPermissions(user, type,"r");

        int count = 0;
        String sql = "";

        if (f) {
            if (Group.dealSend == type) {

                if (Order.orderDispatching == statues) {
                    sql = "select count(*) from  mdorder  where  (dealSendid = 0   and andate is not null  and sendId = 0 and printSatues = 0  and deliveryStatues not in (3,4,5,11,12,13)  and ( mdorder.id in (select orderid from mdorderproduct where salestatues in (0,1,2,3)))   or id in (select orderid from mdorderupdateprint where statues = 0 and mdtype in (0 ,1,2,4) ))  " + search;
                } else if (Order.repareorderDispatching == statues) {
                    sql = "select count(*) from  mdorder  where  (dealSendid = 0   and andate is null  and sendId = 0 and printSatues = 0  and deliveryStatues not in (3,4,5,11,12,13)  and ( mdorder.id in (select orderid from mdorderproduct where salestatues in (0,1,2,3))))  " + search;
                } else if (Order.neworder == statues) {
                    sql = "select count(*) from  mdorder  where  dealSendid = 0   and sendId = 0 and printSatues = 0  and deliveryStatues not in (3,4,5,11,12,13)  and mdorder.id in (select orderid from mdorderproduct where salestatues in (0,1,2,3))  " + search;
                } else if (Order.motify == statues) {
                    sql = "select count(*) from  mdorder  where  id in (select orderid from mdorderupdateprint where statues = 0 and mdtype = 0 )   " + search;
                } else if (Order.returns == statues) {
                    sql = "select count(*) from  mdorder  where  id in (select orderid from mdorderupdateprint where statues = 0 and mdtype = 1 )   " + search;
                } else if (Order.release == statues) {
                    sql = "select count(*) from  mdorder  where  id in (select orderid from mdorderupdateprint where statues = 0 and mdtype = 2  )   " + search;
                } else if (Order.callback == statues) {
                    sql = "select count(*) from  mdorder where  deliveryStatues in (2)  and wenyuancallback = 0 " + search;
                } else if (Order.orderPrint == statues) {
                    sql = "select count(*) from  mdorder  where  dealSendid != 0   and sendId = 0 and printSatues = 0  and deliveryStatues != 3    " + search;
                } else if (Order.serach == statues) {
                    sql = "select count(*) from  mdorder  where 1 =1 " + search;
                } else if (Order.charge == statues) {
                    sql = "select count(*) from  mdorder  where statues1 = 1 and statues2 = 1 and statues3 = 0  and oderStatus not in (20) " + search;
                } else if (Order.come == statues) {
                    sql = "select count(*) from  mdorder  where  statues1 = 0  and oderStatus not in (20) " + search;
                } else if (Order.go == statues) {
                    sql = "select count(*) from  mdorder  where  statues1 = 1 and statues2 = 0  and oderStatus not in (20) " + search;
                } else if (Order.over == statues) {
                    sql = "select count(*) from  mdorder  where dealSendid != 0   and printSatues = 1  and deliveryStatues not in (0,3) and oderStatus not in (30)  and statues4 = 0  " + search;
                } else if (Order.dingma == statues) {
                    sql = "select count(*) from  mdorder  where id  in (select orderid from mdorderproduct where statues = 1 )  and statuesdingma = 0  " + search;
                } else if (Order.deliveryStatuesTuihuo == statues) {
                    sql = "select count(*) from  mdorder where  deliveryStatues in (3,4,5,11,12,13)  and statues1 = 1 and statues2 = 1 and categoryID is null " + search;
                }
            } else if (Group.sencondDealsend == type) {
                if (Order.orderDispatching == statues) {
                    sql = "select count(*) from  mdorder where  dealSendid = " + user.getId() + "  and  ( printSatues = 1 and printSatuesp = 0  and sendId = 0  and  deliveryStatues in (0,9) and mdorder.id not in (select orderid from mdorderupdateprint where statues = 2 and mdtype = 6 )  or (mdorder.id in (select orderid from mdorderupdateprint where mdtype in (3,6,7,8)  and statues = 0 )) )  " + search;
                    //sql = "select count(*) from  mdorder where  ( dealSendid != 0   and printSatues = 1   and  printSatuesp = 0 and sendId = 0  and  deliveryStatues in (0,9)  and mdorder.id not in (select orderid from mdorderupdateprint where statues = 2 and mdtype = 6 )   or (mdorder.id in (select orderid from mdorderupdateprint where mdtype in (3,6,7,8) and pGroupId = "+ user.getUsertype()+ " and statues = 0 )) ) "+search;
                } else if (Order.release == statues) {
                    sql = "select count(*) from  mdorder where  mdorder.id  in (select orderid from mdorderupdateprint where mdtype in (3,4,5) and pGroupId = " + user.getUsertype() + " )      " + search;
                } else if (Order.dispatch == statues) {
                    sql = "select count(*) from  mdorder where   dealSendid != 0   and printSatues = 1   and  printSatuesp = 0 and sendId = 0  and  deliveryStatues in (0,9)   and mdorder.id not in (select orderid from mdorderupdateprint where statues = 2 and mdtype = 6 )  " + search;
                } else if (Order.porderDispatching == statues) {
                    sql = "select count(*) from  mdorder where  dealSendid != 0   and printSatues = 1    and  installid = 0 and  deliveryStatues in (1,10)  and statuesinstall = 0  and returnid = 0 or (mdorder.id in (select orderid from mdorderupdateprint where mdtype in (4,5) and pGroupId = " + user.getUsertype() + "  and statues = 0 )) " + search;
                } else if (Order.installonly == statues) {
                    sql = "select count(*) from  mdorder where  dealSendid != 0   and printSatues = 1    and  installid = 0 and  deliveryStatues in (1,10)  and statuesinstall = 0  " + search;
                } else if (Order.orderPrint == statues) {
                    sql = "select count(*) from  mdorder where  sendId != 0  and  deliveryStatues = 0  and printSatuesp = 0  " + search;
                } else if (Order.porderPrint == statues) {
                    sql = "select count(*) from  mdorder where  installid != 0 and  deliveryStatues = 1  and printSatuesp = 0 and returnstatues = 0 " + search;
                } else if (Order.serach == statues) {
                    sql = "select count(*) from  mdorder where  dealSendid != 0  and printSatues = 1  " + search;
                } else if (Order.callback == statues) {
                    sql = "select count(*) from  mdorder where  deliveryStatues in (2)  and statuescallback = 0 " + search;
                } else if (Order.charge == statues) { //and deliverytype = 1
                    sql = "select count(*) from  mdorder where  deliveryStatues in (2,5)  and statuescallback = 1   and deliverytype = 2  and  statuesinstall = 0 and oderStatus not in (30) " + search;
                } else if (Order.chargeall == statues) { //and deliverytype = 1
                    sql = "select count(*) from  mdorder where  deliveryStatues in (2,5)  and statuescallback = 1   and deliverytype = 1  and  statuesinstall = 0 and oderStatus not in (30) " + search;
                } else if (Order.pcharge == statues) {
                    sql = "select count(*) from  mdorder where  deliveryStatues in (1,2,4,5)  and deliverytype = 2   and statuespaigong  = 0 and oderStatus not in (30) " + search;
                } else if (Order.orderquery == statues) {
                    sql = "select count(*) from  mdorder where  ( deliveryStatues in (0,9,10)   and sendid != 0   or  installid != 0 and deliveryStatues in (1,10,9) or returnid != 0  and returnstatues =0 ) and printSatuesp = 1  ";
                }
            }
        } else {
            if (flag && Group.send == type) {
                sql = "select count(*) from  mdorder where sendId = " + user.getId();
            } else if (flag && Group.sale == type) {
                sql = "select count(*) from  mdorder where  orderbranch = '" + user.getBranch() + "' and  deliveryStatues= 0 order by saledate";
            } else if (flag && Group.dealSend == type) {
                String sqlstr = " 1 = 1 and mdorder.saleID in (select id from mduser where mduser.usertype in (select groupid from mdrelategroup where pgroupid = " + user.getUsertype() + "))";

                String sqlop = "select orderid from mdorderupdateprint where  groupid in ( select groupid from  mdrelategroup where  pgroupid = '" + user.getUsertype() + "') and statues = 0  and mdtype in (0 ,1,2,4,10) ";
                String sqlopp = "select orderid from mdorderupdateprint where  groupid in ( select groupid from  mdrelategroup where  pgroupid = '" + user.getUsertype() + "')";

                if (Order.orderDispatching == statues) {
                    sql = "select count(*) from mdorder where   (  " + sqlstr + "   and andate is not null  and  dealSendid = 0   and printSatues = 0 and deliveryStatues not in (3,4,5,11,12,13)  and sendId = 0   and ( mdorder.id in (select orderid from mdorderproduct where salestatues in (0,1,2,3)))   or id in (" + sqlop + " ))  " + search;
                } else if (Order.repareorderDispatching == statues) {
                    sql = "select count(*) from mdorder where   (  " + sqlstr + "   and andate is null  and  dealSendid = 0   and printSatues = 0 and deliveryStatues not in (3,4,5,11,12,13)  and sendId = 0   and ( mdorder.id in (select orderid from mdorderproduct where salestatues in (1,2,3))))  " + search;
                } else if (Order.neworder == statues) {
                    sql = "select count(*) from mdorder where    mdorder.saleID in (select id from mduser where mduser.usertype in (select id from mdgroup where pid = " + user.getUsertype() + ") )  and  dealSendid = 0   and printSatues = 0  and  deliveryStatues not in (3,4,5,11,12,13)  and mdorder.id in (select orderid from mdorderproduct where salestatues in (0,1,2,3))  " + search;
                } else if (Order.motify == statues) {
                    sql = "select count(*) from mdorder where   (  " + sqlstr + "   and   id in (" + sqlopp + "  and statues = 0  and mdtype = 0  ) )  " + search;
                } else if (Order.returns == statues) {
                    sql = "select count(*) from mdorder where   (  " + sqlstr + "   and   id in (" + sqlopp + "  and statues = 0  and mdtype = 1 ) )  " + search;
                } else if (Order.huanhuo == statues) {
                    sql = "select count(*) from mdorder where   (  " + sqlstr + "   and   id in (" + sqlopp + "  and statues = 0  and mdtype = 10 ) )  " + search;
                } else if (Order.release == statues) {
                    sql = "select count(*) from mdorder where   (  " + sqlstr + "   and   id in (" + sqlopp + "  and statues = 0  and mdtype = 2  ) )   " + search;
                } else if (Order.callback == statues) {
                    sql = "select count(*) from mdorder where  " + sqlstr + "   and deliveryStatues in (2)  and wenyuancallback = 0  " + search;
                } else if (Order.orderPrint == statues) {
                    sql = "select count(*) from mdorder where  " + sqlstr + "   and  dealSendid != 0  and printSatues = 0  and sendId = 0  and deliveryStatues != 3  " + search;
                } else if (Order.charge == statues) {
                    sql = "select count(*) from mdorder where  " + sqlstr + "   and statues1 = 1 and statues2 = 1 and statuesChargeSale is null  and oderStatus not in (20) " + search;
                } else if (Order.come == statues) {
                    sql = "select count(*) from mdorder where  " + sqlstr + "     and statues1 = 0 and oderStatus not in (20) " + search;
                } else if (Order.go == statues) {
                    sql = "select count(*) from mdorder where  " + sqlstr + "     and statues1 = 1 and statues2 = 0  and oderStatus not in (20) " + search;
                } else if (Order.over == statues) {
                    sql = "select count(*) from  mdorder where   " + sqlstr + "    and printSatues = 1 and (sendId != 0 and deliverytype = 2 or sendId != 0 and deliverytype = 1 and wenyuancallback = 1 or  installid != 0 and wenyuancallback = 1  ) and deliveryStatues not in (0,3,8,9,10)  and statues4 = 0 and oderStatus not in (30) " + search;
                    //sql = "select count(*) from  mdorder where   "+sqlstr+"    and printSatues = 1 and sendId != 0  and deliveryStatues not in (0,3)  and statues4 = 0  "+search;
                } else if (Order.serach == statues) {
                    sql = "select count(*) from  mdorder where  " + sqlstr + "   " + search;
                    //sql = "select * from  mdorder where mdorder.saleID in (select id from mduser where mduser.usertype in (select id from mdgroup where pid = "+user.getUsertype()+"))  "+search+"  or (mdorder.id in (select orderid from mdorderupdateprint where mdtype = 3 and pGroupId = "+ user.getUsertype()+ " ))  order by "+sort+"  desc limit " + ((page-1)*num)+","+ page*num;
                } else if (Order.dingma == statues) {
                    sql = "select count(*) from  mdorder  where  mdorder.saleID in  (select id from mduser where mduser.usertype in (select id from mdgroup where pid = " + user.getUsertype() + "))  and id  in (select orderid from mdorderproduct where statues = 1 )  and statuesdingma = 0 " + search;
                } else if (Order.deliveryStatuesTuihuo == statues) {
                    sql = "select count(*) from  mdorder where  deliveryStatues in (3,4,5,11,12,13) and statues1 = 1 and statues2 = 1 and categoryID is null " + search;
                }
            } else if (Group.sencondDealsend == type) {
                if (Order.orderDispatching == statues) {
                    sql = "select count(*) from  mdorder where  dealSendid = " + user.getId() + "  and  ( printSatues = 1 and printSatuesp = 0  and sendId = 0  and  deliveryStatues in (0,9) and mdorder.id not in (select orderid from mdorderupdateprint where statues = 2 and mdtype = 6 )  or (mdorder.id in (select orderid from mdorderupdateprint where mdtype in (3,6,7,8)  and statues = 0 )) )  " + search;
                    //sql = "select count(*) from  mdorder where   ( dealSendid = "+user.getId()+"  and printSatues = 1 and printSatuesp = 0  and sendId = 0  and  deliveryStatues in (0,9)  and mdorder.id not in (select orderid from mdorderupdateprint where statues = 2 and mdtype = 6 )   or (mdorder.id in (select orderid from mdorderupdateprint where mdtype in (3,6,7)  and statues = 0 )) )  "+search;
                } else if (Order.release == statues) {
                    sql = "select count(*) from  mdorder where  dealSendid = " + user.getId() + "  and  mdorder.id in (select orderid from mdorderupdateprint where mdtype in (3,4,5) and pGroupId = " + user.getUsertype() + " and statues != 4 )  " + search;
                } else if (Order.dispatch == statues) {
                    sql = "select count(*) from  mdorder where  dealSendid = " + user.getId() + "  and printSatues = 1 and printSatuesp = 0  and sendId = 0  and  deliveryStatues in (0,9) and mdorder.id not in (select orderid from mdorderupdateprint where statues = 2 and mdtype = 6 )   " + search;
                } else if (Order.porderDispatching == statues) {
                    sql = "select count(*) from  mdorder where  dealSendid = " + user.getId() + "  and printSatues = 1 and printSatuesp = 0    and installid = 0 and  deliveryStatues in (1,10)  and statuesinstall = 0  and returnid = 0 or (mdorder.id in (select orderid from mdorderupdateprint where mdtype in (4,5) and pGroupId = " + user.getUsertype() + "  and statues = 0 )) " + search;
                } else if (Order.installonly == statues) {
                    sql = "select count(*) from  mdorder where  dealSendid = " + user.getId() + "  and printSatues = 1 and printSatuesp = 0    and installid = 0 and  deliveryStatues in (1,10)  and statuesinstall = 0  ";
                } else if (Order.orderPrint == statues) {
                    sql = "select count(*) from  mdorder where  dealSendid = " + user.getId() + "    and  sendId != 0  and printSatuesp = 0   " + search;
                } else if (Order.porderPrint == statues) {
                    sql = "select count(*) from  mdorder where  dealSendid = " + user.getId() + "  and  installid != 0 and  deliveryStatues = 1  and printSatuesp = 0  and returnstatues = 0  " + search;
                } else if (Order.serach == statues) {
                    sql = "select count(*) from  mdorder where  dealSendid = " + user.getId() + "  and printSatues = 1 " + search;
                } else if (Order.callback == statues) {
                    sql = "select count(*) from  mdorder where  dealSendid = " + user.getId() + " and  deliveryStatues in (2)  and statuescallback = 0 " + search;
                } else if (Order.charge == statues) {
                    sql = "select count(*) from  mdorder where  dealSendid = " + user.getId() + " and  deliveryStatues in (2,5)  and statuescallback = 1   and deliverytype = 2  and  statuesinstall = 0 and oderStatus not in (30) " + search;
                } else if (Order.chargeall == statues) {
                    sql = "select count(*) from  mdorder where  dealSendid = " + user.getId() + " and  deliveryStatues in (2,5)  and statuescallback = 1   and deliverytype = 1  and  statuesinstall = 0 and oderStatus not in (30) " + search;
                } else if (Order.pcharge == statues) {
                    sql = "select count(*) from  mdorder where  dealSendid = " + user.getId() + " and  deliveryStatues in (1,2,4,5)  and deliverytype = 2   and statuespaigong  = 0  and oderStatus not in (30) " + search;
                } else if (Order.orderquery == statues) {
                    sql = "select count(*) from  mdorder where  dealSendid = " + user.getId() + "  and (deliveryStatues in (0,9,10)   and sendid != 0  or  installid != 0  and deliveryStatues in (1,10,9)  or returnid != 0  and returnstatues =0  )  and printSatuesp = 1    " + search;
                }
            } else if (Group.aftersalerepare == type) {
                if (Order.aftersalerepare == statues) {
                    sql = "select count(*) from mdorder where deliveryStatues in (2) and id in (select orderid from mdorderproduct where issubmit is null) " + search;
                }
            }


        }
        if ("".equals(sql)) {
            count = 0;
            return count;
        }
        logger.info(sql);

        Connection conn = DB.getInstance().getConn();
        Statement stmt = DB.getStatement(conn);
        ResultSet rs = DB.getResultSet(stmt, sql);
        try {
            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(stmt);
            DB.close(rs);
            DB.close(conn);
        }
        return count;
    }

    //wrote by wilsonlee 2014-11-21
    //未消单的Order
    public static List<Order> getUnConfirmedDBOrders(String time) {

        String sql = "select * from  mdorder  where statues1 = 1 and statues2 = 0 and statuesChargeSale is null and oderStatus not in (20)  and saledate <= '" + time + "'  order by orderbranch";

        List<Order> orders = getOrdersBySql(sql);

        return orders;

    }

    //wrote by wilsonlee 2014-11-21
    //未结款的Order
    public static List<Order> getUnCheckedDBOrders(String time) {
        String sql = "select * from  mdorder  where statues1 = 1 and statues2 = 1 and statuesChargeSale is null and oderStatus not in (20)  and saledate <= '" + time + "'  order by orderbranch";
        List<Order> orders = getOrdersBySql(sql);

        return orders;

    }

    //wrote by wilsonlee 2014-11-21
    //已经结款的Order
    public static List<Order> getCheckedDBOrders(String time) {
        //boolean flag = UserManager.checkPermissions(user, Group.dealSend);
        //flag = true;
        String sql = "select * from  mdorder  where statues1 = 1 and statues2 = 1 and statuesChargeSale is not null and oderStatus not in (20) and saledate <= '" + time + "'  order by orderbranch";
        List<Order> orders = getOrdersBySql(sql);
        return orders;
    }

    //wrote by wilsonlee
    //已经结款的Order
    public static List<Order> getCheckedDBOrders() {
        //boolean flag = UserManager.checkPermissions(user, Group.dealSend);
        //flag = true;
        List<Order> Orders = new ArrayList<Order>();

        String sql = "select * from  mdorder  where statues1 = 1 and statues2 = 1 and statues3 = 1 and oderStatus not in (20) order by orderbranch";
        List<Order> orders = getOrdersBySql(sql);
        return orders;
    }

    public static void main(String args[]) {
        List<Order> orders = getUnCheckedDBOrdersbyBranch(74 + "", "2014-09-06");
        Iterator<Order> it = orders.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().getId());
        }

    }

    //根据门店获取Order 2014-11-21
    public static List<Order> getUnConfirmedDBOrdersbyBranch(String branchid, String time) {

        String sql = "select * from  mdorder  where statues1 = 1 and statues2 = 0 and statuesChargeSale is null  and oderStatus not in (20) and orderbranch in (" + branchid + ")  and saledate <= '" + time + "' order by orderbranch";
        List<Order> orders = getOrdersBySql(sql);
        return orders;
    }

    //根据门店类别获取Order 2014-11-21
    public static List<Order> getUnConfirmedDBOrdersbyBranchType(String branchid, String time) {
        String sql = "select * from  mdorder  where statues1 = 1 and statues2 = 0 and statuesChargeSale is null and oderStatus not in (20)  and orderbranch in (select id from mdbranch where pid in ( " + branchid + ")) and saledate <= '" + time + "'  order by orderbranch";
        List<Order> orders = getOrdersBySql(sql);
        return orders;
    }

    //根据门店获取Order 2014-11-21
    public static List<Order> getUnCheckedDBOrdersbyBranch(String branchid, String time) {
        String sql = "select * from  mdorder  where statues1 = 1 and statues2 = 1 and statuesChargeSale is null  and oderStatus not in (20) and orderbranch in (" + branchid + ")  and saledate <= '" + time + "' order by orderbranch";
        List<Order> orders = getOrdersBySql(sql);
        return orders;
    }

    //根据门店类别获取Order 2014-11-21
    public static List<Order> getUnCheckedDBOrdersbyBranchType(String branchid, String time) {
        String sql = "select * from  mdorder  where statues1 = 1 and statues2 = 1 and statuesChargeSale is null and oderStatus not in (20)  and orderbranch in (select id from mdbranch where pid in ( " + branchid + ")) and saledate <= '" + time + "'  order by orderbranch";
        List<Order> orders = getOrdersBySql(sql);
        return orders;
    }

    //2014-11-21
    public static List<Order> getCheckedDBOrdersbyBranchType(String branchid, String time) {

        String sql = "select * from  mdorder  where statues1 = 1 and statues2 = 1 and statuesChargeSale is not null and oderStatus not in (20)  and orderbranch in (select id from mdbranch where pid in ( " + branchid + ")) and saledate <= '" + time + "' order by orderbranch ";
        List<Order> orders = getOrdersBySql(sql);
        return orders;
    }

    public static List<Order> getListByids(String ids) {

        String sql = "select * from  mdorder  where id in (" + ids + ")";
        List<Order> orders = getOrdersBySql(sql);
        return orders;
    }

    //2014-11-21
    public static List<Order> getCheckedDBOrdersbyBranch(String branchid, String time) {

        String sql = "select * from  mdorder  where statues1 = 1 and statues2 = 1 and statuesChargeSale is not null and oderStatus not in (20)  and orderbranch in ( " + branchid + ") and saledate <= '" + time + "'  order by orderbranch";
        List<Order> orders = getOrdersBySql(sql);
        return orders;
    }

    public static Order getOrderID(User user, int id) {
        String sql = "select * from  mdorder where id = " + id;
        List<Order> orders = getOrdersBySql(sql);
        return getFromList(orders);
    }

    public static Order getFromList(List<Order> orders) {
        if (null != orders && orders.size() > 0) {
            return orders.get(0);
        }
        return null;
    }
    public static Map<Integer, Order> getOrdermapByIds(User user, String id) {
        String sql = "select * from  mdorder where id in (" + id + ")";
        return getOrdersMapBySql(sql);
    }

    public static boolean delete(User user, int oid) {

        boolean flag = false;

        Order order = OrderManager.getOrderID(user, oid);

        if (order.getStatues2() == 1) {
            String sql = "update mdorder set deliveryStatues = 3 where id = " + order.getId();
            flag = DBUtill.sava(sql);
        } else {
            List<String> listsqls = new ArrayList<String>();
            String sqlp = OrderProductManager.delete(order.getId());
            String sqlg = GiftManager.delete(order.getId());
            String sqlop = OrderPrintlnManager.deleteByoid(order.getId());

            String sql = "delete from mdorder where id = " + order.getId();
            listsqls.add(sqlp);
            listsqls.add(sqlg);
            listsqls.add(sqlop);
            listsqls.add(sql);

            if (order.getOderStatus().equals(20 + "")) {
                String sql1 = " delete from mdorderupdateprint where orderid = " + order.getImagerUrl();
                listsqls.add(sql1);
            }
            if (listsqls.size() == 0) {
                return false;
            }

            flag = DBUtill.sava(listsqls);
        }
        return flag;
    }

    public static List<String> update(int id) {

        List<String> listsqls = new ArrayList<String>();

        String sqlop = OrderPrintlnManager.deleteByoid(id);

        String sql = "delete from mdorder where id = " + id;

        listsqls.add(sqlop);
        listsqls.add(sql);
        return listsqls;
    }

    public static void updateHPOS(User user, String ids) {

        String sql = "update mdorder set categoryID = 0 where id in " + ids;

        DBUtill.sava(sql);
    }

    // 添加H单号后更新状态

    public static boolean deleteed(int id) {
        boolean b = false;
        Connection conn = DB.getInstance().getConn();
        String sql = "update mdorder set deliveryStatues = 3 where id = " + id;
        Statement stmt = DB.getStatement(conn);
        try {
            DB.executeUpdate(stmt, sql);
            b = true;
            OrderPrintlnManager.delete(id, OrderPrintln.returns);
        } finally {
            DB.close(stmt);
            DB.close(conn);
        }
        return b;
    }

    public static int getShifangStatues(Order or) {

        int opstatues = -1;

        if (or.getDeliveryStatues() == 0 || or.getDeliveryStatues() == 9) {
            opstatues = OrderPrintln.salerelease;
        } else if (or.getDeliveryStatues() == 1 || or.getDeliveryStatues() == 10) {
            if (or.getInstallid() != 0) {
                opstatues = OrderPrintln.salereleaseanzhuang;
            }
            if (or.getReturnid() != 0) {
                opstatues = OrderPrintln.salereleasereturn;
            }
        } else if (or.getDeliveryStatues() == 2) {
            if (or.getReturnid() != 0) {
                opstatues = OrderPrintln.salereleasereturn;
            }
        }

        return opstatues;
    }

    public static List<Order> getOrdersBySql(String sql) {
        Map<Integer, Order> linkedHashMap = getOrdersMapBySql(sql);
        List<Order> list = new ArrayList<Order>();

        Iterator<Map.Entry<Integer, Order>> it = linkedHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Order> entity = it.next();
            list.add(entity.getValue());
        }
        return list;
    }

    public static Map<Integer, Order> getOrdersMapBySql(String sql) {
        sql = addJoinOrderProduct(sql);

        logger.info(sql);
        Connection conn = DB.getInstance().getConn();
        Statement stmt = DB.getStatement(conn);
        ResultSet rs = DB.getResultSet(stmt, sql);
        Map<Integer, Order> linkedHashMap = new LinkedHashMap();

        try {
            while (rs.next()) {
                Order p = gerOrderFromRs(rs);
                if (null != linkedHashMap.get(p.getId())) {
                    p = linkedHashMap.get(p.getId());
                } else {
                    linkedHashMap.put(p.getId(), p);
                }
                OrderProduct op = OrderProductManager.getOrderStatuesFromRs(rs);
                if (null == p.getOrderProduct()) {
                    p.setOrderProduct(new ArrayList<OrderProduct>());
                }
                p.getOrderProduct().add(op);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(stmt);
            DB.close(rs);
            DB.close(conn);
        }

        return linkedHashMap;
    }

    public static Order gerOrderFromRs(ResultSet rs) {
        Order p = null;
        try {
            p = new Order();
            p.setId(rs.getInt("mdorder.id"));
            p.setLocate(rs.getString("mdorder.locates"));
            p.setLocateDetail(rs.getString("mdorder.locateDetail"));
            p.setSaleTime(rs.getString("mdorder.saledate"));
            p.setSaleID(rs.getInt("mdorder.saleID"));
            p.setSendId(rs.getInt("mdorder.sendID"));
            p.setOdate(rs.getString("mdorder.andate"));
            p.setPhone1(rs.getString("mdorder.phone1"));
            p.setPhone2(rs.getString("mdorder.phone2"));
            p.setUsername(rs.getString("mdorder.username"));
            p.setPrintSatues(rs.getInt("mdorder.printSatues"));
            p.setDeliveryStatues(rs.getInt("mdorder.deliveryStatues"));
            p.setPos(rs.getString("mdorder.pos"));
            p.setSailId(rs.getString("mdorder.sailId"));
            p.setCheck(rs.getString("mdorder.checked"));
            p.setRemark(rs.getString("mdorder.remark"));
            p.setBranch(rs.getInt("mdorder.orderbranch"));
            p.setCategoryID(rs.getString("mdorder.categoryID"));
            p.setDealsendId(rs.getInt("mdorder.dealSendid"));
            p.setStatues1(rs.getInt("mdorder.statues1"));
            p.setStatues2(rs.getInt("mdorder.statues2"));
            p.setStatues3(rs.getInt("mdorder.statues3"));
            p.setStatues4(rs.getInt("mdorder.statues4"));
            p.setPrintSatuesP(rs.getInt("mdorder.printSatuesp"));
            p.setStatuesDingma(rs.getInt("mdorder.statuesdingma"));
            p.setInstallid(rs.getInt("mdorder.installid"));
            p.setPrintlnid(rs.getString("mdorder.printlnid"));
            p.setStatuescallback(rs.getInt("mdorder.statuescallback"));
            p.setStatuesPaigong(rs.getInt("mdorder.statuespaigong"));
            p.setDayremark(rs.getInt("mdorder.dayremark"));
            p.setDayID(rs.getInt("mdorder.dayID"));
            p.setPhoneRemark(rs.getInt("mdorder.phoneRemark"));
            p.setInstalltime(rs.getString("mdorder.installTime"));
            p.setSendtime(rs.getString("mdorder.sendTime"));
            p.setDeliverytype(rs.getInt("mdorder.deliverytype"));
            p.setPosremark(rs.getInt("mdorder.posRemark"));
            p.setSailidrecked(rs.getInt("mdorder.sailIdremark"));
            p.setReckedremark(rs.getInt("mdorder.checkedremark"));
            p.setStatuesinstall(rs.getInt("mdorder.statuesinstall"));
            p.setReturnid(rs.getInt("mdorder.returnid"));
            p.setSubmitTime(rs.getString("mdorder.submitTime"));
            p.setReturnstatuse(rs.getInt("mdorder.returnstatues"));
            p.setReturntime(rs.getString("mdorder.returntime"));
            p.setReturnprintstatues(rs.getInt("mdorder.returnprintstatues"));
            p.setReturnwenyuan(rs.getInt("mdorder.returnwenyuan"));
            p.setPrintdingma(rs.getInt("mdorder.printdingma"));
            p.setDealSendTime(rs.getString("mdorder.dealsendTime"));
            p.setWenyuancallback(rs.getInt("mdorder.wenyuancallback"));
            p.setOderStatus(rs.getString("mdorder.oderStatus"));
            p.setImagerUrl(rs.getString("mdorder.imagerUrl"));
            p.setChargeDealsendtime(rs.getString("mdorder.chargeDealsendtime"));
            p.setChargeSendtime(rs.getString("mdorder.chargeSendtime"));
            p.setChargeInstalltime(rs.getString("mdorder.chargeInstalltime"));
            p.setStatuesCharge(rs.getString("mdorder.statuesChargeSale"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

    private static String addJoinOrderProduct(String sql) {
        StringBuffer sb = new StringBuffer();
        if ("".equals(sql)) {
            return null;
        } else {
            sb.append(sql);
            if (!sql.contains("limit")) {
                sb.append(" limit 0,500");
            }
            sb.insert(0, "select * from ( ");
            sb.append(") mdorder JOIN mdorderproduct on mdorderproduct.orderid = mdorder.id ");
        }
        return sb.toString();
    }
}
