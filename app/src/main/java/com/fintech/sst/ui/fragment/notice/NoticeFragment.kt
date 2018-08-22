package com.fintech.sst.ui.fragment.notice

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fintech.sst.R
import com.fintech.sst.base.BaseFragment
import com.fintech.sst.data.db.Notice
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.fragment_notice.*

class NoticeFragment : BaseFragment<NoticeContract.Presenter>(), NoticeContract.View {
    override val presenter: NoticeContract.Presenter = NoticePresenter(this)

    private var status: Int = 0
    private val adapter = NoticeAdapter(R.layout.item_notice_list)
    private var pageIndex = 1

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            status = arguments!!.getInt(PARAM_TYPE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = this@NoticeFragment.adapter.apply {
                setEmptyView(R.layout.empty_view_recycler,recyclerView)
            }
        }

        refreshLayout.apply {
            setRefreshHeader(ClassicsHeader(context))
            setRefreshFooter(ClassicsFooter(context))
            setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pageIndex++
                    presenter.noticeList(status,pageNow = pageIndex,append = true)
                }

                override fun onRefresh(refreshLayout: RefreshLayout) {
                    pageIndex = 1
                    presenter.noticeList(status,pageNow = pageIndex)
                }

            })
        }

        presenter.noticeList(status)
    }

    override fun loadMore(notices: List<Notice>?) {
        if (notices == null || notices.isEmpty()){
            showToast("没有更多数据了")
            refreshLayout.finishLoadMoreWithNoMoreData()
            return
        }
        refreshLayout.finishLoadMore()
        adapter.data.addAll(notices)
        adapter.notifyDataSetChanged()
    }

    override fun refreshData(notices: List<Notice>?) {
        refreshLayout.finishRefresh()
        adapter.setNewData(notices)
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
        // TODO: Update argument status and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        private val PARAM_TYPE = "status"

        fun newInstance(status: Int = 0): NoticeFragment {
            val fragment = NoticeFragment()
            val args = Bundle()
            args.putInt(PARAM_TYPE, status)
            fragment.arguments = args
            return fragment
        }
    }
}