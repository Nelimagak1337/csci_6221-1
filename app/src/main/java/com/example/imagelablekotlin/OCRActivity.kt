package com.example.imagelablekotlin

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.graphics.Bitmap
import android.os.Bundle

import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

import android.view.View
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import android.content.Intent
import android.app.Activity
import android.net.Uri
import android.provider.MediaStore
import java.io.IOException
import com.google.android.gms.vision.text.TextRecognizer
import android.widget.Toast
import com.google.android.gms.vision.Frame
import android.util.SparseArray
import com.google.android.gms.vision.text.TextBlock
import java.lang.StringBuilder
import android.content.ClipData
import android.content.ClipboardManager

class OCRActivity : AppCompatActivity() {
    var button_capture: Button? = null
    var button_copy: Button? = null
    var textview_data: TextView? = null
    var bitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocractivity)
        button_capture = findViewById(R.id.button_capture)
        button_copy = findViewById(R.id.button_copy)
        textview_data = findViewById(R.id.text_data)
        if (ContextCompat.checkSelfPermission(
                this@OCRActivity,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@OCRActivity, arrayOf(
                    Manifest.permission.CAMERA
                ), REQUEST_CAMERA_CODE
            )
        }
        button_capture?.setOnClickListener(View.OnClickListener { // Allows the crop image activity and ask user to choose where the image will be imported from
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this@OCRActivity)
        })
        button_copy?.setOnClickListener(View.OnClickListener { // Copy the text to the clipboard
            val scanned_text = textview_data?.getText().toString()
            copyToClipBoard(scanned_text)
        })
    }

    // to capture the image result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            // get the cropped image from the data
            val result = CropImage.getActivityResult(data)
            // check for the result code
            if (resultCode == RESULT_OK) {
                // Create Uri for the result
                val resultUri = result.uri
                // Create a bitmap for Uri
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, resultUri)
                    getTextFromImage(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Start OCR
    private fun getTextFromImage(bitmap: Bitmap?) {
        // Build text recognizer
        val recognizer = TextRecognizer.Builder(this).build()
        //check if text recognizer is ready
        if (!recognizer.isOperational) {
            Toast.makeText(this@OCRActivity, "Text recognizer not functional", Toast.LENGTH_SHORT)
                .show()
        } else {
            // If working well, extract the text from the image
            val frame = Frame.Builder().setBitmap(bitmap).build()
            val textBlockSparseArray = recognizer.detect(frame)
            val stringBuilder = StringBuilder()
            // travers all items in the txtblcsparsearry to add to the string builder
            for (i in 0 until textBlockSparseArray.size()) {
                val textBlock = textBlockSparseArray.valueAt(i)
                stringBuilder.append(textBlock.value)
                stringBuilder.append("\n")
            }
            textview_data!!.text = stringBuilder.toString()
            // when the "capture" button has been clicked, two new buttons occur
            button_capture!!.text = "Retake"
            button_copy!!.visibility = View.VISIBLE
        }
    }

    private fun copyToClipBoard(text: String) {
        val clipBoard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied data", text)
        clipBoard.setPrimaryClip(clip)
        Toast.makeText(this@OCRActivity, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_CAMERA_CODE = 100
    }
}