package com.osukastudios.mobileafia.Model;

public class User {

    private String id, name, email, idnumber, phonenumber, employeenumber,
            timeavailable, department, specialist, type, search, profilepictureurl;

    public User() {
    }
    public User(String id, String name, String email, String idnumber, String phonenumber,
                String employeenumber, String timeavailable, String department,
                String specialist, String type, String search, String profilepictureurl) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.idnumber = idnumber;
        this.phonenumber = phonenumber;
        this.employeenumber = employeenumber;
        this.timeavailable = timeavailable;
        this.department = department;
        this.specialist = specialist;
        this.type = type;
        this.search = search;
        this.profilepictureurl = profilepictureurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdnumber() {
        return idnumber;
    }

    public void setIdnumber(String idnumber) {
        this.idnumber = idnumber;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getEmployeenumber() {
        return employeenumber;
    }

    public void setEmployeenumber(String employeenumber) {
        this.employeenumber = employeenumber;
    }

    public String getTimeavailable() {
        return timeavailable;
    }

    public void setTimeavailable(String timeavailable) {
        this.timeavailable = timeavailable;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSpecialist() {
        return specialist;
    }

    public void setSpecialist(String specialist) {
        this.specialist = specialist;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getProfilepictureurl() {
        return profilepictureurl;
    }

    public void setProfilepictureurl(String profilepictureurl) {
        this.profilepictureurl = profilepictureurl;
    }
}
