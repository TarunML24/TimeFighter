package com.raywenderlich.timefighter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog


class MainActivity : AppCompatActivity() {

    internal var score = 0

    internal lateinit var tapMeButton : Button
    internal lateinit var gameScoreTextView : TextView
    internal lateinit var timeLeftTextView : TextView
    internal lateinit var countDownTimer : CountDownTimer
    internal val initialCountDown : Long = 60000
    internal val countDownInterval : Long = 1000
    internal var gameStarted = false

    internal var timeLeftOnCountDownTimer: Long = 60000


    companion object{
        private val TAG = MainActivity::class.java.simpleName
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate call, Score is : $score")

        tapMeButton = findViewById(R.id.tapMeButton)
        gameScoreTextView = findViewById(R.id.gameScoreTextView)
        timeLeftTextView = findViewById(R.id.timeLeftTextView)
        tapMeButton.setOnClickListener{view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this,R.anim.bounce)
            view.startAnimation(bounceAnimation)


            incrementScore()
        }
        if(savedInstanceState!=null){
            timeLeftOnCountDownTimer = savedInstanceState.getLong(TIME_LEFT_KEY)
            score = savedInstanceState.getInt(SCORE_KEY)

            restoreGame()
        }
        else{
            resetGame()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if(item.itemId == R.id.actionAbout){
            showInfo()
        }
        return true
    }

    private fun showInfo(){

        val dialogTitle = getString(R.string.aboutTitle,BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.aboutMessage)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()

    }
    private fun restoreGame(){

        gameScoreTextView.text = getString(R.string.yourScore,score)
        val restoredTime = timeLeftOnCountDownTimer/1000
        timeLeftTextView.text = getString(R.string.timeLeft,restoredTime)

        countDownTimer = object : CountDownTimer(timeLeftOnCountDownTimer,countDownInterval){

            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnCountDownTimer = millisUntilFinished
                val timeLeft = millisUntilFinished/1000
                timeLeftTextView.text = getString(R.string.timeLeft,timeLeft)

            }

            override fun onFinish() {
                endGame()
            }


        }

        countDownTimer.start()
        gameStarted = true


    }
    override fun onSaveInstanceState(outState: Bundle){

        super.onSaveInstanceState(outState)
        outState.putInt(SCORE_KEY,score)
        outState.putLong(TIME_LEFT_KEY,timeLeftOnCountDownTimer)
        countDownTimer.cancel()

        Log.d(TAG,"onSaveInstance : Saving score = $score  & Time Left = $timeLeftOnCountDownTimer")

    }

    override fun onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy called.")
    }

    private fun startGame(){
        countDownTimer.start()
        gameStarted=true
    }

    private fun incrementScore() {
        if(!gameStarted){
            startGame()
        }

        val blinkAnimation = AnimationUtils.loadAnimation(this,R.anim.blink)
        gameScoreTextView.startAnimation(blinkAnimation)
        score+=1
        val newScore = getString(R.string.yourScore,score)
        gameScoreTextView.text = newScore

    }

    private fun resetGame(){

        score = 0
        gameScoreTextView.text = getString(R.string.yourScore,score)

        val initialTimeLeft = initialCountDown/1000
        timeLeftTextView.text = getString(R.string.timeLeft,initialTimeLeft)

        countDownTimer = object : CountDownTimer(initialCountDown,countDownInterval){


            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnCountDownTimer = millisUntilFinished
                val timeLeft = millisUntilFinished/1000
                timeLeftTextView.text = getString(R.string.timeLeft,timeLeft)

            }

            override fun onFinish() {
                endGame()
            }

        }
        gameStarted = false

    }

    private fun endGame(){
        Toast.makeText(this,getString(R.string.finalScore,score),Toast.LENGTH_LONG).show()
        resetGame()
    }
}
