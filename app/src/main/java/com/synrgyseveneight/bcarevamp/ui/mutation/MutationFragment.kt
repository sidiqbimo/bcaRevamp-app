package com.synrgyseveneight.bcarevamp.ui.mutation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        val btnToFilter = view.findViewById<Button>(R.id.option_filter)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        val receivedDate = arguments?.getString("dateOption")
        val receivedDateStart = arguments?.getString("dateStart")
        val receivedDateEnd = arguments?.getString("dateEnd")
        val receivedScreen = arguments?.getString("screen")
        val displayedDate = if (receivedDate.isNullOrEmpty()) "Saring Pencarian" else receivedDate

        if (receivedScreen == "screen1") {
            pageHistoryActive = true
        }else if(receivedScreen == "screen2"){
            pageHistoryActive = false
        }

        btnToFilter.text = displayedDate

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        historyAdapter = MutationHistoryAdapter(emptyList())
        proofAdapter = MutationProofAdapter(emptyList())

        // Set historyAdapter sebagai default awal
        if (pageHistoryActive) {
            recyclerView.adapter = historyAdapter
            setSwitchButtonState(btnSwitchHistory, textSwitchHistory, btnSwitchProof, textSwitchProof, true)
        }else{
            recyclerView.adapter = proofAdapter
            setSwitchButtonState(btnSwitchHistory, textSwitchHistory, btnSwitchProof, textSwitchProof, false)
        }


        // Observer untuk memperbarui data di kedua adapter
        viewModelMutation.mutationData.observe(viewLifecycleOwner, Observer { mutationList ->
            historyAdapter.updateData(mutationList)
            proofAdapter.updateData(mutationList)
        })

        viewModelMutation.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        })
        viewModelMutation.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        })

        if (receivedDateStart != null && receivedDateEnd != null) {
            Toast.makeText(context, "Tanggal : $receivedDateStart - $receivedDateEnd", Toast.LENGTH_SHORT).show()
            viewModelAuth.userToken.observe(viewLifecycleOwner) { token ->
                val requestBody = MutationRequest(
                    startDate = receivedDateStart.toString(),
                    endDate = receivedDateEnd.toString(),
                    transactionCategory = "ALL_TRANSACTIONS"
                )
                viewModelMutation.fetchMutations(page = 0, size = 10, token = "$token", requestBody = requestBody)
            }
        }else{

            viewModelAuth.userToken.observe(viewLifecycleOwner) { token ->
                val requestBody = MutationRequest(
                    startDate = "2024-08-01",
                    endDate = "2024-08-16",
                    transactionCategory = "ALL_TRANSACTIONS"
                )
                viewModelMutation.fetchMutations(page = 0, size = 10, token = "$token", requestBody = requestBody)
            }
        }



        btnSwitchHistory.setOnClickListener {
            pageHistoryActive = true
            setSwitchButtonState(btnSwitchHistory, textSwitchHistory, btnSwitchProof, textSwitchProof, true)
            recyclerView.adapter = historyAdapter
        }

        btnSwitchProof.setOnClickListener {
            pageHistoryActive = false
            setSwitchButtonState(btnSwitchHistory, textSwitchHistory, btnSwitchProof, textSwitchProof, false)
            recyclerView.adapter = proofAdapter
        }

        btnToFilter.setOnClickListener {
            val screen = if (pageHistoryActive) "screen1" else "screen2"
            val action = MutationFragmentDirections.actionMutationFragmentToMutationOptionFilterFragment(screen)
            findNavController().navigate(action)
        }
    }
    private fun setSwitchButtonState(
        btnSwitchHistory: ConstraintLayout,
        textSwitchHistory: TextView,
        btnSwitchProof: ConstraintLayout,
        textSwitchProof: TextView,
        isHistoryActive: Boolean
    ) {
        if (isHistoryActive) {
            btnSwitchHistory.background = ContextCompat.getDrawable(requireContext(), R.drawable.switch_rounded_active)
            textSwitchHistory.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            btnSwitchProof.background = null
            textSwitchProof.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        } else {
            btnSwitchProof.background = ContextCompat.getDrawable(requireContext(), R.drawable.switch_rounded_active)
            textSwitchProof.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            btnSwitchHistory.background = null
            textSwitchHistory.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }

}