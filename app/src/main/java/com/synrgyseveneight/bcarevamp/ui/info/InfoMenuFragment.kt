package com.synrgyseveneight.bcarevamp.ui.info

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.core.content.getSystemService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.R


class InfoMenuFragment : Fragment() {
    private lateinit var cardAdapter: CardAdapter
    private lateinit var apiService: ApiService
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info_menu, container, false)

        //initialize recyclerview
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        //intialize retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://setara.com/api/v1/user/getBalance")
            .addConverterFactory(GsonConverterFactory.create())
            .build()



        apiService = retrofit.create(ApiService::class.java)

        //Data Example
        val cardList = listOf(
            CardAdapter.CardInfo("Info Saldo", "Informasi terkait saldo rekening", R.drawable.arrow_right),
            CardAdapter.CardInfo("Mutasi", "Informasi riwayat dan bukti transaksi rekening", R.drawable.arrow_right),
            CardAdapter.CardInfo("Rekening Deposito", "Rekening dari deposito berjangka BCA", R.drawable.arrow_right),
            CardAdapter.CardInfo("Info Reward BCA", "Informasi loyalty point BCA pengguna", R.drawable.arrow_right),
            CardAdapter.CardInfo("Info Reksadana", "Informasi produk reksadana BCA pengguna", R.drawable.arrow_right),
            CardAdapter.CardInfo("Info Kurs", "Informasi nilai kurs mata uang asing", R.drawable.arrow_right),
            CardAdapter.CardInfo("Info RDN", "Informasi saldo dan mutasi RDN BCA pengguna", R.drawable.arrow_right),
            CardAdapter.CardInfo("Info KPR", "Informasi ajuan KPR BCA pengguna", R.drawable.arrow_right),
            CardAdapter.CardInfo("Info Kartu Kredit", "Informasi kartu kredit BCA pengguna", R.drawable.arrow_right)
        )

        //set up adapter
        cardAdapter = CardAdapter(cardList) {cardInfo ->
            if (cardInfo.title == "Info Saldo"){
                //inflate from dialog info saldo
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_info_saldo, null)

                //create and show dialog
                val show = AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }else{
                //initialize toast
                val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val layout = inflater.inflate(R.layout.custom_toast, null)

                val toast = Toast(context)
                toast.duration = Toast.LENGTH_SHORT
                toast.view = layout
                toast.show()
            }
        }
        recyclerView.adapter = cardAdapter

        fetchBalance()

        return view
    }

    private fun fetchBalance() {
        apiService.getBalance().enqueue(object : Callback<BalanceResponse>{
            var saldo: TextView? = view!!?.findViewById(R.id.infoSaldoContent)
            var time: TextView? = view!!?.findViewById(R.id.checkTime)

            override fun onResponse(call: Call<BalanceResponse>, response: Response<BalanceResponse>) {
                if (response.isSuccessful){
                    response.body()?.let { balanceResponse ->


                        if (balanceResponse.status){
                            val checkTime = balanceResponse.data.checkTime
                            val balance = balanceResponse.data.balance
                            if (saldo != null) {
                                saldo!!.text= "Rp$balance"
                            }else{
                                saldo!!.text= "Saldo Anda Tidak Ada"
                            }



                        }
                        else{
                            saldo!!.text = "Failed get Balance"
                        }
                    }
                }
            }

            override fun onFailure(call: Call<BalanceResponse>, t: Throwable) {
                saldo!!.text = "Error: ${t.message}"
            }
        })
    }
}