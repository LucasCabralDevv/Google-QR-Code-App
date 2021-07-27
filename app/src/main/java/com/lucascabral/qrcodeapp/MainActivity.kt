package com.lucascabral.qrcodeapp

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.lucascabral.qrcodeapp.databinding.ActivityMainBinding
import java.lang.Exception
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var analyzer: MyImageAnalyzer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        analyzer = MyImageAnalyzer(supportFragmentManager)

        this.window.setFlags(1024, 1024)
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != (PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CAMERA), 101)
                } else {
                    val processCameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                    bindPreview(processCameraProvider)
                }
            } catch (ex: ExecutionException) {
                ex.printStackTrace()
            } catch (ex: InterruptedException) {
                ex.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty()) {
            val processCameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            bindPreview(processCameraProvider)
        }
    }

    private fun bindPreview(processCameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder().build()
        val cameraSelector: CameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        preview.setSurfaceProvider(binding.mainPreviewView.surfaceProvider)
        val imageCapture = ImageCapture.Builder().build()
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(cameraExecutor, analyzer)
        processCameraProvider.unbindAll()
        processCameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis)
    }
}