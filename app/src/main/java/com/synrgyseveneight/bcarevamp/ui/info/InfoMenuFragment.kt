package com.synrgyseveneight.bcarevamp.ui.info

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.synrgyseveneight.bcarevamp.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.synrgyseveneight.bcarevamp.data.datastore.AuthDataStore
import com.synrgyseveneight.bcarevamp.data.network.RetrofitClient
import com.synrgyseveneight.bcarevamp.data.repository.AuthRepository
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModel
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class InfoMenuFragment : Fragment() {
    // inisiasi viewmodel
    private val viewModelAuth: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.instance, AuthDataStore.getInstance(requireContext())))
    }

    // Inisiasi adapter
    private lateinit var adapterOpsiInfo : CardAdapter

    // Jumlah percobaan ambil saldo jika null
    private var retryCountForSaldo = 0

    // Tempat simpan value
    private var balanceSave: String = ""
    private var accountNumberSave: String = ""
    private var checkTimeSave: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info_menu, container, false)

        //Data Example
        val cardList = listOf(
            CardAdapter.CardInfo("Info Saldo", "Informasi terkait saldo rekening"),
            CardAdapter.CardInfo("Mutasi", "Informasi riwayat dan bukti transaksi rekening"),
            CardAdapter.CardInfo("Rekening Deposito", "Rekening dari deposito berjangka BCA"),
            CardAdapter.CardInfo("Info Reward BCA", "Informasi loyalty point BCA pengguna"),
            CardAdapter.CardInfo("Info Reksadana", "Informasi produk reksadana BCA pengguna"),
            CardAdapter.CardInfo("Info Kurs", "Informasi nilai kurs mata uang asing"),
            CardAdapter.CardInfo("Info RDN", "Informasi saldo dan mutasi RDN BCA pengguna"),
            CardAdapter.CardInfo("Info KPR", "Informasi ajuan KPR BCA pengguna"),
            CardAdapter.CardInfo("Info Kartu Kredit", "Informasi kartu kredit BCA pengguna")
        )

        //initialize recyclerview
        val recyclerView = view.findViewById<RecyclerView>(R.id.infoOptions_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

        // Library Dialog yang bisa menampilkan custom dialogue
        val dialog = Dialog(requireContext())
        // Listener saat klik setiap opsi dalam recyclerview
        adapterOpsiInfo = CardAdapter(cardList) { cardInfo ->
            when (cardInfo.title) {
                "Info Saldo" -> {
                    dialog.setContentView(R.layout.dialog_info_saldo)

                    val window = dialog.window
                    window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    window?.setGravity(Gravity.CENTER)
                    window?.setBackgroundDrawable(null)

                    // Textview dalam dialog
                    val balanceText = dialog.findViewById<TextView>(R.id.infoSaldoContent)
                    val updateBalanceTimeText = dialog.findViewById<TextView>(R.id.checkTime)
                    val accountNumber = dialog.findViewById<TextView>(R.id.transaction_code)

                    // Update token
                    viewModelAuth.userToken.observe(viewLifecycleOwner) { token ->
                        Log.d("InfoFragment", "Token updated: $token")
                        if (token != null) {
                            viewModelAuth.fetchBalance(token)
                        }
                    }

                    // Update waktu cek saldo
                    viewModelAuth.balanceCheckTime.observe(viewLifecycleOwner) { checkTime ->
                        Log.d("InfoFragment", "Balance check time updated: $checkTime")
//                        updateBalanceTimeText.text = checkTime
                        val formattedCheckTime = formatDateString(checkTime)
                        updateBalanceTimeText.text = formattedCheckTime
                        updateBalanceTimeText.contentDescription = "Saldo terakhir diperbarui pada $formattedCheckTime"
                    }

                    // Update saldo
                    viewModelAuth.userBalance.observe(viewLifecycleOwner) { balance ->
                        Log.d("InfoFragment", "Balance updated: $balance")
                        balanceText.text = "Rp $balance"

                        if (balance==null){
                            retryCountForSaldo++
                            balanceText.contentDescription = "Saldo sedang dimuat, mohon tunggu"
                            if (retryCountForSaldo <= 3) {
                                viewModelAuth.fetchBalance(viewModelAuth.userToken.value?:"")
                            } else {
                                // Kalau udah tiga kali masih null, balik ke Beranda
                                Toast.makeText(context, "Gagal memuat saldo. Silakan coba lagi", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_infoMenuFragment_to_homeFragment)


                            }
                        } else {
                            retryCountForSaldo = 0
                            balanceText.text = "Rp ${balance}"
                            balanceText.contentDescription = "Saldo saat ini adalah ${balanceText.text}"

                            balanceSave = "Rp ${balance}"
                            Log.d ("InfoFragment", "Balance saved: $balanceSave")
                        }
                    }

                    // Get nomor rekening
                    viewModelAuth.userAccountNumber.observe(viewLifecycleOwner) {
                        Log.d("InfoFragment", "Account number updated: $it")
                        accountNumber.text = formattedAccountNumber(it ?: "")

                        accountNumberSave = it.toString()
                        Log.d ("InfoFragment", "Account number saved: $accountNumberSave")

                        if (accountNumber == null){
                            accountNumber?.contentDescription = "Nomor rekening gagal ditampilkan, sedang mencoba kembali"
                        } else {
                            accountNumber.contentDescription = "Nomor rekening Anda adalah ${accountNumber.text}"
                        }

//                        val titlePageInfo = dialog.findViewById<TextView>(R.id.title_tab)
//                        titlePageInfo.contentDescription = "Saldo Anda saat ini adalah ${balanceSave}. Nomor rekening Anda adalah ${accountNumberSave}"
                    }



                    dialog.show()

                    val closeButton = dialog.findViewById<Button>(R.id.btnKembali)
                    closeButton.setOnClickListener {
                        dialog.dismiss()
                    }
                }
                "Mutasi" -> {
                    findNavController().navigate(R.id.action_infoMenuFragment_to_mutationFragment)
                }
                "Rekening Deposito" -> {
                    // TODO : Navigasi ke fragment Rekening Deposito
                    showCustomToast("Mohon maaf, layanan belum tersedia")
                }
                "Info Reward BCA" -> {
                    // TODO : Navigasi ke fragment Info Reward BCA
                    showCustomToast("Mohon maaf, layanan belum tersedia")
                }
                "Info Reksadana" -> {
                    // TODO : Navigasi ke fragment Info Reksadana
                    showCustomToast("Mohon maaf, layanan belum tersedia")
                }
                "Info Kurs" -> {
                    // TODO : Navigasi ke fragment Info Kurs
                    showCustomToast("Mohon maaf, layanan belum tersedia")
                }
                "Info RDN" -> {
                    // TODO : Navigasi ke fragment Info RDN
                    showCustomToast("Mohon maaf, layanan belum tersedia")
                }
                "Info KPR" -> {
                    // TODO : Navigasi ke fragment Info KPR
                    showCustomToast("Mohon maaf, layanan belum tersedia")

                }
                "Info Kartu Kredit" -> {
                    // TODO : Navigasi ke fragment Info Kartu Kredit
                    showCustomToast("Mohon maaf, layanan belum tersedia")
                }
            }
        }

        recyclerView.adapter = adapterOpsiInfo

//        fetchBalance()
        // Back button
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        return view
    }

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


    private fun formattedAccountNumber (accountNumber: String): String {
        val cleanStringNomorRekening = accountNumber.replace("-", "")
        val parsedNoRek = cleanStringNomorRekening.toString()
        val formattedNoRek = parsedNoRek.replace(Regex("(\\d{4})"), "$1-")
        return if (formattedNoRek.endsWith("-")) {
            formattedNoRek.dropLast(1)
        } else {formattedNoRek}
    }

    // Convert format BE ke FE Android readable
    private fun formatDateString(dateString: String): String {
        Log.d("InfoFragment", "Date string: $dateString")

        // Check if dateString is null or empty
        if (dateString.isNullOrEmpty()) {
            return "Terjadi kesalahan"
        }

        // Define the input and output date formats
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSS", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("HH.mm.ss 'WIB', d MMMM yyyy", Locale("in", "ID"))

        // Parse the input date string
        val date = inputFormat.parse(dateString)

        // Format the date to the output format
        return outputFormat.format(date)
    }
}