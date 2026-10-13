<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%
    @SuppressWarnings("unchecked")
    Map<String, String> errors = (Map<String, String>) request.getAttribute("errors");
    String paramName = (String) request.getAttribute("paramName");
    String paramEmail = (String) request.getAttribute("paramEmail");
    String paramPhone = (String) request.getAttribute("paramPhone");
    String paramAccountType = (String) request.getAttribute("paramAccountType");
    String paramDeposit = (String) request.getAttribute("paramDeposit");
%>
<%@ include file="/jsp/common/header.jsp" %>

<div class="dashboard-layout">
    <aside class="sidebar">
        <div class="sidebar-title">Admin Console</div>
        <ul class="sidebar-nav">
            <li><a href="<%= cp %>/admin/dashboard">Platform Overview</a></li>
            <li><a href="<%= cp %>/admin/customers">Customer Directory</a></li>
            <li class="active"><a href="<%= cp %>/admin/customers/add">+ Add Customer</a></li>
            <li><a href="<%= cp %>/admin/logout">Sign Out</a></li>
        </ul>
    </aside>

    <section class="dashboard-content">
        <div class="card" style="max-width: 650px;">
            <div class="card-header">
                <h2>Enroll New Customer Account</h2>
            </div>

            <form action="<%= cp %>/admin/customers/add" method="post">
                <div class="form-group">
                    <label for="name" class="required">Customer Legal Name</label>
                    <input type="text" id="name" name="name" required
                           value="<%= paramName != null ? paramName : "" %>"
                           placeholder="e.g. Alice Smith">
                    <% if (errors != null && errors.containsKey("name")) { %>
                        <div class="field-error"><%= errors.get("name") %></div>
                    <% } %>
                </div>

                <div class="form-group">
                    <label for="email" class="required">Email Address</label>
                    <input type="email" id="email" name="email" required
                           value="<%= paramEmail != null ? paramEmail : "" %>"
                           placeholder="e.g. alice@example.com">
                    <% if (errors != null && errors.containsKey("email")) { %>
                        <div class="field-error"><%= errors.get("email") %></div>
                    <% } %>
                </div>

                <div class="form-group">
                    <label for="phone" class="required">Mobile Number</label>
                    <input type="text" id="phone" name="phone" required
                           value="<%= paramPhone != null ? paramPhone : "" %>"
                           placeholder="e.g. 9876543210">
                    <% if (errors != null && errors.containsKey("phone")) { %>
                        <div class="field-error"><%= errors.get("phone") %></div>
                    <% } %>
                </div>

                <div class="form-group">
                    <label for="password" class="required">Temporary Initial Password</label>
                    <input type="password" id="password" name="password" required minlength="6"
                           placeholder="At least 6 characters">
                    <% if (errors != null && errors.containsKey("password")) { %>
                        <div class="field-error"><%= errors.get("password") %></div>
                    <% } %>
                </div>

                <div class="form-group">
                    <label for="accountType" class="required">Account Type</label>
                    <select id="accountType" name="accountType" required>
                        <option value="SAVINGS" <%= "SAVINGS".equalsIgnoreCase(paramAccountType) ? "selected" : "" %>>
                            Savings Account (Min. Balance Rs. 1,000.00)
                        </option>
                        <option value="CURRENT" <%= "CURRENT".equalsIgnoreCase(paramAccountType) ? "selected" : "" %>>
                            Current Account (Min. Balance Rs. 5,000.00)
                        </option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="openingDeposit">Initial Opening Deposit (Rs.)</label>
                    <input type="number" step="0.01" min="0" id="openingDeposit" name="openingDeposit"
                           value="<%= paramDeposit != null ? paramDeposit : "" %>"
                           placeholder="e.g. 1000.00">
                </div>

                <div class="actions-row">
                    <button type="submit" class="btn btn-primary">Enroll Customer</button>
                    <a href="<%= cp %>/admin/customers" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
    </section>
</div>

<%@ include file="/jsp/common/footer.jsp" %>

