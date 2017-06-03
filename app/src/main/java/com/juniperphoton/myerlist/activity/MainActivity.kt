package com.juniperphoton.myerlist.activity

import android.animation.Animator
import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewAnimationUtils
import butterknife.ButterKnife
import butterknife.OnClick
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.adapter.CategoryAdapter
import com.juniperphoton.myerlist.adapter.ToDoAdapter
import com.juniperphoton.myerlist.event.ReCreateEvent
import com.juniperphoton.myerlist.event.RefreshToDoEvent
import com.juniperphoton.myerlist.model.ToDo
import com.juniperphoton.myerlist.model.ToDoCategory
import com.juniperphoton.myerlist.presenter.MainContract
import com.juniperphoton.myerlist.presenter.MainPresenter
import com.juniperphoton.myerlist.realm.RealmUtils
import com.juniperphoton.myerlist.util.LocalSettingUtil
import com.juniperphoton.myerlist.util.Params
import com.juniperphoton.myerlist.util.StartEndAnimator
import com.juniperphoton.myerlist.util.TypefaceUtil
import com.juniperphoton.myerlist.widget.AddingView
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_drawer.*
import kotlinx.android.synthetic.main.no_item_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

@Suppress("unused", "unused_parameter")
class MainActivity : BaseActivity(), MainContract.View {
    companion object {
        private val TAG = "MainActivity"
    }

    private var cateAdapter: CategoryAdapter? = null
    private var toDoAdapter: ToDoAdapter? = null

    private var presenter: MainContract.Presenter? = null

    private var selectedCategoryId = 0
    private var selectedCategoryPosition = 0

    private var revealX: Int = 0
    private var revealY: Int = 0

    private var handledShortCuts: Boolean = false

    private var modifyingToDoId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        presenter = MainPresenter(this)

