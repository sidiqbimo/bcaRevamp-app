package com.synrgyseveneight.bcarevamp.ui.mutation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.synrgyseveneight.bcarevamp.R

class MutationProofFragment : Fragment() {
    private  lateinit var proofAdapter: MutationProofAdapter

    private val mutationProofItems = listOf(
        MutationProofAdapter.MutationProofItem("10-08-2024", "No Reff: 12345", "Transfer", "Sukses", "******5678", "Rp 300.000"),
        MutationProofAdapter.MutationProofItem("05-08-2024", "No Reff: 67890", "Pembelian", "Gagal", "******9012", "Rp 80.000"),
        // ... tambahkan item lainnya sesuai kebutuhan
    )
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

        val receivedDate = arguments?.getString("date")
        val displayedDate = if (receivedDate.isNullOrEmpty()) "Saring Pencarian" else receivedDate
        btnToFilter.text = displayedDate

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        proofAdapter = MutationProofAdapter(mutationProofItems)
        recyclerView.adapter = proofAdapter

        btnToFilter.setOnClickListener{
            findNavController().navigate(R.id.action_mutationProofFragment_to_mutationOptionFilterFragment)
        }
        btnToMutationHistory.setOnClickListener{
            findNavController().navigate(R.id.action_mutationProofFragment_to_mutationHistoryFragment)
        }
    }
}

