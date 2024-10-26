package org.example;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.example.ConsultationDay.getCorrespondingDay;

public class Appointment {
    private static int idCounter = 1;
    private int AppointmentId;
    private int doctorId;
    private Patient patient;
    private Treatment treatment;
    private Payment payment;
    private Boolean appointmentConfirmed;
    private String session;
    private String appointmentStatus;
    private int consultationId;

    public Treatment getTreatment() {
        return treatment;
    }

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public int getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(int consultationId) {
        this.consultationId = consultationId;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public Appointment(){}

    public Appointment(String session, int doctorId,int consultationId) {
        this.AppointmentId=idCounter++;
        this.session = session;
        this.doctorId = doctorId;
        this.consultationId=consultationId;
        this.appointmentConfirmed = false;
        this.appointmentStatus="Available";
    }

    public int getAppointmentId() {
        return AppointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        AppointmentId = appointmentId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }
    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Boolean getAppointmentConfirmed() {
        return appointmentConfirmed;
    }

    public void setAppointmentConfirmed(Boolean appointmentConfirmed) {
        this.appointmentConfirmed = appointmentConfirmed;
    }

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    // startTimeStr 10:00 endTimeStr endTimeStr
    // brakes 15 min slots , eg : 10:00-10.15 , 10.15-10.30
    // returns lists eg: ["10:00-10.15" , "10.15-10.30"]
    public static List<String> generateSessions(String startTimeStr, String endTimeStr) {
        List<String> sessions = new ArrayList<>();

        // Parse the String times to LocalTime
        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalTime endTime = LocalTime.parse(endTimeStr);

        LocalTime sessionStart = startTime;
        while (sessionStart.isBefore(endTime)) {
            LocalTime sessionEnd = sessionStart.plusMinutes(15);
            if (sessionEnd.isAfter(endTime)) {
                sessionEnd = endTime; // Ensure the last session doesn't go beyond the end time
            }
            sessions.add(sessionStart + " - " + sessionEnd);
            sessionStart = sessionEnd;
        }
        return sessions;
    }

//    Appointment will be updated
    public static Appointment updateAppointment(
            HashMap<String, ArrayList<Appointment>> doctorsAppointments,
            String doctorName, int appointmentId, Payment payment, Patient patient) {

        // Get the appointments for the doctor
        ArrayList<Appointment> appointments = doctorsAppointments.get(doctorName);

        // Check if the doctor exists in the map
        if (appointments != null) {
            // Iterate over the appointments
            for (Appointment appointment : appointments) {
                if (appointment.getAppointmentId() == appointmentId) {
                    // Update the appointment details
                    appointment.setAppointmentConfirmed(true);
                    appointment.setPayment(payment);
                    appointment.setPatient(patient);
                    return appointment;
                }
            }
            System.out.println("Appointment with ID " + appointmentId + " not found.");
        } else {
            System.out.println("Doctor " + doctorName + " not found.");
        }
        return null;
    }

    public static void completeAppointment(
            HashMap<String, ArrayList<Appointment>> doctorsAppointments,
            Appointment selectedAppointment, Treatment treatment) {

        // Print out treatment price
        System.out.println("Treatment price: " + treatment.getPrice());
        for (ArrayList<Appointment> appointments : doctorsAppointments.values()) {
            for (Appointment appointment : appointments) {

                if (appointment.getAppointmentId()==selectedAppointment.getAppointmentId()) {
                    if (appointment.getPayment() != null) {
                        appointment.getPayment().setTreatmentCharge(treatment.getPrice());
                        appointment.setAppointmentStatus("Completed");
                        appointment.setTreatment(treatment);

                    }
                    System.out.println("Updated appointment ID: " + appointment.getAppointmentId() +
                            " with treatment charge: " + treatment.getPrice()+"  "+appointment.getTreatment()==null?
                            "Not Treated":appointment.getTreatment().getName());
                    return;
                }
            }
        }
        System.out.println("No appointment found with ID: " + selectedAppointment.getAppointmentId());
    }

    public static ArrayList<Appointment> searchAppointmentsByDate(
            ArrayList<Appointment> appointments, int consultationId) {
        ArrayList<Appointment> foundAppointments = new ArrayList<>();

        for (Appointment appointment : appointments) {

            int consultation = appointment.getConsultationId();

            if (consultationId==consultation) {
                foundAppointments.add(appointment);
            }
        }
        return foundAppointments;
    }

    //    search by range
    public static ArrayList<Appointment>
    searchAppointmentsByRange(ArrayList<Appointment> appointments,
                              List<Integer> consultationIds,
                              String startTime, String endTime) {
        ArrayList<Appointment> foundAppointments = new ArrayList<>();

        // Parse the provided start and end times to LocalTime
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        for (Appointment appointment : appointments) {
            int consultationId = appointment.getConsultationId();

            // Check if the consultationId is in the provided list of consultationIds
            if (consultationIds.contains(consultationId)) {
                // Split the session time (assuming it is in the format "HH:mm - HH:mm")
                String[] sessionTimes = appointment.getSession().split(" - ");
                if (sessionTimes.length == 2) {
                    // Parse the start and end times of the session
                    LocalTime sessionStart = LocalTime.parse(sessionTimes[0].trim());
                    LocalTime sessionEnd = LocalTime.parse(sessionTimes[1].trim());

                    // Check if the session overlaps with the provided time range
                    if (!sessionStart.isAfter(end) && !sessionEnd.isBefore(start)) {
                        foundAppointments.add(appointment); // Add the appointment if within range
                    }
                } else {
                    System.out.println("Invalid session time format for appointment ID: " + appointment.getAppointmentId());
                }
            }
        }

        return foundAppointments;
    }

    // search appointments with Appointment id or Patient's name
    public static ArrayList<Appointment> findAppointmentsByIdName(HashMap<String,
            ArrayList<Appointment>> doctorsAppointments, String search) {
        ArrayList<Appointment> matchedAppointments = new ArrayList<>();
        boolean isNumeric = false;

//        checking passed value is character or number
        try {
            Integer.parseInt(search);
            isNumeric = true;  // search is an integer
        } catch (NumberFormatException e) {

        }

        // Loop through the appointments and match based on search criteria
        for (ArrayList<Appointment> appointments : doctorsAppointments.values()) {
            for (Appointment appointment : appointments) {
                if (isNumeric) {
                    // If search is a number, compare appointmentId
                    if (appointment.getAppointmentId() == Integer.parseInt(search)) {
                        matchedAppointments.add(appointment);
                    }
                } else {
                    // If search is not a number, compare patient's name
                    if (appointment.getPatient() != null && appointment.getPatient().getName().equalsIgnoreCase(search)) {
                        matchedAppointments.add(appointment);
                    }
                }
            }
        }

        return matchedAppointments;
    }

    public static Appointment findAppointmentsById(HashMap<String, ArrayList<Appointment>> doctorsAppointments, int id) {
        Appointment searchAppointment=new Appointment();
        for (ArrayList<Appointment> appointments : doctorsAppointments.values()) {
            for (Appointment appointment : appointments) {
                if (appointment.getAppointmentId() == id) {
                    searchAppointment=appointment;
                    return appointment ;
                }
            }
        }
        return searchAppointment;
    }

    public static void printAppointmentDetails(Appointment appointment){

        System.out.println();
        System.out.println();
        System.out.printf("%-15s %-20s %-20s %-20s %-25s %-15s%n",
                "Appointment ID", "Patient Name", "Doctor Name", "Consultation Session", "Status","Treatment");
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-15s %-20s %-20s %-20s %-25s %-15s%n",
                appointment.getAppointmentId(),
                appointment.getPatient() != null ? appointment.getPatient().getName() : "No Patient",
                appointment.getDoctorId()==1?"janaka":"sumana",
                getCorrespondingDay(String.valueOf(appointment.getConsultationId()))+" "+appointment.getSession(),
                appointment.getAppointmentStatus(),
                appointment.getTreatment()!=null?appointment.getTreatment().getName():"Not treated"
        );
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println();
        System.out.println();
    }



}
