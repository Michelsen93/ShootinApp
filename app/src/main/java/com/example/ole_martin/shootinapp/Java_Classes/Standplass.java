package com.example.ole_martin.shootinapp.Java_Classes;

/**
 * Created by Sindre on 24.03.2017.
 */

public class Standplass {

    int number;
    int maxHit;
    int numberOfFigures;

    // max innertreff
    int maxBullseyes;

    // Usikker på denne - skal være max tid for hver våpengruppe.
    int maxTimeWeaonsgroup;

    // Koordinater til hvor standplassen er

    // Styrk ønsket en stoppeklokke her

    public Standplass(int n, int max, int nFig, int maxBulls, int maxT){
        this.number = n;
        this.maxHit = max;
        this.numberOfFigures = nFig;
        this.maxBullseyes = maxBulls;
        this.maxTimeWeaonsgroup = maxT;
    }
}
