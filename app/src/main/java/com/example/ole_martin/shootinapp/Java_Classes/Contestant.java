package com.example.ole_martin.shootinapp.Java_Classes;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Sindre on 24.03.2017.
 */

public class Contestant extends Person {

    List<Scorecard> scoreCards;
    //List<Pictures/Drawables?> scorecardPitures;

    public Contestant(String fname, String lname, String password, String unique_ID, Club club, String email, String tlfnr){
        super(fname, lname, password, unique_ID, club, email, tlfnr);
        scoreCards = new ArrayList<Scorecard>();
    }
}
