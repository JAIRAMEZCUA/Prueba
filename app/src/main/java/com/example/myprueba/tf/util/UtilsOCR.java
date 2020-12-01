package com.example.myprueba.tf.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
//import com.google.zxing.BinaryBitmap;
//import com.google.zxing.ChecksumException;
//import com.google.zxing.FormatException;
//import com.google.zxing.NotFoundException;
//import com.google.zxing.RGBLuminanceSource;
//import com.google.zxing.Result;
//import com.google.zxing.common.HybridBinarizer;
//import com.google.zxing.pdf417.PDF417Reader;
import com.na_at.sdk.commons.model.identity.CaptureField;
import com.na_at.sdk.commons.model.identity.Percentage;
import com.na_at.sdk.commons.util.FileManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by oscar on 16/10/17.
 */

public class UtilsOCR {



    public static Bitmap getBitmapCroppedFromBitmap(Bitmap bitmap, Percentage p, boolean rotate) {
        Bitmap bitmap2 = null;
        Matrix m = new Matrix();
        m.postRotate(90);
        try {
            if (rotate)
                bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (rotate && bitmap2 != null) {
            bitmap = Bitmap.createBitmap(bitmap2, Math.round((p.getX()) * bitmap2.getWidth()), Math.round((p.getY()) * bitmap2.getHeight()), Math.round(p.getWidth() * bitmap2.getWidth()), Math.round(p.getHeight() * bitmap2.getHeight()));
            return bitmap;
        } else if (bitmap != null) {
            bitmap2 = Bitmap.createBitmap(bitmap, Math.round((p.getX()) * bitmap.getWidth()), Math.round((p.getY()) * bitmap.getHeight()), Math.round(p.getWidth() * bitmap.getWidth()), Math.round(p.getHeight() * bitmap.getHeight()));
            return bitmap2;
        }
        return null;
    }

    public static Bitmap getBitmapCroppedFromUri(String uri, Percentage p, boolean rotate) {
        Bitmap bitmap = null;
        Bitmap bitmap2 = null;
        Matrix m = new Matrix();
        m.postRotate(90);
        try {
            bitmap = BitmapFactory.decodeFile(uri);
            if (rotate)
                bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (rotate && bitmap2 != null) {
            bitmap = Bitmap.createBitmap(bitmap2, Math.round((p.getX()) * bitmap2.getWidth()), Math.round((p.getY()) * bitmap2.getHeight()), Math.round(p.getWidth() * bitmap2.getWidth()), Math.round(p.getHeight() * bitmap2.getHeight()));
            return bitmap;
        } else if (bitmap != null) {
            bitmap2 = Bitmap.createBitmap(bitmap, Math.round((p.getX()) * bitmap.getWidth()), Math.round((p.getY()) * bitmap.getHeight()), Math.round(p.getWidth() * bitmap.getWidth()), Math.round(p.getHeight() * bitmap.getHeight()));
            return bitmap2;
        }
        return null;
    }

    public static String performOCR(Context context, Bitmap bitmap) {
        return performOCR(context, bitmap, "[^a-zA-Z0-9Ññ()</\n ]");
    }

    private static TextRecognizer textRecognizer;

    public static TextRecognizer getTextRecognizer(Context context) {
        if (textRecognizer == null) {
            textRecognizer = new TextRecognizer.Builder(context).build();
        }
        return textRecognizer;
    }

    public static String performOCR(Context context, Bitmap bitmap, String replaceRegex) {
        //getImageUri(activity, bitmap);
        //bitmapToFile(bitmap, Environment.getExternalStorageDirectory().getAbsolutePath()+"/prueba.jpg");
        String textResult = "";
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
//        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        SparseArray<TextBlock> textblock = getTextRecognizer(context).detect(frame);
        TextBlock tb = null;
        List<Text> texto = new ArrayList<>();
        for (int i = 0; i < textblock.size(); i++) {
            tb = textblock.get(textblock.keyAt(i));
            Log.e("TEXT", tb.toString() + "");
            texto.addAll(tb.getComponents());
        }
        for (Text t : texto) {
            for (Text t2 : t.getComponents()) {
                textResult += t2.getValue() + " ";
            }
            textResult += "\n";
        }

        if (!textResult.equals("")) {
            bitmap.recycle();
//            return textResult.replaceAll("[^a-zA-Z0-9Ññ()</\n ]","");
            return textResult.replaceAll(replaceRegex,"");
        } else {
            return "";
        }
    }

    public static String extractIdIFE(String s) {
        String result = s.replaceAll("\\s+", "").replaceAll("[-+.^:,]", "");
        if (!TextUtils.isDigitsOnly(result)) {
            result = result.replace('b', '6').replace('L', '6').replace('l', '1').replace('i', '1').replace('I', '1')
                    .replace('y', '4').replace('o', '0').replace('O', '0').replace('s', '5').replace('S', '5')
                    .replace('z', '2').replace('Z', '2').replace('g', '9').replace('Y', '4').replace('e', '2')
                    .replace('?', '7').replace('E', '6').replace("B","8");
        }
        return result.replaceAll("[^a-zA-Z0-9Ññ()/\n ]","");
    }

    public static String getFilterString(String string){
        String[] text = string.split(" ");
        String result = "";

        for(String t: text){
            if(t.length()>result.length())
                result = t;
        }

        return result.replace(")","0").replace("(","0").toUpperCase();
    }

    public static String getFilterCIC(String string){

        String[] text = string.split(" ");
        String result = "";

        for(String t: text){
            if(t.length()>0)
                result += t;
        }

        return result.replaceAll("[a-zA-Z]","").replace("\n","");
    }

    public static String getFilterIdINE(String string){
        return getFilterCIC(string);
    }

    public static String getFilterStringYear(String string){

        String result  = extractIdIFE(string);

        return result;
    }

    public static String getFilterStringCURP(String string) {
        String result = getFilterString(string);
        if (result.length() >= 18) {
            String p1 = ocrCharReplace(result.substring(0, 4));
            String p2 = ocrDigitsReplace(result.substring(4, 10));
            String p3 = ocrCharReplace(result.substring(10, 16));
            String p4 = ocrDigitsReplace(result.substring(16, 18));
            return p1 + p2 + p3 + p4;
        }
        return "";
    }

    public static String getFilterStringKeyElector(String string) {
        String result = getFilterString(string);
        String p1;
        String p2;
        String p3;

        if (result.length() == 16) {
            result = result + "00";
        } else if (result.length() == 17) {
            result = result + "0";
        }

        if (result.length() >= 12) {
            p1 = ocrCharReplace(result.substring(0, 6));
            p2 = ocrDigitsReplace(result.substring(6, 12));
            p3 = result.substring(12, result.length());
            return p1.concat(p2).concat(p3);
        } else {
            return string;
        }
    }

    public static String ocrCharReplace(String text) {
        return text
                .replaceAll("0", "O")
                .replaceAll("1", "I")
                .replaceAll("2", "Z")
//                .replaceAll("3", "")
                .replaceAll("4", "A")
                .replaceAll("5", "S")
                .replaceAll("6", "C")
                .replaceAll("7", "T")
                .replaceAll("8", "B")
//                .replaceAll("9", "")
                ;
    }

    public static String ocrDigitsReplace(String text) {
        return text
                .replaceAll("A", "4")
                .replaceAll("B", "8")
                .replaceAll("C", "6")
                .replaceAll("D", "0")
//                .replaceAll("E", "")
//                .replaceAll("F", "")
                .replaceAll("G", "6")
                .replaceAll("H", "4")
                .replaceAll("I", "1")
//                .replaceAll("J", "")
//                .replaceAll("K", "")
                .replaceAll("L", "1")
//                .replaceAll("M", "")
//                .replaceAll("N", "")
//                .replaceAll("Ñ", "")
                .replaceAll("O", "0")
//                .replaceAll("P", "")
                .replaceAll("Q", "0")
                .replaceAll("R", "8")
                .replaceAll("S", "5")
                .replaceAll("T", "7")
//                .replaceAll("U", "")
//                .replaceAll("V", "")
//                .replaceAll("W", "")
//                .replaceAll("X", "")
                .replaceAll("Y", "1")
                .replaceAll("Z", "2")
                ;
    }

    private static final String[] NOMBRE_TAGS = {
      "NOMBRE", "NOMB", "NOMBR", "NOMERE", "NOMER", "OMERE"
    };

    public static String[] extractNameIFE(String s) {
        String[] lines = s.split("\r\n|\r|\n");
        String[] fullName = new String[3];
        if (hasText(lines[0], NOMBRE_TAGS) && lines.length > 3) {
            for (int i = 1; i <= 3; i++) {
                fullName[i - 1] = lines[i].trim();
            }
        } else if (lines.length >= 3) {
            for (int i = 0; i <= 2; i++) {
                fullName[i] = lines[i].trim();
            }
        } else {
            for (int i = 1; i < lines.length; i++) {
                fullName[i] = lines[i].trim();
            }
        }

        return fullName;
    }

    public static boolean hasText(String textString, String[] values) {
        for (String text : values) {
            if (UtilsOCR.hasText(textString, text))
                return true;
        }
        return false;
    }

    public static boolean hasText(String source, String text) {
        String[] lines = source.split("\r\n|\r|\n");
        for(String line: lines)
            if (line.contains(text)) return true;
        return false;
    }

    public static String[] extractMRZ(String s) {
        s = s.replaceAll("[\r|\n]+","").replaceAll("[k|K]{2,}","<").replaceAll("[k|K]<","<").replace(" ","").replace("k","<").replace(" K<","<");
        String[] lines = s.split("[<]+");
        String[] mrz  = {"","",""};

        if(lines.length == 3){

            mrz = new String[3];
            mrz[0] = lines[0].replace("\n","");
            mrz[1] = lines[1].replace("\n","");
            mrz[2] = lines[2].replace("\n","");

        }else if(lines.length >= 4){
            mrz = new String[3];
            mrz[0] = lines[0].replace("\n","");
            mrz[1] = lines[1].replace("\n","");
            mrz[2] = lines[2].replace("\n","") +" "+lines[3].replace("\n","");
        }


        return mrz;
    }

    public static String[] extractMRZPassport(String s) {
        s = s.replaceAll("[\r|\n]+","").replace(" ","").replace("k","<").replace(" K<","<").replace("K<","<");
        if(s.length()>5) {
            s = s.substring(5, s.length());
        }
        String[] lines = s.split("[<]+");
        String[] mrz = new String[3];

        if(lines.length == 3){

            mrz[0] = lines[0].replace("\n","");
            mrz[1] = lines[1].replace("\n","");
            mrz[2] = lines[2].replace("\n","");

        }else if(lines.length == 4){
            mrz[0] = lines[0].replace("\n","");
            mrz[1] = lines[1].replace("\n","");
            mrz[2] = lines[2].replace("\n","") +" "+lines[3].replace("\n","");
        }else{
            return null;
        }


        return mrz;
    }

    public static String extractAddressIFE(String s) {
        String result = "";
        String[] lines = s.split("\r\n|\r|\n");
        if (lines[0].contains("DOMICILIO") && lines.length > 3) {
            for (int i = 1; i <= 3; i++) {
                result += lines[i];
            }
        } else if (lines.length <= 3) {
            for (int i = 0; i<lines.length && i <= 2; i++) {
                result += lines[i];
            }
        } else {
            for (int i = 0; i < lines.length; i++) {
                result += lines[i];
                if(lines[i].contains("."))
                    break;
            }
        }
        return result.replace(" SIN"," S/N");
    }

    public static String[] extractLastNameProfessionalLicenseA(String s) {
        String result[] = new String[3];
        String[] lines = s.split("[ |\n]+");

        if(lines!=null && lines.length ==3){
            result[0] = lines[1];
            result[1] = lines[2];
            result[2] = lines[0];

        }else if(lines!=null && lines.length >=4){
            result[0] = lines[2];
            result[1] = lines[3];
            result[2] = lines[0] +" "+lines[1];

        }

        return result;
    }

    public static String[] extractLastNameProfessionalLicense(String s) {
        String result[] = new String[3];
        String[] lines = s.split("[ |\n]+");

        if(lines!=null && lines.length ==3){
            result[0] = lines[0];
            result[1] = lines[1];
            result[2] = lines[2];

        }else if(lines!=null && lines.length >=4){
            result[0] = lines[0];
            result[1] = lines[1];
            result[2] = lines[2] +" "+lines[3];

        }

            return result;
    }

    public static String[] extractLastNameMigratory(String s) {
        String result[] = new String[3];
        String[] lines = s.split("[ ]+");

        if(lines!=null && lines.length ==3){
            result[0] = lines[0];
            result[1] = lines[1];
            result[2] = lines[2];

        }else if(lines!=null && lines.length >=4){
            result[0] = lines[0];
            result[1] = lines[1];
            result[2] = lines[2] +" "+lines[3];

        }

        return result;
    }

    public static String[] extractLastNamePassport(String s) {
        String result[] = new String[3];
        s = s.replaceAll("[\n|\r]+","").trim();
        String[] lines = s.split("[ ]+");

        if(lines!=null && lines.length == 2){
            result[0] = lines[0];
            result[1] = lines[1];

        }else if(lines!=null && lines.length  == 1){
            result[0] = lines[0];
        }

        return result;
    }

    public static String[] extractNamePassport(String s) {
        String result[] = new String[3];
        s = s.replaceAll("[\n|\r]+","").trim();
        String[] lines = s.split("[ ]+");

        if(lines!=null && lines.length == 2){
            result[0] = lines[0];
            result[1] = lines[1];

        }else if(lines!=null && lines.length  == 1){
            result[0] = lines[0];
        }

        return result;
    }



//    private static PDF417Reader reader;
//    public static String performBarcode(Bitmap bitmap) {
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        int[] pixels = new int[width * height];
//        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
//        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(
//                width, height, pixels);
//        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
//        if (reader == null)
//            reader = new PDF417Reader();
//        try {
//            Result result = reader.decode(binaryBitmap);
//            byte[] raw = result.getRawBytes();
//            if (raw != null && raw.length > 0) {
//                return new String(raw);
//            } else {
//                String text = result.getText();
//                Log.d("UtilsOCR", "Reading only {Alpha} and {Digits}...");
//                String alphaAndDigits = text.replaceAll("[^\\p{Alpha}\\p{Digit}\\+\\_]+", " ");
//                Log.d("UtilsOCR", "Read: " + alphaAndDigits);
//                return alphaAndDigits;
//            }
//        } catch (NotFoundException e) {
//            e.printStackTrace();
//        } catch (FormatException e) {
//            e.printStackTrace();
//        } catch (ChecksumException e) {
//            e.printStackTrace();
//        }
//        return "";
////        String result = "";
////        try {
////            BarcodeDetector detector = new BarcodeDetector.Builder(activity).setBarcodeFormats(Barcode.PDF417).build();
////            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
////            SparseArray<Barcode> barcodes = detector.detect(frame);
////            if (barcodes.size() > 0) {
////                Barcode thisCode = barcodes.valueAt(0);
////                result = thisCode.rawValue;
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////        return result;
//    }

    public static String performDate(String date) {

        String result = "";
        date = date.replace(" ","").replace("-","").trim();

        try {
            SimpleDateFormat format = null;
            if (date.length() >= 9) {
                format = new SimpleDateFormat("ddMMMyyyy");
            }
            if(format!=null) {
                Date newDate = null;
                if(date.length() == 9) {
                    newDate = format.parse(date);
                    format = new SimpleDateFormat("dd-MMM-yyyy");
                    result = format.format(newDate).toUpperCase();
                }else if(date.length() > 9){
                    String d = date.substring(0,9);
                    String l = date.substring(9);
                    newDate = format.parse(d);
                    format = new SimpleDateFormat("dd-MMM-yyyy");
                    result = format.format(newDate).toUpperCase() +" "+ l;
                }
            }else{
                return date;
            }
        }catch (Exception e){
            return date;
        }

        return result;

    }

    public static String performBarcode(Bitmap bitmap, Context context) {
        String result = "";
        BarcodeDetector detector = new BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.PDF417).build();
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);

        if(barcodes.size()>0) {
            Barcode thisCode = barcodes.valueAt(0);
            result = thisCode.rawValue;
        }

        return result;
    }

