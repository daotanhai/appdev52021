<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/common/taglib.jsp" %>
<%@ page import="com.laptrinhjavaweb.util.SecurityUtils" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<div id="navbar" class="navbar navbar-default ace-save-state">
    <div class="navbar-container ace-save-state" id="navbar-container">
        <div class="navbar-header pull-left">
            <a href="/training-staff/trang-chu" class="navbar-brand">
                <small>
                    <i class="fa fa-leaf"></i>
                    Training staff's home page
                </small>
            </a>
        </div>
        <div class="navbar-buttons navbar-header pull-right collapse navbar-collapse" role="navigation">
            <ul class="nav ace-nav">
                <li class="light-blue dropdown-modal">
                    <a data-toggle="dropdown" href="#" class="dropdown-toggle">
                       Hello, <%=SecurityUtils.getPrincipal().getFullName()%>
                    </a>
                <li class="light-blue dropdown-modal">
                    <a href='<c:url value='/thoat'/>'>
                        <i class="ace-icon fa fa-power-off"></i>
                        Logout
                    </a>
                </li>
                </li>
            </ul>
        </div>
    </div>
</div>