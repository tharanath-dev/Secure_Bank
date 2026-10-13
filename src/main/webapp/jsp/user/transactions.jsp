<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.securebank.model.Transaction" %>
<%@ page import="java.util.List" %>
<%
    @SuppressWarnings("unchecked")
    List<Transaction> transactions = (List<Transaction>) request.getAttribute("transactions");
    String selectedType = (String) request.getAttribute("selectedType");
    Double totalDeposits = (Double) request.getAttribute("totalDeposits");
    Double totalWithdrawals = (Double) request.getAttribute("totalWithdrawals");
    if (selectedType == null) selectedType = "ALL";
    if (totalDeposits == null) totalDeposits = 0.0;
    if (totalWithdrawals == null) totalWithdrawals = 0.0;
%>
<%@ include file="/jsp/common/header.jsp" %>

<div class="dashboard-layout">
    <aside class="sidebar">
        <div class="sidebar-title">Customer Menu</div>
        <ul class="sidebar-nav">
            <li><a href="<%= cp %>/user/dashboard">Account Overview</a></li>
            <li><a href="<%= cp %>/user/deposit">Deposit Funds</a></li>
            <li><a href="<%= cp %>/user/withdraw">Withdraw Funds</a></li>
            <li><a href="<%= cp %>/user/transfer">Transfer Money</a></li>
            <li class="active"><a href="<%= cp %>/user/transactions">Transaction History</a></li>
            <li><a href="<%= cp %>/user/statement">Download Statement</a></li>
            <li><a href="<%= cp %>/user/profile">Profile &amp; Security</a></li>
            <li><a href="<%= cp %>/user/logout">Sign Out</a></li>
        </ul>
    </aside>

    <section class="dashboard-content">
        <div class="card">
            <div class="card-header">
                <h2>Account Transaction History</h2>
                <a href="<%= cp %>/user/statement" class="btn btn-secondary btn-sm">Download Statement (.txt)</a>
            </div>

            <!-- Aggregates calculated via Stream API -->
            <div class="stat-grid" style="grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));">
                <div class="stat-card stat-green">
                    <div class="stat-label">Total Credits (Filtered)</div>
                    <div class="stat-value">Rs. <%= String.format("%,.2f", totalDeposits) %></div>
                    <div class="stat-sub">Calculated using Stream mapToDouble &amp; sum</div>
                </div>
                <div class="stat-card stat-red">
                    <div class="stat-label">Total Debits (Filtered)</div>
                    <div class="stat-value">Rs. <%= String.format("%,.2f", totalWithdrawals) %></div>
                    <div class="stat-sub">Calculated using Stream mapToDouble &amp; sum</div>
                </div>
            </div>

            <!-- Filter Controls using Stream filtering in backend -->
            <form action="<%= cp %>/user/transactions" method="get" style="display: flex; gap: 0.75rem; align-items: center; margin-bottom: 1.5rem; flex-wrap: wrap;">
                <label for="type" style="margin-bottom: 0; font-size: 0.9rem;">Filter by Type:</label>
                <select id="type" name="type" style="width: auto; padding: 0.4rem 0.75rem;">
                    <option value="ALL" <%= "ALL".equalsIgnoreCase(selectedType) ? "selected" : "" %>>All Activity</option>
                    <option value="DEPOSIT" <%= "DEPOSIT".equalsIgnoreCase(selectedType) ? "selected" : "" %>>Deposits Only</option>
                    <option value="WITHDRAW" <%= "WITHDRAW".equalsIgnoreCase(selectedType) ? "selected" : "" %>>Withdrawals Only</option>
                    <option value="TRANSFER_OUT" <%= "TRANSFER_OUT".equalsIgnoreCase(selectedType) ? "selected" : "" %>>Transfers Sent (Out)</option>
                    <option value="TRANSFER_IN" <%= "TRANSFER_IN".equalsIgnoreCase(selectedType) ? "selected" : "" %>>Transfers Received (In)</option>
                </select>
                <button type="submit" class="btn btn-primary btn-sm">Apply Filter</button>
                <% if (!"ALL".equalsIgnoreCase(selectedType)) { %>
                    <a href="<%= cp %>/user/transactions" class="btn btn-secondary btn-sm">Reset</a>
                <% } %>
            </form>

            <div class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th>Date &amp; Time</th>
                            <th>Transaction ID</th>
                            <th>Type</th>
                            <th>Narration / Beneficiary</th>
                            <th class="text-right">Amount</th>
                            <th class="text-right">Balance After</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (transactions == null || transactions.isEmpty()) { %>
                            <tr>
                                <td colspan="6" class="empty-state">No transactions found matching criteria.</td>
                            </tr>
                        <% } else { %>
                            <% for (Transaction t : transactions) { %>
                                <tr>
                                    <td><%= t.getFormattedDate() %></td>
                                    <td class="mono"><%= t.getTransactionId() %></td>
                                    <td>
                                        <span class="badge <%= t.isCredit() ? "badge-active" : "badge-inactive" %>">
                                            <%= t.getType() %>
                                        </span>
                                    </td>
                                    <td>
                                        <%= t.getRemark() != null ? t.getRemark() : "-" %>
                                        <% if (t.getRelatedAccountNumber() != null) { %>
                                            <span style="color: #64748b; font-size: 0.85rem;">[Acc: <%= t.getRelatedAccountNumber() %>]</span>
                                        <% } %>
                                    </td>
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
        </div>
    </section>
</div>

<%@ include file="/jsp/common/footer.jsp" %>

