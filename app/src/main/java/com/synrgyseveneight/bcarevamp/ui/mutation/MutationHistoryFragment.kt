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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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

class MutationHistoryFragment : Fragment() {
    private lateinit var historyAdapter: MutationHistoryAdapter

    private val viewModelHistory: MutationViewModel by viewModels {
        MutationViewModelFactory(MutationRepository(RetrofitClient.instance))
    }

    private val viewModelAuth: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.instance, AuthDataStore.getInstance(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mutation_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview_history)
        val btnToMutationProof = view.findViewById<ConstraintLayout>(R.id.switch_button_proof)
        val btnToFilter = view.findViewById<Button>(R.id.option_filter)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        val receivedDate = arguments?.getString("date")
        val displayedDate = if (receivedDate.isNullOrEmpty()) "Saring Pencarian" else receivedDate
        btnToFilter.text = displayedDate

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        historyAdapter = MutationHistoryAdapter(emptyList())
        recyclerView.adapter = historyAdapter

        viewModelHistory.mutationData.observe(viewLifecycleOwner, Observer { mutationList ->
            historyAdapter.updateData(mutationList)
        })

        viewModelHistory.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        })
        viewModelHistory.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
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
            viewModelHistory.fetchMutations(page = 0, size = 10, token = "$token", requestBody = requestBody)
        }


        btnToFilter.setOnClickListener{
            findNavController().navigate(R.id.action_mutationHistoryFragment_to_mutationOptionFilterFragment)
        }
        btnToMutationProof.setOnClickListener{
            findNavController().navigate(R.id.action_mutationHistoryFragment_to_mutationProofFragment)
        }

    }

}