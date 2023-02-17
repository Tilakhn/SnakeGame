package com.tilak.snakegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {


    private final List<SnakePoints> snakePointsList = new ArrayList<>();
    private SurfaceView surfaceView;
    private TextView scoreTV;

    private SurfaceHolder surfaceHolder;

    //snake moving positions can either be right,left, top or bottom
    //By default snake moves right
    private String movingPosition = "right";

    //score
    private int score = 0;

    // max length/size of the snake(can be changed)
    private static final int pointSize = 28;

    //Minimum length of the snake
    private static final int defaultTalePoints = 3;

    //Snake color and speed of the snake.
    private static final int snakeColor = Color.YELLOW;
    private static final int snakeMovingSpeed = 800;

    //random point position
    private int positionX, positionY;

    // timer to move / change snake position after a specific time(snakeMovingSpeed)
    private Timer timer;
    //canvas to draw snake and show on surface view
    private Canvas canvas = null;

    //point color/ single point color of a snake
    private Paint pointColor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = findViewById(R.id.surfaceView);
        scoreTV = findViewById(R.id.scoreTV);

        final AppCompatImageButton topBtn = findViewById(R.id.topBtn);
        final AppCompatImageButton leftBtn= findViewById(R.id.leftBtn);
        final AppCompatImageButton rightBtn = findViewById(R.id.rightBtn);
        final AppCompatImageButton bottomBtn = findViewById(R.id.bottomBtn);

        surfaceView.getHolder().addCallback(this);

        topBtn.setOnClickListener(view -> {

            if(!movingPosition.equals("bottom")){
                movingPosition = "top";
            }
        });

        leftBtn.setOnClickListener(view -> {

            if(!movingPosition.equals("right")){
                movingPosition = "left";
            }
        });

        rightBtn.setOnClickListener(view -> {

            if(!movingPosition.equals("left")){
                movingPosition = "right";
            }
        });

        bottomBtn.setOnClickListener(view -> {

            if(!movingPosition.equals("top")){
                movingPosition = "bottom";
            }
        });

    }

    @Override
    protected void onUserLeaveHint() {
        // Home button pressed
        super.onUserLeaveHint();
        if (timer != null) {
            timer.purge();
            timer.cancel();
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (timer != null) {
            timer.purge();
            timer.cancel();
        }
        finish();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

        this.surfaceHolder = surfaceHolder;

        init();

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    private void init(){

        snakePointsList.clear();

        scoreTV.setText("0");
        score = 0;

        movingPosition ="right";

        int startPositionX = (pointSize) * defaultTalePoints;

        for(int i=0;i<defaultTalePoints;i++){

            SnakePoints snakePoints = new SnakePoints(startPositionX, pointSize);
            snakePointsList.add(snakePoints);

            startPositionX = startPositionX - (pointSize * 2);
        }

        addPoints();

        moveSnake();
    }

    private void addPoints(){

        //getting surfaceView width and height to add points on the surface to be eaten by the snake
        int surfaceWidth = surfaceView.getWidth() - (pointSize * 2);
        int surfaceHeight = surfaceView.getHeight() - (pointSize * 2);

        int randomXPosition = new Random().nextInt( surfaceWidth / pointSize);
        int randomYPosition = new Random().nextInt( surfaceHeight / pointSize);
        //check is random point appears on the body of snake if it appears then again add new point
        for(int i=0;i<snakePointsList.size();i++){
            if(snakePointsList.get(i).getPositionX() == randomXPosition &&
                    snakePointsList.get(i).getPositionY() == randomYPosition ){
                randomXPosition = new Random().nextInt( surfaceWidth / pointSize);
                randomYPosition = new Random().nextInt( surfaceHeight / pointSize);
                i = 0;
            }
        }
        //check if randomXPosition/randomYPosition is even or add. We need only even number.
        if((randomXPosition % 2) !=0){
            randomXPosition = randomXPosition + 1;
        }

        if((randomYPosition % 2) !=0){
            randomYPosition = randomYPosition + 1;
        }

        positionX = (pointSize * randomXPosition) + pointSize;
        positionY = (pointSize * randomYPosition) + pointSize;
    }

    private void moveSnake(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                //getting head position
                int headPositionX = snakePointsList.get(0).getPositionX();
                int headPositionY = snakePointsList.get(0).getPositionY();

                //check if snake eaten a point
                if( headPositionX == positionX && positionY == headPositionY){
                    //grow snake after eating
                    growSnake();

                    //add another random point on the screen
                    addPoints();
                }

                // check which side is the snake moving
                switch (movingPosition){
                    case "right" :
                        //move snake's head to right and other points follow snake's head
                        snakePointsList.get(0).setPositionX(headPositionX + (pointSize* 2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        break;
                    case "left" :
                        //move snake's head to left and other points follow snake's head
                        snakePointsList.get(0).setPositionX(headPositionX - (pointSize* 2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        break;
                    case "top" :
                        //move snake's head to top and other points follow snake's head
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY - (pointSize * 2));
                        break;
                    case "bottom" :
                        //move snake's head to bottom and other points follow snake's head
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY  + (pointSize * 2));
                        break;
                }

                //check if game over. weather snake tough edges or snake itself
                if (checkGameOver(headPositionX, headPositionY)) {

                    //stop timer/ stop moving snake
                    timer.purge();
                    timer.cancel();

                    //show game over dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this);
                    builder.setMessage("Your score is "+ score);
                    builder.setTitle("Game Over");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Start Again", (dialogInterface, i) -> {
                        //restart game
                        init();
                    });

                    //timer runs in background so we need to show dialog on the main thread
                    runOnUiThread(builder::show);
                }
                else{

                    //lock canvas on surface-holder to draw on it
                    canvas = surfaceHolder.lockCanvas();
                    if (canvas != null) {
                        // clear canvas with while color
                        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);

                        // change snake's head position. other snake will follow snake's head
                        canvas.drawCircle(snakePointsList.get(0).getPositionX(), snakePointsList.get(0).getPositionY(), pointSize, createPointColor());

                        //draw random point circle on the surface to be eaten by the snake
                        canvas.drawCircle(positionX, positionY, pointSize, createPointColor());

                        //other points is following snake's head
                        for (int i = 1; i < snakePointsList.size(); i++) {

                            int getTempPositionX = snakePointsList.get(i).getPositionX();
                            int getTempPositionY = snakePointsList.get(i).getPositionY();

                            //moving point across the head
                            snakePointsList.get(i).setPositionX(headPositionX);
                            snakePointsList.get(i).setPositionY(headPositionY);
                            canvas.drawCircle(snakePointsList.get(i).getPositionX(), snakePointsList.get(i).getPositionY(), pointSize, createPointColor());

                            //change head position
                            headPositionX = getTempPositionX;
                            headPositionY = getTempPositionY;

                        }

                        //unlock canvas to draw on surface-view
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        },1000 - snakeMovingSpeed, 1000 - snakeMovingSpeed);
    }

    private void growSnake(){

        // create new snake point
        SnakePoints snakePoints = new SnakePoints(0,0);
        // add points to the snake's take
        snakePointsList.add(snakePoints);
        //increment score
        score++;
        //setting score to textView
        runOnUiThread(() -> scoreTV.setText(String.valueOf(score)));

    }

    private boolean checkGameOver(int headPositionX, int headPositionY){
        boolean gameOver = false;

        //check if snake's head touches edge
        if(snakePointsList.get(0).getPositionX() <0 || snakePointsList.get(0).getPositionY() < 0
        || snakePointsList.get(0).getPositionX() >= surfaceView.getWidth()
        || snakePointsList.get(0).getPositionY() >= surfaceView.getHeight())
        {
            gameOver = true;
        }
        else{
            // check if snake's head touches snake itself
            for(int i=0;i<snakePointsList.size();i++){
                if(headPositionX == snakePointsList.get(i).getPositionX()
                && headPositionY == snakePointsList.get(i).getPositionY())
                {
                    gameOver = true;
                    break;
                }
            }
        }
        return gameOver;

    }

    private Paint createPointColor(){

        //check if color not defined before
        if(pointColor == null){

            pointColor = new Paint();
            pointColor.setColor(snakeColor);
            pointColor.setStyle(Paint.Style.FILL);
            pointColor.setAntiAlias(true);
        }

        return pointColor;
    }
}