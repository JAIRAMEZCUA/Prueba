#  Android SDK v1
**November 2020**

Guia para la implementación de dependencias de FAD
----------

## Licencia ##
Este software es de NA-AT technologies

----------

## Introducción ##

Este documento provee la información detallada sobre las dependencias necesarias para el funcionamiento de los modulos de FAD DE ANDROID SDK.


----------


## Módulos ##

El SDK incluye los siguientes modulos:

**Módulo ID :**

- Módulo para capturas de Identificaciones (INE/IFE).

**Módulo Captura de otros documentos :**

- Módulo para captura de otros documentos .

**Módulo Selfie :**

- Realiza la toma del rostro de una persona mediante alguno de los métodos de captura: tiempo o prueba de vida (4 gestos permitidos).

**Módulo Captura de huellas :**

- Realiza la captura de huellas (dedos índices) para validación ante el Instituto Nacional Electoral, el módulo hace uso de una interfaz que permite capturar las huellas  con dispositivos físicos y huella por fotografía..

**Módulo Enrolamiento :**

- Realiza la captura de ausencias (impedimentos) y/o huellas necesarias para enrolamiento con un motor biométrico en el back end, el módulo  hace uso de una interfaz que permite la captura con dispositivos físicos.

**Módulo Firma:**

- Se utiliza para capturar la firma digital del usuario mediante autenticación del mismo y prueba de vida.


**Módulo Manager:**

- Se utiliza para administrar a los otros módulos dentro del SDK FAD Biometría Android.



### Setup General ###

**La versión actual de las dependencias es  es la [12.0.0], aunque esta puede cambiar.**

**Nota:** Para tener la versión actualizada de las dependencias de FAD, solicitar dicha versión al área de desarrollo por medio de un correo electrónico a las siguientes cuentas:  [amartinez@na-at.com.mx] y [avillanueva@na-at.com.mx]

        $version = "12.0.0"

**Importante: Se recomienda que la app este registrada en la plataforma de Firebase  por lo cual necesitamos agregar a la App el archivo de  “google-services.json”.**

**Se coloca los repositorios necesarios para la descarga de las implementaciones de dependencias para el SDK.**

**Nota** Algunos módulos necesitarán agregar nuevas dependencias en el **Build.gradle**.

        // Top-level build file where you can add configuration options common to all sub-projects/modules.

        buildscript {

            repositories {
                google()
                jcenter()
                mavenCentral()
            }
            dependencies {
                classpath 'com.android.tools.build:gradle:3.5.0'
                // NOTE: Do not place your application dependencies here; they belong
                // in the individual na_at build.gradle files
            }
        }

        allprojects {

            repositories {
                google()
                jcenter()
                maven {
                    //Necesario para  trabajar con una edición de SQLite.
                    url "https://s3.amazonaws.com/repo.commonsware.com"
                }
                maven {
                     credentials {
                       username "USER"
                       password "PASSWORD"
                     }
                    //Necesaria para descargar los artefactos de las dependencias del SDK.
                    url 'https://repository.firmaautografa.com/artifactory/libs-release-local'
                }
            }
        }

        task clean(type: Delete) {
            delete rootProject.buildDir
        }

**Nota:** Todos los modulos necesitan implementar los modulos de **Commons,Data y Manager**, por lo cual agregamos sus  dependencias en el **build.gradle**:

   **Módulo Manager:**

      dependencies {
            implementation(group: 'com.na_at.sdk', name: 'manager', version:  $version, ext: 'aar'){
                transitive=true
            }
      }

   **Módulo Data:**

      dependencies {
            implementation(group: 'com.na_at.sdk', name: 'data', version:  $version, ext: 'aar'){
                transitive=true
            }
      }
   **Módulo Commons:**

      dependencies {
             implementation(group: 'com.na_at.sdk', name: 'commons', version:  $version, ext: 'aar'){
                 transitive=true
             }
      }

## Credenciales para el uso de los módulos ##

**Existen dos maneras de auntentificación para hacer uso de los módulos del SDK son :**

**1.    Usando client, secret, username y password**

**2.    Usando token**


