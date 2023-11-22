package com.example.smartsort;

import com.google.gson.Gson;

public class CreateFormInput {
    private String[] sortTypes;
    private String[] options;
    private String[] texts;
    private String formName;
    private String idInstruct;
    private String[] inputTypes;
    private String email;
    private String key;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFormName() {
        return formName;
    }

    public void setForName(String formName) {
        this.formName = formName;
    }

    public String getIdInstruct() {
        return idInstruct;
    }

    public void setIdInstruct(String idInstruct) {
        this.idInstruct = idInstruct;
    }

    public String[] getSortTypes() {
        return sortTypes;
    }

    public void setSortTypes(String d) {
        Gson gson = new Gson();
        sortTypes = gson.fromJson(d, String[].class);
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String o) {
        Gson gson = new Gson();
        options = gson.fromJson(o, String[].class);
    }

    public String[] getTexts() {
        return texts;
    }

    public void setTexts(String t) {
        Gson gson = new Gson();
        texts = gson.fromJson(t, String[].class);
    }

    public String[] getInputTypes() {
        return inputTypes;
    }

    public void setInputTypes(String elements) {
        Gson gson = new Gson();
        inputTypes = gson.fromJson(elements, String[].class);
    }

}
