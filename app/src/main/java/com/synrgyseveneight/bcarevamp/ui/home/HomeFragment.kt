package com.synrgyseveneight.bcarevamp.ui.home

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.data.datastore.AuthDataStore
import com.synrgyseveneight.bcarevamp.data.network.RetrofitClient
import com.synrgyseveneight.bcarevamp.data.repository.AuthRepository
import com.synrgyseveneight.bcarevamp.data.repository.MonthlyReportRepository
import com.synrgyseveneight.bcarevamp.ui.common.HorizontalSpaceItemDecoration
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModel
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModelFactory
import com.synrgyseveneight.bcarevamp.viewmodel.ErrorType
import com.synrgyseveneight.bcarevamp.viewmodel.MonthlyReportViewModel
import com.synrgyseveneight.bcarevamp.viewmodel.MonthlyReportViewModelFactory
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {
    private val viewModelAuth: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.instance, AuthDataStore.getInstance(requireContext())))
    }
    private val reportViewModel: MonthlyReportViewModel by viewModels {
        MonthlyReportViewModelFactory(MonthlyReportRepository(RetrofitClient.instance))
    }
    private lateinit var favoriteTransactionAdapter: FavoriteTransactionAdapter

    private lateinit var transaksiFavorit : RecyclerView


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
        val imagePath = viewModelAuth.userImagePath.value

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
            .load(imagePath)
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
        val transferButton = view.findViewById<ImageView>(R.id.transferButton)
        val transferTitle = view.findViewById<TextView>(R.id.transferTitle)
        val accountNumber = view.findViewById<TextView>(R.id.accountNumber)
        val saldoButtonImage = view.findViewById<ImageView>(R.id.infoButton)
        val saldoButtonText = view.findViewById<TextView>(R.id.infoTitle)
        val navController = findNavController()


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

        // ewallet
        val eWalletLogo = view.findViewById<ImageView>(R.id.ewallletButton)
        eWalletLogo.setOnClickListener {
            showCustomToast("Mohon maaf, layanan belum tersedia")
        }

        // Pembelian
        val purchaseLogo = view.findViewById<ImageView>(R.id.buyButton)
        val purchaseTitle = view.findViewById<TextView>(R.id.buyTitle)
        purchaseLogo.setOnClickListener {
            showCustomToast("Mohon maaf, layanan belum tersedia")
        }
        purchaseTitle.setOnClickListener {
            showCustomToast("Mohon maaf, layanan belum tersedia")
        }

        // Cardless
        val cardlessLogo = view.findViewById<ImageView>(R.id.cardlessButton)
        val cardlessTitle = view.findViewById<TextView>(R.id.cardlessTitle)
        cardlessLogo.setOnClickListener {
            showCustomToast("Mohon maaf, layanan belum tersedia")
        }
        cardlessTitle.setOnClickListener {
            showCustomToast("Mohon maaf, layanan belum tersedia")
        }

        // Lainnya
        val moreLogo = view.findViewById<ImageView>(R.id.moreButton)
        val moreTitle = view.findViewById<TextView>(R.id.moreTitle)
        moreLogo.setOnClickListener {
            showCustomToast("Mohon maaf, layanan belum tersedia")
        }
        moreTitle.setOnClickListener {
            showCustomToast("Mohon maaf, layanan belum tersedia")
        }

        val editMenuTitle = view.findViewById<TextView>(R.id.editMenuTitle)
        val editMenuButton = view.findViewById<ImageView>(R.id.editMenuButton)
        editMenuButton.setOnClickListener{
            showCustomToast("Mohon maaf, layanan belum tersedia")
        }
        editMenuTitle.setOnClickListener{
            showCustomToast("Mohon maaf, layanan belum tersedia")
        }

        

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        // API implement
        val accountNumberText = view.findViewById<TextView>(R.id.accountNumber)
        val greetingsName = view.findViewById<TextView>(R.id.greetings)
        val profilePict = view.findViewById<ImageView>(R.id.circularImageView)
        val logoutButton = view.findViewById<ImageView>(R.id.logoutButton)
        val favmonthpicker = view.findViewById<ConstraintLayout>(R.id.fav_month_picker)
        val textMonthPicker = view.findViewById<TextView>(R.id.date_picker_title)
        val amountIncome = view.findViewById<TextView>(R.id.amount_income)
        val amountOutcome = view.findViewById<TextView>(R.id.amount_outcome)
        val amountSelisih = view.findViewById<TextView>(R.id.amount_selisih)
        val progressBarReport = view.findViewById<View>(R.id.progressBarReport)


        viewModelAuth.userToken.observe(viewLifecycleOwner) { token ->
            if (token != null) {
                viewModelAuth.fetchBalance(token)
            }
        }


        // mengecek balance null
        viewModelAuth.userBalance.observe(viewLifecycleOwner) { balance ->
            //jika balance tidak null
            if (balance != null) {
                Log.d("HomeFragment", "Balance updated: $balance")
                val saldoAmount = view.findViewById<TextView>(R.id.saldoAmount)
                saldoAmount.text = "Rp $balance"
            } else {
                // jika balance null maka otomatis token expired
                viewLifecycleOwner.lifecycleScope.launch {
                    Toast.makeText(requireContext(), "Token Expired, Silahkan Login Kembali", Toast.LENGTH_SHORT).show()
                }
                viewModelAuth.clearToken {
                    findNavController().navigate(R.id.action_homeFragment_to_loginSecondFragment)
                }
            }
        }

        viewModelAuth.userAccountNumber.observe(viewLifecycleOwner) {
            Log.d("HomeFragment", "Account updated: $it")
            accountNumberText.text = formattedAccountNumber(it?: "Gagal memuat")
        }

        viewModelAuth.userImagePath.observe(viewLifecycleOwner) {
            Log.d("HomeFragment", "Avatar updated: $it")
            Glide.with(this)
                .load(it)
                .circleCrop()
                .error(R.drawable.icon_person)
                .into(profilePict)
        }

        // Error handling
        viewModelAuth.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        // Error handling to go to log in
        viewModelAuth.logoutEvent.observe(viewLifecycleOwner) {
            // Navigate to the login screen
            findNavController().navigate(R.id.action_homeFragment_to_loginSecondFragment)
        }

        viewModelAuth.userName.observe(viewLifecycleOwner) { name ->
            greetingsName.text = "Halo, "+name+"!"
        }

        viewModelAuth.userToken.observe(viewLifecycleOwner) { token ->
            // Request laporan awal dengan bulan dan tahun saat ini
            reportViewModel.getMonthlyReport(
                selectedMonth.toString(),
                selectedYear.toString(),
                token ?: ""
            )
        }

        favmonthpicker.setOnClickListener {
            MonthPickerDialog(requireContext()) { formattedDate, selectedMonth, selectedYear ->
                // Set textMonthPicker dengan tanggal yang dipilih
                textMonthPicker.text = formattedDate

                // Mengambil token dan memanggil API
                viewModelAuth.userToken.observe(viewLifecycleOwner) { token ->
                    reportViewModel.getMonthlyReport(
                        selectedMonth.toString(),
                        selectedYear.toString(),
                        token ?: ""
                    )
                }
            }.show()
        }

        reportViewModel.navigationEvent.observe(viewLifecycleOwner, { errorType ->
            when(errorType){
                ErrorType.ERROR_404 -> findNavController().navigate(R.id.action_homeFragment_to_error404Fragment)
                ErrorType.ERROR_500 -> findNavController().navigate(R.id.action_homeFragment_to_error500Fragment)
                ErrorType.UNKNOWN_ERROR -> {
//                    Toast.makeText(requireContext(), "Unknown error", Toast.LENGTH_SHORT).show()
                }
            }
        })

        logoutButton.setOnClickListener{
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_logout, null)
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setBackground(ColorDrawable(Color.Transparent.toArgb()))
                .create()
            val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
            val btnLogout = dialogView.findViewById<Button>(R.id.btnLogout)
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            btnLogout.setOnClickListener {
                viewModelAuth.clearToken {
                    findNavController().navigate(R.id.action_homeFragment_to_loginSecondFragment)
                }
                Toast.makeText(this.context, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            dialog.show()
        }


        reportViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            progressBarReport.visibility = if (isLoading) View.VISIBLE else View.GONE

        })

        reportViewModel.income.observe(viewLifecycleOwner) { income ->
            amountIncome.text = formatBalance(income.toDouble())
        }

        reportViewModel.expense.observe(viewLifecycleOwner) { expense ->
            amountOutcome.text = formatBalance(expense.toDouble())
        }

        reportViewModel.total.observe(viewLifecycleOwner) { total ->
            amountSelisih.text = formatBalance(total.toDouble())
        }

    }

    private fun formatBalance(balance: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale.GERMANY)
        return "Rp " + formatter.format(balance)
}

    private fun formattedAccountNumber (accountNumber: String): String {
        val cleanStringNomorRekening = accountNumber.replace("-", "")
        val parsedNoRek = cleanStringNomorRekening.toString()
        val formattedNoRek = parsedNoRek.replace(Regex("(\\d{4})"), "$1-")
        return if (formattedNoRek.endsWith("-")) {
            formattedNoRek.dropLast(1)
        } else {formattedNoRek}
    }

    // Variabel untuk menyimpan pilihan dari NumberPicker
    private var selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1
    private var selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR)

    @SuppressLint("ResourceAsColor")
    private fun showCustomToast(message: String){

        //create linear layout
        val customToastLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 16, 16, 16)
            background = ContextCompat.getDrawable(requireContext(), R.drawable.toast_background)
            elevation = 10f
        }

        val toastIcon = ImageView(requireContext()).apply {
            setImageResource(R.drawable.icon_toast)
            setPadding(0, 0, 16, 0)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_VERTICAL
                setMargins(24, 0, 24, 0)
            }
        }

        val typefaces = ResourcesCompat.getFont(requireContext(), R.font.nunitoregular)


        //text view for toast message
        val toastTextView = TextView(requireContext()).apply {
            text = message
            setTextColor(R.color.darkBlue)
            textSize = 16f
            typeface = typefaces

        }

        customToastLayout.addView(toastIcon)
        customToastLayout.addView(toastTextView)

        with(Toast(requireContext())){
            duration = Toast.LENGTH_SHORT
            view = customToastLayout
            setGravity(Gravity.CENTER, 0,400)
            show()
        }

        //accessibility
        customToastLayout.announceForAccessibility(message)

    }

}