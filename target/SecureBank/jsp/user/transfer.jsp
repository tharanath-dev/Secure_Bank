<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.securebank.model.Account" %>
<%@ page import="com.securebank.util.SessionUtil" %>
<%
    Account account = SessionUtil.getLoggedAccount(request);
    double minBal = account != null ? account.getMinimumBalance() : 0.0;
    double maxTransfer = account != null ? Math.max(0.0, account.getBalance() - minBal) : 0.0;
%>
<%@ include file="/jsp/common/header.jsp" %>

<div class="dashboard-layout">
    <aside class="sidebar">
        <div class="sidebar-title">Customer Menu</div>
        <ul class="sidebar-nav">
            <li><a href="<%= cp %>/user/dashboard">Account Overview</a></li>
            <li><a href="<%= cp %>/user/deposit">Deposit Funds</a></li>
            <li><a href="<%= cp %>/user/withdraw">Withdraw Funds</a></li>
            <li class="active"><a href="<%= cp %>/user/transfer">Transfer Money</a></li>
            <li><a href="<%= cp %>/user/transactions">Transaction History</a></li>
            <li><a href="<%= cp %>/user/statement">Download Statement</a></li>
            <li><a href="<%= cp %>/user/profile">Profile &amp; Security</a></li>
            <li><a href="<%= cp %>/user/logout">Sign Out</a></li>
        </ul>
    </aside>

    <section class="dashboard-content">
        <div class="card" style="max-width: 620px;">
            <div class="card-header">
                <h2>Fund Transfer (Peer-to-Peer)</h2>
            </div>

            <div style="background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 4px; padding: 1rem; margin-bottom: 1.5rem;">
                <div style="display: flex; justify-content: space-between; margin-bottom: 0.35rem;">
                    <span style="font-size: 0.85rem; color: #64748b;">From Account:</span>
                    <strong class="mono"><%= account != null ? account.getAccountNumber() : "-" %></strong>
                </div>
                <div style="display: flex; justify-content: space-between;">
                    <span style="font-size: 0.85rem; color: #64748b;">Max Transferable (after Min Balance):</span>
                    <strong style="color: #1e7e34;">Rs. <%= String.format("%.2f", maxTransfer) %></strong>
                </div>
            </div>

            <form action="<%= cp %>/user/transfer" method="post">
                <div class="form-group">
                    <label for="toAccountNumber" class="required">Beneficiary 12-Digit Account Number</label>
                    <input type="text" id="toAccountNumber" name="toAccountNumber" required maxlength="12"
                           value="<%= request.getAttribute("paramToAccount") != null ? request.getAttribute("paramToAccount") : "" %>"
                           placeholder="Enter 12-digit account number" class="mono">
                    <div class="input-help">Must be an active SecureBank registered account number.</div>
                </div>

                <div class="form-group">
                    <label for="amount" class="required">Transfer Amount (Rs.)</label>
                    <input type="number" step="0.01" min="1" id="amount" name="amount" required
                           value="<%= request.getAttribute("paramAmount") != null ? request.getAttribute("paramAmount") : "" %>"
                           placeholder="Enter transfer amount, e.g. 1500.00">
                </div>

                <div class="form-group">
                    <label for="remark">Payment Purpose / Description</label>
                    <input type="text" id="remark" name="remark" maxlength="150"
                           value="<%= request.getAttribute("paramRemark") != null ? request.getAttribute("paramRemark") : "" %>"
                           placeholder="e.g. Rent, Project dues, Medical">
                </div>

                <div class="actions-row">
                    <button type="submit" class="btn btn-primary">Authorize &amp; Transfer</button>
                    <a href="<%= cp %>/user/dashboard" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
    </section>
</div>

<%@ include file="/jsp/common/footer.jsp" %>

