<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.securebank.model.Customer" %>
<%@ page import="com.securebank.model.Account" %>
<%@ page import="com.securebank.model.Transaction" %>
<%@ page import="java.util.List" %>
<%
    Customer customer = (Customer) request.getAttribute("customer");
    Account account = (Account) request.getAttribute("account");
    Account.AccountSummary summary = (Account.AccountSummary) request.getAttribute("accountSummary");
    @SuppressWarnings("unchecked")
    List<Transaction> transactions = (List<Transaction>) request.getAttribute("transactions");
%>
<%@ include file="/jsp/common/header.jsp" %>

<div class="dashboard-layout">
    <aside class="sidebar">
        <div class="sidebar-title">Admin Console</div>
        <ul class="sidebar-nav">
            <li><a href="<%= cp %>/admin/dashboard">Platform Overview</a></li>
            <li class="active"><a href="<%= cp %>/admin/customers">Customer Directory</a></li>
            <li><a href="<%= cp %>/admin/customers/add">+ Add Customer</a></li>
            <li><a href="<%= cp %>/admin/logout">Sign Out</a></li>
        </ul>
    </aside>

    <section class="dashboard-content">
        <!-- Customer & Account Details Card -->
        <div class="card">
            <div class="card-header">
                <div>
                    <h2>Customer Profile: <%= customer != null ? customer.getName() : "Customer" %></h2>
                    <div style="font-size: 0.85rem; color: #64748b;">User ID: #<%= customer != null ? customer.getUserId() : "-" %></div>
                </div>
                <div>
                    <% if (account != null) { %>
                        <form action="<%= cp %>/admin/toggle-status" method="post" style="display:inline;"
                              onsubmit="return confirm('Confirm updating status for this account?');">
                            <input type="hidden" name="accountId" value="<%= account.getAccountId() %>">
                            <input type="hidden" name="userId" value="<%= customer.getUserId() %>">
                            <% if (account.isActive()) { %>
                                <button type="submit" class="btn btn-danger btn-sm">Deactivate Account</button>
                            <% } else { %>
                                <button type="submit" class="btn btn-success btn-sm">Activate Account</button>
                            <% } %>
                        </form>
                    <% } %>
                    <a href="<%= cp %>/admin/customers" class="btn btn-secondary btn-sm" style="margin-left: 0.5rem;">&larr; Back</a>
                </div>
            </div>

            <!-- Customer & Account Metadata Grid -->
            <div class="stat-grid" style="grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));">
                <div class="stat-card">
                    <div class="stat-label">Contact Details</div>
                    <div style="font-size: 0.95rem; margin-top: 0.25rem;">
                        <strong>Email:</strong> <%= customer != null ? customer.getEmail() : "-" %><br>
                        <strong>Phone:</strong> <%= customer != null ? customer.getPhone() : "-" %><br>
                        <strong>Enrolled:</strong> <%= customer != null && customer.getCreatedAt() != null ? customer.getCreatedAt().toString().substring(0, 10) : "-" %>
                    </div>
                </div>

                <div class="stat-card stat-blue">
                    <div class="stat-label">Account Information</div>
                    <div style="font-size: 0.95rem; margin-top: 0.25rem;">
                        <strong>Account No:</strong> <span class="mono"><%= account != null ? account.getAccountNumber() : "-" %></span><br>
                        <strong>Classification:</strong> <%= account != null ? account.getAccountType() : "-" %><br>
                        <strong>Min. Balance:</strong> Rs. <%= account != null ? String.format("%.2f", account.getMinimumBalance()) : "0.00" %>
                    </div>
                </div>

                <div class="stat-card <%= (account != null && account.isActive()) ? "stat-green" : "stat-red" %>">
                    <div class="stat-label">Ledger Balance &amp; Status</div>
                    <div class="stat-value" style="font-size: 1.45rem;">
                        <%= account != null ? account.getFormattedBalance() : "Rs. 0.00" %>
                    </div>
                    <div style="margin-top: 0.35rem;">
                        <% if (account != null && account.isActive()) { %>
                            <span class="badge badge-active">ACTIVE &bull; OPERATIONAL</span>
                        <% } else { %>
                            <span class="badge badge-inactive">INACTIVE &bull; BLOCKED</span>
                        <% } %>
                    </div>
                </div>
            </div>

            <% if (summary != null) { %>
                <!-- Demonstrates Module II: Static Nested Class Account.AccountSummary -->
                <div style="background-color: #f8fafc; border: 1px dashed #cbd5e1; border-radius: 4px; padding: 0.85rem; margin-top: 1rem; font-size: 0.85rem; color: #475569;">
                    <strong>Static Nested Class (Account.AccountSummary):</strong><br>
                    Holder: <code><%= summary.getCustomerName() %></code> |
                    Account: <code><%= summary.getAccountNumber() %></code> |
                    Type: <code><%= summary.getAccountType() %></code> |
                    Balance: <code><%= summary.getFormattedBalance() %></code> |
                    Status: <code><%= summary.getStatus() %></code>
                </div>
            <% } %>
        </div>

        <!-- Customer Transactions List -->
        <div class="card">
            <div class="card-header">
                <h3>Customer Transaction History</h3>
            </div>

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
                                <td colspan="6" class="empty-state">No transactions recorded for this customer yet.</td>
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

