package com.example.ole_martin.shootinapp.beans;

/**
 * Created by ole-martin on 20.02.2017.
 */

public abstract class  Person {
    private String name;
    private String surname;
    private String id;
    private String club;

    public Person(String name, String surname, String id, String club){
        this.name = name;
        this.surname = surname;
        this.id = id;
        this.club = club;
    }

    public void viewResults(){

    }
    public void alterScore(){

    }

}
