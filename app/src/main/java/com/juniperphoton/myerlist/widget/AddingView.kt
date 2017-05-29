package com.juniperphoton.myerlist.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.juniperphoton.myerlist.App
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.model.ToDoCategory
import com.juniperphoton.myerlist.util.KeyboardUtil

@Suppress("Unused")
class AddingView(private val ctx: Context, attrs: AttributeSet) : FrameLayout(ctx, attrs), View.OnTouchListener {
    private var mode = ADD_MODE

    @JvmField
    @BindView(R.id.adding_view_root)
    internal var root: RelativeLayout? = null

    @JvmField
    @BindView(R.id.adding_view_add_content)
    internal var editText: EditText? = null

    @JvmField
    @BindView(R.id.adding_view_cate)
    internal var cateName: TextView? = null

    @JvmField
    @BindView(R.id.select_category_view)
    internal var selectCategoryView: SelectCategoryView? = null

    @JvmField
    @BindView(R.id.adding_view_title)
    internal var tittleText: TextView? = null

    var onClickOk: ((Int, String, Int) -> Unit)? = null
    var onClickCancel: (() -> Unit)? = null

    private var inputText: String? = null

    init {
        LayoutInflater.from(ctx).inflate(R.layout.view_adding, this, true)
        ButterKnife.bind(this)

        root!!.setOnTouchListener(this)
    }

    fun setVisibleMode(visibility: Int, mode: Int) {
        setVisibility(visibility)
        this.mode = mode
        updateUi()
    }

    fun setContent(text: String) {
        editText!!.setText(text)
    }

    private fun updateUi() {
        when (mode) {
            ADD_MODE -> tittleText!!.text = App.instance!!.getString(R.string.adding_adding)
            MODIFY_MODE -> tittleText!!.text = App.instance!!.getString(R.string.modify_adding)
        }
    }

    @OnClick(R.id.adding_view_ok)
    internal fun onClickOk() {
        prepareToHide()
        onClickOk?.invoke(selectCategoryView!!.selectedIndex, inputText!!, mode)
    }

    @OnClick(R.id.adding_view_cancel)
    internal fun onClickCancel() {
        prepareToHide()
        onClickCancel?.invoke()
    }

    private fun prepareToHide() {
        KeyboardUtil.hide(ctx, editText!!.windowToken)
        inputText = editText!!.editableText.toString()
    }

    fun reset() {
        editText!!.setText("")
    }

    fun setSelected(index: Int) {
        selectCategoryView!!.setSelected(index)
    }

    fun setOnSelectionChangedCallback(callback: ((Int) -> Unit)?) {
        selectCategoryView!!.onSelectionChanged = callback
    }

    fun updateCategory(category: ToDoCategory) {
        cateName!!.text = category.name
        animateBackground(category.intColor)
    }

    fun showInputPane() {
        editText!!.requestFocus()
        postDelayed({
            val inputMethodManager = ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(editText, 0)
        }, 50)
    }

    fun makeCategoriesSelection() {
        selectCategoryView!!.makeViews()
    }

    private fun animateBackground(toColor: Int) {
        val colorDrawable = root!!.background as ColorDrawable
        val from = colorDrawable.color

        val valueAnimator = ValueAnimator.ofArgb(from, toColor)
        valueAnimator.duration = 300
        valueAnimator.addUpdateListener { animation -> root!!.background = ColorDrawable(animation.animatedValue as Int) }
        valueAnimator.start()
    }

    internal var startX: Float = 0.toFloat()
    internal var dx: Float = 0.toFloat()

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> startX = event.x
            MotionEvent.ACTION_MOVE -> dx = event.x - startX
            MotionEvent.ACTION_UP -> {
                if (dx > FLING_THRESHOLD) {
                    selectCategoryView!!.toggleSelection(false)
                } else if (dx < -FLING_THRESHOLD) {
                    selectCategoryView!!.toggleSelection(true)
                }
                startX = 0f
                dx = 0f
            }
        }
        return true
    }

    companion object {

        private val FLING_THRESHOLD = 20f

        val ADD_MODE = 1
        val MODIFY_MODE = 1 shl 1
    }
}
