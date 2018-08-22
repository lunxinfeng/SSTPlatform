package com.fintech.sst.ui.fragment.order

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fintech.sst.R
import com.fintech.sst.base.BaseFragment
import com.fintech.sst.net.bean.OrderList
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.fragment_order.*

class OrderFragment : BaseFragment<OrderContract.Presenter>(),OrderContract.View {
    override val presenter: OrderContract.Presenter = OrderPresenter(this)

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
        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = this@OrderFragment.adapter.apply {
                setEmptyView(R.layout.empty_view_recycler,recyclerView)
            }
        }

        refreshLayout.apply {
            setRefreshHeader(ClassicsHeader(context))
            setRefreshFooter(ClassicsFooter(context))
            setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageIndex++
                    presenter.orderList(type,pageNow = pageIndex,append = true)
                }

                override fun onRefresh(refreshLayout: RefreshLayout) {
                    pageIndex = 1
                    presenter.orderList(type,pageNow = pageIndex)
                }
            })
        }
        presenter.orderList(type)
    }

    override fun loadMore(orders: List<OrderList>?) {
        refreshLayout.finishLoadMore()
        if (orders == null || orders.isEmpty()){
            showToast("没有更多数据了")
            return
        }
        adapter.data.addAll(orders)
        adapter.notifyDataSetChanged()
    }

    override fun refreshData(orders: List<OrderList>?) {
        refreshLayout.finishRefresh()
        adapter.setNewData(orders)
    }

    override fun loadError(error: String) {
        showToast(error)
        refreshLayout.finishLoadMore(false)
        refreshLayout.finishRefresh()
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

        fun newInstance(type: Int = 0): OrderFragment {
            val fragment = OrderFragment()
            val args = Bundle()
            args.putInt(PARAM_TYPE, type)
            fragment.arguments = args
            return fragment
        }
    }
}
