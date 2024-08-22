package com.synrgyseveneight.bcarevamp.ui.qris

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
import androidx.core.content.res.ResourcesCompat
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
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QRISCameraFragment : Fragment() {

    private val viewModelAuth: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.instance, AuthDataStore.getInstance(requireContext())))
    }

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeScanner: BarcodeScanner
    private lateinit var previewView: PreviewView

    // gallery scan
    private val PICK_IMAGE_REQUEST_CODE = 101

    private var isFlashOn = false

    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null

    private val args: QRISCameraFragmentArgs by navArgs()

    private var loadingCircle: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        Toast.makeText(requireContext(), "Scan QRIS", Toast.LENGTH_SHORT).show()
        val view = inflater.inflate(R.layout.fragment_q_r_i_s_camera, container, false)
        val qrisShowButton = view.findViewById<ImageView>(R.id.button_Qristampil)

        qrisShowButton.setOnClickListener {
           showCustomToast("Mohon maaf, layanan belum tersedia.")
        }

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

        val flashcameraButton = view.findViewById<ImageView>(R.id.flashButton)
        val galleryscanbutton = view.findViewById<ImageView>(R.id.galleryButton)

        // Scan QRIS from gallery
        galleryscanbutton.setOnClickListener {
            Log.d("QRISuCameraFragment", "Gallery button clicked")
            openGallery()
        }

        // Initialize ImageCapture
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        imageCapture = ImageCapture.Builder().build()

        // Set flashcameratoggle click listener
        flashcameraButton.setOnClickListener {
            Log.d("QRISCameraFragment", "Flash button clicked")
            toggleFlash()
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

            imageCapture = ImageCapture.Builder().build()

            val imageAnalyzer = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(cameraExecutor, { imageProxy ->
                    processImageProxy(imageProxy)
                })
            }

            // Bind everything to the lifecycle, including ImageCapture
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture, imageAnalyzer
            )

            // Test turning on the torch immediately after binding
            camera?.cameraControl?.enableTorch(false)

        }, ContextCompat.getMainExecutor(requireContext()))
    }


    private fun toggleFlash() {
        val flashimageicon = view?.findViewById<ImageView>(R.id.flashButton)
        camera?.let { camera ->
            if (camera.cameraInfo.hasFlashUnit()) {
                isFlashOn = if (isFlashOn) {
                    // Turn off the torch
                    camera.cameraControl.enableTorch(false)
                    Log.d("QRISCameraFragment", "Torch turned off")
                    flashimageicon?.setImageResource(R.drawable.button_flash_whiteborder)
                    flashimageicon?.announceForAccessibility("Lampu kamera mati")
                    false
                } else {
                    // Turn on the torch
                    camera.cameraControl.enableTorch(true)
                    Log.d("QRISCameraFragment", "Torch turned on")
                    flashimageicon?.setImageResource(R.drawable.button_flash_white)
                    flashimageicon?.announceForAccessibility("Lampu kamera menyala")
                    true
                }
            } else {
                Toast.makeText(requireContext(), "Flash not available on this device", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // scan from gallery
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                scanImageFromUri(imageUri)
            }
        }
    }

    private fun scanImageFromUri(imageUri: Uri) {
        try {
            val inputImage = InputImage.fromFilePath(requireContext(), imageUri)
            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    processBarcodes(barcodes)
                }
                .addOnFailureListener { e ->
                    Log.e("QRISCameraFragment", "Error scanning image from gallery", e)
                    Toast.makeText(requireContext(), "Failed to scan the image", Toast.LENGTH_SHORT).show()
                }
        } catch (e: IOException) {
            Log.e("QRISCameraFragment", "Error reading image from gallery", e)
        }
    }

    private fun processBarcodes(barcodes: List<Barcode>) {
        for (barcode in barcodes) {
            barcode.rawValue?.let { qrCode ->
                if (barcode.valueType == Barcode.TYPE_TEXT) {
                    val idQris = qrCode
                    Log.d("QRISCameraFragment", "QR Code: $idQris")
                    findNavController().navigate(
                        QRISCameraFragmentDirections.actionQRISCameraFragmentToQRISNominalInputFragment(
                            idQris,
                            "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"
                        )
                    )
                    return
                }
            }
        }
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
                    Handler(Looper.getMainLooper()).postDelayed({
                        setupCamera()
                    }, 1000)
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

    @SuppressLint("ResourceAsColor")
    private fun showCustomToast(message: String){

        //create linear layout
        val customToastLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 16, 16, 16)
            background = ContextCompat.getDrawable(requireContext(), R.drawable.toast_background)
            elevation = 10f
        }

        val toastIcon = ImageView(requireContext()).apply {
            setImageResource(R.drawable.icon_toast)
            setPadding(0, 0, 16, 0)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_VERTICAL
                setMargins(24, 0, 24, 0)
            }
        }

        val typefaces = ResourcesCompat.getFont(requireContext(), R.font.nunitoregular)


        //text view for toast message
        val toastTextView = TextView(requireContext()).apply {
            text = message
            setTextColor(R.color.darkBlue)
            textSize = 16f
            typeface = typefaces

        }

        customToastLayout.addView(toastIcon)
        customToastLayout.addView(toastTextView)

        with(Toast(requireContext())){
            duration = Toast.LENGTH_SHORT
            view = customToastLayout
            setGravity(Gravity.CENTER, 0,400)
            show()
        }

        //accessibility
        customToastLayout.announceForAccessibility(message)

    }

}