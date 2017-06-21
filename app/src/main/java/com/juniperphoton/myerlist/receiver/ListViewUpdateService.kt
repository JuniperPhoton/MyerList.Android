package com.juniperphoton.myerlist.receiver

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.extension.toColor
import com.juniperphoton.myerlist.extension.toResColor
import com.juniperphoton.myerlist.model.ToDo
import com.juniperphoton.myerlist.model.ToDoCategory
import com.juniperphoton.myerlist.util.LocalSettingUtil
import com.juniperphoton.myerlist.util.Params
import io.realm.Realm

class ListViewUpdateService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return ListViewUpdateFactory(this)
    }
}

class ListViewUpdateFactory(private var context: Context) : RemoteViewsService.RemoteViewsFactory {
    companion object {
        private const val TAG = "ListViewUpdateFactory"
    }

    private var list: List<ToDo>? = null
    private var cateList: List<ToDoCategory>? = null

    override fun onCreate() {
    }

    private fun updateDataSet() {
        val realm = Realm.getDefaultInstance()
        val cateList = realm.where(ToDoCategory::class.java)
                .greaterThanOrEqualTo(ToDoCategory.KEY_ID, 0)
                .findAll()
        var query = realm.where(ToDo::class.java).notEqualTo(ToDo.KEY_DELETED, true)

        val filter = LocalSettingUtil.getInt(context, Params.KEY_FILTER_OPTION, 0)
        val todoList = when (filter) {
            1 -> query.equalTo(ToDo.KEY_IS_DONE, ToDo.VALUE_UNDONE).findAllSorted(ToDo.KEY_POSITION)
            2 -> query.equalTo(ToDo.KEY_IS_DONE, ToDo.VALUE_DONE).findAllSorted(ToDo.KEY_POSITION)
            else -> query.findAllSorted(ToDo.KEY_POSITION)
        }

        val unmanagedList = ArrayList<ToDo>()
        todoList.forEach {
            unmanagedList.add(realm.copyFromRealm(it))
        }

        val unmanagedCateList = ArrayList<ToDoCategory>()
        cateList.forEach {
            unmanagedCateList.add(realm.copyFromRealm(it))
        }

        this.list = unmanagedList
        this.cateList = unmanagedCateList

        realm.close()
    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(context.packageName, R.layout.widget_loading_layout)
    }

    override fun getItemId(position: Int): Long {
        return if (list?.size ?: 0 <= 0) 0 else list!![position].hashCode().toLong()
    }

    override fun onDataSetChanged() {
        Log.d(TAG, "onDataSetChanged")
        updateDataSet()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_row_todo)
        remoteViews.setTextViewText(R.id.widget_row_to_do_content, list!![position].content)
        remoteViews.setViewVisibility(R.id.widget_done_line, if (list!![position].isdone == ToDo.VALUE_DONE) View.VISIBLE else View.GONE)

        val cate = cateList!!.find {
            it.id == list!![position].cate!!.toInt()
        }
        val cateColor = cate?.color?.toColor() ?: R.color.MyerListBlue.toResColor()
        val backgroundColor = if (position % 2 == 0) Color.WHITE else R.color.PlaceholderGray.toResColor()
        remoteViews.setInt(R.id.widget_cate_view, "setBackgroundColor", cateColor)
        remoteViews.setInt(R.id.widget_row_root, "setBackgroundColor", backgroundColor)
        remoteViews.setOnClickFillInIntent(R.id.widget_row_root, Intent())
        return remoteViews
    }

    override fun getCount(): Int {
        return list!!.size
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {
    }
}