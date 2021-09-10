package com.example.alarmapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

public class QrAlarmActivity extends AppCompatActivity {

    TextView finalResult;

    CodeScanner codeScanner1;
    CodeScannerView scannerView1;
    TextView resultData1;
    Button qrStopAlarm;

    private int count1 = 1;
    private SensorManager mSensorManager1;
    private float mAccel1;
    private float mAccelCurrent1;
    private float mAccelLast1;


    TextView tvDisplayDate1;
    Button Qrsnooze;

    //vars
    String matchingResult;
    static final long FIVE_MINUTE_IN_MILLIS_QR=60000;//millisecs
    //private boolean val = false;

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

        setContentView(R.layout.activity_qr_alarm);


        getQrcodescan();
        finalResult = findViewById(R.id.finalResult);
        finalResult.setText(matchingResult);


        tvDisplayDate1 = findViewById(R.id.Date1);
        long date1 = System.currentTimeMillis();


        SimpleDateFormat sdf = new SimpleDateFormat("MMM MM");
        String dateString = sdf.format(date1);
        tvDisplayDate1.setText(dateString);

        scannerView1 = findViewById(R.id.scannerView1);
        codeScanner1 = new CodeScanner(this,scannerView1);
        resultData1 = findViewById(R.id.resulsOfQr1);
        qrStopAlarm = findViewById(R.id.qrStopAlm);


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


        //QrSnooze
        Qrsnooze = findViewById(R.id.Qrsnooze);
        Qrsnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                setNoti1();

                Intent myIntent = new Intent(QrAlarmActivity.this, QrMyReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(QrAlarmActivity.this,24444,myIntent,0);
                manager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+ FIVE_MINUTE_IN_MILLIS_QR,pendingIntent);

                Toast.makeText(QrAlarmActivity.this, "Snoozed Alarm in: 5 minutes", Toast.LENGTH_SHORT).show();

                r.stop();
                vibrator.cancel();
                finish();
            }
        });



        codeScanner1.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultData1.setText(result.getText());
                        qrStopAlarm.setVisibility(View.VISIBLE);
                        /**Intent Qrintent = new Intent(QrScanner.this,QrScannerAlarm.class);
                         Qrintent.putExtra("Saved_QR_code", result.getText());
                         startActivity(Qrintent);*/
                    }
                });
            }
        });

        //String results = resultData.getText().toString();

        scannerView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner1.startPreview();
            }
        });



        qrStopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resultData1.getText().toString().equals(finalResult.getText().toString())) {
                    Toast.makeText(QrAlarmActivity.this, "QrCode Matched...", Toast.LENGTH_SHORT).show();
                    r.stop();
                    vibrator.cancel();
                    finishAffinity();
                    finish();

                }
                else{
                    Toast.makeText(QrAlarmActivity.this, "QrCode is incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSensorManager1 = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager1).registerListener(mSensorListener1, mSensorManager1.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel1 = 10f;
        mAccelCurrent1 = SensorManager.GRAVITY_EARTH;
        mAccelLast1 = SensorManager.GRAVITY_EARTH;


    }

    private void getQrcodescan(){
        try {
            FileInputStream finResult = openFileInput("SavedQrcode.txt");

            if (finResult != null){
                InputStreamReader inputStreamReader = new InputStreamReader(finResult);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while((receiveString = bufferedReader.readLine())!=null){
                    stringBuilder.append(receiveString);
                }
                finResult.close();
                matchingResult = stringBuilder.toString();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private final SensorEventListener mSensorListener1 = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast1 = mAccelCurrent1;
            mAccelCurrent1 = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent1 - mAccelLast1;
            mAccel1 = mAccel1 * 0.9f + delta;
            if (mAccel1 > 12) {
                TextView Scount1 = findViewById(R.id.Scount1);
                Scount1.setVisibility(View.VISIBLE);
                Scount1.setText("Shake Count : " + count1);
                count1 = count1 + 1;
                if (count1 == 11) {
                    Qrsnooze.setVisibility(View.VISIBLE);
                }

            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

        private void setNoti1(){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.alarm)
                        .setContentTitle("Snoozed Alarm Set")
                        .setContentText("Alarm will back in 5 mins");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent1 = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent1);

        // Add as notification
        NotificationManager nManager1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager1.notify(0, builder.build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestForCamera();
    }

    private void requestForCamera() {
        Dexter.withContext(this).withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                codeScanner1.startPreview();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                Toast.makeText(QrAlarmActivity.this, "These Permission required to run QR Code", Toast.LENGTH_SHORT).show();
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    @Override
    public void onBackPressed() {
        //do nothing
        Toast.makeText(this, "Match the Qrcode First", Toast.LENGTH_SHORT).show();
    }

}