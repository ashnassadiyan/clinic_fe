package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;

public class Payment {
    private static int idCounter = 1000;
    private int paymentId;
    private double advance;
    private double treatmentCharge;

    public int getPaymentId() {
        return paymentId;
    }

    public Payment( double advance) {
        this.paymentId =idCounter++;
        this.advance = advance;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public double getAdvance() {
        return advance;
    }

    public void setAdvance(double advance) {
        this.advance = advance;
    }

    public double getTreatmentCharge() {
        return treatmentCharge;
    }

    public void setTreatmentCharge(double treatmentCharge) {
        this.treatmentCharge = treatmentCharge;
    }

    public static void generateInvoice(String appointmentNumber,String appointmentDate,
                                       String patientName, String doctorName,
                                       double advancePaid, String filePath,String day) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(200, 700);
            contentStream.showText("**** Aurora Skin Clinic ****");
            contentStream.newLineAtOffset(0, -15);
            contentStream.newLineAtOffset(0, -15);
            contentStream.newLineAtOffset(0, -15);
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("**** Appointment Confirmation ****");
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Appointment Number: " + appointmentNumber);
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Appointment Date: " +day+" "+ appointmentDate);
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Patient Name: " + patientName);
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Doctor: Dr. " + doctorName);
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Advance paid: LKR " + advancePaid);
            contentStream.endText();
            contentStream.close();
            document.save(filePath);
            System.out.println("PDF invoice created successfully at " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
