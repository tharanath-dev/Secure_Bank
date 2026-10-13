<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.securebank.model.Account" %>
<%@ page import="com.securebank.util.SessionUtil" %>
<%
    Account account = SessionUtil.getLoggedAccount(request);
%>
<%@ include file="/jsp/common/header.jsp" %>

<div class="dashboard-layout">
    <aside class="sidebar">
        <div class="sidebar-title">Customer Menu</div>
        <ul class="sidebar-nav">
            <li><a href="<%= cp %>/user/dashboard">Account Overview</a></li>
            <li class="active"><a href="<%= cp %>/user/deposit">Deposit Funds</a></li>
            <li><a href="<%= cp %>/user/withdraw">Withdraw Funds</a></li>
            <li><a href="<%= cp %>/user/transfer">Transfer Money</a></li>
            <li><a href="<%= cp %>/user/transactions">Transaction History</a></li>
            <li><a href="<%= cp %>/user/statement">Download Statement</a></li>
            <li><a href="<%= cp %>/user/profile">Profile &amp; Security</a></li>
            <li><a href="<%= cp %>/user/logout">Sign Out</a></li>
        </ul>
    </aside>

    <section class="dashboard-content">
        <div class="card" style="max-width: 600px;">
            <div class="card-header">
                <h2>Deposit Funds</h2>
            </div>

            <div style="background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 4px; padding: 1rem; margin-bottom: 1.5rem;">
                <div style="font-size: 0.85rem; color: #64748b;">Target Account</div>
                <div class="mono" style="font-size: 1.1rem; font-weight: 700; color: #0a2540;">
                    <%= account != null ? account.getAccountNumber() : "-" %> (<%= account != null ? account.getAccountType() : "" %>)
                </div>
                <div style="font-size: 0.85rem; color: #64748b; margin-top: 0.35rem;">
                    Current Available Balance: <strong><%= account != null ? account.getFormattedBalance() : "Rs. 0.00" %></strong>
                </div>
            </div>

            <form action="<%= cp %>/user/deposit" method="post">
                <div class="form-group">
                    <label for="amount" class="required">Deposit Amount (Rs.)</label>
                    <input type="number" step="0.01" min="1" id="amount" name="amount" required
                           value="<%= request.getAttribute("paramAmount") != null ? request.getAttribute("paramAmount") : "" %>"
                           placeholder="Enter deposit amount, e.g. 1000.00">
                    <div class="input-help">Amount must be greater than zero.</div>
                </div>

                <div class="form-group">
                    <label for="remark">Deposit Remark / Reference (Optional)</label>
                    <input type="text" id="remark" name="remark" maxlength="100"
                           value="<%= request.getAttribute("paramRemark") != null ? request.getAttribute("paramRemark") : "" %>"
                           placeholder="e.g. Salary, Cheque deposit, Cash">
                </div>

                <div class="actions-row">
                    <button type="submit" class="btn btn-primary">Confirm &amp; Deposit</button>
                    <a href="<%= cp %>/user/dashboard" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
    </section>
</div>

<%@ include file="/jsp/common/footer.jsp" %>

