package com.example.gossip.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gossip.Models.Message;
import com.example.gossip.R;
import com.example.gossip.databinding.IteamReciverBinding;
import com.example.gossip.databinding.IteamSendBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {
    Context context ;
    ArrayList<Message> messages;
    final int ITEM_SENT = 1 ;
    final  int ITEM_RECEIVE = 2;

    String senderRoom;
    String receiverRoom;


    public  MessagesAdapter(Context context , ArrayList<Message> messages ,String senderRoom,String receiverRoom){
        this.context = context;
        this.messages = messages;
        this.senderRoom =senderRoom;
        this.receiverRoom = receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.iteam_send,parent,false);
            return new SentViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.iteam_reciver,parent,false);
            return new ReceiverViewHolder(view);
        }

    }

//if uid match then msg sent else receive
    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return  ITEM_SENT;
        }else{
            return ITEM_RECEIVE;
        }
    }
//if uid match then msg sent else receive

//set message data from viewHolder using typecast
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

//        Emoji Reaction
//        int reactions[] = new int[]{
//                R.drawable.laugh,
//                R.drawable.angry,
//                R.drawable.love,
//                R.drawable.laughing,
//        };
//        ReactionsConfig config = new ReactionsConfigBuilder(context)
//                .withReactions(reactions)
//                .build();
//        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
//            if(holder.getClass()==SentViewHolder.class){
//                SentViewHolder viewHolder = (SentViewHolder)holder;
//                viewHolder.binding.feeling.setImageResource(reactions[pos]);
//                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
//            }
//            else{
//                ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
//                viewHolder.binding.feeling.setImageResource(reactions[pos]);
//                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
//
//            }
//            return true; // true is closing popup, false is requesting a new selection
//        });

//      Emoji Reaction
        Message message = messages.get(position);

//      For Photo attachment

        if (holder.getClass()==SentViewHolder.class){
            SentViewHolder viewHolder = (SentViewHolder)holder;
            if (message.getMessage().equals("Photo")){
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.placeholder)
                        .into(viewHolder.binding.image);
            }
        }else{
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            if (message.getMessage().equals("Photo")){
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.placeholder)
                        .into(viewHolder.binding.image);
            }
        }
//      For Photo attachment

        if (holder.getClass()==SentViewHolder.class){
        SentViewHolder viewHolder = (SentViewHolder)holder;




        viewHolder.binding.message.setText(message.getMessage());

//       Emoji Reaction
//    viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
//    @Override
//    public boolean onTouch(View view, MotionEvent motionEvent) {
//        popup.onTouch(view,motionEvent);
//        return false;
//    }
//      });
//        Emoji Reaction

        }else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.binding.message.setText(message.getMessage());

 //              Emoji Reaction
//            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View view, MotionEvent motionEvent) {
//                    popup.onTouch(view,motionEvent);
//                    return false;
//                }
//            });
//        Emoji Reaction

        }


//set message data from viewHolder using typecast

}



    @Override
    public int getItemCount() {
        return messages.size();
    }

    public  class SentViewHolder extends RecyclerView.ViewHolder {

        IteamSendBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = IteamSendBinding.bind(itemView);

        }
    }

    public  class ReceiverViewHolder extends RecyclerView.ViewHolder {
        IteamReciverBinding binding;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = IteamReciverBinding.bind(itemView);
        }
    }
}
