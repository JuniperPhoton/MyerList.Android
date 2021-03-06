package com.juniperphoton.myerlist.activity

import android.animation.Animator
import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.ImageView
import android.widget.TextView
import butterknife.ButterKnife
import butterknife.OnClick
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.adapter.CategoryAdapter
import com.juniperphoton.myerlist.adapter.ToDoAdapter
import com.juniperphoton.myerlist.extension.getResString
import com.juniperphoton.myerlist.extension.startActivity
import com.juniperphoton.myerlist.model.ToDo
import com.juniperphoton.myerlist.model.ToDoCategory
import com.juniperphoton.myerlist.presenter.MainContract
import com.juniperphoton.myerlist.presenter.MainPresenter
import com.juniperphoton.myerlist.util.*
import com.juniperphoton.myerlist.widget.AddingView
import io.realm.Realm
import io.realm.RealmList
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_drawer.*
import kotlinx.android.synthetic.main.no_item_layout.*

@Suppress("unused", "unused_parameter")
class MainActivity : BaseActivity(), MainContract.View {
    companion object {
        private const val TAG = "MainActivity"
    }

    private var cateAdapter: CategoryAdapter? = null
    private var toDoAdapter: ToDoAdapter? = null

    private var drawerHeaderView: View? = null
    private var drawerEmailView: TextView? = null
    private var undoneText: TextView? = null

    private var presenter: MainContract.Presenter? = null

    private var selectedCategoryId = 0
    private var selectedCategoryPosition = 0

    private var revealX: Int = 0
    private var revealY: Int = 0

    private var handledShortCuts: Boolean = false

    private var modifyingToDoId = -1

    val drawerLayout: DrawerLayout by lazy {
        drawer_layout
    }

    val filterButton: ImageView by lazy {
        filter_button
    }

    val refreshLayout: SwipeRefreshLayout by lazy {
        main_refresh_layout
    }

    val toDoList: RecyclerView by lazy {
        to_do_list
    }

    val addFab: FloatingActionButton by lazy {
        add_fab
    }

    val addingView: AddingView by lazy {
        adding_view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)

        presenter = MainPresenter(this)

        drawerLayout.let {
            val toggle = ActionBarDrawerToggle(
                    this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            it.addDrawerListener(toggle)
            it.post { toggle.syncState() }
        }

        initViews()
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

        presenter?.start()
        if (cateAdapter != null) {
            refreshCategoryList()
        }
    }

    override fun onPause() {
        super.onPause()

        presenter?.stop()
    }

    override fun updateFilterIcon(filterOption: Int) {
        when (filterOption) {
            MainPresenter.FILTER_ALL -> filterButton.setImageResource(R.drawable.ic_filter_all)
            MainPresenter.FILTER_DONE -> filterButton.setImageResource(R.drawable.ic_filter_done)
            MainPresenter.FILTER_UNDONE -> filterButton.setImageResource(R.drawable.ic_filter_undone)
        }
    }

