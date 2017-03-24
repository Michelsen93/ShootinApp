package com.example.ole_martin.shootinapp.Java_Classes;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Sindre on 24.03.2017.
 */




public class Club {

    String email;
    String clubName;
    String address;

    Person contactPerson;
    List<Competition> competitions;

    public Club(String email, String clubname, String address, Person cPerson){
        this.email = email;
        this.clubName = clubname;
        this.address = address;
        this.contactPerson = cPerson;
        competitions = new ArrayList<Competition>();
    }

}
