<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.securebank.service.AdminService" %>
<%
    AdminService as = new AdminService();
    boolean setupNeeded = false;
    try {
        setupNeeded = as.isSetupNeeded();
    } catch (Exception ignored) {
    }
%>
<%@ include file="/jsp/common/header.jsp" %>

<div style="margin-bottom: 2rem; padding: 2.5rem 2rem; background: #ffffff; border: 1px solid #d1d7dc; border-radius: 4px; border-left: 5px solid #0a2540;">
    <h1 style="font-size: 2.2rem; margin-bottom: 0.75rem;">Welcome to SecureBank</h1>
    <p style="font-size: 1.1rem; color: #4a5568; max-width: 800px; margin-bottom: 1.5rem;">
        SecureBank is an enterprise-grade retail banking portal built entirely with core Java technologies:
        Java Servlets, JavaServer Pages (JSP), JDBC, and MySQL. It offers real-time transactions, account management,
        and strict role-based access control.
    </p>

    <% if (setupNeeded) { %>
        <div class="alert alert-info" style="margin-bottom: 1.5rem;">
            <strong>System Notice:</strong> No administrator account has been created yet.
            Please visit the <a href="<%= cp %>/admin/setup" style="font-weight: 600; text-decoration: underline;">Initial System Setup</a> page to provision the first admin.
        </div>
    <% } %>

    <div style="display: flex; gap: 1rem; flex-wrap: wrap;">
        <a href="<%= cp %>/user/register" class="btn btn-primary">Open a Bank Account</a>
        <a href="<%= cp %>/user/login" class="btn btn-secondary">Customer Sign In</a>
        <a href="<%= cp %>/admin/login" class="btn btn-secondary">Administrative Portal</a>
    </div>
</div>

<div class="stat-grid" style="grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));">
    <div class="card" style="margin-bottom: 0;">
        <h3 style="color: #0a2540; margin-bottom: 0.5rem;">Retail Accounts</h3>
        <p style="color: #4a5568; font-size: 0.95rem;">
            Open either a <strong>Savings Account</strong> (Rs. 1,000.00 minimum balance) or a <strong>Current Account</strong> (Rs. 5,000.00 minimum balance) with an automated 12-digit account number.
        </p>
        <a href="<%= cp %>/user/register" style="font-weight: 600;">Register Account &rarr;</a>
    </div>

    <div class="card" style="margin-bottom: 0;">
        <h3 style="color: #0a2540; margin-bottom: 0.5rem;">Instant Fund Transfers</h3>
        <p style="color: #4a5568; font-size: 0.95rem;">
            Execute secure deposits, withdrawals, and peer-to-peer transfers protected by JDBC ACID transactions with automatic rollback on errors.
        </p>
        <a href="<%= cp %>/user/login" style="font-weight: 600;">Sign in to Transfer &rarr;</a>
    </div>

    <div class="card" style="margin-bottom: 0;">
        <h3 style="color: #0a2540; margin-bottom: 0.5rem;">Audit &amp; Compliance</h3>
        <p style="color: #4a5568; font-size: 0.95rem;">
            Comprehensive audit trail logged via Java character streams, tokenized password recovery, and downloadable account statements in plain text format.
        </p>
        <a href="<%= cp %>/admin/login" style="font-weight: 600;">Admin Dashboard &rarr;</a>
    </div>
</div>

<%@ include file="/jsp/common/footer.jsp" %>

