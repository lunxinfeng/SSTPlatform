package com.fintech.sst.ui.activity.notice

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import com.fintech.sst.R
import com.fintech.sst.ui.fragment.notice.NoticeFragment
import kotlinx.android.synthetic.main.activity_aisle_list.*

class NoticeListActivity : AppCompatActivity(), NoticeFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aisle_list)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val fragmentList = mutableListOf<NoticeFragment>()
        fragmentList.add(NoticeFragment.newInstance())
        fragmentList.add(NoticeFragment.newInstance(1))
        fragmentList.add(NoticeFragment.newInstance(2))
        fragmentList.add(NoticeFragment.newInstance(3))
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
