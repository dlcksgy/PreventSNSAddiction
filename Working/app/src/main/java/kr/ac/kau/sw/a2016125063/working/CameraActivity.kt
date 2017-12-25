package kr.ac.kau.sw.a2016125063.working

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
import java.util.*
import android.view.TextureView
import kotlinx.android.synthetic.main.activity_camera.*
import android.hardware.camera2.CameraManager
import android.os.Environment
import android.view.Surface


class CameraActivity : Activity() ,Camera2APIs.Camera2Interface, TextureView.SurfaceTextureListener{


    val ext = Environment.getExternalStorageDirectory().absolutePath
    val dir = ext+"/work"
    val mTextureView  by lazy{textureView}
    val mCamera by lazy{Camera2APIs(this)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_camera)
        //다이얼로그 밖을 눌렀을 때에도 안끝나도록
        setFinishOnTouchOutside(false)


    }

    override fun onResume() {
        super.onResume()
        if (mTextureView.isAvailable) {
            openCamera()
        } else {
            mTextureView.surfaceTextureListener = this
        }

    }

    override fun onStart() {

        if (mTextureView.isAvailable) {
            openCamera()
        } else {
            mTextureView.surfaceTextureListener = this
            mTextureView.bitmap
        }
        super.onStart()

    }

    override fun onStop() {

        if (mTextureView.isAvailable) {
            openCamera()
        } else {
            mTextureView.surfaceTextureListener = this
        }
        super.onStop()
    }

    override fun onRestart() {

        if (mTextureView.isAvailable) {
            openCamera()
        } else {
            mTextureView.surfaceTextureListener = this
        }
    super.onRestart()
    }

    fun openCamera(){
        val cameraManager = mCamera.CameraManager_1(this)
        val cameraId = mCamera.CameraCharacteristics_2(cameraManager)
        mCamera.CameraDevice_3(cameraManager,cameraId!!)
    }
    override fun onCameraDeviceOpened(cameraDevice: CameraDevice, cameraSize: Size) {
        val texture = mTextureView.surfaceTexture
        texture.setDefaultBufferSize(mCamera.mCameraSize!!.width, mCamera.mCameraSize!!.height)
        val surface = Surface(texture)

        mCamera.CaptureSession_4(cameraDevice,surface)
        mCamera.CaptureRequest_5(cameraDevice, surface)
    }

    override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {
    }

    override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
        finish()
        return true
    }

    override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, p1: Int, p2: Int) {
        mTextureView.setSurfaceTextureListener(this);
        openCamera()
    }

    //back버튼을 눌러도 안꺼지도록 override해준다.
    override fun onBackPressed() { }


}
