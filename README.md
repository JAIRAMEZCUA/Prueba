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




### Setup ###
 Agregamos la dependencia en **build.gradle**:

**Módulo Manager**:
    	dependencies {
			//if possible, use v7:28.0.0 for android support version
			//implementation 'com.android.support:appcompat-v7:28'
			//implementation 'com.android.support:support-v4:28.0.0'
			//implementation 'com.android.support:appcompat-v7:28.0.0'
			//implementation 'com.android.support:exifinterface:28.0.0'

			//Face Capture and Barcode reading. Only add if using acuantcamera or acuanthgliveness
			implementation 'com.google.android.gms:play-services-vision:17.0.2'

			//External library for MRZ reading. Only add if using the MRZ part of acuantcamera
			implementation 'com.rmtheis:tess-two:9.0.0'

			//external libraries for echip reading. Only add if using acuantechipreader
			implementation group: 'com.github.mhshams', name: 'jnbis', version: '1.0.4'
			implementation('org.jmrtd:jmrtd:0.7.11') {
				transitive = true;
			}
			implementation('org.ejbca.cvc:cert-cvc:1.4.6') {
				transitive = true;
			}
			implementation('org.bouncycastle:bcprov-jdk15on:1.61') {
				transitive = true;
			}
			implementation('net.sf.scuba:scuba-sc-android:0.0.18') {
				transitive = true;
			}
			//end echip reading

			//internal common library
			implementation project(path: ':acuantcommon')

			//camera with autocapture - Uses camera 2 API
			implementation project(path: ':acuantcamera')

			//document parse, classification, authentication
			implementation project(path: ':acuantdocumentprocessing')

			//face match library
			implementation project(path: ':acuantfacematchsdk')

			//for reading epassport chips
			implementation project(path: ':acuantechipreader')

			//face capture and liveliness
			implementation project(path: ':acuantipliveness')
			implementation('com.iproov.sdk:iproov:5.2.1@aar') {
				transitive = true
			}

			//face capture and liveliness
			implementation project(path: ':acuanthgliveness')

			//image processing (cropping, glare, sharpness)
			implementation project(path: ':acuantimagepreparation')

			//face capture
			implementation project(path: ':acuantfacecapture')

			//passive liveness
			implementation project(path: ':acuantpassiveliveness')
  		}


**Note** Es necesario declarar  la parte de transitive = true , debido a que los artefactos contienen dependencias embebidas.


**NA-AT technologies**  **Campeche, Segundo Piso 300, Hipódromo Condesa, Cuauhtémoc, 06100 Cuauhtemoc, CDMX**

----------------------------------------------------

