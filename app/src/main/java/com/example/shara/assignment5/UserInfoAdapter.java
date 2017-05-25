package com.example.shara.assignment5;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by shara on 3/17/2017.
 */

public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.MyViewHolder> {

    private List<DisplayInfo> userList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nickname, year, city, state;

        public MyViewHolder(View view) {
            super(view);
            nickname = (TextView) view.findViewById(R.id.nickname);
            state = (TextView) view.findViewById(R.id.state);
            city = (TextView) view.findViewById(R.id.city);
            year = (TextView) view.findViewById(R.id.year);
        }
    }


    public UserInfoAdapter(List<DisplayInfo> userList) {
        this.userList = userList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.info_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DisplayInfo user = userList.get(position);
        holder.nickname.setText(user.getName());
        holder.state.setText(user.getState());
        holder.city.setText(user.getCity());
        holder.year.setText(user.getYear());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}