Si la app no tiene su propio login se usa la opción 1 y para ello se requiere pedir las credenciales (client, secret, username, password) a la gerencia de desarrollo FAD avillanueva@na-at.com.mx  , caso contrario la opción 2

La primer autenticación es con client, secret, username y password, los accesos se deben solicitar a la gerencia de fad al correo ‘avillanueva@na-at.com.mx’

Cuando se inicializa el manager por primera vez se tiene que autenticar con el servidor para ver que la aplicación está autorizada para su uso,

     //objeto de credenciales (oAuth2)
        FadCredentials credentials = FadCredentials.builder()
                .client(BuildConfig.CLIENT)
                .secret(BuildConfig.SECRET)
                .username(BuildConfig.USERNAME)
                .password(BuildConfig.PASSWORD)
                .build();

La segunda autenticación es por medio de Token, la aplicación host es la que se autentica

        //credentials
        FadCredentials credentials = FadCredentials.builder()
               .tokenType("bearer")
               .accessToken("XXXXXX")
               .accessToken("fakeToken")
               .build();

### Setup para el módulo de Face ###

**Nota:** Al momento de la ejecución de este módulo se debe aceptar el permiso de **Cámara**.

**En el Manifest de Android configuramos las etiquetas de Metadatos.**

    <application>
        <meta-data android:name="com.google.firebase.ml.vision.DEPENDENCIES"
            android:value="face" />
    </application>

Agregamos la dependencia en **build.gradle**:

        dependencies {
             implementation(group: 'com.na_at.sdk', name: 'face', version:  $version, ext: 'aar'){
                  transitive=true
             }
        }

Mostraremos el fragmento de configuración para el modo dinamico:

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

Mostraremos el fragmento de configuración para el modo de tiempo:

    private FaceConfig faceConfig() {
        return FaceConfig.builder()
                .mode(FaceConfig.MODE_TIME) //configurando el modulo de face en modo de tiempo
                .captureTime(5) //el tiempo de captura de rostro es de 5 segundos
                .onlyFrontCamera(true)//camara frontal activada
                .onlyRearCamera(false)
                .build();
    }


### Setup para el módulo de Face-Zoom ###

**Nota para ocupar este modulo se necesita tener implementado la dependencia de Face.**

 Agregamos la dependencia en **build.gradle**:

      dependencies {
            //zoom
           implementation(group: 'com.facetec.zoom', name: 'zoom-authentication', version: '8.2.0', ext: 'aar')
           implementation(group: 'com.na_at.sdk', name: 'face-zoom', version:  $version, ext: 'aar')
      }

Mostraremos el fragmento de código para el modulo de face-zoom.

    private FaceConfig getFaceZoomConfig(){
        return FaceConfig.builder()
                .setType(FaceConfig.ZOOM) //declarando que el modulo de face ocupara el Zoom.
                .addProperty(FaceConfig.ZOOM_API_KEY, API_KEY_ZOOM) //La clave necesita ser solicitada desde [https://www.facetec.com/]
                .setSimilarityPercent(%dereconocimiento) //Se compara con el modulo de identity previamente ejecutado para comparar rostros.
                .build();
    }
### Setup para el módulo de Face-Acuant ###

**Se necesitan agregar las siguientes dependencias en el Build.gradle**

    allprojects {
        repositories {
            google()
            jcenter()

            //Face Capture and Barcode reading. Only add if using acuantcamera or acuanthgliveness
            maven { url 'https://maven.google.com' }
            maven { url 'https://dl.bintray.com/acuant/Acuant' }
            maven { url 'https://raw.githubusercontent.com/iProov/android/master/maven/' }
        }
    }

**Nota para ocupar este modulo se necesita tener implementado la dependencia de Face.**

 Agregamos la dependencia en **build.gradle**:

      dependencies {
          implementation(group: 'com.na_at.sdk', name: 'face-acuant', version: $version, ext: 'aar'){
                transitive=true
          }
      }

Mostraremos el fragmento de código para el modulo de face-Acuant.

