package com.juniperphoton.myerlist.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.adapter.PickColorAdapter
import com.juniperphoton.myerlist.util.toColor
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
        init()
        colorPickerRoot.setOnClickListener { finish() }
    }

    fun init() {
        colors = generateColors()

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

    fun generateColors(): MutableList<Int> {
        return mutableListOf("#F75B44",
                "#F73215",
                "#F7445B",
                "#E1184B",
                "#C11943",
                "#80224C",
                "#66436F",
                "#713A80",
                "#4D3A80",
                "#F75B44",
                "#352F44",
                "#474E88",
                "#2E3675",
                "#2A2E51",
                "#417C98",
                "#6FD1FF",
                "#3CBBF7",
                "#217CDC",
                "#4CAFFF",
                "#5474C1",
                "#317CA0",
                "#39525F",
                "#4F9595",
                "#2C8D8D",
                "#00BEBE",
                "#2B8A78",
                "#3FBEA6",
                "#3FBE7D",
                "#1C9B5A",
                "#5A9849",
                "#739849",
                "#C9D639",
                "#D6CD00",
                "#F7C142",
                "#F7D842",
                "#F79E42").map {
            it.toColor()
        }.toMutableList()
    }
}
