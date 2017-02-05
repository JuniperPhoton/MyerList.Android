package com.juniperphoton.myerlist.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.juniperphoton.myerlist.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryManagementActivity extends BaseActivity {

    @BindView(R.id.activity_category_manage_list)
    RecyclerView mCategoryList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);
        ButterKnife.bind(this);
    }
}
