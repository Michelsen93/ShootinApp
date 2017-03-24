package com.example.ole_martin.shootinapp.Java_Classes;

/**
 * Created by Sindre on 24.03.2017.
 */

public class Person {

    String foreName;
    String lastName;
    String password;
    String unique_ID;
    Club club;
    String email;
    String tlfNr;
    // Drawable picture

    public Person(String fname, String lname, String password, String unique_ID, Club club, String email, String tlfnr){
        this.foreName = fname;
        this.lastName = lname;
        this.password = password;
        this.unique_ID = unique_ID;
        this.club = club;
        this.email = email;
        this.tlfNr = tlfnr;
    }
}
