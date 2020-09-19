package com.app0.simforpay.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app0.simforpay.R
import com.app0.simforpay.adapter.Data
import com.app0.simforpay.adapter.FriendsAdapter
import kotlinx.android.synthetic.main.frag_friends.*

class FriendsFrag : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val frList = generateDummyList(3)

        rvFriends.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
        rvFriends.adapter = FriendsAdapter(frList, requireContext())
        rvFriends.setHasFixedSize(true)
    }

    private fun generateDummyList(size: Int): List<Data> {
        val list = ArrayList<Data>()

        for (i in 0 until size) {
            val item = Data("이름", "@아이디")
            list += item
        }

        return list
    }

}