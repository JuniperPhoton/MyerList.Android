package com.juniperphoton.myerlist.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import butterknife.ButterKnife
import butterknife.OnClick
import com.juniperphoton.myerlist.BuildConfig
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.extension.createIntent
import com.juniperphoton.myerlist.extension.getResString
import com.juniperphoton.myerlist.presenter.LoginPresenter
import com.juniperphoton.myerlist.util.KeyboardUtil
import com.juniperphoton.myerlist.util.Params
import com.juniperphoton.myerlist.view.LoginView
import kotlinx.android.synthetic.main.activity_login.*

@Suppress("unused", "unused_parameter")
class LoginActivity : BaseActivity(), LoginView {
    private var presenter: LoginPresenter? = null

    private var loginMode: Int = 0

    private var dialog: ProgressDialog? = null

    override val email: String
        get() = emailView.text.toString()

    override val password: String
        get() = passwordView.text.toString()

    override val secondPassword: String
        get() = passwordSecView.text.toString()

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
                loginTitleView.text = R.string.login_title.getResString()
                passwordSecViewRoot.visibility = View.GONE
                loginButton.text = R.string.login.getResString()
            }
            Params.LoginMode.REGISTER -> {
                loginTitleView.text = R.string.register_title.getResString()
                passwordSecViewRoot.visibility = View.VISIBLE
                loginButton.text = R.string.register.getResString()
            }
        }

        if (BuildConfig.DEBUG) {
            emailView.setText(R.string.email_dev.getResString())
        }

        val listener = TextView.OnEditorActionListener handle@ { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                KeyboardUtil.hide(this, passwordView.windowToken)
                onClickButton()
                return@handle true
            }
            return@handle false
        }

        passwordView.setOnEditorActionListener(listener)
        passwordSecView.setOnEditorActionListener(listener)

        presenter = LoginPresenter(this, loginMode)
    }

    override fun onPause() {
        super.onPause()
        dialog?.dismiss()
    }

    @OnClick(R.id.loginButton)
    fun onClickButton() {
        dialog = ProgressDialog(this, ProgressDialog.STYLE_SPINNER)
        dialog?.apply {
            setTitle(R.string.loading_hint.getResString())
            setMessage(R.string.waiting.getResString())
            setCancelable(false)
            show()
            if (loginMode == Params.LoginMode.LOGIN) {
                presenter!!.login()
            } else {
                presenter!!.register()
            }
        }
    }

    override fun dismissDialog() {
        dialog?.dismiss()
    }

    override fun navigateToMain() {
        val intent = createIntent<MainActivity>()
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}