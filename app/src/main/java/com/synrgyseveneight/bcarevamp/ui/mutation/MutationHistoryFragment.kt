package com.synrgyseveneight.bcarevamp.ui.mutation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.synrgyseveneight.bcarevamp.R

class MutationHistoryFragment : Fragment() {
    private lateinit var historyAdapter: MutationHistoryAdapter

    private val mutationItems = listOf(
        MutationHistoryAdapter.MutationItem("******1234", "Transfer", "- Rp 50.000", "10:00 WIB"),
        MutationHistoryAdapter.MutationItem("******5678", "Pembelian", "- Rp 200.000", "09:30 WIB"),
        MutationHistoryAdapter.MutationItem("******9012", "Top Up", "+ Rp 1.000.000", "08:45 WIB"),
        MutationHistoryAdapter.MutationItem("******1234", "Transfer", "- Rp 50.000", "10:00 WIB"),
        MutationHistoryAdapter.MutationItem("******5678", "Pembelian", "- Rp 200.000", "09:30 WIB"),
        MutationHistoryAdapter.MutationItem("******9012", "Top Up", "+ Rp 1.000.000", "08:45 WIB"),
        MutationHistoryAdapter.MutationItem("******1234", "Transfer", "- Rp 50.000", "10:00 WIB"),
        MutationHistoryAdapter.MutationItem("******5678", "Pembelian", "- Rp 200.000", "09:30 WIB"),
        MutationHistoryAdapter.MutationItem("******9012", "Top Up", "+ Rp 1.000.000", "08:45 WIB")

        // ... tambahkan item lainnya sesuai kebutuhan
    )
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

        val receivedDate = arguments?.getString("date")
        val displayedDate = if (receivedDate.isNullOrEmpty()) "Saring Pencarian" else receivedDate
        btnToFilter.text = displayedDate


        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        historyAdapter = MutationHistoryAdapter(mutationItems)
        recyclerView.adapter = historyAdapter


        btnToFilter.setOnClickListener{
            findNavController().navigate(R.id.action_mutationHistoryFragment_to_mutationOptionFilterFragment)
        }
        btnToMutationProof.setOnClickListener{
            findNavController().navigate(R.id.action_mutationHistoryFragment_to_mutationProofFragment)
        }

    }

}