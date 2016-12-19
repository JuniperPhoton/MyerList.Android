package com.juniperphoton.myerlistandroid.activity;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.TextView;

import com.juniperphoton.myerlistandroid.R;
import com.juniperphoton.myerlistandroid.adapter.CategoryAdapter;
import com.juniperphoton.myerlistandroid.adapter.ToDoAdapter;
import com.juniperphoton.myerlistandroid.callback.OnItemOperationCompletedCallback;
import com.juniperphoton.myerlistandroid.callback.OnDrawerSelectedChanged;
import com.juniperphoton.myerlistandroid.model.DeletedList;
import com.juniperphoton.myerlistandroid.model.OrderedToDoList;
import com.juniperphoton.myerlistandroid.model.ToDo;
import com.juniperphoton.myerlistandroid.model.ToDoCategory;
import com.juniperphoton.myerlistandroid.presenter.MainPresenter;
import com.juniperphoton.myerlistandroid.util.AppConfig;
import com.juniperphoton.myerlistandroid.util.DisplayUtil;
import com.juniperphoton.myerlistandroid.util.EndAnimator;
import com.juniperphoton.myerlistandroid.util.LocalSettingUtil;
import com.juniperphoton.myerlistandroid.util.Params;
import com.juniperphoton.myerlistandroid.view.MainView;
import com.juniperphoton.myerlistandroid.widget.AddingView;
import com.juniperphoton.myerlistandroid.widget.SelectCategoryView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

