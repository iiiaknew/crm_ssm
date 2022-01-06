<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <base href="<%=basePath%>"/>
    <meta charset="UTF-8">

    <link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>
    <link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet"/>

    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
    <script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

    <link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
    <script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination/en.js"></script>

    <script type="text/javascript">

        $(function () {
            // 为创建按钮绑定事件
            $("#addBtn").click(function (){
                $(".time").datetimepicker({
                    minView: "month",
                    language:  'zh-CN',
                    format: 'yyyy-mm-dd',
                    autoclose: true,
                    todayBtn: true,
                    pickerPosition: "bottom-left"
                });
                // 走后台 拿用户 展示所有者
                $.ajax({
                    url : "workbench/activity/getUserList.do",
                    type : "get",
                    dataType : "json",
                    success : function (data){

                        var html = "<option></option>";
                        // data ：用户集合
                        $.each(data, function (i, n){
                            html += "<option value="+n.id+">"+n.name+"</option>";
                        })
                        $("#create-owner").html(html);

                        // 获取当前登录的用户，设置成默认所有者
                        var id = "${sessionScope.user.id}";
                        $("#create-owner").val(id);
                        // 打开模态窗口
                        $("#createActivityModal").modal("show");
                    }
                })
            })

            // 为保存按钮绑定事件
            $("#saveBtn").click(function (){
                $.ajax({
                    url : "workbench/activity/save.do",
                    data : {
                        "owner" : $.trim($("#create-owner").val()),
                        "name" : $.trim($("#create-name").val()),
                        "startDate" : $.trim($("#create-startDate").val()),
                        "endDate" : $.trim($("#create-endDate").val()),
                        "cost" : $.trim($("#create-cost").val()),
                        "description" : $.trim($("#create-description").val())
                    },
                    type : "post",
                    dataType : "json",
                    success : function (data){

                        if(data){
                            // 清空模态窗口数据
                            $("#activityAddForm")[0].reset();

                            $("#createActivityModal").modal("hide");
                            //pageList(1, 2);
                            /*
                            pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
				                    ,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
                            这段代码不用动
                            第一个参数  操作后停留在当前页
                            第二个参数  操作后页面维持展现记录数
                            在添加操作后回到首页，维持每页展现的记录数
                            * */
                            pageList(1, $("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
                        }else{
                            alert("添加失败");
                        }
                    }
                })

            })

            // 为查询按钮绑定事件
            $("#searchBtn").click(function (){
                // 点击查询按钮，把查询条件存到隐藏域
                $("#hidden-name").val($.trim($("#search-name").val()));
                $("#hidden-owner").val($.trim($("#search-owner").val()));
                $("#hidden-startDate").val($.trim($("#search-startDate").val()));
                $("#hidden-endDate").val($.trim($("#search-endDate").val()));

                pageList(1, 2);
            })

            // 为全选按钮绑定事件
            $("#qx").click(function (){
                $("input[name=xz]").prop("checked", this.checked);
            })

            // 反全选，
            // 动态生成的代码，不能使用普通的绑定事件的形式来做
            // 要用$(需要绑定元素的外层对象).on(绑定事件的方式,需要绑定的元素的jquery对象,回调函数)
            $("#activityBody").on("click", $("input[name=xz]"), function (){
                $("#qx").prop("checked", $("input[name=xz]").length == $("input[name=xz]:checked").length);
            })

            // 为删除按钮绑定事件
            $("#deleteBtn").click(function (){
                var $xz = $("input[name=xz]:checked");

                if($xz.length == 0){
                    alert("请选择要删除的记录");
                }else{
                    if(confirm("确定删除所选中的记录吗")){
                        var param = "";

                        for(var i=0; i<$xz.length; i++){
                            param += "id=" + $($xz[i]).val();
                            // 如果不是最后一项，加&
                            if(i<$xz.length-1){
                                param += "&";
                            }
                        }

                        $.ajax({
                            url : "workbench/activity/delete.do",
                            data : param,
                            type : "post",
                            dataType : "json",
                            success : function (data){
                                if(data){
                                    // pageList(1, 2);
                                    // 删除成功后，回到首页，维持每页记录数
                                    pageList(1, $("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
                                }else{
                                    alert("删除失败");
                                }
                            }
                        })
                    }
                }
            })

            // 为修改按钮添加事件
            $("#editBtn").click(function (){
                $(".time").datetimepicker({
                    minView: "month",
                    language:  'zh-CN',
                    format: 'yyyy-mm-dd',
                    autoclose: true,
                    todayBtn: true,
                    pickerPosition: "bottom-left"
                });
                var $xz = $("input[name=xz]:checked");
                if($xz.length == 0){
                    alert("请选择要修改的记录");
                }else if($xz.length > 1){
                    alert("只能选择一条记录进行修改");
                }else{
                    var id = $xz.val();
                    $("#edit-id").val(id);

                    $.ajax({
                        url : "workbench/activity/getUserListAndActivity.do",
                        data : {
                            "id" : id
                        },
                        type : "post",
                        dataType : "json",
                        success : function (data){
                            var html = "<option></option>";
                            $.each(data.uList, function (i, n){
                                html += "<option value="+n.id+">"+n.name+"</option>";
                            })
                            $("#edit-owner").html(html);
                            var id = "${sessionScope.user.id}";
                            $("#edit-owner").val(id);

                            $("#edit-name").val(data.a.name);
                            $("#edit-startDate").val(data.a.startDate);
                            $("#edit-endDate").val(data.a.endDate);
                            $("#edit-cost").val(data.a.cost);
                            $("#edit-description").val(data.a.description);

                            $("#editActivityModal").modal("show");
                        }
                    })
                }
            })

            // 为更新按钮添加事件
            $("#updateBtn").click(function (){

                $.ajax({
                    url : "workbench/activity/update.do",
                    data : {
                        "id" : $.trim($("#edit-id").val()),
                        "owner" : $.trim($("#edit-owner").val()),
                        "name" : $.trim($("#edit-name").val()),
                        "startDate" : $.trim($("#edit-startDate").val()),
                        "endDate" : $.trim($("#edit-endDate").val()),
                        "cost" : $.trim($("#edit-cost").val()),
                        "description" : $.trim($("#edit-description").val())
                    },
                    type : "post",
                    dataType : "json",
                    success : function (data){
                        if(data){
                            $("#editActivityModal").modal("hide");
                            //pageList(1, 2);
                            /*
                            pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
				                    ,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
                            这段代码不用动
                            第一个参数  操作后停留在当前页
                            第二个参数  操作后页面维持展现记录数
                            在修改操作后位置在当前页，维持每页展现的记录数
                            * */
                            pageList($("#activityPage").bs_pagination('getOption', 'currentPage'), $("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
                        }else{
                            alert("修改失败");
                        }
                    }
                })
            })

            pageList(1, 2);
        });

        function pageList(pageNo, pageSize){
            // 将隐藏域中的查寻条件 赋值到文本框
            $("#search-name").val($.trim($("#hidden-name").val()));
            $("#search-owner").val($.trim($("#hidden-owner").val()));
            $("#search-startDate").val($.trim($("#hidden-startDate").val()));
            $("#search-endDate").val($.trim($("#hidden-endDate").val()));

            $.ajax({
                url : "workbench/activity/pageList.do",
                data : {
                    "name" : $.trim($("#search-name").val()),
                    "owner" : $.trim($("#search-owner").val()),
                    "startDate" : $.trim($("#search-startDate").val()),
                    "endDate" : $.trim($("#search-endDate").val()),
                    "pageNo" : pageNo,
                    "pageSize" : pageSize
                },
                type : "get",
                dataType : "json",
                success : function (data){
                    // data : 市场活动列表  查询条数
                    var html = "";

                    $.each(data.dataList, function (i, n){
                        html += '<tr class="active">';
                        html += '<td><input type="checkbox" name="xz" value="'+n.id+'"/></td>';
                        html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id='+n.id+'\';">'+n.name+'</a></td>';
                        html += '<td>'+n.owner+'</td>';
                        html += '<td>'+n.startDate+'</td>';
                        html += '<td>'+n.endDate+'</td>';
                        html += '</tr>';
                    })

                    $("#activityBody").html(html);

                    // 计算总页数
                    var totalPages = data.total%pageSize==0 ? data.total/pageSize : parseInt(data.total/pageSize)+1;

                    // 数据处理完毕后 结合分页查询展现数据
                    $("#activityPage").bs_pagination({
                        currentPage: pageNo, // 页码
                        rowsPerPage: pageSize, // 每页显示的记录条数
                        maxRowsPerPage: 20, // 每页最多显示的记录条数
                        totalPages: totalPages, // 总页数
                        totalRows: data.total, // 总记录条数

                        visiblePageLinks: 3, // 显示几个卡片

                        showGoToPage: true,
                        showRowsPerPage: true,
                        showRowsInfo: true,
                        showRowsDefaultInfo: true,

                        onChangePage : function(event, data){
                            pageList(data.currentPage , data.rowsPerPage);
                        }
                    });
                }
            })
        }


    </script>
</head>
<body>
<input type="hidden" id="hidden-name" />
<input type="hidden" id="hidden-owner" />
<input type="hidden" id="hidden-startDate" />
<input type="hidden" id="hidden-endDate" />
<!-- 创建市场活动的模态窗口 -->
<div class="modal fade" id="createActivityModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 85%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
            </div>
            <div class="modal-body">

                <form id="activityAddForm" class="form-horizontal" role="form">

                    <div class="form-group">
                        <label for="create-owner" class="col-sm-2 control-label">所有者<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="create-owner">

                            </select>
                        </div>
                        <label for="create-name" class="col-sm-2 control-label">名称<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="create-name">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="create-startDate" class="col-sm-2 control-label">开始日期</label>
                        <div class="col-sm-10" style="width: 300px;"><input type="text" class="form-control time" id="create-startDate">
                        </div>
                        <label for="create-endDate" class="col-sm-2 control-label">结束日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control time" id="create-endDate">
                        </div>
                    </div>
                    <div class="form-group">

                        <label for="create-cost" class="col-sm-2 control-label">成本</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="create-cost">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="create-description" class="col-sm-2 control-label">描述</label>
                        <div class="col-sm-10" style="width: 81%;">
                            <textarea class="form-control" rows="3" id="create-description"></textarea>
                        </div>
                    </div>

                </form>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="saveBtn">保存</button>
            </div>
        </div>
    </div>
</div>

<!-- 修改市场活动的模态窗口 -->
<div class="modal fade" id="editActivityModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 85%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
            </div>
            <div class="modal-body">

                <form class="form-horizontal" role="form">
                    <%--隐藏域 储存修改市场活动的id--%>
                    <input type="hidden" id="edit-id">

                    <div class="form-group">
                        <label for="edit-owner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="edit-owner">
                                <%--<option>zhangsan</option>
                                <option>lisi</option>
                                <option>wangwu</option>--%>
                            </select>
                        </div>
                        <label for="edit-name" class="col-sm-2 control-label">名称<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-name" value="发传单">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-startDate" class="col-sm-2 control-label">开始日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control time" id="edit-startDate" value="2020-10-10">
                        </div>
                        <label for="edit-endDate" class="col-sm-2 control-label">结束日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control time" id="edit-endDate" value="2020-10-20">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-cost" class="col-sm-2 control-label">成本</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-cost" value="5,000">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-description" class="col-sm-2 control-label">描述</label>
                        <div class="col-sm-10" style="width: 81%;">
                            <textarea class="form-control" rows="3" id="edit-description">市场活动Marketing，是指品牌主办或参与的展览会议与公关市场活动，包括自行主办的各类研讨会、客户交流会、演示会、新产品发布会、体验会、答谢会、年会和出席参加并布展或演讲的展览会、研讨会、行业交流会、颁奖典礼等</textarea>
                        </div>
                    </div>

                </form>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="updateBtn">更新</button>
            </div>
        </div>
    </div>
</div>


<div>
    <div style="position: relative; left: 10px; top: -10px;">
        <div class="page-header">
            <h3>市场活动列表</h3>
        </div>
    </div>
</div>
<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
    <div style="width: 100%; position: absolute;top: 5px; left: 10px;">

        <div class="btn-toolbar" role="toolbar" style="height: 80px;">
            <form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">名称</div>
                        <input class="form-control" type="text" id="search-name">
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">所有者</div>
                        <input class="form-control" type="text" id="search-owner">
                    </div>
                </div>


                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">开始日期</div>
                        <input class="form-control" type="text"  id="search-startDate"/>
                    </div>
                </div>
                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">结束日期</div>
                        <input class="form-control" type="text"  id="search-endDate">
                    </div>
                </div>

                <button type="button" class="btn btn-default" id="searchBtn">查询</button>

            </form>
        </div>
        <div class="btn-toolbar" role="toolbar"
             style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
            <div class="btn-group" style="position: relative; top: 18%;">
                <button type="button" class="btn btn-primary" id="addBtn">
                    <span class="glyphicon glyphicon-plus"></span> 创建
                </button>
                <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改
                </button>
                <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
            </div>

        </div>
        <div style="position: relative;top: 10px;">
            <table class="table table-hover">
                <thead>
                <tr style="color: #B3B3B3;">
                    <td><input type="checkbox" id="qx"/></td>
                    <td>名称</td>
                    <td>所有者</td>
                    <td>开始日期</td>
                    <td>结束日期</td>
                </tr>
                </thead>
                <tbody id="activityBody">
                <%--<tr class="active">
                    <td><input type="checkbox"/></td>
                    <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                    <td>zhangsan</td>
                    <td>2020-10-10</td>
                    <td>2020-10-20</td>
                </tr>
                <tr class="active">
                    <td><input type="checkbox"/></td>
                    <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                    <td>zhangsan</td>
                    <td>2020-10-10</td>
                    <td>2020-10-20</td>
                </tr>--%>
                </tbody>
            </table>
        </div>

        <div style="height: 50px; position: relative;top: 30px;">
            <div id="activityPage"></div>
        </div>

    </div>

</div>
</body>
</html>