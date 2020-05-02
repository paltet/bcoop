package com.bcoop.bcoop.ui.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bcoop.bcoop.Model.Usuari;
import com.bcoop.bcoop.R;
import com.bcoop.bcoop.UserSearch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;
    private RecyclerView mResultList;
    private ResultListAdapter adapter;
    private Spinner searchSpinner;
    private String habilitat_seleccionada;
    private ArrayList<String> habilites = new ArrayList<>();
    private Usuari currentUser;
    ArrayList<UserSearch> users = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth  mAuth = FirebaseAuth.getInstance();




    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        //final TextView textView = root.findViewById(R.id.textView);
        //searchViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
           /* @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        setCurrentUSer();
        setHabilitats(root);
        Log.d("habilitatsFinal", String.valueOf(habilites));
        setSpinnerContent(root);

        mResultList = root.findViewById(R.id.ResultList);


        adapter = new ResultListAdapter(users);
        mResultList.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mResultList.setLayoutManager(layoutManager);

/*
        searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                habilitat_seleccionada = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        return root;
    }

    private void setSpinnerContent(View root) {

        searchSpinner = root.findViewById(R.id.spinner1);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, habilites);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchSpinner.setAdapter(myAdapter);

        AdapterView.OnItemSelectedListener mListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                habilitat_seleccionada = parent.getSelectedItem().toString();
                Toast.makeText(parent.getContext(), "Seleccionada:"+parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                searchUsers(parent.getItemAtPosition(position).toString());
            } //still never shows up in toast

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getActivity(), "none found = user_store_id.", Toast.LENGTH_SHORT).show(); //still nothing
            }
        };
        searchSpinner.setOnItemSelectedListener(mListener); // Register this spinner for a mListener



    }

    private void searchUsers(String hability) {
        Log.d("habilitatSeleccionada", hability);
        users = new ArrayList<>();
        adapter = new ResultListAdapter(users);
        mResultList.setAdapter(adapter);
        final ArrayList<Usuari> list = new ArrayList<>();

        String dbQueryHab = "habilitats." + hability;
        /*db.collection("Usuari")
                .orderBy(dbQueryHab)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Usuari user = document.toObject(Usuari.class);
                                list.add(user);
                                users.add(new UserSearch(user.getNom(), calculateDistance(user.getLocationLatitude(), user.getLocationLongitude()), getPhoto(user.getFoto()), user.getHabilitats(), user.getLocationLatitude(), user.getLocationLongitude(), user.getValoracio(), user.getEmail()));
                                Log.d("distance", String.valueOf(users));
                                adapter = new ResultListAdapter(users);
                                mResultList.setAdapter(adapter);

                            }



                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });*/



    }

    private String getPhoto(String foto) {



        return "";
    }

    private String calculateDistance(double locationLatitude, double locationLongitude) {

        double dist = 0.0;
        double myLat = currentUser.getLocationLatitude();
        double myLong = currentUser.getLocationLongitude();
        final double r2d = 180.0D / 3.141592653589793D;
        final double d2r = 3.141592653589793D / 180.0D;
        final double d2km = 111189.57696D * r2d;
        DecimalFormat formatter = new DecimalFormat("#0.00");
        final double x = locationLatitude * d2r;
        final double y = myLat * d2r;
        dist = (Math.acos( Math.sin(x) * Math.sin(y) + Math.cos(x) * Math.cos(y) * Math.cos(d2r * (locationLongitude - myLong))) * d2km)/1000;



        return new DecimalFormat("##.##").format(dist);
    }



    private void setHabilitats(final View root){

        final ArrayList<String> habilitats = new ArrayList<>();

        db.collection("Habilitat")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String hab = document.getId().toString();
                                habilitats.add(hab);
                                habilites = habilitats;
                            }
                            setSpinnerContent(root);

                        } else {

                        }
                    }
                });

        Log.d("habilitats", String.valueOf(habilites));


    }

    private void setCurrentUSer(){

        db.collection("Usuari").document(mAuth.getCurrentUser().getEmail())
            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(Usuari.class);
            }
        });

    }





}
