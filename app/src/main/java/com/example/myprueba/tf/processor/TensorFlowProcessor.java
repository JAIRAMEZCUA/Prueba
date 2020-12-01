package com.example.myprueba.tf.processor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.example.myprueba.tf.tflite.Classifier;
import com.example.myprueba.tf.tflite.ImageUtils;
import com.example.myprueba.tf.tflite.Logger;
import com.example.myprueba.tf.tflite.TFLiteObjectDetectionAPIModel;
import com.naat.camerawidget.AbstractImageProcessor;
import com.naat.camerawidget.ProcessorResult;


import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class TensorFlowProcessor extends AbstractImageProcessor {

    private static final String TAG = TensorFlowProcessor.class.getSimpleName();

    // Configuration values for the prepackaged SSD model.
    private static final int TF_OD_API_INPUT_SIZE = 300;
//    private static final boolean TF_OD_API_IS_QUANTIZED = true;
//    private static final String TF_OD_API_MODEL_FILE = "detect_uint8_v1.tflite";
//    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap_v1.txt";
    private static final boolean TF_OD_API_IS_QUANTIZED = false;
    private static final String TF_OD_API_MODEL_FILE = "detect_float16.tflite";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap9c.txt";

    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.95f;
    private static final DetectorMode MODE = DetectorMode.TF_OD_API;
    private static final boolean MAINTAIN_ASPECT = false;

    private static final Logger LOGGER = new Logger();

    private Classifier detector;

    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;

    public TensorFlowProcessor() {
    }

    private boolean initialized = false;

    private void init(int previewWidth, int previewHeight) {
        int cropSize = TF_OD_API_INPUT_SIZE;

        try {
            detector =
                    TFLiteObjectDetectionAPIModel.create(
                            context.getAssets(),
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_INPUT_SIZE,
                            TF_OD_API_IS_QUANTIZED);
            cropSize = TF_OD_API_INPUT_SIZE;
        } catch (final IOException e) {
            e.printStackTrace();
            LOGGER.e(e, "Exception initializing classifier!");
            Toast toast =
                    Toast.makeText(context, "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
//            finish();
        }
        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Bitmap.Config.ARGB_8888);

        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        cropSize, cropSize,
                        0, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);
    }

    @Override
    public ProcessorResult processImageData(Bitmap imageData) {
        if (!initialized) {
            init(imageData.getWidth(), imageData.getHeight());
            initialized = true;
        }
        rgbFrameBitmap = imageData;

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
//        croppedBitmap = imageData;
        final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);
        float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
        switch (MODE) {
            case TF_OD_API:
                minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                break;
        }

        final List<Classifier.Recognition> mappedRecognitions =
                new LinkedList<Classifier.Recognition>();

        for (final Classifier.Recognition result : results) {
            final RectF location = result.getLocation();
            if (location != null && result.getConfidence() >= minimumConfidence) {
//                canvas.drawRect(location, paint);
//                cropToFrameTransform.mapRect(location);

                result.setLocation(location);
                mappedRecognitions.add(result);
            }
            Log.d(TAG, "ID is: " + result.getId());
            Log.d(TAG, "Title is: " + result.getTitle());
            Log.d(TAG, "Confidence is: " + result.getConfidence());
        }

        if (mappedRecognitions.isEmpty()) {
            Log.d(TAG, "No recognitions found...");
            return ProcessorResult.fail("No recognitions found.");
        }

        // take first only
        Classifier.Recognition recognition = mappedRecognitions.get(0);

        final RectF location = recognition.getLocation();
        cropToFrameTransform.mapRect(location);

        Bitmap result = Bitmap.createBitmap(imageData, (int) location.left, (int) location.top, (int) location.width(), (int) location.height());

        // validate blur
        if (isImageBlurred(result)) {
            Log.d(TAG, "Image is blur!!!");
            return ProcessorResult.fail("Result not sharped enough.");
        }

        Bundle extraResults = new Bundle();
        extraResults.putString("id", recognition.getId());
        extraResults.putString("title", recognition.getTitle());
        extraResults.putFloat("confidence", recognition.getConfidence());
        extraResults.putParcelable("location", location);
        return ProcessorResult.success(result, extraResults);
    }

//    private RectF scaleToPreview(RectF location) {
//        int frameWidth = cameraWidget.getFrameWidth();
//        int frameHeight = cameraWidget.getFrameHeight();
//        int sensorOrientation = 0; // TODO validate
//
//        int previewWidth = cameraWidget.getPreviewSize().getWidth();
//        int previewHeight = cameraWidget.getPreviewSize().getHeight();
//
//        final boolean rotated = sensorOrientation % 180 == 90;
//        final float multiplier =
//                Math.min(
//                        previewHeight / (float) (rotated ? frameWidth : frameHeight),
//                        previewWidth / (float) (rotated ? frameHeight : frameWidth));
//        Matrix frameToCanvasMatrix =
//                ImageUtils.getTransformationMatrix(
//                        frameWidth,
//                        frameHeight,
//                        (int) (multiplier * (rotated ? frameHeight : frameWidth)),
//                        (int) (multiplier * (rotated ? frameWidth : frameHeight)),
//                        sensorOrientation,
//                        false);
//
//        final RectF trackedPos = new RectF(location);
//        frameToCanvasMatrix.mapRect(trackedPos);
//        return trackedPos;
//    }

    // Which detection model to use: by default uses Tensorflow Object Detection API frozen
    // checkpoints.
    private enum DetectorMode {
        TF_OD_API;
    }

    static {
        System.loadLibrary("opencv_java4");
    }

    private boolean isImageBlurred(Bitmap image) {
        int threshold = 50;
        Mat src = new Mat();
        Mat dst = new Mat();
        MatOfDouble mean = new MatOfDouble();
        MatOfDouble stdDev = new MatOfDouble();

        // convert bitmap to Mat object
        Utils.bitmapToMat(image, src);

        // converting to gray scale
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGB2GRAY, 0);

        // calculating Laplacian & assign to dst
        Imgproc.Laplacian(src, dst, CvType.CV_64F, 1, 1, 0);

        // calculating standard deviation
        Core.meanStdDev(dst, mean, stdDev);

        double variance = Math.pow(stdDev.toArray()[0], 2);
        Log.d(TAG, "Variance is: " + variance);
        if (variance > threshold)
            return false;
        return true;
    }


}
