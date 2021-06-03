<%@include file="/common/taglib.jsp" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<c:url var="userEditURL" value="/quan-tri/user/edit"/>
<c:url var="userListURL" value="/quan-tri/user/list"/>
<c:url var="userAPI" value="/api/user"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>User list</title>
</head>

<body>
<div class="main-content">
    <form action="<c:url value='/quan-tri/user/list'/>" id="formSubmit" method="get">
        <div class="main-content-inner">
            <div class="page-content">
                <div class="row">
                    <div class="col-xs-12">
                        <div class="widget-box table-filter">
                            <div class="table-btn-controls">
                                <div class="pull-right tableTools-container">
                                    <div class="dt-buttons btn-overlap btn-group">
                                        <%-- <c:url var="createNewURL" value="/quan-tri/bai-viet/chinh-sua"/>
                                            <a flag="info" class="dt-button buttons-colvis btn btn-white btn-primary btn-bold" data-toggle="tooltip" title='Thêm bài viết' href='${createNewURL}'>
                                                        <span>
                                                            <i class="fa fa-plus-circle bigger-110 purple"></i>
                                                        </span>
                                            </a> --%>
                                        <button id="btnDelete" type="button" onclick="warningBeforeDelete()"
                                                class="dt-button buttons-html5 btn btn-white btn-primary btn-bold"
                                                data-toggle="tooltip" title='Xóa users'>
															<span>
																<i class="fa fa-trash-o bigger-110 pink"></i>
															</span>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="table-responsive">
                                    <table class="table table-bordered">
                                        <thead>
                                        <tr>
                                            <th><input type="checkbox" id="checkAll"></th>
                                            <th>UserName</th>
                                            <th>Password</th>
                                            <th>Role</th>
                                            <th>Actions</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <!-- Get het cac user trong table user để đổ vào userDTO, controller sẽ đổ ra view: -->
                                        <c:forEach var="item" items="${model.listResult}">
                                            <tr>
                                                <!-- value = id cua user  -->
                                                <!-- nó lặp, nên cần phân biệt id checkbox nào của user nào, do đó, dùng checkbox+id của user -->
                                                <td><input type="checkbox" id="checkbox_${item.id}" value="${item.id}">
                                                </td>
                                                <td>${item.userName}</td>
                                                <td>${item.password}</td>
                                                <td>${item.role}</td>
                                                <!-- Thêm Nút Chỉnh sửa/Cập nhật cho user -->
                                                <td>
                                                    <c:url var="userEditURL2" value="/quan-tri/user/edit">
                                                        <c:param name="id" value="${item.id}"/>
                                                    </c:url>
                                                    <a class="btn btn-sm btn-primary btn-edit" data-toggle="tooltip"
                                                       title="Update this user" href='${userEditURL2}'><i
                                                            class="fa fa-pencil-square-o" aria-hidden="true"></i>
                                                    </a>
                                                    <!-- <button id="btnDelete" type="button" onclick="warningBeforeDelete()"
                                                    class="dt-button buttons-html5 btn btn-white btn-primary btn-bold" data-toggle="tooltip" title='Xóa bài viết'>
                                                        <span>
                                                            <i class="fa fa-trash-o bigger-110 pink"></i>
                                                        </span>
                                                    </button> -->
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                    <!--  Phan trang 1 2 3 4 5 6 -->
                                    <div class="container">
                                        <nav aria-label="Page navigation">
                                            <ul class="pagination" id="pagination"></ul>
                                            <input type="hidden" value="" id="page" name="page">
                                            <input type="hidden" value="" id="limit" name="limit">
                                        </nav>
                                    </div>
                                    <!-- Ket thuc phan trang -->
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>
<!-- /.main-content -->
<script>
    var totalPages = ${model.totalPage};
    var currentPage = ${model.page};
    $(function () {
        window.pagObj = $('#pagination').twbsPagination({
            totalPages: totalPages,
            visiblePages: 10,
            startPage: currentPage,
            onPageClick: function (event, page) {
                // currentPage là page đang đứng hiện tại, page là page muốn chuyển tiếp
                if (currentPage != page) {
                    $('#limit').val(2);
                    $('#page').val(page);
                    $('#formSubmit').submit();
                }
            }
        }).on('page', function (event, page) {
            console.info(page + ' (from event listening)');
        });
    });

    // jquery
    function warningBeforeDelete() {
        swal({
            title: "Confirm delete",
            text: "Are you sure to delete?",
            type: "warning",
            showCancelButton: true,
            confirmButtonClass: "btn-success",
            cancelButtonClass: "btn-danger",
            confirmButtonText: "Confirm",
            cancelButtonText: "Cancel",
        }).then(function (isConfirm) {
            if (isConfirm) {
                // get tất cả ids, là 1 mảng và put vào biến var ids
                // check box nằm trong tbody, checked là những cái nào đc check rồi thì get
                var ids = $('tbody input[type=checkbox]:checked').map(function () {
                    return $(this).val();
                }).get();
                // put mang ids vao
                deleteUser(ids);
            }
        });
    }

    function deleteUser(data) {
        $.ajax({
            url: '${userAPI}',
            type: 'DELETE',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (result) {
                window.location.href = "${userListURL}?page=1&limit=2&message=delete_success";
                successPopup();
            },
            error: function (error) {
                window.location.href = "${userListURL}?page=1&limit=2&message=error_system";
                errorPopup();
            }
        });
    }

    function successPopup() {
        alert("Successful!");
    }

    function errorPopup() {
        alert("Error");
    }
</script>
</body>
</html>