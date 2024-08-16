package com.synrgyseveneight.bcarevamp.ui.mutation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
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

class MutationProofFragment : Fragment() {
    private  lateinit var proofAdapter: MutationProofAdapter

    private val viewModelAuth: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.instance, AuthDataStore.getInstance(requireContext())))
    }

    private val viewModelProof: MutationViewModel by viewModels {
        MutationViewModelFactory(MutationRepository(RetrofitClient.instance))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mutation_proof, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnToMutationHistory = view.findViewById<ConstraintLayout>(R.id.switch_button_history)
        val btnToFilter = view.findViewById<Button>(R.id.option_filter)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview_proof)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        val receivedDate = arguments?.getString("date")
        val displayedDate = if (receivedDate.isNullOrEmpty()) "Saring Pencarian" else receivedDate
        btnToFilter.text = displayedDate

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        proofAdapter = MutationProofAdapter(emptyList())
        recyclerView.adapter = proofAdapter

        viewModelProof.mutationData.observe(viewLifecycleOwner, Observer { mutationList ->
            proofAdapter.updateData(mutationList)
        })

        viewModelProof.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        })
        viewModelProof.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            progressBar.visibility =
                if (isLoading)
                    View.VISIBLE
                else
                    View.GONE
        })

        viewModelAuth.userToken.observe(viewLifecycleOwner) { token ->
            val requestBody = MutationRequest(
                startDate = "2024-08-01",
                endDate = "2024-08-16",
                transactionCategory = "ALL_TRANSACTIONS"
            )
            viewModelProof.fetchMutations(page = 0, size = 10, token = "$token", requestBody = requestBody)
        }

        btnToFilter.setOnClickListener{
            findNavController().navigate(R.id.action_mutationProofFragment_to_mutationOptionFilterFragment)
        }
        btnToMutationHistory.setOnClickListener{
            findNavController().navigate(R.id.action_mutationProofFragment_to_mutationHistoryFragment)
        }
    }
}

