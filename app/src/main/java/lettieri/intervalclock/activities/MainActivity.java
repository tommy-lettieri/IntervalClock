package lettieri.intervalclock.activities;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import lettieri.intervalclock.R;
import lettieri.intervalclock.views.ProgressBar;
import lettieri.intervalclock.views.ProgressCircle;

public class MainActivity extends AppCompatActivity {

    private static final int REFRESH_RATE = 50;

    private ProgressCircle circle;
    private ProgressBar bar;
    private Button btnStop;
    private Button btnStart;
    private EditText txtInterval;

    private MediaPlayer mp;

    private boolean running = false;
    private float interval = 0f;
    private float time = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setup();
        timerThread();
        setProgress();
    }

    /***
     * Get refrences to views in the layout
     */
    private void findViews() {
        circle = (ProgressCircle) findViewById(R.id.circle);
        bar = (ProgressBar) findViewById(R.id.bar);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnStart = (Button) findViewById(R.id.btnStart);
        txtInterval = (EditText)findViewById(R.id.txtInterval);
    }

    /***
     * Add the click events for the start and stop buttons
     */
    private void setup(){
        // if started the start button should pause, otherwise it should start using the interval from the text box, if there is no interval set it to 0
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float newInterval = 0f;
                try {
                    newInterval = Float.parseFloat(txtInterval.getText().toString())*1000;
                } catch(NumberFormatException e) {
                    txtInterval.setText(String.valueOf((int)newInterval));
                }

                // if the interval has changed reset the time
                if(interval != newInterval) {
                    time = 0;
                }
                interval = newInterval;
                setRunning(!running);
            }
        });

        // on stop set the time to 0 and set running to false
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = 0f;
                setProgress();
                setRunning(false);
            }
        });
    }

    /***
     * Set progress will tell the ui thread to update the clock views
     * This occurs by calculating the percent
     */
    private void setProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float percent = 0f;
                if(interval != 0f) {
                    percent = time/interval;
                }

                circle.setPercent(percent);
                bar.setPercent(percent);
            }
        });
    }

    /***
     * This method will start a thread that will act as the clock mechanism
     * using a thread in this way is fine for now but in the future I should switch to async task
     * If this application supports landscape and portrait in the future some more thread proofing should be done
     */
    private void timerThread() {
        // TODO make application more thread safe by not using raw threads
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if(running) {
                        // increment by the refresh rate to get the time
                        time += REFRESH_RATE;
                        // check for interval otherwise get divide by zero when using mod
                        if(interval != 0f) {
                            // if the time has exceeded the interval then play the sound and do actions required on interval met
                            if(time > interval) {
                                // if I can fix the mp.start problem being seperate from instantiation  this null check will not be required
                                if(mp != null) {
                                    mp.stop();
                                }
                                mp = MediaPlayer.create(MainActivity.this, R.raw.whistle);
                                mp.start();
                                // to keep time from growing mod by interval
                                time %= interval;
                            }
                        } else {
                            // if the interval is 0 set time to 0
                            time = 0;
                        }

                        // update the clocks
                        setProgress();
                    }

                    // have the thread wait the refresh rate which right now is 50ms which is 200 times/second
                    try {
                        Thread.sleep(REFRESH_RATE);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }

    /***
     * Hide the keyboard using the interval edit text
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtInterval.getWindowToken(), 0);
    }

    /***
     * Set the running flag
     * This includes updating the pause/play button
     * and stopping the sound if it is playing
     * @param running
     */
    private void setRunning(boolean running) {
        this.running = running;
        // if I can fix the mp.start problem being seperate from instantiation  this null check will not be required
        if(mp != null) {
            mp.stop();
        }
        if(running) {
            btnStart.setText("Pause");
        } else {
            btnStart.setText("Play");
        }
        hideKeyboard();
    }
}
