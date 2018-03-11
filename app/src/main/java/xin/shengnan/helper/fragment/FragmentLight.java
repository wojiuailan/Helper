package xin.shengnan.helper.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;

import xin.shengnan.helper.R;

public class FragmentLight extends Fragment {

    private ImageView mIVLighrButton;
    private boolean lightOn = false;
    private Camera mCamera;
    private Camera.Parameters mParameters;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_light, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mIVLighrButton = view.findViewById(R.id.iv_light_button);

        mIVLighrButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (lightOn) {
                    mIVLighrButton.setImageResource(R.drawable.off);
                    lightOn = false;
                    closeLight();
                } else {
                    mIVLighrButton.setImageResource(R.drawable.on);
                    lightOn = true;
                    openLight();
                }
            }
        });


    }

    private void openLight() {
        int textureId = 0;
        try {
            mCamera = Camera.open();
            mCamera.setPreviewTexture(new SurfaceTexture(textureId));
            mCamera.startPreview();

            mParameters = mCamera.getParameters();
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

            mCamera.setParameters(mParameters);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeLight() {
        mParameters = mCamera.getParameters();
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

        mCamera.setParameters(mParameters);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCamera != null) {
            mParameters = mCamera.getParameters();
            if (Camera.Parameters.FLASH_MODE_ON.equals(mParameters.getFlashMode())) {
                closeLight();
            }
        }
    }
}
