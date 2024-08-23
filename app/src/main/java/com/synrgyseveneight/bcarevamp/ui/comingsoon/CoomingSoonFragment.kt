package com.synrgyseveneight.bcarevamp.ui.comingsoon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.synrgyseveneight.bcarevamp.R

class CoomingSoonFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_comingsoon, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textTitle: TextView = view.findViewById(R.id.title_tab)
        textTitle.text = "Coming Soon"

    }
}
