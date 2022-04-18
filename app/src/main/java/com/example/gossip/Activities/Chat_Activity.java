package com.example.gossip.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.gossip.Adapter.MessagesAdapter;
import com.example.gossip.Adapter.UsersAdapter;
import com.example.gossip.Models.Message;
import com.example.gossip.R;
import com.example.gossip.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Chat_Activity extends AppCompatActivity {
    ActivityChatBinding binding;
    MessagesAdapter adapter;
    ArrayList<Message> messages;

    String senderRoom;
    String receiverRoom;
    FirebaseDatabase database;
    FirebaseStorage storage;

    ProgressDialog dialog;

    String senderUid;
    String receiverUid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);   //Bind Custom created toolbar from themes

        messages = new ArrayList<>();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Image...!!!");
        dialog.setCancelable(false);

//        Applying Adapter
        adapter = new MessagesAdapter(this,messages,senderRoom,receiverRoom);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        Applying Adapter

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();


//    For change background image using firebase remote config
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
       String backgroundImage = mFirebaseRemoteConfig.getString("Chat_Activity_Background");
        Glide.with(Chat_Activity.this)
                .load(backgroundImage)
                .into(binding.backGroundImage);
//    For change background image using firebase remote config


//          ActionBar Name and UID
        String name = getIntent().getStringExtra("name");
        String token = getIntent().getStringExtra("token");  // got token for sending notification
        String profile = getIntent().getStringExtra("image"); //Get profile image from database

        receiverUid = getIntent().getStringExtra("uid");
        senderUid = FirebaseAuth.getInstance().getUid();

//      Load profileImage
        binding.profileName.setText(name);
        Glide.with(Chat_Activity.this).load(profile)
                .placeholder(R.drawable.status)
                .into(binding.profileImage);
//      Load profileImage

//      ActionBar Back Button
        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
      });
//      ActionBar Back Button


        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setTitle(name);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //ActionBar BackButton Enable


//      Getting online and offline for showing
        database.getReference().child("presence").child(receiverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String status = snapshot.getValue(String.class);
                    if (!status.isEmpty()){
                        if(status.equals("Offline")){
                            binding.onlineStatus.setVisibility(View.GONE);
                        }else{
                        binding.onlineStatus.setText(status);
                        binding.onlineStatus.setVisibility(View.VISIBLE);
                    }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
//      Getting online and offline for showing
//          ActionBar Name and UID


//          Create uniqueRoom
        String senderUid = FirebaseAuth.getInstance().getUid();
        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;
//          Create uniqueRoom


//        Send Messages
        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String messageTxt = binding.msgBox.getText().toString();

                Date date = new Date();
                Message message = new Message(messageTxt ,senderUid,date.getTime());
                binding.msgBox.setText(""); //clear message box after msg send

               String randomKey = database.getReference().push().getKey(); //For genrate same key for emoji and delete msg                database.getReference().child("chats")

//            lastMsg and Time
                HashMap<String,Object> lastMsgObj = new HashMap<>();
                lastMsgObj.put("lastMsg", message.getMessage());
                lastMsgObj.put("lastMsgTime",date.getTime());
                database.getReference().child("chats")
                        .child(senderRoom)
                        .updateChildren(lastMsgObj);
                database.getReference().child("chats")
                        .child(receiverRoom)
                        .updateChildren(lastMsgObj);
//             lastMsg and Time


                database.getReference().child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .child(randomKey)//Generate same randomKey
//                            .push() //do not generate same randomKey
                            .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("chats")
                                .child(receiverRoom)
                                .child("messages")
//                            .push() //do not generate same randomKey
                                .child(randomKey) //Generate same randomKey
                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {

                            @Override
                            public void onSuccess(Void unused) {
                                sendNotification(name ,message.getMessage(),token);
                            }
                        });


                    }
                });
            }
        });
//        Send Messages



//         For Media Attachment
        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,25);
            }
        });
//         For Media Attachment


//      for typing status
        final Handler handler = new Handler();
        binding.msgBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStoppedTyping,1000);
                database.getReference().child("presence").
                        child(senderUid)
                        .setValue("typing...");


            }
            Runnable userStoppedTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence")
                            .child(senderUid)
                            .setValue("Online");
                }
            };
        });

