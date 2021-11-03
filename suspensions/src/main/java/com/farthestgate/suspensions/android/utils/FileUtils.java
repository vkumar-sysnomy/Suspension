package com.farthestgate.suspensions.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.farthestgate.suspensions.android.constant.AppConstant;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;

/**
 * Created by Suraj Gopal on 12/27/2016.
 */
public class FileUtils {

    private static final String TAG = FileUtils.class.getName();

    public static File getDirectory(String path) {
        File mediaStorageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String rootDir = Environment.getExternalStorageDirectory().toString();
            mediaStorageDir = new File(rootDir + File.separator + path);
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(TAG, "failed to create directory");
                }
            }
        }
        return mediaStorageDir;
    }

    public static File getFile(String path, String fileName) {
        File mediaStorageDir = getDirectory(path);
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    public static String readFile(String path, String fileName) {
        BufferedReader br = null;
        String response = null;
        try {
            StringBuffer output = new StringBuffer();
            File file = getFile(path, fileName);
            if(file.exists()) {
                String fpath = file.getAbsolutePath();

                br = new BufferedReader(new FileReader(fpath));
                String line = "";
                while ((line = br.readLine()) != null) {
                    output.append(line);
                }
                response = output.toString();
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
        return response;
    }

    public static boolean  moveFile(String origin, String destination){
        boolean isFileMoved = false;
        try{
            File fileOrigin =new File(origin);
            if(fileOrigin.renameTo(FileUtils.getFile(destination, fileOrigin.getName()))){
                isFileMoved = true;
            }else{
                isFileMoved = false;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return isFileMoved;
    }

    public static void copyDirectory(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }
            File targetDir = new File(targetLocation.getAbsolutePath() + "/" + sourceLocation.getName());
            if (!targetDir.exists())
                targetDir.mkdir();

            String[] children = sourceLocation.list();
            for (int i = 0; i < sourceLocation.listFiles().length; i++) {

                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetDir, children[i]));
            }
        } else {
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    public static void deleteDir(File file){
        if(file.isDirectory()){
            //directory is empty, then delete it
            if(file.list().length==0){
                file.delete();
            }else{
                //list all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    deleteDir(fileDelete);
                }
                //check the directory again, if empty then delete it
                if(file.list().length==0){
                    file.delete();
                }
            }

        }else{
            //if file, then delete it
            file.delete();
        }
    }

    public static boolean isDataFileExist(String path, String fileName){
        boolean fileExist= false;
        try{
            File dataFile = getFile(path, fileName);
            fileExist = dataFile.exists();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return fileExist;
    }

    public static void saveToFile(String filePath, String fileName, String fileData) {
        try {
            File file = getFile(filePath, fileName);
            if(!file.exists())
                file.createNewFile();
            Writer writer =  new BufferedWriter(new FileWriter(file));
            writer.write(fileData);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static void logError(Throwable exc) {
        try {
            final String SINGLE_LINE_SEP = "\n";
            final String DOUBLE_LINE_SEP = "\n\n";
            StackTraceElement[] arr = exc.getStackTrace();
            final StringBuffer report = new StringBuffer(exc.toString());
            final String lineSeperator = "-------------------------------\n\n";
            report.append(lineSeperator);
            report.append("--------- Stack trace ---------\n\n");
            if (arr != null) {
                for (int i = 0; i < arr.length; i++) {
                    report.append("    ");
                    report.append(arr[i].toString());
                    report.append(SINGLE_LINE_SEP);
                }
            }
            report.append(lineSeperator);
            report.append("--------- Cause ---------\n\n");
            Throwable cause = exc.getCause();
            if (cause != null) {
                report.append(cause.toString());
                report.append(DOUBLE_LINE_SEP);
                arr = cause.getStackTrace();
                for (int i = 0; i < arr.length; i++) {
                    report.append("    ");
                    report.append(arr[i].toString());
                    report.append(SINGLE_LINE_SEP);
                }
            }
            String errorContent = report.toString();
            File fileToCreate = FileUtils.getFile(AppConstant.ERROR_FOLDER, DateUtils.getCurrentDate("ddMMyyyy") + "_" + "err.log");
            FileOutputStream os = new FileOutputStream(fileToCreate, true);
            errorContent = errorContent + SINGLE_LINE_SEP;
            os.write(errorContent.getBytes());
            os.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
