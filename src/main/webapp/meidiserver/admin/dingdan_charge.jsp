<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>

<%@ include file="searchdynamic.jsp" %>

<%

    request.setCharacterEncoding("utf-8");

    List<Order> list = OrderManager.getOrderlist(user, Group.sencondDealsend, Order.charge, num, Page, sort, sear);
    count = OrderManager.getOrderlistcount(user, Group.sencondDealsend, Order.charge, num, Page, sort, sear);

    HashMap<Integer, User> usermap = UserManager.getMap();
//获取送货员    
    Map<Integer, List<Gift>> gMap = GiftManager.getOrderStatuesM(user);
    List<User> listS = UserManager.getUsers(user, Group.send);

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>订单管理</title>

    <link rel="stylesheet" type="text/css" rev="stylesheet" href="../style/css/bass.css"/>
    <style type="text/css">
        .fixedHead {
            position: fixed;
        }

        .tabled tr td {
            width: 50px
        }

        * {
            margin: 0;
            padding: 0;
        }

        td {
            width: 100px;
            line-height: 30px;
        }

        #table {
            width: 2100px;
            table-layout: fixed;
        }

        #th {
            background-color: #888888;
            position: absolute;
            width: 2100px;
            height: 30px;
            top: 0;
            left: 0;
        }

        #wrap {

            position: relative;
            padding-top: 30px;
            overflow: auto;
            height: 450px;
        }

    </style>
</head>

<body>

<script type="text/javascript" src="../js/common.js"></script>
<!--   头部开始   -->
<script type="text/javascript" src="../js/jquery-1.7.2.min.js"></script>
<script type="text/javascript">
    var id = "";
    var pages = "";
    var num = "";
    var oid = "<%=id%>";
    var pgroup = "<%=pgroup%>";
    var opstatues = "<%=opstatues%>";

    $(function () {
        $("#wrap").bind("scroll", function () {

            if (pre_scrollTop != ($("#wrap").scrollTop() || document.body.scrollTop)) {
                //滚动了竖直滚动条
                pre_scrollTop = ($("#wrap").scrollTop() || document.body.scrollTop);

                if (obj_th) {
                    obj_th.style.top = ($("#wrap").scrollTop() || document.body.scrollTop) + "px";
                }
            }
            else if (pre_scrollLeft != (document.documentElement.scrollLeft || document.body.scrollLeft)) {
                //滚动了水平滚动条
                pre_scrollLeft = (document.documentElement.scrollLeft || document.body.scrollLeft);
            }
        });

    });

    function func(str) {
        $(id).css("display", "none");
        $("#" + str).css("display", "block");
        id = "#" + str;
    }
    function funcc(str, str2) {
        $(id).css("display", "none");
        $("#" + str).css("display", "block");
        id = "#" + str;
        $.ajax({
            type: "post",
            url: "server.jsp",
            data: "method=dingdan&id=" + str2,
            dataType: "",
            success: function (data) {
                // window.location.href="senddingdan.jsp";
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                // alert(errorThrown);
            }
        });
    }

    function changes(str1) {
        $.ajax({
            type: "post",
            url: "server.jsp",
            data: "method=dingdaned&id=" + str1,
            dataType: "",
            success: function (data) {
                window.location.href = "dingdanpeidan2.jsp";
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                // alert(errorThrown);
            }
        });
    }

    function change(str1, str2) {
        var uid = $("#" + str1).val();
        $.ajax({
            type: "post",
            url: "../user/server.jsp",
            data: "method=peidan&id=" + str2 + "&uid=" + uid,
            dataType: "",
            success: function (data) {
                alert("设置成功");
                window.location.href = "dingdanpeidan2.jsp";
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                // alert(errorThrown);
            }
        });

    }

    function winconfirm() {
        var question = confirm("你确认要执行此操作吗？");
        if (question != "0") {
            var attract = new Array();
            var i = 0;

            $("input[type='checkbox']").each(function () {
                if ($(this).attr("checked")) {
                    var str = this.name;
                    if (str != null && str != "") {
                        attract[i] = str;
                        i++;
                    }
                }
            });

            $.ajax({
                type: "post",
                url: "server.jsp",
                data: "method=statuesinstall&id=" + attract.toString(),
                dataType: "",
                success: function (data) {
                    if (data == -1) {
                        alert("操作失败");
                        return;
                    } else if (data > 0) {
                        alert("操作成功");
                        window.location.href = "dingdan_charge.jsp";
                    }
                    ;
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    alert("服务器忙，请稍后重试");
                }
            });
        }
    }

    function orderPrint(id, statues) {
        $.ajax({
            type: "post",
            url: "server.jsp",
            data: "method=print2&id=" + id + "&statues=" + statues,
            dataType: "",
            success: function (data) {
                window.location.href = "printPaigong.jsp?id=" + id;
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                // alert(errorThrown);
            }
        });
    }

    function seletall(all) {
        if ($(all).attr("checked")) {
            $("input[type='checkbox']").each(function () {
                $(this).attr("checked", true);

            });
        } else if (!$(all).attr("checked")) {
            $("input[type='checkbox']").each(function () {
                $(this).attr("checked", false);
            });
        }
        ;
    }
