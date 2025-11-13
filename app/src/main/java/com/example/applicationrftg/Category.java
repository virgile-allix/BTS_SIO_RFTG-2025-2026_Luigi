package com.example.applicationrftg;

import com.google.gson.annotations.SerializedName;


public class Category {

    @SerializedName("categoryId")
    private int categoryId;

    @SerializedName("name")
    private String name;

    // Constructeur par défaut requis par Gson
    public Category() {
    }

    // Getters et setters
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
