package com.example.alarmapp;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterBuilder;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;


public class QrScanner extends AppCompatActivity {


    CodeScanner codeScanner;
    CodeScannerView scannerView;
    TextView resultData;
    Button scannedResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_qr_scanner);

        scannerView = findViewById(R.id.scannerView);
        codeScanner = new CodeScanner(this,scannerView);
        resultData = findViewById(R.id.resulsOfQr);
        scannedResults = findViewById(R.id.scannedResults);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultData.setText(result.getText());
                        scannedResults.setVisibility(View.VISIBLE);
                        /**Intent Qrintent = new Intent(QrScanner.this,QrScannerAlarm.class);
                        Qrintent.putExtra("Saved_QR_code", result.getText());
                        startActivity(Qrintent);*/
                    }
                });
            }
        });




        //String results = resultData.getText().toString();

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();
            }
        });


        scannedResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Qrintent = new Intent(QrScanner.this,QrScannerAlarm.class);
                Qrintent.putExtra("Saved_QR", resultData.getText().toString());
                startActivity(Qrintent);
            }
        });
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
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                Toast.makeText(QrScanner.this, "These Permission required to run QR Code", Toast.LENGTH_SHORT).show();
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    @Override
    public void onBackPressed() {
        //do nothing
        startActivity(new Intent(QrScanner.this, CreateAlarm.class));
    }
}