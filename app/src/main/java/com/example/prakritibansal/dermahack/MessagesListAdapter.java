package com.example.prakritibansal.dermahack;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.prakritibansal.dermahack.Conversation;
import com.example.prakritibansal.dermahack.TextMessage;
import com.squareup.picasso.Picasso;


/**
 * Created by prakritibansal on 12/23/17.
 */

public class MessagesListAdapter extends BaseAdapter {
    private Context context;
    private int count;
    private static LayoutInflater layoutInflater;


    public MessagesListAdapter(Context context) {
        this.context = context;
        count = Conversation.getCount();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        return Conversation.getMessage(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if(Conversation.inCardSet(position)){
            return 0;
        }else if(Conversation.inSentSet(position)){
            return 1;
        }else{
            return 2;
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int theType = getItemViewType(position);
        Holder holder = null;
        if (convertView == null) {

            holder = new Holder();

            if (theType == 0) {
                convertView = layoutInflater.inflate(R.layout.message_card, null);
                holder.imageUrl = convertView.findViewById(R.id.coverImageView);
            } else if (theType == 1) {
                convertView = layoutInflater.inflate(R.layout.message_sent, null);
                holder.data = (TextView) convertView.findViewById(R.id.editTextUserDetailInput_tx);
            }else if(theType == 2){
                convertView = layoutInflater.inflate(R.layout.message_response, null);
                holder.data = (TextView) convertView.findViewById(R.id.editTextUserDetailInput_rx);

            }
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        TextMessage item = Conversation.getMessage(position);
        if(item!= null){
            if(theType == 0){
                Picasso.with(context).load(item.getMessage()).fit().centerCrop().into(holder.imageUrl);

            }else if(theType == 1){
                holder.data.setText(item.getMessage());
            }else if(theType == 2){
                holder.data.setText(item.getMessage());
            }
        }
        return convertView;
    }

    // Helper class to recycle View's
    static class Holder {
        TextView data;
        TextView subTitle;
        ImageView imageUrl;
        ListView btnlist;
    }


    // Add new items
    public void refreshList(TextMessage message) {
        Conversation.add(message);
        notifyDataSetChanged();
    }


}

