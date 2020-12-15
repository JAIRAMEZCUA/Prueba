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

import com.na_at.sdk.commons.config.FadConfig;
import com.na_at.sdk.commons.config.FadCredentials;
import com.na_at.sdk.commons.config.module.AppointmentConfig;
import com.na_at.sdk.commons.config.module.AuthConfig;
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
import com.na_at.sdk.commons.model.identity.ProviderConfiguration;
import com.na_at.sdk.commons.util.FileManager;
import com.na_at.sdk.commons.util.StringUtils;
import com.na_at.sdk.data.db.entity.auth.AuthModule;
import com.na_at.sdk.identity.model.DefaultIdentityConfig;
import com.na_at.sdk.identity.processor.ImageProcessor;
import com.na_at.sdk.identity.processor.ImageProcessorFactory;
import com.na_at.sdk.manager.FadManager;

import java.io.File;

public class MainActivity extends AppCompatActivity  {

    private static final int FAD_SDK_REQUEST_CODE = 1234;



    private FadManager mFadManager;


    private File file;
    FadCredentials credentials;

    Button mbtnZTS;

    Button mbtnFace,mBtnFZoom,btnAcuant;
    Button btnIdenty,btnIdentyFlow;
    Button btnAut;

    Button btnConfi1;

    Button btnEnroll,btnSign,btnFinger,btnVideo,btnOther,btnappo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mbtnFace = findViewById(R.id.btnFace);
        mBtnFZoom = findViewById(R.id.btnFaceZoom);
        btnAcuant = findViewById(R.id.btnAcuant);
        btnIdenty = findViewById(R.id.btnIdenty);
        btnIdentyFlow = findViewById(R.id.btnIdentyFlow);
        btnOther = findViewById(R.id.btnDocs);
        btnConfi1 = findViewById(R.id.btnConfi1);

        mbtnZTS = findViewById(R.id.ZTS);
        btnEnroll = findViewById(R.id.btnEnroll);
        btnSign = findViewById(R.id.btnSign);
        btnFinger = findViewById(R.id.btnFing);
        btnVideo = findViewById(R.id.btnVideo);
        btnappo = findViewById(R.id.btnappoint);

        btnAut = findViewById(R.id.btnaut);

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


        btnOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FadConfig fadConfig = FadConfig.builder()
                        .credentials(credentials)
                        .addConfig(getOtherDocConfig())
                        .build();
                FadManager.IntentBuilder builder = mFadManager.newIntentBuilder()
                        .config(fadConfig);
                Intent intent = builder.build(getApplicationContext());
                startActivityForResult(intent, FAD_SDK_REQUEST_CODE);
            }
        });


        btnappo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FadConfig fadConfig = FadConfig.builder()
                        .credentials(credentials)
                        .addConfig(getAppointmentConfig())
                        .build();
                FadManager.IntentBuilder builder = mFadManager.newIntentBuilder()
                        .config(fadConfig);
                Intent intent = builder.build(getApplicationContext());
                startActivityForResult(intent, FAD_SDK_REQUEST_CODE);
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
                Intent intent = builder.build(getApplicationContext());
                startActivityForResult(intent, FAD_SDK_REQUEST_CODE);
            }
        });

