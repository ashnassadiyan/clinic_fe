package org.example;

import java.util.ArrayList;

public class ConsultationDay {
    private int id;
    private String day;
    private String startTime;
    private String endTime;

    public ConsultationDay(int id, String day, String startTime, String endTime) {
        this.id = id;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;

    }

     public int getId() {
         return id;
     }

     public void setId(int id) {
         this.id = id;
     }

     public String getDay() {
         return day;
     }

     public void setDay(String day) {
         this.day = day;
     }

     public String getStartTime() {
         return startTime;
     }

     public void setStartTime(String startTime) {
         this.startTime = startTime;
     }

     public String getEndTime() {
         return endTime;
     }

     public void setEndTime(String endTime) {
         this.endTime = endTime;
     }

    public static void printConsultationDays(ArrayList<ConsultationDay> consultations) {
        System.out.printf("%-5s %-10s %-10s %-10s%n", "ID", "Day", "Start Time", "End Time");
        System.out.println("-----------------------------------");

        for (ConsultationDay consultation : consultations) {
            System.out.printf("%-5d %-10s %-10s %-10s%n",
                    consultation.getId(),
                    consultation.getDay(),
                    consultation.getStartTime(),
                    consultation.getEndTime());
        }
    }

    public static String getCorrespondingDay(String day){
        return switch (day) {
            case "1" -> "Monday";
            case "2" -> "Wednesday";
            case "3" -> "Friday";
            default -> "Saturday";
        };

    }
 }