        if (drawerLayout != null) {
            val toggle = ActionBarDrawerToggle(
                    this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            drawerLayout.addDrawerListener(toggle)
            drawerLayout.post { toggle.syncState() }
        }

        initViews()
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "destroyed")
    }

    override fun onResume() {
        super.onResume()
        presenter!!.start()
        if (cateAdapter != null) {
            refreshCategoryList()
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onPause() {
        super.onPause()
        presenter!!.stop()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
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

        mainRefreshLayout.setOnRefreshListener {
            if (cateAdapter != null && cateAdapter!!.data!!.size > 0) {
                presenter!!.getToDos()
            } else {
                presenter!!.getCategories()
            }
        }
        drawerEmailView.text = LocalSettingUtil.getString(this, Params.EMAIL_KEY, "")

        cateAdapter = CategoryAdapter()
        cateAdapter!!.onSelected = handler@ { category, position ->
            if (selectedCategoryId == category.id) {
                return@handler
            }
            if (category.id == ToDoCategory.PERSONALIZATION_ID) {
                drawerLayout?.closeDrawer(Gravity.START)
                val intent = Intent(this@MainActivity, CategoryManagementActivity::class.java)
                startActivity(intent)
                return@handler
            }

            selectedCategoryPosition = position
            selectedCategoryId = category.id
            toDoAdapter!!.canDrag = selectedCategoryId == 0
            drawerRoot.background = ColorDrawable(category.intColor)
            addFAB.backgroundTintList = ColorStateList.valueOf(category.intColor)
            toolbar.title = category.name

            if (selectedCategoryId == ToDoCategory.DELETED_ID) {
                addFAB.setImageResource(R.drawable.ic_delete)
            } else {
                addFAB.setImageResource(R.drawable.ic_add)
            }

            drawerLayout.postDelayed({ drawerLayout.closeDrawer(GravityCompat.START) }, 300)

            refreshToDoList()
        }

        categoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        categoryRecyclerView.adapter = cateAdapter

        toDoAdapter = ToDoAdapter()
        toDoAdapter!!.onArrangeCompleted = {
            uploadOrders()
        }
        toDoAdapter!!.onClickItem = onClick@ { position, cateView ->
            val location = IntArray(2)
            cateView.getLocationOnScreen(location)
            val x = location[0] + cateView.width / 2
            val y = location[1] + cateView.height / 2

            val toDo = toDoAdapter!!.getData(position)
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
        toDoAdapter!!.onClickRecover = { position ->
            val toDo = toDoAdapter!!.getData(position)
            presenter!!.recoverToDo(toDo)
        }
        toDoAdapter!!.onUpdateDone = { position ->
            val toDo = toDoAdapter!!.getData(position)
            presenter!!.updateIsDone(toDo)
            updateCount()
        }
        toDoAdapter!!.onDelete = { position ->
            val toDo = toDoAdapter!!.getData(position)
            presenter!!.deleteToDo(toDo)
        }

        toDoRecyclerView.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
        (toDoRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        toDoRecyclerView.adapter = toDoAdapter

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
                    presenter!!.modifyToDo(category.id.toString(), content,
                            modifyingToDoId.toString())
                    modifyingToDoId = -1
                }
            }
        }

        addingView.onClickCancel = {
            hideAddingView()
        }

        TypefaceUtil.setTypeFace(undoneText, "fonts/AGENCYB.TTF", this)

        refreshCategoryList()
        //refreshToDoList()
        handleShortcutsAction()
    }

    private fun handleShortcutsAction() {
        val action = intent.action ?: return
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

    @OnClick(R.id.drawer_settings)
    internal fun onClickSettings() {
        drawerLayout?.closeDrawer(Gravity.START)
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    @OnClick(R.id.drawer_about)
    internal fun onClickAbout() {
        drawerLayout?.closeDrawer(Gravity.START)
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

    @OnClick(R.id.addFAB)
    internal fun onClickFAB() {
        if (selectedCategoryId == ToDoCategory.DELETED_ID) {
            if (toDoAdapter!!.data!!.size == 0) {
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
        addFAB.getLocationOnScreen(location)

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
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                addingView.selectedIndex = 0
                addingView.visibility = View.GONE
                addingView.reset()
            }
        })
    }

    override fun refreshCategoryList() {
        val realm = RealmUtils.mainInstance

        val categories = realm.where(ToDoCategory::class.java)
                .equalTo(ToDoCategory.SID_KEY, LocalSettingUtil.getString(this, Params.SID_KEY))
                .findAllSorted(ToDoCategory.POSITION_KEY, Sort.ASCENDING)
        val list = ArrayList<ToDoCategory>()
        if (categories != null) {
            list += categories
        }
        list.add(0, ToDoCategory.allCategory)
        list.add(ToDoCategory.deletedCategory)
        list.add(ToDoCategory.personalizationCategory)

        cateAdapter!!.refreshData(list)
        cateAdapter!!.selectItem(0)

        addingView.makeCategoriesSelection()
    }

    override fun refreshToDoList() {
        var results: RealmResults<ToDo>? = null

        val realm = RealmUtils.mainInstance

        when (selectedCategoryId) {
            ToDoCategory.PERSONALIZATION_ID -> {
                // empty
            }
            ToDoCategory.DELETED_ID -> results = realm.where(ToDo::class.java)
                    .equalTo(ToDo.DELETED_KEY, java.lang.Boolean.TRUE)
                    .equalTo(ToDoCategory.SID_KEY, LocalSettingUtil.getString(this, Params.SID_KEY))
                    .findAllSorted(ToDo.POSITION_KEY, Sort.ASCENDING)
            ToDoCategory.ALL_ID -> results = realm.where(ToDo::class.java).notEqualTo(ToDo.DELETED_KEY, java.lang.Boolean.TRUE)
                    .findAllSorted(ToDo.POSITION_KEY, Sort.ASCENDING)
            else -> results = realm.where(ToDo::class.java)
                    .notEqualTo(ToDo.DELETED_KEY, java.lang.Boolean.TRUE)
                    .equalTo(ToDo.CATE_KEY, selectedCategoryId.toString())
                    .findAllSorted(ToDo.POSITION_KEY, Sort.ASCENDING)
        }

        updateNoItemUi(results!!.size == 0)
        toDoAdapter!!.refreshData(results.toMutableList())

        updateCount()
    }

    override fun notifyDataSetChanged() {
        toDoAdapter?.notifyDataSetChanged()
    }

    private fun updateCount() {
        val toDos = toDoAdapter!!.data
        val count = toDos!!.count { it.isdone == ToDo.IS_NOT_DONE }
        undoneText.text = count.toString()
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
        } else if (selectedCategoryId != 0) {
            cateAdapter!!.selectItem(0)
        } else {
            super.onBackPressed()
        }
    }

    override fun toggleRefreshing(show: Boolean) {
        mainRefreshLayout?.post {
            mainRefreshLayout?.isRefreshing = show
        }
    }

    override fun uploadOrders() {
        val orderStr = StringBuilder()
        for (toDo in toDoAdapter!!.data!!) {
            orderStr.append(toDo.id)
            orderStr.append(",")
        }
        orderStr.deleteCharAt(orderStr.length - 1)
        presenter!!.updateOrders(orderStr.toString())
    }

    override fun notifyToDoDeleted(pos: Int) {
        refreshToDoList()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun receiveEvent(event: ReCreateEvent) {
        recreate()
        EventBus.getDefault().removeAllStickyEvents()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun receiveRefreshToDoEvent(event: RefreshToDoEvent) {
        refreshToDoList()
        EventBus.getDefault().removeAllStickyEvents()
    }
}
