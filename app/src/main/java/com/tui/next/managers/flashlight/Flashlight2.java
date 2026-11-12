package com.tui.next.managers.flashlight;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.tui.next.tuils.PrivateIOReceiver;

@TargetApi(23)
public class Flashlight2 extends Flashlight {

    public static final String TYPE = Constants.ID_DEVICE_OUTPUT_TORCH_FLASH_NEW;

    private String torchCameraId;
    private boolean flashSupported;

    public Flashlight2(Context context) {
        super(context);
        flashSupported = false;
    }

    @Override
    protected void turnOn() {
        if (!this.getStatus()) {
            CameraManager mCameraManager = (CameraManager) this.mContext.getSystemService(Context.CAMERA_SERVICE);
            if (mCameraManager == null) return;

            try {
                String[] cameraIds = mCameraManager.getCameraIdList();
                torchCameraId = null;
                flashSupported = false;
                for (String id : cameraIds) {
                    CameraCharacteristics chars = mCameraManager.getCameraCharacteristics(id);
                    Boolean available = chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    if (Boolean.TRUE.equals(available)) {
                        torchCameraId = id;
                        flashSupported = true;
                        break;
                    }
                }
            } catch (Exception e) {
                Intent intent = new Intent(PrivateIOReceiver.ACTION_OUTPUT);
                intent.putExtra(PrivateIOReceiver.TEXT, e.toString());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }

            if (this.flashSupported && this.torchCameraId != null) {
                try {
                    mCameraManager.setTorchMode(this.torchCameraId, true);
                    this.updateStatus(true);
                } catch (CameraAccessException e) {
                    Intent intent = new Intent(PrivateIOReceiver.ACTION_OUTPUT);
                    intent.putExtra(PrivateIOReceiver.TEXT, e.toString());
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }
            }
        }
    }

    @Override
    protected void turnOff() {
        if (this.getStatus()) {
            if (this.torchCameraId != null && this.flashSupported) {
                CameraManager mCameraManager = (CameraManager) this.mContext.getSystemService(Context.CAMERA_SERVICE);
                if (mCameraManager == null) return;
                try {
                    mCameraManager.setTorchMode(this.torchCameraId, false);
                } catch (CameraAccessException e) {
                    Intent intent = new Intent(PrivateIOReceiver.ACTION_OUTPUT);
                    intent.putExtra(PrivateIOReceiver.TEXT, e.toString());
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    return;
                }
                this.updateStatus(false);
            }
        }
    }
}
