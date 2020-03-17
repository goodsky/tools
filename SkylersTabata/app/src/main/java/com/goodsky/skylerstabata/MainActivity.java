package com.goodsky.skylerstabata;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // State machine
    private enum TabataState {

        // The default state
        NotWorkingOut,

        // Exercising
        WorkingOut,

        // The break between working out,
        // but going back to the same workout
        BreakBetweenSets,

        // The break between working out,
        // but going to a new workout
        BreakBetweenRounds
    }

    private TabataState state = TabataState.NotWorkingOut;
    private boolean isPaused;

    private CountDownTimer countdown;
    private int secondsLeftInState;

    private int currentSet = 0;
    private int currentRound = 0;
    private int currentLoop = 0;

    // Sound effects
    private SoundPool soundPool;
    private int bell_fight_id;
    private int gong_id;
    private int hit_id;

    // UI Elements
    private TextView statusTextView;
    private TextView countdownTextView;
    private Button startPauseButton;
    private Button resetButton;

    private TextView setsTextView;
    private TextView roundsTextView;
    private TextView loopsTextView;
    private TextView remainingTextView;

    public void clickStartPause(View view) {

        if (state == TabataState.NotWorkingOut) {
            // Start a new workout
            // Which starts with a short break to get you ready.
            startCountdown(TabataState.BreakBetweenSets);
        } else {
            // Pause or Resume the workout
            if (isPaused) {
                isPaused = false;
                resumeCountdown();

                if (state == TabataState.WorkingOut) {
                    soundPool.play(bell_fight_id, 1.0f, 1.0f, 1, 0, 1.0f);
                }
            } else {
                isPaused = true;
                stopCountdown();
            }
        }

        updateUi();
    }

    public void clickReset(View view) {

        currentSet = 0;
        currentRound = 0;
        currentLoop = 0;

        state = TabataState.NotWorkingOut;
        isPaused = false;
        secondsLeftInState = Settings.secondsBetweenSets;

        updateUi();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Context context = getApplicationContext();

        // Initialize Sounds
        soundPool = new SoundPool.Builder().build();
        bell_fight_id = soundPool.load(context, R.raw.bell_fight, 1);
        gong_id = soundPool.load(context, R.raw.gong2_low, 1);
        hit_id = soundPool.load(context, R.raw.hit, 1);

        // Initialize UI
        statusTextView = findViewById(R.id.textViewStatus);
        countdownTextView = findViewById(R.id.textViewCountdown);
        startPauseButton = findViewById(R.id.buttonStartPause);
        resetButton = findViewById(R.id.buttonReset);

        setsTextView = findViewById(R.id.setsTextView);
        roundsTextView = findViewById(R.id.roundsTextView);
        loopsTextView = findViewById(R.id.loopsTextView);
        remainingTextView = findViewById(R.id.remainingTextView);

        // Initialize State Machine
        clickReset(null);
    }

    private void updateUi() {

        // Update Status
        CharSequence statusText;
        if (state == TabataState.NotWorkingOut)
        {
            // Waiting to start
            statusText = getText(R.string.status_default);
        }
        else
        {
            String currentRoundString = Settings.rounds[currentRound];
            if (state == TabataState.WorkingOut)
            {
                // Working out
                if (isPaused)
                {
                    String pausedFormat = getText(R.string.status_paused_format).toString();
                    statusText = String.format(pausedFormat, currentRoundString);
                }
                else
                {
                    String workingOutFormat = getText(R.string.status_working_out_format).toString();
                    statusText = String.format(workingOutFormat, currentRoundString);
                }
            }
            else
            {
                // Break between sets
                String breakFormat = getText(R.string.status_break_format).toString();
                statusText = String.format(breakFormat, currentRoundString);
            }
        }

        statusTextView.setText(statusText);

        // Update Countdown
        int minutesLeft = secondsLeftInState / 60;
        int secondsLeft = secondsLeftInState % 60;

        String countdownText = String.format(Locale.US, "%02d:%02d", minutesLeft, secondsLeft);
        countdownTextView.setText(countdownText);

        // Update Start/Pause/Resume Button
        CharSequence startPauseText;
        if (state == TabataState.NotWorkingOut)
        {
            startPauseText = getText(R.string.button_start);
        }
        else
        {
            if (isPaused)
            {
                startPauseText = getText(R.string.button_resume);
            }
            else
            {
                startPauseText = getText(R.string.button_pause);
            }
        }

        startPauseButton.setText(startPauseText);

        // Update Overview
        String setsFormat = getString(R.string.overview_sets_format);
        String setsText = String.format(setsFormat, currentSet + 1, Settings.setsPerRound);
        setsTextView.setText(setsText);

        String roundsFormat = getString(R.string.overview_rounds_format);
        String roundsText = String.format(roundsFormat, currentRound + 1, Settings.rounds.length);
        roundsTextView.setText(roundsText);

        String loopsFormat = getString(R.string.overview_loops_format);
        String loopsText = String.format(loopsFormat, currentLoop + 1, Settings.loopsPerWorkout);
        loopsTextView.setText(loopsText);

        /*
        int remainingLoops = Settings.loopsPerWorkout - currentLoop - 1;
        int remainingRounds = (remainingLoops * Settings.rounds.length) + Settings.rounds.length - currentRound - 1;

        int remainingSets = remainingRounds * Settings.setsPerRound + Settings.setsPerRound - currentSet - 1;
        int remainingRoundBreaks = remainingRounds;
        int remainingSetBreaks = remainingRounds * (Settings.setsPerRound - 1) + Settings.setsPerRound - currentSet - 1;
         */
    }

    private void startCountdown(TabataState newState)
    {
        state = newState;
        switch (newState)
        {
            case WorkingOut:
                secondsLeftInState = Settings.secondsPerSet;
                break;

            case BreakBetweenSets:
                secondsLeftInState = Settings.secondsBetweenSets;
                break;

            case BreakBetweenRounds:
                secondsLeftInState = Settings.secondsBetweenRounds;
        }

        resumeCountdown();
    }

    private void resumeCountdown()
    {
        if (countdown != null)
        {
            Log.e("COUNTDOWN", "Started a countdown while one was in progress!");
            countdown.cancel();
            countdown = null;
        }

        Log.i("COUNTDOWN", String.format("Starting countdown with %d seconds", secondsLeftInState));
        countdown = new TabataCountDown(secondsLeftInState);
        countdown.start();
    }

    private void stopCountdown()
    {
        Log.i("COUNTDOWN", "Stopping countdown");

        if (countdown != null) {
            countdown.cancel();
            countdown = null;
        }
        else
        {
            Log.e("COUNTDOWN", "No countdown was in progress");
        }
    }

    private class TabataCountDown extends CountDownTimer {

        private static final int ms = 1000;

        public TabataCountDown(int secondsLeftInState) {
            super(secondsLeftInState * ms, ms);
        }

        public void onTick(long msUntilFinished) {
            secondsLeftInState = (int)(msUntilFinished / ms) + 1;

            // ticking sounds for 3.. 2.. 1..
            if (secondsLeftInState > 0 && secondsLeftInState <= 3)
            {
                soundPool.play(hit_id, 1.0f, 1.0f, 1, 0, 1.0f);
            }

            updateUi();
        }

        public void onFinish() {
            stopCountdown();

            switch (state) {
                case WorkingOut:
                    soundPool.play(gong_id, 1.0f, 1.0f, 1, 0, 1.0f);

                    currentSet += 1;
                    if (currentSet < Settings.setsPerRound)
                    {
                        startCountdown(TabataState.BreakBetweenSets);
                    }
                    else
                    {
                        currentSet = 0;
                        currentRound += 1;

                        if (currentRound < Settings.rounds.length)
                        {
                            startCountdown(TabataState.BreakBetweenRounds);
                        }
                        else
                        {
                            currentRound = 0;
                            currentLoop += 1;

                            if (currentLoop < Settings.loopsPerWorkout)
                            {
                                startCountdown(TabataState.BreakBetweenRounds);
                            }
                            else
                            {
                                // All done!
                                clickReset(null);
                            }
                        }
                    }

                    break;

                case BreakBetweenSets:
                case BreakBetweenRounds:
                    soundPool.play(bell_fight_id, 1.0f, 1.0f, 1, 0, 1.0f);

                    startCountdown(TabataState.WorkingOut);

                    break;
            }

            updateUi();
        }
    }
}
