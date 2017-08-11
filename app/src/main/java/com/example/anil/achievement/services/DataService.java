package com.example.anil.achievement.services;

import com.example.anil.achievement.models.Places;

import java.util.ArrayList;

/**
 * Created by anil on 08/08/2017.
 */

public class DataService
{
    private static final DataService instance = new DataService();

    public static DataService getInstance()
    {
        return instance;
    }

    private DataService()
    {
    }

    public ArrayList<Places> getPlacesInLocation(int location)
    {
        ArrayList<Places> placesArrayList = new ArrayList<>();
        placesArrayList.add( new Places( (float)53.010139, (float)-2.181438, "Staffordshire University", "College Rd, Stoke-on-Trent ST4 2DE", "tempurl" ) );
        placesArrayList.add( new Places( (float)53.016735, (float)-2.182314, "Grandads Off Licence The Watering Hole", "31 Wellesley St, Stoke-on-Trent ST1 4NF", "tempurl" ) );

        return placesArrayList;
    }
}
