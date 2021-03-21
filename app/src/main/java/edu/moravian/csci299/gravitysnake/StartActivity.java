package edu.moravian.csci299.gravitysnake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

/**
 * This class is the StartActivity. It gets created in the beginning activity_start layout.
 */
public class StartActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private GameModel gameModel;
    private SharedPreferences preferences;

    /**
     * Initializes gameModel, the start Button, and the level select SeekBar.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        gameModel = (new ViewModelProvider(this)).get(GameModel.class);
        preferences = getPreferences(Context.MODE_PRIVATE);
        for (int i = 0; i < gameModel.NUM_LEVELS; i++)
        {
            gameModel.setHighScore(i, preferences.getInt(String.format(getString(R.string.high_score_preference_key), i), 0));
        }

        Log.d("From StartActivity", "Before: " + gameModel.getHighScore(3));
        gameModel.setHighScore(3, gameModel.getHighScore(3) + 1);
        Log.d("From StartActivity", "After: " + gameModel.getHighScore(3));

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        SeekBar bar = findViewById(R.id.levelSelectBar);
        bar.setOnSeekBarChangeListener(this);
        gameModel.setCurrentLevel(bar.getProgress());

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        Log.d("From StartActivity", "In OnDestroy: " + gameModel.getHighScore(3));

        SharedPreferences.Editor editor = preferences.edit();
        for (int i = 0; i < gameModel.NUM_LEVELS; i++)
        {
            editor.putInt(String.format(getString(R.string.high_score_preference_key), i), gameModel.getHighScore(i));
        }
        editor.apply();
    }

    /**
     * Called when the start Button was clicked.
     * Sends an intent to start GameActivity with extra 'level' being the level chosen.
     * @param v View that was clicked.
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("level", gameModel.getCurrentLevel());
        startActivity(intent);
    }

    /**
     * Called when level select SeekBar's progress changes.
     * Sets the gameModel's current level to the new progress.
     * @param seekBar SeekBar that had it's progress changed.
     * @param progress number that is the new progress.
     * @param fromUser true if the progress change was initiated by the user.
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        gameModel.setCurrentLevel(progress);
    }

    /** Does nothing but must be provided. */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    /** Does nothing but must be provided. */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
