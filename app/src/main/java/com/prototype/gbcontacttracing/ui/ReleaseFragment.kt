package com.prototype.gbcontacttracing.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.prototype.gbcontacttracing.R
import com.prototype.gbcontacttracing.databaseManager.DataBaseManager

class ReleaseFragment : Fragment() {

    private val releaseServerEndpoint = "http://p4pproto.sites.gettysburg.edu/GBContactTracing/release.php"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflatedView = inflater.inflate(R.layout.fragment_release, container, false)

        val submitButton = inflatedView.findViewById<Button>(R.id.releaseButton)
        val signature = inflatedView.findViewById<EditText>(R.id.sign)
        val radioGroup =  inflatedView.findViewById<RadioGroup>(R.id.radioGroup)

        submitButton.setOnClickListener{
            Toast.makeText(
                context,
                "Please fill the information before releasing information.",
                Toast.LENGTH_LONG
            )
                .show()
        }

        radioGroup.setOnCheckedChangeListener { _, _ ->
            onFieldChange(inflatedView, submitButton, signature)
        }

        signature.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //Do Nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               //Do Nothing
            }

            override fun afterTextChanged(s: Editable?) {
                onFieldChange(inflatedView, submitButton, signature)
            }

        })



        return inflatedView
    }

    private fun onFieldChange(inflatedView: View, submitButton: Button, signature: EditText){

        val radioYes = inflatedView.findViewById<RadioButton>(R.id.radioYes)
        val radioNo = inflatedView.findViewById<RadioButton>(R.id.radioNo)

        if (radioYes.isChecked){
            if(signature.text.isBlank()){
                submitButton.setOnClickListener{
                    setSignRequired()
                }
            }
            else {
                submitButton.setOnClickListener{
                    setSubmitOnClear()
                }
            }
        } else if (radioNo.isChecked) {
            submitButton.setOnClickListener{
                Toast.makeText(
                    context,
                    "Please submit information only when you are asked.",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    private fun setSignRequired() {
            Toast.makeText(
                context,
                "You need to sign the form before releasing information.",
                Toast.LENGTH_LONG
            )
                .show()
    }

    private fun setSubmitOnClear() {
            val db = context?.let { DataBaseManager(it) }
            db?.setupTable()
            val data = db?.readAllData()
            val user =
                context?.getSharedPreferences(
                    "GBContactTracing",
                    AppCompatActivity.MODE_PRIVATE
                )
                    ?.getString("user_id", "UNNAMED")

            val requestBody =
                "{\"id\":\"196bcd9c-23c4-11eb-adc1-0242ac120002\",\"data\":\"$data\",\"user\":\"$user\"}"

            Log.i("requestBody", requestBody)

            Fuel.post(releaseServerEndpoint)
                .jsonBody(requestBody)
                .also { println(it) }
                .response { result ->
                    val (byte, error) = result
                    if (byte != null) {
                        Log.i("Result is", String(byte))
                        Toast.makeText(
                            context,
                            "Your information has been send to the health center",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    } else {
                        Log.i("Result is", "Nothing")
                    }

                    if (error != null) {
                        Log.e("Error http request", "email verification")
                    }
                }
    }
}