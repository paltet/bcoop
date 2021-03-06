package com.bcoop.bcoop.ui.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcoop.bcoop.Model.Xat;
import com.bcoop.bcoop.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyChatsAdapter extends BaseAdapter {
    private Context context;
    private List<String> myXats;
    private String otherUser;
    private List<String> lastIDs;
    private List<Boolean> selected;

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    public MyChatsAdapter(Context context, List<String> xats, List<String> lastIDs) {
        this.context = context;
        this.myXats = xats;
        this.lastIDs = lastIDs;
        selected = new ArrayList<>();
        for (int i = 0; i < this.myXats.size(); ++i)
            selected.add(i, false);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public int getCount() {
        return myXats.size();
    }

    @Override
    public Object getItem(int position) {
        return myXats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        otherUser = (String) getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.layout_current_chat, null);
        final TextView lastMessageTime = convertView.findViewById(R.id.lastMessageTimeText);
        final ImageView img = convertView.findViewById(R.id.userImage);
        img.setImageResource(R.drawable.profile);
        final TextView username = convertView.findViewById(R.id.usernameText);

        if (selected.get(position))
            convertView.setBackgroundColor(Color.LTGRAY);

        final DocumentReference documentReferenceUsuari = firestore.collection("Usuari").document(otherUser);
        documentReferenceUsuari.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String usrn = (String) documentSnapshot.get("nom");
                    username.setText(usrn);

                    String uriImage = (String) documentSnapshot.get("foto");
                    if (uriImage != null) {
                        StorageReference storageReference = storage.getReferenceFromUrl(uriImage);
                        try {
                            final File file = File.createTempFile("image", uriImage.substring(uriImage.lastIndexOf('.')));
                            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                    img.setImageBitmap(bitmap);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        final DocumentReference documentReference = firestore.collection("Xat").document(lastIDs.get(position));
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Xat xat = documentSnapshot.toObject(Xat.class);
                if (xat.getMissatges().size() == 0)
                    Log.d("Petaaaaaaaaaaaaaaaaaa ", lastIDs.get(position));
                else {
                    String lastMessage = xat.getMissatges().get(xat.getMissatges().size() - 1).getText();
                    if (lastMessage != null)
                        lastMessageTime.setText(lastMessage);
                    else lastMessageTime.setText(R.string.image);
                }
            }
        });

        return convertView;
    }

    private String changeTimeFormat(Date lastMessage) {
        return  lastMessage.toString().substring(11, 16);
    }

    public void update(List<String> delete) {
        for (String deleteXat : delete)
            myXats.remove(deleteXat);
        for (int i = 0; i < this.myXats.size(); ++i)
            selected.add(i, false);
        notifyDataSetChanged();
    }

    public void changeBackground(int position) {
        selected.set(position, !selected.get(position));
        notifyDataSetChanged();
    }

    public void clearSelection() {
        for (int i = 0; i < this.myXats.size(); ++i)
            selected.add(i, false);
        notifyDataSetChanged();
    }
}
