package com.example.myprueba.tf.processor;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.example.myprueba.tf.util.UtilsOCR;
import com.na_at.sdk.commons.model.identity.OCRConstants;
import com.na_at.sdk.commons.model.identity.Percentage;
import com.naat.camerawidget.ProcessorResult;

import java.util.Arrays;

public class INEProcessorTF extends TensorFlowProcessor {

    private static final String TAG = INEProcessorTF.class.getSimpleName();
    private static String ELECTOR_KEY_PATTERN = "^[A-Z]{6}[0-9]{8}[A-Z]{1}[0-9]{3}$";

    private static final String[] MEXICO_VALUES = {
            "MXICO", "MEXICO", "MÉXICO", "IXICO", "MXIC", "MEXIC", "xicO"
    };
    private static final String[] LOCALIDAD_VALUES = {
            "LOCALIDAD", "LOCAL", "LOC", "OCA"
    };
    private static final int TYPE_A = 1;
    private static final int TYPE_B = 2;
    private static final int TYPE_G = 3;

    Percentage pFront = new Percentage(0.02f, 0.25f, 0.28f, 0.50f);
    /***
     * INE crops
     */
    Percentage pNameINE = new Percentage(0.3f, 0.25f, 0.3f, 0.3f);
    Percentage pClaveINE = new Percentage(0.488f, 0.648f, 0.37f, 0.092f);
    Percentage pTitleINE = new Percentage(0.200000003f, 0.0500000007f, 0.209999993f, 0.150000006f);
    Percentage pOcrINE = new Percentage(0.545f, 0.64f, 0.408f, 0.13f);
    Percentage pCicINE = new Percentage(0.05f, 0.65f, 0.47f, 0.11f);
    Percentage pFotoINE = new Percentage(0.060f, 0.25f, 0.29f, 0.70f);

    Percentage pLocalidad = new Percentage(0.3f, 0.85f, 0.2f, 0.1f);
    Percentage pFullName = new Percentage(0.30f, 0.25f, 0.30f, 0.30f);
    Percentage pAddress = new Percentage(0.29f, 0.46f, 0.47f, 0.32f);
    Percentage pCurp = new Percentage(0.373f, 0.710f, 0.30f, 0.09f);
    Percentage pYear = new Percentage(0.86f, 0.71f, 0.077f, 0.10f);
    Percentage pAnioEmision = new Percentage(0.86f, 0.71f, 0.077f, 0.10f);
    Percentage pNumEmision = new Percentage(0.86f, 0.71f, 0.077f, 0.10f);

    /***
     * IFE crops
     */
    Percentage pNameIFE = new Percentage(0.02f, 0.25f, 0.45f, 0.30f);
    Percentage pClaveIFE = new Percentage(0.22f, 0.69f, 0.37f, 0.092f);
    Percentage pOcrIFE = new Percentage(0.13f, 0.02f, 0.60f, 0.07f);
    Percentage pFotoIFE = new Percentage(0.63f, 0.26f, 0.36f, 0.70f);

    private boolean manual;

    public void setManual(boolean manual) {
        this.manual = manual;
    }

    private boolean front = false;
    private int subType;

