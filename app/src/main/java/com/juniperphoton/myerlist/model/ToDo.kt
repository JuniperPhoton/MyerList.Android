package com.juniperphoton.myerlist.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ToDo : RealmObject(), Cloneable {
    companion object {
        val ID_KEY = "id"
        val SID_KEY = "sid"
        val ISDONE_KEY = ""
        val CATE_KEY = "cate"
        val DELETED_KEY = "deleted"
        val POSITION_KEY = "position"
        val IS_DONE = "1"
        val IS_NOT_DONE = "0"
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