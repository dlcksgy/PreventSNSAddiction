package kr.ac.kau.sw.a2016125063.preventsnsaddiction

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
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.view.Surface
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.*
import com.google.firebase.storage.UploadTask
import com.google.android.gms.tasks.OnSuccessListener
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import java.text.SimpleDateFormat


class CameraActivity : Activity() ,Camera2APIs.Camera2Interface, TextureView.SurfaceTextureListener{

    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val mStorageRef : StorageReference = storage.getReferenceFromUrl("gs://preventsnsaddiction.appspot.com/")

    val date = Date()
    val sdf_m = SimpleDateFormat("mm").format(date).toString()
    val sdf_h = SimpleDateFormat("H").format(date).toString()
    val sdf_s = SimpleDateFormat("s").format(date).toString()
    val jpgName = "/image/picture/"+sdf_h+sdf_m+sdf_s+".jpg"

    val imageRef : StorageReference by lazy{ mStorageRef.child(jpgName)}
    val ext = Environment.getExternalStorageDirectory().absolutePath
    val dir = ext+"/work"
    var picture : Bitmap? = null
    val mTextureView  by lazy{ textureView }
    val mCamera by lazy{Camera2APIs(this)}
    val task2 = object : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg p0: Void?): Void? {
            Thread.sleep(1500)

            picture = mTextureView.bitmap
            saveBitmaptoJpeg(picture!!,"work","picture")

            val stream = FileInputStream(File(dir+"/picture.jpg"))
            val uploadTask = imageRef.putStream(stream)
            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
            }.addOnSuccessListener { taskSnapshot ->
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                val downloadUrl = taskSnapshot.downloadUrl
            }

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
        if ((checkSelfPermission(android.Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED))
        {
            return
        }
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

        if ((checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED))
        {
            return
        }
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
