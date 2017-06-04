package com.juniperphoton.myerlist.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ToDo : RealmObject(), Cloneable {
    companion object {
        const val KEY_ID = "id"
        const val KEY_SID = "sid"
        const val KEY_IS_DONE = "isdone"
        const val KEY_CATEGORY = "cate"
        const val KEY_DELETED = "deleted"
        const val KEY_POSITION = "position"
        const val VALUE_DONE = "1"
        const val VALUE_UNDONE = "0"
    }

    @PrimaryKey
    var id: String? = null
    var sid: String? = null
    var time: String? = null
    var content: String? = null
    var isdone: String? = null
    var cate: String? = null
    var deleted: Boolean = false
    var position: Int = 0

    public override fun clone(): Any {
        val newToDo = ToDo()
        newToDo.deleted = deleted
        newToDo.time = time
        newToDo.isdone = isdone
        newToDo.cate = cate
        newToDo.content = content
        newToDo.id = id
        newToDo.sid = sid
        return newToDo
    }
}