/*
        mbtnZTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FadConfig.Builder builder = FadConfig.builder()
                        .endpoint(StringUtils.encode("https://uat.firmaautografa.com"))
                        .requestLocation(true)
                        .preventScreenCapture(false)
                        .credentials(credentials);

                builder.addConfig(DefaultIdentityConfig.build());
              //  builder.addConfig(getFaceZoomConfig());
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

    /*








        mbtnFER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FadConfig fadConfig = FadConfig.builder()
                        .credentials(credentials)
                        //  .addConfig(identityConfig())
                        .addConfig(faceConfig())
                        //.addConfig(getResumeConfig())
                        .build();
                FadManager.IntentBuilder builder = mFadManager.newIntentBuilder()
                        .config(fadConfig);


                Intent intent = builder.build(getApplicationContext());
                startActivityForResult(intent, FAD_SDK_REQUEST_CODE);
            }
        });

         */

        btnAut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FadConfig fadConfig = FadConfig.builder()
                        .credentials(credentials)
                        .addConfig(createAuthModule())
                        .build();
                FadManager.IntentBuilder builder = mFadManager.newIntentBuilder()
                        .config(fadConfig);
                Intent intent = builder.build(getApplicationContext());
                startActivityForResult(intent, FAD_SDK_REQUEST_CODE);
            }
        });

        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FadConfig fadConfig = FadConfig.builder()
                        .credentials(credentials)
                        .addConfig(faceConfig())
                        .addConfig(getSignConfig())
                        .addConfig(getOtherDocConfig())
                        .addConfig(getResumeConfig())
                        .build();
                FadManager.IntentBuilder builder = mFadManager.newIntentBuilder()
                        .config(fadConfig);
                Intent intent = builder.build(getApplicationContext());
                startActivityForResult(intent, FAD_SDK_REQUEST_CODE);
            }
        });

        mbtnFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FadConfig fadConfig = FadConfig.builder()
                        .credentials(credentials)
                        .addConfig(faceConfig())
                        .build();
                FadManager.IntentBuilder builder = mFadManager.newIntentBuilder()
                        .config(fadConfig);


                Intent intent = builder.build(getApplicationContext());
                startActivityForResult(intent, FAD_SDK_REQUEST_CODE);
            }
        });

        btnAcuant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                testAcuantFace();
            }
        });


        mBtnFZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FadConfig fadConfig = FadConfig.builder()
                        .credentials(credentials)
                        .addConfig(getFaceZoomConfig())
                        .build();
                FadManager.IntentBuilder builder = mFadManager.newIntentBuilder()
                        .config(fadConfig);


                Intent intent = builder.build(getApplicationContext());
                startActivityForResult(intent, FAD_SDK_REQUEST_CODE);
            }
        });



        btnIdenty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FadConfig fadConfig = FadConfig.builder()
                        .credentials(credentials)
                        .addConfig(identityConfig())
                        .build();
                FadManager.IntentBuilder builder = mFadManager.newIntentBuilder()
                        .config(fadConfig);


                Intent intent = builder.build(getApplicationContext());
                startActivityForResult(intent, FAD_SDK_REQUEST_CODE);
            }
        });


