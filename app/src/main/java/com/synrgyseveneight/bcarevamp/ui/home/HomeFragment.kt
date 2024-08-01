package com.synrgyseveneight.bcarevamp.ui.home

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bcarevamp.data.DataStoreManager
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.data.model.Account
import com.synrgyseveneight.bcarevamp.ui.comingsoon.ComingSoonFragment
import com.synrgyseveneight.bcarevamp.ui.common.HorizontalSpaceItemDecoration
import com.synrgyseveneight.bcarevamp.ui.info.MutationFragment
import com.synrgyseveneight.bcarevamp.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var favoriteTransactionAdapter: FavoriteTransactionAdapter
    private lateinit var homeViewModel : HomeViewModel
    private lateinit var dataStoreManager : DataStoreManager

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myaccountnumber = "0000000000"
        val mybalance = "0000"

        val view = inflater.inflate(R.layout.fragment_home, container, false)


        val censoredSaldo = view.findViewById<TextView>(R.id.censoredSaldoAmount)

        val eyeToggle = view.findViewById<ImageView>(R.id.eyeButton)

        val copyAccountNumberButton = view.findViewById<ImageView>(R.id.copyButton)
        val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager


        val balanceCheckText = view.findViewById<TextView>(R.id.saldoAmount)
        val accountNumberText = view.findViewById<TextView>(R.id.accountNumber)


//        Copy rekening
        copyAccountNumberButton.setOnClickListener {
            val clip = android.content.ClipData.newPlainText("Rekening", myaccountnumber)
            clipboardManager.setPrimaryClip(clip)
            Toast.makeText(context,"Nomor rekening berhasil disalin", Toast.LENGTH_SHORT).show()
        }

        // Hide the balance first
        censoredSaldo.visibility = View.VISIBLE
        balanceCheckText.visibility = View.GONE

//        Eye Toggle to show or hide balance
        eyeToggle.setOnClickListener {
            if (censoredSaldo.visibility == View.VISIBLE) {
                censoredSaldo.visibility = View.GONE
                balanceCheckText.visibility = View.VISIBLE
                eyeToggle.setImageResource(R.drawable.icon_eyeopen)
            } else {
                censoredSaldo.visibility = View.VISIBLE
                balanceCheckText.visibility = View.GONE
                eyeToggle.setImageResource(R.drawable.icon_eyeclose)
            }
        }

        //        Grab values from datastore
        accountNumberText.text = myaccountnumber
        balanceCheckText.text = mybalance

//        Grab values from datastore
        accountNumberText.text = myaccountnumber
        balanceCheckText.text = mybalance

//        Clean up string from dots or hyphens
        val cleanStringNomorRekening = accountNumberText.text.toString().replace("-", "")


//        parse norek to String
        val parsedNoRek = cleanStringNomorRekening.toString()


//        norek formatting add hyphens
        val formattedNoRek = parsedNoRek.replace(Regex("(\\d{4})"), "$1-")

        // Set the formatted number as the text of the TextView
        accountNumberText.text = if (formattedNoRek.endsWith("-")) {
            formattedNoRek.dropLast(1)
        } else {formattedNoRek}

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



    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        // API implement
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        dataStoreManager = DataStoreManager(requireContext())
        val balanceCheckText = view.findViewById<TextView>(R.id.saldoAmount)
        val accountNumberText = view.findViewById<TextView>(R.id.accountNumber)
        val greetingsName = view.findViewById<TextView>(R.id.greetings)
        val profilePict = view.findViewById<ImageView>(R.id.circularImageView)

        // TODO: Temporary code to fetch user details and token (NANTI DIHAPUS KALAU UDH ADA FLOW SIGN IN)
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.signInAndFetchDetails(requireContext(),"JANE1234", "jane123")
        }
//
//        //        API GETTER
//        viewLifecycleOwner.lifecycleScope.launch {
//            dataStoreManager.tokenFlow.collect { token ->
//                token?.let {
//                    homeViewModel.getBalance("Bearer $it")
//                }
//            }
//        }
        homeViewModel.balance.observe(viewLifecycleOwner) {
            balanceCheckText.text = formatBalance(it)
        }

        homeViewModel.userDetails.observe(viewLifecycleOwner) {account ->
//            DEBUG
            Log.d("HomeFragment", "User details updated: $account")

            greetingsName.text = "Halo, ${account.name}!"

//            accountNumberText.text = account.account_number
            accountNumberText.text = formatAccountNumber(account.account_number)
//            TODO: Load image with picasso??
            loadImage(profilePict, account.avatar_path)
        }
    }

    private fun loadImage(imageView: ImageView, url: String) {
        // Implement your image loading logic here
    }

    private fun formatBalance(balance: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale.GERMANY)
        return formatter.format(balance)
    }

    private fun formatAccountNumber(accountNumber: String): String {
        val cleanStringNomorRekening = accountNumber.replace("-", "")
        val parsedNoRek = cleanStringNomorRekening.toString()
        val formattedNoRek = parsedNoRek.replace(Regex("(\\d{4})"), "$1-")
        return if (formattedNoRek.endsWith("-")) {
            formattedNoRek.dropLast(1)
        } else {formattedNoRek}
    }

}