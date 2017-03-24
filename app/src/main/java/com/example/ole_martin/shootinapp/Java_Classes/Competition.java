package com.example.ole_martin.shootinapp.Java_Classes;

import java.util.List;

/**
 * Created by Sindre on 24.03.2017.
 */

public class Competition {

    List<Standplass> standplasser;
    java.util.Date date;
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

}
