package com.bcoop.bcoop.ui.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcoop.bcoop.Model.Missatge;
import com.bcoop.bcoop.Model.Xat;
import com.bcoop.bcoop.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

class ChatAdapter extends BaseAdapter {

    private List<Missatge> missatges;
    private Context context;
    private String currentUser;

    private FirebaseStorage storage;


    public ChatAdapter() {}

    public ChatAdapter(Context context, Xat xat, List<Missatge> previousMessages) {
        this.context = context;
        this.missatges = previousMessages;
        this.missatges.addAll(xat.getMissatges());

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public int getCount() {
        int n = missatges.size();
        return n;
    }

    @Override
    public Object getItem(int position) {
        return missatges.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Missatge missatge = (Missatge) getItem(position);
        if (missatge.getRemitent().equals(currentUser)) {
            if (missatge.getText() != null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.layout_message_mine, null);
                TextView message = convertView.findViewById(R.id.message_body);
                message.setText(missatge.getText());
            }
            else {
                convertView = LayoutInflater.from(context).inflate(R.layout.layout_message_mine_image, null);
                getImageFromStorage(missatge.getFitxer(), convertView);
            }
        }
        else {
            if (missatge.getText() != null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.layout_message_other, null);
                TextView message = convertView.findViewById(R.id.message_body);
                message.setText(missatge.getText());
            }
            else {
                convertView = LayoutInflater.from(context).inflate(R.layout.layout_message_other_image, null);
                getImageFromStorage(missatge.getFitxer(), convertView);
            }
        }

        return convertView;
    }

    public void addedMessages(List<Missatge> missatgeList) {
        missatges = missatgeList;
        notifyDataSetChanged();
    }

    private void getImageFromStorage(String uriImage, View convertView) {
        final ImageView img = convertView.findViewById(R.id.message_body);
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
        else img.setImageResource(R.drawable.profile);
    }
}