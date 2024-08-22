package com.synrgyseveneight.bcarevamp.ui.transfer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.synrgyseveneight.bcarevamp.R

class TransferOptionFragment : Fragment() {

    private lateinit var transferOptionAdapter: TransferOptionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transfer_option, container, false)

        // Sample data
        val transferoptions = listOf(
            TransferOptionAdapter.TransferOption(R.drawable.illust_tfantarbca, "Transfer Antar BCA", "Transfer dana ke sesama rekening BCA"),
            TransferOptionAdapter.TransferOption(R.drawable.illust_tfbanklain, "Transfer ke Bank Lain", "Transfer dana ke bank lain dalam negeri"),
            TransferOptionAdapter.TransferOption(R.drawable.illust_proxytf, "Transfer Proxy Adress", "Transfer dana melalui Proxy Adress"),
            TransferOptionAdapter.TransferOption(R.drawable.illust_tf_va, "Virtual Account", "Transfer dana ke rekening Virtual Account BCA"),
            TransferOptionAdapter.TransferOption(R.drawable.illust_tfsakuku, "Sakuku", "Transfer dana ke nomor Sakuku"),
            TransferOptionAdapter.TransferOption(R.drawable.illust_impordaftartf, "Impor Daftar Transfer", "Transfer dana ke nomor Sakuku"),
        )

        // Initialize RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.transferOptions_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)
        transferOptionAdapter = TransferOptionAdapter(transferoptions)  { transferOption ->
            when (transferOption.title) {
                "Transfer Antar BCA" -> findNavController().navigate(R.id.action_transferOptionFragment_to_transferListFragment)
                // Add other navigation actions based on the title
                else -> showCustomToast("Mohon maaf, layanan belum tersedia")
            }
        }
        recyclerView.adapter = transferOptionAdapter

        // Back button
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        return view
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