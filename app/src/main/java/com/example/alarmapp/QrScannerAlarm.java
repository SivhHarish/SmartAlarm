package com.example.alarmapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;


public class QrScannerAlarm extends AppCompatActivity {

    //widgets
    TimePicker timePicker1;
    TextView QrtextView;
    ImageView QrsetAlm;
    TextView QrsetAlm1;
    ImageView QrmCancel;
    TextView resultView1;

    //vars
    private int mHour1 = 0;
    private int mMin1 = 0;
    String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_qr_scanner_alarm);

        resultView1 = findViewById(R.id.resultView1);
        value = getIntent().getStringExtra("Saved_QR");
        resultView1.setText(value);
        resultView1.setPaintFlags(resultView1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        QrmCancel = (ImageView)findViewById(R.id.Qrcancel);
        timePicker1 =(TimePicker)findViewById(R.id.QrtimePicker);
        QrtextView = (TextView)findViewById(R.id.QrtimeTextView);
        QrsetAlm = (ImageView)findViewById(R.id.QrsetAlm);
        QrsetAlm1 = (TextView)findViewById(R.id.QrsetAlm1);

        timePicker1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mHour1 = hourOfDay;
                mMin1 = minute;
                QrtextView.setText("Time:"+" "+mHour1+":"+mMin1);

            }
        });

        QrsetAlm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimer();
            }
        });

        QrsetAlm1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimer();
            }
        });

        QrmCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QrScannerAlarm.this,QrScanner.class));
            }
        });
    }


    private void setTimer(){
        AlarmManager manager1 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Date dat = new Date();
        Calendar cal_alarm1 = Calendar.getInstance();
        Calendar cal_now1 = Calendar.getInstance();
        cal_now1.setTime(dat);
        cal_alarm1.setTime(dat);
        cal_alarm1.set(Calendar.HOUR_OF_DAY,mHour1);
        cal_alarm1.set(Calendar.MINUTE,mMin1);
        cal_alarm1.set(Calendar.SECOND,0);

        if (cal_alarm1.before(cal_now1)){
            cal_alarm1.add(Calendar.DATE,1);
        }

        Calendar remain1 = cal_alarm1;
        setNoti();

        //saving the Qrcode
        saveFile();

        Intent myIntent = new Intent(this, QrMyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,24445,myIntent,0);
        manager1.set(AlarmManager.RTC_WAKEUP,cal_alarm1.getTimeInMillis(),pendingIntent);
        long calendar1 = cal_alarm1.getTimeInMillis() - cal_now1.getTimeInMillis();
        long mins1 = calendar1/(1000*60);

        Toast.makeText(this, "Alarm in: "+ String.valueOf(mins1) +" minutes", Toast.LENGTH_SHORT).show();

        this.finish();
        startActivity(new Intent(this,MainActivity.class));
    }

    private void setNoti(){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.alarm)
                        .setContentTitle("Alarm Set in")
                        .setContentText("Time :"+mHour1+" - "+mMin1);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(0, builder.build());
    }


    private void saveFile(){
        if (value!= null) {
            File file = new File(QrScannerAlarm.this.getFilesDir(), "text");
            if (!file.exists()) {
                file.mkdir();
            }
            try {
                String data = value;
                FileOutputStream fOut = openFileOutput ( "SavedQrcode.txt" , Context.MODE_PRIVATE ) ;
                OutputStreamWriter osw = new OutputStreamWriter ( fOut ) ;
                osw.write (data) ;
                osw.flush();
                osw.close ( ) ;
                Toast.makeText(QrScannerAlarm.this, "Your QrCode is saved", Toast.LENGTH_LONG).show();
            } catch (Exception e) { }
        }
    }


    @Override
    public void onBackPressed() {
        //do nothing
        startActivity(new Intent(QrScannerAlarm.this, QrScanner.class));
    }
}