#  Android SDK v1
**November 2020**

Guia para la implementación de dependencias de FAD
----------

## License
Este software es de NA-AT technologies

----------

## Introduction ##

Este documento provee la información detallada sobre las dependencias necesarias para el funcionamiento de los modulos de FAD DE ANDROID SDK.

**Note:** Para tener la versión actualizada de las dependencias de FAD, solicitar dicha versión al área de desarrollo por medio de un correo electrónico a las siguientes cuentas:  [amartinez@na-at.com.mx] y [avillanueva@na-at.com.mx]


----------


## Modules ##

El SDK incluye los siguientes modulos:

**Módulo ID :**

- Se encarga  de la captura de identificaciones siguiendo las especificaciones que marca la Circular Única Bancaria (INE / IFE, Pasaporte, si no se cuenta con alguna de ellas,se deben capturar dos identificaciones adicionales).

**Módulo Documentos :**

- Permite la captura de cualquier documento así como el recorte del mismo para su clasificación y extracción de información (OCR).

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
**Colocamos los repositorios necesarios para la descarga de las implementaciones de dependencias para el SDK**

 allprojects {

    repositories {
        google()
        jcenter()
        maven {
            url "https://s3.amazonaws.com/repo.commonsware.com"
        }
        maven {
            credentials {
                username "$fadUser"
                password "$fadPassword"
            }
            url 'https://repository.firmaautografa.com/artifactory/libs-release-local'
        }

        maven { url "https://jitpack.io" }

        maven {
            url "https://github.com/jitsi/jitsi-maven-repository/raw/master/releases"
        }
        maven { url 'http://raw.github.com/saki4510t/libcommon/master/repository/' }

        maven {
            url "https://identy.jfrog.io/identy/gradle-release-local"
            credentials {
                username = "naat"
                password = "#R3lR0gugPGDcrII3G37C"
            }
        }

        maven {
            url "s3://maven.readid.com"
            credentials(AwsCredentials) {
                accessKey "AKIARPBHJEUUXQPJCDOY"
                secretKey "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"
            }
        }

        //Face Capture and Barcode reading. Only add if using acuantcamera or acuanthgliveness
        maven { url 'https://maven.google.com' }
        maven { url 'https://dl.bintray.com/acuant/Acuant' }
        maven { url 'https://raw.githubusercontent.com/iProov/android/master/maven/' }
    }
    apply plugin: "com.jfrog.artifactory"
    apply plugin: 'maven-publish'
 }

**Nota:** Todos los modulos necesitan implementar los modulos de Commons,Data y Manager, por lo cual agregamos sus  dependencias en el **build.gradle**:

      ** Módulo Manager: **
      dependencies {
            //Manager
            implementation(group: 'com.na_at.sdk', name: 'manager', version: "0.12.0_alpha_new", ext: 'aar'){
                transitive=true
            }
      }

      **Módulo Data:**
      dependencies {
            //Data
            implementation(group: 'com.na_at.sdk', name: 'data', version: "0.12.0_alpha_new", ext: 'aar'){
                    transitive=true
            }
      }
      **Módulo Commons:**
      dependencies {
             //Commons
             implementation(group: 'com.na_at.sdk', name: 'commons', version: "0.12.0_alpha_new", ext: 'aar'){
                       transitive=true
             }
      }

### Setup para el módulo de Face ###
Agregamos la dependencia en **build.gradle**:


           dependencies {
                    //Face
                    implementation(group: 'com.na_at.sdk', name: 'face', version: "0.12.0_alpha_new", ext: 'aar'){
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


### Setup para el módulo de Face###
 Agregamos la dependencia en **build.gradle**:





        **Módulo Commons :**
          dependencies {
                //Commons
                implementation(group: 'com.na_at.sdk', name: 'commons', version: "0.12.0_alpha_new", ext: 'aar'){
                    transitive=true
                }
          }



        **Módulo ID :**
            dependencies {
                //identity
                implementation(group: 'com.na_at.sdk', name: 'identity', version: "0.12.0_alpha_new", ext: 'aar'){
                transitive=true
                }
            }


        **Módulo Enrolamiento :**
            dependencies {
             //enroll
             implementation(group: 'com.na_at.sdk', name: 'enroll', version: '0.12.0_alpha_new', ext: 'aar'){
                 transitive = true
                }
             }
        **Módulo Face-Zoom :**
            dependencies {
                //zoom
                implementation(group: 'com.facetec.zoom', name: 'zoom-authentication', version: '8.2.0', ext: 'aar')
                implementation(group: 'com.na_at.sdk', name: 'face-zoom', version: "0.12.0_alpha_new", ext: 'aar'){
                  transitive = true
                }
            }

         **Módulo Resume :**
         dependencies {
             //resume
             implementation(group: 'com.na_at.sdk', name: 'resume', version: "0.12.0_alpha_new", ext: 'aar'){
                transitive=true
             }
         }
        **Módulo Sign :**
            dependencies {
             //sign
             implementation(group: 'com.na_at.sdk.embedded', name: 'sign', version: '0.12.0_alpha_new', ext: 'aar'){
                 transitive = true
                 }
             }
        **Módulo Appointments:**
            dependencies {
                //appointment
                implementation(group: 'com.na_at.sdk', name: 'appointments', version: '0.12.0_alpha_new', ext: 'aar'){
                    transitive=true
                }
            }
         **Módulo Fingerprints:**
         dependencies {
           //finger
           implementation(group: 'com.na_at.sdk', name: 'fingerprints', version: '0.12.0_alpha_new', ext: 'aar'){
                 transitive=true
           }

           // karalundi sdk **Se necesita hacer el POM**
           //no es compatible con t-f
           implementation(group: 'com.identy.core-native', name: 'core-native', version: '2.9.2.6', ext: 'aar')
           api ('com.google.android.gms:play-services-safetynet:16.0.0')
         }

        **Módulo Videoconference:**
            dependencies {
               // videoconference
              implementation(group: 'com.na_at.sdk', name: 'videoconference', version: '0.12.0_alpha_new', ext: 'aar'){
                    transitive = true
                }
            }

        **Módulo Camara Widget :**
            dependencies {
                // camera widget
                implementation(group: 'com.naat', name: 'camerawidget', version: '3.0.0', ext: 'aar'){
                    transitive=true
                }
             }


**Note** Es necesario declarar  la parte de transitive = true , debido a que los artefactos contienen dependencias embebidas.


**NA-AT technologies**  **Campeche, Segundo Piso 300, Hipódromo Condesa, Cuauhtémoc, 06100 Cuauhtemoc, CDMX**

----------------------------------------------------

