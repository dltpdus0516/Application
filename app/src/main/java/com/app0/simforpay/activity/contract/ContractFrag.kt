package com.app0.simforpay.activity.contract

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app0.simforpay.R
import com.app0.simforpay.activity.MainAct
import com.app0.simforpay.retrofit.RetrofitHelper
import com.app0.simforpay.retrofit.domain.*
import com.app0.simforpay.util.TextInput
import com.app0.simforpay.util.dialog.CustomDialog
import com.app0.simforpay.util.sharedpreferences.Key
import com.app0.simforpay.util.sharedpreferences.MyApplication
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hendraanggrian.appcompat.widget.Mention
import com.hendraanggrian.appcompat.widget.MentionArrayAdapter
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView
import kotlinx.android.synthetic.main.frag_contract.*
import me.grantland.widget.AutofitTextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

private const val ARG_PARAM1 = "id"
private const val ARG_PARAM2 = "title"
private const val ARG_PARAM3 = "borrowDate"
private const val ARG_PARAM4 = "paybackDate"
private const val ARG_PARAM5 = "price"
private const val ARG_PARAM6 = "lenderName"
private const val ARG_PARAM7 = "lenderBank"
private const val ARG_PARAM8 = "lenderAccount"
private const val ARG_PARAM9 = "borrower"
private const val ARG_PARAM10 = "penalty"
private const val ARG_PARAM11 = "content"
private const val ARG_PARAM12 = "alarm"
private const val ARG_PARAM13 = "state"

class ContractFrag : Fragment() {

    private val Retrofit = RetrofitHelper.getRetrofit()
    private lateinit var callback: OnBackPressedCallback
    private val calendar = Calendar.getInstance()
    private val friendsInfo = mutableMapOf<String, Pair<String, Pair<String, String>>>()
    lateinit var userInfo: User

    private var getid: Int? = null
    private var gettitle: String? = null
    private var getborrowDate: String? = null
    private var getpaybackDate: String? = null
    private var getprice: Int = 0
    private var getlenderName: String? = null
    private var getlenderBank: String? = null
    private var getlenderAccount: Int? = null
    private var getborrower: String? = null
    private var getPenalty: String? = null
    private var getcontent: String? = null
    private var getalarm: Int? = null
    private var getstate: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainAct = activity as MainAct
        mainAct.HideBottomNavi(true)

        arguments?.let {
            getid = it.getInt(ARG_PARAM1)
            gettitle = it.getString(ARG_PARAM2)
            getborrowDate = it.getString(ARG_PARAM3)
            getpaybackDate = it.getString(ARG_PARAM4)
            getprice = it.getInt(ARG_PARAM5)
            getlenderName = it.getString(ARG_PARAM6)
            getlenderBank = it.getString(ARG_PARAM7)
            getlenderAccount = it.getInt(ARG_PARAM8)
            getborrower = it.getString(ARG_PARAM9)
            getPenalty = it.getString(ARG_PARAM10)
            getcontent = it.getString(ARG_PARAM11)
            getalarm = it.getInt(ARG_PARAM12)
            getstate = it.getInt(ARG_PARAM13)
        }
    }

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

        val mentionAdapter: ArrayAdapter<Mention> = MentionArrayAdapter(this.requireContext())

        var id=Integer.parseInt(MyApplication.prefs.getString(Key.LENDER_ID.toString(), ""))

        Retrofit.getFreinds(id).enqueue(object : Callback<ArrayList<FriendsSuccess>> {
            override fun onResponse(call: Call<ArrayList<FriendsSuccess>>, response: Response<ArrayList<FriendsSuccess>>) {

                mentionAdapter.clear()

                response.body()?.forEach {
                    val friendList = Pair(it.friendsBank, it.friendsAccount)

                    friendsInfo[it.friendsName] = Pair(it.friendsId,friendList)
                    mentionAdapter.add(Mention(it.friendsName, it.friendsMyid))
                }
                Retrofit.getUser(id).enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        userInfo = response.body()!!
                        mentionAdapter.add(Mention(response.body()?.name.toString(), response.body()?.myId.toString()))
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {}

                })
            }

            override fun onFailure(call: Call<ArrayList<FriendsSuccess>>, t: Throwable) {

            }
        })

