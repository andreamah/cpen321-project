package com.example.johnnyma.testbench;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Random;

/*
 * TODO: add loading question dialog
 * TODO: add emoji-sending code
 */


public class GameplayActivity extends AppCompatActivity  {

    //emoji encondings
    public static final int EMOJI_OK = 0;
    public static final int EMOJI_BIGTHINK = 1;
    public static final int EMOJI_FIRE = 2;
    public static final int EMOJI_POOP = 3;
    public static final int EMOJI_HUNNIT = 4;
    public static final int EMOJI_HEART = 5;
    public static final int EMOJI_CRYLAUGH = 6;

    Socket socket; // socket handle
    // handles for all layout elements
    Button incorrect1;
    Button incorrect2;
    Button incorrect3;
    Button correct;
    TextView body;
    TextView playerName;
    TextView opponentName;
    TextView playerScore;
    TextView opponentScore;
    TextView courseHeader;
    TextView questionHeader;
    ImageView playerAvatar;
    ImageView opponentAvatar;
    // all questions
    ArrayList<Question> questions;
    String course;
    int player_score;
    int opponent_score;
    String player_name;
    String opponent_name;
    int player_avatar;
    int opponent_avatar;
    int player_rank;
    int opponent_rank;
    int currentQuestion = 1;

    //emoji stuff
    ImageView emoji_bigthink;
    ImageView emoji_crylaugh;
    ImageView emoji_heart;
    ImageView emoji_poop;
    ImageView emoji_hunnit;
    ImageView emoji_fire;
    ImageView emoji_ok;
    private PopupWindow emojiPopup;
    private LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);
        Intent starting_intent = getIntent();

        course = starting_intent.getStringExtra("course");
        courseHeader = findViewById(R.id.course);
        courseHeader.setText(course.substring(0,3)+ " " + course.substring(4, 6));

        player_name = starting_intent.getStringExtra("player_name");
        playerName = findViewById(R.id.opponent_name);
        playerName.setText(player_name);

        player_avatar = Integer.parseInt(starting_intent.getStringExtra("player_avatar"));
        playerAvatar = findViewById(R.id.player_avatar);
        setPlayerAvatar();

        player_rank = Integer.parseInt(starting_intent.getStringExtra("player_rank"));

        opponent_name = starting_intent.getStringExtra("opponent_name");
        opponentName = findViewById(R.id.opponent_name);
        opponentName.setText(opponent_name);

        opponent_avatar = Integer.parseInt(starting_intent.getStringExtra("opponent_avatar"));
        opponentAvatar = findViewById(R.id.opponent_avatar);
        setOpponentAvatar();

        opponent_rank = Integer.parseInt(starting_intent.getStringExtra("opponent_rank"));

        player_score = 0;
        playerScore = findViewById(R.id.player_score);
        playerScore.setText("Score: "+player_score);

        opponent_score = 0;
        opponentScore = findViewById(R.id.opponent_score);
        opponentScore.setText("Score: " + opponent_score);

        body = findViewById(R.id.question_body);

        questionHeader = findViewById(R.id.question_num);

        //set emoji views and onclick listners for emojis
        emoji_ok = (ImageView) findViewById(R.id.ok_emoji2);
        emoji_poop = (ImageView) findViewById(R.id.ok_emoji);
        emoji_bigthink = (ImageView) findViewById(R.id.bigthink_emoji);
        emoji_fire = (ImageView) findViewById(R.id.ok_emoji3);
        emoji_hunnit = (ImageView) findViewById(R.id.hunnit_emoji2);
        emoji_crylaugh = (ImageView) findViewById(R.id.crylaugh_emoji);
        emoji_heart = (ImageView) findViewById(R.id.heart_emoji);
        setEmojiListeners();
        socket.on("broadcast_emoji", popupEmoji);

        socket = SocketHandler.getSocket();
        socket.on("get_questions", getQuestions);
        waitForQuestion();
    }
    protected void setPlayerAvatar(){
        switch(player_avatar) {
            case 0:
                playerAvatar.setImageResource(R.drawable.penguin_avatar);
            case 1:
                playerAvatar.setImageResource(R.drawable.mountain_avatar);
            case 2:
                playerAvatar.setImageResource(R.drawable.rocket_avatar);
            case 3:
                playerAvatar.setImageResource(R.drawable.frog_avatar);
            case 4:
                playerAvatar.setImageResource(R.drawable.thunderbird_avatar);
            case 5:
                playerAvatar.setImageResource(R.drawable.cupcake_avatar);
        }
    }

    protected void setOpponentAvatar(){
        switch(opponent_avatar) {
            case 0:
                opponentAvatar.setImageResource(R.drawable.penguin_avatar);
            case 1:
                opponentAvatar.setImageResource(R.drawable.mountain_avatar);
            case 2:
                opponentAvatar.setImageResource(R.drawable.rocket_avatar);
            case 3:
                opponentAvatar.setImageResource(R.drawable.frog_avatar);
            case 4:
                opponentAvatar.setImageResource(R.drawable.thunderbird_avatar);
            case 5:
                opponentAvatar.setImageResource(R.drawable.cupcake_avatar);
        }
    }


    protected void randomizeAnswers(Question q){
        ArrayList<Integer> answers = new ArrayList<>();
        Random random = new Random();
        int rand;
        while (answers.size() < 4) {
            rand = random.nextInt() % 4 + 1;
            if(answers.contains(rand))
                answers.add(rand);

        }
        switch (answers.indexOf(1)) {
            case 0:
                incorrect1 = findViewById(R.id.answer_1);
                break;
            case 1:
                incorrect1 = findViewById(R.id.answer_2);
                break;
            case 2:
                incorrect1 = findViewById(R.id.answer_3);
            case 3:
                incorrect1 = findViewById(R.id.answer_4);
        }
        incorrect1.setText(q.incorrectAnswer1);
        switch (answers.indexOf(2)) {
            case 0:
                incorrect2 = findViewById(R.id.answer_1);
                break;
            case 1:
                incorrect2 = findViewById(R.id.answer_2);
                break;
            case 2:
                incorrect2 = findViewById(R.id.answer_3);
            case 3:
                incorrect2 = findViewById(R.id.answer_4);
        }
        incorrect2.setText(q.incorrectAnswer2);
        switch (answers.indexOf(3)) {
            case 0:
                incorrect3 = findViewById(R.id.answer_1);
                break;
            case 1:
                incorrect3 = findViewById(R.id.answer_2);
                break;
            case 2:
                incorrect3 = findViewById(R.id.answer_3);
            case 3:
                incorrect3 = findViewById(R.id.answer_4);
        }
        incorrect3.setText(q.incorrectAnswer3);
        switch (answers.indexOf(4)) {
            case 0:
                correct = findViewById(R.id.answer_1);
                break;
            case 1:
                correct = findViewById(R.id.answer_2);
                break;
            case 2:
                correct = findViewById(R.id.answer_3);
            case 3:
                correct = findViewById(R.id.answer_4);
        }
        correct.setText(q.correctAnswer);
    }

    protected void endTurn(){
        socket.on("turn_over", turnOver);
    }

    protected void waitForQuestion() {
        if (currentQuestion > 7)
            endGame();
        else {
            socket.emit("ready_next");
            socket.on("start_question", readyQuestion);
        }
    }
    protected void playQuestion() {
        questionHeader.setText("Question " + currentQuestion + "of 7");
        body.setText(questions.get(currentQuestion).body);
        // randomly assign questions to question buttons
        randomizeAnswers(questions.get(currentQuestion));

        // start timer
        final int time = (int)System.currentTimeMillis();
        incorrect1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add to event answer time
                socket.emit("answer_wrong");
                incorrect1.setBackgroundColor(0xd69191);
                incorrect1.setTextColor(0x880000);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        endTurn();
                    }
                }, 1000);
            }
        });

        incorrect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add to event answer time
                socket.emit("answer_wrong");
                incorrect2.setBackgroundColor(0xd69191);
                incorrect2.setTextColor(0x880000);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        endTurn();
                    }
                }, 1000);
            }
        });

        incorrect3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add to event answer time
                socket.emit("answer_wrong");
                incorrect3.setBackgroundColor(0xd69191);
                incorrect3.setTextColor(0x880000);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        endTurn();
                    }
                }, 1000);
            }
        });

        correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correct.setBackgroundColor(0x885f89);
                correct.setTextColor(0x29722f);
                int score = calculateScore((int)System.currentTimeMillis() - time);
                socket.emit("answer_right", score);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        endTurn();
                    }
                }, 1000);

            }
        });
        endTurn();
        while (System.currentTimeMillis() - time < 10000); //im not to sure about this waiting stuff TODO
        socket.emit("answer_wrong");
        endTurn();
        // update score based on contents attached to event
    }

    protected void endGame(){
        return;
    }

    protected void parseQuestions(JSONArray questionsJSON) {
        for (int i = 0; i < 7 ; i++) {
            try {
                questions.add(new Question(questionsJSON.getJSONObject(i)));
            } catch (JSONException e) {
                return;
            }
        }
    }

    public Emitter.Listener turnOver = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable(){
                @Override
                public void run() {
                    try {
                        JSONObject scores = new JSONObject((String) args[0]);
                        player_score = scores.getInt("player_score");
                        opponent_score = scores.getInt("opponent_score");
                    } catch (JSONException e) {
                        return;
                    }
                    playerScore.setText("Score: " + player_score);
                    opponentScore.setText("Score: " + opponent_score);
                    currentQuestion++;
                    waitForQuestion();
                }
            });
        }
    };

    public Emitter.Listener readyQuestion = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable(){
                @Override
                public void run() {
                    playQuestion();
                }
            });
        }
    };

    public Emitter.Listener getQuestions = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable(){
                @Override
                public void run() {
                    try {
                        JSONArray questions = new JSONArray((String) args[0]);
                        parseQuestions(questions);
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };


    public Emitter.Listener popupEmoji = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View container = layoutInflater.inflate(R.layout.layout_emoji, null);
                    ImageView emojiImage = (ImageView) container.findViewById(R.id.emoji);
                    switch((int) args[0]){
                        case EMOJI_OK:
                            emojiImage.setImageResource(R.drawable.ok_emoji);
                            break;
                        case EMOJI_BIGTHINK:
                            emojiImage.setImageResource(R.drawable.bigthink_emoji);
                            break;
                        case EMOJI_CRYLAUGH:
                            emojiImage.setImageResource(R.drawable.crylaugh_emoji);
                            break;
                        case EMOJI_FIRE:
                            emojiImage.setImageResource(R.drawable.fire_emoji);
                            break;
                        case EMOJI_HEART:
                            emojiImage.setImageResource(R.drawable.heart_emoji);
                            break;
                        case EMOJI_HUNNIT:
                            emojiImage.setImageResource(R.drawable.hunnit_emoji);
                            break;
                        case EMOJI_POOP:
                            emojiImage.setImageResource(R.drawable.poop_emoji);
                            break;
                    }

                    emojiPopup = new PopupWindow(container, 100, 100, false);
                    emojiPopup.showAtLocation(findViewById(android.R.id.content), Gravity.NO_GRAVITY, 500, 500); //TODO change location

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            emojiPopup.dismiss();
                        }
                    }, 2000);
                }
            });
        }
    };

    protected int calculateScore(int answerTime){
        return (10000 - answerTime) / 10000 * 500 + 500;
    }
    private class Question {
        String id;
        String body;
        String correctAnswer;
        String incorrectAnswer1;
        String incorrectAnswer2;
        String incorrectAnswer3;
        boolean profEndorsed;

        public Question(JSONObject questionJSON) {
            try {
                id = questionJSON.getString("id");
                body = questionJSON.getString("question_text");
                correctAnswer = questionJSON.getString("correct_answer");
                incorrectAnswer1 = questionJSON.getString("incorrect_answer_1");
                incorrectAnswer2 = questionJSON.getString("incorrect_answer_2");
                incorrectAnswer3 = questionJSON.getString("incorrect_answer_3");
                String endorsed = questionJSON.getString("professor_endorsed");
                profEndorsed = endorsed.equals("1") ? true : false;
            } catch (JSONException e) {
                return;
            }
        }
    }

    /*
        USED DURING ONCREATE
     */
    protected void setEmojiListeners(){
        emoji_ok.setClickable(true);
        emoji_ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                socket.emit("send_emoji", EMOJI_OK);
            }
        });

        emoji_fire.setClickable(true);
        emoji_fire.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                socket.emit("send_emoji", EMOJI_FIRE);
            }
        });

        emoji_bigthink.setClickable(true);
        emoji_bigthink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                socket.emit("send_emoji", EMOJI_BIGTHINK);
            }
        });
        emoji_crylaugh.setClickable(true);
        emoji_crylaugh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                socket.emit("send_emoji", EMOJI_CRYLAUGH);
            }
        });

        emoji_heart.setClickable(true);
        emoji_heart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                socket.emit("send_emoji", EMOJI_HEART);
            }
        });

        emoji_hunnit.setClickable(true);
        emoji_hunnit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                socket.emit("send_emoji", EMOJI_HUNNIT);
            }
        });

        emoji_poop.setClickable(true);
        emoji_poop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                socket.emit("send_emoji", EMOJI_POOP);
            }
        });
    }
}
