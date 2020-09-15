package com.app0.simforpay.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.app0.simforpay.R
import kotlinx.android.synthetic.main.contract_item.view.*


//class ContractAdapter(private val contractList: List<ContractData>) : RecyclerView.Adapter<ContractAdapter.ContractViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ContractViewHolder {
//        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.contract_item, parent, false)
//
//        return ContractViewHolder(itemView)
//    }
//
//    override fun onBindViewHolder(holder: ContractViewHolder, position: Int) {
//        val currentItem = contractList[position]
//
//        holder.name.text = currentItem.name
//        holder.content.text = currentItem.content
//    }
//
//    override fun getItemCount() = contractList.size
//
//    class ContractViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val name: AutofitTextView = itemView.contractName
//        val content: AutofitTextView = itemView.contractContent
//    }
//}

class ContractAdapter(private val models: List<ContractData>, context: Context) : PagerAdapter() {

    private val layoutInflater: LayoutInflater? = null
    private var context: Context? = null

    override fun getCount(): Int {
        return models.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val view: View = LayoutInflater.from(context).inflate(R.layout.contract_item, container, false)

        view.contractName.text = models[position].name
        view.contractContent.text = models[position].content

        view.setOnClickListener{
            // button click
        }

        container.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    init {
        this.context = context
    }
}