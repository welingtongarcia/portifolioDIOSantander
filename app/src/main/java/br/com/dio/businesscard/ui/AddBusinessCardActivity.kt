package br.com.dio.businesscard.ui

import android.content.DialogInterface
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import br.com.dio.businesscard.App
import br.com.dio.businesscard.R
import br.com.dio.businesscard.data.BusinessCard
import br.com.dio.businesscard.databinding.ActivityAddBusinessCardBinding
import com.google.android.material.snackbar.Snackbar
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class AddBusinessCardActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAddBusinessCardBinding.inflate(layoutInflater) }
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as App).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        insertListeners()
    }

    private fun insertListeners() {
        binding.btnClose.setOnClickListener {
            finish()
        }
        binding.btnConfirm.setOnClickListener {
            validate()?.let { businessCard ->
                mainViewModel.insert(businessCard)
                Toast.makeText(this, R.string.label_show_success, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        binding.tilColor.editText?.setOnClickListener {
            val builder = ColorPickerDialog.Builder(this)
                .setTitle(getString(R.string.label_pick_a_color))
                .setPositiveButton(
                    getString(R.string.label_confirm),
                    ColorEnvelopeListener { envelope: ColorEnvelope?, fromUser: Boolean ->
                        binding.tilColor.editText?.setText("#${envelope?.hexCode}")
                    })
                .setNegativeButton(
                    getString(R.string.label_cancel)
                ) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
                .attachAlphaSlideBar(false)
            builder.colorPickerView.flagView = BubbleFlag(this)
            builder.show()
        }

        binding.tilPhone.editText?.addTextChangedListener(PhoneNumberFormattingTextWatcher())
    }

    private fun validate(): BusinessCard? {
        val name = binding.tilName.editText?.text
        val business = binding.tilBusiness.editText?.text
        val phone = binding.tilPhone.editText?.text
        val email = binding.tilEmail.editText?.text
        val customBackground = binding.tilColor.editText?.text

        if (!name.isNullOrBlank()) {
            if (!phone.isNullOrBlank()) {
                if (!email.isNullOrBlank()) {
                    if (!business.isNullOrBlank()) {
                        if (!customBackground.isNullOrBlank()) {
                            return BusinessCard(
                                name = name.toString(),
                                business = business.toString(),
                                phone = phone.toString(),
                                email = email.toString(),
                                customBackground = customBackground.toString()
                            )
                        } else {
                            showSnackbar(R.string.message_type_background_color)
                        }
                    } else {
                        showSnackbar(R.string.message_type_the_company_name)
                    }
                } else {
                    showSnackbar(R.string.message_type_your_email)
                }
            } else {
                showSnackbar(R.string.message_type_your_phone_number)
            }
        } else {
            showSnackbar(R.string.message_type_your_name)
        }
        return null
    }

    private fun showSnackbar(@StringRes message: Int) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .show()
    }
}