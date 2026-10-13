<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.securebank.model.Customer" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%
    @SuppressWarnings("unchecked")
    Map<String, Integer> stats = (Map<String, Integer>) request.getAttribute("stats");
    @SuppressWarnings("unchecked")
    List<Customer> recentCustomers = (List<Customer>) request.getAttribute("recentCustomers");

    int totalCustomers = (stats != null && stats.containsKey("totalCustomers")) ? stats.get("totalCustomers") : 0;
    int totalAccounts = (stats != null && stats.containsKey("totalAccounts")) ? stats.get("totalAccounts") : 0;
    int activeAccounts = (stats != null && stats.containsKey("activeAccounts")) ? stats.get("activeAccounts") : 0;
    int inactiveAccounts = (stats != null && stats.containsKey("inactiveAccounts")) ? stats.get("inactiveAccounts") : 0;
%>
<%@ include file="/jsp/common/header.jsp" %>

<div class="dashboard-layout">
    <aside class="sidebar">
        <div class="sidebar-title">Admin Console</div>
        <ul class="sidebar-nav">
            <li class="active"><a href="<%= cp %>/admin/dashboard">Platform Overview</a></li>
            <li><a href="<%= cp %>/admin/customers">Customer Directory</a></li>
            <li><a href="<%= cp %>/admin/customers/add">+ Add Customer</a></li>
            <li><a href="<%= cp %>/admin/logout">Sign Out</a></li>
        </ul>
    </aside>

    <section class="dashboard-content">
        <div class="card">
            <div class="card-header">
                <h2>Administrative Management Console</h2>
                <a href="<%= cp %>/admin/customers/add" class="btn btn-primary btn-sm">+ Enroll Customer</a>
            </div>

            <!-- Platform Aggregate KPI Cards -->
            <div class="stat-grid">
                <div class="stat-card stat-blue">
                    <div class="stat-label">Total Customers</div>
                    <div class="stat-value"><%= totalCustomers %></div>
                    <div class="stat-sub">Registered users</div>
                </div>

                <div class="stat-card">
                    <div class="stat-label">Total Accounts</div>
                    <div class="stat-value"><%= totalAccounts %></div>
                    <div class="stat-sub">Savings &amp; Current</div>
                </div>

                <div class="stat-card stat-green">
                    <div class="stat-label">Active Accounts</div>
                    <div class="stat-value"><%= activeAccounts %></div>
                    <div class="stat-sub">Fully operational</div>
                </div>

                <div class="stat-card stat-red">
                    <div class="stat-label">Inactive Accounts</div>
                    <div class="stat-value"><%= inactiveAccounts %></div>
                    <div class="stat-sub">Deactivated / Suspended</div>
                </div>
            </div>
        </div>

        <!-- Recent Customer Accounts Table -->
        <div class="card">
            <div class="card-header">
                <h3>Recent Customer Accounts</h3>
                <a href="<%= cp %>/admin/customers" style="font-size: 0.9rem; font-weight: 600;">View Full Directory &rarr;</a>
            </div>

            <div class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th>User ID</th>
                            <th>Customer Name</th>
                            <th>Email Address</th>
                            <th>Account Number</th>
                            <th>Type</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (recentCustomers == null || recentCustomers.isEmpty()) { %>
                            <tr>
                                <td colspan="7" class="empty-state">No customers registered in the database yet.</td>
                            </tr>
                        <% } else { %>
                            <% for (Customer c : recentCustomers) { %>
                                <tr>
                                    <td>#<%= c.getUserId() %></td>
                                    <td><strong><%= c.getName() %></strong></td>
                                    <td><%= c.getEmail() %></td>
                                    <td class="mono"><%= c.getAccount() != null ? c.getAccount().getAccountNumber() : "-" %></td>
                                    <td><%= c.getAccount() != null ? c.getAccount().getAccountType() : "-" %></td>
                                    <td>
                                        <% if (c.getAccount() != null && c.getAccount().isActive()) { %>
                                            <span class="badge badge-active">ACTIVE</span>
                                        <% } else { %>
                                            <span class="badge badge-inactive">INACTIVE</span>
                                        <% } %>
                                    </td>
                                    <td>
                                        <a href="<%= cp %>/admin/customer-details?userId=<%= c.getUserId() %>" class="btn btn-secondary btn-sm">Inspect &rarr;</a>
                                    </td>
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