    @Override
    public ProcessorResult processImageData(Bitmap inputBitmap) {
        // process OpenCV contour detection
        ProcessorResult processorResult = super.processImageData(inputBitmap);
        if (!processorResult.isSuccess()) {
            Log.d(TAG, "Processor fail with message: " + processorResult.getMessage());
            return processorResult;
        }

        Bundle results = processorResult.getResults();
        String title = results.getString("title", "???");
        String ocrAlias;
        if ("ine_front".equals(title)) {
            front = true;
            ocrAlias = OCRConstants.INE_FRONT_OCR_KEY;
            subType = TYPE_A; // TODO how to identify type G
        } else if ("ife_front".equals(title)) {
            front = true;
            ocrAlias = OCRConstants.IFE_FRONT_OCR_KEY;
            subType = TYPE_B;
        } else if ("ine_back".equals(title)) {
            front = false;
            ocrAlias = OCRConstants.INE_BACK_OCR_KEY;
            subType = TYPE_A; // TODO how to identify type G
        } else if ("ife_back".equals(title)) {
            front = false;
            ocrAlias = OCRConstants.IFE_BACK_OCR_KEY;
            subType = TYPE_B;
        } else {
            Log.d(TAG, "Not a valid ID title for this processor: " + title);
            return ProcessorResult.fail("Not a valid ID title for this processor: " + title);
        }

        processorUIHandler.onFocusChangeListener(true);

        float confidence = results.getFloat("confidence");
        if (confidence * 100 < 99) {
            processorUIHandler.onFocusChangeListener(false);
            Log.d(TAG, "Confidence is not enough: " + confidence);
            return ProcessorResult.fail("Confidence is not enough: " + confidence);
        }

        processorUIHandler.onProcessing();
        Bitmap crop = processCrop(processorResult.getImage());
        if (crop == null) {
            processorUIHandler.onFocusChangeListener(false);
            Log.d(TAG, "Crop process failed");
            return ProcessorResult.fail("Crop process failed");
        }

        // validate aspect
//        var lowAspectRatio: CGFloat = 0.625
//        var highAspectRatio: CGFloat = 0.635
        float aspectRatio = (float) crop.getHeight() / crop.getWidth();
        Log.d(TAG, "Aspect ratio: " + aspectRatio);
//        if (aspectRatio < 0.62 || aspectRatio > 0.63) {
        if (aspectRatio < 0.59 || aspectRatio > 0.65) {
//        if (aspectRatio < 0.625 || aspectRatio > 0.635) {
            Log.d(TAG, "Image does not match with aspect ratio: " + aspectRatio);
            processorUIHandler.onFocusChangeListener(false);
            return ProcessorResult.fail("Aspect ratio does not match: " + aspectRatio);
        }

        results.putString("ocrAlias", ocrAlias);
        return ProcessorResult.success(crop, results);
    }

