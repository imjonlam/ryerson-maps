package com.abcd.paulboutot.cps406project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.abcd.paulboutot.cps406project.R;
import com.abcd.paulboutot.cps406project.ScanFragment;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class QRActivity extends AppCompatActivity {
    private int REQUEST_CODE = 1;
    private ImageButton selectImage;
    TextView txtView;
    BarcodeDetector detector;
    ScanFragment scanFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        // Checking Permissions
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE);
        }
        else {
            connectFragment();
        }

        // Setup References
        detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                .build();
        selectImage = findViewById(R.id.selectImage);

        // Setup Listeners
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().setType("image/*");
                intent.setAction(intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Boolean success = false;

        if (requestCode == 0) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<Barcode> barcodes = detector.detect(frame);
                    Barcode thisCode = barcodes.valueAt(0);
                    sendDecoded(thisCode.rawValue);
                    success = true;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    connectFragment();
                    Toast.makeText(getApplicationContext(), "Invalid QR Type.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (!success) {connectFragment();}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        connectFragment();
    }

   private void connectFragment() {
        scanFragment = new ScanFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.scan_fragment, scanFragment, null);
        fragmentTransaction.commit();
    }

    public void sendDecoded(String message) {
        Intent intent = new Intent(this, MainActivity.class).putExtra("fromQR", message);
        startActivity(intent);
    }
}
