package pn.app.qrcodescanner.bottomnavbar.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import pn.app.qrcodescanner.R

class QrcodeScanFragment : Fragment() {
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var urlfield : TextView
    private lateinit var previevView : PreviewView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun startcameraPreview() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {
        val previewView = view?.findViewById<PreviewView>(R.id.previewView)
        var preview : Preview = Preview.Builder()
            .build()

        var cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(previewView?.surfaceProvider)

        var camera = cameraProvider?.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)
    }

    private fun getCameraPermission()  {
        val permissions = listOf<String>(android.Manifest.permission.CAMERA)
        if (ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions.toTypedArray(),101)
        } else {
            startcameraPreview()
        }
    }

    private fun scanQr(bitmap : Bitmap)  {
        var resultUrl = ""
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC,
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_CODE_93,
                Barcode.FORMAT_CODABAR,
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_ITF,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_PDF417,
                Barcode.FORMAT_DATA_MATRIX)
            .build()

        val image = InputImage.fromBitmap(bitmap, 0)
        val scanner = BarcodeScanning.getClient()
        val result = scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val bounds = barcode.boundingBox
                    val corners = barcode.cornerPoints

                    val rawValue = barcode.rawValue

                    val valueType = barcode.valueType
                    when (valueType) {
                        Barcode.TYPE_URL -> {
                            val title = barcode.url!!.title
                            val url = barcode.url!!.url
                            if (url != null) {
                                resultUrl = url
                                urlfield.text = resultUrl
                            }
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),"Invalid barcode",Toast.LENGTH_LONG).show()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0]==PackageManager.PERMISSION_GRANTED) {
            startcameraPreview()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_qrcode_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scanbtn = view.findViewById<Button>(R.id.button)
        urlfield = view.findViewById(R.id.urlfield)
        val copybtn = view.findViewById<FloatingActionButton>(R.id.copybtn)
        val clearbtn = view.findViewById<Button>(R.id.clearbtn)
        val openurlbtn = view.findViewById<FloatingActionButton>(R.id.openurl)
        previevView = view.findViewById(R.id.previewView)
        getCameraPermission()
        scanbtn.setOnClickListener {
            val bitmap = previevView.bitmap
            Log.i("qrresult","cicked btn")
            if (bitmap != null) {
                scanQr(bitmap)
            }
        }

        copybtn.setOnClickListener {
            if (urlfield.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(),"no url",Toast.LENGTH_LONG).show()
            } else {
                val clipboard = (requireActivity()).getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("url",urlfield.text))
                Toast.makeText(requireContext(),"copied ${urlfield.text}",Toast.LENGTH_LONG).show()
            }
        }

        clearbtn.setOnClickListener {
            urlfield.text = ""
        }

        openurlbtn.setOnClickListener {
            if (!urlfield.text.isNullOrEmpty()) {
                val openurlIntent = Intent(android.content.Intent.ACTION_VIEW)
                openurlIntent.data = Uri.parse(urlfield.text.toString())
                startActivity(openurlIntent)
            }
        }
    }

}
