package com.farthestgate.suspensions.android.ui.camera;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.farthestgate.suspensions.android.R;
import com.farthestgate.suspensions.android.constant.AppConstant;
import com.farthestgate.suspensions.android.model.Config;
import com.farthestgate.suspensions.android.ui.gallery.GalleryActivity;
import com.farthestgate.suspensions.android.ui.suspensionMain.model.Suspension;
import com.farthestgate.suspensions.android.utils.DialogUtils;
import com.farthestgate.suspensions.android.utils.FileUtils;
import com.farthestgate.suspensions.android.utils.ImageUtils;
import com.farthestgate.suspensions.android.utils.PreferenceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CameraFragment extends Fragment implements Camera.PictureCallback, Camera.AutoFocusCallback,
        Camera.ShutterCallback, VerticalSeekBar.OnSeekBarChangeListener{

    @BindView(R.id.photo_btn)
    ImageView pic_button;
    @BindView(R.id.zoom)
    VerticalSeekBar verticalSeekBar;
    @BindView(R.id.camera_preview)
    CameraPreview cameraPreview;
    @BindView(R.id.focus)
    ImageView focus_btn;
    @BindView(R.id.btnSavePreview)
    ImageButton btnSave;
    @BindView(R.id.btnDiscardPreview)
    ImageButton btnDiscard;
    @BindView(R.id.galleryImageButton)
    ImageButton btnGallery;
    @BindView(R.id.preview)
    ImageView previewView;
    @BindView(R.id.flashModeOn)
    ImageButton flashModeOn;
    @BindView(R.id.flashModeOff)
    ImageButton flashModeOff;

    private Drawable in_focus;
    private Drawable out_focus;
    private Camera camera;
    private MediaActionSound mediaActionSound;

    private boolean isFlash =false;
    private int image_x_size;
    private int image_y_size;

    private OnCammeraAction mListener;

    private PreferenceUtils preferenceUtils;
    private Config config;
    private Suspension suspension;

    public static CameraFragment newInstance(){
        CameraFragment cameraFragment = new CameraFragment();
        return cameraFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnCammeraAction) context;
        } catch (ClassCastException e) {
            FileUtils.logError(e);
            throw new ClassCastException(context.toString()
                    + " must implement OnCammeraAction");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        config = Config.getConfigData();

        preferenceUtils = new PreferenceUtils(getContext());
        suspension = preferenceUtils.getObject(AppConstant.SUSPENSION, Suspension.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.camera_fragment, container, false);
        ButterKnife.bind(this, v);

        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        config = Config.getConfigData();

        image_x_size = Integer.parseInt(config.imageXSize);
        image_y_size = Integer.parseInt(config.imageYSize);

        in_focus    = getResources().getDrawable(R.mipmap.photo_mark);
        out_focus   = getResources().getDrawable(R.mipmap.photo_mark2);
        mediaActionSound = new MediaActionSound();
        mediaActionSound.load(MediaActionSound.SHUTTER_CLICK);

        verticalSeekBar.setOnSeekBarChangeListener(this);
        toggleFlashMode(!isFlash);
    }

    @Override
    public void onResume() {
        super.onResume();
        toggleFlashMode(!isFlash);
        if (camera == null) {
            camera = getCameraInstance();
            if(camera != null){
                initCameraPreview(true);
            }
        }
    }

    // ALWAYS remember to release the camera when you are finished
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseCamera();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick(R.id.flashModeOn)
    public void flashModeOn() {
        isFlash = true;
        if (cameraAvailable(camera)) {
            toggleFlashMode(!isFlash);
            initCameraPreview(false);
        }
    }

    @OnClick(R.id.flashModeOff)
    public void flashModeOff() {
        isFlash = false;
        if(cameraAvailable(camera)){
            toggleFlashMode(!isFlash);
            initCameraPreview(false);
        }
    }

    @OnClick(R.id.galleryImageButton)
    public void openGallery(){
        Intent intent = new Intent(getActivity(), GalleryActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.photo_btn)
    public void openCamera(){
        try {
            if(suspension.getPhotoCount() < 5)
                camera.takePicture(CameraFragment.this, null, CameraFragment.this);
            else
                DialogUtils.showToast(getContext(), "You can capture 5 photos per suspension");
        } catch (RuntimeException e) {
            FileUtils.logError(e);
            getActivity().finish();
        }
    }

    @OnClick(R.id.focus)
    public void focus(){
        try {
            camera.autoFocus(CameraFragment.this);
        } catch (RuntimeException autofocusException) {
            FileUtils.logError(autofocusException);
        }
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (success)
            focus_btn.setImageDrawable(in_focus);
        else
            focus_btn.setImageDrawable(out_focus);

        mediaActionSound.play(MediaActionSound.FOCUS_COMPLETE);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            verticalSeekBar.setEnabled(false);
            zoomTo(verticalSeekBar.getProgress()).onComplete(new Runnable() {
                @Override
                public void run() {
                    verticalSeekBar.setEnabled(true);
                }
            }).go();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onPictureTaken(final byte[] data, Camera camera) {
        final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        previewView.setImageBitmap(bitmap);
        previewView.setVisibility(View.VISIBLE);
        btnDiscard.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.VISIBLE);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation scaleAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.preview_anim);
                previewView.startAnimation(scaleAnim);
                suspension.setPhotoCount(suspension.getPhotoCount() + 1);
                preferenceUtils.saveObject(AppConstant.SUSPENSION, suspension);
                String fileName= suspension.getSuspensionNumber() + "-" + "photo" + "-" + suspension.getPhotoCount() + ".jpg";
                File file = ImageUtils.saveToFile(fileName, bitmap, image_x_size, image_y_size);
                mListener.OnPictureTaken(file);
                resetView();
            }
        });

        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetView();
            }
        });
    }

    @Override
    public void onShutter() {

    }

    // Show the camera view on the activity
    private void initCameraPreview(boolean initCamera) {
        if (initCamera) {
            cameraPreview.init(camera);
            camera.getParameters().setPreviewFormat(ImageFormat.JPEG);
            camera.getParameters().setJpegQuality(80);
            Integer zoomMax = camera.getParameters().getMaxZoom();
            verticalSeekBar.setMax(zoomMax);
        }
        //Set Flash mode
        Camera.Parameters parameters = camera.getParameters();
        List<String> flashModes = parameters.getSupportedFlashModes();
        if (flashModes != null) {
            for (String flashMode : flashModes) {
                if (isFlash) {
                    if (flashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_ON)) {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        break;
                    }
                } else {
                    if (flashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_OFF)) {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        break;
                    }
                }
            }
        }
        parameters.setPictureSize(parameters.getPreviewSize().width,parameters.getPreviewSize().height);
        camera.setParameters(parameters);
    }

    public ZoomTransaction zoomTo(int level) {
        if (camera == null) {
            DialogUtils.showToast(getActivity(), "Camera not available");
            return null;
        } else {
            Camera.Parameters params=camera.getParameters();
            if (level >= 0 && level <= params.getMaxZoom()) {
                return(new ZoomTransaction(camera, level));
            } else{
                throw new IllegalArgumentException(
                        String.format("Invalid zoom level: %d", level));
            }
        }
    }

    private void resetView() {
        previewView .setVisibility(View.INVISIBLE);
        btnDiscard  .setVisibility(View.INVISIBLE);
        btnSave     .setVisibility(View.INVISIBLE);
        toggleFlashMode(!isFlash);
        cameraPreview.init(camera);
        camera.startPreview();
        previewView.destroyDrawingCache();
        if(cameraAvailable(camera)){
            initCameraPreview(false);
        }
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            // Camera is not available or doesn't exist
            FileUtils.logError(e);
        }
        return c;
    }

    private boolean cameraAvailable(Camera camera) {
        return camera != null;
    }

    private void toggleFlashMode(boolean on){
        if(on){
            flashModeOn.setVisibility(View.VISIBLE);
            flashModeOff.setVisibility(View.INVISIBLE);
        }else{
            flashModeOn.setVisibility(View.INVISIBLE);
            flashModeOff.setVisibility(View.VISIBLE);
        }
    }

    public interface OnCammeraAction {
        void OnPictureTaken(File file);
    }

    /*public File saveToFile(Bitmap bitmap, int width, int height){
        String fileName= suspension.getSuspensionNumber() + "-" + "photo" + "-" + suspension.getPhotoCount() + ".jpg";;

        File pictureFile = FileUtils.getFile(AppConstant.PHOTOS_FOLDER, fileName);
        try {
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            FileOutputStream fos = new FileOutputStream(pictureFile);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        }  catch (Exception e) {
        }
        return pictureFile;
    }*/
}
