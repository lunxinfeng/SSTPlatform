package com.fintech.sst.ui.activity.notice

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.fintech.sst.ui.fragment.notice.NoticeFragment

class ViewPagerAdapter(fragmentManager: FragmentManager,
                       private val fragmentList: List<NoticeFragment>): FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int = fragmentList.size
}