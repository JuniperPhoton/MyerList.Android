package com.juniperphoton.myerlist.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import butterknife.ButterKnife
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.adapter.PickColorAdapter
import com.juniperphoton.myerlist.util.ColorUtil
import kotlinx.android.synthetic.main.activity_color_picker.*

@Suppress("unused","unused_parameter")
class PickColorActivity : BaseActivity() {
    companion object {
        private val SPAN_COUNT = 6
        val RESULT_KEY = "picked_color"
    }

    private var colors: MutableList<Int>? = null
    private var adapter: PickColorAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_picker)
        ButterKnife.bind(this)
        init()
        colorPickerRoot.setOnClickListener { finish() }
    }

    fun init() {
        colors = ColorUtil.generateColors()

        adapter = PickColorAdapter(this)
        adapter!!.onSelectColor = { color ->
            val intent = Intent()
            intent.putExtra(RESULT_KEY, color)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        colorPickList.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        colorPickList.adapter = adapter
        adapter!!.refreshData(colors!!)
    }
}
