<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.securebank.model.Customer" %>
<%@ page import="com.securebank.model.Account" %>
<%@ page import="com.securebank.model.Transaction" %>
<%@ page import="java.util.List" %>
<%
    Customer customer = (Customer) request.getAttribute("customer");
    Account account = (Account) request.getAttribute("account");
    @SuppressWarnings("unchecked")
    List<Transaction> recentTransactions = (List<Transaction>) request.getAttribute("recentTransactions");
%>
<%@ include file="/jsp/common/header.jsp" %>

<div class="dashboard-layout">
    <!-- Left Navigation Sidebar -->
    <aside class="sidebar">
        <div class="sidebar-title">Customer Menu</div>
        <ul class="sidebar-nav">
            <li class="active"><a href="<%= cp %>/user/dashboard">Account Overview</a></li>
            <li><a href="<%= cp %>/user/deposit">Deposit Funds</a></li>
            <li><a href="<%= cp %>/user/withdraw">Withdraw Funds</a></li>
            <li><a href="<%= cp %>/user/transfer">Transfer Money</a></li>
            <li><a href="<%= cp %>/user/transactions">Transaction History</a></li>
            <li><a href="<%= cp %>/user/statement">Download Statement</a></li>
            <li><a href="<%= cp %>/user/profile">Profile &amp; Security</a></li>
            <li><a href="<%= cp %>/user/logout">Sign Out</a></li>
        </ul>
    </aside>

    <!-- Main Content Area -->
    <section class="dashboard-content">
        <!-- Account Overview Card -->
        <div class="card">
            <div class="card-header">
                <div>
                    <h2>Welcome, <%= customer != null ? customer.getName() : "Customer" %></h2>
                    <div style="font-size: 0.85rem; color: #64748b; margin-top: 0.2rem;">
                        Account Number: <strong class="mono" style="color: #0a2540;"><%= account != null ? account.getAccountNumber() : "-" %></strong>
                    </div>
                </div>
                <div>
                    <% if (account != null && account.isActive()) { %>
                        <span class="badge badge-active">ACTIVE ACCOUNT</span>
                    <% } else { %>
                        <span class="badge badge-inactive">INACTIVE</span>
                    <% } %>
                </div>
            </div>

            <!-- Stats Grid for Balances and Limits -->
            <div class="stat-grid">
                <div class="stat-card stat-blue">
                    <div class="stat-label">Available Balance</div>
                    <div class="stat-value"><%= account != null ? account.getFormattedBalance() : "Rs. 0.00" %></div>
                    <div class="stat-sub">Real-time ledger balance</div>
                </div>

                <div class="stat-card">
                    <div class="stat-label">Account Classification</div>
                    <div class="stat-value" style="font-size: 1.3rem;">
                        <%= account != null ? account.getAccountType() : "STANDARD" %>
                    </div>
                    <div class="stat-sub">
                        Min. Balance: Rs. <%= account != null ? String.format("%.2f", account.getMinimumBalance()) : "0.00" %>
                    </div>
                </div>

                <div class="stat-card stat-green">
                    <div class="stat-label">Quick Actions</div>
                    <div style="display: flex; gap: 0.5rem; margin-top: 0.5rem;">
                        <a href="<%= cp %>/user/deposit" class="btn btn-primary btn-sm">Deposit</a>
                        <a href="<%= cp %>/user/withdraw" class="btn btn-secondary btn-sm">Withdraw</a>
                        <a href="<%= cp %>/user/transfer" class="btn btn-secondary btn-sm">Transfer</a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Recent Transactions Panel -->
        <div class="card">
            <div class="card-header">
                <h3>Recent Transactions (Last 5)</h3>
                <a href="<%= cp %>/user/transactions" style="font-size: 0.9rem; font-weight: 600;">View All Transactions &rarr;</a>
            </div>

            <div class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th>Date &amp; Time</th>
                            <th>Transaction ID</th>
                            <th>Type</th>
                            <th>Remark / Beneficiary</th>
                            <th class="text-right">Amount</th>
                            <th class="text-right">Balance After</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (recentTransactions == null || recentTransactions.isEmpty()) { %>
                            <tr>
                                <td colspan="6" class="empty-state">No transactions yet on this account.</td>
                            </tr>
                        <% } else { %>
                            <% for (Transaction t : recentTransactions) { %>
                                <tr>
                                    <td><%= t.getFormattedDate() %></td>
                                    <td class="mono"><%= t.getTransactionId() %></td>
                                    <td>
                                        <span class="badge <%= t.isCredit() ? "badge-active" : "badge-inactive" %>">
                                            <%= t.getType() %>
                                        </span>
                                    </td>
                                    <td><%= t.getRemark() != null ? t.getRemark() : "-" %></td>
                                    <td class="text-right <%= t.isCredit() ? "badge-credit" : "badge-debit" %>">
                                        <%= t.isCredit() ? "+ " : "- " %><%= t.getFormattedAmount() %>
                                    </td>
                                    <td class="text-right mono"><%= t.getFormattedBalanceAfter() %></td>
                                </tr>
                            <% } %>
                        <% } %>
                    </tbody>
                </table>
            </div>

            <div style="margin-top: 1rem; text-align: right;">
                <a href="<%= cp %>/user/statement" class="btn btn-secondary btn-sm">Download Official Statement (.txt)</a>
            </div>
        </div>
    </section>
</div>

<%@ include file="/jsp/common/footer.jsp" %>

