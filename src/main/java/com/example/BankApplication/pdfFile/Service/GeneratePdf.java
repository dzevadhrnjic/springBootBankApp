package com.example.BankApplication.pdfFile.Service;

import com.example.BankApplication.account.Model.Account;
import com.example.BankApplication.account.Database.AccountRepository;
import com.example.BankApplication.account.Exception.ValidationIdAccountException;
import com.example.BankApplication.pdfFile.Model.StatementPdf;
import com.example.BankApplication.pdfFile.Database.StatementRepository;
import com.example.BankApplication.user.Database.UserRepository;
import com.example.BankApplication.user.Exception.EmailNotVerifiedException;
import com.example.BankApplication.user.Model.User;
import com.example.BankApplication.user.Service.EmailService;
import com.example.BankApplication.user.Service.UserService;
import com.example.BankApplication.user.Util.TokenUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class GeneratePdf {

    TokenUtil tokenUtil = new TokenUtil();

    private final EmailService emailService;
    private final UserService userService;

    public GeneratePdf(EmailService emailService, UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    StatementRepository statementRepository;

    @Autowired
    AccountRepository accountRepository;

    public StatementPdf statement(String token, Long accountId) throws MessagingException, DocumentException, IOException {

        Long userId = tokenUtil.verifyJwt(token);
        Account idAccount = accountRepository.getAccountByUserIdAndUserId(userId, accountId);
        User user = userRepository.getUserById(userId);

        if (idAccount == null){
            throw new ValidationIdAccountException("No account with that id");
        }

        if (!user.isVerifyemail()){
            throw new EmailNotVerifiedException("Please verify email");
        }

        createPdfFile(accountId);

        emailService.sendEmailWithAttachment(user.getEmail(), "Statement", "Your statement " +
                                                user.getFirstname());

        return null;
    }

    public void createPdfFile(Long accountId) throws DocumentException, IOException, MessagingException {

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("transactionStatement.pdf"));

        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setSize(16);
        font.setColor(BaseColor.BLACK);

        Paragraph paragraph = new Paragraph("List of transactions", font);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{1.5f, 3.5f, 3.0f, 1.5f});
        table.setSpacingBefore(10);

        addTableHeader(table);
        tableData(table, accountId);
        document.add(table);
        document.close();
    }

    private void addTableHeader(PdfPTable table) {

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BaseColor.ORANGE);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(BaseColor.BLACK);

        cell.setPhrase(new Phrase("Sender name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Receiver name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Amount", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Date", font));
        table.addCell(cell);
    }

    public void tableData(PdfPTable table, Long accountId) {
        for (StatementPdf statement : statementRepository.getStatement(accountId)) {
            table.addCell(String.valueOf(statement.getSendername()));
            table.addCell(String.valueOf(statement.getReceivername()));
            table.addCell(String.valueOf(statement.getAmount()));
            table.addCell(String.valueOf(statement.getCreatedat()));
        }
    }
}