    public Bitmap processCrop(Bitmap crop) {
        // frente ine
        if (front) {
            boolean hasName = false;
            String electorKey = "";

            // texto del lado izquierdo de la credencial
            String text = UtilsOCR.performOCR(context, UtilsOCR.getBitmapCroppedFromBitmap(crop, pFront, false));
            Percentage pPhoto = null;

            // old ocr source
            String fullName[] = {}, address = "", curp = "", year = "", anioEmision, numEmision, anioExpira;

            if (text.length() > 10) {
                // IFE
//                setSubType(TYPE_B);

                // extraemos clave de elector
                electorKey = UtilsOCR.performOCR(context, UtilsOCR.getBitmapCroppedFromBitmap(crop, pClaveIFE, false));
                electorKey = UtilsOCR.getFilterStringKeyElector(electorKey);
                electorKey = electorKey.replaceAll("\n", "");

                // validamos cadena "NOMBRE"
                hasName = UtilsOCR.hasText((UtilsOCR.performOCR(context, UtilsOCR.getBitmapCroppedFromBitmap(crop, pNameIFE, false))), "NOM");

                // extraemos foto
                pPhoto = pFotoIFE;
            } else {
                // validamos referencia a titulo "MÉXICO" en INE
                String title = UtilsOCR.performOCR(context, UtilsOCR.getBitmapCroppedFromBitmap(crop, pTitleINE, false));
                if (hasText(title, MEXICO_VALUES)) {
                    String localidad = UtilsOCR.performOCR(context, UtilsOCR.getBitmapCroppedFromBitmap(crop, pLocalidad, false));
                    if (hasText(localidad, LOCALIDAD_VALUES)) {
                        // INE
//                        setSubType(TYPE_A);

                        // update percentages
                        pClaveINE = new Percentage(0.488f, 0.648f, 0.37f, 0.092f);
                        pFotoINE = new Percentage(0.060f, 0.25f, 0.29f, 0.70f);

                        // additional percent
                        pFullName = new Percentage(0.30f, 0.25f, 0.30f, 0.30f);
                        pAddress = new Percentage(0.29f, 0.46f, 0.47f, 0.32f);
                        pCurp = new Percentage(0.373f, 0.710f, 0.30f, 0.09f);
                        pYear = new Percentage(0.86f, 0.71f, 0.077f, 0.10f);
                    } else {
                        // INE
//                        setSubType(TYPE_G);

                        // update percentages
                        pClaveINE = new Percentage(0.52f, 0.7f, 0.37f, 0.092f);
                        pFotoINE = new Percentage(0.06f, 0.25f, 0.275f, 0.55f);

                        pFullName = new Percentage(0.30f, 0.25f, 0.30f, 0.30f);
                        pAddress = new Percentage(0.29f, 0.46f, 0.47f, 0.32f);
                        pCurp = new Percentage(0.32f, 0.81f, 0.30f, 0.09f);
                        pYear = new Percentage(0.7f, 0.82f, 0.065f, 0.06f);
                        pNumEmision = new Percentage(0.76f, 0.82f, 0.05f, 0.06f);
                        pAnioEmision = new Percentage(0.7f, 0.92f, 0.065f, 0.06f);
                    }

                    // extraemos clave de elector
                    electorKey = UtilsOCR.performOCR(context, UtilsOCR.getBitmapCroppedFromBitmap(crop, pClaveINE, false));
                    electorKey = UtilsOCR.getFilterStringKeyElector(electorKey);
                    electorKey = electorKey.replaceAll("\n", "");

                    // validamos cadena "NOMBRE"
                    hasName = UtilsOCR.hasText((UtilsOCR.performOCR(context, UtilsOCR.getBitmapCroppedFromBitmap(crop, pNameINE, false))), "NOM");

                    pPhoto = pFotoINE;
                }
            }

            boolean result = electorKey.length() > 0 && hasName;
            if (result && manual)
                result = electorKey.matches(ELECTOR_KEY_PATTERN);

            if (result) {
                Log.d(TAG, "OCR preview (electorKey): " + electorKey);
                // FIXME - remove when SDK OCR is functional
                if (getSubType() == TYPE_G) {
                    // perform OCR & set values to model
                    fullName = UtilsOCR.extractNameIFE(UtilsOCR.performOCR(context, UtilsOCR.getBitmapCroppedFromBitmap(crop, pFullName, false)));
                    address = UtilsOCR.extractAddressIFE(UtilsOCR.performOCR(context, UtilsOCR.getBitmapCroppedFromBitmap(crop, pAddress, false)));

                    curp = UtilsOCR.performOCR(context, UtilsOCR.getBitmapCroppedFromBitmap(crop, pCurp, false));
                    curp = UtilsOCR.getFilterStringCURP(curp);
                    curp = curp.replaceAll("\n", "");

                    year = UtilsOCR.performOCR(context, UtilsOCR.getBitmapCroppedFromBitmap(crop, pYear, false));
                    year = UtilsOCR.getFilterStringYear(year);

                    numEmision = UtilsOCR.performOCR(context, UtilsOCR.getBitmapCroppedFromBitmap(crop, pNumEmision, false));
                    numEmision = UtilsOCR.getFilterStringYear(numEmision);

                    anioEmision = UtilsOCR.performOCR(context, UtilsOCR.getBitmapCroppedFromBitmap(crop, pAnioEmision, false));
                    anioEmision = UtilsOCR.getFilterStringYear(anioEmision);
                    anioExpira = anioEmision.length() == 4 ? String.valueOf(Integer.valueOf(anioEmision) + 10) : "";

                    // old ocr data
                    Log.d(TAG, "OCR preview (fullName): " + Arrays.toString(fullName));
                    Log.d(TAG, "OCR preview (address): " + address);
                    Log.d(TAG, "OCR preview (curp): " + curp);
                    Log.d(TAG, "OCR preview (year): " + year);
                    Log.d(TAG, "OCR preview (anioEmision): " + anioEmision);
                    Log.d(TAG, "OCR preview (numEmision): " + numEmision);
                    Log.d(TAG, "OCR preview (anioExpira): " + anioExpira);

//                    // set values to model
//                    identityDocument.getDocumentCaptureList().get(0).setOcrProcessing(false);
//                    identityDocument.getDocumentCaptureList().get(1).setOcrProcessing(false);
//                    identityDocument.setFieldValue(INE.FIELD_OCR_ANIO_EXPIRACION, anioExpira);
//                    identityDocument.setFieldValue(INE.FIELD_OCR_ANIO_EMISION, anioEmision);
//                    identityDocument.setFieldValue(INE.FIELD_OCR_ANIO_REG, year);
//                    identityDocument.setFieldValue(INE.FIELD_OCR_NO_EMISION, numEmision);
//                    identityDocument.setFieldValue(INE.FIELD_OCR_CURP, curp);
//                    identityDocument.setFieldValue(INE.FIELD_OCR_CLAVE, electorKey);
//                    if (fullName.length == 3) {
//                        identityDocument.setFieldValue(INE.FIELD_OCR_APP, fullName[0]);
//                        identityDocument.setFieldValue(INE.FIELD_OCR_APM, fullName[1]);
//                        identityDocument.setFieldValue(INE.FIELD_OCR_NOMBRES, fullName[2]);
//                    } else if (fullName.length == 2) {
//                        identityDocument.setFieldValue(INE.FIELD_OCR_APP, fullName[0]);
//                        identityDocument.setFieldValue(INE.FIELD_OCR_NOMBRES, fullName[1]);
//                    } else if (fullName.length == 1) {
//                        identityDocument.setFieldValue(INE.FIELD_OCR_NOMBRES, fullName[0]);
//                    }
                } else {
//                    identityDocument.getDocumentCaptureList().get(0).setOcrProcessing(true);
//                    identityDocument.getDocumentCaptureList().get(1).setOcrProcessing(true);
                }

                // extract photo bitmap & save
//                savePhoto(UtilsOCR.getBitmapCroppedFromBitmap(crop, pPhoto, false));
                return crop;
            }
            Log.d(TAG, "Clave de elector inválida: " + electorKey);
            return null;
        } else {
            if (getSubType() == TYPE_A || getSubType() == TYPE_G) {
                if (getSubType() == TYPE_G) {
                    pOcrINE = new Percentage(0.53f, 0.68f, 0.408f, 0.13f);
                    pCicINE = new Percentage(0.02f, 0.68f, 0.46f, 0.11f);
                } else {
                    pOcrINE = new Percentage(0.545f, 0.64f, 0.408f, 0.13f);
                    pCicINE = new Percentage(0.05f, 0.65f, 0.47f, 0.11f);
                }

                // INE
                String idINE = UtilsOCR.extractIdIFE(UtilsOCR.performOCR(context, UtilsOCR.getBitmapCroppedFromBitmap(crop, pOcrINE, false)));
                idINE = UtilsOCR.getFilterIdINE(idINE);

                String cic = UtilsOCR.performOCR(context, UtilsOCR.getBitmapCroppedFromBitmap(crop, pCicINE, false));
                cic = UtilsOCR.getFilterCIC(cic);

                boolean result = idINE.length() > 10 && cic.length() > 7;
                if (result) {
                    // validate presence of all MRZ fields from backCapture
//                    showMrzFields(identityDocument);

                    if (getSubType() == TYPE_G) {
                        Percentage pOcrMrzNames = new Percentage(0.01f, 0.86f, 0.95f, 0.11f);
                        String mrzNames = UtilsOCR.performOCR(context, UtilsOCR.getBitmapCroppedFromBitmap(crop, pOcrMrzNames, false));
                        mrzNames = UtilsOCR.ocrCharReplace(mrzNames);
                        mrzNames = mrzNames.replaceAll("\n", "");

                        String mrzApp = "", mrzApm = "", mrzName = "";

                        String[] mrzValues = mrzNames.split("<");
                        if (mrzValues.length >= 1) {
                            mrzApm = mrzValues[0].trim();
                        }
                        if (mrzValues.length >= 2) {
                            mrzApp = mrzValues[0].trim();
                            mrzApm = mrzValues[1].trim();
                        }
                        if (mrzValues.length >= 3) {
                            mrzApp = mrzValues[0].trim();
                            mrzApm = mrzValues[1].trim();
                            mrzName = mrzValues[2].trim();
                            if (mrzName.isEmpty() && mrzValues.length >= 4)
                                mrzName = mrzValues[3].trim();
                        }

//                        identityDocument.setFieldValue(INE.FIELD_MRZ_OCR, idINE);
//                        identityDocument.setFieldValue(INE.FIELD_MRZ_CIC, cic);
//                        identityDocument.setFieldValue(INE.FIELD_MRZ_APP, mrzApp);
//                        identityDocument.setFieldValue(INE.FIELD_MRZ_APM, mrzApm);
//                        identityDocument.setFieldValue(INE.FIELD_MRZ_NOMBRES, mrzName);
                    }

                    return crop;
                }
                return null;
            } else {
//                assert (getSubType() == TYPE_B);

                String idINE = UtilsOCR.extractIdIFE(UtilsOCR.performOCR(context, UtilsOCR.getBitmapCroppedFromBitmap(crop, pOcrIFE, true)));
                idINE = UtilsOCR.extractIdIFE(idINE);

                boolean result = idINE.length() > 10;
                if (result) {
                    // remove all MRZ fields from backCapture
//                    hideMrzFields(identityDocument);

                    return crop;
                }
                return null;
            }
        }
    }

//    private void setSubType(int subType) {
//        DocumentCapture frontCapture = identityDocument.getDocumentCaptureList().get(0);
//
//        CaptureField fieldType = identityDocument.getFields().get(INE.FIELD_SUB_TYPE);
//        if (fieldType != null)
//            fieldType.setValue(String.valueOf(subType));
//
//        CaptureField fieldIsINE = identityDocument.getFields().get(INE.FIELD_IS_INE);
//        if (fieldIsINE != null)
//            fieldIsINE.setValue(subType == TYPE_A ? "true" : "false");
//
//        // update ocr alias
//        DocumentCapture backCapture = identityDocument.getDocumentCaptureList().get(1);
//        if (subType == TYPE_A) {
//            frontCapture.setOcrAlias(OCRConstants.INE_FRONT_OCR_KEY);
//            backCapture.setOcrAlias(OCRConstants.INE_BACK_OCR_KEY);
//        } else {
//            assert (subType == TYPE_B);
//            frontCapture.setOcrAlias(OCRConstants.IFE_FRONT_OCR_KEY);
//            backCapture.setOcrAlias(OCRConstants.IFE_BACK_OCR_KEY);
//        }
//    }

