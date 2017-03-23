package com.example.ole_martin.shootinapp.beans;

import java.util.List;

/**
 * Created by ole-martin on 20.02.2017.
 */

public class ScoreCard {
    private List<Round> rounds;
    private String weaponClass;
    private String shooterClass;
    private int sumScore;
    private int sumMark;
    private int id;

    public ScoreCard(List<Round> rounds,String weaponClass, String shooterClass, int sumScore, int sumMark, int id){
        this.rounds = rounds;
        this.weaponClass = weaponClass;
        this.shooterClass = shooterClass;
        this.sumScore = sumScore;
        this.sumMark = sumMark;
        this.id = id;
    }
}
