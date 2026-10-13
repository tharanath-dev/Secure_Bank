<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String demoResetUrl = (String) request.getAttribute("demoResetUrl");
    String plainToken = (String) request.getAttribute("plainToken");
    String paramEmail = (String) request.getAttribute("paramEmail");
%>
<%@ include file="/jsp/common/header.jsp" %>

<div class="form-container">
    <div class="card">
        <div class="card-header">
            <h2>Forgot Password</h2>
        </div>

        <p style="font-size: 0.9rem; color: #555d67; margin-bottom: 1.25rem;">
            Enter your registered email address. We will generate a secure one-time password recovery link.
        </p>

        <% if (demoResetUrl != null) { %>
            <!-- Demo Mode Token Presentation -->
            <div class="alert alert-info" style="border-left: 4px solid #0055b3; margin-bottom: 1.5rem;">
                <div style="font-weight: 700; color: #0a2540; margin-bottom: 0.35rem;">
                    Demo Mode Notice:
                </div>
                <div style="font-size: 0.9rem; margin-bottom: 0.75rem;">
                    In a production banking system, this secure link would be dispatched via email or SMS. For this micro-project demonstration, use the link below:
                </div>
                <div style="margin-bottom: 0.75rem; word-break: break-all;">
                    <a href="<%= demoResetUrl %>" class="btn btn-success btn-sm" style="display: inline-block;">
                        Proceed to Reset Password &rarr;
                    </a>
                </div>
                <div style="font-size: 0.8rem; color: #475569;">
                    Reset URL: <br><code style="font-size: 0.8rem;"><%= demoResetUrl %></code>
                </div>
                <div style="font-size: 0.75rem; color: #64748b; margin-top: 0.35rem;">
                    Token Validity: 15 minutes (Background thread purges expired tokens).
                </div>
            </div>
        <% } %>

        <form action="<%= cp %>/user/forgot-password" method="post">
            <div class="form-group">
                <label for="email" class="required">Registered Email Address</label>
                <input type="email" id="email" name="email" required
                       value="<%= paramEmail != null ? paramEmail : "" %>"
                       placeholder="Enter your registered email">
            </div>

            <div class="actions-row">
                <button type="submit" class="btn btn-primary btn-block">Generate Password Reset Link</button>
            </div>
        </form>

        <div style="margin-top: 1.5rem; text-align: center; border-top: 1px solid #e9ecef; padding-top: 1rem; font-size: 0.9rem;">
            Remember your credentials?
            <a href="<%= cp %>/user/login" style="font-weight: 600;">Back to Sign In</a>
        </div>
    </div>
</div>

<%@ include file="/jsp/common/footer.jsp" %>

