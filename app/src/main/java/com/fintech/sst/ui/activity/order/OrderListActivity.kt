package com.fintech.sst.ui.activity.order

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import com.fintech.sst.R
import com.fintech.sst.ui.fragment.order.OrderFragment
import kotlinx.android.synthetic.main.activity_order_list.*


class OrderListActivity : AppCompatActivity(),OrderFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val fragmentList = mutableListOf<OrderFragment>()
        fragmentList.add(OrderFragment.newInstance())
        fragmentList.add(OrderFragment.newInstance(10))
        fragmentList.add(OrderFragment.newInstance(20))
        fragmentList.add(OrderFragment.newInstance(30))
        fragmentList.add(OrderFragment.newInstance(40))
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, fragmentList)
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
    }
}
