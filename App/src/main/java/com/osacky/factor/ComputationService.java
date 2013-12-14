package com.osacky.factor;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.parse.ParseObject;

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
        long num_threads = workIntent.getLongExtra(getString(R.string.parse_object_threads), -1);
        long rem_threads = workIntent.getLongExtra(getString(R.string.parse_object_rem_threads), -1);
        long sqrt = workIntent.getLongExtra(getString(R.string.parse_object_sqrt), -1);
        long diff_threads = num_threads - rem_threads;

        double low = (double) (diff_threads-1) / (double) num_threads;
        double high = (double) diff_threads / (double) num_threads;

        return_factors(number, (long) (sqrt*low), (long) (sqrt*high));

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
//        Log.e("time1", "" + time1);

        ArrayList<Long> toRet = new ArrayList<Long>();
//        ParseObject factors = new ParseObject(getString(R.string.parse_factors));
//        factors.put(getString(R.string.parse_factors_key), number);

        if(low==0)
        {
            low=2;
            if(number%2==0)
            {
                toRet.add(low);
                toRet.add(number/2);
                ParseObject create = new ParseObject("Factors");
                create.put("prime_number", number);
                create.put("factor", low);
                create.saveInBackground();
                ParseObject create2 = new ParseObject("Factors");
                create2.put("prime_number", number);
                create2.put("factor", number/2);
                create2.saveInBackground();
                Log.e("Factor:", "2");
                Log.e("Factor:", ""+ (number/2));
            }
        }

        if(low%2==0)
            low = low+1;

        long one_percent = (high-low)/100;

        if(one_percent%2==1)
            one_percent++;

        long target = low+one_percent;

        for(long i=low; i<=high; i+=2)
        {
            if(i==target)
            {
                target+=one_percent;

                Calendar t2 = Calendar.getInstance();
                long time2 = t2.getTimeInMillis();
      //        Log.e("time2", "" + time2);

                Intent broadcast_intent = new Intent();
                broadcast_intent.setAction(getString(R.string.broadcast_action));
                broadcast_intent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcast_intent.putExtra(getString(R.string.progress), "");
                broadcast_intent.putExtra(getString(R.string.num_proc), i-low);
                broadcast_intent.putExtra(getString(R.string.time_taken), time2 - time1);
                if(!toRet.isEmpty())
                    broadcast_intent.putExtra(getString(R.string.factor), toRet.get(toRet.size()-1));
                sendBroadcast(broadcast_intent);
            }

            if(number%i == 0)
            {

                toRet.add(i);
                toRet.add(number / i);
                ParseObject create = new ParseObject("Factors");
                create.put("prime_number", number);
                create.put("factor", i);
                create.saveInBackground();
                ParseObject create2 = new ParseObject("Factors");
                create2.put("prime_number", number);
                create2.put("factor", number/i);
                create2.saveInBackground();
                Log.e("Factor:", "" + i);
                Log.e("Factor:", "" + (number/i));
            }
        }
//
//        factors.put(getString(R.string.parse_factors_value), toRet);
//        factors.saveInBackground();
    }
}
