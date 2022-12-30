package com.example.qrcodescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //View Object
    private Button buttonScanning;
    private TextView textViewName, textViewClass, textViewId;
    //qr scanning object
    private IntentIntegrator qrScan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //view object
        buttonScanning = (Button) findViewById(R.id.buttonscan);
        textViewClass = (TextView) findViewById(R.id.textViewKelas);
        textViewName = (TextView) findViewById(R.id.textViewNama);
        textViewId = (TextView) findViewById(R.id.textViewNim);
        //Inisialisasi scan Object
        qrScan = new IntentIntegrator(this);

        //Implementasi onClick Listener
        buttonScanning.setOnClickListener(this);
    }
    //untuk mendapatkan hasil scanning
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //jika qrcode tidak ada sama sekali
            if (result.getContents() == null) {
                Toast.makeText(this, "Hasil Scanning tidak ada", Toast.LENGTH_LONG).show();
            } else if (Patterns.WEB_URL.matcher(result.getContents()).matches()) {
                Intent visitUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents()));
                startActivity(visitUrl);
            } else if (Patterns.PHONE.matcher(result.getContents()).matches()) {
                String telp = String.valueOf(result.getContents());
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + telp));
                startActivity(callIntent);
                try {
                    startActivity(Intent.createChooser(callIntent, "waiting..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "no phone apk client.", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                //jika qr code tidak ditemukan datanya
                try {
                    //konversi datanya ke json
                    JSONObject obj = new JSONObject(result.getContents());
                    //diset nilai datanya ke textviews
                    textViewName.setText(obj.getString("nama"));
                    textViewId.setText(obj.getString("nim"));
                    textViewClass.setText(obj.getString("kelas"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        //inisialisasi qrcode scanning
        qrScan.initiateScan();
    }
}