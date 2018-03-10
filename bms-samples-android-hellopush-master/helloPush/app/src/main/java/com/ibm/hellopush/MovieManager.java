package com.ibm.hellopush;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akhilanand on 28/02/18.
 */

public class MovieManager {

    private List<MovieItems> results;

    public static MovieManager thisInstance=null;

    private String userId;


    private MovieManager() {
        results = new ArrayList<>();
    }


    public static MovieManager getInstance(){
        if(thisInstance==null){
            thisInstance = new MovieManager();
        }
        return  thisInstance;
    }

    public List<MovieItems> getResults() {
        return results;
    }

    public void addResults(MovieItems movieitem) {
        results.add(movieitem);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
