package com.farthestgate.suspensions.android.ui.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

	private Camera camera;
	private SurfaceHolder holder;

	public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CameraPreview(Context context) {
		super(context);
	}

	public void init(Camera camera) {
		this.camera = camera;
		initSurfaceHolder();
        adjustPreviewParameters(this.camera.getParameters());
	}

	@SuppressWarnings("deprecation") // needed for < 3.0
	private void initSurfaceHolder() {
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
        try {
            initCamera(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void adjustPreviewParameters(Camera.Parameters parameters) {
        String flashMode=
                findBestFlashModeMatch(parameters,
						Camera.Parameters.FLASH_MODE_RED_EYE,
						Camera.Parameters.FLASH_MODE_AUTO,
						Camera.Parameters.FLASH_MODE_ON);

        parameters.setFlashMode(flashMode);

        this.camera.setParameters(parameters);
    }


    private void initCamera(SurfaceHolder holder) throws IOException {
			camera.setPreviewDisplay(holder);
            camera.enableShutterSound(true);
			camera.startPreview();

	}


    @Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	public static String findBestFlashModeMatch(Camera.Parameters params,
												String... modes) {
		String match=null;

		List<String> flashModes=params.getSupportedFlashModes();

		if (flashModes != null) {
			for (String mode : modes) {
				if (flashModes.contains(mode)) {
					match=mode;
					break;
				}
			}
		}

		return(match);
	}


}