<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/jsp/common/header.jsp" %>

<div class="form-container">
    <div class="card">
        <div class="card-header">
            <h2>Customer Sign In</h2>
        </div>

        <form action="<%= cp %>/user/login" method="post">
            <div class="form-group">
                <label for="email" class="required">Email Address</label>
                <input type="email" id="email" name="email" required
                       value="<%= request.getAttribute("paramEmail") != null ? request.getAttribute("paramEmail") : "" %>"
                       placeholder="e.g. customer@example.com">
            </div>

            <div class="form-group">
                <div style="display: flex; justify-content: space-between; align-items: baseline;">
                    <label for="password" class="required">Password</label>
                    <a href="<%= cp %>/user/forgot-password" style="font-size: 0.85rem;">Forgot Password?</a>
                </div>
                <input type="password" id="password" name="password" required placeholder="Enter your password">
            </div>

            <div class="actions-row">
                <button type="submit" class="btn btn-primary btn-block">Sign In</button>
            </div>
        </form>

        <div style="margin-top: 1.5rem; text-align: center; border-top: 1px solid #e9ecef; padding-top: 1rem; font-size: 0.9rem;">
            Don't have a SecureBank account?
            <a href="<%= cp %>/user/register" style="font-weight: 600;">Open an Account</a>
        </div>
    </div>
</div>

<%@ include file="/jsp/common/footer.jsp" %>

