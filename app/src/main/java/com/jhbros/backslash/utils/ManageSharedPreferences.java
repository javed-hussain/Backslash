package com.jhbros.backslash.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by jhbros on 3/8/17.
 */

public class ManageSharedPreferences {
    private SharedPreferences sp;
    private SharedPreferences.Editor speditor;

    public ManageSharedPreferences(String name, Context ctx){
        sp=ctx.getSharedPreferences(name,Context.MODE_PRIVATE);
        speditor=sp.edit();

    }

    public void setValue(String key,int val){
        speditor.putInt(key,val).apply();
    }


    public void setValue(String key,boolean val){
        speditor.putBoolean(key,val).apply();
    }


    public void setValue(String key,String val){
        speditor.putString(key,val).apply();
    }


    public void setValue(String key,float val){
        speditor.putFloat(key,val).apply();
    }

    public int getval(String key, int def){
        return sp.getInt(key,def);
    }


    public String getval(String key, String def){
        return sp.getString(key,def);
    }


    public boolean getval(String key, boolean def){
        return sp.getBoolean(key,def);
    }


    public float getval(String key, float def){
        return sp.getFloat(key,def);
    }

}
