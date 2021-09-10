package com.example.alarmapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.random;

public class AlarmActivity extends AppCompatActivity {

    private static final String TAG = "AlarmActivity";

    //vars
    private int rnds1 = 0;
    private int rnds2 = 0;
    private int rnds3 = 0;
    private int ans = 0;
    private boolean ans1 = false;
    private boolean pas = false;
    private  String ans3;
    static final long FIVE_MINUTE_IN_MILLIS=300000;//millisecs

    private int count = 1;
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private int mHour = 0;
    private int mMin  = 0;

    //widgets
    TextView f;
    TextView m;
    TextView l;
    Button b;
    EditText an;
    TextView tvDisplayDate;
    TextView shake;
    Button snooze;
    TextView Scount;

    @RequiresApi(Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_alarm);

        tvDisplayDate =(TextView)findViewById(R.id.Date);
        long date = System.currentTimeMillis();


        SimpleDateFormat sdf = new SimpleDateFormat("MMM MM");
        String dateString = sdf.format(date);
        tvDisplayDate.setText(dateString);


        rnds1  = ThreadLocalRandom.current().nextInt(-99,99);
        rnds2  = ThreadLocalRandom.current().nextInt(0,3);
        rnds3  = ThreadLocalRandom.current().nextInt(-99,99);
        f = (TextView)findViewById(R.id.first);
        f.setText(String.valueOf("("+rnds1+")"));
        m = (TextView)findViewById(R.id.middle);
        switch (rnds2){
            case 0:{
                m.setText("+");
                ans = rnds1+rnds3;
                break;
            }
            case 1:{
                m.setText("-");
                ans = rnds1-rnds3;
                break;
            }
            case 2:{
                m.setText("*");
                ans = rnds1*rnds3;
                break;
            }
            case 3:{
                m.setText("/");
                ans = rnds1/rnds3;
                break;
            }
        }

        Toast.makeText(this,"Answer is " +ans, Toast.LENGTH_SHORT).show();

        l =(TextView)findViewById(R.id.last);
        l.setText(String.valueOf("("+rnds3+")"));
        b = (Button)findViewById(R.id.stop);

        //To Vibrate
        Vibrator vibrator =(Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] mVibratorPattern = new long[]{0, 400, 200, 400};
        vibrator.vibrate(mVibratorPattern, 0);

        //To Increase Volume
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager .setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //For Notification
        NotificationCompat.Builder noti = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle("Alarm is ON")
                .setContentText("You had set up the alarm");

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, noti.build());
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.setLooping(true);
        r.play();



        //Stop Alarm
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                an = (EditText)findViewById(R.id.answer);
                ans3 = an.getText().toString();
                Log.d(TAG, "onClick: Answer string: "+String.valueOf(ans));

                if (ans == Integer.parseInt(ans3)){
                    ans1 = true;
                    pas = true;
                    Toast.makeText(AlarmActivity.this, "Bye Bye", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(AlarmActivity.this, "Incorrect answer", Toast.LENGTH_SHORT).show();
                }
                if (ans1){
                    r.stop();
                    vibrator.cancel();
                    finishAffinity();
                    finish();
                }
            }
        });

        //Snooze Button
        snooze =(Button)findViewById(R.id.snooze);
        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                setNoti();

                Intent myIntent = new Intent(AlarmActivity.this, MyReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this,24444,myIntent,0);
                manager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+ FIVE_MINUTE_IN_MILLIS,pendingIntent);

                Toast.makeText(AlarmActivity.this, "Snoozed Alarm in: 5 minutes", Toast.LENGTH_SHORT).show();

                r.stop();
                vibrator.cancel();
                finish();
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            if (mAccel > 12) {
                Scount = (TextView)findViewById(R.id.Scount);
                Scount.setVisibility(View.VISIBLE);
                Scount.setText("Shake Count : "+ count);
                count = count+1;
                if(count == 11){
                    snooze.setVisibility(View.VISIBLE);
                }

            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    private void setNoti(){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.alarm)
                        .setContentTitle("Snoozed Alarm Set")
                        .setContentText("Alarm will back in 5 mins");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(0, builder.build());
    }



    /**@Override
    public void onPause() {
        super.onPause();
        startActivity(getIntent().addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        Toast.makeText(this, "Solve the Problem first then you can move out", Toast.LENGTH_SHORT).show();
    }*/

    @Override
    public void onBackPressed() {
        Toast.makeText(this,"Solve the problem first",Toast.LENGTH_SHORT).show();
    }
}