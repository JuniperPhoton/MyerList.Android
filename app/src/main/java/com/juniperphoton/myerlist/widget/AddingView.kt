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
import com.juniperphoton.myerlist.util.LocalSettingUtil
import com.juniperphoton.myerlist.util.Params.SWITCH_CATEGORY_HINT
import com.juniperphoton.myerlist.util.ToastService

@Suppress("unused", "unused_parameter")
class AddingView(private val ctx: Context, attrs: AttributeSet) : FrameLayout(ctx, attrs), View.OnTouchListener {
    companion object {
        private val FLING_THRESHOLD = 20f
        val ADD_MODE = 1
        val MODIFY_MODE = 1 shl 1
    }

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

    @JvmField
    @BindView(R.id.switchCategoryHintView)
    internal var hintView: View? = null

    var onClickOk: ((Int, String, Int) -> Unit)? = null
    var onClickCancel: (() -> Unit)? = null

    private val inputText: String?
        get() = editText?.text?.toString()

    var content: String
        get() = editText?.text?.toString() ?: ""
        set(value) {
            editText?.setText(value)
        }

    var mode = ADD_MODE
        set(value) {
            field = value
            when (value) {
                ADD_MODE -> tittleText!!.text = App.instance!!.getString(R.string.adding_adding)
                MODIFY_MODE -> tittleText!!.text = App.instance!!.getString(R.string.modify_adding)
            }
        }

    var selectedIndex: Int = 0
        set(value) {
            field = value
            selectCategoryView!!.selectedIndex = value
        }

    init {
        LayoutInflater.from(ctx).inflate(R.layout.view_adding, this, true)
        ButterKnife.bind(this)

        root!!.setOnTouchListener(this)

        if (LocalSettingUtil.checkKey(context, SWITCH_CATEGORY_HINT)) {
            hintView?.visibility = View.GONE
        }
    }

    @OnClick(R.id.adding_view_ok)
    internal fun onClickOk() {
        if (!checkInput()) {
            return
        }
        prepareToHide()
        onClickOk?.invoke(selectCategoryView!!.selectedIndex, inputText!!, mode)
    }

    @OnClick(R.id.adding_view_cancel)
    internal fun onClickCancel() {
        prepareToHide()
        onClickCancel?.invoke()
    }

    @OnClick(R.id.switchCategoryHintView)
    internal fun onClickHint() {
        LocalSettingUtil.putBoolean(context, SWITCH_CATEGORY_HINT, true)
        hintView?.visibility = View.GONE
    }

    private fun checkInput(): Boolean {
        if (!editText?.text?.toString().isNullOrBlank()) {
            return true
        }
        ToastService.sendShortToast(R.string.empty_input_hint)
        return false
    }

    private fun prepareToHide() {
        KeyboardUtil.hide(ctx, editText!!.windowToken)
    }

    fun reset() {
        editText!!.setText("")
    }

    fun setOnSelectionChangedCallback(callback: ((Int) -> Unit)?) {
        selectCategoryView!!.onSelectionChanged = callback
    }

    fun updateCategory(category: ToDoCategory) {
        cateName!!.text = category.name
        animateBackground(category.intColor)
    }

    fun showInputPane() {
        editText?.let {
            it.requestFocus()
            it.setSelection(it.length(), it.length())
        }
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

    private var startX: Float = 0F
    private var dx: Float = 0F

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
}
