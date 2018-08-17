package com.fintech.sst.ui.fragment.notice

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ajguan.library.EasyRefreshLayout
import com.fintech.sst.R
import com.fintech.sst.base.BaseFragment
import com.fintech.sst.net.bean.OrderList
import com.fintech.sst.ui.fragment.order.OrderAdapter
import kotlinx.android.synthetic.main.fragment_notice.*

class NoticeFragment : BaseFragment<NoticeContract.Presenter>(), NoticeContract.View {
    override val presenter: NoticeContract.Presenter = NoticePresenter(this)

    private var type: Int = 0
    private val adapter = OrderAdapter(R.layout.item_order_list)
    private var pageIndex = 1

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            type = arguments!!.getInt(PARAM_TYPE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.apply {
//            isUpFetchEnable = true
//            setUpFetchListener {
//                println("NoticeFragment.onViewCreated:上拉刷新")
//                presenter.noticeList(type)
//            }
//            setOnLoadMoreListener({ presenter.noticeList(type) },recyclerView)
//            disableLoadMoreIfNotFullPage()
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = this@NoticeFragment.adapter
        }

        easyRefreshLayout.addEasyEvent(object : EasyRefreshLayout.EasyEvent {
            override fun onLoadMore() {
                pageIndex++
                presenter.noticeList(type,pageNow = pageIndex,append = true)
            }

            override fun onRefreshing() {
                pageIndex = 1
                presenter.noticeList(type,pageNow = pageIndex)
            }
        })

        presenter.noticeList(type)
    }

    override fun loadMore(orders: List<OrderList>) {
        easyRefreshLayout.loadMoreComplete()
        adapter.data.addAll(orders)
        adapter.notifyDataSetChanged()
    }

    override fun refreshData(orders: List<OrderList>) {
        easyRefreshLayout.refreshComplete()
        adapter.setNewData(orders)
    }

    override fun loadError(error: String) {
        showToast(error)
        easyRefreshLayout.loadMoreFail()
        easyRefreshLayout.refreshComplete()
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        private val PARAM_TYPE = "type"

        fun newInstance(type: Int = 0): NoticeFragment {
            val fragment = NoticeFragment()
            val args = Bundle()
            args.putInt(PARAM_TYPE, type)
            fragment.arguments = args
            return fragment
        }
    }
}