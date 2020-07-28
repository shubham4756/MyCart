package com.example.mymall.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymall.Model.NotificationModel;
import com.example.mymall.NotificationActivity;
import com.example.mymall.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    private List<NotificationModel> notificationModelList;

    public NotificationAdapter(List<NotificationModel> notificationModelList) {
        this.notificationModelList = notificationModelList;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        String image=notificationModelList.get(position).getImage();
        String body=notificationModelList.get(position).getBody();
        boolean readed=notificationModelList.get(position).isReaded();

        holder.setData(image,body,readed);
    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.notification_image_view);
            textView=itemView.findViewById(R.id.notification_text_view);

        }

        private void setData(String image,String body,boolean readed) {
            Glide.with(itemView.getContext()).load(image).into(imageView);
            if (readed) {
                textView.setAlpha(0.5f);
            } else {
                textView.setAlpha(1f);
            }
            textView.setText(body);
        }
    }
}