    private fun showFilterMenu() {
        val options = arrayOf(R.string.filter_all.getResString(),
                R.string.filter_undone.getResString(),
                R.string.filter_done.getResString())
        val builder = AlertDialog.Builder(this)
        builder.setSingleChoiceItems(options, presenter!!.filterOption) { dialog, which ->
            dialog.dismiss()
            presenter?.filterOption = which
        }
        builder.setTitle(R.string.filter_title)
        builder.setPositiveButton(R.string.confirm_ok) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun updateNoItemUi(show: Boolean) {
        if (show) {
            noItemLayout.visibility = View.VISIBLE
        } else {
            noItemLayout.visibility = View.GONE
        }
    }

    private fun initViews() {
        addingView.visibility = View.GONE

        toolbar.post { toolbar.title = getString(R.string.all) }

        refreshLayout.setOnRefreshListener {
            if (cateAdapter != null && cateAdapter!!.data!!.size > 0) {
                presenter!!.getToDos()
            } else {
                presenter!!.getCategories()
            }
        }

        drawerHeaderView = LayoutInflater.from(this).inflate(R.layout.drawer_header, drawerLayout, false)
        drawerHeaderView?.let {
            drawerEmailView = it.findViewById(R.id.drawerEmailView) as TextView
            undoneText = it.findViewById(R.id.undoneText) as TextView
        }
        drawerEmailView?.text = LocalSettingUtil.getString(this, Params.EMAIL_KEY, "")

        cateAdapter = CategoryAdapter()
        cateAdapter!!.apply {
            headerView = drawerHeaderView
            onSelected = handler@ { category, position ->
                if (selectedCategoryId == category.id) {
                    return@handler
                }
                if (category.id == ToDoCategory.VALUE_PERSONALIZATION_ID) {
                    drawerLayout.closeDrawer(Gravity.START)
                    startActivity<CategoryManagementActivity>()
                    return@handler
                }

                selectedCategoryPosition = position
                selectedCategoryId = category.id
                toDoAdapter!!.canDrag = selectedCategoryId == 0
                drawerRoot.background = ColorDrawable(category.intColor)
                addFab.backgroundTintList = ColorStateList.valueOf(category.intColor)
                toolbar.title = category.name

                if (selectedCategoryId == ToDoCategory.VALUE_DELETED_ID) {
                    addFab.setImageResource(R.drawable.ic_delete)
                } else {
                    addFab.setImageResource(R.drawable.ic_add)
                }

                drawerLayout.postDelayed({ drawerLayout.closeDrawer(GravityCompat.START) }, 300)

                presenter?.onCategorySelected(selectedCategoryPosition)
            }
        }

        categoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        categoryRecyclerView.adapter = cateAdapter

        toDoAdapter = ToDoAdapter()
        toDoAdapter?.apply {
            onArrangeCompleted = {
                uploadOrders()
            }
            onClickItem = onClick@ { position, cateView ->
                val location = IntArray(2)
                cateView.getLocationOnScreen(location)
                val x = location[0] + cateView.width / 2
                val y = location[1] + cateView.height / 2

                val toDo = getData(position)
                var index = cateAdapter!!.getItemIndexById(toDo.cate!!)
                if (index < 0 || index >= cateAdapter!!.data!!.size) {
                    index = 0
                }
                modifyingToDoId = Integer.parseInt(toDo.id)
                startRevealAnimation(x, y, object : StartEndAnimator() {
                    override fun onAnimationStart(animation: Animator) {
                        addingView.visibility = View.VISIBLE
                        addingView.mode = AddingView.MODIFY_MODE
                        addingView.content = toDo.content!!
                        addingView.selectedIndex = index
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        addingView.showInputPane()
                    }
                })
            }
            onClickRecover = { position ->
                val toDo = getData(position)
                presenter?.recoverToDo(toDo)
            }
            onUpdateDone = { position ->
                val toDo = getData(position)
                presenter?.updateIsDone(toDo.id!!, toDo.isdone!!)
            }
            onDelete = { position ->
                val toDo = getData(position)
                presenter?.deleteToDo(toDo)
            }
        }

        toDoList.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
        (toDoList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        toDoList.adapter = toDoAdapter

        addingView.setOnSelectionChangedCallback { position ->
            val toDoCategory = cateAdapter!!.getData(position)
            addingView.updateCategory(toDoCategory)
        }

        addingView.onClickOk = { cateIndex, content, mode ->
            hideAddingView()
            val category = cateAdapter!!.getData(cateIndex)
            when (mode) {
                AddingView.ADD_MODE -> presenter!!.addToDo(category.id.toString(), content)
                AddingView.MODIFY_MODE -> if (modifyingToDoId != -1) {
                    presenter!!.modifyToDo(
                            modifyingToDoId.toString(),
                            category.id.toString(),
                            content)
                    modifyingToDoId = -1
                }
            }
        }

        addingView.onClickCancel = {
            hideAddingView()
        }

        TypefaceUtil.setTypeFace(undoneText!!, "fonts/AGENCYB.TTF", this)

        refreshCategoryList()
        handleShortcutsAction(intent?.action)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == "action.add") {
            handledShortCuts = false
        }
        handleShortcutsAction(intent?.action)
    }

    private fun handleShortcutsAction(action: String?) {
        when (action) {
            "action.add" -> {
                if (!handledShortCuts) {
                    handledShortCuts = true
                    addingView.post { onClickFAB() }
                }
            }
        }
    }

    private fun initData() {
        presenter!!.getCategories()
    }

    @OnClick(R.id.filter_button)
    fun onClickFilter() {
        showFilterMenu()
    }

    @OnClick(R.id.drawer_settings)
    internal fun onClickSettings() {
        drawerLayout.closeDrawer(Gravity.START)
        startActivity<SettingsActivity>()
    }

    @OnClick(R.id.drawer_about)
    internal fun onClickAbout() {
        drawerLayout.closeDrawer(Gravity.START)
        startActivity<AboutActivity>()
    }

    @OnClick(R.id.add_fab)
    internal fun onClickFAB() {
        if (selectedCategoryId == ToDoCategory.VALUE_DELETED_ID) {
            if (toDoAdapter?.data?.size == 0) {
                return
            }
            AlertDialog.Builder(this)
                    .setTitle(getString(R.string.confirm_delete_title))
                    .setPositiveButton(getString(R.string.confirm_ok)) { _, _ -> presenter!!.clearDeletedList() }
                    .setNegativeButton(getString(R.string.confirm_cancel)) { dialog, _ -> dialog.dismiss() }
                    .create().show()
            return
        }
        val location = IntArray(2)
        addFab.getLocationOnScreen(location)

        val x = location[0] + resources.getDimensionPixelSize(R.dimen.fab_center_margin)
        val y = location[1] + resources.getDimensionPixelSize(R.dimen.fab_center_margin)

        startRevealAnimation(x, y, object : StartEndAnimator() {
            override fun onAnimationStart(animation: Animator) {
                addingView.visibility = View.VISIBLE
                addingView.mode = AddingView.ADD_MODE
                addingView.selectedIndex = selectedCategoryPosition
            }

            override fun onAnimationEnd(animation: Animator) {
                addingView.showInputPane()
            }
        })
    }

    private fun hideAddingView() {
        hideRevealAnimation(object : StartEndAnimator() {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator) {
                addingView.selectedIndex = 0
                addingView.visibility = View.GONE
                addingView.reset()
            }
        })
    }

