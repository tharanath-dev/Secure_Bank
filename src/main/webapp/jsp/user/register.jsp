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

<div class="form-container">
    <div class="card">
        <div class="card-header">
            <h2>Open a Bank Account</h2>
        </div>

        <p style="font-size: 0.9rem; color: #555d67; margin-bottom: 1.25rem;">
            Provide your personal information to register. A unique 12-digit account number will be generated automatically.
        </p>

        <form action="<%= cp %>/user/register" method="post">
            <div class="form-group">
                <label for="name" class="required">Full Legal Name</label>
                <input type="text" id="name" name="name" required
                       value="<%= paramName != null ? paramName : "" %>"
                       placeholder="e.g. John Doe">
                <% if (errors != null && errors.containsKey("name")) { %>
                    <div class="field-error"><%= errors.get("name") %></div>
                <% } %>
            </div>

            <div class="form-group">
                <label for="email" class="required">Email Address</label>
                <input type="email" id="email" name="email" required
                       value="<%= paramEmail != null ? paramEmail : "" %>"
                       placeholder="e.g. john.doe@example.com">
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
                <label for="password" class="required">Security Password</label>
                <input type="password" id="password" name="password" required minlength="6"
                       placeholder="At least 6 characters">
                <div class="input-help">Minimum 6 characters. Passwords are saved as salted SHA-256 hashes.</div>
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
                <% if (errors != null && errors.containsKey("accountType")) { %>
                    <div class="field-error"><%= errors.get("accountType") %></div>
                <% } %>
            </div>

            <div class="form-group">
                <label for="openingDeposit">Initial Opening Deposit (Optional)</label>
                <input type="number" step="0.01" min="0" id="openingDeposit" name="openingDeposit"
                       value="<%= paramDeposit != null ? paramDeposit : "" %>"
                       placeholder="e.g. 2500.00">
                <div class="input-help">Optional opening balance to credit immediately upon account creation.</div>
                <% if (errors != null && errors.containsKey("openingDeposit")) { %>
                    <div class="field-error"><%= errors.get("openingDeposit") %></div>
                <% } %>
            </div>

            <div class="actions-row">
                <button type="submit" class="btn btn-primary btn-block">Complete Account Registration</button>
            </div>
        </form>

        <div style="margin-top: 1.5rem; text-align: center; border-top: 1px solid #e9ecef; padding-top: 1rem; font-size: 0.9rem;">
            Already have an account?
            <a href="<%= cp %>/user/login" style="font-weight: 600;">Sign In</a>
        </div>
    </div>
</div>

<%@ include file="/jsp/common/footer.jsp" %>

