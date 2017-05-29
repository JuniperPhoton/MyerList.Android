package com.juniperphoton.myerlist.presenter

import com.juniperphoton.myerlist.App
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.api.APIException
import com.juniperphoton.myerlist.api.CloudService
import com.juniperphoton.myerlist.api.response.CheckUserResponse
import com.juniperphoton.myerlist.api.response.GetSaltResponse
import com.juniperphoton.myerlist.api.response.LoginResponse
import com.juniperphoton.myerlist.api.response.RegisterResponse
import com.juniperphoton.myerlist.model.User
import com.juniperphoton.myerlist.view.LoginView

import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.exceptions.Exceptions
import rx.functions.Func1
import rx.schedulers.Schedulers

import com.juniperphoton.myerlist.BuildConfig.DEBUG
import com.juniperphoton.myerlist.util.*

class LoginPresenter(private val loginView: LoginView, private val mode: Int) : Presenter {
    private var email: String? = null
    private var password: String? = null
    private var password2: String? = null

    private val userFunc: Func1<LoginResponse, User>
        get() = Func1 { loginResponse ->
            val user = loginResponse.user
            if (user != null) {
                return@Func1 user
            }
            throw Exceptions.propagate(APIException(loginResponse.friendErrorMessage))
        }

    private val loginSubscriber: Subscriber<User>
        get() = object : Subscriber<User>() {
            override fun onCompleted() {

            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
                if (e is APIException) {
                    ToastService.sendShortToast(e.message!!)
                }
                loginView.navigateToMain(false)
            }

            override fun onNext(user: User) {
                LocalSettingUtil.putString(App.instance!!, Params.SID_KEY, user.sid.toString())
                LocalSettingUtil.putString(App.instance!!, Params.ACCESS_TOKEN_KEY, user.accessToken!!)
                LocalSettingUtil.putString(App.instance!!, Params.EMAIL_KEY, email!!)
                loginView.navigateToMain(true)
            }
        }

    private val isDataValid: Boolean
        get() {
            email = loginView.email
            password = loginView.password
            password2 = loginView.secondPassword

            if (email.isNullOrBlank()) {
                ToastService.sendShortToast(App.instance!!.getString(R.string.input_email_hint))
                return false
            }
            if (!email!!.isEmailFormat()) {
                ToastService.sendShortToast(App.instance!!.getString(R.string.invaild_email_hint))
                return false
            }

            if (password.isNullOrBlank()) {
                ToastService.sendShortToast(App.instance!!.getString(R.string.input_pwd_hint))
                return false
            }

            if (mode == Params.LoginMode.REGISTER) {
                if (password2.isNullOrBlank()) {
                    ToastService.sendShortToast(App.instance!!.getString(R.string.re_pwd_hint))
                    return false
                }

                if (password != password2) {
                    ToastService.sendShortToast(App.instance!!.getString(R.string.two_pwd_not_match_hint))
                    return false
                }
            }

            return true
        }

    private fun encryptPwd(pwd: String, salt: String): String {
        return Security.get32MD5Str(Security.get32MD5Str(pwd) + salt)
    }

    fun register() {
        if (!isDataValid) {
            loginView.navigateToMain(false)
            return
        }
        CloudService.checkUserExist(email!!)
                .subscribeOn(Schedulers.io())
                .flatMap(Func1<CheckUserResponse, Observable<RegisterResponse>> { response ->
                    if (!response.exist) {
                        if (DEBUG) {
                            return@Func1 CloudService.register(email!!, "6a311e59630cfd8372904e2a1f03aaf7")
                        }
                        return@Func1 CloudService.register(email!!, Security.get32MD5Str(password))
                    }
                    Observable.error<RegisterResponse>(APIException(App.instance!!
                            .getString(R.string.email_registered)))
                })
                .map(userFunc)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginSubscriber)
    }

    fun login() {
        if (!isDataValid) {
            loginView.navigateToMain(false)
            return
        }
        CloudService.checkUserExist(email!!)
                .subscribeOn(Schedulers.io())
                .flatMap(Func1<CheckUserResponse, Observable<GetSaltResponse>> { response ->
                    if (response.exist) {
                        return@Func1 CloudService.getSalt(email!!)
                    }
                    Observable.error<GetSaltResponse>(APIException(response.friendErrorMessage))
                })
                .flatMap(Func1<GetSaltResponse, Observable<LoginResponse>> { response ->
                    if (response.salt != null) {
                        if (DEBUG) {
                            return@Func1 CloudService.login(email!!, "6a311e59630cfd8372904e2a1f03aaf7")
                        }
                        return@Func1 CloudService.login(email!!, encryptPwd(password!!, response.salt))
                    }
                    Observable.error<LoginResponse>(APIException(response.friendErrorMessage))
                })
                .map(userFunc)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginSubscriber)
    }

    override fun start() {

    }

    override fun stop() {

    }
}
