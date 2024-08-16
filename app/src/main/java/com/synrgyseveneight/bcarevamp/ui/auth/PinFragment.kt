package com.synrgyseveneight.bcarevamp.ui.auth
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.synrgyseveneight.bcarevamp.R

class PinFragment : Fragment() {

    private val pinBuilder = StringBuilder()
    private lateinit var pinDots: Array<ImageView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pin, container, false)

        pinDots = arrayOf(
            view.findViewById(R.id.pinDot1),
            view.findViewById(R.id.pinDot2),
            view.findViewById(R.id.pinDot3),
            view.findViewById(R.id.pinDot4),
            view.findViewById(R.id.pinDot5),
            view.findViewById(R.id.pinDot6)
        )

        // Setup tombol angka
        setupNumpadButtons(view)

        // Setup tombol hapus
        view.findViewById<ImageView>(R.id.btnDelete).setOnClickListener {
            deleteLastDigit()
        }

        return view
    }

    private fun setupNumpadButtons(view: View) {
        val buttonIds = arrayOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        for (id in buttonIds) {
            view.findViewById<Button>(id).setOnClickListener { button ->
                addDigit((button as Button).text.toString())
            }
        }
    }

    private fun addDigit(digit: String) {
        if (pinBuilder.length < 6) {
            pinBuilder.append(digit)
            updatePinDots()
        }
    }

    private fun deleteLastDigit() {
        if (pinBuilder.isNotEmpty()) {
            pinBuilder.deleteAt(pinBuilder.length - 1)
            updatePinDots()
        }
    }

    private fun updatePinDots() {
        for (i in pinDots.indices) {
            if (i < pinBuilder.length) {
                pinDots[i].setImageResource(R.drawable.pin_active)
            } else {
                pinDots[i].setImageResource(R.drawable.pin_inactive)
            }
        }
    }
}
