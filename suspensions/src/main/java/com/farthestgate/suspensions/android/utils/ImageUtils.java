package com.farthestgate.suspensions.android.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;

import com.farthestgate.suspensions.android.base.AppController;
import com.farthestgate.suspensions.android.constant.AppConstant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class ImageUtils {

    public static File saveToFile(String fileName, Bitmap bitmap, int width, int height){
        File pictureFile = FileUtils.getFile(AppConstant.PHOTOS_FOLDER, fileName);
        try {
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            FileOutputStream fos = new FileOutputStream(pictureFile);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        }  catch (Exception e) {
        }
        return pictureFile;
    }

    private static Bitmap ConvertBase64ToImage(String base64String) {
        Bitmap bmp = null;
        try {
            if (base64String == null || base64String.equals("")) {
                return null;
            } else {

                byte[] decodedString = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT);
                bmp =  BitmapFactory.decodeByteArray(
                        decodedString, 0, decodedString.length);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    public static String resizeBase64Image(Bitmap bitmap, int width, int height){
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
        byte[] ba = bao.toByteArray();
        String base64image = Base64.encodeToString(ba, Base64.DEFAULT);

        byte [] encodeByte=Base64.decode(base64image.getBytes(),Base64.DEFAULT);
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPurgeable = true;
        Bitmap image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length,options);


        if(image.getHeight() <= height && image.getWidth() <= width){
            return base64image;
        }
        image = Bitmap.createScaledBitmap(image, width, height, false);

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100, baos);

        byte [] b=baos.toByteArray();
        System.gc();
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

    public static Bitmap decodeFile(File file, int newWidth, int newHeight)
    {// target size
        try
        {
            Context ctx = AppController.getContext();
            Resources res = ctx.getResources();

            Bitmap bmp = MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), Uri.fromFile(file));
            if(bmp == null)
            {
                // avoid concurrence
                // Decode image size
                BitmapFactory.Options option = new BitmapFactory.Options();

                // option = getBitmapOutput(file);

                option.inDensity = ctx.getResources().getDisplayMetrics().densityDpi < DisplayMetrics.DENSITY_HIGH ? 120 : 240;
                option.inTargetDensity = res.getDisplayMetrics().densityDpi;

                if(newHeight > 0 && newWidth > 0)
                    option.inSampleSize = calculateInSampleSize(option, newWidth, newWidth);

                option.inJustDecodeBounds = false;
                byte[] decodeBuffer = new byte[20 * 1024];
                option.inTempStorage = decodeBuffer;
                option.inPurgeable = true;
                option.inInputShareable = true;
                option.inScaled = true;

                bmp = BitmapFactory.decodeStream(new FileInputStream(file), null, option);
                if(bmp == null)
                {
                    return null;
                }
            }
            else
            {
                /*int inDensity = res.getDisplayMetrics().densityDpi < DisplayMetrics.DENSITY_HIGH ? 120 : 240;
                int inTargetDensity = res.getDisplayMetrics().densityDpi;
                if(inDensity != inTargetDensity)
                {
                    int newBmpWidth = (bmp.getWidth() * inTargetDensity) / inDensity;
                    int newBmpHeight = (bmp.getHeight() * inTargetDensity) / inDensity;*/
                bmp = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, true);
                //}
            }

            return bmp;
        }
        catch(Exception e) {
            FileUtils.logError(e);
        }
        return null;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if(height > reqHeight || width > reqWidth)
        {
            if(width > height)
            {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            }
            else
            {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    public static boolean applyImageWatermark(File imageFile, String watermark) {
        WeakReference<Bitmap> src = new WeakReference<Bitmap>(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
        WeakReference<Bitmap> result = createBitmapWatermark(src, watermark);
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            result.get().compress(Bitmap.CompressFormat.JPEG, 80, bytes);
            result.get().recycle();
            FileOutputStream fo = new FileOutputStream(imageFile);
            fo.write(bytes.toByteArray());
            fo.close();
            result.clear();
            src.clear();
            return true;
        } catch (IOException e) {
            return false;
//            "Error: " + e.toString();
        }
    }

    private static WeakReference<Bitmap> createBitmapWatermark(WeakReference<Bitmap> src, String watermark) {
        int w = src.get().getWidth();
        int h = src.get().getHeight();
        WeakReference<Bitmap> result = new WeakReference<Bitmap>(Bitmap.createBitmap(w, h, src.get().getConfig()));
        Canvas canvas = new Canvas(result.get());
        canvas.drawBitmap(src.get(), 0, 0, null);
        Paint paint = new Paint();
        paint.setTextSize(50);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        canvas.drawText(watermark, 50f, 110f, paint);
        src.get().recycle();
        src.clear();
        return result;
    }
}