    private int getSubType() {
//        CaptureField fieldType = identityDocument.getFields().get(INE.FIELD_SUB_TYPE);
//        if (fieldType != null)
//            return Integer.valueOf(fieldType.getValue());
//        return -1;
        return subType;
    }

//    private void savePhoto(Bitmap photo) {
//        // create process directory if not exists (TODO move to manager)
//        String processPath = FileManager.createTempFile(context, processId).getAbsolutePath();
//        File folder = new File(processPath);
//        if (!folder.exists()) {
//            folder.mkdirs();
//            Log.d(TAG, "wrote: created folder " + folder.getPath());
//        }
//
//        // create photo directory
//        String photoDirPath = processPath + "/photo/";
//        File dirPhoto = new File(photoDirPath);
//        if (!dirPhoto.exists())
//            dirPhoto.mkdirs();
//
//        // create photo file
//        File photoFile = new File(dirPhoto.getPath(), "foto.png");
//
//        // almacenamos en file system
//        FileManager.bitmapToFile(photo, photoFile);
//
//        // agregamos campo tipo URI
//        identityDocument.setFieldValue(INE.FIELD_PHOTO, photoFile.getAbsolutePath());
//    }

    private boolean hasText(String textString, String[] values) {
        for (String text : values) {
            if (UtilsOCR.hasText(textString, text))
                return true;
        }
        return false;
    }

}
