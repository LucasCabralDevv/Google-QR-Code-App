package com.lucascabral.qrcodeapp

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.fragment.app.FragmentManager
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class MyImageAnalyzer(fragmentManager: FragmentManager) : ImageAnalysis.Analyzer {

    private var bottomDialog: BottomDialog = BottomDialog()
    private var _fragmentManager: FragmentManager = fragmentManager

    override fun analyze(image: ImageProxy) {
        scanBarCode(image)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun scanBarCode(image: ImageProxy) {
        val mediaImage = image.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_QR_CODE,
                    Barcode.FORMAT_AZTEC)
                .build()
            val scanner = BarcodeScanning.getClient(options)
            val result = scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    readerBarcodeData(barcodes)
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    // ...
                }
                .addOnCompleteListener {
                    mediaImage.close()
                }
        }
    }

    private fun readerBarcodeData(barcodes: List<Barcode>) {
        for (barcode in barcodes) {
            val bounds = barcode.boundingBox
            val corners = barcode.cornerPoints

            val rawValue = barcode.rawValue

            // See API reference for complete list of supported types
            when (barcode.valueType) {
                Barcode.TYPE_WIFI -> {
                    val ssid = barcode.wifi!!.ssid
                    val password = barcode.wifi!!.password
                    val type = barcode.wifi!!.encryptionType
                }
                Barcode.TYPE_URL -> {
                    if (!bottomDialog.isAdded) {
                        bottomDialog.show(_fragmentManager, "")
                    }
                    bottomDialog.fetchUrl(barcode.url.url)
                    val title = barcode.url!!.title
                    val url = barcode.url!!.url
                }
            }
        }
    }
}