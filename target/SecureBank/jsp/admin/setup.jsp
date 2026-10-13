<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String paramName = (String) request.getAttribute("paramName");
    String paramEmail = (String) request.getAttribute("paramEmail");
    String paramPhone = (String) request.getAttribute("paramPhone");
    String paramDept = (String) request.getAttribute("paramDept");
%>
<%@ include file="/jsp/common/header.jsp" %>

<div class="form-container">
    <div class="card">
        <div class="card-header">
            <h2>Initial System Setup &ndash; Create First Administrator</h2>
        </div>

        <div class="alert alert-info" style="border-left: 4px solid #0055b3; margin-bottom: 1.5rem;">
            <strong>First-Run Installation:</strong> No administrator account currently exists in the database.
            Use this form to initialize the primary administrator. Once created, this setup endpoint will automatically deactivate itself.
        </div>

        <form action="<%= cp %>/admin/setup" method="post">
            <div class="form-group">
                <label for="name" class="required">Administrator Full Name</label>
                <input type="text" id="name" name="name" required
                       value="<%= paramName != null ? paramName : "" %>"
                       placeholder="e.g. Master Admin">
            </div>

            <div class="form-group">
                <label for="email" class="required">Official Email Address</label>
                <input type="email" id="email" name="email" required
                       value="<%= paramEmail != null ? paramEmail : "" %>"
                       placeholder="e.g. admin@securebank.com">
            </div>

            <div class="form-group">
                <label for="phone" class="required">Contact Phone Number</label>
                <input type="text" id="phone" name="phone" required
                       value="<%= paramPhone != null ? paramPhone : "" %>"
                       placeholder="e.g. 9876543210">
            </div>

            <div class="form-group">
                <label for="department">Operations Department</label>
                <input type="text" id="department" name="department"
                       value="<%= paramDept != null ? paramDept : "Central Banking Operations" %>">
            </div>

            <div class="form-group">
                <label for="password" class="required">Administrative Password</label>
                <input type="password" id="password" name="password" required minlength="6"
                       placeholder="Minimum 6 characters">
                <div class="input-help">Will be encrypted using salted SHA-256 before storage.</div>
            </div>

            <div class="actions-row">
                <button type="submit" class="btn btn-primary btn-block">Initialize System &amp; Create Admin</button>
            </div>
        </form>
    </div>
</div>

<%@ include file="/jsp/common/footer.jsp" %>

