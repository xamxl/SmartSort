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

    public void setFormName(String formName) {
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

    public void setSortTypes(String sortTypes) {
        Gson gson = new Gson();
        this.sortTypes = gson.fromJson(sortTypes, String[].class);
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String options) {
        Gson gson = new Gson();
        this.options = gson.fromJson(options, String[].class);
    }

    public String[] getTexts() {
        return texts;
    }

    public void setTexts(String texts) {
        Gson gson = new Gson();
        this.texts = gson.fromJson(texts, String[].class);
    }

    public String[] getInputTypes() {
        return inputTypes;
    }

    public void setInputTypes(String inputTypes) {
        Gson gson = new Gson();
        this.inputTypes = gson.fromJson(inputTypes, String[].class);
    }

}
