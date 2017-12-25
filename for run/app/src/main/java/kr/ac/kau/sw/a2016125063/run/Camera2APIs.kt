package kr.ac.kau.sw.a2016125063.preventsnsaddiction

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.Window
import android.support.annotation.NonNull
import android.view.Surface
import java.util.*
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CaptureResult

/**
 * Created by Arduino on 2017-12-25.
 */
class Camera2APIs(impl:Camera2Interface) {


    private var mInterface:Camera2Interface? = null
    var mCameraSize:Size? = null
    private var mCaptureSession:CameraCaptureSession? = null
    private var mCameraDevice:CameraDevice? = null
    private var mPreviewRequestBuilder:CaptureRequest.Builder? = null
    private var mCameraDeviceStateCallback = object:CameraDevice.StateCallback() {
        override fun onOpened(@NonNull camera:CameraDevice) {
            mCameraDevice = camera
            mInterface!!.onCameraDeviceOpened(camera, mCameraSize!!)
        }
        override fun onDisconnected(@NonNull camera:CameraDevice) {
            camera.close()
        }
        override fun onError(@NonNull camera:CameraDevice, error:Int) {
            camera.close()
        }
    }
    private val mCaptureSessionCallback = object:CameraCaptureSession.StateCallback() {
        override fun onConfigured(cameraCaptureSession:CameraCaptureSession) {
            try
            {
                mCaptureSession = cameraCaptureSession
                mPreviewRequestBuilder!!.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                cameraCaptureSession.setRepeatingRequest(mPreviewRequestBuilder!!.build(), mCaptureCallback, null)
            }
            catch (e:CameraAccessException) {
                e.printStackTrace()
            }
        }
        override fun onConfigureFailed(cameraCaptureSession:CameraCaptureSession) {
        }
    }
    private val mCaptureCallback = object:CameraCaptureSession.CaptureCallback() {
        override fun onCaptureProgressed(session:CameraCaptureSession, request:CaptureRequest, partialResult:CaptureResult) {
            super.onCaptureProgressed(session, request, partialResult)
        }
        override fun onCaptureCompleted(session:CameraCaptureSession, request:CaptureRequest, result:TotalCaptureResult) {
            super.onCaptureCompleted(session, request, result)
        }
    }
    interface Camera2Interface {
        fun onCameraDeviceOpened(cameraDevice:CameraDevice, cameraSize:Size)
    }
    init{
        mInterface = impl
    }
    fun CameraManager_1(activity:Activity):CameraManager {
        val cameraManager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        return cameraManager
    }
    fun CameraCharacteristics_2(cameraManager:CameraManager):String? {
        try
        {
            for (cameraId in cameraManager.getCameraIdList())
            {
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                if (characteristics.get(CameraCharacteristics.LENS_FACING) === CameraCharacteristics.LENS_FACING_FRONT)
                {
                    val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    val sizes = map.getOutputSizes(SurfaceTexture::class.java)
                    mCameraSize = sizes[0]
                    for (size in sizes)
                    {
                        if (size.getWidth() > mCameraSize!!.getWidth())
                        {
                            mCameraSize = size
                        }
                    }
                    return cameraId
                }
            }
        }
        catch (e:CameraAccessException) {
            e.printStackTrace()
        }
        return null
    }

    fun CameraDevice_3(cameraManager:CameraManager, cameraId:String) {
        try
        {

            cameraManager.openCamera(cameraId, mCameraDeviceStateCallback, null)
        }
        catch (e:CameraAccessException) {
            e.printStackTrace()
        }
    }
    fun CaptureSession_4(cameraDevice:CameraDevice, surface:Surface) {
        try
        {
            cameraDevice.createCaptureSession(Collections.singletonList(surface), mCaptureSessionCallback, null)
        }
        catch (e:CameraAccessException) {
            e.printStackTrace()
        }
    }
    fun CaptureRequest_5(cameraDevice:CameraDevice, surface:Surface) {
        try
        {
            mPreviewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mPreviewRequestBuilder!!.addTarget(surface)
        }
        catch (e:CameraAccessException) {
            e.printStackTrace()
        }
    }
    fun closeCamera() {
        if (null != mCaptureSession)
        {
            mCaptureSession!!.close()
            mCaptureSession = null
        }
        if (null != mCameraDevice)
        {
            mCameraDevice!!.close()
            mCameraDevice = null
        }
    }
}