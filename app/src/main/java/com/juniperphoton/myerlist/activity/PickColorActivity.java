package com.juniperphoton.myerlist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.juniperphoton.myerlist.R;
import com.juniperphoton.myerlist.adapter.PickColorAdapter;
import com.juniperphoton.myerlist.util.ColorUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PickColorActivity extends BaseActivity {
    private static final int SPAN_COUNT = 8;
    public static final String RESULT_KEY = "picked_color";

    @BindView(R.id.color_picker_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.color_picker_root)
    View mRoot;

    private List<Integer> mColors;
    private PickColorAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_picker);
        ButterKnife.bind(this);
        init();
        mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void init() {
        mColors = ColorUtil.generateColors();

        mAdapter = new PickColorAdapter(this);
        mAdapter.setCallback(new PickColorAdapter.Callback() {
            @Override
            public void onSelectColor(int color) {
                Intent intent = new Intent();
                intent.putExtra(RESULT_KEY, color);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.refreshData(mColors);
    }
}
