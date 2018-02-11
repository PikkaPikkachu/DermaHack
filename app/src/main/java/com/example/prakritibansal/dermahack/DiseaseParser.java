package com.example.prakritibansal.dermahack;

/**
 * Created by Dell on 2/11/2018.
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dell on 2/11/2018.
 */

public class DiseaseParser {

    public static ArrayList<String> diseaseList = new ArrayList<String>();
    public static ArrayList<String> descriptionList = new ArrayList<String>();

    public void DiseaseParser (JSONObject object) throws JSONException {

        diseaseList = new ArrayList<>();
        HashMap<Integer,String> temp1 = new HashMap<Integer, String>(), temp2 = new HashMap<Integer, String>();
        JSONArray jsonArray = object.getJSONArray("response");

        for (int i = 0; i < jsonArray.length() ; i++) {
            JSONObject obj = (JSONObject) jsonArray.get(i);
            String disease_name = obj.getString("disease_name");
            String description_name = obj.getString("treatment_info");
            Log.i("Desc", description_name);
            diseaseList.add(disease_name);
            descriptionList.add(description_name);
        }


    }


}
