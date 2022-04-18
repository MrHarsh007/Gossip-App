package com.example.gossip.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gossip.Models.Message;
import com.example.gossip.Models.User;
import com.example.gossip.R;
import com.example.gossip.databinding.IteamRecieveGroupBinding;
import com.example.gossip.databinding.IteamReciverBinding;
import com.example.gossip.databinding.IteamSendBinding;
import com.example.gossip.databinding.IteamSendGroupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupMessagesAdapter extends RecyclerView.Adapter {
    Context context ;
    ArrayList<Message> messages;
    final int ITEM_SENT = 1 ;
    final  int ITEM_RECEIVE = 2;




    public GroupMessagesAdapter(Context context , ArrayList<Message> messages ){
        this.context = context;
        this.messages = messages;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.iteam_send_group,parent,false);
            return new SentViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.iteam_recieve_group,parent,false);
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


//            Showing Name Who is sending MSG
            FirebaseDatabase.getInstance().getReference().child("users")
                    .child(message.getSenderId())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                User user = snapshot.getValue(User.class);
                                 viewHolder.binding.name.setText(user.getName());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
//            Showing Name Who is sending MSG


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



//            Showing Name Who is sending MSG
            FirebaseDatabase.getInstance().getReference().child("users")
                    .child(message.getSenderId())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                User user = snapshot.getValue(User.class);
                                viewHolder.binding.name.setText("@ "+ user.getName());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
//            Showing Name Who is sending MSG
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

        IteamSendGroupBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = IteamSendGroupBinding.bind(itemView);

        }
    }

    public  class ReceiverViewHolder extends RecyclerView.ViewHolder {
        IteamRecieveGroupBinding binding;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = IteamRecieveGroupBinding.bind(itemView);
        }
    }
}
