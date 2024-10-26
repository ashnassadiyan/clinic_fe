package org.example;

public class Patient {
    private static int idCounter = 1;
    private int id;
    private String nic;
    private String email;
    private String mobile;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Patient(String nic, String email, String mobile,String name) {
        this.id = idCounter++;
        this.nic = nic;
        this.email = email;
        this.mobile = mobile;
        this.name=name;
    }

    public Patient(){}
}
