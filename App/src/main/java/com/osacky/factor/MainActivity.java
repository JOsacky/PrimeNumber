package com.osacky.factor;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
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

//        ParseObject.registerSubclass(TheNumber.class);
        Parse.initialize(this, "7A5hnUGC6Zxg6OQhZjWnB2gHAYc7h2VOOxYcVlMg", "L6tf0XRvzq6xSqbmf8EkMzJxqIfMMUqP5J9pljMC");
        ParseAnalytics.trackAppOpened(getIntent());

        Button button = (Button) findViewById(R.id.button);
        final TextView textTime = (TextView) findViewById(R.id.textTime);
        final TextView textProcessed = (TextView)findViewById(R.id.textNumbersProcessed);
        final TextView textFactorFound = (TextView)findViewById(R.id.textFactorFound);

        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                ParseQuery<ParseObject> query = ParseQuery.getQuery(getString(R.string.parse_object));
                query.whereGreaterThan(getString(R.string.parse_object_threads), 0);
                query.findInBackground(new FindCallback<ParseObject>()
                {
                    public void done(List<ParseObject> primeList, ParseException e)
                    {
                        long prime_num;
                        long prime_sqrt;

                        Calendar t1 = Calendar.getInstance();
                        long time1 = t1.getTimeInMillis();
                        Log.d("time1", "" + time1);

                        if(e!=null)
                        {
                            Log.d("Error", "Error: " + e.getMessage());
                        }

                        //IsPrime isPrime = new IsPrime();
                        if(primeList.isEmpty())
                        {
<<<<<<< HEAD
                            //int primeNumber = (int)((Math.random()+.2)*100000);
                            int primeNumber = 179424691;
                            ParseObject blah = new ParseObject("TheNumber");
                            blah.put("number", primeNumber);
                            blah.put("threads", 3);
                            blah.saveInBackground();
                            isPrime(primeNumber, 2, (int) Math.sqrt(primeNumber / 4));
                        }
                        else
                        {
                            int primeNumber = primeList.get(0).getInt("number");
                            int threads = 4-primeList.get(0).getInt("threads");
                            primeList.get(0).increment("threads", -1);
                            int primeSqrt = (int)Math.sqrt(primeNumber);
                            isPrime(primeNumber, primeSqrt * threads / 4, primeSqrt * (threads + 1) / 4);
=======
                            prime_num = (long)((Math.random()+.2)*100000);
                            prime_sqrt = (long) Math.ceil(Math.sqrt(prime_num));

                            ParseObject create = new ParseObject(getString(R.string.parse_object));
                            create.put(getString(R.string.parse_object_prime), prime_num);
                            create.put(getString(R.string.parse_object_threads), 3);
                            create.saveInBackground();

                            IsPrime.isPrime(prime_num, 2, prime_sqrt);
                        }
                        else
                        {
                            primeList.get(0).increment(getString(R.string.parse_object_threads), -1);

                            prime_num = primeList.get(0).getLong(getString(R.string.parse_object_prime));
                            prime_sqrt = (long) Math.ceil(Math.sqrt(prime_num));

                            int threads = 4-primeList.get(0).getInt(getString(R.string.parse_object_threads));

                            IsPrime.isPrime(prime_num, prime_sqrt*threads/4, prime_sqrt*(threads+1)/4);
>>>>>>> a0efd74695d8c8d4bae41fae9a6a2b80c0fb6060
                            primeList.get(0).saveInBackground();
                            if(primeList.get(0).getInt(getString(R.string.parse_object_threads))<=0)
                            {
                                primeList.get(0).deleteInBackground();
                            }
                        }
                        Calendar t2 = Calendar.getInstance();
                        long time2 = t2.getTimeInMillis();
                        textTime.setText("Total Time: " + (time2-time1));
                    }
                });
            }
        });
    }

    public List isPrime(int num, int lo, int hi)
    {
        List toRet = new ArrayList();
        final ProgressBar mProgress;
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mProgress.setProgress(0);
        if(lo==2 && num%2==0)
        {
            toRet.add(2);
            toRet.add(num>>1);
            Log.e("prime number:", "2");
            Log.e("prime number:", ""+ (num/2));

        }

        if(lo%2==0)
            lo = lo+1;

        long onePercent = (hi-lo)/100;
        Log.e("hi: ", "" + hi);
        Log.e("lo: ", "" + lo);
        Log.e("oneP: ", "" + onePercent);

        for(int i=lo; i<=hi; i+=2)
        {
            if(i%onePercent==0)
                mProgress.incrementProgressBy(1);
            if(num%i == 0)
            {
                toRet.add(i);
                Log.e("prime number:",""+ i);
                toRet.add(num/i);
                Log.e("prime number:",""+ (num/i));
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
