package com.juniperphoton.myerlist.model

import android.graphics.Color
import com.juniperphoton.myerlist.App
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.extension.getResColor
import com.juniperphoton.myerlist.extension.getResString
import com.juniperphoton.myerlist.extension.toColorString
import com.juniperphoton.myerlist.util.LocalSettingUtil
import com.juniperphoton.myerlist.util.Params
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ToDoCategory : RealmObject() {
    companion object {
        const val KEY_ID = "id"
        const val KEY_POSITION = "position"
        const val KEY_SID = "sid"

        const val VALUE_ALL_ID = 0
        const val VALUE_DELETED_ID = -1
        const val VALUE_PERSONALIZATION_ID = -2

        val allCategory: ToDoCategory
            get() {
                val category = ToDoCategory()
                category.color = R.color.MyerListBlue.getResColor().toColorString()
                category.name = R.string.all.getResString()
                category.id = VALUE_ALL_ID
                category.setSid(LocalSettingUtil.getString(App.instance!!, Params.SID_KEY)!!)
                return category
            }

        val deletedCategory: ToDoCategory
            get() {
                val category = ToDoCategory()
                category.color = R.color.DeleteColor.getResColor().toColorString()
                category.name = R.string.deleted.getResString()
                category.id = VALUE_DELETED_ID
                return category
            }

        val personalizationCategory: ToDoCategory
            get() {
                val category = ToDoCategory()
                category.color = Color.WHITE.toColorString()
                category.name = R.string.personalization.getResString()
                category.id = VALUE_PERSONALIZATION_ID
                return category
            }
    }

    var name: String? = null
    var color: String? = null

    var position: Int = 0

    @PrimaryKey
    var id: Int = 0

    private var sid: String? = null

    val intColor: Int
        get() = Color.parseColor(color)

    fun setSid(sid: String) {
        this.sid = sid
    }

    val copy: ToDoCategory
        get() {
            val category = ToDoCategory()
            category.name = name
            category.id = id
            category.color = color
            category.position = position
            return category
        }
}
