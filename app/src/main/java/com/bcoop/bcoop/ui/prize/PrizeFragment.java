package com.bcoop.bcoop.ui.prize;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bcoop.bcoop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class PrizeFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Premi> premiList;
    ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_prize, container, false);

        premiList = new ArrayList<>();
        listView = (ListView) root.findViewById(R.id.listView);
        db.collection("Premi")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String nom = document.getString("nom");
                                String descripció = document.getString("descripció");
                                String imatge = document.getString("imatge");
                                Integer preu = document.getDouble("preu").intValue();
                                premiList.add(new Premi(nom, descripció, imatge, preu, null));
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                        PremiAdapter adapter = new PremiAdapter(getContext(), R.layout.my_list_item, premiList, false);
                        listView.setAdapter(adapter);
                    }

                });
        root.findViewById(R.id.VeurePremis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MyPremi.class);
                startActivity(intent);
            }
        });
        return root;
    }
}