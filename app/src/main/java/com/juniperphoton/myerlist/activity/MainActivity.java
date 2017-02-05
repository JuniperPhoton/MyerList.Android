package com.juniperphoton.myerlist.activity;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.TextView;

import com.juniperphoton.myerlist.R;
import com.juniperphoton.myerlist.adapter.CategoryAdapter;
import com.juniperphoton.myerlist.adapter.ToDoAdapter;
import com.juniperphoton.myerlist.callback.OnDrawerSelectedChanged;
import com.juniperphoton.myerlist.callback.OnItemOperationCompletedCallback;
import com.juniperphoton.myerlist.model.OrderedCateList;
import com.juniperphoton.myerlist.model.ToDo;
import com.juniperphoton.myerlist.model.ToDoCategory;
import com.juniperphoton.myerlist.presenter.MainContract;
import com.juniperphoton.myerlist.presenter.MainPresenter;
import com.juniperphoton.myerlist.realm.RealmUtils;
import com.juniperphoton.myerlist.util.AppConfig;
import com.juniperphoton.myerlist.util.DisplayUtil;
import com.juniperphoton.myerlist.util.LocalSettingUtil;
import com.juniperphoton.myerlist.util.Params;
import com.juniperphoton.myerlist.util.StartEndAnimator;
import com.juniperphoton.myerlist.util.TypefaceUtil;
import com.juniperphoton.myerlist.widget.AddingView;
import com.juniperphoton.myerlist.widget.SelectCategoryView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

@SuppressWarnings("UnusedDeclaration")
public class MainActivity extends BaseActivity implements MainContract.View, OnDrawerSelectedChanged,
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

    private MainContract.Presenter mPresenter;

    private int mSelectedCategoryId = 0;
    private int mSelectedCategoryPosition = 0;

    private int mx;
    private int my;

    private boolean mHandledShortCuts;

    private int mModifyingToDoId = -1;

    private RealmChangeListener<RealmResults<ToDo>> mRealmChangeListener = new RealmChangeListener<RealmResults<ToDo>>() {
        @Override
        public void onChange(RealmResults<ToDo> element) {
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

        mToolbar.post(new Runnable() {
            @Override
            public void run() {
                mToolbar.setTitle(getString(R.string.all));
            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mCateAdapter != null && mCateAdapter.getData().size() > 0) {
                    mPresenter.getToDos();
                } else {
                    mPresenter.getCates();
                }
            }
        });
        mEmailView.setText(LocalSettingUtil.getString(this, Params.EMAIL_KEY, ""));

        mCateAdapter = new CategoryAdapter(this);
        mCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        mCategoryRecyclerView.setAdapter(mCateAdapter);
        mCateAdapter.setSelectedCallback(MainActivity.this);

        mToDoAdapter = new ToDoAdapter();
        mToDoAdapter.setCallback(this);
        mToDoRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        ((SimpleItemAnimator) mToDoRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
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
                            onClickFAB();
                        }
                    });
                }
            }
            break;
        }
    }

    private void initData() {
        mPresenter.getCates();
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
    void onClickFAB() {
        if (mSelectedCategoryId == ToDoCategory.DELETED_ID) {
            if (mToDoAdapter.getData().size() == 0) {
                return;
            }
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.confirm_delete_title))
                    .setPositiveButton(getString(R.string.confirm_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPresenter.clearDeletedList();
                        }
                    })
                    .setNegativeButton(getString(R.string.confirm_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
            return;
        }
        int[] location = new int[2];
        mAddFAB.getLocationOnScreen(location);

        int x = location[0] + getResources().getDimensionPixelSize(R.dimen.fab_center_margin);
        int y = location[1] + getResources().getDimensionPixelSize(R.dimen.fab_center_margin);

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
        List<ToDo> resultsWrapper = new ArrayList<>();

        Realm realm = RealmUtils.getMainInstance();
        realm.beginTransaction();

        switch (mSelectedCategoryId) {
            case ToDoCategory.PERSONALIZATION_ID:
                break;
            case ToDoCategory.DELETED_ID:
                RealmResults<ToDo> deletedResults = realm.where(ToDo.class)
                        .equalTo(ToDo.DELETED_KEY, Boolean.TRUE)
                        .findAllSorted(ToDo.POSITION_KEY, Sort.ASCENDING);
                for (ToDo toDo : deletedResults) {
                    resultsWrapper.add(toDo);
                }
                break;
            case ToDoCategory.ALL_ID:
                RealmResults<ToDo> results = realm.where(ToDo.class).notEqualTo(ToDo.DELETED_KEY, Boolean.TRUE)
                        .findAllSorted(ToDo.POSITION_KEY, Sort.ASCENDING);
                for (ToDo toDo : results) {
                    resultsWrapper.add(toDo);
                }
                break;
            default:
                RealmResults<ToDo> toDos = realm.where(ToDo.class)
                        .notEqualTo(ToDo.DELETED_KEY, Boolean.TRUE)
                        .equalTo(ToDo.CATE_KEY, String.valueOf(mSelectedCategoryId))
                        .findAllSorted(ToDo.POSITION_KEY, Sort.ASCENDING);
                for (ToDo toDo : toDos) {
                    resultsWrapper.add(toDo);
                }
                break;
        }
        updateNoItemUi(resultsWrapper.size() == 0);
        mToDoAdapter.refreshData(resultsWrapper);

        realm.commitTransaction();

        updateCount();
        mRefreshLayout.setRefreshing(false);
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
        if (category.getId() == ToDoCategory.PERSONALIZATION_ID) {
            Intent intent = new Intent(MainActivity.this, CategoryManagementActivity.class);
            startActivity(intent);
            return;
        }

        mSelectedCategoryPosition = position;
        mSelectedCategoryId = category.getId();
        mToDoAdapter.setCanDrag(mSelectedCategoryId == 0);
        mDrawerRoot.setBackground(new ColorDrawable(category.getIntColor()));
        mAddFAB.setBackgroundTintList(ColorStateList.valueOf(category.getIntColor()));
        mToolbar.setTitle(category.getName());

        if (mSelectedCategoryId == ToDoCategory.DELETED_ID) {
            mAddFAB.setImageResource(R.drawable.ic_delete);
        } else {
            mAddFAB.setImageResource(R.drawable.ic_add);
        }

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
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
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
        uploadOrders();
    }

    @Override
    public void uploadOrders() {
        StringBuilder orderStr = new StringBuilder();
        for (ToDo toDo : mToDoAdapter.getData()) {
            orderStr.append(toDo.getId());
            orderStr.append(",");
        }
        orderStr.deleteCharAt(orderStr.length() - 1);
        mPresenter.updateOrders(orderStr.toString());
    }

    @Override
    public void notifyToDoDeleted(int pos) {
        displayToDos();
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
    public void onClickRecover(int position) {
        ToDo toDo = mToDoAdapter.getData(position);
        mPresenter.recoverToDo(toDo);
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
