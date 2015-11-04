package com.example.sheepcao.dotaertest;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by ericcao on 11/3/15.
 */
public class SerializableMap implements Serializable {
    private JSONObject map;
    public JSONObject getMap()
    {
        return map;
    }
    public void setMap(JSONObject map)
    {
        this.map=map;
    }
}
