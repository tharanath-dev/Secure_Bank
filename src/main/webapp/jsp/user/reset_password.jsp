<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String token = (String) request.getAttribute("token");
    if (token == null) token = request.getParameter("token");
%>
<%@ include file="/jsp/common/header.jsp" %>

<div class="form-container">
    <div class="card">
        <div class="card-header">
            <h2>Reset Account Password</h2>
        </div>

        <p style="font-size: 0.9rem; color: #555d67; margin-bottom: 1.25rem;">
            Please choose a strong new password for your SecureBank customer account.
        </p>

        <form action="<%= cp %>/user/reset-password" method="post">
            <input type="hidden" name="token" value="<%= token != null ? token : "" %>">

            <div class="form-group">
                <label>Reset Security Token</label>
                <input type="text" disabled value="<%= token != null ? token : "No token provided" %>"
                       class="mono" style="background-color: #f1f5f9; font-size: 0.85rem;">
            </div>

            <div class="form-group">
                <label for="newPassword" class="required">New Password</label>
                <input type="password" id="newPassword" name="newPassword" required minlength="6"
                       placeholder="Enter new password (min. 6 characters)">
            </div>

            <div class="form-group">
                <label for="confirmPassword" class="required">Confirm New Password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required minlength="6"
                       placeholder="Re-enter new password">
            </div>

            <div class="actions-row">
                <button type="submit" class="btn btn-primary btn-block">Confirm &amp; Update Password</button>
            </div>
        </form>

        <div style="margin-top: 1.5rem; text-align: center; border-top: 1px solid #e9ecef; padding-top: 1rem; font-size: 0.9rem;">
            <a href="<%= cp %>/user/login" style="font-weight: 600;">Return to Sign In</a>
        </div>
    </div>
</div>

<%@ include file="/jsp/common/footer.jsp" %>

