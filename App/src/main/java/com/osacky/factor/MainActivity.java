package com.osacky.factor;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.initialize(this, "7A5hnUGC6Zxg6OQhZjWnB2gHAYc7h2VOOxYcVlMg", "L6tf0XRvzq6xSqbmf8EkMzJxqIfMMUqP5J9pljMC");
        ParseAnalytics.trackAppOpened(getIntent());

//        Button compute_button = (Button) findViewById(R.id.compute);
//        Button create_button = (Button) findViewById(R.id.create);
//
//        create_button.setOnClickListener(new View.OnClickListener()
//        {
//            public void onClick(View v)
//            {
//                EditText num_threads = (EditText) findViewById(R.id.textThreads);
//                EditText prime_num = (EditText) findViewById(R.id.textNumber);
//
//                num_threads.getText();
//                buttonParse(Integer.parseInt(num_threads.getText().toString()), Long.parseLong(prime_num.getText().toString()), true);
//            }
//        });
//
//        compute_button.setOnClickListener(new View.OnClickListener()
//        {
//            public void onClick(View v)
//            {
//                buttonParse(0, 0, false);
//            }
//        });
    }

    public void create_computation(View v)
    {
        EditText thread_text = (EditText) findViewById(R.id.textThreads);
        EditText number_text = (EditText) findViewById(R.id.textNumber);

        long num_threads = Long.parseLong(thread_text.getText().toString());
        long number = Long.parseLong(number_text.getText().toString());

        ParseObject create = new ParseObject(getString(R.string.parse_object));
        create.put(getString(R.string.parse_object_number), number);
        create.put(getString(R.string.parse_object_threads), num_threads);
        create.put(getString(R.string.parse_object_rem_threads), num_threads);
        create.saveInBackground();
    }

    public void perform_computation(View v)
    {
        TextView textTime = (TextView) findViewById(R.id.textTime);
        TextView textProcessed = (TextView) findViewById(R.id.textNumbersProcessed);
        TextView textFactorFound = (TextView) findViewById(R.id.textFactorFound);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(getString(R.string.parse_object));
        query.whereGreaterThan(getString(R.string.parse_object_threads), 0);
        ParseObject computation = null;

        try
        {
            computation = query.getFirst();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        if(computation!=null)
        {
            long number;
            long num_threads;
            long diff_threads;
            long sqrt;

            computation.increment(getString(R.string.parse_object_rem_threads), -1);
            computation.saveInBackground();

            number = computation.getLong(getString(R.string.parse_object_number));
            num_threads = computation.getLong(getString(R.string.parse_object_threads));
            diff_threads = num_threads - computation.getInt(getString(R.string.parse_object_rem_threads));
            sqrt = (long) Math.ceil(Math.sqrt(number));
//            sqrt = number;

            Calendar t1 = Calendar.getInstance();
            long time1 = t1.getTimeInMillis();
            Log.e("time1", "" + time1);

            double low = (double) (diff_threads-1) / (double) num_threads;
            double high = (double) diff_threads / (double) num_threads;

            return_factors(number, (long) (sqrt*low), (long) (sqrt*high));

            Calendar t2 = Calendar.getInstance();
            long time2 = t2.getTimeInMillis();
            textTime.setText("Total Time: " + (time2 - time1));

            if (computation.getLong(getString(R.string.parse_object_rem_threads)) <= 0)
            {
                computation.deleteInBackground();
            }
        }
    }


    public List return_factors(long number, long low, long high)
    {
        List toRet = new ArrayList();

        ProgressBar mProgress;
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mProgress.setProgress(0);

        if(low==0 && number%2==0)
        {
            toRet.add(2);
            toRet.add(number>>1);
            low=2;
//            Log.e("prime number:", "2");
//            Log.e("prime number:", ""+ (num/2));
        }

        if(low%2==0)
            low = low+1;

        long onePercent = (high-low)/100;
        Log.e("hi: ", "" + high);
        Log.e("lo: ", "" + low);
        Log.e("oneP: ", "" + onePercent);

        for(long i=low; i<=high; i+=2)
        {
            if(i%onePercent==0)
                mProgress.incrementProgressBy(1);

            if(number%i == 0)
            {
                toRet.add(i);
                toRet.add(number / i);
//                Log.d("prime number:", "" + i);
//                Log.d("prime number:", "" + (num / i));

            }
        }
        mProgress.setProgress(100);
        return toRet;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
