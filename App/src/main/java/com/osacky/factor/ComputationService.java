package com.osacky.factor;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by suketk on 12/13/13.
 */
public class ComputationService extends IntentService
{
    public ComputationService()
    {
        super("ComputationService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        long number = workIntent.getLongExtra(getString(R.string.parse_object_number), -1);
        long low = workIntent.getLongExtra(getString(R.string.parse_object_low), -1);
        long high = workIntent.getLongExtra(getString(R.string.parse_object_high), -1);

        return_factors(number, low, high);

        Intent broadcast_intent = new Intent();
        broadcast_intent.setAction(getString(R.string.broadcast_action));
        broadcast_intent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcast_intent.putExtra(getString(R.string.done_broadcast), 0);
        sendBroadcast(broadcast_intent);
    }

    public void return_factors(long number, long low, long high)
    {
        Calendar t1 = Calendar.getInstance();
        long time1 = t1.getTimeInMillis();

        long one_percent = (high-low)/100;

        long last_factor = -1;
        if(low==0)
            low=1;

        for(long i=low; i<high; i++)
        {
            if(i%one_percent==0)
            {
                Calendar t2 = Calendar.getInstance();
                long time2 = t2.getTimeInMillis();

                Intent broadcast_intent = new Intent();
                broadcast_intent.setAction(getString(R.string.broadcast_action));
                broadcast_intent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcast_intent.putExtra(getString(R.string.progress), "");
                broadcast_intent.putExtra(getString(R.string.num_proc), i-low);
                broadcast_intent.putExtra(getString(R.string.time_taken), time2 - time1);
                broadcast_intent.putExtra(getString(R.string.factor), last_factor);
                sendBroadcast(broadcast_intent);
            }

            if(number%i == 0)
            {
                last_factor=i;

                ParseObject factor1 = new ParseObject(getString(R.string.parse_factors));
                factor1.put(getString(R.string.parse_factors_key), number);
                factor1.put(getString(R.string.parse_factors_value), i);

                ParseObject factor2 = new ParseObject(getString(R.string.parse_factors));
                factor2.put(getString(R.string.parse_factors_key), number);
                factor2.put(getString(R.string.parse_factors_value), number / i);

                factor1.saveInBackground();
                factor2.saveInBackground();
            }
        }

        Calendar t2 = Calendar.getInstance();
        long time2 = t2.getTimeInMillis();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(getString(R.string.parse_object));
        query.whereEqualTo(getString(R.string.parse_object_number), number);
//        query.whereEqualTo(getString(R.string.parse_object_low), low);
        query.whereEqualTo(getString(R.string.parse_object_high), high);

        ParseObject computed = null;
        try{
            computed = query.getFirst();
        }
        catch (ParseException e){
            e.printStackTrace();
        }

        computed.put(getString(R.string.parse_object_thread_time), time2-time1);
        computed.saveInBackground();
    }
}
