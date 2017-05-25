package com.example.shara.assignment5;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shara.assignment5.models.User;

import java.util.List;

/**
 * Created by shara on 3/17/2017.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyChatViewHolder> {

    private List<User> usersList;

    public class MyChatViewHolder extends RecyclerView.ViewHolder {
        public TextView nickname, email;

        public MyChatViewHolder(View view) {
            super(view);
            //uid = (TextView) view.findViewById(R.id.uid);
            nickname = (TextView) view.findViewById(R.id.nickname);
            email = (TextView) view.findViewById(R.id.email);
        }
    }


    public UserListAdapter(List<User> usersList) {
        this.usersList = usersList;
    }

    @Override
    public MyChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_row, parent, false);

        return new MyChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyChatViewHolder holder, int position) {
        User user = usersList.get(position);
        //holder.uid.setText(user.getUid());
        holder.nickname.setText(user.getNickname());
        holder.email.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public User getUser(int position){
        return usersList.get(position);
    }

}