    public static  void saveBitmapField(String directory, Bitmap bitmap, CaptureField captureField, Context context, String processId) {
        String processPath = FileManager.createTempFile(context, processId).getAbsolutePath();
        File folder = new File(processPath);
        if (!folder.exists()) {
            folder.mkdirs();
            Log.d(context.getClass().getSimpleName(), "wrote: created folder " + folder.getPath());
        }

        // create photo directory
        String photoDirPath = processPath + directory; // "/photo/";
        File dirPhoto = new File(photoDirPath);
        if (!dirPhoto.exists())
            dirPhoto.mkdirs();

        // create photo file
        File bitmapFile = new File(dirPhoto.getPath(), captureField.getName()); //foto.png

        // almacenamos en file system
        FileManager.bitmapToFile(bitmap, bitmapFile);

        // agregamos campo tipo URI
        captureField.setValue(bitmapFile.getAbsolutePath());
    }

    public static Bitmap convert(Bitmap bitmap, Bitmap.Config config) {
        Bitmap convertedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
        Canvas canvas = new Canvas(convertedBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return convertedBitmap;
    }


    public static String removeWords(String[] split){

        if (split.length == 1){
            return split[0];
        }
        String result = "";

        for (int i = 0 ; i < split.length; i++){
            if (i == 1 || i == 2){
                result += " " +split [i];
            }
        }
        return result.trim();
    }

    public static String cleanField(String field){
        return field.replace("\n", "").replace(" ", "").trim();
    }

    public static  String removeDuplicates(String[] fields){
        String result = "";
        fields = new HashSet<>(Arrays.asList(fields)).toArray(new String[0]);

        for (String field : fields){
            result += field;
        }

        return result.trim();
    }
}


