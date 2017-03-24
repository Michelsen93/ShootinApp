package com.example.ole_martin.shootinapp.Java_Classes;

/**
 * Created by Sindre on 24.03.2017.
 */

public class Scorecard {

    // Fortsatt usikker hva vi skal kalle dette?
    //List<standplass> standplasser;

    Weapongroup weapongroup;
    ShootingClass shootingClass;
    int sumScore;

    // sumMerke
    int sumBullseye;

    // The person who owns the card's nr
    int unique_ID;

    public Scorecard(Weapongroup wGroup, ShootingClass sClass, int sumScore, int sumBullseye, int unique_ID){
        this.weapongroup = wGroup;
        this.shootingClass = sClass;
        this.sumScore = sumScore;
        this.sumBullseye = sumBullseye;
        this.unique_ID = unique_ID;
    }

}
