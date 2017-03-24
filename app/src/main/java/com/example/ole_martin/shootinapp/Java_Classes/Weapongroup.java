package com.example.ole_martin.shootinapp.Java_Classes;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Sindre on 24.03.2017.
 */

public class Weapongroup {

    String description;
    List<String> weaponNames;
    int numberOfShots;

    public Weapongroup(String desc, int nShots){
        this.description = desc;
        this.numberOfShots = nShots;

        weaponNames = new ArrayList<String>();
    }
}
