package edu.moravian.csci299.gravitysnake;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * The custom View for the Snake Game. This handles the user interaction and
 * sensor information for the snake game but has none of the game logic. That
 * is all within SnakeGame and Snake.
 *
 * NOTE: This class is where most of the work is required. You must document
 * *all* methods besides the constructors (this includes methods already
 * declared that don't have documentation). You will also need to add at least
 * a few methods to this class.
 */
public class SnakeGameView extends View implements SensorEventListener {

    /** The paints and drawables used for the different parts of the game */
    private final Paint scorePaint = new Paint();
    private final Paint snakePaint = new Paint();
    private final Paint wallPaint = new Paint();
    private final Paint foodPaint = new Paint();

    /** The metrics about the display to convert from dp and sp to px */
    private final DisplayMetrics displayMetrics;

    /** The snake game for the logic behind this view */
    private final SnakeGame snakeGame;

    private int gameDifficulty;

    // Required constructors for making your own view that can be placed in a layout
    public SnakeGameView(Context context) { this(context, null);  }

    public SnakeGameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // Get the metrics for the display so we can later convert between dp, sp, and px
        displayMetrics = context.getResources().getDisplayMetrics();

        // Make the game
        snakeGame = new SnakeGame();

        // This color is automatically painted as the background
        // TODO: feel free to change this (and it can even be changed to any Drawable if you use setBackground() instead)
        setBackgroundColor(0xFF333333);

        // Setup all of the paints and drawables used for drawing later
        // TODO: this one paint is a demonstration for text, solid colors usually just require setting the color
        scorePaint.setColor(Color.WHITE);
        scorePaint.setAntiAlias(true);
        scorePaint.setTextAlign(Paint.Align.CENTER);
        scorePaint.setTextSize(spToPx(24)); // use sp for text
        scorePaint.setFakeBoldText(true);

        snakePaint.setColor(Color.GREEN);
        wallPaint.setColor(Color.BLUE);
        foodPaint.setColor(Color.RED);
    }

    /**
     * @return the snake game for this view
     */
    public SnakeGame getSnakeGame() { return snakeGame; }

    /**
     * Utility function to convert dp units to px units. All Canvas and Paint
     * function use numbers in px units but dp units are better for
     * inter-device support.
     * @param dp the size in dp (device-independent-pixels)
     * @return the size in px (pixels)
     */
    public float dpToPx(float dp) { return dp * displayMetrics.density; }

    /**
     * Utility function to convert sp units to px units. All Canvas and Paint
     * function use numbers in px units but sp units are better for
     * inter-device support, especially for text.
     * @param sp the size in sp (scalable-pixels)
     * @return the size in px (pixels)
     */
    public float spToPx(float sp) { return sp * displayMetrics.scaledDensity; }

    /**
     * @param difficulty the new difficulty for the game
     */
    public void setDifficulty(int difficulty) {
        // TODO: may need to set lots of things here to change the game's difficulty. Subject to change
        gameDifficulty = difficulty;
        difficulty ++;
        snakeGame.setInitialSpeed(difficulty * 1.0);
        snakeGame.setStartingLength(difficulty * 20);
        snakeGame.setMovementDirection(270.0);
        snakeGame.setSpeedIncreasePerFood(difficulty * 0.1);
        snakeGame.setWallPlacementProbability(1.0f / 60.0f);
        snakeGame.setLengthIncreasePerFood(difficulty);

    }

    /**
     * Once the view is laid out, we know the dimensions of it and can start
     * the game with the snake in the middle (if the game hasn't already
     * started). We also take this time to set the dp to px factor of the
     * snake.
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // NOTE: this function is done for you
        super.onLayout(changed, left, top, right, bottom);
        if (snakeGame.hasNotStarted()) {
            snakeGame.startGame(right - left, bottom - top);
            snakeGame.setDpToPxFactor(displayMetrics.density);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        postInvalidateOnAnimation(); // automatically invalidate every frame so we get continuous playback

        canvas.drawText("Score: " + snakeGame.getScore(), spToPx(displayMetrics.widthPixels / 4f), spToPx(20.0f),  scorePaint);

        if (snakeGame.update())
        {
            for (PointF p: snakeGame.getSnakeBodyLocations())
            {
                canvas.drawCircle(p.x, p.y, dpToPx(Snake.BODY_PIECE_SIZE_DP), snakePaint);
            }

            for (PointF w: snakeGame.getWallLocations())
            {
                float diff = dpToPx(SnakeGame.WALL_SIZE_DP/2);
                canvas.drawRect(w.x - diff, w.y - diff, w.x + diff, w.y + diff, wallPaint);
            }

            PointF foodLocation = snakeGame.getFoodLocation();
            canvas.drawCircle(foodLocation.x, foodLocation.y, dpToPx(SnakeGame.FOOD_SIZE_DP), foodPaint);
        }
        else
        {
            finishActivity();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF point = new PointF(event.getX(), event.getY());
        if (!snakeGame.touched(point))
        {
            finishActivity();
        }

        return true;
    }

    private void finishActivity() {
        Intent intent = new Intent();
        intent.putExtra("level", gameDifficulty);
        intent.putExtra("score", snakeGame.getScore());

        Activity context = (Activity) getContext();
        context.setResult(Activity.RESULT_OK, intent);
        context.finish();
    }

    /**
     * The snake's trajectory is modified by the change in the gravity sensor.
     * @param event the change in the sensor to be used to change the scale of the arrowView.
     */
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        // TODO
        double x = event.values[0];
        double y = event.values[1];
        snakeGame.setMovementDirection(Math.atan2(y, -x));
    }

    /** Does nothing but must be provided. */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
