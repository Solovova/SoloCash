package db.requests;

import com.google.gson.Gson;
import db.DBMain;
import db.dataclas.GsonContainer;

public class RequestParent {
    static String IDENTIFIER = "";
    DBMain db;
    RequestParent(DBMain db) {
        this.db = db;
    }
    public String answer(GsonContainer gsonContainer){
        return "";
    }

    String formatParamsToAnswer(String params) {
        System.out.println(new Gson().toJson(new GsonContainer(IDENTIFIER, params)));
        return new Gson().toJson(new GsonContainer(IDENTIFIER, params));
    }
}
