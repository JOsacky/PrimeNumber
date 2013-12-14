package com.osacky.factor;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by suketk on 12/14/13.
 */
@ParseClassName("Factors")
public class Factors extends ParseObject
{
    public Factors()
    {}

    public void setKey(long key)
    {
        put("key", key);
    }

    public long getKey()
    {
        return getLong("key");
    }

    public void setValue(ArrayList<Long> arrayList)
    {
        put("value", arrayList);
    }

    public JSONArray getValue()
    {
        return getJSONArray("value");
    }
}
