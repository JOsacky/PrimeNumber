package com.osacky.factor;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Jonathan on 12/3/13.
 */
@ParseClassName("TheNumber")
public class TheNumber extends ParseObject {

    public TheNumber(){
    }

    public int getNumber()
    {
        return getInt("number");
    }

    public void setNumber(int number)
    {
        put("number", number);
    }

    public int getThreads()
    {
        return getInt("threads");
    }

    public void setThreads(int threads)
    {
        put("threads", threads);
    }
}
