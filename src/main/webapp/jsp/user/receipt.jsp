<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.securebank.model.Transaction" %>
<%@ page import="com.securebank.model.Account" %>
<%
    Transaction txn = (Transaction) request.getAttribute("transaction");
    Account account = (Account) request.getAttribute("account");
    Account.Receipt receipt = (Account.Receipt) request.getAttribute("receipt");
%>
<%@ include file="/jsp/common/header.jsp" %>

<div class="receipt-box">
    <div class="receipt-header">
        <h2 style="margin-bottom: 0.25rem;">SecureBank Transaction Receipt</h2>
        <div style="font-size: 0.85rem; color: #64748b;">Official Electronic Confirmation</div>
    </div>

    <div class="receipt-row">
        <span class="receipt-label">Transaction Reference:</span>
        <span class="receipt-val mono"><%= txn != null ? txn.getTransactionId() : "-" %></span>
    </div>

    <div class="receipt-row">
        <span class="receipt-label">Date &amp; Timestamp:</span>
        <span class="receipt-val"><%= txn != null ? txn.getFormattedDate() : "-" %></span>
    </div>

    <div class="receipt-row">
        <span class="receipt-label">Account Number:</span>
        <span class="receipt-val mono"><%= account != null ? account.getAccountNumber() : "-" %></span>
    </div>

    <div class="receipt-row">
        <span class="receipt-label">Account Type:</span>
        <span class="receipt-val"><%= account != null ? account.getAccountType() : "-" %></span>
    </div>

    <div class="receipt-row">
        <span class="receipt-label">Operation Type:</span>
        <span class="receipt-val" style="color: #0055b3;"><%= txn != null ? txn.getType() : "-" %></span>
    </div>

    <div class="receipt-row">
        <span class="receipt-label">Transaction Amount:</span>
        <span class="receipt-val" style="font-size: 1.15rem; color: #1e7e34;"><%= txn != null ? txn.getFormattedAmount() : "Rs. 0.00" %></span>
    </div>

    <div class="receipt-row">
        <span class="receipt-label">Updated Ledger Balance:</span>
        <span class="receipt-val mono"><%= txn != null ? txn.getFormattedBalanceAfter() : "Rs. 0.00" %></span>
    </div>

    <% if (txn != null && txn.getRemark() != null && !txn.getRemark().isEmpty()) { %>
        <div class="receipt-row">
            <span class="receipt-label">Narration / Remark:</span>
            <span class="receipt-val"><%= txn.getRemark() %></span>
        </div>
    <% } %>

    <% if (receipt != null) { %>
        <div style="margin-top: 1.25rem; padding: 0.75rem; background: #f8fafc; border: 1px dashed #cbd5e1; border-radius: 4px; font-size: 0.8rem; color: #475569;">
            <strong>Inner Class Receipt Output:</strong><br>
            <code><%= receipt.getFormattedDetails() %></code>
        </div>
    <% } %>

    <div class="actions-row" style="justify-content: center; margin-top: 2rem;">
        <button onclick="window.print()" class="btn btn-secondary">Print Receipt</button>
        <a href="<%= cp %>/user/dashboard" class="btn btn-primary">Return to Dashboard</a>
    </div>
</div>

<%@ include file="/jsp/common/footer.jsp" %>

