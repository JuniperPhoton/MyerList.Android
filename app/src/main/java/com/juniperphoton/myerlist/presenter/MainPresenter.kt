package com.juniperphoton.myerlist.presenter

import android.util.Log
import com.google.gson.Gson
import com.juniperphoton.myerlist.App
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.api.APIException
import com.juniperphoton.myerlist.api.CloudService
import com.juniperphoton.myerlist.api.response.AddToDoResponse
import com.juniperphoton.myerlist.api.response.CommonResponse
import com.juniperphoton.myerlist.api.response.GetOrderResponse
import com.juniperphoton.myerlist.model.CategoryRespInformation
import com.juniperphoton.myerlist.model.ToDo
import com.juniperphoton.myerlist.model.ToDoCategory
import com.juniperphoton.myerlist.realm.RealmUtils
import com.juniperphoton.myerlist.util.*
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class MainPresenter(private val mView: MainContract.View) : MainContract.Presenter {

    override fun getCategories() {
        CloudService.getCategories()
                .subscribeOn(Schedulers.io())
                .map({ cateResponse ->
                    parseCategories(cateResponse.rawString)
                    null
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Any>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }

                    override fun onNext(cateResponse: Any) {
                        mView.displayCategories()
                        getToDos()
                    }
                })
    }

    override fun getToDos() {
        CloudService.getToDos()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap({ toDoResponse ->
                    if (toDoResponse.toDos != null) {
                        mergeToDos(toDoResponse.toDos)
                        CloudService.getOrders()
                    } else {
                        Observable.error<GetOrderResponse>(APIException(toDoResponse.friendErrorMessage))
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<GetOrderResponse>() {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onNext(getOrderResponse: GetOrderResponse) {
                        Log.d(TAG, "got order")
                        orderToDos(getOrderResponse)
                    }
                })
    }

    private fun mergeToDos(toDos: List<ToDo>) {
        val realm = RealmUtils.mainInstance
        realm.beginTransaction()
        val shouldDeleted = realm.where(ToDo::class.java)
                .notEqualTo(ToDo.DELETED_KEY, java.lang.Boolean.TRUE).findAll()
        for (toDo in shouldDeleted) {
            toDo.deleteFromRealm()
        }
        for (toDo in toDos) {
            realm.copyToRealmOrUpdate(toDo)
        }
        realm.commitTransaction()

        Log.d(TAG, "got to-dos")
    }

    override fun updateOrders(order: String) {
        CloudService.setOrders(order).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Log.d(TAG, "updateOrders") })
    }

    override fun updateIsDone(toDo: ToDo) {
        CloudService.setIsDone(toDo.id!!, toDo.isdone!!).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Log.d(TAG, "updateIsDone") })
    }

    override fun modifyToDo(cate: String, content: String, id: String) {
        val dateStr = dateStr
        val realm = RealmUtils.mainInstance
        realm.beginTransaction()
        val toDo = realm.where(ToDo::class.java).equalTo("id", id).findFirst()
        if (toDo != null) {
            toDo.content = content
            toDo.cate = cate
            toDo.time = dateStr
        }
        realm.commitTransaction()

        CloudService.updateToDo(id, dateStr, content, cate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<CommonResponse>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onNext(commonResponse: CommonResponse) {
                        if (commonResponse.ok) {
                            ToastService.sendShortToast(R.string.modified_hint.getResString()!!)
                        } else {
                            ToastService.sendShortToast(commonResponse.friendErrorMessage)
                            Log.e(TAG, "Modify failed:" + commonResponse.friendErrorMessage)
                        }
                    }
                })
    }

    private val dateStr: String
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = Date()
            return sdf.format(date)
        }

    override fun addToDo(cate: String, content: String) {
        val dateStr = dateStr
        CloudService.addToDo(dateStr, content, ToDo.IS_NOT_DONE, cate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<AddToDoResponse>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        ToastService.sendShortToast(e.message!!)
                    }

                    override fun onNext(addToDoResponse: AddToDoResponse) {
                        val toDo = addToDoResponse.toDo
                        if (toDo != null) {
                            val realm = RealmUtils.mainInstance
                            realm.beginTransaction()

                            val managedToDo = realm.copyToRealm(toDo)

                            if (AppConfig.addToBottom()) {
                                var pos = realm.where(ToDo::class.java).findAll().max(ToDo.POSITION_KEY).toInt()
                                managedToDo.position = ++pos
                            } else {
                                var pos = realm.where(ToDo::class.java).findAll().min(ToDo.POSITION_KEY).toInt()
                                managedToDo.position = --pos
                            }
                            realm.commitTransaction()

                            mView.displayToDos()
                            mView.uploadOrders()
                        }
                    }
                })
    }

    override fun deleteToDo(toDo: ToDo) {
        val id = toDo.id
        val alreadyDeleted = toDo.deleted
        val position = toDo.position

        val realm = RealmUtils.mainInstance
        realm.beginTransaction()
        if (!alreadyDeleted) {
            toDo.deleted = true
        } else {
            toDo.deleteFromRealm()
        }
        realm.commitTransaction()

        mView.notifyToDoDeleted(position)

        if (!alreadyDeleted) {
            CloudService.deleteToDo(id!!).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ Log.d(TAG, "deleteToDo") })
        }
    }

    override fun clearDeletedList() {
        val realm = RealmUtils.mainInstance
        realm.beginTransaction()
        val deletedList = realm.where(ToDo::class.java).equalTo(ToDo.DELETED_KEY, true)
                .findAll()
        for (todo in deletedList) {
            todo.deleteFromRealm()
        }
        realm.commitTransaction()

        mView.displayToDos()
    }

    override fun recoverToDo(toDo: ToDo) {
        addToDo(toDo.cate!!, toDo.content!!)
        val realm = RealmUtils.mainInstance
        realm.beginTransaction()
        toDo.deleteFromRealm()
        realm.commitTransaction()
        mView.displayToDos()
    }

    private fun orderToDos(getOrderResponse: GetOrderResponse) {
        val order = getOrderResponse.order
        if (order != null) {
            val orders = order.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            val realm = RealmUtils.mainInstance
            realm.beginTransaction()

            val localToDos = realm.where(ToDo::class.java).findAll()
            for (i in orders.indices) {
                val toDo = localToDos.where().equalTo(ToDo.ID_KEY, orders[i]).findFirst()
                if (toDo != null) {
                    toDo.position = i
                }
            }

            realm.commitTransaction()
            ToastService.sendShortToast(App.instance!!.getString(R.string.fetch_hint))
            mView.displayToDos()
        } else {
            Log.d(TAG, "Can't get order.")
        }
    }

    private fun parseCategories(resp: String?) {
        val gson = Gson()
        if (resp != null) {
            val information = gson.fromJson(resp, CategoryRespInformation::class.java)
            saveCategoriesToRealm(information!!.cates!!)
        } else {
            val list = gson.fromJson(DEFAULT, CategoryRespInformation::class.java)
            saveCategoriesToRealm(list!!.cates!!)
        }
    }

    private fun saveCategoriesToRealm(list: List<ToDoCategory>) {
        RealmUtils.mainInstance.executeTransaction { realm ->
            for ((i, cate) in list.withIndex()) {
                cate.position = i
                cate.setSid(LocalSettingUtil.getString(App.instance!!, Params.SID_KEY)!!)
                realm.copyToRealmOrUpdate(cate)
            }
        }
    }

    override fun start() {

    }

    override fun stop() {

    }

    companion object {
        private val TAG = "MainPresenter"

        private val DEFAULT = "{ \\\"modified\\\":true, \\\"cates\\\":[{\\\"name\\\":\\\"Work\\\",\\\"color\\\":\\\"#FF436998\\\",\\\"id\\\":1},{\\\"name\\\":\\\"Life\\\",\\\"color\\\":\\\"#FFFFB542\\\",\\\"id\\\":2},{\\\"name\\\":\\\"Family\\\",\\\"color\\\":\\\"#FFFF395F\\\",\\\"id\\\":3},{\\\"name\\\":\\\"Entertainment\\\",\\\"color\\\":\\\"#FF55C1C1\\\",\\\"id\\\":4}]}"
    }
}
