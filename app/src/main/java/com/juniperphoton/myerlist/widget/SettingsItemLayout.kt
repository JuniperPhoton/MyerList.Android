package com.juniperphoton.myerlist.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.util.LocalSettingUtil

class SettingsItemLayout(private val ctx: Context, attrs: AttributeSet) : FrameLayout(ctx, attrs) {
    @BindView(R.id.settings_item_title)
    lateinit var titleTextView: TextView

    @BindView(R.id.settings_item_content)
    lateinit var contentTextView: TextView

    @BindView(R.id.settings_item_switch)
    lateinit var compoundButton: CompoundButton

    @BindView(R.id.divider_view)
    lateinit var dividerView: View

    @BindView(R.id.settings_item_root)
    lateinit var root: View

    var onClick: (() -> Unit)? = null

    private var defaultValue: Boolean
    private var key: String? = null

    var checked: Boolean
        get() = compoundButton.isChecked
        set(checked) {
            compoundButton.isChecked = checked
        }

    var title: String
        get() = titleTextView.text.toString()
        set(value) {
            titleTextView.text = value
        }

    var content: String
        get() = contentTextView.text.toString()
        set(value) {
            contentTextView.text = value
        }

    init {
        LayoutInflater.from(ctx).inflate(R.layout.row_settings_item, this, true)

        ButterKnife.bind(this)

        val array = ctx.obtainStyledAttributes(attrs, R.styleable.SettingsItemLayout)
        val title = array.getString(R.styleable.SettingsItemLayout_setting_title)
        val content = array.getString(R.styleable.SettingsItemLayout_setting_content)
        val hasCheckbox = array.getBoolean(R.styleable.SettingsItemLayout_has_checkbox, false)
        val showDivider = array.getBoolean(R.styleable.SettingsItemLayout_show_divider, true)
        defaultValue = array.getBoolean(R.styleable.SettingsItemLayout_default_checkbox_value, false)
        key = array.getString(R.styleable.SettingsItemLayout_key)
        array.recycle()

        if (title != null) {
            titleTextView.text = title
        }

        if (content != null) {
            contentTextView.text = content
        }

        if (!hasCheckbox) {
            compoundButton.visibility = View.GONE
        }

        if (!showDivider) {
            dividerView.visibility = View.GONE
        }

        root.setOnClickListener {
            compoundButton.isChecked = !compoundButton.isChecked
            onClick?.invoke()
        }

        if (hasCheckbox) {
            initViews()
        }
    }

    private fun initViews() {
        if (LocalSettingUtil.checkKey(ctx, key!!)) {
            val addToBottom = LocalSettingUtil.getBoolean(ctx, key!!, defaultValue)
            compoundButton.isChecked = addToBottom
        } else {
            compoundButton.isChecked = defaultValue
        }
        compoundButton.setOnCheckedChangeListener { _, isChecked -> LocalSettingUtil.putBoolean(ctx, key!!, isChecked) }
    }
}