</script>
<div style="position:fixed;width:100%;height:200px;">

    <jsp:include flush="true" page="head.jsp">
        <jsp:param name="dmsn" value=""/>
    </jsp:include>

    <jsp:include flush="true" page="page.jsp">
        <jsp:param name="searched" value="<%=sear %>"/>
        <jsp:param name="page" value="<%=pageNum %>"/>
        <jsp:param name="numb" value="<%=numb %>"/>
        <jsp:param name="sort" value="<%=sort %>"/>
        <jsp:param name="count" value="<%=count %>"/>
        <jsp:param name="type" value="<%=Order.pcharge%>"/>
    </jsp:include>

    <jsp:include page="search.jsp"/>

    <div class="btn">
        <input type="submit" class="button" name="dosubmit" value="确认" onclick="winconfirm()"></input>
    </div>

</div>

<div style=" height:170px;">
</div>


<br/>

<div id="wrap">
    <table cellspacing="1" id="table">
        <tr id="th">

            <td align="center"><input type="checkbox" value="" id="allselect" onclick="seletall(allselect)"></input>
            </td>
            <td align="center">单号</td>
            <td align="center">门店</td>
            <td align="center">销售员</td>
            <td align="center">顾客姓名</td>
            <td align="center">电话</td>
            <td align="center">送货名称</td>

            <td align="center">送货型号</td>
            <td align="center">送货数量</td>
            <td align="center">赠品</td>
            <td align="center">赠品数量</td>
            <td align="center">赠品状态</td>

            <td align="center">预约日期</td>
            <td align="center">送货地区</td>
            <td align="center">送货地址</td>
            <td align="center">送货状态</td>

            <td align="center">备注</td>
            <td align="center">送货人员</td>
            <td align="center">送货时间</td>
            <td align="center">送货是否已结款</td>
            <td align="center">安装人员</td>
            <td align="center">安装时间</td>

            <td align="center">先送货后安装</td>
            <td align="center">是否已回访</td>

        </tr>

        <tbody>
        <%
            if (null != list) {
                for (int i = 0; i < list.size(); i++) {
                    Order o = list.get(i);

                    String col = "";
                    if (i % 2 == 0) {
                        col = "style='background-color:yellow'";
                    }
        %>
        <tr id="<%=o.getId()+"ss" %>" class="asc" onclick="updateClass(this)">
            <td align="center" width="20"><input type="checkbox" value="" id="check_box" name="<%=o.getId() %>"></input>
            </td>
            <td align="center"><%=o.getPrintlnid() == null ? "" : o.getPrintlnid()%>
            </td>
            <td align="center"><%=o.getbranchName(o.getBranch())%>
            </td>
            <td align="center">


                <%=usermap.get(o.getSaleID()).getUsername() + "</p>" + usermap.get(o.getSaleID()).getPhone() %>

            </td>
            <td align="center"><%=o.getUsername() %>
            </td>
            <td align="center"><%=o.getPhone1()%>
            </td>
            <td align="center"><%= o.getCategory(0, "</p>")%>
            </td>
            <td align="center"><%=o.getSendType(0, "</p>")%>
            </td>
            <td align="center"><%= o.getSendCount(0, "</p>")%>
            </td>
            <td align="center"><%= o.getGifttype("</p>")%>
            </td>
            <td align="center"><%= o.getGifcount("</p>")%>
            </td>
            <td align="center"><%= o.getGifStatues("</p>")%>
            </td>

            <td align="center"><%=o.getOdate() %>
            </td>
            <td align="center"><%=o.getLocate()%>
            </td>
            <td align="center"><%=o.getLocateDetail() %>
            </td>
            <td align="center">
                <%=OrderManager.getDeliveryStatues(o.getDeliveryStatues()) %>
            </td>


            <td align="center">
                <%=o.getRemark() %>
            </td>

            <td align="center">
                <% if (o.getSendId() != 0) {
                %>
                <%=usermap.get(o.getSendId()).getUsername() %>
                <%
                    }
                %>

            </td>
            <td align="center">
                <%=o.getSendtime()
                %>

            </td>
            <td align="center">
                <%=o.getStatuesPaigong() == 1 ? "是" : "否"
                %>
            </td>
            <td align="center">
                <% if (o.getInstallid() != 0 || o.getInstallid() == 0 && o.getDeliveryStatues() == 2) {
                %>
                <%=o.getInstallid() != 0 ? usermap.get(o.getInstallid()).getUsername() : usermap.get(o.getSendId()).getUsername() %>
                <%
                    }
                %>

            </td>
            <td align="center">
                <%=o.getInstalltime()
                %>
            </td>

            <td align="center">
                <%=o.getDeliverytype() == 2 ? "是" : "否"
                %>
            </td>
            <td align="center">
                <%=o.getStatuescallback() == 0 ? "否" : "是" %>
            </td>
        </tr>
        <%
                }

            }
        %>
        </tbody>
    </table>


</div>


</body>
</html>
