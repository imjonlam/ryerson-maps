package com.abcd.paulboutot.cps406project;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.abcd.paulboutot.cps406project.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import android.support.v4.app.Fragment;



/**
 * A simple {@link Fragment} subclass.
 */
public class ScanFragment extends Fragment {
    SurfaceView cameraView;
    BarcodeDetector barcode;
    CameraSource cameraSource;
    SurfaceHolder holder;
    Activity activity;
    Context context;
    Button button;

    public ScanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        context = activity.getApplicationContext();

        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup References
        button = view.findViewById(R.id.button);

        cameraView = (SurfaceView) view.findViewById(R.id.cameraView);
        cameraView.setZOrderMediaOverlay(true);
        holder = cameraView.getHolder();

        // Build Objects
        barcode = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        if(!barcode.isOperational()) {
            Toast.makeText(context, "Sorry, Couldn't setup the detector", Toast.LENGTH_LONG).show();
            activity.finish();
        }

        cameraSource = new CameraSource.Builder(context, barcode)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(24)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1920, 1024)
                .build();

        // Setup Callbacks
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                   if(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(cameraView.getHolder());
                    }

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

       button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setAlpha(0);
                barcode.setProcessor(new Detector.Processor<Barcode>() {
                    @Override
                    public void release() {

                    }

                    @Override
                    public void receiveDetections(Detector.Detections<Barcode> detections) {
                        final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                        if (barcodes.size() > 0) {
                            try {
                                String decoded = barcodes.valueAt(0).displayValue;
                                ((QRActivity) getActivity()).sendDecoded(decoded);
                                cameraSource.stop();
                            } catch (Exception e) {
                                Toast.makeText(getActivity().getApplicationContext(), "Invalid QR Type.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }
}
