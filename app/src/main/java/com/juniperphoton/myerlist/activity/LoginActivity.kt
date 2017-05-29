package com.juniperphoton.myerlist.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.juniperphoton.myerlist.BuildConfig
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.presenter.LoginPresenter
import com.juniperphoton.myerlist.util.Params
import com.juniperphoton.myerlist.view.LoginView

class LoginActivity : BaseActivity(), LoginView {
    @JvmField
    @BindView(R.id.login_email)
    var emailView: EditText? = null

    @JvmField
    @BindView(R.id.login_pwd)
    var passwordView: EditText? = null

    @JvmField
    @BindView(R.id.login_second_pwd)
    var secondPasswordView: EditText? = null

    @JvmField
    @BindView(R.id.login_second_pwd_layout)
    var secondPasswordLayout: TextInputLayout? = null

    @JvmField
    @BindView(R.id.login_title)
    var title: TextView? = null

    @JvmField
    @BindView(R.id.login_btn)
    var loginButton: Button? = null

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
                title!!.text = getString(R.string.login_title)
                secondPasswordLayout!!.visibility = View.GONE
                loginButton!!.text = getString(R.string.login)
            }
            Params.LoginMode.REGISTER -> {
                title!!.text = getString(R.string.register_title)
                secondPasswordLayout!!.visibility = View.VISIBLE
                loginButton!!.text = getString(R.string.register)
            }
        }
        if (BuildConfig.DEBUG) {
            emailView!!.setText("dengweichao@hotmail.com")
            //passwordView.setText("test");
            //secondPasswordView.setText("test");
        }
        presenter = LoginPresenter(this, loginMode)
    }

    override val email: String
        get() = emailView!!.text.toString()

    override val password: String
        get() = passwordView!!.text.toString()

    override val secondPassword: String
        get() = secondPasswordView!!.text.toString()

    @OnClick(R.id.login_btn)
    internal fun onClickButton() {
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
