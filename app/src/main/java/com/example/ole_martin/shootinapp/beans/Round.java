package com.example.ole_martin.shootinapp.beans;

/**
 * Created by ole-martin on 20.02.2017.
 */

public class Round {
    private int number;
    private int hits;
    private int figures;
    private int mark;
    private RoundRules roundRules;

    public Round(int number, int hits, int figures, int mark, RoundRules roundRules){
        this.number = number;
        this.hits = hits;
        this.figures = figures;
        this.mark = mark;
        this.roundRules = roundRules;
    }

}
