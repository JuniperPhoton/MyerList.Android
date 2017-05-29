package com.juniperphoton.myerlist.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout

import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.model.ToDoCategory
import com.juniperphoton.myerlist.realm.RealmUtils
import com.juniperphoton.myerlist.util.getDimenInPixel

class SelectCategoryView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    companion object {
        private val TAG = "SelectCategoryView"
    }

    var onSelectionChanged: ((Int) -> Unit)? = null

    var selectedIndex = 0
        private set

    fun setSelected(index: Int) {
        selectedIndex = index
        updateUi(getChildAt(selectedIndex) as CateCircleView)
        onSelectionChanged?.invoke(selectedIndex)
    }

    fun toggleSelection(plus: Boolean) {
        Log.d(TAG, "toggleSelection:" + plus)
        var target = selectedIndex + if (plus) 1 else -1
        if (target >= childCount) target = 0
        if (target < 0) target = childCount - 1
        selectedIndex = target
        updateUi(getChildAt(selectedIndex) as CateCircleView)
        onSelectionChanged?.invoke(selectedIndex)
    }

    fun makeViews() {
        removeAllViews()
        val realm = RealmUtils.mainInstance
        realm.beginTransaction()
        val categories = realm.where(ToDoCategory::class.java).findAll()
        for (toDoCategory in categories) {
            if (toDoCategory.id >= 0) {
                val circleView = CateCircleView(context, null)
                val layoutParams = LinearLayout.LayoutParams(context.getDimenInPixel(24), context.getDimenInPixel(24))
                layoutParams.setMargins(context.getDimenInPixel(8), 0, 0, 0)
                circleView.layoutParams = layoutParams
                circleView.setColor(toDoCategory.intColor)
                circleView.setOnClickListener{
                    updateUi(it as CateCircleView)
                    onSelectionChanged?.invoke(selectedIndex)
                }
                addView(circleView)
            }
        }
        val circleView = CateCircleView(context, null)
        val layoutParams = LinearLayout.LayoutParams(context.getDimenInPixel(24), context.getDimenInPixel(24))
        circleView.layoutParams = layoutParams
        circleView.setColor(ContextCompat.getColor(context, R.color.MyerListBlue))
        circleView.setOnClickListener{
            updateUi(it as CateCircleView)
            onSelectionChanged?.invoke(selectedIndex)
        }
        addView(circleView, 0)
        getChildAt(0).isSelected = true
        realm.commitTransaction()
    }

    private fun updateUi(circleView: CateCircleView) {
        for (i in 0..childCount - 1) {
            val cateCircleView = getChildAt(i) as CateCircleView
            if (cateCircleView === circleView) {
                cateCircleView.isSelected = true
                selectedIndex = i
            } else {
                cateCircleView.isSelected = false
            }
        }
    }
}
