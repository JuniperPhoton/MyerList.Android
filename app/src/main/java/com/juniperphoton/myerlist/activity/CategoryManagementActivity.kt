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
import com.juniperphoton.myerlist.model.ToDoCategory
import com.juniperphoton.myerlist.presenter.CustomCategoryContract
import com.juniperphoton.myerlist.presenter.CustomCategoryPresenter
import com.juniperphoton.myerlist.util.KeyboardUtil
import com.juniperphoton.myerlist.util.getDimenInPixel
import com.juniperphoton.myerlist.util.toColorString
import kotlinx.android.synthetic.main.activity_manage_category.*

class CategoryManagementActivity : BaseActivity(), CustomCategoryContract.View {
    private var adapter: CustomCategoryAdapter? = null
    private var presenter: CustomCategoryContract.Presenter? = null
    private var progressDialog: ProgressDialog? = null
    private var toDoCategory: ToDoCategory? = null
    private var headerView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_category)
        ButterKnife.bind(this)

        initUi()

        presenter = CustomCategoryPresenter(this)
    }

    override fun onBackPressed() {
        prepareToExit()
    }

    private fun prepareToExit() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.exit_without_saving)
                .setPositiveButton(R.string.ok_adding) { dialog, _ ->
                    dialog.dismiss()
                    super@CategoryManagementActivity.onBackPressed()
                }
                .setNegativeButton(R.string.cancel_adding) { dialog, _ -> dialog.dismiss() }
                .create().show()
    }

    override fun onStart() {
        super.onStart()
        presenter!!.start()
    }

    override fun onPause() {
        super.onPause()
        presenter!!.stop()
    }

    private fun initUi() {
        createHeader()

        adapter = CustomCategoryAdapter(this)
        adapter!!.onClickSelectCategory = {
            toDoCategory = it
            val intent = Intent(this, PickColorActivity::class.java)
            startActivityForResult(intent, 0)
        }
        adapter!!.headerView = headerView
        categoryManageList!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        categoryManageList!!.adapter = adapter

        categoryManageCancelView!!.requestFocus()
    }

    private fun createHeader() {
        headerView = LayoutInflater.from(this).inflate(R.layout.add_cate_header, null, false)
        headerView!!.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                this.getDimenInPixel(52))
        headerView!!.setOnClickListener {
            val category = ToDoCategory()
            category.name = "New cate"
            category.color = "#FF4096C9"
            adapter!!.addData(category)
        }
    }

    override fun initData(data: MutableList<ToDoCategory>) {
        if (adapter == null || data == null) {
            return
        }
        adapter!!.refreshData(data)
    }

    override fun showDialog() {
        progressDialog = ProgressDialog(this, ProgressDialog.STYLE_SPINNER)
        progressDialog!!.setTitle(getString(R.string.loading_hint))
        progressDialog!!.show()
    }

    override fun hideDialog(delay: Int) {
        if (progressDialog != null) {
            categoryManageCancelView!!.postDelayed({ progressDialog!!.hide() }, delay.toLong())
        }
    }

    override fun hideKeyboard() {
        KeyboardUtil.hide(this, categoryManageCancelView!!.windowToken)
    }

    @OnClick(R.id.categoryManageCancelView)
    fun onClickCancel() {
        prepareToExit()
    }

    @OnClick(R.id.activity_cate_per_commit_view)
    fun onClickOk() {
        presenter!!.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) {
            return
        }
        val color = data.getIntExtra(PickColorActivity.RESULT_KEY, Color.BLACK)
        if (toDoCategory != null) {
            toDoCategory!!.color = color.toColorString()
            adapter!!.notifyDataSetChanged()
        }
    }
}
