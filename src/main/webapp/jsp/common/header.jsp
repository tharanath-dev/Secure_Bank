<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.securebank.model.Person" %>
<%@ page import="com.securebank.model.Account" %>
<%@ page import="com.securebank.util.SessionUtil" %>
<%
    String cp = request.getContextPath();
    Person loggedUser = SessionUtil.getLoggedUser(request);
    Account loggedAccount = SessionUtil.getLoggedAccount(request);
    String userRole = (String) session.getAttribute(SessionUtil.SESSION_ROLE);

    // Retrieve flash messages from query params or request attributes
    String successMsg = request.getParameter("success");
    if (successMsg == null) {
        successMsg = (String) request.getAttribute("successMessage");
    }
    String errorMsg = request.getParameter("error");
    if (errorMsg == null) {
        errorMsg = (String) request.getAttribute("errorMessage");
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SecureBank – Banking Management System</title>
    <link rel="stylesheet" href="<%= cp %>/css/style.css">
</head>
<body>

<header class="site-header">
    <div class="header-container">
        <div class="brand-logo">
            <a href="<%= cp %>/">SecureBank</a>
            <span class="brand-tagline">Reliable Banking Management</span>
        </div>
        <nav class="header-nav">
            <a href="<%= cp %>/">Home</a>
            <% if (loggedUser == null) { %>
                <a href="<%= cp %>/user/login">User Login</a>
                <a href="<%= cp %>/user/register" class="nav-highlight">Register Account</a>
                <a href="<%= cp %>/admin/login">Admin Portal</a>
            <% } else if (Person.ROLE_USER.equalsIgnoreCase(userRole)) { %>
                <a href="<%= cp %>/user/dashboard">Dashboard</a>
                <a href="<%= cp %>/user/deposit">Deposit</a>
                <a href="<%= cp %>/user/withdraw">Withdraw</a>
                <a href="<%= cp %>/user/transfer">Transfer</a>
                <a href="<%= cp %>/user/transactions">History</a>
                <a href="<%= cp %>/user/profile">Profile</a>
                <span class="user-badge"><%= loggedUser.getName() %></span>
                <a href="<%= cp %>/user/logout" class="nav-highlight">Logout</a>
            <% } else if (Person.ROLE_ADMIN.equalsIgnoreCase(userRole)) { %>
                <a href="<%= cp %>/admin/dashboard">Admin Dashboard</a>
                <a href="<%= cp %>/admin/customers">Customer Directory</a>
                <a href="<%= cp %>/admin/customers/add">+ Add Customer</a>
                <span class="user-badge">Admin: <%= loggedUser.getName() %></span>
                <a href="<%= cp %>/admin/logout" class="nav-highlight">Logout</a>
            <% } %>
        </nav>
    </div>
</header>

<main class="main-wrapper">

<% if (successMsg != null && !successMsg.trim().isEmpty()) { %>
    <div class="alert alert-success">
        <strong>Success:</strong> <%= successMsg %>
    </div>
<% } %>

<% if (errorMsg != null && !errorMsg.trim().isEmpty()) { %>
    <div class="alert alert-error">
        <strong>Attention:</strong> <%= errorMsg %>
    </div>
<% } %>

