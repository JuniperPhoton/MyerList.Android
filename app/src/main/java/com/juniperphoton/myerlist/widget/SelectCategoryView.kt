package com.juniperphoton.myerlist.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.extension.dpToPixel
import com.juniperphoton.myerlist.extension.toResColor
import com.juniperphoton.myerlist.model.ToDoCategory
import com.juniperphoton.myerlist.realm.RealmUtils
import io.realm.Realm

class SelectCategoryView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    companion object {
        private val TAG = "SelectCategoryView"
        val CATE_VIEW_SIZE_DP = 24
        val CATE_VIEW_MARGIN_lEFT = 8
    }

    var onSelectionChanged: ((Int) -> Unit)? = null

    var selectedIndex = 0
        set(value) {
            field = value
            updateUi(getChildAt(value) as CateCircleView)
            onSelectionChanged?.invoke(value)
        }

    fun toggleSelection(plus: Boolean) {
        Log.d(TAG, "toggleSelection:" + plus)
        var target = selectedIndex + if (plus) 1 else -1
        if (target >= childCount) target = 0
        if (target < 0) target = childCount - 1
        selectedIndex = target
        onSelectionChanged?.invoke(selectedIndex)
    }

    fun makeViews() {
        removeAllViews()
        val categories = Realm.getDefaultInstance().where(ToDoCategory::class.java).findAll()
        categories.filterIndexed handle@ { index, _ ->
            return@handle index >= 0
        }.forEach {
            val circleView = CateCircleView(context, null)
            val layoutParams = LinearLayout.LayoutParams(context.dpToPixel(CATE_VIEW_SIZE_DP), context.dpToPixel(CATE_VIEW_SIZE_DP))
            layoutParams.setMargins(context.dpToPixel(CATE_VIEW_MARGIN_lEFT), 0, 0, 0)
            circleView.layoutParams = layoutParams
            circleView.color = it.intColor
            circleView.setOnClickListener {
                selectedIndex = indexOfChild(it)
            }
            addView(circleView)
        }

        val circleView = CateCircleView(context, null)
        val layoutParams = LinearLayout.LayoutParams(context.dpToPixel(CATE_VIEW_SIZE_DP), context.dpToPixel(CATE_VIEW_SIZE_DP))
        circleView.layoutParams = layoutParams
        circleView.color = R.color.MyerListBlue.toResColor()
        circleView.setOnClickListener {
            selectedIndex = indexOfChild(it)
        }
        addView(circleView, 0)
        selectedIndex = 0
    }

    private fun updateUi(circleView: CateCircleView) {
        (0..childCount - 1)
                .map { getChildAt(it) as CateCircleView }
                .forEach {
                    it.inSelected = it === circleView
                }
    }
}
