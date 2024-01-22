package com.task.imageuploadsoft.ui

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStarted
import com.task.imageuploadsoft.R
import com.task.imageuploadsoft.databinding.ActivityMainBinding
import com.task.imageuploadsoft.model.CameraOptions
import com.task.imageuploadsoft.util.AppPermissionsRunTime
import com.task.imageuploadsoft.util.AppPermissionsRunTime.Companion.instance
import com.task.imageuploadsoft.util.ConnectionUtil
import com.task.imageuploadsoft.util.Constants
import com.task.imageuploadsoft.util.Constants.GALLERY
import com.task.imageuploadsoft.util.Constants.IMAGE_CAPTURE
import com.task.imageuploadsoft.viewmodel.MainActivityViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.Objects


class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var permissionsRunTime: AppPermissionsRunTime? = null
    private var permissionList: ArrayList<AppPermissionsRunTime.Permission>? = null
    private var mCurrentPhotoPath: String? = null
    private var recentPhotoPath: String? = null
    private var handler: Handler? = null

    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        setContentView(view)
        permissionsRunTime = instance
        permissionList = ArrayList()
        handler = Handler(mainLooper)
        binding!!.progressBarCyclic.visibility = View.GONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionList!!.add(AppPermissionsRunTime.Permission.READ_MEDIA_IMAGE)
            permissionList!!.add(AppPermissionsRunTime.Permission.CAMERA)
        } else {
            permissionList!!.add(AppPermissionsRunTime.Permission.CAMERA)
            permissionList!!.add(AppPermissionsRunTime.Permission.READ_EXTERNAL_STORAGE)
            permissionList!!.add(AppPermissionsRunTime.Permission.WRITE_EXTERNAL_STORAGE)
        }
        binding!!.imageView.setOnClickListener { showUploadOptions() }
        binding!!.previewButton.setOnClickListener { // Display the preview of the selected image in a dialog
            if (recentPhotoPath != null) {
                showImagePreviewDialog(recentPhotoPath!!)
            } else {
                showAlert("Please choose image")
            }
        }
        binding!!.fileTypeTextView.setOnClickListener { showUploadOptions() }
//        binding!!.submitButton.setOnClickListener { uploadImages() }
        lifecycleScope.launch {
            lifecycle.withStarted {
                mainActivityViewModel.mainEvent.observe(this@MainActivity, Observer {
                    when (it) {
                        is MainActivityViewModel.MainEvent.Success -> {
                            binding!!.progressBarCyclic!!.visibility = View.GONE
                            refreshImageView()
                            showAlert("Image uploaded successfully")
                        }

                        is MainActivityViewModel.MainEvent.Failure -> {

                            binding!!.progressBarCyclic!!.visibility = View.GONE

                            Log.d("XXXXXXXX", "" + it.m!!)
                        }

                        is MainActivityViewModel.MainEvent.Loading -> {

                            binding!!.progressBarCyclic!!.visibility = View.VISIBLE
                        }

                        else -> {}
                    }

                })
                mainActivityViewModel.imageFile.observe(this@MainActivity, Observer {
                    binding!!.submitButton.setOnClickListener { v ->
                        if (recentPhotoPath != null) {
                            if (!ConnectionUtil.isNetworkAvailable(applicationContext)) {
                                showAlert(getString(R.string.no_internet_connection))
                            } else {
                                mainActivityViewModel.uploadImage(it)
                            }
                        } else {
                            showAlert("Please choose image")
                        }
                    }
                })
            }
        }

    }

    private fun showAlert(message: String) {
        Toast.makeText(
            applicationContext,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun refreshImageView() {
        recentPhotoPath = null
        mCurrentPhotoPath = null
        binding!!.fileTypeTextView.setText(R.string.pick_image)
        binding!!.imageView.setImageBitmap(null)
        binding!!.submitButton.visibility = View.INVISIBLE
        binding!!.previewButton.visibility = View.INVISIBLE
    }

    private fun showImagePreviewDialog(imageUri: String) {
        val imagePreviewDialog = Dialog(this)
        imagePreviewDialog.setContentView(R.layout.dialogue_preview)
        imagePreviewDialog.setCancelable(true)
        val dialogImageView = imagePreviewDialog.findViewById<ImageView>(R.id.dialogImageView)
        dialogImageView.setImageBitmap(BitmapFactory.decodeFile(imageUri))
        imagePreviewDialog.show()
    }

    fun showUploadOptions() {
        val items: Array<CameraOptions>
        items = arrayOf(
            CameraOptions(getString(R.string.txt_camera_option), R.drawable.add_photo),
            CameraOptions(getString(R.string.txt_gallery_option), R.drawable.add_photo_folder)
        )
        val adapter: ListAdapter = object : ArrayAdapter<CameraOptions?>(
            this@MainActivity,
            android.R.layout.select_dialog_item,
            android.R.id.text1,
            items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                val tv = v.findViewById<TextView>(android.R.id.text1)
                tv.textSize = 20f
                tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0)
                tv.compoundDrawablePadding = 0
                return v
            }
        }
        val builder = AlertDialog.Builder(
            this@MainActivity,
            androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert
        )
        builder.setTitle(getString(R.string.txt_select))
        builder.setAdapter(adapter) { dialog: DialogInterface?, item: Int ->
            if (item == 0) {
                openCamera()
            } else if (item == 1) {
                openGallery()
            }
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog: DialogInterface?, id: Int -> }
        builder.show()
    }

    private fun openGallery() {
        if (permissionsRunTime!!.getPermission(permissionList, this@MainActivity)) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(intent, GALLERY)
        }
    }

    private fun openCamera() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (permissionsRunTime!!.getPermission(permissionList, this@MainActivity)) {
                dispatchTakePictureIntent()
            }
        } else {
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent() {
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                val m = StrictMode::class.java.getMethod(getString(R.string.file_uri_exposure))
                m.invoke(null)
            }
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                val photoFile: File
                photoFile = createImageFileDp()
                val photoURI = FileProvider.getUriForFile(this, Constants.SOFTGROUP, photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                startActivityForResult(takePictureIntent, IMAGE_CAPTURE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createImageFileDp(): File {
        val storageDir = File(this.filesDir, "SOFT-GROUP")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        val fName = "SoftGroup" + System.currentTimeMillis() + ".jpg"
        val imageFile = File(storageDir, fName)
        mCurrentPhotoPath = imageFile.absolutePath
        return imageFile
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                IMAGE_CAPTURE -> mCurrentPhotoPath?.let { processImage(it) }
                GALLERY -> if (data != null) {
                    val selectedImage = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor = contentResolver.query(
                        Objects.requireNonNull(selectedImage)!!,
                        filePathColumn,
                        null,
                        null,
                        null
                    )
                    if (cursor != null) {
                        cursor.moveToFirst()
                        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                        val picturePath = cursor.getString(columnIndex)
                        processImage(picturePath)
                        cursor.close()
                    }
                }
            }
        }
    }

    private fun processImage(picturePath: String) {
        binding!!.imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath))
        mCurrentPhotoPath = picturePath
        recentPhotoPath = picturePath
        val f = File(mCurrentPhotoPath!!)
        mainActivityViewModel.compressImage(f)
        mainActivityViewModel.setImageFile(f)
        binding!!.fileTypeTextView.text = f.name
        setOptionVisible()
    }

    private fun setOptionVisible() {
        binding!!.previewButton.visibility = View.VISIBLE
        binding!!.submitButton.visibility = View.VISIBLE
    }
}