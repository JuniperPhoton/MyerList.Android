package com.juniperphoton.myerlist.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.extension.getActivity

class CommonTitleBar(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    @JvmField
    @BindView(R.id.back_iv)
    var backView: View? = null

    @JvmField
    @BindView(R.id.title_tv)
    var textView: TextView? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.common_title_bar, this, true)

        ButterKnife.bind(this)

        val array = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleBar)
        val title = array.getString(R.styleable.CommonTitleBar_title)
        textView!!.text = title
        array.recycle()

        backView!!.setOnClickListener { view ->
            val activity = view.getActivity()
            activity?.finish()
        }
    }
}
