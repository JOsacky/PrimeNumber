package com.osacky.factor;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;



public class MainActivity extends Activity {

    private Vibrator myVib;
    public class ResponseReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.hasExtra(getString(R.string.done_broadcast)))
                finish_up();
            else if(intent.hasExtra(getString(R.string.progress)))
                updateProgress(intent.getLongExtra(getString(R.string.num_proc), -1),
                        intent.getLongExtra(getString(R.string.time_taken), -1),
                        intent.getLongExtra(getString(R.string.factor), -1));
        }

        private void finish_up()
        {
            ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressBar);
            mProgress.setProgress(100);
            Button button = (Button) findViewById(R.id.compute);
            button.setBackgroundColor(getResources().getColor(R.color.green));
        }

        private void updateProgress(long num_proc, long time, long factor)
        {
            ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressBar);
            mProgress.incrementProgressBy(1);

            TextView textProc = (TextView) findViewById(R.id.textNumbersProcessed);
            TextView textTime = (TextView) findViewById(R.id.textTime);
            TextView textFactor = (TextView) findViewById(R.id.textLastFactor);

            textProc.setText(getString(R.string.num_proc) + " " + num_proc);
            textTime.setText(getString(R.string.time_taken) + " " + time);
            if(factor!=-1)
                textFactor.setText(getString(R.string.factor) + " " + factor);
            else
                textFactor.setText(getString(R.string.factor) + " N/A");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

//        ParseObject.registerSubclass(Factors.class);
        Parse.initialize(this, "7A5hnUGC6Zxg6OQhZjWnB2gHAYc7h2VOOxYcVlMg", "L6tf0XRvzq6xSqbmf8EkMzJxqIfMMUqP5J9pljMC");
        ParseAnalytics.trackAppOpened(getIntent());

        // The filter's action is BROADCAST_ACTION
        IntentFilter mStatusIntentFilter = new IntentFilter(getString(R.string.broadcast_action));
        mStatusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(new ResponseReceiver(), mStatusIntentFilter);
    }

    public void create_computation(View v)
    {
        EditText thread_text = (EditText) findViewById(R.id.textThreads);
        EditText number_text = (EditText) findViewById(R.id.textNumber);
        myVib.vibrate(50);

        if(thread_text.getText().toString().length() <=0)
        {
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.thread_lower_bound), 4);
            toast.show();
        }
        else if(number_text.getText().toString().length() <=0)
        {
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.number_lower_bound), 4);
            toast.show();
        }
        else if(number_text.getText().toString().length() > 19)
        {
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.number_upper_bound), 4);
            toast.show();
        }
        else
        {
            long num_threads = Long.parseLong(thread_text.getText().toString());
            long number = Long.parseLong(number_text.getText().toString());
            long sqrt = (long) Math.ceil(Math.sqrt(number));
            if(num_threads==0)
            {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.thread_lower_bound), 4);
                toast.show();
            }
            else if(number < 1000000)
            {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.number_lower_bound), 4);
                toast.show();
            }
            else if(number > Long.MAX_VALUE)
            {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.number_upper_bound), 4);
                toast.show();
            }
            else if(num_threads > 20)
            {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.thread_upper_bound), 4);
                toast.show();
            }
            else
            {
                ParseQuery<ParseObject> query = ParseQuery.getQuery(getString(R.string.parse_object));
                query.whereEqualTo(getString(R.string.parse_object_rem_threads), 0);
                query.whereEqualTo("number", number);
                if(query == null) {
                    ParseObject create = new ParseObject(getString(R.string.parse_object));
                    create.put(getString(R.string.parse_object_number), number);
                    create.put(getString(R.string.parse_object_threads), num_threads);
                    create.put(getString(R.string.parse_object_rem_threads), num_threads);
                    create.put(getString(R.string.parse_object_sqrt), sqrt);
                    create.saveInBackground();
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Value already computed, press show result", 4);
                    toast.show();
                }
            }
        }
    }

    public void perform_computation(View v)
    {
        EditText thread_text = (EditText) findViewById(R.id.textThreads);
        EditText number_text = (EditText) findViewById(R.id.textNumber);
        Button button = (Button) findViewById(R.id.compute);
        button.setBackgroundColor(getResources().getColor(R.color.red));
        ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mProgress.setProgress(0);
        myVib.vibrate(50);

        TextView textTime = (TextView) findViewById(R.id.textTime);
        TextView textProc = (TextView) findViewById(R.id.textNumbersProcessed);
        textTime.setText(getString(R.string.time_taken));
        textProc.setText(getString(R.string.num_proc));

        ParseQuery<ParseObject> query = ParseQuery.getQuery(getString(R.string.parse_object));
        query.whereGreaterThan(getString(R.string.parse_object_rem_threads), 0);
        ParseObject computation = null;

        try
        {
            computation = query.getFirst();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        if(computation ==null)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Nothing to compute, please create", 4);
            toast.show();
            button.setBackgroundColor(getResources().getColor(R.color.green));
        }

        if(computation!=null)
        {
            long number;
            long num_threads;
            long diff_threads;
            long rem_threads;
            long sqrt;

            computation.increment(getString(R.string.parse_object_rem_threads), -1);
            computation.saveInBackground();

            number = computation.getLong(getString(R.string.parse_object_number));
            num_threads = computation.getLong(getString(R.string.parse_object_threads));
            rem_threads = computation.getLong(getString(R.string.parse_object_rem_threads));
            sqrt = computation.getLong(getString(R.string.parse_object_sqrt));
            diff_threads = num_threads - rem_threads;
            thread_text.setText("" + num_threads);
            number_text.setText("" + number);

            Intent mServiceIntent = new Intent(this, ComputationService.class);
            mServiceIntent.putExtra(getString(R.string.parse_object_number), number);
            mServiceIntent.putExtra(getString(R.string.parse_object_rem_threads), rem_threads);
            mServiceIntent.putExtra(getString(R.string.parse_object_threads), num_threads);
            mServiceIntent.putExtra(getString(R.string.parse_object_sqrt), sqrt);
            this.startService(mServiceIntent);
        }
    }

    public void show_results(View v) {
        EditText number_text = (EditText) findViewById(R.id.textNumber);
        final long number = Long.parseLong(number_text.getText().toString());

        Intent show_results_intent = new Intent(this, ShowResults.class);
        show_results_intent.putExtra(getString(R.string.parse_factors_key), number);
        startActivity(show_results_intent);
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