Construimos el objeto de Face-Acuant, asignando las credenciales y su configuración del módulo

    private void testAcuantFace() {

        FadConfig.Builder builder = FadConfig.builder()
                .endpoint(StringUtils.encode(BUILD.ENDPOINT))
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

Asignamos la configuración para uso del módulo de face-acuant

    private ProviderConfiguration getProvideConfiguration() {
            ProviderConfiguration providerConfiguration = new ProviderConfiguration();
            //Aquant
                providerConfiguration.setAcUserName($user);
                providerConfiguration.setAcPassword($password);
                providerConfiguration.setAcSubscriptionId($suscripcionID);
                providerConfiguration.setAcFrmEndpoint($FRMENDPOINT);
                providerConfiguration.setAcAssureIdEndpoint($ASSUREENDPOINT);
                providerConfiguration.setAcMediscanEndpoint($MediscanEndpoin);
                providerConfiguration.setAcPassiveLivenessEndpoint($LivenessEndpoint);
                providerConfiguration.setAcAcasEndpoint($AcasEndpoint);
                providerConfiguration.setAcOzoneEndpoint($OzoneEndpoint);

        return providerConfiguration;
    }

### Setup para el módulo de Identity ###

**Nota:** Al momento de la ejecución de este módulo se debe aceptar los permisos de **Internet** y **Cámara**.

**En el Manifest configuramos las etiquetas de Metadatos.**

    <application>
            <meta-data
                android:name="com.google.android.gms.vision.DEPENDENCIES"
                android:value="ocr, barcode"
                tools:replace="android:value"/>

            <meta-data
                android:name="com.google.firebase.ml.vision.DEPENDENCIES"
                android:value="ocr, barcode"
                tools:replace="android:value"/>

    </application>




**Se necesitan agregar las siguientes dependencias en el Build.gradle**

    allprojects {
        repositories {
            google()
            jcenter()

            maven {
                url "https://identy.jfrog.io/identy/gradle-release-local"
                credentials {
                    username = "USER"
                    password = "PASSWORD"
                }
            }
        }
    }

**Nota : ocupamos las dependencias de OCR y openCV.**

Agregamos la dependencia en **build.gradle**:

     dependencies {
         //identity
         implementation(group: 'com.na_at.sdk', name: 'identity', version:  $version, ext: 'aar'){
             transitive=true
         }

        //ocr
       implementation(group: 'com.naat', name: 'ocr.sdk', version: $ocr_version, ext: 'aar')

        //opencv
        implementation (group: 'com.na_at.opencv', name: 'openCVLibrary412', version: $openCV_version, ext: 'aar')

     }


Mostraremos el fragmento de código para el modulo de identity

    private IdentityConfig identityConfig(){
    //Asignamos la configuración por default del identity que es para reconocer la INE o identificación del usuario.
        return DefaultIdentityConfig.build();
    }

### Setup para el módulo de Identity-tensorflow ###

**Requisitos :**

1.  Para ocupar este modulo se necesita tener implementado la dependencia de Identity.
2.  Necesita estar registrada la App en nuestro Backend para que pueda hacer uso de este modulo.
3.  Para el módulo identity-tf es obligatorio tener el xml y el txt como entradas: las fuentes de entrada pueden ser un AsseetSource, FileSource y URLSource.


**Nota : Implementamos el modulo de camera Widget**

Agregamos la dependencia en **build.gradle**:

       dependencies {
            //identity-tf
            implementation(group: 'com.na_at.sdk', name: 'identity-tf', version: $version,  ext: 'aar'){
                transitive(true)
            }

            // tensorflow
            implementation('org.tensorflow:tensorflow-lite:0.0.0-nightly') { changing = true }

             //camera widget
             implementation(group: 'com.naat', name: 'camerawidget', version: $version_widget, ext: 'aar')

       }


Mostraremos el fragmento de código para el modulo de Identity-tensor flow.

     private void testIdentity() {

            FadConfig.Builder builder = FadConfig.builder()
                    .endpoint(StringUtils.encode(BuildConfig.ENDPOINT))
                    .requestLocation(false) //deshabilitamos el GPS
                    .preventScreenCapture(false) //Permitimos capturas de pantalla
                    .credentials(credentials); //asignamos las credenciales proporcionamos por NAAT

            // default identity config
            builder.addConfig(DefaultIdentityConfig.build());

            //Las siguientes lineas serán para el reconocimiento de la INE será por medio de tensor-flow
            ImageProcessorFactory.getInstance().register(ImageProcessor.CAPTURE_INE_FRONT, INEProcessorTF.class);
            ImageProcessorFactory.getInstance().register(ImageProcessor.CAPTURE_INE_BACK, INEProcessorTF.class);

            FadManager.IntentBuilder intentBuilder = fadManager.newIntentBuilder()
                    .showHeader(boolean)
                    .showSubHeader(bolean)
                    .config(builder.build());

            startActivityForResult(intentBuilder.build(this), FAD_SDK_REQUEST);
     }


### Setup para el módulo de Identity-Acuant ###

**Se necesitan agregar las siguientes dependencias en el Build.gradle**

    allprojects {
        repositories {
            google()
            jcenter()

            //Face Capture and Barcode reading. Only add if using acuantcamera or acuanthgliveness
            maven { url 'https://maven.google.com' }
            maven { url 'https://dl.bintray.com/acuant/Acuant' }
            maven { url 'https://raw.githubusercontent.com/iProov/android/master/maven/' }
        }
    }

Agregamos la dependencia en **build.gradle**:

       dependencies {
             //identity-acuant
             implementation(group: 'com.na_at.sdk', name: 'identity-acuant', version: $version , ext: 'aar'){
                transitive=true
             }
       }


Mostraremos el fragmento de código para el modulo de Identity-Aqua

    private IdentityConfig getAcuantIdentityConfig() {
            Option dynamicOption = Option.builder()
                    .setLabel()
                    .withDocuments()
                    .build();

            // default identity condition
            Condition mainCondition = Condition.builder()
                    .setStatement()
                    .setIcon()
                    .withOption()
                    .build();

            return IdentityConfig.builder()
                    .setMainCondition(mainCondition)
                    .setShowIsValidity(bolean)
                    .setShowSecurityFeatures(bolean)
                    .setValidityINE(bolean)
                    .setShowDialogConfirm(bolean)
                    .setOcrProvider(IdentityConfig.OCR_PROVIDER_ACUANT)//declaramos que el OCR sea vía ACUANT.
                    .build();
    }


### Setup para el módulo de Identity-read-id ###

**Se necesitan agregar las siguientes dependencias en el Build.gradle**

    allprojects {
        repositories {
            google()
            jcenter()
            maven {
                url "s3://maven.readid.com"
                credentials(AwsCredentials) {
                    accessKey "$KEY"
                    secretKey "$SECRET"
                }
            }
        }
    }


### Setup para el módulo de Resume ###

**Nota:** Al momento de la ejecución de este módulo se debe aceptar el permiso de Localización .

Agregamos la dependencia en **build.gradle**:

 **Módulo Resume :**

         dependencies {
             implementation(group: 'com.na_at.sdk', name: 'resume', version:  $version, ext: 'aar'){
                transitive=true
             }
         }


Mostraremos el fragmento de código para el modulo de Resume.

    private ResumeConfig getResumeConfig() {
        return ResumeConfig.builder()
                .showResult(boolean) //configuración para cargar la información y enviarla al servidor
                .setShowInfo(boolean) //muestra la información obtenida.
                .setOverwrite(boolean) //Sobre escritura de datos
                .setFaceValueCompare(%dereconocimiento)  --> El resume compara los rostros entre el modulo de identity y face.
                .build();
    }


### Setup para el módulo de Fingerprints ###

**Nota : Este módulo no es compatible con el módulo de captura de ID con tensorflow.**

Agregamos la dependencia en **build.gradle**:

 **Módulo Fingerprints:**

         dependencies {
               //finger
               implementation(group: 'com.na_at.sdk', name: 'fingerprints', version:  $version , ext: 'aar'){
                     transitive=true
               }

               // karalundi sdk **Se necesita hacer el POM**
               implementation(group: 'com.identy.core-native', name: 'core-native', version: '2.9.2.6', ext: 'aar')
               api ('com.google.android.gms:play-services-safetynet:16.0.0')
         }


Mostraremos el fragmento de código para el modulo de Fingerprints.

       private FingerprintIDConfig getFingerprintIDConfig() {
            FingerprintIDConfig.Builder builder = FingerprintIDConfig.builder()
                    .setTypeScanner(FingerprintIDConfig.SCANNER_TYPE_KARALUNDI) //tipo de scanner para la aextracción de la huella.
                    .setMaxNfiqValid(maxNfiq) //validaciones máximas por mano
                    .setMaxCaptureAttempts(maxAttempts) //intentos máximos
                    .setOptionOptic(boolean) //lector optico
                    .setOptionCamera(bolean) //lector mediante camara
                    //Dedos a tomar en cuenta
                    .setFingerOptions(new Finger[]{Finger.LEFT_INDEX, Finger.LEFT_MIDDLE, Finger.LEFT_RING, Finger.LEFT_LITTLE, Finger.RIGHT_INDEX, Finger.RIGHT_MIDDLE, Finger.RIGHT_RING, Finger.RIGHT_LITTLE})
                    .addProp("API_KEY", $APIKEY)
                    .addProp("LICENSE", $LICENSE)
                    .setCloseOnError(bolean);
            return builder.build();
       }



### Setup para el módulo de Enrolamiento ###

Agregamos la dependencia en **build.gradle**:

  **Módulo Enrolamiento :**

        dependencies {
           implementation(group: 'com.na_at.sdk', name: 'enroll', version:  $version , ext: 'aar'){
               transitive = true
           }
        }


Mostraremos el fragmento de código para el modulo de Enrolamiento.

    private EnrollConfig enrollConfig(){
      return EnrollConfig.builder()
                .scannerType(EnrollConfig.SCANNER_TYPE_WATSON) //configuración para el tipo de escáner
                .minFingerCapture(min) //configuración del número mínimo de huellas a capturar
                .maxCaptureAttempts(maxAttempts) //intentos máximos de captura
                .maxValidNfiq(validaciones_max) //configuración del número máximo de validaciones a ejecutar.
                .build();

    }

### Setup para el módulo de Firma  ###

**Nota Para el módulo firma es obligatorio tener el xml y el pdf como entradas: las fuentes de entrada pueden ser un AsseetSource, FileSource y URLSource:**

    SignConfig.FadSource xmlSource = new AssetSource(binding.textXmlAssetName.getText().toString());
    SignConfig.FadSource pdfSource = new AssetSource(binding.textPdfAssetName.getText().toString());

    new FileSource(“url de archivo”);
    new UriSource(“uri del archivo”);

**Para hacer uso de  este módulo necesita estar registrado en el Backend de FAD**

Necesitamos implementar la implementación de la libreria OPENCV.

Agregamos la dependencia en **build.gradle**:

   **Módulo Firma:**

        dependencies {
                implementation(group: 'com.na_at.sdk.embedded', name: 'sign', version: $version, ext: 'aar'){
                    transitive = true
                }
                //opencv
                implementation (group: 'com.na_at.opencv', name: 'openCVLibrary412', version: $openCV_Version, ext: 'aar')
        }

Mostraremos el fragmento de código para el modulo de Firma.

    private SignConfig getSignConfig() {
        SignConfig.FadSource xmlSource = new AssetSource("data.xml");
        SignConfig.FadSource pdfSource = new AssetSource("pdf.pdf");
        return SignConfig.builder(xmlSource, pdfSource)
                // strings
                .setEndpoint(binding.textEndpoint.getText().toString())
                .setPrivacyText(binding.textPrivacyText.getText().toString()) //configuración para mostrar el aviso de privacidad
                // integers
                .setTimeoutRequest(timeout) //tiempo de espera máximo antes de la respuesta del servidor
                .setAvailableFreeSpace(freeSpace) //espacio disponible libre en dispositivo para que pueda operar
                .setAvailableFreeRam(freeRam) //espacio disponible en memoria RAM para que pueda operar
                // booleans
                .setEnableOAuth(binding.checkOauthEnable.isChecked()) //en caso de estar habilitada la autorización al back end este requerirá la configuración de este parámetro.
                .setSoCracked(binding.checkSoCracked.isChecked())
                .setVideoConfirmationMandatory(binding.checkVideoConfirmation.isChecked())
                .setEmailsEnabled(binding.checkEmailsEnable.isChecked())
                .setLastGetKeys(binding.checkLastGetKeys.isChecked())
                .setShowPrivacyView(binding.checkShowPrivacyView.isChecked()) //configuración para mostrar u ocultar el aviso de privacidad
                .setOverwrite(binding.checkOverwrite.isChecked())
                .build();

    }


### Setup para el módulo de Videoconference ###

**Nota:** Al momento de la ejecución debemos Aceptar los permisos de **Alarma** y  **Comprobación de Internet**.


**Se necesitan agregar las siguientes dependencias en el Build.gradle**

    allprojects {
        repositories {
            google()
            jcenter()
            maven { url 'http://raw.github.com/saki4510t/libcommon/master/repository/' }
            maven { url "https://github.com/jitsi/jitsi-maven-repository/raw/master/releases"}
            maven { url "https://jitpack.io" }
        }
    }

Agregamos la dependencia en **build.gradle**:

  **Módulo Videoconference:**

          dependencies {
                 implementation(group: 'com.na_at.sdk', name: 'videoconference', version: $version, ext: 'aar'){
                     transitive = true
                 }
          }


Mostraremos el fragmento de código para el modulo de Videoconference.

    private VideoConferenceConfig getVideoConferenceConfig() {
        return VideoConferenceConfig.builder()
                .contactFullName(USERNAME)
                .setName(NAME)
                .setLastName(LASTNAME)
                .setSecondName(SECONDNAME)
                .contactNumber(NUMBER)
                .contactEmail(EMAIL)
                .setVideoconferenceId("")
                .setScriptId(SCRIPT_ID)
                .build();
    }

### Setup para el módulo de  captura de otros documentos ###

**Requisitos , necesitamos tener implementado el modulo de CameraWidget y OpenCV.**

Agregamos la dependencia en **build.gradle**:

   **Módulo Document:**

          dependencies {
                 // OtherDocs
                 implementation(group: 'com.na_at.sdk', name: 'other_docs', version: $version , ext: 'aar')
                     transitive = true
                 }

                 implementation(group: 'com.naat', name: 'camerawidget', version: $version_camera, ext: 'aar')
                 implementation (group: 'com.na_at.opencv', name: 'openCVLibrary412', version: $version_openCV, ext: 'aar')

          }


