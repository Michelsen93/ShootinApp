package com.example.ole_martin.shootinapp.activity;

import android.app.Application;

import com.example.ole_martin.shootinapp.util.DAO;

/**
 * Created by ole-martin on 13.05.2017.
 */

public class FixedState extends Application{
    @Override
    public void onCreate(){
        super.onCreate();
        DAO dao = new DAO(this);


    }
}
