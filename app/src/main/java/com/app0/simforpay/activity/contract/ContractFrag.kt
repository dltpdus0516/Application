package com.app0.simforpay.activity.contract

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app0.simforpay.R
import com.app0.simforpay.retrofit.RetrofitHelper
import com.app0.simforpay.retrofit.domain.Borrower
import com.app0.simforpay.retrofit.domain.Contract
import com.app0.simforpay.retrofit.domain.ContractSuccess
import com.app0.simforpay.retrofit.domain.User
import com.app0.simforpay.util.CustomDialog
import com.app0.simforpay.util.TextInput
import com.app0.simforpay.util.sharedpreferences.Key
import com.app0.simforpay.util.sharedpreferences.MyApplication
import com.google.android.material.textfield.TextInputLayout
import com.hendraanggrian.appcompat.widget.Mention
import com.hendraanggrian.appcompat.widget.MentionArrayAdapter
import kotlinx.android.synthetic.main.frag_contract.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class ContractFrag : Fragment() {

    private val Retrofit = RetrofitHelper.getRetrofit()
    private val calendar = Calendar.getInstance()
    private val userInfo = mutableMapOf<String, Int>()
    private lateinit var callback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Press Back Button
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                ShowAlertDialog()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_contract, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mentionAdapter : ArrayAdapter<Mention> = MentionArrayAdapter(this.requireContext())

        Retrofit.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {

                mentionAdapter.clear()

                response.body()?.forEach {

                    userInfo[it.name] = it.id
                    mentionAdapter.add(Mention(it.name))
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {}

        })

//        Log.d("size", mentionAdapter.count.toString())

        lender.mentionAdapter = mentionAdapter
        borrower1.mentionAdapter = mentionAdapter
        borrower2.mentionAdapter = mentionAdapter
        borrower3.mentionAdapter = mentionAdapter
        borrower4.mentionAdapter = mentionAdapter
        borrower5.mentionAdapter = mentionAdapter

        // Bank dropdown
        val items = listOf(
            "NH농협", "KB국민", "신한", "우리", "하나", "IBK기업", "SC제일", "씨티", "KDB산업", "SBI저축",
            "새마을", "대구", "광주", "우체국", "신협", "전북", "경남", "부산", "수협", "제주", "카카오뱅크"
        )
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        bank.setAdapter(adapter)

        // Enable Switch
        swAlert.isEnabled = false
        if (!swAlert.isEnabled) {
            swAlert.setOnClickListener {
                Toast.makeText(context, "완료일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        TextInput.CheckFive(btnSave, contractName, tradeDay, price, lender, borrower1)
    }

    override fun onResume() {
        super.onResume()
        var cnt = 1 // borrower cnt
        val borrowerPrices = listOf(
            borrowerPrice1,
            borrowerPrice2,
            borrowerPrice3,
            borrowerPrice4,
            borrowerPrice5
        )

        // Click Back Button
        btnBack.setOnClickListener {
            ShowAlertDialog()
        }

        // Datepicker
        tradeDay.setOnClickListener {
            ShowDatePickerDialog(tradeDay, "trade")
        }

        complDay.setOnClickListener {
            swAlert.isEnabled = true

            if(!tradeDay.text.isNullOrEmpty())
                ShowDatePickerDialog(complDay, "compl")
            else
                Toast.makeText(context, "거래일을 입력해주세요.", Toast.LENGTH_SHORT).show()
        }

        btnDelComplDay.setOnClickListener {
            complDay.setText("")
            swAlert.isChecked = false
            swAlert.isEnabled = false
        }

        // Set individual Price
        price.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                TextBorrowerPrice(cnt, borrowerPrices)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        cbN1.setOnCheckedChangeListener { buttonView, isChecked ->
            TextBorrowerPrice(cnt, borrowerPrices)
        }

        // Add & Delete Borrower
        btnAddBorrower.setOnClickListener {
            cnt++
            VisibilBorrower(cnt, 0)
            LayBorrower(cnt)

            TextBorrowerPrice(cnt, borrowerPrices)
        }

        btnDelBorrower.setOnClickListener {
            VisibilBorrower(cnt, 1)
            cnt--
            LayBorrower(cnt)

            TextBorrowerPrice(cnt, borrowerPrices)
        }

        // Random checkbox
        val randomPenalty = listOf(
            "이목구비 최대한 멀리 떨어트리기", "노래 한 소절 부르기", "노래 없이 춤추기", "심부름하기",
            "치킨 쏘기", "소원 하나 들어주기", "사극 말투 쓰기", "프리스타일 랩 60초동안 하기", "세상에서 가장 웃긴 표정 짓기",
            "굴욕 각도로 셀카 찍어서 SNS 올리기", "혓바닥 코에 붙이고 있기"
        )

        cbRandom.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                val randomNum = Random().nextInt(randomPenalty.size)

                penalty.setText(randomPenalty[randomNum])
            }
            else{
                penalty.setText("")
            }
        }

        btnSave.setOnClickListener{
            val user_id = Integer.parseInt(MyApplication.prefs.getString(Key.LENDER_ID.toString(), ""))
            val title = contractName.text.toString()
            val borrow_date = tradeDay.text.toString()
            val payback_date = complDay.text.toString()
            val price = Integer.parseInt(price.text.toString())
            val lender_name = lender.text.toString().replace("@", "").trim()
            val lender_id: Int? = userInfo[lender_name]
            val lender_bank = bank.text.toString()
            val lender_account: Int? = accountNum.text.toString().toIntOrNull()
            val borrowerList = arrayListOf<Borrower>()
            val penalty = penalty.text.toString()
            val alarm = if (swAlert.isChecked) 1 else 0

            val borrowerIdList = arrayOf(borrower1, borrower2, borrower3, borrower4, borrower5)
            val priceIdList = arrayOf(borrowerPrice1, borrowerPrice2, borrowerPrice3, borrowerPrice4, borrowerPrice5)
            val payback_state = 0

            var borrowerName = borrowerIdList[0].text.toString().replace("@","").trim()

            for(i in 0 until cnt){

                val userName = borrowerIdList[i].text.toString().replace("@","").trim()
                val borrower_id: Int? = userInfo[userName]
                val borrower_price = priceIdList[i].text.toString().replace("원", "").replace(",","").trim().toIntOrNull()

                borrowerList.add(Borrower(borrower_id, userName, borrower_price, payback_state))
                if(i >= 1) borrowerName = "$userName,$borrowerName"

            }

            var content = "${borrowerName}은(는) ${lender_name}에게 "
            if(payback_date != "") content = content + payback_date +"까지 "
            content = content + priceIdList[0].text.toString().replace("원", "").replace(",","").trim() + "원을 갚을 것을 약속합니다."
            if(penalty != "") content = content + " 만약 이행하지 못할시에는" + penalty +"를 할 것 입니다."

            val contractInfo = Contract(user_id, title, borrow_date, payback_date, price, lender_id, lender_name, lender_bank, lender_account, borrowerList, penalty, content,alarm)

            Log.d("test", contractInfo.toString())

            Retrofit.ContractCall(contractInfo)
                .enqueue(object : Callback<ContractSuccess> {
                    override fun onResponse( call: Call<ContractSuccess>, response: Response<ContractSuccess>)
                    {
                        if (response.body()?.result == "true"){


                            fragmentManager!!.beginTransaction().replace(R.id.layFull,
                                ContractShareFrag.newInstance(contractName.text.toString(), content)
                            ).commit()

                        }
                        else
                            Toast.makeText(context, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(call: Call<ContractSuccess>, t: Throwable) {

                    }
                })
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    fun ShowAlertDialog() {
        val dialog = CustomDialog.CustomDialogBuilder()
            .setTitle("뒤로가시겠습니까?")
            .setMessage("뒤로가시면 지금까지 작성했던 내용이 삭제됩니다.")
            .setNegativeBtnText("취소")
            .setPositiveBtnText("확인")
            .setBtnClickListener(object : CustomDialog.CustomDialogListener {
                override fun onClickPositiveBtn() {
                    findNavController().navigate(R.id.action_fragContract_to_fragHome)
                }
            }).create()
        dialog.show(parentFragmentManager, dialog.tag)
    }

    fun ShowDatePickerDialog(editText: EditText, str: String) {
        val listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            editText.setText("${year}년 ${month + 1}월 ${dayOfMonth}일")

            if (str == "trade") {
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
        }

        DatePickerDialog(
            this.requireContext(),
            listener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            if (str == "trade")
                datePicker.maxDate = System.currentTimeMillis()
            else
                datePicker.minDate = calendar.time.time
        }.show()
    }

    fun VisibilBorrower(cnt: Int, btnState: Int){
        var layBorrower = layBorrower2 as TextInputLayout
        var borrowerPrice = borrowerPrice2 as TextView

        when(cnt){
            2 -> {
                if (btnState == 0)
                    btnDelBorrower.visibility = View.VISIBLE
                else
                    btnDelBorrower.visibility = View.INVISIBLE

                layBorrower = layBorrower2
                borrowerPrice = borrowerPrice2
            }

            3 -> {
                layBorrower = layBorrower3
                borrowerPrice = borrowerPrice3
            }

            4 -> {
                layBorrower = layBorrower4
                borrowerPrice = borrowerPrice4

                btnAddBorrower.isEnabled = true
            }

            5 -> {
                layBorrower = layBorrower5
                borrowerPrice = borrowerPrice5

                btnAddBorrower.isEnabled = false
            }
        }

        if(btnState == 0){
            layBorrower.visibility = View.VISIBLE
            borrowerPrice.visibility = View.VISIBLE
        }
        else{
            layBorrower.visibility = View.GONE
            borrowerPrice.visibility = View.GONE
        }
    }

    fun LayBorrower(cnt: Int){
        val params = btnDelBorrower.layoutParams as ConstraintLayout.LayoutParams
        var layBorrowerId = layBorrower2.id

        when(cnt){
            2 -> layBorrowerId = layBorrower2.id
            3 -> layBorrowerId = layBorrower3.id
            4 -> layBorrowerId = layBorrower4.id
            5 -> layBorrowerId = layBorrower5.id
        }

        params.topToTop = layBorrowerId
        params.bottomToBottom = layBorrowerId
        btnDelBorrower.requestLayout()
    }

    fun TextBorrowerPrice(cnt: Int, borrowerPrices: List<TextView>){
        if(price.text.toString() != ""){
            var borrowerPrice = 0
          
            borrowerPrice = if(cbN1.isChecked) price.text.toString().toInt()/cnt else price.text.toString().toInt()

            for(textView in borrowerPrices){
                textView.text = NumberFormat.getInstance(Locale.KOREA).format(borrowerPrice) + "원"
            }
        }
    }
}