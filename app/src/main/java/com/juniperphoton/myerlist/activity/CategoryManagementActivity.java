package com.juniperphoton.myerlist.activity;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juniperphoton.myerlist.R;
import com.juniperphoton.myerlist.adapter.CustomCategoryAdapter;
import com.juniperphoton.myerlist.model.ToDoCategory;
import com.juniperphoton.myerlist.presenter.CustomCategoryContract;
import com.juniperphoton.myerlist.presenter.CustomCategoryPresenter;
import com.juniperphoton.myerlist.util.ColorUtil;
import com.juniperphoton.myerlist.util.DisplayUtil;
import com.juniperphoton.myerlist.util.KeyboardUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CategoryManagementActivity extends BaseActivity implements CustomCategoryContract.View,
        CustomCategoryAdapter.Callback {
    @BindView(R.id.activity_category_manage_list)
    RecyclerView mCategoryList;

    @BindView(R.id.activity_cate_per_cancel_view)
    View mCancelView;

    private CustomCategoryAdapter mAdapter;
    private CustomCategoryContract.Presenter mPresenter;
    private ProgressDialog mProgressDialog;
    private ToDoCategory mCategory;
    private View mHeaderView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);
        ButterKnife.bind(this);

        initUi();

        mPresenter = new CustomCategoryPresenter(this);
    }

    @Override
    public void onBackPressed() {
        prepareToExit();
    }

    private void prepareToExit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.exit_without_saving)
                .setPositiveButton(R.string.ok_adding, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        CategoryManagementActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.cancel_adding, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.stop();
    }

    private void initUi() {
        createHeader();

        mAdapter = new CustomCategoryAdapter(this);
        mAdapter.setCallback(this);
        mAdapter.setHeaderView(mHeaderView);
        mCategoryList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mCategoryList.setAdapter(mAdapter);

        mCancelView.requestFocus();
    }

    private void createHeader() {
        mHeaderView = LayoutInflater.from(this).inflate(R.layout.add_cate_header, null, false);
        mHeaderView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                DisplayUtil.getDimenInPixel(52, this)));
        mHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToDoCategory category = new ToDoCategory();
                category.setName("New cate");
                category.setColor("#FF4096C9");
                mAdapter.addData(category);
            }
        });
    }

    public void initData(List<ToDoCategory> data) {
        if (mAdapter == null || data == null) {
            return;
        }
        mAdapter.refreshData(data);
    }

    @Override
    public void showDialog() {
        mProgressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle(getString(R.string.loading_hint));
        mProgressDialog.show();
    }

    @Override
    public void hideDialog(int delay) {
        if (mProgressDialog != null) {
            mCancelView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mProgressDialog.hide();
                }
            }, delay);
        }
    }

    @Override
    public void hideKeyboard() {
        KeyboardUtil.hide(this, mCancelView.getWindowToken());
    }

    @OnClick(R.id.activity_cate_per_cancel_view)
    public void onClickCancel() {
        prepareToExit();
    }

    @OnClick(R.id.activity_cate_per_commit_view)
    public void onClickOk() {
        mPresenter.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        int color = data.getIntExtra(PickColorActivity.RESULT_KEY, Color.BLACK);
        if (mCategory != null) {
            mCategory.setColor(ColorUtil.fromInt(color));
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClickSelectCategory(final ToDoCategory category) {
        mCategory = category;
        Intent intent = new Intent(this, PickColorActivity.class);
        startActivityForResult(intent, 0);
    }
}
