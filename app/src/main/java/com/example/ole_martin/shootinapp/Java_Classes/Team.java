package com.example.ole_martin.shootinapp.Java_Classes;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Sindre on 24.03.2017.
 */

public class Team {

    int teamNr;
    int competitionNr;
    int startTime;
    List<Contestant> teamMembers;

    // Varsel skytetid?

    public Team(int tNr, int cNr, int sTime){
        this.teamNr = tNr;
        this.competitionNr = cNr;
        this.startTime = sTime;
        teamMembers = new ArrayList<Contestant>();
    }
}
