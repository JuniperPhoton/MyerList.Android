package com.juniperphoton.myerlist.presenter

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.juniperphoton.myerlist.api.CloudService
import com.juniperphoton.myerlist.api.response.CommonResponse
import com.juniperphoton.myerlist.model.ToDoCategory
import com.juniperphoton.myerlist.realm.RealmUtils
import com.juniperphoton.myerlist.util.ToastService

import java.util.ArrayList

import io.realm.Sort
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class CustomCategoryPresenter(private val view: CustomCategoryContract.View) : CustomCategoryContract.Presenter {
    private var list: MutableList<ToDoCategory>? = null

    override fun commit() {
        view.hideKeyboard()

        val realm = RealmUtils.mainInstance
        realm.beginTransaction()
        realm.delete(ToDoCategory::class.java)
        for ((i, category) in list!!.withIndex()) {
            category.position = i
            realm.copyToRealmOrUpdate(category)
        }
        realm.commitTransaction()
        upload()
    }

    private fun upload() {
        val jsonArray = JsonArray()
        for (category in list!!) {
            val `object` = JsonObject()
            `object`.addProperty("name", category.name)
            `object`.addProperty("color", category.color)
            `object`.addProperty("id", category.id)
            jsonArray.add(`object`)
        }
        val jsonObject = JsonObject()
        jsonObject.addProperty("modified", true)
        jsonObject.add("cates", jsonArray)
        val str = jsonObject.toString()

        CloudService.updateToDoCategories(str)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe({ view.showDialog() })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<CommonResponse>() {
                    override fun onCompleted() {
                        view.hideDialog(500)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onNext(commonResponse: CommonResponse) {
                        if (commonResponse.ok) {
                            ToastService.sendShortToast("Updated")
                            view.finish()
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
                .findAllSorted(ToDoCategory.POSITION_KEY, Sort.ASCENDING)
        realm.commitTransaction()
        list = ArrayList<ToDoCategory>()

        // Copy them first, every changes will not write into realm db
        for (category in realmResults) {
            list!!.add(category.copy)
        }
        view.initData(list!!)
    }
}