//        Log.d("size", mentionAdapter.count.toString())

        lender.mentionAdapter = mentionAdapter
        borrower1.mentionAdapter = mentionAdapter
        borrower2.mentionAdapter = mentionAdapter
        borrower3.mentionAdapter = mentionAdapter
        borrower4.mentionAdapter = mentionAdapter
        borrower5.mentionAdapter = mentionAdapter

        lender.setOnItemClickListener { adapterView, view, i, l ->
            adapterView.adapter = mentionAdapter

            val name = lender.text.toString().replace("@", "").trim()
            if(userInfo.name == name){
                bank?.setText(userInfo.bank)
                accountNum?.setText(userInfo.account)
            }else{
                bank?.setText(friendsInfo[name]?.second?.first)
                accountNum?.setText(friendsInfo[name]?.second?.second)
            }
        }

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

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume() {
        super.onResume()

        var cnt = 1 // borrower cnt
        val borrowerNames = listOf(borrower1, borrower2, borrower3, borrower4, borrower5)
        val borrowerPrices = listOf(borrowerPrice1, borrowerPrice2, borrowerPrice3, borrowerPrice4, borrowerPrice5)

        //Contract modify edittext set the text
        if(getid.toString() != "null") setContract(borrowerNames, borrowerPrices)

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

            if (!tradeDay.text.isNullOrEmpty())
                ShowDatePickerDialog(complDay, "compl")
            else
                Toast.makeText(context, "거래일을 입력해주세요.", Toast.LENGTH_SHORT).show()
        }

        btnDelComplDay.setOnClickListener {
            complDay.setText("")
            swAlert.isChecked = false
            swAlert.isEnabled = false
        }

        var pointNumStr: String = ""

        // Set individual Price
        price.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                TextBorrowerPrice(cnt, borrowerPrices)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && s.toString() != pointNumStr) {
                    pointNumStr = DecimalFormat("###,###").format(Integer.parseInt( s.toString().replace(",","") ))
//                    Log.e("price", pointNumStr)
                    price.setText(pointNumStr)
                    price.setSelection(pointNumStr.length)  //커서를 오른쪽 끝으로 보냄
                }
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
            if (isChecked) {
                val randomNum = Random().nextInt(randomPenalty.size)

                penalty.setText(randomPenalty[randomNum])
            } else penalty.setText("")
        }

        btnSave.setOnClickListener {
            val user_id = Integer.parseInt(MyApplication.prefs.getString(Key.LENDER_ID.toString(), ""))
            val title = contractName.text.toString()
            val borrow_date = tradeDay.text.toString()
            val payback_date = complDay.text.toString()
            var priceInt = price.text.toString().replace(",","")
            val price = Integer.parseInt(priceInt)
            val lender_name = lender.text.toString().replace("@", "").trim()

            var lender_id = if(lender.text.toString().contains("@")){
                if(userInfo.name == lender_name){
                    userInfo.id
                }else{
                    Integer.parseInt(friendsInfo[lender_name]?.first)
                }
            } else 1

            val lender_bank = bank.text.toString()
            val lender_account: Int? = accountNum.text.toString().toIntOrNull()
            val borrowerList = arrayListOf<Borrower>()
            val penalty = penalty.text.toString()
            val alarm = if (swAlert.isChecked) 1 else 0

            val payback_state = 0

            var borrowerName = borrowerNames[0].text.toString().replace("@", "").trim()
            var borrower_price: Int? = 0

            for (i in 0..cnt-1) {
                val userName = borrowerNames[i].text.toString().replace("@", "").trim()
                var borrower_id : Int = 1
                if(borrowerNames[i].text.toString().contains("@")){
                    if(userInfo.name == userName){
                        borrower_id = userInfo.id
                    }else{
                        borrower_id = Integer.parseInt(friendsInfo[userName]?.first)
                    }
                }
                borrower_price = borrowerPrices[i].text.toString().replace("원", "").replace(",", "").trim().toIntOrNull()

                borrowerList.add(Borrower(borrower_id, userName, borrower_price, payback_state))
                if (i >= 1) borrowerName = "$userName,$borrowerName"
            }

            var content = "${borrowerName}은(는) ${lender_name}에게 "
            if (payback_date != "") content = content + payback_date + "까지 "
            content = content + borrower_price.toString() + "원을 갚을 것을 약속합니다."
            if (penalty != "") content = content + " 만약 이행하지 못할시에는 " + penalty + "를 할 것 입니다."

            val contractInfo = Contract(
                user_id,
                title,
                borrow_date,
                payback_date,
                price,
                lender_id,
                lender_name,
                lender_bank,
                lender_account,
                borrowerList,
                penalty,
                content,
                alarm
            )

            if (getid.toString() == "null") { // 작성
                MyApplication.prefs.setBoolean(Key.ContractConnect.toString(), true)

                Retrofit.ContractCall(contractInfo)
                    .enqueue(object : Callback<ContractSuccess> {
                        override fun onResponse(
                            call: Call<ContractSuccess>,
                            response: Response<ContractSuccess>
                        ) {
                            if (response.body()?.result == "true") {
                                fragmentManager!!.beginTransaction().replace(R.id.layFull, ContractShareFrag.newInstance(contractName.text.toString(), content)).commit()

                            } else
                                Toast.makeText(context, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(call: Call<ContractSuccess>, t: Throwable) {

                        }
                    })
            } else {
                MyApplication.prefs.setBoolean(Key.ContractConnect.toString(), false)

                Retrofit.UpdateContract(getid, contractInfo)
                    .enqueue(object : Callback<ResResultSuccess> {
                        override fun onResponse(
                            call: Call<ResResultSuccess>,
                            response: Response<ResResultSuccess>
                        ) {
                            if (response.body()?.result == "true") {
                                fragmentManager!!.beginTransaction().replace(R.id.layFull, ContractShareFrag.newInstance(title, content)).commit()

                            } else
                                Toast.makeText(context, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(call: Call<ResResultSuccess>, t: Throwable) {

                        }
                    })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        val mainAct = activity as MainAct
        mainAct.HideBottomNavi(false)
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
                    if(getid.toString() == "null"){
                        findNavController().navigate(R.id.action_fragContract_to_fragHome)
                    }
                    else{
                        fragmentManager?.popBackStackImmediate()
                    }
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

    @RequiresApi(Build.VERSION_CODES.Q)
    fun PriceComma(str: String, editText: TextInputEditText?, textView: AutofitTextView?) {
        var pointNumStr = ""

        if(!TextUtils.isEmpty(str) && str != pointNumStr) {
            pointNumStr = DecimalFormat("###,###").format(Integer.parseInt(str.replace(",","") ))

            if (editText != null) {
                editText.setText(pointNumStr)
                editText.setSelection(pointNumStr.length)  //커서를 오른쪽 끝으로 보냄
            }
            else if (textView != null){
                textView.text = pointNumStr + "원"
            }
        }
    }

    fun VisibilBorrower(cnt: Int, btnState: Int) {
        var layBorrower = layBorrower2 as TextInputLayout
        var borrowerPrice = borrowerPrice2 as TextView

        when (cnt) {
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

        if (btnState == 0) {
            layBorrower.visibility = View.VISIBLE
            borrowerPrice.visibility = View.VISIBLE
        } else {
            layBorrower.visibility = View.GONE
            borrowerPrice.visibility = View.GONE
        }
    }

    fun LayBorrower(cnt: Int) {
        val params = btnDelBorrower.layoutParams as ConstraintLayout.LayoutParams
        var layBorrowerId = layBorrower2.id

        when (cnt) {
            2 -> layBorrowerId = layBorrower2.id
            3 -> layBorrowerId = layBorrower3.id
            4 -> layBorrowerId = layBorrower4.id
            5 -> layBorrowerId = layBorrower5.id
        }

        params.topToTop = layBorrowerId
        params.bottomToBottom = layBorrowerId
        btnDelBorrower.requestLayout()
    }

    fun TextBorrowerPrice(cnt: Int, borrowerPrices: List<TextView>) {
        if (price.text.toString() != "") {
            var priceStr = price.text.toString().replace(",","")
            val priceInt = if (cbN1.isChecked) priceStr.toInt() / cnt else priceStr.toInt()

            for (textView in borrowerPrices) {
                textView.text = NumberFormat.getInstance(Locale.KOREA).format(priceInt).toString() + "원"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setContract(borrowerNames: List<SocialAutoCompleteTextView>, borrowerPrices: List<AutofitTextView>) {
        var allBorrower: List<String>
        var oneBorrower: List<String>
        var names: List<String> = emptyList()
        var prices: List<String> = emptyList()
        var cnt = 0

        contractName.setText(gettitle)
        tradeDay.setText(getborrowDate)
        complDay.setText(getpaybackDate)

        if (getprice.toString() != "0") {
            PriceComma(getprice.toString(), price, null)
        }

        lender.setText("@$getlenderName")
        bank.setText(getlenderBank)

        if (getlenderAccount.toString() != "0" && getlenderAccount.toString() != "null") {
            accountNum.setText(getlenderAccount.toString())
        }

        if (getborrower.toString() != "null") {
            allBorrower = getborrower!!.split("!")

            for (i in 0 until allBorrower.size-1) {
                // 마지막 공백은 제외, index=0 시작
                // ..(마지막 수 포함), until (마지막 수 미포함)
               oneBorrower = allBorrower[i].split(",")
                names += oneBorrower[3]
                prices += oneBorrower[4]
                cnt++
            }

            if (cnt != 1) { // 최소 한 명의 빌리는 사람이 존재
                VisibilBorrower(cnt, 0)
                LayBorrower(cnt)
            }
            if (prices[0] != getprice.toString()) {
                cbN1.isChecked = true
            }

            setBorrower(cnt, names, prices, borrowerNames, borrowerPrices)
        }

        penalty.setText(getPenalty)

        if (getalarm == 1) {
            swAlert.isEnabled = true
            swAlert.isChecked = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun setBorrower(
        cnt: Int,
        names: List<String>,
        prices: List<String>,
        borrowerNames: List<SocialAutoCompleteTextView>,
        borrowerPrices: List<AutofitTextView>
    ) {
        var pointNumStr = ""

        for(i in 0 until cnt){
            borrowerNames[i].setText("@" + names[i])
            PriceComma(prices[i], null, borrowerPrices[i])
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            id: Int,
            title: String,
            borrowerDate: String,
            paybackDate: String,
            price: Int,
            lenderName: String,
            lenderBank: String,
            lenderAccount: Int,
            borrower: String,
            penalty: String,
            content: String,
            alarm: Int,
            state: Int
        ) =
            ContractFrag().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, id)
                    putString(ARG_PARAM2, title)
                    putString(ARG_PARAM3, borrowerDate)
                    putString(ARG_PARAM4, paybackDate)
                    putInt(ARG_PARAM5, price)
                    putString(ARG_PARAM6, lenderName)
                    putString(ARG_PARAM7, lenderBank)
                    putInt(ARG_PARAM8, lenderAccount)
                    putString(ARG_PARAM9, borrower)
                    putString(ARG_PARAM10, penalty)
                    putString(ARG_PARAM11, content)
                    putInt(ARG_PARAM12, alarm)
                    putInt(ARG_PARAM13, state)
                }
            }
    }
}
