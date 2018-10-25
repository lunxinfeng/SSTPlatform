package com.fintech.sst.ui.fragment.login.ali

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.transition.Fade
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.fintech.sst.R
import com.fintech.sst.base.BaseFragment
import com.fintech.sst.ui.activity.aisle.AisleManagerActivity
import com.fintech.sst.ui.fragment.login.account.LoginAccountFragment
import kotlinx.android.synthetic.main.fragment_login_ali.*


class LoginAliFragment : BaseFragment<LoginAliContract.Presenter>(), LoginAliContract.View {
    override val presenter: LoginAliContract.Presenter = LoginAliPresenter(this)
    override val context: FragmentActivity
        get() = activity!!

    override fun loginSuccess() {
        startActivity(Intent(activity, AisleManagerActivity::class.java))
        activity?.finish()
    }

    override fun loginFail(hint: String) {
        showToast(hint)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_ali, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragment = LoginAccountFragment.newInstance()
        fragment.sharedElementEnterTransition = Fade()
        fragment.enterTransition = Fade()
        exitTransition = Fade()
        fragment.sharedElementReturnTransition = Fade()

        val btnLoginAli = view.findViewById<Button>(R.id.btnLoginAli)
        btnLoginAli.setOnClickListener { presenter.aliLogin() }
        btnLoginWechat.setOnClickListener { presenter.wechatLogin() }
    }

    companion object {
        fun newInstance(): LoginAliFragment {
            val fragment = LoginAliFragment()
            return fragment
        }
    }

}
