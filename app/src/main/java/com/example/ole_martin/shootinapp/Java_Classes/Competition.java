package com.example.ole_martin.shootinapp.Java_Classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Created by Sindre on 24.03.2017.
 */

public class Competition {

    List<Standplass> standplasser;
    Date date;
    List<Weapongroup> weapongroups;
    int competitionNumber;
    Club club;
    List<Contestant> contestants;
    List<Team> teams;
    List<Person> referees;
    List<Person> competitionLeaders;
    String competitionType;
    String program;
    String location;

    // Cash or medal
    String prize;

    // Enum: Felt eller bane?
    Discipline discipline;
    Boolean active;

    public Competition(Date date, int competitionNumber, Club club, String competitionType, String program, String location){
        this.date = date;
        this.competitionNumber = competitionNumber;
        this.club = club;
        this.competitionType = competitionType;
        this.program = program;
        this.location = location;

        standplasser = new ArrayList<Standplass>();
        weapongroups = new ArrayList<Weapongroup>();
        contestants = new ArrayList<Contestant>();
        teams = new ArrayList<Team>();
        referees = new ArrayList<Person>();
        competitionLeaders = new ArrayList<Person>();
    }

}
