package com.juniperphoton.myerlist.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.adapter.PickColorAdapter
import com.juniperphoton.myerlist.util.ColorUtil

import butterknife.BindView
import butterknife.ButterKnife

class PickColorActivity : BaseActivity() {
    companion object {
        private val SPAN_COUNT = 6
        val RESULT_KEY = "picked_color"
    }

    @JvmField
    @BindView(R.id.color_picker_list)
    var recyclerView: RecyclerView? = null

    @JvmField
    @BindView(R.id.color_picker_root)
    var root: View? = null

    private var colors: MutableList<Int>? = null
    private var adapter: PickColorAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_picker)
        ButterKnife.bind(this)
        init()
        root!!.setOnClickListener { finish() }
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
        recyclerView!!.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        recyclerView!!.adapter = adapter
        adapter!!.refreshData(colors!!)
    }
}
