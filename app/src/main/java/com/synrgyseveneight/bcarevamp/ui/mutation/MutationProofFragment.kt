package com.synrgyseveneight.bcarevamp.ui.mutation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.synrgyseveneight.bcarevamp.R

class MutationProofFragment : Fragment() {

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

        val receivedDate = arguments?.getString("date")
        val displayedDate = if (receivedDate.isNullOrEmpty()) "Saring Pencarian" else receivedDate
        btnToFilter.text = displayedDate

        btnToFilter.setOnClickListener{
            findNavController().navigate(R.id.action_mutationProofFragment_to_mutationOptionFilterFragment)
        }
        btnToMutationHistory.setOnClickListener{
            findNavController().navigate(R.id.action_mutationProofFragment_to_mutationHistoryFragment)
        }
    }
}

