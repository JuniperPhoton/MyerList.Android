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
import com.juniperphoton.myerlist.event.ReCreateEvent
import com.juniperphoton.myerlist.event.RefreshToDoEvent
import com.juniperphoton.myerlist.extension.getResString
import com.juniperphoton.myerlist.model.CategoryRespInformation
import com.juniperphoton.myerlist.model.ToDo
import com.juniperphoton.myerlist.model.ToDoCategory
import com.juniperphoton.myerlist.util.AppConfig
import com.juniperphoton.myerlist.util.LocalSettingUtil
import com.juniperphoton.myerlist.util.Params
import com.juniperphoton.myerlist.util.ToastService
import io.realm.Realm
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class MainPresenter(private val view: MainContract.View) : MainContract.Presenter {
    companion object {
        private val TAG = "MainPresenter"

        private val DEFAULT = "{ \\\"modified\\\":true, \\\"cates\\\":[{\\\"name\\\":\\\"Work\\\",\\\"color\\\":\\\"#FF436998\\\",\\\"id\\\":1},{\\\"name\\\":\\\"Life\\\",\\\"color\\\":\\\"#FFFFB542\\\",\\\"id\\\":2},{\\\"name\\\":\\\"Family\\\",\\\"color\\\":\\\"#FFFF395F\\\",\\\"id\\\":3},{\\\"name\\\":\\\"Entertainment\\\",\\\"color\\\":\\\"#FF55C1C1\\\",\\\"id\\\":4}]}"

        const val FILTER_ALL = 0
        const val FILTER_UNDONE = 1
        const val FILTER_DONE = 2
    }

    override var filterOption: Int = LocalSettingUtil.getInt(App.instance!!, Params.KEY_FILTER_OPTION, FILTER_ALL)
        set(value) {
            if (field != value) {
                field = value
                view.refreshToDoList(value)
                view.updateFilterIcon(value)

                LocalSettingUtil.putInt(App.instance!!, Params.KEY_FILTER_OPTION, value)
            }
        }

    override fun onCategorySelected(item: Int) {
        view.refreshToDoList(filterOption)
    }

    override fun getCategories() {
        view.toggleRefreshing(true)
        CloudService.getCategories()
                .subscribeOn(Schedulers.io())
                .map { cateResponse ->
                    parseCategories(cateResponse?.rawString)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Unit>() {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        view.toggleRefreshing(false)
                        view.refreshToDoList(filterOption)
                    }

                    override fun onNext(cateResponse: Unit) {
                        view.refreshCategoryList()
                        getToDos()
                    }
                })
    }

    override fun getToDos() {
        view.toggleRefreshing(true)
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
                        view.toggleRefreshing(false)
                        view.refreshToDoList(filterOption)
                    }

                    override fun onError(e: Throwable) {
                        view.toggleRefreshing(false)
                        view.refreshToDoList(filterOption)
                    }

                    override fun onNext(getOrderResponse: GetOrderResponse) {
                        Log.d(TAG, "got order")
                        orderToDos(getOrderResponse)
                    }
                })
    }

    private fun mergeToDos(toDos: List<ToDo>) {
        Realm.getDefaultInstance().executeTransaction { realm ->
            val localWithoutDeleteFlag = realm.where(ToDo::class.java)
                    .notEqualTo(ToDo.KEY_DELETED, java.lang.Boolean.TRUE).findAll()
            localWithoutDeleteFlag.forEach {
                it.deleteFromRealm()
            }
            toDos.forEach {
                realm.copyToRealmOrUpdate(it)
            }
        }
        Log.d(TAG, "mergeToDos")
    }

    override fun updateOrders(order: String) {
        CloudService.setOrders(order).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<CommonResponse>() {
                    override fun onError(e: Throwable?) {
                        e?.printStackTrace()
                    }

                    override fun onCompleted() {
                    }

                    override fun onNext(t: CommonResponse?) {
                        Log.d(TAG, "updateOrders")
                    }
                })
    }

    override fun updateIsDone(id: String, oldValue: String) {
        val newValue = if (oldValue == ToDo.VALUE_DONE) ToDo.VALUE_UNDONE else ToDo.VALUE_DONE

        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val managedToDo = realm.where(ToDo::class.java).equalTo(ToDo.KEY_ID, id).findFirst()
        managedToDo!!.isdone = newValue
        realm.commitTransaction()

        view.notifyDataSetChanged()

        CloudService.setIsDone(id, newValue).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<CommonResponse>() {
                    override fun onError(e: Throwable?) {
                        e?.printStackTrace()
                    }

                    override fun onCompleted() {
                    }

                    override fun onNext(t: CommonResponse?) {
                    }
                })
    }

    override fun modifyToDo(id: String, cate: String, content: String) {
        val dateStr = dateStr
        Realm.getDefaultInstance().executeTransaction {
            val toDo = it.where(ToDo::class.java).equalTo(ToDo.KEY_ID, id).findFirst()
            toDo?.let {
                it.content = content
                it.cate = cate
                it.time = dateStr
            }
        }

        view.notifyDataSetChanged()
        view.toggleRefreshing(true)

        CloudService.updateToDo(id, dateStr, content, cate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<CommonResponse>() {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        view.toggleRefreshing(false)
                        e.printStackTrace()
                    }

                    override fun onNext(commonResponse: CommonResponse) {
                        view.toggleRefreshing(false)
                        if (commonResponse.ok) {
                            ToastService.sendShortToast(R.string.modified_hint.getResString())
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
        view.toggleRefreshing(true)
        CloudService.addToDo(dateStr, content, ToDo.VALUE_UNDONE, cate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<AddToDoResponse>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        ToastService.sendShortToast(e.message!!)
                        view.toggleRefreshing(false)
                    }

                    override fun onNext(addToDoResponse: AddToDoResponse) {
                        view.toggleRefreshing(false)
                        val toDo = addToDoResponse.toDo
                        if (toDo != null) {
                            Realm.getDefaultInstance().executeTransaction {
                                val managedToDo = it.copyToRealm(toDo)
                                if (AppConfig.addToBottom()) {
                                    var pos = it.where(ToDo::class.java).findAll().max(ToDo.KEY_POSITION).toInt()
                                    managedToDo.position = ++pos
                                } else {
                                    var pos = it.where(ToDo::class.java).findAll().min(ToDo.KEY_POSITION).toInt()
                                    managedToDo.position = --pos
                                }
                            }
                            view.refreshToDoList(filterOption)
                            view.uploadOrders()
                        }
                    }
                })
    }

    override fun deleteToDo(item: ToDo) {
        val id = item.id
        val alreadyDeleted = item.deleted

        Realm.getDefaultInstance().executeTransaction { realm ->
            val todo = realm.where(ToDo::class.java).equalTo(ToDo.KEY_ID, id).findFirst()
            todo?.let {
                if (!alreadyDeleted) {
                    it.deleted = true
                } else {
                    it.deleteFromRealm()
                }
            }
        }

        view.notifyDataSetChanged()

        if (!alreadyDeleted) {
            CloudService.deleteToDo(id!!).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<CommonResponse>() {
                        override fun onError(e: Throwable?) {
                            e?.printStackTrace()
                        }

                        override fun onCompleted() {
                        }

                        override fun onNext(t: CommonResponse?) {
                            Log.d(TAG, "deleteToDo")
                        }
                    })
        }
    }

    override fun clearDeletedList() {
        Realm.getDefaultInstance().executeTransaction {
            val deletedList = it.where(ToDo::class.java).equalTo(ToDo.KEY_DELETED, true)
                    .findAll()
            for (todo in deletedList) {
                todo.deleteFromRealm()
            }
        }

        view.refreshToDoList(filterOption)
    }

    override fun recoverToDo(toDo: ToDo) {
        addToDo(toDo.cate!!, toDo.content!!)
        Realm.getDefaultInstance().executeTransaction {
            toDo.deleteFromRealm()
        }
        view.refreshToDoList(filterOption)
    }

    private fun orderToDos(getOrderResponse: GetOrderResponse) {
        val order = getOrderResponse.order
        if (order != null) {
            Log.d(TAG, "todo order$order")

            val orders = order.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            Realm.getDefaultInstance().executeTransaction {
                val localToDos = it.where(ToDo::class.java).findAll()
                orders.indices.forEach {
                    val toDo = localToDos.where().equalTo(ToDo.KEY_ID, orders[it]).findFirst()
                    if (toDo != null) {
                        toDo.position = it
                    }
                }
            }

            ToastService.sendShortToast(App.instance!!.getString(R.string.fetch_hint))
        } else {
            Log.d(TAG, "Can't get order.")
        }
    }

    private fun parseCategories(resp: String?) {
        Log.d(TAG, "cate resp$resp")
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
        Realm.getDefaultInstance().executeTransaction { realm ->
            for ((i, cate) in list.withIndex()) {
                cate.position = i
                cate.setSid(LocalSettingUtil.getString(App.instance!!, Params.SID_KEY)!!)
                realm.copyToRealmOrUpdate(cate)
            }
        }
    }

    @Suppress("unused_parameter", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun receiveEvent(event: ReCreateEvent) {
        view.reCreateView()
        EventBus.getDefault().removeAllStickyEvents()
    }

    @Suppress("unused_parameter", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun receiveRefreshToDoEvent(event: RefreshToDoEvent) {
        view.refreshToDoList(filterOption)
        EventBus.getDefault().removeAllStickyEvents()
    }

    override fun start() {
        EventBus.getDefault().register(this)
        view.updateFilterIcon(filterOption)
    }

    override fun stop() {
        EventBus.getDefault().unregister(this)
    }
}
