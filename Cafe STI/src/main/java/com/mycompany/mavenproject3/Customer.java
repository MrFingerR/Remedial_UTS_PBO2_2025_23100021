/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;

/**
 *
 * @author ASUS
 */
public class Customer {
    private int id;
    private String name;
    private boolean gender;
    private int umur;

    // Konstruktor sesuai dengan penggunaan di CustomerForm
    public Customer(int id, String name, boolean gender, int umur) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.umur = umur;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean getGender() {
        return gender;
    }

    public int getumur() {
        return umur;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public void setUmur(int umur) {
        this.umur = umur;
    }

    public String getGenderString() {
        return gender ? "Male" : "Female";
    }
}


