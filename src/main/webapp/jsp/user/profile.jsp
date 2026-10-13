<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.securebank.model.Customer" %>
<%@ page import="com.securebank.util.SessionUtil" %>
<%
    Customer customer = (Customer) SessionUtil.getLoggedUser(request);
    String profileError = (String) request.getAttribute("profileError");
    String passwordError = (String) request.getAttribute("passwordError");
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
            <li><a href="<%= cp %>/user/transactions">Transaction History</a></li>
            <li><a href="<%= cp %>/user/statement">Download Statement</a></li>
            <li class="active"><a href="<%= cp %>/user/profile">Profile &amp; Security</a></li>
            <li><a href="<%= cp %>/user/logout">Sign Out</a></li>
        </ul>
    </aside>

    <section class="dashboard-content">
        <!-- Edit Profile Card -->
        <div class="card" style="max-width: 600px; margin-bottom: 2rem;">
            <div class="card-header">
                <h2>Profile Information</h2>
            </div>

            <% if (profileError != null) { %>
                <div class="alert alert-error"><%= profileError %></div>
            <% } %>

            <form action="<%= cp %>/user/profile" method="post">
                <input type="hidden" name="action" value="updateProfile">

                <div class="form-group">
                    <label for="name" class="required">Full Legal Name</label>
                    <input type="text" id="name" name="name" required
                           value="<%= customer != null ? customer.getName() : "" %>">
                </div>

                <div class="form-group">
                    <label for="email">Email Address (Registered)</label>
                    <input type="email" id="email" disabled
                           value="<%= customer != null ? customer.getEmail() : "" %>"
                           style="background-color: #f1f5f9; cursor: not-allowed;">
                    <div class="input-help">
                        Security Notice: Masked format: <strong><%= customer != null ? customer.getMaskedEmail() : "" %></strong>. Primary email cannot be changed online.
                    </div>
                </div>

                <div class="form-group">
                    <label for="phone" class="required">Phone Number</label>
                    <input type="text" id="phone" name="phone" required
                           value="<%= customer != null ? customer.getPhone() : "" %>">
                </div>

                <div class="actions-row">
                    <button type="submit" class="btn btn-primary">Update Profile</button>
                </div>
            </form>
        </div>

        <!-- Change Password Card -->
        <div class="card" style="max-width: 600px;">
            <div class="card-header">
                <h2>Security &amp; Password Update</h2>
            </div>

            <% if (passwordError != null) { %>
                <div class="alert alert-error"><%= passwordError %></div>
            <% } %>

            <form action="<%= cp %>/user/profile" method="post">
                <input type="hidden" name="action" value="changePassword">

                <div class="form-group">
                    <label for="currentPassword" class="required">Current Password</label>
                    <input type="password" id="currentPassword" name="currentPassword" required>
                </div>

                <div class="form-group">
                    <label for="newPassword" class="required">New Password</label>
                    <input type="password" id="newPassword" name="newPassword" required minlength="6">
                    <div class="input-help">Must be at least 6 characters long.</div>
                </div>

                <div class="form-group">
                    <label for="confirmPassword" class="required">Confirm New Password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required minlength="6">
                </div>

                <div class="actions-row">
                    <button type="submit" class="btn btn-primary">Save New Password</button>
                </div>
            </form>
        </div>
    </section>
</div>

<%@ include file="/jsp/common/footer.jsp" %>