//      for typing status




//        Receive Messages
        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                    messages.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        Message message = snapshot1.getValue(Message.class);
                        messages.add(message);
//                        message.setMessage(snapshot1.getKey());
                    }
                    adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//        Receive Messages
    }



//   For Sending Msg notification from device A to device B
   void sendNotification(String name , String message , String token){
      try {
          RequestQueue queue = Volley.newRequestQueue(this);

          String url = "https://fcm.googleapis.com/fcm/send";
          JSONObject data = new JSONObject();
          data.put("title", name);
          data.put("body", message);
          JSONObject notificationData = new JSONObject();
          notificationData.put("notification",data);
          notificationData.put("to",token);
          JsonObjectRequest request = new JsonObjectRequest(url, notificationData, new Response.Listener<JSONObject>() {
              @Override
              public void onResponse(JSONObject response) {
//                  Toast.makeText(Chat_Activity.this, "Success", Toast.LENGTH_SHORT).show();
              }
          }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                  Toast.makeText(Chat_Activity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
              }
          }){
              @Override
              public Map<String, String> getHeaders() throws AuthFailureError {
                 HashMap<String ,String> map = new HashMap<>();
                 String key = "Key=AAAAd6w4Ovw:APA91bGP29SuX9RkB9DpKEIaAKrT8yV38Cep9c2IedPYBCkfE85yQJz76UWfDn-WoTPUak4-2Y5bwb-yktPaEEBlAD5ALn7M7AcQZPR2xNodUxuHivd1mc-Z7TmSOxqgM6FLxgtrYa7L";
                 map.put("Authorization",key);
                 map.put("Content-Type","application/json");
                  return map;
              }
          };
        queue.add(request);
      }catch (Exception ex){

      }

   }
//   For Sending Msg notification from device A to device B




    //      For Showing Offline while go to Home
    @Override
    protected void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Offline");
    }
    //      For Showing Offline while go to Home


//         For Media Attachment
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 25 ){
            if (data != null){
                if (data.getData() != null){
                    Uri selectedImage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference().child("chats")
                            .child(calendar.getTimeInMillis()+"");
                    dialog.show();
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                       dialog.dismiss();
                            if (task.isSuccessful()){
                           reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {
                                   String filePath = uri.toString();
                                   String messageTxt = binding.msgBox.getText().toString();

                                   Date date = new Date();
                                   Message message = new Message(messageTxt ,senderUid,date.getTime());
                                   message.setMessage("Photo");
                                   message.setImageUrl(filePath);
                                   binding.msgBox.setText(""); //clear message box after msg send
                                   String randomKey = database.getReference().push().getKey(); //For genrate same key for emoji and delete msg                database.getReference().child("chats")

//                      lastMsg and Time
                                   HashMap<String,Object> lastMsgObj = new HashMap<>();
                                   lastMsgObj.put("lastMsg", message.getMessage());
                                   lastMsgObj.put("lastMsgTime",date.getTime());
                                   database.getReference().child("chats")
                                           .child(senderRoom)
                                           .updateChildren(lastMsgObj);
                                   database.getReference().child("chats")
                                           .child(receiverRoom)
                                           .updateChildren(lastMsgObj);
//                      lastMsg and Time

                                   database.getReference().child("chats")
                                           .child(senderRoom)
                                           .child("messages")
                                           .child(randomKey)//Generate same randomKey
//                                          .push() //do not generate same randomKey
                                           .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void unused) {
                                           database.getReference().child("chats")
                                                   .child(receiverRoom)
                                                   .child("messages")
//                                              .push() //do not generate same randomKey
                                                   .child(randomKey) //Generate same randomKey
                                                   .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                               @Override
                                               public void onSuccess(Void unused) {

                                               }
                                           });


                                       }
                                   });
//                                   Toast.makeText(Chat_Activity.this, filePath, Toast.LENGTH_SHORT).show();

                               }
                           });
                       }
                        }
                    });
                }
            }
        }
    }
//         For Media Attachment


    @Override
    protected void onResume() {
        super.onResume();

        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
    }

//      ActionBar BackBtn method for go to MainActivity
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
//      ActionBar BackBtn method for go to MainActivity
}