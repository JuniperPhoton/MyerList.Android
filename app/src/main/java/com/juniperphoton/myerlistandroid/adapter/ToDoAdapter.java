package com.juniperphoton.myerlistandroid.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juniperphoton.myerlistandroid.App;
import com.juniperphoton.myerlistandroid.R;
import com.juniperphoton.myerlistandroid.model.ToDo;
import com.juniperphoton.myerlistandroid.model.ToDoCategory;
import com.juniperphoton.myerlistandroid.widget.CircleView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class ToDoAdapter extends BaseAdapter<ToDo, ToDoAdapter.ToDoViewHolder> {

    public ToDoAdapter(List<ToDo> data) {
        super(data);
    }

    @Override
    protected ToDoAdapter.ToDoViewHolder onCreateItemViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(App.getInstance()).inflate(R.layout.row_todo, parent, false);
        return new ToDoViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(ToDoAdapter.ToDoViewHolder holder, int dataPosition) {
        holder.bind(holder.getAdapterPosition());
    }

    class ToDoViewHolder extends BaseAdapter.BaseViewHolder {
        @BindView(R.id.row_todo_color_view)
        CircleView circleView;

        @BindView(R.id.row_todo_content)
        TextView contentView;

        @BindView(R.id.done_line)
        View doneView;

        ToDoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(int position) {
            final ToDo todo = getData(position);
            Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    int cate = Integer.valueOf(todo.getCate());
                    ToDoCategory category = realm.where(ToDoCategory.class).equalTo("id",
                            cate).findFirst();
                    if (category != null) {
                        circleView.setColor(category.getIntColor());
                    }
                }
            });
            if (todo.getIsdone().equals("1")) {
                doneView.setVisibility(View.VISIBLE);
            } else {
                doneView.setVisibility(View.GONE);
            }
            contentView.setText(todo.getContent());
        }
    }
}
