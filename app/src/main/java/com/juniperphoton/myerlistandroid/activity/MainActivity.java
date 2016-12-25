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
import com.juniperphoton.myerlistandroid.model.OrderedCateList;
import com.juniperphoton.myerlistandroid.model.OrderedToDoList;
import com.juniperphoton.myerlistandroid.model.ToDo;
import com.juniperphoton.myerlistandroid.model.ToDoCategory;
import com.juniperphoton.myerlistandroid.presenter.MainPresenter;
import com.juniperphoton.myerlistandroid.realm.RealmUtils;
import com.juniperphoton.myerlistandroid.util.AppConfig;
import com.juniperphoton.myerlistandroid.util.DisplayUtil;
import com.juniperphoton.myerlistandroid.util.StartEndAnimator;
import com.juniperphoton.myerlistandroid.util.LocalSettingUtil;
import com.juniperphoton.myerlistandroid.util.Params;
import com.juniperphoton.myerlistandroid.util.TypefaceUtil;
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

    @BindView(R.id.no_item_layout)
    View mNoItemLayout;

    private CategoryAdapter mCateAdapter;
    private ToDoAdapter mToDoAdapter;

    private MainPresenter mPresenter;

    private int mSelectedCategoryId = 0;
    private int mSelectedCategoryPosition = 0;

    private int mx;
    private int my;

    private boolean mHandledShortCuts;

    private int mModifyingToDoId = -1;

    private RealmChangeListener<OrderedToDoList> mRealmChangeListener = new RealmChangeListener<OrderedToDoList>() {
        @Override
        public void onChange(OrderedToDoList element) {
            //updateNoItemUi(element.getToDos().size() == 0);
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
        //RealmUtils.getMainInstance().close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.stop();
    }

    /**
     * 更新没有 Item 的 UI
     *
     * @param show 是否显示
     */
    private void updateNoItemUi(boolean show) {
        if (show) {
            mNoItemLayout.setVisibility(View.VISIBLE);
        } else {
            mNoItemLayout.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        mAddingView.setVisibility(View.GONE);
        mToolbar.setTitle(getString(R.string.all));

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mCateAdapter != null && mCateAdapter.getData().size() > 0) {
                    mPresenter.getToDos();
                } else {
                    mPresenter.getCate();
                }
            }
        });
        mEmailView.setText(LocalSettingUtil.getString(this, Params.EMAIL_KEY, ""));

        mCateAdapter = new CategoryAdapter();
        mCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        mCategoryRecyclerView.setAdapter(mCateAdapter);
        mCateAdapter.setSelectedCallback(MainActivity.this);

        mToDoAdapter = new ToDoAdapter();
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
        TypefaceUtil.setTypeFace(mUndoneText, "fonts/AGENCYB.TTF", this);

        displayCategories();
        displayToDos();
        handleShortcutsAction();
    }

    /**
     * 处理 7.1 的快捷方式
     */
    private void handleShortcutsAction() {
        String action = getIntent().getAction();
        if (action == null) {
            return;
        }
        switch (action) {
            case "action.add": {
                if (!mHandledShortCuts) {
                    mHandledShortCuts = true;
                    mAddingView.post(new Runnable() {
                        @Override
                        public void run() {
                            onClickAdd();
                        }
                    });
                }
            }
            break;
        }
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

        startRevealAnimation(x, y, new StartEndAnimator() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAddingView.setVisibleMode(View.VISIBLE, AddingView.ADD_MODE);
                mAddingView.setSelected(mSelectedCategoryPosition);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAddingView.showInputPane();
            }
        });
    }

    private void hideAddingView() {
        hideRevealAnimation(new StartEndAnimator() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAddingView.setSelected(0);
                mAddingView.setVisibility(View.GONE);
                mAddingView.reset();
            }
        });
    }

    @Override
    public void displayCategories() {
        mRefreshLayout.setRefreshing(true);

        Realm realm = RealmUtils.getMainInstance();
        realm.beginTransaction();

        OrderedCateList categories = realm.where(OrderedCateList.class).findFirst();
        List<ToDoCategory> list = new ArrayList<>();
        if (categories != null) {
            for (ToDoCategory cate : categories.getCates()) {
                list.add(cate);
            }
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
        RealmUtils.getMainInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                OrderedToDoList query = realm.where(OrderedToDoList.class).findFirst();
                if (query == null) return;
                RealmList<ToDo> list = query.getToDos();
                query.addChangeListener(mRealmChangeListener);
                long count;

                switch (mSelectedCategoryId) {
                    case ToDoCategory.ALL_ID:
                        mToDoAdapter.refreshData(list);
                        updateNoItemUi(list.size() == 0);
                        break;
                    case ToDoCategory.DELETED_ID:
                        DeletedList deletedList = realm.where(DeletedList.class).findFirst();
                        if (deletedList != null) {
                            list = deletedList.getToDos();
                            mToDoAdapter.refreshData(list);
                        }
                        break;
                    case ToDoCategory.PERSONALIZATION_ID:
                        break;
                    default:
                        RealmResults<ToDo> toDos = list.where().equalTo("cate", String.valueOf(mSelectedCategoryId)).findAll();
                        mToDoAdapter.refreshData(toDos);
                        updateNoItemUi(toDos.size() == 0);
                        break;
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

    private void startRevealAnimation(int x, int y, StartEndAnimator animator) {
        mx = x;
        my = y;
        int radius = Math.max(getWindow().getDecorView().getWidth(), getWindow().getDecorView().getHeight());
        Animator anim = ViewAnimationUtils.createCircularReveal(mAddingView, x, y, 0, radius);
        anim.addListener(animator);
        anim.start();
    }

    private void hideRevealAnimation(StartEndAnimator animator) {
        int radius = Math.max(getWindow().getDecorView().getWidth(), getWindow().getDecorView().getHeight());
        Animator anim = ViewAnimationUtils.createCircularReveal(mAddingView, mx, my, radius, 0);
        anim.addListener(animator);
        anim.start();
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
        mToolbar.setTitle(category.getName());

        mDrawerLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        }, 300);

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
        mPresenter.deleteToDo(toDo);
    }

    @Override
    public void onClickedItem(int position, View cateView) {
        int[] location = new int[2];
        cateView.getLocationOnScreen(location);
        int radius = Math.max(getWindow().getDecorView().getWidth(), getWindow().getDecorView().getHeight());
        int x = location[0] + cateView.getWidth() / 2;
        int y = location[1] + cateView.getHeight() / 2;

        final ToDo toDo = mToDoAdapter.getData(position);
        final int index = mCateAdapter.getItemIndexById(toDo.getCate());
        if (index < 0) {
            return;
        }
        mModifyingToDoId = Integer.parseInt(toDo.getId());
        startRevealAnimation(x, y, new StartEndAnimator() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAddingView.setVisibleMode(View.VISIBLE, AddingView.MODIFY_MODE);
                mAddingView.setSelected(index);
                mAddingView.setContent(toDo.getContent());
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAddingView.showInputPane();
            }
        });
    }

    @Override
    public void onClickOk(int cateIndex, String content, int mode) {
        hideAddingView();
        ToDoCategory category = mCateAdapter.getData(cateIndex);
        if (category == null) return;
        switch (mode) {
            case AddingView.ADD_MODE:
                mPresenter.addToDo(String.valueOf(category.getId()), content);
                break;
            case AddingView.MODIFY_MODE:
                if (mModifyingToDoId != -1) {
                    mPresenter.modifyToDo(String.valueOf(category.getId()), content,
                            String.valueOf(mModifyingToDoId));
                    mModifyingToDoId = -1;
                    break;
                }
        }
    }

    @Override
    public void onClickCancel() {
        hideAddingView();
    }
}
