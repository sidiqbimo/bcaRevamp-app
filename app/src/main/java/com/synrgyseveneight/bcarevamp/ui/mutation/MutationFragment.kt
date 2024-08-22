package com.synrgyseveneight.bcarevamp.ui.mutation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.data.datastore.AuthDataStore
import com.synrgyseveneight.bcarevamp.data.model.MutationRequest
import com.synrgyseveneight.bcarevamp.data.network.RetrofitClient
import com.synrgyseveneight.bcarevamp.data.repository.AuthRepository
import com.synrgyseveneight.bcarevamp.data.repository.MutationRepository
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModel
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModelFactory
import com.synrgyseveneight.bcarevamp.viewmodel.MutationViewModel
import com.synrgyseveneight.bcarevamp.viewmodel.MutationViewModelFactory
import java.io.File
import java.time.LocalDate

class MutationFragment : Fragment() {
    private lateinit var historyAdapter: MutationHistoryAdapter
    private  lateinit var proofAdapter: MutationProofAdapter
    private var pageHistoryActive = true

    private val viewModelMutation: MutationViewModel by viewModels {
        MutationViewModelFactory(MutationRepository(RetrofitClient.instance))

    }

    private val viewModelAuth: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.instance, AuthDataStore.getInstance(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mutation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnSwitchHistory = view.findViewById<ConstraintLayout>(R.id.switch_button_history)
        val btnSwitchProof = view.findViewById<ConstraintLayout>(R.id.switch_button_proof)
        val textSwitchHistory = view.findViewById<TextView>(R.id.text_switch_history)
        val textSwitchProof = view.findViewById<TextView>(R.id.text_switch_proof)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        val btnToFilter = view.findViewById<MaterialButton>(R.id.option_filter)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val imageNotFound = view.findViewById<ImageView>(R.id.img_trans_not_found)
        val accountNumber = view.findViewById<TextView>(R.id.account_number)
        val buttonDownload = view.findViewById<MaterialButton>(R.id.button_download)
        val receivedDate = arguments?.getString("dateOption")
        val receivedDateStart = arguments?.getString("dateStart")
        val receivedDateEnd = arguments?.getString("dateEnd")
        val receivedCategory = arguments?.getString("dataCategory")
        val receivedScreen = arguments?.getString("screen")


        setFilterButtonState(receivedDate, btnToFilter)
        val displayedDate = if (receivedDate.isNullOrEmpty()) "Saring Pencarian" else receivedDate
        val categoryTransaction = if (receivedCategory.isNullOrEmpty()) "ALL_TRANSACTIONS" else receivedCategory
        btnToFilter.text = displayedDate

        if (receivedScreen == "screen1") {
            pageHistoryActive = true
        }else if(receivedScreen == "screen2"){
            pageHistoryActive = false
        }


        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        historyAdapter = MutationHistoryAdapter(emptyList())
        proofAdapter = MutationProofAdapter(emptyList())


        if (pageHistoryActive) {
            recyclerView.adapter = historyAdapter
            setSwitchButtonState(btnSwitchHistory, textSwitchHistory, btnSwitchProof, textSwitchProof, buttonDownload, true)
        }else{
            recyclerView.adapter = proofAdapter
            setSwitchButtonState(btnSwitchHistory, textSwitchHistory, btnSwitchProof, textSwitchProof, buttonDownload, false)
        }

        viewModelAuth.userAccountNumber.observe(viewLifecycleOwner) {
            accountNumber.text = formattedAccountNumber(it?: "Gagal memuat")
        }

        viewModelMutation.mutationResponseData.observe(viewLifecycleOwner, Observer { mutationList ->
            historyAdapter.updateData(mutationList)
            proofAdapter.updateData(mutationList)
            val isEmpty = mutationList.isEmpty()
            recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
            imageNotFound.visibility = if (isEmpty) View.VISIBLE else View.GONE
        })

        viewModelMutation.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        })
        viewModelMutation.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        })

        if (receivedDateStart != null && receivedDateEnd != null) {
//            Toast.makeText(context, "Tanggal : $receivedDateStart - $receivedDateEnd", Toast.LENGTH_SHORT).show()
            viewModelAuth.userToken.observe(viewLifecycleOwner) { token ->
                val requestBody = MutationRequest(
                    startDate = receivedDateStart.toString(),
                    endDate = receivedDateEnd.toString(),
                    transactionCategory = categoryTransaction.toString()
                )
                viewModelMutation.fetchMutations(page = 0, size = 10000, token = "$token", requestBody = requestBody)
            }
        }else{
            val today = LocalDate.now()
            val startDate = today.withDayOfMonth(1)
            viewModelAuth.userToken.observe(viewLifecycleOwner) { token ->
//                Toast.makeText(context, "Tanggal : $startDate - $today", Toast.LENGTH_SHORT).show()
                val requestBody = MutationRequest(
                    startDate = startDate.toString(),
                    endDate = today.toString(),
                    transactionCategory = categoryTransaction.toString()
                )
                viewModelMutation.fetchMutations(page = 0, size = 10000, token = "$token", requestBody = requestBody)
            }
        }

        btnSwitchHistory.setOnClickListener {
            pageHistoryActive = true
            setSwitchButtonState(btnSwitchHistory, textSwitchHistory, btnSwitchProof, textSwitchProof, buttonDownload, true)
            recyclerView.adapter = historyAdapter
        }

        btnSwitchProof.setOnClickListener {
            pageHistoryActive = false
            setSwitchButtonState(btnSwitchHistory, textSwitchHistory, btnSwitchProof, textSwitchProof, buttonDownload, false)
            recyclerView.adapter = proofAdapter
        }

        btnToFilter.setOnClickListener {
            val screen = if (pageHistoryActive) "screen1" else "screen2"
            val action = MutationFragmentDirections.actionMutationFragmentToMutationOptionFilterFragment(screen)
            findNavController().navigate(action)
        }

        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        buttonDownload.setOnClickListener {
            viewModelAuth.userToken.observe(viewLifecycleOwner) { token ->
                if (token != null) {
                    viewModelMutation.downloadMutationReport(token, requireContext())
                }
            }
        }

        viewModelMutation.pdfFilePath.observe(viewLifecycleOwner, Observer { filePath ->
            if (filePath != null) {
                openPdfFile(filePath)
            }
        })

        viewModelMutation.downloadSuccess.observe(viewLifecycleOwner, Observer { success ->
            if (success) {
                Toast.makeText(context, "Laporan Mutasi Berhasil Didownload", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Gagal Mendownload Laporan", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setSwitchButtonState(
        btnSwitchHistory: ConstraintLayout,
        textSwitchHistory: TextView,
        btnSwitchProof: ConstraintLayout,
        textSwitchProof: TextView,
        buttonDownload: MaterialButton,
        isHistoryActive: Boolean
    ) {
        if (isHistoryActive) {
            btnSwitchHistory.background = ContextCompat.getDrawable(requireContext(), R.drawable.switch_rounded_active)
            textSwitchHistory.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            btnSwitchProof.background = null
            textSwitchProof.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            buttonDownload.visibility = View.VISIBLE
        } else {
            btnSwitchProof.background = ContextCompat.getDrawable(requireContext(), R.drawable.switch_rounded_active)
            textSwitchProof.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            btnSwitchHistory.background = null
            textSwitchHistory.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            buttonDownload.visibility = View.GONE
        }
    }

    private fun setFilterButtonState(receivedDate: String?, btnToFilter: MaterialButton){
        if (!receivedDate.isNullOrEmpty()) {

            btnToFilter.background = ContextCompat.getDrawable(requireContext(), R.drawable.bluebackground_roundedrectangle)
            btnToFilter.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            btnToFilter.icon = null
            btnToFilter.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)

        } else {

            btnToFilter.background = ContextCompat.getDrawable(requireContext(), R.drawable.whitebackground_roundedrectangle)
            btnToFilter.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            btnToFilter.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_funnel)
            btnToFilter.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
        }
    }
    private fun formattedAccountNumber (accountNumber: String): String {
        val cleanStringNomorRekening = accountNumber.replace("-", "")
        val parsedNoRek = cleanStringNomorRekening.toString()
        val formattedNoRek = parsedNoRek.replace(Regex("(\\d{4})"), "$1-")
        return if (formattedNoRek.endsWith("-")) {
            formattedNoRek.dropLast(1)
        } else {formattedNoRek}
    }

    private fun openPdfFile(filePath: String) {
        val file = File(filePath)
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(Intent.createChooser(intent, "Buka PDF Mutasi dengan..."))
    }


}