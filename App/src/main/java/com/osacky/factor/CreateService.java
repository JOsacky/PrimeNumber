package com.osacky.factor;

import android.app.IntentService;
import android.content.Intent;

import com.parse.ParseObject;

/**
 * Created by suketk on 12/15/13.
 */
public class CreateService extends IntentService
{

    public CreateService()
    {
        super("CreateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long number = intent.getLongExtra(getString(R.string.parse_object_number), -1);
        long num_threads = intent.getLongExtra(getString(R.string.parse_object_threads), -1);
        long sqrt = (long) Math.ceil(Math.sqrt(number));

        //puts number, low, high, to_compute in our database for each thread
        for (int i = 0; i < num_threads; i++) {
            double low = (double) (i) / (double) num_threads;
            double high = (double) (i+1) / (double) num_threads;

            ParseObject create = new ParseObject(getString(R.string.parse_object));
            create.put(getString(R.string.parse_object_number), number);
            create.put(getString(R.string.parse_object_low), (long) (sqrt*low));
            create.put(getString(R.string.parse_object_high), (long) (sqrt*high));
            create.put(getString(R.string.parse_object_to_compute), true);
            create.saveInBackground();
        }
    }
}
