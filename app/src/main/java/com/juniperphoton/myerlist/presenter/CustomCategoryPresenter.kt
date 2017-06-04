package com.juniperphoton.myerlist.presenter

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.juniperphoton.myerlist.api.CloudService
import com.juniperphoton.myerlist.api.response.CommonResponse
import com.juniperphoton.myerlist.event.RefreshToDoEvent
import com.juniperphoton.myerlist.model.ToDoCategory
import com.juniperphoton.myerlist.realm.RealmUtils
import com.juniperphoton.myerlist.util.AppConfig
import com.juniperphoton.myerlist.util.ToastService
import io.realm.Sort
import org.greenrobot.eventbus.EventBus
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

class CustomCategoryPresenter(private val view: CustomCategoryContract.View) : CustomCategoryContract.Presenter {
    companion object {
        private const val DELAY_MILLIS = 500L
    }

    private var list: MutableList<ToDoCategory>? = null

    override fun commit() {
        view.hideKeyboard()
        view.showDialog()

        val realm = RealmUtils.mainInstance
        realm.beginTransaction()
        realm.delete(ToDoCategory::class.java)
        for ((i, category) in list!!.withIndex()) {
            category.position = i
            category.setSid(AppConfig.sid!!)
            realm.copyToRealmOrUpdate(category)
        }
        realm.commitTransaction()
        upload()
    }

    private fun upload() {
        val jsonArray = JsonArray()
        for (category in list!!) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("name", category.name)
            jsonObject.addProperty("color", category.color)
            jsonObject.addProperty("id", category.id)
            jsonArray.add(jsonObject)
        }
        val jsonObject = JsonObject()
        jsonObject.addProperty("modified", true)
        jsonObject.add("cates", jsonArray)
        val str = jsonObject.toString()

        CloudService.updateToDoCategories(str)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<CommonResponse>() {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        view.hideDialog(DELAY_MILLIS)
                        e.printStackTrace()
                    }

                    override fun onNext(commonResponse: CommonResponse) {
                        view.hideDialog(DELAY_MILLIS)
                        if (commonResponse.ok) {
                            ToastService.sendShortToast("Updated")
                            view.postDelay(Runnable {
                                view.finish()
                            }, DELAY_MILLIS)
                            EventBus.getDefault().postSticky(RefreshToDoEvent())
                        } else {
                            ToastService.sendShortToast("Failed to update category")
                        }
                    }
                })
    }

    override fun start() {
        refreshData()
    }

    override fun stop() {

    }

    override fun refreshData() {
        val realm = RealmUtils.mainInstance
        realm.beginTransaction()
        val realmResults = realm.where(ToDoCategory::class.java)
                .findAllSorted(ToDoCategory.KEY_POSITION, Sort.ASCENDING)
        realm.commitTransaction()
        list = ArrayList<ToDoCategory>()

        // Copy them first, every changes will not write into realm db
        for (category in realmResults) {
            list!!.add(category.copy)
        }
        view.initData(list!!)
    }
}
