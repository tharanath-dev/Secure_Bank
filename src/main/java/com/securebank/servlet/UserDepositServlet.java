package com.securebank.servlet;

import com.securebank.exception.AccountNotFoundException;
import com.securebank.exception.InactiveAccountException;
import com.securebank.exception.InvalidAmountException;
import com.securebank.model.Account;
import com.securebank.model.Person;
import com.securebank.model.Transaction;
import com.securebank.service.BankService;
import com.securebank.util.SessionUtil;
import com.securebank.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Module V: Servlet handling customer deposit requests.
 * Demonstrates:
 * - Module III: Multiple catch blocks handling custom checked exceptions
 * - Module II: Inner class Receipt generation
 */
@WebServlet("/user/deposit")
public class UserDepositServlet extends HttpServlet {

    private BankService bankService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.bankService = new BankService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, Person.ROLE_USER)) return;
        request.getRequestDispatcher("/jsp/user/deposit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, Person.ROLE_USER)) return;

        Account account = SessionUtil.getLoggedAccount(request);
        String amountStr = request.getParameter("amount");
        String remark = request.getParameter("remark");

        Double amount = ValidationUtil.parsePositiveAmount(amountStr);
        if (amount == null) {
            request.setAttribute("errorMessage", "Please enter a valid positive numeric amount.");
            request.setAttribute("paramRemark", remark);
            request.getRequestDispatcher("/jsp/user/deposit.jsp").forward(request, response);
            return;
        }

        try {
            // Execute synchronized deposit
            Transaction txn = bankService.deposit(account.getAccountNumber(), amount, remark);

            // Refresh session account balance
            Account updated = bankService.getValidAccount(account.getAccountNumber());
            SessionUtil.updateSessionAccount(request, updated);

            // Module II: Instantiate Account.Receipt inner class demonstrating outer.new Inner()
            Account.Receipt receipt = updated.new Receipt(
                    txn.getTransactionId(),
                    txn.getType(),
                    txn.getAmount(),
                    txn.getCreatedAt(),
                    txn.getBalanceAfter()
            );

            request.setAttribute("transaction", txn);
            request.setAttribute("receipt", receipt);
            request.setAttribute("account", updated);

            SessionUtil.recordActivity(request, "Deposited Rs. " + amount);
            request.getRequestDispatcher("/jsp/user/receipt.jsp").forward(request, response);

        } catch (InvalidAmountException | AccountNotFoundException | InactiveAccountException e) {
            // Module III: Multi-catch
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("paramAmount", amountStr);
            request.setAttribute("paramRemark", remark);
            request.getRequestDispatcher("/jsp/user/deposit.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            request.getRequestDispatcher("/jsp/user/deposit.jsp").forward(request, response);
        }
    }
}

