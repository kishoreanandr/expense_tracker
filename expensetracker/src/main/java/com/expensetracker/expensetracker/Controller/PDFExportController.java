package com.expensetracker.expensetracker.Controller;

import com.expensetracker.expensetracker.Service.ExpenseService;
import com.expensetracker.expensetracker.Service.BudgetService;
import com.expensetracker.expensetracker.Entity.Expense;
import com.expensetracker.expensetracker.Entity.Budget;
import com.expensetracker.expensetracker.Entity.Category;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

@RestController
@RequestMapping("/api/pdf")
public class PDFExportController {
    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private BudgetService budgetService;

    @GetMapping("/export")
    public void exportPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=expense_report.pdf");
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font tableHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font tableBody = FontFactory.getFont(FontFactory.HELVETICA, 11);

            // Title
            Paragraph title = new Paragraph("Expense & Budget Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            // Expenses Section
            document.add(new Paragraph("Expenses", tableHeader));
            PdfPTable expenseTable = new PdfPTable(6);
            expenseTable.setWidthPercentage(100);
            expenseTable.setSpacingBefore(10f);
            expenseTable.setSpacingAfter(10f);
            String[] expHeaders = {"Date", "Category", "Amount", "Note", "Merchant", "Payment Method"};
            for (String h : expHeaders) {
                PdfPCell cell = new PdfPCell(new Phrase(h, tableHeader));
                expenseTable.addCell(cell);
            }
            List<Expense> expenses = expenseService.getAllExpensesForCurrentUser();
            for (Expense e : expenses) {
                expenseTable.addCell(new Phrase(e.getExpenseDate() != null ? e.getExpenseDate().toString() : "", tableBody));
                expenseTable.addCell(new Phrase(e.getCategory() != null ? e.getCategory().getName() : "", tableBody));
                expenseTable.addCell(new Phrase(e.getAmount() != null ? e.getAmount().toString() : "", tableBody));
                expenseTable.addCell(new Phrase(e.getNote() != null ? e.getNote() : "", tableBody));
                expenseTable.addCell(new Phrase(e.getMerchant() != null ? e.getMerchant() : "", tableBody));
                expenseTable.addCell(new Phrase(e.getPaymentMethod() != null ? e.getPaymentMethod() : "", tableBody));
            }
            document.add(expenseTable);

            // Budgets Section
            document.add(new Paragraph("Budgets", tableHeader));
            PdfPTable budgetTable = new PdfPTable(4);
            budgetTable.setWidthPercentage(100);
            budgetTable.setSpacingBefore(10f);
            budgetTable.setSpacingAfter(10f);
            String[] budHeaders = {"Period Type", "Budget Type", "Category", "Amount"};
            for (String h : budHeaders) {
                PdfPCell cell = new PdfPCell(new Phrase(h, tableHeader));
                budgetTable.addCell(cell);
            }
            List<Budget> budgets = budgetService.getAllBudgetsForCurrentUser();
            for (Budget b : budgets) {
                budgetTable.addCell(new Phrase(b.getPeriodType() != null ? b.getPeriodType().toString() : "", tableBody));
                budgetTable.addCell(new Phrase(b.getBudgetType() != null ? b.getBudgetType().toString() : "", tableBody));
                budgetTable.addCell(new Phrase(b.getCategory() != null ? b.getCategory().getName() : "Overall", tableBody));
                budgetTable.addCell(new Phrase(b.getAmount() != null ? b.getAmount().toString() : "", tableBody));
            }
            document.add(budgetTable);

            document.close();
        } catch (Exception e) {
            response.reset();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain");
            response.getWriter().write("Failed to generate PDF: " + e.getMessage());
        }
    }
} 