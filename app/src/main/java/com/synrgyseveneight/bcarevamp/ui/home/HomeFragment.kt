package com.synrgyseveneight.bcarevamp.ui.home

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.data.datastore.AuthDataStore
import com.synrgyseveneight.bcarevamp.data.network.RetrofitClient
import com.synrgyseveneight.bcarevamp.data.repository.AuthRepository
import com.synrgyseveneight.bcarevamp.ui.comingsoon.ComingSoonFragment
import com.synrgyseveneight.bcarevamp.ui.common.HorizontalSpaceItemDecoration
import com.synrgyseveneight.bcarevamp.ui.info.MutationFragment
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModel
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModelFactory
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {
    private val viewModelAuth: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.instance, AuthDataStore.getInstance(requireContext())))
    }
    private lateinit var favoriteTransactionAdapter: FavoriteTransactionAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val balanceCheckText = view.findViewById<TextView>(R.id.saldoAmount)
//        Buttons
        val censoredSaldo = view.findViewById<TextView>(R.id.censoredSaldo)

        val eyeToggle = view.findViewById<ImageView>(R.id.eyeButton)



//        val balanceLeft = viewModelAuth.userBalance.value

        val copyAccountNumberButton = view.findViewById<ImageView>(R.id.copyButton)
        val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // TODO: Masih null
        val avatarPath = viewModelAuth.userAvatarPath.value
        Log.d("HomeFragment", "Avatar Path: $avatarPath")


        //        Copy rekening
            copyAccountNumberButton.setOnClickListener {
                val myaccountNumber = viewModelAuth.userAccountNumber.value
                val mycleanaccountNumber = myaccountNumber?.replace("-", "")
                Log.d("HomeFragment", "Account Number: $mycleanaccountNumber")

                val clip = android.content.ClipData.newPlainText("Rekening", mycleanaccountNumber)
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
                eyeToggle.contentDescription = "Saldo saat ini adalah ${balanceCheckText.text}. Klik dua kali untuk menyembunyikan saldo"
                it.announceForAccessibility("Saldo ditampilkan. Saldo saat ini adalah ${balanceCheckText.text}. Klik dua kali untuk menyembunyikan saldo")
            } else {
                censoredSaldo.visibility = View.VISIBLE
                balanceCheckText.visibility = View.GONE
                eyeToggle.setImageResource(R.drawable.icon_eyeclose)
                eyeToggle.contentDescription = "Saldo disembunyikan. Klik dua kali untuk menampilkan saldo"
                it.announceForAccessibility("Saldo disembunyikan. Klik dua kali untuk menampilkan saldo")
            }
        }

        // Sample data
        val transactions = listOf(
            FavoriteTransactionAdapter.Transaction(R.drawable.icon_transfer, "Transfer", "Transfer Antar Rekening BCA", "Naysilla Hani"),
            FavoriteTransactionAdapter.Transaction(R.drawable.icon_wallet, "Top Up", "Top Up OVO", "Aditya Syarif"),
            FavoriteTransactionAdapter.Transaction(R.drawable.icon_wallet, "Top Up", "Top Up ShopeePay", "Dwi Kurniawan")
        )

        // Dapatkan gambar PP
        Glide.with(this)
            .load(avatarPath)
            .circleCrop()
            .error(R.drawable.icon_person)
            .into(view.findViewById<ImageView>(R.id.circularImageView))

        // Initialize RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.fav_transaction_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        favoriteTransactionAdapter = FavoriteTransactionAdapter(transactions)
        recyclerView.adapter = favoriteTransactionAdapter

        //Tambahkan Spcing di RecyclerView
        val spaceDecoration = HorizontalSpaceItemDecoration(16)
        recyclerView.addItemDecoration(spaceDecoration)

        // Navigasi Quick Menu
        val nameTv = view.findViewById<TextView>(R.id.greetings)
        val transferButton = view.findViewById<ImageView>(R.id.transferButton)
        val transferTitle = view.findViewById<TextView>(R.id.transferTitle)
        val logoutButton = view.findViewById<ImageView>(R.id.logoutButton)
        val accountNumber = view.findViewById<TextView>(R.id.accountNumber)

        val saldoButtonImage = view.findViewById<ImageView>(R.id.infoButton)
        val saldoButtonText = view.findViewById<TextView>(R.id.infoTitle)


        val navController = findNavController()
        viewModelAuth.userName.observe(viewLifecycleOwner) { name ->
            nameTv.text = "Halo, "+name+"!"
        }

        // NAVIGATION
        val clickListenerToInfoMenu = View.OnClickListener {
            navController.navigate(R.id.action_homeFragment_to_infoMenuFragment)
        }
        saldoButtonImage.setOnClickListener(clickListenerToInfoMenu)
        saldoButtonText.setOnClickListener(clickListenerToInfoMenu)


        // ke Transfer antar BCA
        val clickListener = View.OnClickListener {
            navController.navigate(R.id.action_homeFragment_to_transferOptionFragment)
        }
        transferButton.setOnClickListener(clickListener)
        transferTitle.setOnClickListener(clickListener)

        val eWalletLogo = view.findViewById<ImageView>(R.id.ewallletButton)
        eWalletLogo.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_eWalletOptionFragment)
        }

        logoutButton.setOnClickListener{
            viewModelAuth.clearToken {
                // Navigasi kembali ke LoginFragment
                findNavController().navigate(R.id.action_homeFragment_to_loginSecondFragment)
            }
            Toast.makeText(this.context, "Berhasil Logout", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        // API implement
        val accountNumberText = view.findViewById<TextView>(R.id.accountNumber)
        val greetingsName = view.findViewById<TextView>(R.id.greetings)
        val profilePict = view.findViewById<ImageView>(R.id.circularImageView)

        viewModelAuth.userToken.observe(viewLifecycleOwner) { token ->
            if (token != null) {
                viewModelAuth.fetchBalance(token)
            }
        }

        viewModelAuth.userBalance.observe(viewLifecycleOwner) {
            Log.d("HomeFragment", "Balance updated: $it")
            val saldoAmount = view.findViewById<TextView>(R.id.saldoAmount)
            saldoAmount.text = "Rp ${it}"
        }

        viewModelAuth.userAccountNumber.observe(viewLifecycleOwner) {
            Log.d("HomeFragment", "Account updated: $it")
            accountNumberText.text = formattedAccountNumber(it?: "Gagal memuat")
        }

        viewModelAuth.userAvatarPath.observe(viewLifecycleOwner) {
            Log.d("HomeFragment", "Avatar updated: $it")
            Glide.with(this)
                .load(it)
                .circleCrop()
                .error(R.drawable.icon_person)
                .into(profilePict)
        }


    }

//    Dipindah ke AuthViewModel.kt
    private fun formatBalance(balance: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale.GERMANY)
        return formatter.format(balance)
    }

    private fun formattedAccountNumber (accountNumber: String): String {
        val cleanStringNomorRekening = accountNumber.replace("-", "")
        val parsedNoRek = cleanStringNomorRekening.toString()
        val formattedNoRek = parsedNoRek.replace(Regex("(\\d{4})"), "$1-")
        return if (formattedNoRek.endsWith("-")) {
            formattedNoRek.dropLast(1)
        } else {formattedNoRek}
    }

}