/*
        btnIdentyFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FadConfig.Builder builder = FadConfig.builder()
                        .endpoint(StringUtils.encode("https://uat.firmaautografa.com"))
                        .requestLocation(true)
                        .preventScreenCapture(false)
                        .credentials(credentials);

                builder.addConfig(DefaultIdentityConfig.build());
                //  builder.addConfig(getFaceZoomConfig());
                //builder.addConfig(getResumeConfig());

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

 */
        btnConfi1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FadConfig fadConfig = FadConfig.builder()
                        .credentials(credentials)
                         .addConfig(identityConfig())
                         .addConfig(faceConfig())
                         .addConfig(enrollConfig())
                         .addConfig(getResumeConfig())

                        .build();
                FadManager.IntentBuilder builder = mFadManager.newIntentBuilder()
                        .config(fadConfig);


                Intent intent = builder.build(getApplicationContext());
                startActivityForResult(intent, FAD_SDK_REQUEST_CODE);
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
                Intent intent = builder.build(getApplicationContext());
                startActivityForResult(intent, FAD_SDK_REQUEST_CODE);
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


                Intent intent = builder.build(getApplicationContext());
                startActivityForResult(intent, FAD_SDK_REQUEST_CODE);
            }
        });

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

    private EnrollConfig enrollConfig(){
        return EnrollConfig.builder()
                .scannerType(EnrollConfig.SCANNER_TYPE_WATSON)
                .minFingerCapture(0)
                .maxCaptureAttempts(3)
                .maxValidNfiq(10)
                .build();

    }

    private FaceConfig faceConfig2() {
        return FaceConfig.builder()
                .mode(FaceConfig.MODE_TIME)
                .captureTime(4) //segundos
                .onlyFrontCamera(true)
                .onlyRearCamera(false)
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

    private FaceConfig getFaceZoomConfig(){
        return FaceConfig.builder()
                .setType(FaceConfig.ZOOM)
                .addProperty(FaceConfig.ZOOM_API_KEY, "d5jKXRWPvpulWiBPEqGcnlDsx2ionDwn")
                .setSimilarityPercent(50)
                .build();
    }


    private IdentityConfig identityConfig(){
        IdentityConfig identityConfig = DefaultIdentityConfig.build();
        return  identityConfig;
    }


    private ResumeConfig getResumeConfig() {
        return ResumeConfig.builder()
                .showResult(true)
                // .setFaceValueCompare(50)
                .build();
    }


    private FingerprintIDConfig getFingerprintIDConfig() {

        return FingerprintIDConfig.builder()
                //.setTypeScanner(FingerprintIDConfig.SCANNER_TYPE_WATSON)
                .setTypeScanner(null)
                .setOptionOptic(true)
                .setOptionCamera(true)
                .addProp("API_KEY", "AIzaSyAlG8ML3lOwPHiqIlte6SUnOuNGzfDFi5g")
                .addProp("LICENSE", "com.fad.bio.poc2020-06-15 00 00 00.lic")
                .build();

    }


    private  AuthConfig createAuthModule() {
        return AuthConfig.builder()
                .setTypeScanner(AuthConfig.SCANNER_TYPE_WATSON)
                .setAuthId("1234")
                .setAuthName("Alexis Martínez")
                .build();
    }

    private SignConfig getSignConfig() {
        SignConfig.FadSource xmlSource = new AssetSource("data.xml");
        SignConfig.FadSource pdfSource = new AssetSource("pdf.pdf");
        return SignConfig.builder(xmlSource, pdfSource)
                .build();

    }
    private void testAcuantFace() {

        FadConfig.Builder builder = FadConfig.builder()
                .endpoint(StringUtils.encode("https://uat.firmaautografa.com"))
                .requestLocation(false)
                .preventScreenCapture(false)
                .credentials(credentials);

        // ACUANT config
        builder.addConfig(FaceConfig.builder()
                .setType(FaceConfig.ACUANT)
                .setProviderConfiguration(getProvideConfiguration())
                .build());

        FadManager.IntentBuilder intentBuilder = mFadManager.newIntentBuilder()
                .showHeader(true)
                .showSubHeader(false)
                .config(builder.build());

        startActivityForResult(intentBuilder.build(this), FAD_SDK_REQUEST_CODE);
    }


    private OtherDocsConfig getOtherDocConfig() {
        Document document = new Document("Comprobante");
        document.setName("Comprobante");
        Document document1 = new Document("rfc");
        document1.setName("RFC");
        document1.setDescription("Registro Federal de Contribuyentes");
        Document document2 = new Document("curp");
        document2.setName("CURP");
        OtherDocsConfig.Builder builder = OtherDocsConfig.builder()
                .addDocument(document)
                .addDocument(document1)
                .addDocument(document2)
                .setOptionalMode(true)
                .setLimitDocuments(1);
        return builder.build();
    }

    private AppointmentConfig getAppointmentConfig() {
        return AppointmentConfig
                .builder()
                .contactEmail("amartinez@na-at.com.mx")
                .contactFullName("Alexis Martinez")
                .contactNumber("5522977855")
                .build();
    }


    private ProviderConfiguration getProvideConfiguration() {
        ProviderConfiguration providerConfiguration = new ProviderConfiguration();
        //Aquant
        providerConfiguration.setAcUserName("Acuant_Admin@BdC.com");
        providerConfiguration.setAcPassword("J6Jqt2XbQ6^)GefD");
        providerConfiguration.setAcSubscriptionId("ce8066aa-1196-4071-a4c3-ededff1c3f17");
        providerConfiguration.setAcFrmEndpoint("https://frm.acuant.net");
        providerConfiguration.setAcAssureIdEndpoint("https://services.assureid.net");
        providerConfiguration.setAcMediscanEndpoint("https://medicscan.acuant.net");
        providerConfiguration.setAcPassiveLivenessEndpoint("https://us.passlive.acuant.net");
        providerConfiguration.setAcAcasEndpoint("https://acas.acuant.net");
        providerConfiguration.setAcOzoneEndpoint("https://ozone.acuant.net");
        //ReadId
        providerConfiguration.setRiBaseUrl("https://saas-preprod.readid.com:443/odata/v1/ODataServlet/");
        providerConfiguration.setRiAccessKey("096ecf43-3424-4dd1-91f7-419cdb34ebe1");

        return providerConfiguration;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FAD_SDK_REQUEST_CODE) {
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
