package com.example.myprueba;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;


import com.example.myprueba.tf.processor.INEProcessorTF;
import com.na_at.sdk.commons.Constants;
import com.na_at.sdk.commons.config.FadConfig;
import com.na_at.sdk.commons.config.FadCredentials;
import com.na_at.sdk.commons.config.module.FaceConfig;
import com.na_at.sdk.commons.config.module.IdentityConfig;
import com.na_at.sdk.commons.util.FileManager;
import com.na_at.sdk.commons.util.StringUtils;
import com.na_at.sdk.identity.model.DefaultIdentityConfig;
import com.na_at.sdk.identity.processor.ImageProcessor;
import com.na_at.sdk.identity.processor.ImageProcessorFactory;
import com.na_at.sdk.manager.FadManager;

import java.io.File;

public class MainActivity extends AppCompatActivity  {

    private static final int FAD_SDK_REQUEST_CODE = 1234;
    private static final int FAD_SDK_REQUEST_CODE_2 = 12345;
    private static final int FAD_SDK_REQUEST_CODE_3 = 345;
    private FadManager mFadManager;


    private File file;
    FadCredentials credentials;

    Button mbtnFER,mbtnZTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mbtnFER = findViewById(R.id.FIR);
        mbtnZTS  = findViewById(R.id.ZTS);
        // build manager
        mFadManager = FadManager.builder(this)
                .build();

        //credentials
        credentials = FadCredentials.builder()
                .client("fad")
                .secret("fadsecret")
                .username("avillanueva@na-at.com.mx")
                .password("c775e7b757ede630cd0aa1113bd102661ab38829ca52a6422ab782862f268646")
                .build();


        mbtnFER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FadConfig fadConfig = FadConfig.builder()
                        .credentials(credentials)
                        .addConfig(faceConfig())
                        .addConfig(identityConfig())
                        .build();
                FadManager.IntentBuilder builder = mFadManager.newIntentBuilder()
                        .config(fadConfig);


                Intent  intent= builder.build(getApplicationContext());
                startActivityForResult(intent, FAD_SDK_REQUEST_CODE_3);
            }
        });



        mbtnZTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    FadCredentials credentials = FadCredentials.builder()
                            .client("fad")
                            .secret("fadsecret")
                            .username("avillanueva@na-at.com.mx")
                            .password("c775e7b757ede630cd0aa1113bd102661ab38829ca52a6422ab782862f268646")
                            .build();


                    FadConfig.Builder builder = FadConfig.builder()
                            .endpoint(StringUtils.encode("https://uat.firmaautografa.com"))
                            .requestLocation(true)
                            .preventScreenCapture(false)
                            .credentials(credentials);

                builder.addConfig(getFaceZoomConfig());
                builder.addConfig(DefaultIdentityConfig.build());


                ImageProcessorFactory.getInstance().register(ImageProcessor.CAPTURE_INE_FRONT, INEProcessorTF.class);
                ImageProcessorFactory.getInstance().register(ImageProcessor.CAPTURE_INE_BACK, INEProcessorTF.class);

                FadManager.IntentBuilder intentBuilder = mFadManager.newIntentBuilder()
                        .showHeader(true)
                        .showSubHeader(false)
                        .config(builder.build());

                Intent  intent= intentBuilder.build(getApplicationContext());
                startActivityForResult(intent, FAD_SDK_REQUEST_CODE);

            }
        });
    }




    private FaceConfig faceConfig() {
        Log.d("FACE", "SE EJECUTO FACE");

        int[] gestures = new int[]{
                FaceConfig.GESTURE_TURN_RIGHT,
                FaceConfig.GESTURE_TURN_LEFT,
                FaceConfig.GESTURE_BLINK,
                FaceConfig.GESTURE_SMILE,
        };
        FaceConfig faceConfig = FaceConfig.builder()
                .mode(FaceConfig.MODE_DYNAMIC)
                .availableGestures(gestures)
                .onlyFrontCamera(true)
                .onlyRearCamera(true)
                .build();

        return faceConfig;
    }



    private FaceConfig getFaceZoomConfig(){
        Log.d("FACECONFIG", "SE EJECUTO ZOOM");

        return FaceConfig.builder()
                .setType(FaceConfig.ZOOM)
                .addProperty(FaceConfig.ZOOM_API_KEY, "d5jKXRWPvpulWiBPEqGcnlDsx2ionDwn")
                .build();
    }

    private IdentityConfig identityConfig(){
        Log.d("identity", "SE EJECUTO IDENTITY");
        IdentityConfig identityConfig = DefaultIdentityConfig.build();
       return  identityConfig;
/*
        Option ineOption = Option.builder()
                .setLabel("INE")
                .withDocuments(1, new INE())
//                .setLabel("Cédula Colombiana")
//                .withDocuments(1, new CedulaColombiana())
                .build();

        IdentityConfig identityConfig = IdentityConfig.builder()
             .setShowSecurityFeatures(true)
             .setOcrApiKey("c0e69e96fdaa54852d464df79ab3e84dfa923a7570d25f59ecc1da2295cb20e5")
             .setOcrModuleName("com.example.myprueba")
                .withOption(ineOption)
                .
                .setOverwrite(true)

             .build();
        return identityConfig;

 */
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FAD_SDK_REQUEST_CODE) {
            //close is forced in OrchestratorActivity
            if (data == null) return;
            aprueba(resultCode,data);
        }
        if (requestCode == FAD_SDK_REQUEST_CODE_2) {
            //close is forced in OrchestratorActivity
            if (data == null) return;
            aprueba(resultCode,data);
        }
        if (requestCode == FAD_SDK_REQUEST_CODE_3) {
            //close is forced in OrchestratorActivity
            if (data == null) return;
            aprueba(resultCode,data);

        }
    }


    public void aprueba( int resultCode, @Nullable Intent data){
        String processId = data.getExtras().getString("processId");
        boolean success = data.getExtras().getBoolean("success");

        if (resultCode == RESULT_OK) {
            Log.i("SDK", "SDK Finish!!!");
            if (success) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true)
                        .setIcon(R.drawable.ic_fad_bio_logo)
                        .setTitle("App")
                        .setMessage("Process " + processId + " complete!");
                builder.create()
                        .show();
            } else {
                String error = data.getExtras().getString("error");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true)
                        .setIcon(R.drawable.ic_fad_bio_logo)
                        .setTitle("App - " + processId)
                        .setMessage(error);
                builder.create()
                        .show();
            }
        } else {
            assert (resultCode == RESULT_CANCELED);
            String error = data.getExtras().getString("error");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true)
                    .setIcon(R.drawable.ic_fad_bio_logo)
                    .setTitle("App")
                    .setMessage("Process '" + processId + "'\nError: " + error);
            builder.create()
                    .show();
        }
    }
}
