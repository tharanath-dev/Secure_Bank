<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Boolean setupNeeded = (Boolean) request.getAttribute("setupNeeded");
    String paramEmail = (String) request.getAttribute("paramEmail");
%>
<%@ include file="/jsp/common/header.jsp" %>

<div class="form-container">
    <div class="card">
        <div class="card-header">
            <h2>Administrative Sign In</h2>
        </div>

        <% if (setupNeeded != null && setupNeeded) { %>
            <div class="alert alert-info" style="margin-bottom: 1.5rem;">
                <strong>Initial Setup Required:</strong> No admin account exists yet.
                <a href="<%= cp %>/admin/setup" style="font-weight: 600; text-decoration: underline;">Click here to run First-Time Setup</a>.
            </div>
        <% } %>

        <form action="<%= cp %>/admin/login" method="post">
            <div class="form-group">
                <label for="email" class="required">Administrator Email</label>
                <input type="email" id="email" name="email" required
                       value="<%= paramEmail != null ? paramEmail : "" %>"
                       placeholder="admin@securebank.com">
            </div>

            <div class="form-group">
                <label for="password" class="required">Password</label>
                <input type="password" id="password" name="password" required
                       placeholder="Enter administrative password">
            </div>

            <div class="actions-row">
                <button type="submit" class="btn btn-primary btn-block">Authenticate Administrator</button>
            </div>
        </form>

        <div style="margin-top: 1.5rem; text-align: center; border-top: 1px solid #e9ecef; padding-top: 1rem; font-size: 0.9rem;">
            Customer? <a href="<%= cp %>/user/login" style="font-weight: 600;">Go to Customer Sign In</a>
        </div>
    </div>
</div>

<%@ include file="/jsp/common/footer.jsp" %>