    override fun refreshCategoryList() {
        val realm = Realm.getDefaultInstance()

        val categories = realm.where(ToDoCategory::class.java)
                .equalTo(ToDoCategory.KEY_SID, LocalSettingUtil.getString(this, Params.SID_KEY))
                .findAllSorted(ToDoCategory.KEY_POSITION, Sort.ASCENDING)

        val list = RealmList<ToDoCategory>()
        list.apply {
            this += categories
            add(0, ToDoCategory.allCategory)
            add(ToDoCategory.deletedCategory)
            add(ToDoCategory.personalizationCategory)
        }

        cateAdapter?.refreshData(list)
        cateAdapter?.selectItem(0)

        addingView.makeCategoriesSelection()
    }

    override fun refreshToDoList(filter: Int) {
        val realm = Realm.getDefaultInstance()
        var query = when (selectedCategoryId) {
            ToDoCategory.VALUE_DELETED_ID -> realm.where(ToDo::class.java)
                    .equalTo(ToDo.KEY_DELETED, true)
                    .equalTo(ToDoCategory.KEY_SID, LocalSettingUtil.getString(this, Params.SID_KEY))
            ToDoCategory.VALUE_ALL_ID -> realm.where(ToDo::class.java)
                    .notEqualTo(ToDo.KEY_DELETED, true)
            else -> realm.where(ToDo::class.java)
                    .notEqualTo(ToDo.KEY_DELETED, true)
                    .equalTo(ToDo.KEY_CATEGORY, selectedCategoryId.toString())
        }

        query = when (filter) {
            MainPresenter.FILTER_UNDONE -> {
                query.equalTo(ToDo.KEY_IS_DONE, ToDo.VALUE_UNDONE)
            }
            MainPresenter.FILTER_DONE -> {
                query.equalTo(ToDo.KEY_IS_DONE, ToDo.VALUE_DONE)
            }
            else -> {
                query
            }
        }

        val results = query.findAllSorted(ToDo.KEY_POSITION, Sort.ASCENDING)

        updateNoItemUi(results.size == 0)
        toDoAdapter!!.refreshData(results)
        updateCount()
        WidgetUpdater.update(this)
    }

    override fun notifyDataSetChanged() {
        toDoAdapter?.notifyDataSetChanged()
        WidgetUpdater.update(this)
        updateCount()
    }

    private fun updateCount() {
        val count = Realm.getDefaultInstance()
                .where(ToDo::class.java)
                .equalTo(ToDo.KEY_IS_DONE, ToDo.VALUE_UNDONE)
                .equalTo(ToDo.KEY_DELETED, false).count()
        undoneText?.text = count.toString()
    }

    private fun startRevealAnimation(x: Int, y: Int, animator: StartEndAnimator) {
        revealX = x
        revealY = y
        val width = window.decorView.width
        val height = window.decorView.height

        val radius = Math.sqrt(Math.pow(width.toDouble(), 2.0) + Math.pow(height.toDouble(), 2.0)) - resources.getDimensionPixelSize(R.dimen.fab_center_margin)
        val anim = ViewAnimationUtils.createCircularReveal(addingView, x, y, 0f, radius.toInt().toFloat())
        anim.addListener(animator)
        anim.start()
    }

    private fun hideRevealAnimation(animator: StartEndAnimator) {
        val radius = Math.max(window.decorView.width, window.decorView.height)
        val anim = ViewAnimationUtils.createCircularReveal(addingView, revealX, revealY, radius.toFloat(), 0f)
        anim.addListener(animator)
        anim.start()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        if (addingView.visibility == View.VISIBLE) {
            hideAddingView()
        } else {
            moveTaskToBack(true)
        }
    }

    override fun toggleRefreshing(show: Boolean) {
        refreshLayout.post {
            refreshLayout.isRefreshing = show
        }
    }

    override fun reCreateView() {
        recreate()
    }

    override fun uploadOrders() {
        val orderStr = StringBuilder()
        val todoList = Realm.getDefaultInstance().where(ToDo::class.java)
                .notEqualTo(ToDo.KEY_DELETED, true)
                .findAllSorted(ToDo.KEY_POSITION, Sort.ASCENDING)
        todoList.forEach {
            orderStr.append(it.id)
            orderStr.append(",")
        }
        orderStr.deleteCharAt(orderStr.length - 1)
        presenter!!.updateOrders(orderStr.toString())

        WidgetUpdater.update(this)
    }
}