Mostraremos el fragmento de código para el modulo de captura de otros documentos.

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



### Setup para el módulo de Appointments ###

**Nota:** Al momento de la ejecución debemos Aceptar los permisos de **Alarma** y **Comprobación de Internet**.

**Se necesitan agregar las siguientes dependencias en el Build.gradle**

    allprojects {
        repositories {
            google()
            jcenter()
            maven { url 'http://raw.github.com/saki4510t/libcommon/master/repository/' }
            maven { url "https://github.com/jitsi/jitsi-maven-repository/raw/master/releases"}
            maven { url "https://jitpack.io" }
        }
    }


Agregamos la dependencia en **build.gradle**:

     **Módulo Appointments:**

            dependencies {
                //appointment
                implementation(group: 'com.na_at.sdk', name: 'appointments', version:  $version , ext: 'aar'){
                    transitive=true
                }

                 //Calendar
                 implementation 'com.github.prolificinteractive:material-calendarview:2.0.1'

                 // (other dependencies)
                 implementation("org.jitsi.react:jitsi-meet-sdk:${rootProject.ext.jitsi}") {
                        transitive = true
                 }
            }


Mostraremos el fragmento de código para el modulo de Appointment.

    private AppointmentConfig getAppointmentConfig() {
        return AppointmentConfig
                .builder()
                .contactEmail(EMAIL)
                .contactFullName(FULL_NAME)
                .contactNumber(NUMBER)
                .build();
    }


**Nota** Es necesario declarar  la parte de transitive = true , debido a que los artefactos contienen dependencias embebidas.


**NA-AT technologies**  **Campeche, Segundo Piso 300, Hipódromo Condesa, Cuauhtémoc, 06100 Cuauhtemoc, CDMX**

----------------------------------------------------