@SuppressWarnings("UnusedDeclaration")
public class MainActivity extends BaseActivity implements MainView, OnDrawerSelectedChanged,
        OnItemOperationCompletedCallback, AddingView.AddingViewCallback {

    private static final String TAG = "MainActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.activity_drawer_list)
    RecyclerView mCategoryRecyclerView;

    @BindView(R.id.todo_list)
    RecyclerView mToDoRecyclerView;

    @BindView(R.id.drawer_root)
    View mDrawerRoot;

    @BindView(R.id.add_fab)
    FloatingActionButton mAddFAB;

    @BindView(R.id.drawer_account_email)
    TextView mEmailView;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.undone_text)
    TextView mUndoneText;

    @BindView(R.id.main_adding_view)
    AddingView mAddingView;

    private CategoryAdapter mCateAdapter;
    private ToDoAdapter mToDoAdapter;

    private MainPresenter mPresenter;

    private int mSelectedCategoryId = 0;
    private int mSelectedCategoryPosition = 0;

    private RealmChangeListener realmChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            displayCategories();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mPresenter = new MainPresenter(this);

        if (mDrawerLayout != null) {
            final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            mDrawerLayout.addDrawerListener(toggle);
            mDrawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    toggle.syncState();
                }
            });
        }

        if (!AppConfig.hasLogined()) {
            Intent intent = new Intent();
            intent.setClass(this, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            initViews();
            initData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "destroyed");
        Realm.getDefaultInstance().close();
    }

    private void initViews() {
        mAddingView.setVisibility(View.GONE);
        mToolbar.setTitle(getString(R.string.all));

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getCate();
            }
        });
        mEmailView.setText(LocalSettingUtil.getString(this, Params.EMAIL_KEY, ""));

        mCateAdapter = new CategoryAdapter(new ArrayList<ToDoCategory>());
        mCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        mCategoryRecyclerView.setAdapter(mCateAdapter);
        mCateAdapter.setSelectedCallback(MainActivity.this);

        mToDoAdapter = new ToDoAdapter(new ArrayList<ToDo>());
        mToDoAdapter.setCallback(this);
        mToDoRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        mToDoRecyclerView.setAdapter(mToDoAdapter);

        mAddingView.setOnSelectionChangedCallback(new SelectCategoryView.OnSelectionChangedCallback() {
            @Override
            public void onSelectionChanged(int position) {
                ToDoCategory toDoCategory = mCateAdapter.getData(position);
                mAddingView.updateCategory(toDoCategory);
            }
        });

        mAddingView.setCallback(this);

        displayCategories();
        displayToDos();
    }

    private void initData() {
        mPresenter.getCate();
    }

    @OnClick(R.id.drawer_settings)
    void onClickSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.drawer_about)
    void onClickAbout() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.add_fab)
    void onClickAdd() {
        int[] location = new int[2];
        mAddFAB.getLocationOnScreen(location);

        int x = location[0] + DisplayUtil.getDimenInPixel(28, this);
        int y = location[1] + DisplayUtil.getDimenInPixel(28, this);

        int radius = Math.max(getWindow().getDecorView().getWidth(), getWindow().getDecorView().getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(mAddingView, x, y, 0, radius);
        mAddingView.setVisibility(View.VISIBLE);
        mAddingView.setSelected(mSelectedCategoryPosition);
        anim.addListener(new EndAnimator() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAddingView.showInputPane();
            }
        });
        anim.start();
    }

    private void hideAddingView() {
        int[] location = new int[2];
        mAddFAB.getLocationOnScreen(location);

        int x = location[0] + DisplayUtil.getDimenInPixel(28, this);
        int y = location[1] + DisplayUtil.getDimenInPixel(28, this);

        int radius = Math.max(getWindow().getDecorView().getWidth(), getWindow().getDecorView().getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(mAddingView, x, y, radius, 0);
        anim.addListener(new EndAnimator() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAddingView.setSelected(0);
                mAddingView.setVisibility(View.GONE);
            }
        });
        anim.start();
    }

    @Override
    public void displayCategories() {
        mRefreshLayout.setRefreshing(true);

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        RealmResults<ToDoCategory> categories = realm.where(ToDoCategory.class).findAll();
        ArrayList<ToDoCategory> list = new ArrayList<>();
        for (ToDoCategory cate : categories) {
            list.add(cate);
        }
        list.add(0, ToDoCategory.getAllCategory());
        list.add(ToDoCategory.getDeletedCategory());
        list.add(ToDoCategory.getPersonalizationCategory());
        mCateAdapter.refreshData(list);
        mCateAdapter.selectItem(0);

        realm.commitTransaction();

        mAddingView.makeCategoriesSelection();
    }

    @Override
    public void displayToDos() {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                OrderedToDoList query = realm.where(OrderedToDoList.class).findFirst();
                if (query == null) return;
                RealmList<ToDo> list = query.getToDos();
                long count;
                if (mSelectedCategoryId == 0) {
                    mToDoAdapter.refreshData(list);
                } else if (mSelectedCategoryId == -1) {
                    DeletedList deletedList = realm.where(DeletedList.class).findFirst();
                    if (deletedList != null) {
                        list = deletedList.getToDos();
                        mToDoAdapter.refreshData(list);
                    }
                } else {
                    RealmResults<ToDo> toDos = list.where().equalTo("cate", String.valueOf(mSelectedCategoryId)).findAll();
                    mToDoAdapter.refreshData(toDos);
                }

                updateCount();
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void updateCount() {
        int count = 0;
        List<ToDo> toDos = mToDoAdapter.getData();
        for (ToDo todo : toDos) {
            if (todo.getIsdone().equals("0")) {
                count++;
            }
        }
        mUndoneText.setText(String.valueOf(count));
    }

    @Override
    public void onSelectedChanged(ToDoCategory category, int position) {
        if (mSelectedCategoryId == category.getId()) {
            return;
        }
        mSelectedCategoryPosition = position;
        mSelectedCategoryId = category.getId();
        mToDoAdapter.setCanDrag(mSelectedCategoryId == 0);
        mDrawerRoot.setBackground(new ColorDrawable(category.getIntColor()));
        mAddFAB.setBackgroundTintList(ColorStateList.valueOf(category.getIntColor()));
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mToolbar.setTitle(category.getName());
        displayToDos();
    }

    @Override
    public void onBackPressed() {
        if (mAddingView.getVisibility() == View.VISIBLE) {
            hideAddingView();
        } else if (mSelectedCategoryId != 0) {
            mCateAdapter.selectItem(0);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onArrangeCompleted() {
        StringBuilder orderStr = new StringBuilder();
        for (ToDo toDo : mToDoAdapter.getData()) {
            orderStr.append(toDo.getId());
            orderStr.append(",");
        }
        orderStr.deleteCharAt(orderStr.length() - 1);
        mPresenter.updateOrders(orderStr.toString());
    }

    @Override
    public void onUpdateDone(int position) {
        ToDo toDo = mToDoAdapter.getData(position);
        mPresenter.updateIsDone(toDo);
        updateCount();
    }

    @Override
    public void onDelete(int position) {
        ToDo toDo = mToDoAdapter.getData(position);
        mToDoAdapter.removeData(position);
        mPresenter.deleteToDo(toDo);
    }

    @Override
    public void onClickOk(int cateIndex, String content) {
        hideAddingView();
        ToDoCategory category = mCateAdapter.getData(cateIndex);
        if (category != null) {
            mPresenter.addToDo(String.valueOf(category.getId()), content);
        }
    }

    @Override
    public void onClickCancel() {
        hideAddingView();
    }
}
