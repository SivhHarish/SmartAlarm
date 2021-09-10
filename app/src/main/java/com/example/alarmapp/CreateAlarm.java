package com.example.alarmapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import android.util.*;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;
import java.util.Date;

public class CreateAlarm extends AppCompatActivity {

    //widgets
    TimePicker timePicker;
    TextView textView;
    ImageView setAlm;
    TextView setAlm1;
    ImageView mCancel;
    Button Qr;

    //vars
    private int mHour = 0;
    private int mMin = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_create_alarm);

        mCancel = (ImageView)findViewById(R.id.cancel);
        timePicker =(TimePicker)findViewById(R.id.timePicker);
        textView = (TextView)findViewById(R.id.timeTextView);
        setAlm = (ImageView)findViewById(R.id.setAlm);
        setAlm1 = (TextView)findViewById(R.id.setAlm1);
        Qr = (Button)findViewById(R.id.qr);


        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMin = minute;
                textView.setText("Time:"+" "+mHour+":"+mMin);

            }
        });

        setAlm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimer();
            }
        });

        setAlm1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimer();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAlarm.this,MainActivity.class));
            }
        });
        Qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAlarm.this,QrScanner.class));
            }
        });
    }

    private void setTimer(){
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Date dat = new Date();
        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTime(dat);
        cal_alarm.setTime(dat);
        cal_alarm.set(Calendar.HOUR_OF_DAY,mHour);
        cal_alarm.set(Calendar.MINUTE,mMin);
        cal_alarm.set(Calendar.SECOND,0);

        if (cal_alarm.before(cal_now)){
            cal_alarm.add(Calendar.DATE,1);
        }

        Calendar remain = cal_alarm;
        setNoti();

        Intent myIntent = new Intent(this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,24444,myIntent,0);
        manager.set(AlarmManager.RTC_WAKEUP,cal_alarm.getTimeInMillis(),pendingIntent);
        long calendar = cal_alarm.getTimeInMillis() - cal_now.getTimeInMillis();
        long mins = calendar/(1000*60);

        Toast.makeText(this, "Alarm in: "+ String.valueOf(mins) +" minutes", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    private void setNoti(){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.alarm)
                        .setContentTitle("Alarm Set in")
                        .setContentText("Time :"+mHour+" - "+mMin);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(0, builder.build());
    }



    @Override
    public void onBackPressed() {
        //do nothing
        startActivity(new Intent(CreateAlarm.this, MainActivity.class));
    }
}