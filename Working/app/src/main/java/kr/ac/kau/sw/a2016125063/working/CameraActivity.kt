package kr.ac.kau.sw.a2016125063.working

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import android.os.AsyncTask
import android.os.Environment
import android.view.Surface
import java.io.*


class CameraActivity : Activity() ,Camera2APIs.Camera2Interface, TextureView.SurfaceTextureListener{


    val ext = Environment.getExternalStorageDirectory().absolutePath
    val dir = ext+"/work"
    var picture : Bitmap? = null
    val mTextureView  by lazy{textureView}
    val mCamera by lazy{Camera2APIs(this)}
    val task2 = object : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg p0: Void?): Void? {
            Thread.sleep(1500)

            picture = mTextureView.bitmap
            saveBitmaptoJpeg(picture!!,"work","picture")
            finish()
            return null
        }

    }

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

        task2.execute()

    }

    fun saveBitmaptoJpeg(bitmap:Bitmap, folder:String, name:String) {
        val ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath()
        // Get Absolute Path in External Sdcard
        val foler_name = "/" + folder + "/"
        val file_name = name + ".jpg"
        val string_path = ex_storage + foler_name
        val file_path: File
        try
        {
            file_path = File(string_path)
            if (!file_path.isDirectory())
            {
                file_path.mkdirs()
            }
            val out = FileOutputStream(string_path + file_name)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.close()
        }
        catch (exception: FileNotFoundException) {
            Log.e("FileNotFoundException", exception.message)
        }
        catch (exception:IOException) {
            Log.e("IOException", exception.message)
        }
    }

    //back버튼을 눌러도 안꺼지도록 override해준다.
    override fun onBackPressed() { }


}
