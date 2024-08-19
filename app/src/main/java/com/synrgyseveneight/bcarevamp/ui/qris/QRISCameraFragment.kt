package com.synrgyseveneight.bcarevamp.ui.qris

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.data.datastore.AuthDataStore
import com.synrgyseveneight.bcarevamp.data.network.RetrofitClient
import com.synrgyseveneight.bcarevamp.data.repository.AuthRepository
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModel
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModelFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QRISCameraFragment : Fragment() {

    private val viewModelAuth: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.instance, AuthDataStore.getInstance(requireContext())))
    }

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeScanner: BarcodeScanner
    private lateinit var previewView: PreviewView

    private var isFlashOn = false
    private lateinit var flashcameratoggle: ImageView

    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null

    private val args: QRISCameraFragmentArgs by navArgs()

    private var loadingCircle: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_q_r_i_s_camera, container, false)
        previewView = view.findViewById(R.id.previewView)

        val backButton = view.findViewById<ImageView>(R.id.quitButton)
        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        val loadingCircle = view.findViewById<View>(R.id.loadingCircle)
        loadingCircle?.visibility = View.INVISIBLE

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            setupCamera()
        } else {
            requestCameraPermission()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        flashcameratoggle = view.findViewById<ImageView>(R.id.flashButton)
        // flash camera
        flashcameratoggle.setOnClickListener {
            Log.d("QRISCameraFragment", "Flash button clicked")
            toggleFlash()
        }

        // Initialize flashcameratoggle
        flashcameratoggle = view.findViewById<ImageView>(R.id.flashButton)

        // Initialize ImageCapture
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        imageCapture = ImageCapture.Builder().build()

        // Set flashcameratoggle click listener
        flashcameratoggle.setOnClickListener {
            imageCapture?.flashMode = if (imageCapture?.flashMode == ImageCapture.FLASH_MODE_ON) {
                ImageCapture.FLASH_MODE_OFF
            } else {
                ImageCapture.FLASH_MODE_ON
            }
        }
    }

    // flash camera
    private fun toggleFlash() {
        if (camera?.cameraInfo?.hasFlashUnit() == true) {
            isFlashOn = if (isFlashOn) {
                // Turn off the torch
                camera?.cameraControl?.enableTorch(false)
                flashcameratoggle.setImageResource(R.drawable.button_flash_whiteborder)
                Log.d("QRISCameraFragment", "Torch off")
                false
            } else {
                // Turn on the torch
                camera?.cameraControl?.enableTorch(true)
                flashcameratoggle.setImageResource(R.drawable.button_flash_white)
                Log.d("QRISCameraFragment", "Torch on")
                true
            }
        } else {
            Toast.makeText(requireContext(), "Lampu kilat tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        barcodeScanner = BarcodeScanning.getClient()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalyzer = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(cameraExecutor, { imageProxy ->
                    processImageProxy(imageProxy)
                })
            }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )

        }, ContextCompat.getMainExecutor(requireContext()))
    }



    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return
        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        try {
            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { qrCode ->
                            if (barcode.valueType == Barcode.TYPE_TEXT) {
                                val idQris = qrCode
                                Log.d("QRISCameraFragment", "QR Code: $idQris" )
                                    findNavController().navigate(
                                        QRISCameraFragmentDirections.actionQRISCameraFragmentToQRISNominalInputFragment(
                                            idQris,
                                            "0",
                                            "0",
                                            "0",
                                            "0",
                                            "0",
                                            "0",
                                            "0",
                                            "0",
                                            "0",
                                            "0",
                                            "0"
                                        )
                                    )


                                return@addOnSuccessListener
                            }
                        }
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } catch (e: Exception) {
            Log.e("QRISCameraFragment", "Error processing image", e)
            findNavController().navigate(R.id.action_QRISCameraFragment_to_homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }

    private fun requestCameraPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CAMERA_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    setupCamera()
                } else {
                    Toast.makeText(requireContext(), "Izin kamera dibutuhkan", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(QRISCameraFragmentDirections.actionQRISCameraFragmentToHomeFragment())
                }
                return
            }
        }
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 10
    }

}