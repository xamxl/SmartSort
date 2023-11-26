package com.example.smartsort;

import java.util.ArrayList;

import com.google.gson.Gson;

public class SubmitFormInput {
    private String formName;
    private String email;
    private ArrayList<String> submissions;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public ArrayList<String> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(String submissions) {
        System.out.println(submissions);
        Gson gson = new Gson();
        this.submissions = gson.fromJson(submissions, ArrayList.class);
        System.out.println(this.submissions);
    }
}
