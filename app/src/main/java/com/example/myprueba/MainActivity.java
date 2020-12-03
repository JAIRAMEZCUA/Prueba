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
import com.na_at.sdk.commons.config.module.AppointmentConfig;
import com.na_at.sdk.commons.config.module.Document;
import com.na_at.sdk.commons.config.module.EnrollConfig;
import com.na_at.sdk.commons.config.module.FaceConfig;
import com.na_at.sdk.commons.config.module.FingerprintIDConfig;
import com.na_at.sdk.commons.config.module.IdentityConfig;
import com.na_at.sdk.commons.config.module.OtherDocsConfig;
import com.na_at.sdk.commons.config.module.ResumeConfig;
import com.na_at.sdk.commons.config.module.SignConfig;
import com.na_at.sdk.commons.config.module.VideoConferenceConfig;
import com.na_at.sdk.commons.data.AssetSource;
import com.na_at.sdk.commons.model.enroll.Finger;
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
    private static final int FAD_SDK_REQUEST_CODE_4 = 3458;
    private static final int FAD_SDK_REQUEST_CODE_5 = 6558;
    private static final int FAD_SDK_REQUEST_CODE_6 = 9458;
    private static final int FAD_SDK_REQUEST_CODE_7 = 458;
    private static final int FAD_SDK_REQUEST_CODE_8 = 658;


    private FadManager mFadManager;


    private File file;
    FadCredentials credentials;

    Button mbtnFER,mbtnZTS;

    Button btnEnroll,btnSign,btnFinger,btnVideo,btnOther,btnappo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mbtnFER = findViewById(R.id.FIR);
        mbtnZTS  = findViewById(R.id.ZTS);
        btnEnroll = findViewById(R.id.btnEnroll);
        btnSign = findViewById(R.id.btnSign);
        btnFinger = findViewById(R.id.btnFing);
        btnVideo = findViewById(R.id.btnVideo);
        btnOther = findViewById(R.id.btnOther);
        btnappo = findViewById(R.id.btnappoint);

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


         btnappo.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 FadConfig fadConfig = FadConfig.builder()
                         .credentials(credentials)
                         .addConfig(getAppointmentConfig())
                         .build();
                 FadManager.IntentBuilder builder = mFadManager.newIntentBuilder()
                         .config(fadConfig);
                 Intent  intent= builder.build(getApplicationContext());
                 startActivityForResult(intent, FAD_SDK_REQUEST_CODE_8);
             }
         });
         btnOther.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 FadConfig fadConfig = FadConfig.builder()
                         .credentials(credentials)
                         .addConfig(getOtherDocConfig())
                         .build();
                 FadManager.IntentBuilder builder = mFadManager.newIntentBuilder()
                         .config(fadConfig);
                 Intent  intent= builder.build(getApplicationContext());
                 startActivityForResult(intent, FAD_SDK_REQUEST_CODE_7);
             }
         });
         btnVideo.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 FadConfig fadConfig = FadConfig.builder()
                         .credentials(credentials)
                         .addConfig(getVideoConferenceConfig())
                         .build();
                 FadManager.IntentBuilder builder = mFadManager.newIntentBuilder()
                         .config(fadConfig);
                 Intent  intent= builder.build(getApplicationContext());
                 startActivityForResult(intent, FAD_SDK_REQUEST_CODE_6);
             }
         });

         btnSign.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 FadConfig fadConfig = FadConfig.builder()
                         .credentials(credentials)
                         .addConfig(getSignConfig())
                         .build();
                 FadManager.IntentBuilder builder = mFadManager.newIntentBuilder()
                         .config(fadConfig);
                 Intent  intent= builder.build(getApplicationContext());
                 startActivityForResult(intent, FAD_SDK_REQUEST_CODE_5);
             }
         });
         btnFinger.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 FadConfig fadConfig = FadConfig.builder()
                         .credentials(credentials)
                         .addConfig(getFingerprintIDConfig())
                         .build();
                 FadManager.IntentBuilder builder = mFadManager.newIntentBuilder()
                         .config(fadConfig);
                 Intent  intent= builder.build(getApplicationContext());
                 startActivityForResult(intent, FAD_SDK_REQUEST_CODE_4);
             }
         });

         btnEnroll.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 FadConfig fadConfig = FadConfig.builder()
                         .credentials(credentials)
                         .addConfig(enrollConfig())
                         .build();
                 FadManager.IntentBuilder builder = mFadManager.newIntentBuilder()
                         .config(fadConfig);


                 Intent  intent= builder.build(getApplicationContext());
                 startActivityForResult(intent, FAD_SDK_REQUEST_CODE_2);
             }
         });
        mbtnFER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FadConfig fadConfig = FadConfig.builder()
                        .credentials(credentials)
                        //.addConfig(identityConfig())
                        .addConfig(faceConfig2())
                        .addConfig(getResumeConfig())
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
                FadConfig.Builder builder = FadConfig.builder()
                        .endpoint(StringUtils.encode("https://uat.firmaautografa.com"))
                        .requestLocation(true)
                        .preventScreenCapture(false)
                        .credentials(credentials);

               // builder.addConfig(DefaultIdentityConfig.build());
                builder.addConfig(getFaceZoomConfig());
                builder.addConfig(getResumeConfig());

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

    private SignConfig getSignConfig() {
        SignConfig.FadSource xmlSource = new AssetSource("data.xml");
        SignConfig.FadSource pdfSource = new AssetSource("pdf.pdf");
        return SignConfig.builder(xmlSource, pdfSource)
                // defaults
                .build();
    }


    private AppointmentConfig getAppointmentConfig() {
        return AppointmentConfig
                .builder()
                .contactEmail("amartinez@na-at.com.mx")
                .contactFullName("Alexis Martinez")
                .contactNumber("5522977855")
                .build();
    }
    private FingerprintIDConfig getFingerprintIDConfig() {
        FingerprintIDConfig.Builder builder = FingerprintIDConfig.builder()
                .setTypeScanner(FingerprintIDConfig.SCANNER_TYPE_KARALUNDI)
                .setMaxNfiqValid(5)
                .setMaxCaptureAttempts(-1)
                .setOptionOptic(false)
                .setOptionCamera(false)
                .setFingerOptions(new Finger[]{Finger.LEFT_INDEX, Finger.LEFT_MIDDLE, Finger.LEFT_RING, Finger.LEFT_LITTLE, Finger.RIGHT_INDEX, Finger.RIGHT_MIDDLE, Finger.RIGHT_RING, Finger.RIGHT_LITTLE})
                //.setFingerOptions(new Finger[] {Finger.LEFT_INDEX})
                .addProp("API_KEY", "AIzaSyAlG8ML3lOwPHiqIlte6SUnOuNGzfDFi5g")
                .addProp("LICENSE", "com.fad.bio.poc2020-06-15 00 00 00.lic")
                .setCloseOnError(false);


        return builder.build();
    }

    private EnrollConfig enrollConfig(){
      return EnrollConfig.builder()
                .scannerType(EnrollConfig.SCANNER_TYPE_WATSON)
                .minFingerCapture(0)
                 .maxCaptureAttempts(3)
                .maxValidNfiq(10)
                .build();

    }

    private ResumeConfig getResumeConfig() {
        return ResumeConfig.builder()
                .showResult(true)
               // .setFaceValueCompare(50)
                .build();
    }
    private FaceConfig faceConfig() {
        int[] gestures = new int[]{
                FaceConfig.GESTURE_TURN_RIGHT,
                FaceConfig.GESTURE_TURN_LEFT,
                FaceConfig.GESTURE_BLINK,
                FaceConfig.GESTURE_SMILE,
        };
        FaceConfig faceConfig = FaceConfig.builder()
                .mode(FaceConfig.MODE_DYNAMIC)
                .availableGestures(gestures) //Pasamos el arreglo con los gestos.
                .onlyFrontCamera(true)//Camara frontal activada.
                .onlyRearCamera(false)//Camara Trasera de deshabilitada.
                .build();

        return faceConfig;
    }

    private FaceConfig faceConfig2() {
        return FaceConfig.builder()
                .mode(FaceConfig.MODE_TIME)
                .captureTime(4) //segundos
                .onlyFrontCamera(true)
                .onlyRearCamera(false)
                .build();
    }

    private OtherDocsConfig getOtherDocConfig() {
        Document document = new Document("Comprobante");
        document.setName("Comprobante");
        OtherDocsConfig.Builder builder = OtherDocsConfig.builder()
                .addDocument(document)
                .setOptionalMode(true)
                .setLimitDocuments(1);
        return builder.build();
    }

    private VideoConferenceConfig getVideoConferenceConfig() {
        return VideoConferenceConfig.builder()
                .contactFullName("Carlos Enrique Allendelagua Sanchez")
                .setName("Carlos")
                .setLastName("Allendelagua")
                .setSecondName("Sanchez")
                .contactNumber("525545304357")
                .contactEmail("callendelagua@na-at.com.mx")
                .setVideoconferenceId("")
                .setScriptId("4504")
                .build();
    }


    private FaceConfig getFaceZoomConfig(){
        return FaceConfig.builder()
                .setType(FaceConfig.ZOOM)
                .addProperty(FaceConfig.ZOOM_API_KEY, "d5jKXRWPvpulWiBPEqGcnlDsx2ionDwn")
                .setSimilarityPercent(50)
                .build();
    }

    private IdentityConfig identityConfig(){
        Log.d("identity", "SE EJECUTO IDENTITY");
        IdentityConfig identityConfig = DefaultIdentityConfig.build();
        return  identityConfig;
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
        if (requestCode == FAD_SDK_REQUEST_CODE_4) {
            //close is forced in OrchestratorActivity
            if (data == null) return;
            aprueba(resultCode,data);

        }
        if (requestCode == FAD_SDK_REQUEST_CODE_5) {
            //close is forced in OrchestratorActivity
            if (data == null) return;
            aprueba(resultCode,data);

        }
        if (requestCode == FAD_SDK_REQUEST_CODE_6) {
            //close is forced in OrchestratorActivity
            if (data == null) return;
            aprueba(resultCode,data);

        }

        if (requestCode == FAD_SDK_REQUEST_CODE_7) {
            //close is forced in OrchestratorActivity
            if (data == null) return;
            aprueba(resultCode,data);

        }

        if (requestCode == FAD_SDK_REQUEST_CODE_8) {
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
