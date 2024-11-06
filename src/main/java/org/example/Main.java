package org.example;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.IOException;

import static org.example.Appointment.*;
import static org.example.ConsultationDay.getCorrespondingDay;
import static org.example.ConsultationDay.printConsultationDays;
import static org.example.Payment.generateInvoice;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {






    public static List<Integer> convertStringToArray(String input) {
        List<Integer> result = new ArrayList<>();

        // Split the string by commas
        String[] parts = input.split(",");

        // Parse each part and handle ranges
        for (int i = 0; i < parts.length; i++) {
            try {
                int start = Integer.parseInt(parts[i].trim());

                if (i + 1 < parts.length) {
                    int end = Integer.parseInt(parts[i + 1].trim());

                    // If the next number is greater than the current, generate a range
                    if (end > start) {
                        for (int j = start; j <= end; j++) {
                            if (!result.contains(j)) { // avoid duplicates
                                result.add(j);
                            }
                        }
                        i++; // Skip the next number since it's part of the range
                    } else {
                        // If not a range, just add the current number
                        if (!result.contains(start)) {
                            result.add(start);
                        }
                    }
                } else {
                    // Add the last number if no range is left
                    if (!result.contains(start)) {
                        result.add(start);
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number: " + parts[i]);
            }
        }

        return result;
    }

    public static void generateFinalInvoice(Appointment appointment) throws IOException {
        // Create a new PDF document
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        // Initialize content stream
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Set title and header
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
        contentStream.newLineAtOffset(100, 750);
        contentStream.showText("******************** Aurora Skincare Clinic ***********************");
        contentStream.endText(); // Correctly end the first text block

        // Patient details
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(100, 720);
        contentStream.showText("Patient Name: " + (appointment.getPatient() != null ? appointment.getPatient().getName() : "N/A"));
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Session: " + appointment.getSession());
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Treatment: " + (appointment.getTreatment() != null ? appointment.getTreatment().getName() : "N/A"));
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Doctor: " + (appointment.getDoctorId() == 1 ? "Doctor One" : "Doctor Two"));
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Status: " + appointment.getAppointmentStatus());
        contentStream.endText(); // End the patient details text block

        // Financial details
        double advance = (appointment.getPayment() != null) ? appointment.getPayment().getAdvance() : 0.0;
        double charge = (appointment.getTreatment() != null) ? appointment.getTreatment().getPrice() : 0.0;
        double tax = (charge + advance) * 0.025;
        double total = charge + advance + tax;

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(100, 600);
        contentStream.showText("Advance: " + String.format("%.2f", advance));
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Charge: " + String.format("%.2f", charge));
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Tax (2.5%): " + String.format("%.2f", tax));
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Total: " + String.format("%.2f", total));
        contentStream.endText(); // End the financial details text block
        // Close the content stream
        contentStream.close();
        // Save the document
        document.save("Invoice.pdf");
        // Close the document
        document.close();
    }

    public static void main(String[] args) {
        // days
        String Monday="Monday";
        String Wednesday="Wednesday";
        String Friday="Friday";
        String Saturday="Saturday";
        String filePath = "invoice.pdf";
        boolean running = true;

        // doctors
        Doctor DoctorOne=new Doctor(1,"janaka");
        Doctor DoctorTwo=new Doctor(2,"sumana");




        // consultation array list
        ArrayList<ConsultationDay> consultations = new ArrayList<>();
        // <-------------------------------                -------------------------------------->
        // since there 4 consultation days I am creating 4 objects
        //  and Adding them consultation array list
        //      create ConsultationDay object for 	Monday 10:00am - 01:00pm
        ConsultationDay monday=new ConsultationDay(1,Monday,"10:00","13:00");
        consultations.add(monday);
        //      create ConsultationDay object for Wednesday 14:00am - 17:00pm
        ConsultationDay wednesday=new ConsultationDay(2,Wednesday,"14:00","17:00");
        consultations.add(wednesday);

        //      create ConsultationDay object for Friday 04:00pm - 08:00pm
        ConsultationDay friday=new ConsultationDay(3,Friday,"16:00","20:00");
        consultations.add(friday);

        //      create ConsultationDay object for Saturday 09:00am - 13:00pm
        ConsultationDay saturday=new ConsultationDay(4,Saturday,"09:00","13:00");
        consultations.add(saturday);



        // Appointments ArrayList for subasinhga
        ArrayList<Appointment> appointmentsOne=new ArrayList<>();
        // Appointments ArrayList for jayathilaka
        ArrayList<Appointment> appointmentsTwo=new ArrayList<>();

        // this loop will be creating appointments according to consultations
        for (ConsultationDay consultation : consultations) {
            List<String> sessions = generateSessions(consultation.getStartTime(), consultation.getEndTime());

            // Creating appointment sessions for DoctorOne
            for (String session : sessions) {
                Appointment appointment=new Appointment(session,DoctorOne.getId(),consultation.getId());
                appointmentsOne.add(appointment);
            }

            // Creating appointment sessions for DoctorTwo
            for (String session : sessions) {
                Appointment appointment=new Appointment(session,DoctorTwo.getId(),consultation.getId());
                appointmentsTwo.add(appointment);
            }
        }

        HashMap<String,ArrayList<Appointment>> DoctorsAppointments=new HashMap<String, ArrayList<Appointment>>();

        DoctorsAppointments.put("1",appointmentsOne);
        DoctorsAppointments.put("2",appointmentsTwo);




        System.out.println();
        System.out.println("***************************************");
        System.out.println("**** Welcome to Aurora Skin care ****");
        System.out.println("***************************************");
        System.out.println();

        while (running) {
            System.out.println("Please Select the service You would like to have");
            System.out.println("1. Check for Appointment");
            System.out.println("2. Complete the appointment session");
            System.out.println("3. Search Appointments");

            Scanner scanner = new Scanner(System.in);
            System.out.println();
            System.out.print("Type the selection number (1/2/3): ");
            String selection = scanner.nextLine();


            if (selection.equals("1") || selection.equals("2") || selection.equals("3")) {
                // option 1 booking Appointment
                if(selection.equals("1")){
                    System.out.println();
                    System.out.println("*** check for appointment is selected ***");
                    System.out.println();

                    String selectedDoctor;
                    String doctorsName="";


                    while (true) {
                        System.out.print("Type 1 to select Dr " + DoctorOne.getName() + " or Type 2 to select Dr " + DoctorTwo.getName() + " : ");
                        selectedDoctor = scanner.nextLine();
                        if (selectedDoctor.equals("1") || selectedDoctor.equals("2")) {
                            if(selectedDoctor.equals("1")){
                                doctorsName=DoctorOne.getName();
                                System.out.println();
                                System.out.println("**** Dr "+DoctorOne.getName()+" is selected ****");
                                System.out.println();
                            }
                            else {
                                doctorsName=DoctorTwo.getName();
                                System.out.println();
                                System.out.println("**** Dr "+DoctorTwo.getName()+" is selected ****");
                                System.out.println();
                            }
                            break;
                        } else {
                            System.out.println("Invalid input. Please enter 1 or 2.");
                        }
                    }

                    String selectedDay;
                    while (true) {
                        System.out.print("Select Day (1. Monday 2. Wednesday 3. Friday 4. Saturday): ");
                        selectedDay = scanner.nextLine();
                        if (selectedDay.equals("1") ||
                            selectedDay.equals("2") ||
                            selectedDay.equals("3") ||
                            selectedDay.equals("4")) {
                            break;
                        } else {
                            System.out.println("Invalid input. Please enter 1, 2, 3, or 4.");
                        }
                    }

                    System.out.println();

                    ArrayList<Appointment> doctorOneAppointments= DoctorsAppointments.get(selectedDoctor);
                    ArrayList<Appointment>searchedAppointments= searchAppointmentsByDate(doctorOneAppointments,Integer.parseInt(selectedDay));
                    System.out.println("*** Appointments on "+getCorrespondingDay(selectedDay)+" for Dr "+doctorsName+" *** ");
                    System.out.println();
                    System.out.printf("%-20s %-20s %-20s %-20s%n",
                            "Appointment Number", "Session", "Appointment Status", "Has Been Booked");
                    System.out.println("---------------------------------------------------------------");
                    // Print each appointment
                    for (Appointment session : searchedAppointments) {
                        System.out.printf("%-20s %-20s %-20s %-20s%n",
                                session.getAppointmentId(),
                                session.getSession(),
                                session.getAppointmentStatus(),
                                session.getAppointmentConfirmed());
                    }

                    String accepted;
                    while (true) {
                        System.out.print("Would you like to make an appointment (Y/N): ");
                        accepted = scanner.nextLine();
                        if (accepted.equalsIgnoreCase("Y") || accepted.equalsIgnoreCase("N")) {
                            break;
                        } else {
                            System.out.println("Invalid input. Please enter Y or N.");
                        }
                    }

                    String appointmentNumber;
                    if(accepted.equalsIgnoreCase("Y")){

                        while (true) {
                            System.out.print("Please type the appointment number :");
                            appointmentNumber = scanner.nextLine();
                            // checking the appointment is booked already
                            ArrayList<Appointment> bookedAppointments=findAppointmentsByIdName(DoctorsAppointments,appointmentNumber);
                            Appointment isItBooked= bookedAppointments.getFirst();

                            if (!isItBooked.getAppointmentConfirmed()) {
                                break;
                            } else {
                                System.out.println("Selected Appointment is not available , please select a different Appointment number");
                            }
                        }

                        System.out.print("Patient's Name :");
                        String name=scanner.nextLine();
                        System.out.print("Patient's Email :");
                        String email=scanner.nextLine();
                        System.out.print("Patient's Mobile :");
                        String mobile=scanner.nextLine();
                        System.out.print("Patient's NIC :");
                        String nic=scanner.nextLine();

                        Patient patient=new Patient(nic,email,mobile,name);
                        Payment payment=new Payment(500);
                        Appointment retrieved= updateAppointment(
                                DoctorsAppointments,selectedDoctor,Integer.parseInt(appointmentNumber),payment,patient);

                        System.out.println();
                        System.out.println("Receipt is generated , please check the invoice");
                        generateInvoice(
                                appointmentNumber,
                                retrieved.getSession(),patient.getName(),doctorsName,500.00,
                                filePath,getCorrespondingDay(String.valueOf(retrieved.getConsultationId())));
                        System.out.println();
                        // confirmed Appointment will displayed here
                        System.out.printf("%-20s %-20s %-20s %-20s%n",
                                "Appointment Number", "Session", "Appointment Status", "Has Been Booked");

                        System.out.println("---------------------------------------------------------------");
                        // Print each appointment
                        for (Appointment session : searchedAppointments) {
                            System.out.printf("%-20s %-20s %-20s %-20s%n",
                                    session.getAppointmentId(),
                                    session.getSession(),
                                    session.getAppointmentStatus(),
                                    session.getAppointmentConfirmed());
                        }
                        System.out.println();
                        System.out.println();
                        System.out.println("*** Appointment is booked successfully *** ");
                        System.out.println("*** ---------------------------------- *** ");
                        System.out.println("-------------------------------------------");
                        System.out.println();
                        System.out.println();
                    }



                }

                // option 2 complete a booked appointment
                else if (selection.equals("2")){
                    Treatment treatmentOne=new Treatment("Acne Treatment",2750.00);
                    Treatment treatmentTwo=new Treatment("Skin Whitening",7650.00);
                    Treatment treatmentThree=new Treatment("Mole Removal",3850.00);
                    Treatment treatmentFour=new Treatment("Laser Treatment",12500.00);

                    System.out.println();
                    System.out.println("*** Complete the appointment session is selected ***");
                    System.out.println();

                    System.out.print("Please enter the Appointment Number :");
                    String appointmentNumber=scanner.nextLine();
                    Appointment appointment=findAppointmentsById(DoctorsAppointments,Integer.parseInt(appointmentNumber));
                    if(appointment==null){
                        System.out.print("No Appointments found !");
                        return;
                    }
                    printAppointmentDetails(appointment);

                    String treatmentType;
                    while (true) {
                        System.out.println("*** Please select Treatment type ***");
                        System.out.println("1. " + treatmentOne.getName() + "  LKR " + treatmentOne.getPrice() + "/-");
                        System.out.println("2. " + treatmentTwo.getName() + "  LKR " + treatmentTwo.getPrice() + "/-");
                        System.out.println("3. " + treatmentThree.getName() + "  LKR " + treatmentThree.getPrice() + "/-");
                        System.out.println("4. " + treatmentFour.getName() + "  LKR " + treatmentFour.getPrice() + "/-");

                        System.out.print("Treatment type (1/2/3/4): ");
                        treatmentType = scanner.nextLine().trim();

                        // Validate if the input is one of the valid options
                        if (treatmentType.equals("1") || treatmentType.equals("2") || treatmentType.equals("3") || treatmentType.equals("4")) {
                            break; // Exit the loop if a valid option is selected
                        } else {
                            System.out.println("Invalid input. Please select a valid treatment option (1/2/3/4).");
                        }
                    }



                    System.out.println(treatmentType+"treatmentType");
                    if(treatmentType.equals("1")){
                        completeAppointment(DoctorsAppointments,appointment,treatmentOne);
                    }
                    if(treatmentType.equals("2")){
                        completeAppointment(DoctorsAppointments,appointment,treatmentTwo);
                    }
                    if(treatmentType.equals("3")){
                        completeAppointment(DoctorsAppointments,appointment,treatmentOne);
                    }
                    if(treatmentType.equals("4")){
                        completeAppointment(DoctorsAppointments,appointment,treatmentOne);
                    }

                    Appointment updatedAppointment=findAppointmentsById(DoctorsAppointments,Integer.parseInt(appointmentNumber));
                    printAppointmentDetails(updatedAppointment);

                    try {
                        generateFinalInvoice(updatedAppointment);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }








                }

                // option 3 search appointments
                // Patient's name or Appointment ID
                // Date range
                else if (selection.equals("3")) {
                    System.out.println();
                    System.out.println("*** Search Appointments is selected ***");
                    System.out.println();

                    String searchSelection = "";
                    while (true) {
                        System.out.println("How would you like to search?");
                        System.out.println("1. Search with Appointment number or Patient's name");
                        System.out.println("2. Search with Date range");
                        System.out.println();
                        System.out.print("Select the Search option (1/2): ");
                        searchSelection = scanner.nextLine().trim();

                        // Validate if the input is either "1" or "2"
                        if (searchSelection.equals("1") || searchSelection.equals("2")) {
                            break;
                        } else {
                            System.out.println("Invalid input. Please select a valid search option (1 or 2).");
                        }
                    }



                    // type Appointment Number or Patient's Name
                    if(searchSelection.equals("1")){
                        System.out.println("Type Appointment Number or Patient's Name : ");
                        String appointmentNumber = scanner.nextLine();
                        ArrayList<Appointment> appointments=findAppointmentsByIdName(DoctorsAppointments,appointmentNumber);
                        if(appointments.isEmpty()){
                            System.out.println();
                            System.out.println("-------- Could not find Appointment Please check again ----------");
                            System.out.println();
                        }
                        System.out.println();


                        System.out.printf("%-20s %-20s %-20s %-20s %-20s %-20s%n",
                                "Appointment ID", "Date", "Session", "Status", "Booked By", "Doctor");
                        System.out.println("-------------------------------------------------------------------------------------------------------------");


                        for (Appointment appointment : appointments) {
                            System.out.printf("%-20s %-20s %-20s %-20s %-20s %-20s%n",
                                    appointment.getAppointmentId(),
                                    getCorrespondingDay(String.valueOf(appointment.getConsultationId())),
                                    appointment.getSession(),
                                    appointment.getAppointmentStatus(),
                                    (appointment.getPatient() != null ? appointment.getPatient().getName() : "Available"),
                                    (appointment.getDoctorId() == 1 ?"Dr "+ DoctorOne.getName() :"Dr "+ DoctorTwo.getName()));
                        }
                        System.out.println("---------------------------------------------------------------------------------------------------------------");
                        System.out.println();
                    }
                    // search date range and view details
                    if(searchSelection.equals("2")){
                        System.out.println("Select the Dates ");
                        for (ConsultationDay consultation: consultations){
                            System.out.println(consultation.getId()+". "+"Day : "+consultation.getDay());
                        }
                        System.out.print("Type the Selection Number (1/2/3/4) eg:- 1,2 : ");
                        String selectedRange = scanner.nextLine();
                        List<Integer> consultationIds= convertStringToArray(selectedRange);
                        System.out.print("Type Start time :");
                        String startTime = scanner.nextLine();
                        System.out.print("Type End time :");
                        String endTime = scanner.nextLine();

                        System.out.print("Type 1 to select Dr "+DoctorOne.getName()+" Type 2 to select Dr "+DoctorTwo.getName()+" : ");
                        String selectedDoctor = scanner.nextLine();
                        ArrayList<Appointment> doctorOneAppointments= DoctorsAppointments.get(selectedDoctor);
                        ArrayList<Appointment>searchedAppointments= searchAppointmentsByRange(doctorOneAppointments,consultationIds,startTime,endTime);
                        String doctorsName="";
                        if(selectedDoctor.equals("1")){
                            doctorsName=DoctorOne.getName();
                        }else {
                            doctorsName=DoctorTwo.getName();
                        }

                        System.out.println("*** Appointments on for Dr "+doctorsName+" within the selected date ranges *** ");
                        System.out.println();

                        System.out.printf("%-20s %-20s %-20s %-20s %-20s %-20s%n",
                                "Appointment ID", "Date", "Session", "Status", "Booked By", "Doctor");
                        System.out.println("-------------------------------------------------------------------------------------------------------------");

                        for (Appointment appointment : searchedAppointments) {
                            System.out.printf("%-20s %-20s %-20s %-20s %-20s %-20s%n",
                                    appointment.getAppointmentId(),
                                    getCorrespondingDay(String.valueOf(appointment.getConsultationId())),
                                    appointment.getSession(),
                                    appointment.getAppointmentStatus(),
                                    (appointment.getPatient() != null ? appointment.getPatient().getName() : "Not Booked"),
                                    (appointment.getDoctorId() == 1 ?"Dr "+ DoctorOne.getName() :"Dr "+ DoctorTwo.getName()));
                        }
                        System.out.println("---------------------------------------------------------------------------------------------------------------");
                        System.out.println();

                        System.out.print("Enter Appointment Number to view details :");
                        String appointmentId=scanner.nextLine();
                        if(appointmentId!=null){
                            Appointment appointment=findAppointmentsById(DoctorsAppointments,Integer.parseInt(appointmentId));
                            printAppointmentDetails(appointment);
                        }





                    }



                }

            } else {
                System.out.println("Invalid input. Please enter 1, 2, or 3.");
                System.out.println(); // Add spacing for readability
            }

        }

}}