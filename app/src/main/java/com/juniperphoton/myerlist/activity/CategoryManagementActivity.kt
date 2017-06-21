package com.juniperphoton.myerlist.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.OnClick
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.adapter.CustomCategoryAdapter
import com.juniperphoton.myerlist.extension.*
import com.juniperphoton.myerlist.model.ToDoCategory
import com.juniperphoton.myerlist.presenter.CustomCategoryContract
import com.juniperphoton.myerlist.presenter.CustomCategoryPresenter
import com.juniperphoton.myerlist.util.KeyboardUtil
import kotlinx.android.synthetic.main.activity_manage_category.*

@Suppress("unused", "unused_parameter")
class CategoryManagementActivity : BaseActivity(), CustomCategoryContract.View {
    private var adapter: CustomCategoryAdapter? = null
    private var presenter: CustomCategoryContract.Presenter? = null
    private var progressDialog: ProgressDialog? = null
    private var toDoCategory: ToDoCategory? = null
    private var headerView: View? = null

    val categoryList: RecyclerView by lazy {
        category_manage_list
    }

    val cancelView: View by lazy {
        cancel_view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_category)
        ButterKnife.bind(this)

        initUi()

        presenter = CustomCategoryPresenter(this)
    }

    override fun onBackPressed() {
        if (adapter?.isDirty ?: true) {
            prepareToExit()
        } else {
            super.onBackPressed()
        }
    }

    private fun prepareToExit() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.exit_without_saving)
                .setPositiveButton(R.string.yes) { dialog, _ ->
                    dialog.dismiss()
                    super@CategoryManagementActivity.onBackPressed()
                }
                .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
                .create().show()
    }

    override fun onStart() {
        super.onStart()
        presenter?.start()
    }

    override fun onPause() {
        super.onPause()
        presenter!!.stop()
        progressDialog?.dismiss()
    }

    private fun initUi() {
        createHeader()

        adapter = CustomCategoryAdapter(this)
        adapter?.let {
            it.onClickSelectCategory = {
                toDoCategory = it
                startActivityForResult<PickColorActivity>(0)
            }
            it.headerView = headerView
            categoryList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            categoryList.adapter = adapter

            cancel_view.requestFocus()
        }
    }

    private fun createHeader() {
        headerView = LayoutInflater.from(this).inflate(R.layout.add_cate_header, null, false)
        headerView?.apply {
            layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    R.dimen.manage_category_header_height.getResDimen())
            setOnClickListener {
                val category = ToDoCategory()
                category.name = R.string.default_category_name.getResString()
                category.color = R.color.MyerListBlue.getResColor().toColorString()

                val maxId = adapter?.data?.maxBy { it.id }?.id ?: 0

                category.id = maxId + 1
                adapter?.addData(category)
                adapter?.isDirty = true
            }
        }
    }

    override fun initData(data: MutableList<ToDoCategory>) {
        if (adapter == null) {
            return
        }
        adapter?.refreshData(data)
    }

    override fun showDialog() {
        progressDialog = ProgressDialog(this, ProgressDialog.STYLE_SPINNER)
        progressDialog?.apply {
            setTitle(R.string.loading_hint.getResString())
            setMessage(R.string.waiting.getResString())
            show()
        }
    }

    override fun hideDialog(delayMillis: Long) {
        progressDialog?.let {
            cancelView.postDelayed({ it.hide() }, delayMillis)
        }
    }

    override fun hideKeyboard() {
        KeyboardUtil.hide(this, cancelView.windowToken)
    }

    @OnClick(R.id.cancel_view)
    fun onClickCancel() {
        onBackPressed()
    }

    @OnClick(R.id.commit_view)
    fun onClickOk() {
        presenter!!.commit()
    }

    override fun postDelay(runnable: Runnable, delayMillis: Long) {
        cancelView.postDelayed(runnable, delayMillis)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) {
            return
        }
        val color = data.getIntExtra(PickColorActivity.RESULT_KEY, Color.BLACK)
        toDoCategory?.let {
            it.color = color.toColorString()
            adapter!!.notifyDataSetChanged()
        }
    }
}
