package com.osacky.factor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
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

        /*
        Take care of UI details after computation has completed.
         */
        private void finish_up()
        {
            ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressBar);
            mProgress.setProgress(100);
            Button button = (Button) findViewById(R.id.compute);
            button.setEnabled(true);
        }

        /*
        Increment progress bar, and update relevant text fields.
         */
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

        Parse.initialize(this, "7A5hnUGC6Zxg6OQhZjWnB2gHAYc7h2VOOxYcVlMg", "L6tf0XRvzq6xSqbmf8EkMzJxqIfMMUqP5J9pljMC");
        ParseAnalytics.trackAppOpened(getIntent());

        //Register the broadcast receiver so we can dynamically update UI.
        IntentFilter mStatusIntentFilter = new IntentFilter(getString(R.string.broadcast_action));
        mStatusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(new ResponseReceiver(), mStatusIntentFilter);
    }

    public void create_computation(View v)
    {
        EditText thread_text = (EditText) findViewById(R.id.textThreads);
        EditText number_text = (EditText) findViewById(R.id.textNumber);
        myVib.vibrate(50); //Haptic feedback

        //Error handling toasts
        if(thread_text.getText().toString().length() <=0){
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.thread_lower_bound), 4);
            toast.show();
        }
        else if(number_text.getText().toString().length() <=0){
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.number_lower_bound), 4);
            toast.show();
        }
        else if(number_text.getText().toString().length() > 19){
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.number_upper_bound), 4);
            toast.show();
        }
        else
        {
            long num_threads = Long.parseLong(thread_text.getText().toString());
            long number = Long.parseLong(number_text.getText().toString());

            if(num_threads==0){
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.thread_lower_bound), 4);
                toast.show();
            }
            else if(number < 1000000){
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.number_lower_bound), 4);
                toast.show();
            }
            else if(number > Long.MAX_VALUE){
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.number_upper_bound), 4);
                toast.show();
            }
            else if(num_threads > 20){
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.thread_upper_bound), 4);
                toast.show();
            }
            else
            {
                //Check if number has already been created by querying for said number
                ParseQuery<ParseObject> query = ParseQuery.getQuery(getString(R.string.parse_object));
                query.whereEqualTo(getString(R.string.parse_object_number), number);

                ParseObject created = null;
                try {
                    created = query.getFirst();
                } catch (ParseException e){
                    e.printStackTrace();
                }

                //If not created start background service to create.
                if(created == null)
                {
                    Log.e("null", "Should call intent.");
                    Intent mServiceIntent = new Intent(this, CreateService.class);
                    mServiceIntent.putExtra(getString(R.string.parse_object_number), number);
                    mServiceIntent.putExtra(getString(R.string.parse_object_threads), num_threads);
                    this.startService(mServiceIntent);
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Value already created, press show result", 4);
                    toast.show();
                }
            }
        }
    }

    public void perform_computation(View v)
    {
        myVib.vibrate(50);

        //Set up UI details before computation starts
        ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mProgress.setProgress(0);

        TextView textTime = (TextView) findViewById(R.id.textTime);
        TextView textProc = (TextView) findViewById(R.id.textNumbersProcessed);
        TextView textFactor = (TextView) findViewById(R.id.textLastFactor);
        textTime.setText(getString(R.string.time_taken));
        textProc.setText(getString(R.string.num_proc));
        textFactor.setText(getString(R.string.factor));

        EditText number_text = (EditText) findViewById(R.id.textNumber);
        Button button = (Button) findViewById(R.id.compute);
        button.setEnabled(false);

        //Create query to get information to start computation
        ParseQuery<ParseObject> query = ParseQuery.getQuery(getString(R.string.parse_object));
        query.whereEqualTo(getString(R.string.parse_object_to_compute), true);

        ParseObject computation = null;
        try{
            computation = query.getFirst();
        } catch (ParseException e){
            e.printStackTrace();
        }

        if(computation==null){
            Toast toast = Toast.makeText(getApplicationContext(), "Nothing to compute, please create", 4);
            toast.show();
            button.setEnabled(true);
        }
        else
        {
            //Mark it as computed and save, so another thread does not solve the same sub-problem
            computation.put(getString(R.string.parse_object_to_compute), false);
            try {
                computation.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long number = computation.getLong(getString(R.string.parse_object_number));
            long low = computation.getLong(getString(R.string.parse_object_low));
            long high = computation.getLong(getString(R.string.parse_object_high));

            number_text.setText("" + number);

            //Start background service to compute
            Intent mServiceIntent = new Intent(this, ComputationService.class);
            mServiceIntent.putExtra(getString(R.string.parse_object_number), number);
            mServiceIntent.putExtra(getString(R.string.parse_object_low), low);
            mServiceIntent.putExtra(getString(R.string.parse_object_high), high);
            this.startService(mServiceIntent);
        }
    }

    public void show_results(View v) {
        EditText number_text = (EditText) findViewById(R.id.textNumber);
        long number = Long.parseLong(number_text.getText().toString());

        //Start activity to print list of results
        Intent show_results_intent = new Intent(this, ShowResults.class);
        show_results_intent.putExtra(getString(R.string.parse_factors_key), number);
        startActivity(show_results_intent);
    }
}
