<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.securebank.model.Customer" %>
<%@ page import="java.util.List" %>
<%
    @SuppressWarnings("unchecked")
    List<Customer> customers = (List<Customer>) request.getAttribute("customers");
    String searchQuery = (String) request.getAttribute("searchQuery");
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
        <div class="card">
            <div class="card-header">
                <h2>Customer Directory</h2>
                <a href="<%= cp %>/admin/customers/add" class="btn btn-primary btn-sm">+ Enroll New Customer</a>
            </div>

            <!-- Search Form using Java Stream API in Backend -->
            <form action="<%= cp %>/admin/customers" method="get" style="display: flex; gap: 0.75rem; align-items: center; margin-bottom: 1.5rem;">
                <input type="text" name="q" value="<%= searchQuery != null ? searchQuery : "" %>"
                       placeholder="Search by customer name, email, or 12-digit account number..."
                       style="flex: 1; max-width: 500px;">
                <button type="submit" class="btn btn-primary btn-sm">Search</button>
                <% if (searchQuery != null && !searchQuery.trim().isEmpty()) { %>
                    <a href="<%= cp %>/admin/customers" class="btn btn-secondary btn-sm">Clear Search</a>
                <% } %>
            </form>

            <div class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th>User ID</th>
                            <th>Customer Name</th>
                            <th>Email Address</th>
                            <th>Phone</th>
                            <th>Account Number</th>
                            <th>Type</th>
                            <th class="text-right">Balance</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (customers == null || customers.isEmpty()) { %>
                            <tr>
                                <td colspan="9" class="empty-state">
                                    <%= (searchQuery != null && !searchQuery.trim().isEmpty()) 
                                            ? "No customers matched your search query \"" + searchQuery + "\"." 
                                            : "No registered customers found in the database." %>
                                </td>
                            </tr>
                        <% } else { %>
                            <% for (Customer c : customers) { %>
                                <tr>
                                    <td>#<%= c.getUserId() %></td>
                                    <td><strong><%= c.getName() %></strong></td>
                                    <td><%= c.getEmail() %></td>
                                    <td><%= c.getPhone() %></td>
                                    <td class="mono"><%= c.getAccount() != null ? c.getAccount().getAccountNumber() : "-" %></td>
                                    <td><%= c.getAccount() != null ? c.getAccount().getAccountType() : "-" %></td>
                                    <td class="text-right mono">
                                        <%= c.getAccount() != null ? c.getAccount().getFormattedBalance() : "Rs. 0.00" %>
                                    </td>
                                    <td>
                                        <% if (c.getAccount() != null && c.getAccount().isActive()) { %>
                                            <span class="badge badge-active">ACTIVE</span>
                                        <% } else { %>
                                            <span class="badge badge-inactive">INACTIVE</span>
                                        <% } %>
                                    </td>
                                    <td>
                                        <div style="display: flex; gap: 0.35rem;">
                                            <a href="<%= cp %>/admin/customer-details?userId=<%= c.getUserId() %>" class="btn btn-secondary btn-sm">View Details</a>
                                            <% if (c.getAccount() != null) { %>
                                                <form action="<%= cp %>/admin/toggle-status" method="post" style="display:inline;"
                                                      onsubmit="return confirm('Toggle status for this account?');">
                                                    <input type="hidden" name="accountId" value="<%= c.getAccount().getAccountId() %>">
                                                    <input type="hidden" name="userId" value="<%= c.getUserId() %>">
                                                    <% if (c.getAccount().isActive()) { %>
                                                        <button type="submit" class="btn btn-danger btn-sm">Deactivate</button>
                                                    <% } else { %>
                                                        <button type="submit" class="btn btn-success btn-sm">Activate</button>
                                                    <% } %>
                                                </form>
                                            <% } %>
                                        </div>
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

