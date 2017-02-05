package com.juniperphoton.myerlistandroid.presenter;

import com.juniperphoton.myerlistandroid.App;
import com.juniperphoton.myerlistandroid.R;
import com.juniperphoton.myerlistandroid.api.APIException;
import com.juniperphoton.myerlistandroid.api.CloudService;
import com.juniperphoton.myerlistandroid.api.response.CheckUserResponse;
import com.juniperphoton.myerlistandroid.api.response.GetSaltResponse;
import com.juniperphoton.myerlistandroid.api.response.LoginResponse;
import com.juniperphoton.myerlistandroid.model.User;
import com.juniperphoton.myerlistandroid.util.DataUtil;
import com.juniperphoton.myerlistandroid.util.LocalSettingUtil;
import com.juniperphoton.myerlistandroid.util.Params;
import com.juniperphoton.myerlistandroid.util.Security;
import com.juniperphoton.myerlistandroid.util.ToastService;
import com.juniperphoton.myerlistandroid.view.LoginView;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LoginPresenter implements Presenter {
    private boolean DEBUG = true;

    private LoginView mLoginView;
    private int mMode;

    private String mEmail;
    private String mPassword;
    private String mPassword2;

    public LoginPresenter(LoginView loginView, int mode) {
        mLoginView = loginView;
        mMode = mode;
    }

    private boolean isDataValid() {
        mEmail = mLoginView.getEmail();
        mPassword = mLoginView.getPassword();
        mPassword2 = mLoginView.getSecondPassword();

        if (DataUtil.isStringNullOrEmpty(mEmail)) {
            ToastService.sendShortToast(App.getInstance().getString(R.string.input_email_hint));
            return false;
        }
        if (!DataUtil.isEmailFormat(mEmail)) {
            ToastService.sendShortToast(App.getInstance().getString(R.string.invaild_email_hint));
            return false;
        }

        if (DataUtil.isStringNullOrEmpty(mPassword)) {
            ToastService.sendShortToast(App.getInstance().getString(R.string.input_pwd_hint));
            return false;
        }

        if (mMode == Params.LoginMode.REGISTER) {
            if (DataUtil.isStringNullOrEmpty(mPassword2)) {
                ToastService.sendShortToast(App.getInstance().getString(R.string.re_pwd_hint));
                return false;
            }

            if (!mPassword.equals(mPassword2)) {
                ToastService.sendShortToast(App.getInstance().getString(R.string.two_pwd_not_match_hint));
                return false;
            }
        }

        return true;
    }

    private String encrpyPwd(String pwd, String salt) {
        return Security.get32MD5Str(Security.get32MD5Str(pwd) + salt);
    }

    public void register() {

    }

    public void login() {
        if (!isDataValid()) {
            return;
        }
        CloudService.getInstance().checkUserExist(mEmail)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Func1<CheckUserResponse, Observable<GetSaltResponse>>() {
                    @Override
                    public Observable<GetSaltResponse> call(CheckUserResponse response) {
                        if (response.getExist()) {
                            return CloudService.getInstance().getSalt(mEmail);
                        }
                        return Observable.error(new APIException(response.getFriendErrorMessage()));
                    }
                })
                .flatMap(new Func1<GetSaltResponse, Observable<LoginResponse>>() {
                    @Override
                    public Observable<LoginResponse> call(GetSaltResponse response) {
                        if (response.getSalt() != null) {
                            if (DEBUG) {
                                return CloudService.getInstance().login(mEmail, "6a311e59630cfd8372904e2a1f03aaf7");
                            }
                            return CloudService.getInstance().login(mEmail, encrpyPwd(mPassword, response.getSalt()));
                        }
                        return Observable.error(new APIException(response.getFriendErrorMessage()));
                    }
                })
                .map(new Func1<LoginResponse, User>() {
                    @Override
                    public User call(LoginResponse loginResponse) {
                        User user = loginResponse.getUser();
                        if (user != null) {
                            return user;
                        }
                        throw Exceptions.propagate(new APIException(loginResponse.getFriendErrorMessage()));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastService.sendShortToast(e.getMessage());
                        mLoginView.afterLogin(false);
                    }

                    @Override
                    public void onNext(User user) {
                        LocalSettingUtil.putString(App.getInstance(), Params.SID_KEY, String.valueOf(user.getSID()));
                        LocalSettingUtil.putString(App.getInstance(), Params.ACCESS_TOKEN_KEY, user.getAccessToken());
                        LocalSettingUtil.putString(App.getInstance(), Params.EMAIL_KEY, mEmail);
                        mLoginView.afterLogin(true);
                    }
                });
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
