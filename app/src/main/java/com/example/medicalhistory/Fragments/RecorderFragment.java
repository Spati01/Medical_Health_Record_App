package com.example.medicalhistory.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.medicalhistory.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class RecorderFragment extends Fragment {

   View view;
    ImageButton btnRec;
    TextView txtRecStatus;
    Chronometer timeRec;

    GifImageView gifView;

    private static String fileName;
    private MediaRecorder recorder;
    boolean isRecording;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};




 //   File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/VRecorder");
    File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toURI());



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recorder,container,false);

        btnRec=view.findViewById(R.id.btnRec);
        txtRecStatus=view.findViewById(R.id.txtRecStatus);
        gifView = view.findViewById(R.id.gifView);
        timeRec = view.findViewById(R.id.timeRec);
        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,REQUEST_RECORD_AUDIO_PERMISSION);
        isRecording = false;


        askruntimePermission();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String date = format.format(new Date());

       /*fileName = getContext().getExternalCacheDir().getAbsolutePath();
       fileName += "/VRecorder.3gp";*/
        fileName = path + "/recording_" + date + ".amr";
        if(!path.exists()){
            path.mkdirs();
        }




        
        btnRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isRecording){
                   try{
                       startRecording();
                       gifView.setVisibility(View.VISIBLE);
                       timeRec.setBase(SystemClock.elapsedRealtime());
                       timeRec.start();
                       txtRecStatus.setText("Recording.....");
                       btnRec.setImageResource(R.drawable.ic_stop);
                       isRecording=true;
                   }
                   catch (Exception e){
                       e.printStackTrace();
                       Toast.makeText(getContext(), "Couldn't Record", Toast.LENGTH_SHORT).show();
                   }
                }
                else if (isRecording) {
                    stopRecording();
                    gifView.setVisibility(View.GONE);
                    timeRec.setBase(SystemClock.elapsedRealtime());
                    timeRec.stop();
                    txtRecStatus.setText("");
                    btnRec.setImageResource(R.drawable.ic_record);
                    isRecording = false;
                }
            }
        });
        return view;

    }




    private void startRecording(){
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(fileName);


        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
    }


    private void stopRecording(){
        recorder.stop();
        recorder.release();
        recorder = null;
    }
    private void askruntimePermission() {

    /* Dexter.withContext(getContext()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
             Manifest.permission.WRITE_EXTERNAL_STORAGE,
             Manifest.permission.RECORD_AUDIO).withListener(new MultiplePermissionsListener() {
         @Override
         public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
             //Toast.makeText(getContext(), "Granted!!", Toast.LENGTH_SHORT).show();
         }

         @Override
         public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
           
             permissionToken.continuePermissionRequest();
             
         }
     }).check(); */
   
    }

//
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(getActivity(), new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(getActivity(), "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }





    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);


          if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

