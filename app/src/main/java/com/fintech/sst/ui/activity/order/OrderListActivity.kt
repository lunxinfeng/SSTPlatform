package com.fintech.sst.ui.activity.order

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import com.fintech.sst.R
import com.fintech.sst.helper.METHOD_ALI
import com.fintech.sst.helper.METHOD_BANK
import com.fintech.sst.helper.METHOD_WECHAT
import com.fintech.sst.helper.METHOD_YUN
import com.fintech.sst.ui.fragment.order.OrderFragment
import kotlinx.android.synthetic.main.activity_order_list.*


class OrderListActivity : AppCompatActivity(),OrderFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val type = intent.getStringExtra("type")
        when(type){
            METHOD_ALI -> setTheme(R.style.App_Ali)
            METHOD_WECHAT -> setTheme(R.style.App_WeChat)
            METHOD_BANK -> setTheme(R.style.App_Bank)
            METHOD_YUN -> setTheme(R.style.App_Yun)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }


        val fragmentListAli = mutableListOf<OrderFragment>()
        fragmentListAli.add(OrderFragment.newInstance(0, type))
        fragmentListAli.add(OrderFragment.newInstance(10, type))
        fragmentListAli.add(OrderFragment.newInstance(20, type))
        fragmentListAli.add(OrderFragment.newInstance(30, type))
        fragmentListAli.add(OrderFragment.newInstance(40, type))

//        val fragmentListWeChat = mutableListOf<OrderFragment>()
//        fragmentListWeChat.add(OrderFragment.newInstance(0, METHOD_WECHAT))
//        fragmentListWeChat.add(OrderFragment.newInstance(10, METHOD_WECHAT))
//        fragmentListWeChat.add(OrderFragment.newInstance(20, METHOD_WECHAT))
//        fragmentListWeChat.add(OrderFragment.newInstance(30, METHOD_WECHAT))
//        fragmentListWeChat.add(OrderFragment.newInstance(40, METHOD_WECHAT))

        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, fragmentListAli)
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

//        bottomNavigationView.setOnNavigationItemSelectedListener {
//            when(it.itemId){
//                R.id.item_ali -> {
//                    viewPager.adapter = ViewPagerAdapter(supportFragmentManager, fragmentListAli)
//                }
//
//                R.id.item_wechat -> {
//                    viewPager.adapter = ViewPagerAdapter(supportFragmentManager, fragmentListWeChat)
//                }
//            }
//            false
//        }
    }
}
