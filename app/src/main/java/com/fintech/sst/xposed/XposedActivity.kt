package com.fintech.sst.xposed

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.fintech.sst.R
import com.fintech.sst.helper.RxBus
import com.fintech.sst.xposed.AlipayHook.*
import com.fintech.sst.xposed.PayHelperUtils.*
import com.lxf.recyclerhelper.BaseQuickAdapter
import com.lxf.recyclerhelper.BaseViewHolder
import kotlinx.android.synthetic.main.activity_xposed.*



class XposedActivity : AppCompatActivity() {

    private val dataList = mutableListOf<String>()
    private val adapter = RecyclerAdapter(R.layout.item_xposed,dataList)
    private val billReceiver = BillReceived()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xposed)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@XposedActivity)
            this.adapter = this@XposedActivity.adapter
        }

        RxBus.getDefault().toObservable(Message::class.java)
                .subscribe {
                    dataList.add(it.content)
                    adapter.notifyDataSetChanged()
                }
        button.setOnClickListener {
            RxBus.getDefault().send(Message().apply {
                content = "-----------------"
            })
        }


        val intentFilter = IntentFilter()
        intentFilter.addAction(BILLRECEIVED_ACTION)
        intentFilter.addAction(MSGRECEIVED_ACTION)
        intentFilter.addAction(QRCODERECEIVED_ACTION)
        intentFilter.addAction(TRADENORECEIVED_ACTION)
        intentFilter.addAction(LOGINIDRECEIVED_ACTION)
        intentFilter.addAction(SAVEALIPAYCOOKIE_ACTION)
        intentFilter.addAction(GETTRADEINFO_ACTION)
        registerReceiver(billReceiver, intentFilter)
    }

    internal class RecyclerAdapter(
            layoutId:Int,
            data:List<String>? = null
    ): BaseQuickAdapter<String, BaseViewHolder>(layoutId,data) {
        override fun convert(holder: BaseViewHolder?, item: String?) {
            holder?.setText(R.id.tv_content,item?:"null")
        }
    }


    internal class BillReceived : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action!!.contentEquals(MSGRECEIVED_ACTION)) {
                val msg = intent.getStringExtra("msg")
                RxBus.getDefault().send(Message().apply {
                    content = "==================\n$msg"
                })
            }
        }
    }
}
