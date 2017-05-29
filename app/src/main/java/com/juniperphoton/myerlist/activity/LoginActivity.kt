package com.juniperphoton.myerlist.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import com.juniperphoton.myerlist.BuildConfig
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.presenter.LoginPresenter
import com.juniperphoton.myerlist.util.Params
import com.juniperphoton.myerlist.view.LoginView
import kotlinx.android.synthetic.main.activity_login.*

@Suppress("unused")
class LoginActivity : BaseActivity(), LoginView {
    private var presenter: LoginPresenter? = null

    private var loginMode: Int = 0

    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ButterKnife.bind(this)
        initViews()
    }

    private fun initViews() {
        val intent = intent
        loginMode = intent.getIntExtra(Params.LOGIN_MODE, -1)
        when (loginMode) {
            Params.LoginMode.LOGIN -> {
                loginTitleView!!.text = getString(R.string.login_title)
                passwordSecViewRoot!!.visibility = View.GONE
                loginButton!!.text = getString(R.string.login)
            }
            Params.LoginMode.REGISTER -> {
                loginTitleView!!.text = getString(R.string.register_title)
                passwordSecViewRoot!!.visibility = View.VISIBLE
                loginButton!!.text = getString(R.string.register)
            }
        }
        if (BuildConfig.DEBUG) {
            emailView!!.setText("dengweichao@hotmail.com")
            passwordView.setText("test");
            //passwordSecView.setText("test");
        }
        presenter = LoginPresenter(this, loginMode)
    }

    override val email: String
        get() = emailView!!.text.toString()

    override val password: String
        get() = passwordView!!.text.toString()

    override val secondPassword: String
        get() = passwordSecView!!.text.toString()

    @OnClick(R.id.loginButton)
    fun onClickButton() {
        dialog = ProgressDialog(this, ProgressDialog.STYLE_SPINNER)
        dialog!!.setTitle(getString(R.string.loading_hint))
        dialog!!.setMessage(getString(R.string.waitting))
        dialog!!.setCancelable(false)
        dialog!!.show()
        if (loginMode == Params.LoginMode.LOGIN) {
            presenter!!.login()
        } else {
            presenter!!.register()
        }
    }

    override fun navigateToMain(ok: Boolean) {
        dialog!!.dismiss()
        if (ok) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
