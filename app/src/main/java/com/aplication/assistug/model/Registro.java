package com.aplication.assistug.model;

import androidx.annotation.Keep;

public class Registro {

    String apikey;
    String applicationid;
    String databaseurl;

    @Keep
    public Registro (){

    }

    public Registro(String apikey, String applicationid, String databaseurl) {
        this.apikey = apikey;
        this.applicationid = applicationid;
        this.databaseurl = databaseurl;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getApplicationid() {
        return applicationid;
    }

    public void setApplicationid(String applicationid) {
        this.applicationid = applicationid;
    }

    public String getDatabaseurl() {
        return databaseurl;
    }

    public void setDatabaseurl(String databaseurl) {
        this.databaseurl = databaseurl;
    }
}
