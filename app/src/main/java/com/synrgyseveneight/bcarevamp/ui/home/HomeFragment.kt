package com.synrgyseveneight.bcarevamp.ui.home

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.ui.comingsoon.ComingSoonFragment
import com.synrgyseveneight.bcarevamp.ui.common.HorizontalSpaceItemDecoration
import com.synrgyseveneight.bcarevamp.ui.info.MutationFragment
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var favoriteTransactionAdapter: FavoriteTransactionAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val balanceCheckText = view.findViewById<TextView>(R.id.saldoAmount)
        val accountNumberText = view.findViewById<TextView>(R.id.accountNumber)

//        Add dots in balance rupiah
        val cleanString = balanceCheckText.text.toString().replace(".", "")
        // Parse it into a Double
        val parsed = cleanString.toDouble()
        // Format it using NumberFormat
        val formatted = NumberFormat.getNumberInstance(Locale.GERMANY).format(parsed)
        // Set the formatted number as the text of the TextView
        balanceCheckText.text = Editable.Factory.getInstance().newEditable(formatted)

        // Sample data
        val transactions = listOf(
            FavoriteTransactionAdapter.Transaction(R.drawable.icon_transfer, "Transfer", "Transfer Antar Rekening BCA", "Naysilla Hani"),
            FavoriteTransactionAdapter.Transaction(R.drawable.icon_wallet, "Top Up", "Top Up OVO", "Aditya Syarif"),
            FavoriteTransactionAdapter.Transaction(R.drawable.icon_wallet, "Top Up", "Top Up ShopeePay", "Dwi Kurniawan")
        )

        // Initialize RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.fav_transaction_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        favoriteTransactionAdapter = FavoriteTransactionAdapter(transactions)
        recyclerView.adapter = favoriteTransactionAdapter

        //Tambahkan Spcing di RecyclerView
        val spaceDecoration = HorizontalSpaceItemDecoration(16)
        recyclerView.addItemDecoration(spaceDecoration)

        // Navigasi Quick Menu
        val transferButton = view.findViewById<ImageView>(R.id.transferButton)
        val transferTitle = view.findViewById<TextView>(R.id.transferTitle)

        val navController = findNavController()

        val clickListener = View.OnClickListener {
            navController.navigate(R.id.action_homeFragment_to_transferOptionFragment)
        }

        transferButton.setOnClickListener(clickListener)
        transferTitle.setOnClickListener(clickListener)

        return view
    }

}