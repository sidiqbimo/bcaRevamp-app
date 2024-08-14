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

class MutationHistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mutation_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnToMutationProof = view.findViewById<ConstraintLayout>(R.id.switch_button_proof)
        val btnToFilter = view.findViewById<Button>(R.id.option_filter)

        btnToFilter.setOnClickListener{
            findNavController().navigate(R.id.action_mutationHistoryFragment_to_mutationOptionFilterFragment)
        }
        btnToMutationProof.setOnClickListener{
            findNavController().navigate(R.id.action_mutationHistoryFragment_to_mutationProofFragment)
        }




    }
}