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

**Módulo Manager :**
    dependencies {
        //Manager
        implementation(group: 'com.na_at.sdk', name: 'manager', version: "0.12.0_alpha_new", ext: 'aar'){
            transitive=true
         }
    }

**Módulo Data :**
    dependencies {
         //Data
        implementation(group: 'com.na_at.sdk', name: 'data', version: "0.12.0_alpha_new", ext: 'aar'){
            transitive=true
        }
   }




**Note** Es necesario declarar  la parte de transitive = true , debido a que los artefactos contienen dependencias embebidas.


**NA-AT technologies**  **Campeche, Segundo Piso 300, Hipódromo Condesa, Cuauhtémoc, 06100 Cuauhtemoc, CDMX**

----------------------------------------------------

