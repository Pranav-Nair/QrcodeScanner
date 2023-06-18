package pn.app.qrcodescanner.bottomnavbar.fragments

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.camera.core.impl.utils.ContextUtil.getApplicationContext
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.g0dkar.qrcode.QRCode
import pn.app.qrcodescanner.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class QrCodeGenerator : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qr_code_generator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val generatebtn = view.findViewById<Button>(R.id.makeqr)
        val clearbtn = view.findViewById<Button>(R.id.clearbtn)
        val urlfield = view.findViewById<EditText>(R.id.urlfieldet)
        val qriv = view.findViewById<ImageView>(R.id.qrcodeiv)
        val sharebtn = view.findViewById<FloatingActionButton>(R.id.sharebtn)

        clearbtn.setOnClickListener {
            qriv.setImageResource(R.drawable.ic_launcher_qrcode_foreground)
            urlfield.text.clear()
            removeTempfiles()
        }

        generatebtn.setOnClickListener {
            if (!urlfield.text.isNullOrEmpty()) {
                val qrcode = QRCode(urlfield.text.toString())
                val image : Bitmap = qrcode.render().nativeImage() as Bitmap
                saveToInternalStorage(image)
                qriv.setImageBitmap(image)

            }
        }

        sharebtn.setOnClickListener {
            val folderName = "${requireActivity().applicationContext.filesDir}/tmp/qrcodes"
            val directory = File(folderName)
            directory.mkdirs()
            val fileName = "qrcode.png"
            val file = File(directory, fileName)
            if (file.exists()) {
            val qrimg = FileProvider.getUriForFile(requireContext(),requireContext().packageName+".provider", file)
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    // Example: content://com.google.android.apps.photos.contentprovider/...
                    putExtra(Intent.EXTRA_STREAM, qrimg)
                    type = "image/png"
                }
                requireContext().startActivity(Intent.createChooser(shareIntent, "share"))
            }
        }
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap) {
        val folderName = "${requireActivity().applicationContext.filesDir}/tmp/qrcodes"
        val directory = File(folderName)
        directory.mkdirs()
        val fileName = "qrcode.png"
        val file = File(directory, fileName)

// Save the image to the file
        try {
            val outputStream = FileOutputStream(file)
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            // File saved successfully
        } catch (e: Exception) {
            e.printStackTrace()
            // Error saving the file
        }
    }

    private fun removeTempfiles() {
        val folderName = "${requireActivity().applicationContext.filesDir}/tmp/qrcodes"
        Log.i("cleaned","$folderName")
        val folder = File(folderName)
        if (folder.exists() && folder.isDirectory) {
            val files = folder.listFiles()
            for (file in files) {
                if (file.isFile) {
                    file.delete()
                }
            }
        }
    }
}