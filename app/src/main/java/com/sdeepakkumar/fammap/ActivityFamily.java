package com.sdeepakkumar.fammap;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sdeepakkumar.fammap.databinding.ActivityFamilyBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityFamily extends AppCompatActivity {

    private ActivityFamilyBinding binding;
    private String selectedRelation;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private AdapterMember mAdapterMember;
    private List<Member> mListMember;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFamilyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();

        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);

        mAuth = FirebaseAuth.getInstance();

        mListMember = new ArrayList<>();
        mAdapterMember = new AdapterMember(ActivityFamily.this, mListMember);

        binding.familyMembersRecyleview.setVisibility(View.GONE);
        binding.notFoundLayout.setVisibility(View.GONE);
        binding.loadingLayoutProgressBar.setVisibility(View.VISIBLE);

        binding.backButton.setOnClickListener(view -> onBackPressed());

        db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Family")
                .orderBy("added_by", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Member member = new Member();
                                member.setUid(document.getData().get("uid").toString());
                                member.setName(document.getData().get("name").toString());
                                member.setEmail(document.getData().get("email").toString());
                                member.setProfileUrl(document.getData().get("profile_pic_url").toString());
                                member.setRelation(document.getData().get("relation").toString());
                                member.setAddedBy(document.getData().get("added_by").toString());
                                member.setPendingConfirmation((boolean) document.getData().get("pending_confirmation"));

                                mListMember.add(member);
                            }

                            if (mListMember.size() > 0){
                                binding.familyMembersRecyleview.setVisibility(View.VISIBLE);
                                binding.notFoundLayout.setVisibility(View.GONE);
                                binding.loadingLayoutProgressBar.setVisibility(View.GONE);
                            }else {
                                binding.familyMembersRecyleview.setVisibility(View.GONE);
                                binding.notFoundLayout.setVisibility(View.VISIBLE);
                                binding.loadingLayoutProgressBar.setVisibility(View.GONE);
                            }

                            binding.familyMembersRecyleview.setHasFixedSize(true);
                            binding.familyMembersRecyleview.setItemAnimator(new DefaultItemAnimator());
                            binding.familyMembersRecyleview.setLayoutManager(new LinearLayoutManager(ActivityFamily.this, LinearLayoutManager.VERTICAL, false));
                            binding.familyMembersRecyleview.setAdapter(mAdapterMember);
                            binding.familyMembersRecyleview.setNestedScrollingEnabled(false);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("On Failure:", e.toString());
                    }
                });

        binding.addButton.setOnClickListener(view -> {
            openAddMemberDialog();
        });
    }

    private void openAddMemberDialog(){
        Dialog dDialog = new Dialog(ActivityFamily.this, R.style.FullScreenDialogStyle);
        dDialog.setContentView(R.layout.dialog_add_family);

        TextInputEditText mEmailEditText = dDialog.findViewById(R.id.emailEditText);
        LinearLayout mAddMemberButton = dDialog.findViewById(R.id.addMemberButton);
        TextView mAddMemberText = dDialog.findViewById(R.id.addMemberText);
        ProgressBar mProgressBar = dDialog.findViewById(R.id.progressBar);

        Chip chipFather = dDialog.findViewById(R.id.chipFather);
        Chip chipMother = dDialog.findViewById(R.id.chipMother);
        Chip chipBrother = dDialog.findViewById(R.id.chipBrother);
        Chip chipSister = dDialog.findViewById(R.id.chipSister);
        Chip chipWife = dDialog.findViewById(R.id.chipWife);
        Chip chipHusband = dDialog.findViewById(R.id.chipHusband);
        Chip chipFriend = dDialog.findViewById(R.id.chipFriend);
        Chip chipColleague = dDialog.findViewById(R.id.chipColleage);
        Chip chipOthers = dDialog.findViewById(R.id.chipOthers);

        chipFather.setOnClickListener(view -> {
            getSelectedRelation(chipFather);
        });

        chipMother.setOnClickListener(view -> {
            getSelectedRelation(chipMother);
        });

        chipBrother.setOnClickListener(view -> {
            getSelectedRelation(chipBrother);
        });

        chipSister.setOnClickListener(view -> {
            getSelectedRelation(chipSister);
        });

        chipWife.setOnClickListener(view -> {
            getSelectedRelation(chipWife);
        });

        chipHusband.setOnClickListener(view -> {
            getSelectedRelation(chipHusband);
        });

        chipFriend.setOnClickListener(view -> {
            getSelectedRelation(chipFriend);
        });

        chipColleague.setOnClickListener(view -> {
            getSelectedRelation(chipColleague);
        });

        chipOthers.setOnClickListener(view -> {
            getSelectedRelation(chipOthers);
        });

        dDialog.setCancelable(true);
        dDialog.setCanceledOnTouchOutside(true);

        dDialog.findViewById(R.id.cancelButton).setOnClickListener(view -> {
            dDialog.cancel();
        });

        mAddMemberButton.setOnClickListener(view -> {
            if (mEmailEditText.getText().toString().equals("") || mEmailEditText.getText().toString().equals(mAuth.getCurrentUser().getEmail())){
                Toast.makeText(this, "Please fill family member's email!", Toast.LENGTH_SHORT).show();
            }else if (Objects.equals(selectedRelation, "")){
                Toast.makeText(this, "Please select one relation!", Toast.LENGTH_SHORT).show();
            }else {
                mAddMemberButton.setVisibility(View.VISIBLE);
                mAddMemberButton.setEnabled(false);
                mAddMemberText.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);

                db.collection("Users")
                        .whereEqualTo("email", mEmailEditText.getText().toString())
                        .orderBy("added_at", Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().size() > 0){
                                        List<DocumentSnapshot> data = task.getResult().getDocuments();
                                        String uid = data.get(0).get("uid").toString();

                                        DocumentReference docRef = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Family").document(uid);
                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        Toast.makeText(ActivityFamily.this, "This member already added!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        //Log.d(TAG, "No such document");
                                                        Map<String,Object> userMap = new HashMap<>();
                                                        userMap.put("uid", uid);
                                                        userMap.put("name", data.get(0).get("name").toString());
                                                        userMap.put("email", data.get(0).get("email").toString());
                                                        userMap.put("profile_pic_url", data.get(0).get("profile_pic_url").toString());
                                                        userMap.put("added_at", FieldValue.serverTimestamp());
                                                        userMap.put("updated_at", "");
                                                        userMap.put("relation", selectedRelation);
                                                        userMap.put("added_by", mAuth.getCurrentUser().getUid());
                                                        userMap.put("pending_confirmation", true);

                                                        db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Family").document(uid).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @SuppressLint("SetTextI18n")
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                //Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                                                Map<String,Object> userMap = new HashMap<>();
                                                                userMap.put("uid", mAuth.getCurrentUser().getUid());
                                                                userMap.put("name", mAuth.getCurrentUser().getDisplayName());
                                                                userMap.put("email", mAuth.getCurrentUser().getEmail());
                                                                userMap.put("profile_pic_url", mAuth.getCurrentUser().getPhotoUrl().toString());
                                                                userMap.put("added_at", FieldValue.serverTimestamp());
                                                                userMap.put("updated_at", "");
                                                                userMap.put("relation", "");
                                                                userMap.put("added_by", mAuth.getCurrentUser().getUid());
                                                                userMap.put("pending_confirmation", true);

                                                                db.collection("Users").document(uid).collection("Family").document(mAuth.getCurrentUser().getUid()).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @SuppressLint("SetTextI18n")
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        //Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                                                        mAddMemberButton.setVisibility(View.VISIBLE);
                                                                        mAddMemberButton.setEnabled(true);
                                                                        mAddMemberText.setVisibility(View.VISIBLE);
                                                                        mProgressBar.setVisibility(View.GONE);

                                                                        dDialog.cancel();

                                                                        Toast.makeText(ActivityFamily.this, "Member added successfully!!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        //Log.d(TAG, "onFailure: " + e.toString());
                                                                        mAddMemberButton.setVisibility(View.VISIBLE);
                                                                        mAddMemberButton.setEnabled(true);
                                                                        mAddMemberText.setVisibility(View.VISIBLE);
                                                                        mProgressBar.setVisibility(View.GONE);
                                                                    }
                                                                });
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                //Log.d(TAG, "onFailure: " + e.toString());
                                                                mAddMemberButton.setVisibility(View.VISIBLE);
                                                                mAddMemberButton.setEnabled(true);
                                                                mAddMemberText.setVisibility(View.VISIBLE);
                                                                mProgressBar.setVisibility(View.GONE);
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    //Log.d(TAG, "get failed with ", task.getException());
                                                    Toast.makeText(ActivityFamily.this, "Failed", Toast.LENGTH_SHORT).show();
                                                    mAddMemberButton.setVisibility(View.VISIBLE);
                                                    mAddMemberButton.setEnabled(true);
                                                    mAddMemberText.setVisibility(View.VISIBLE);
                                                    mProgressBar.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    }else {
                                        Toast.makeText(ActivityFamily.this, "User not found!", Toast.LENGTH_SHORT).show();
                                        mAddMemberButton.setVisibility(View.VISIBLE);
                                        mAddMemberButton.setEnabled(true);
                                        mAddMemberText.setVisibility(View.VISIBLE);
                                        mProgressBar.setVisibility(View.GONE);
                                    }
                                }else {
                                    Toast.makeText(ActivityFamily.this, "Invalid email, please fill correct email!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("On Failure:", e.toString());
                                Toast.makeText(ActivityFamily.this, "failed!", Toast.LENGTH_SHORT).show();
                                mAddMemberButton.setVisibility(View.VISIBLE);
                                mAddMemberButton.setEnabled(true);
                                mAddMemberText.setVisibility(View.VISIBLE);
                                mProgressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });

        /*mGoogleContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleContainer.setVisibility(View.GONE);
                mProgressBarGoogle.setVisibility(View.VISIBLE);

                if (!Constants.sharedPreferences.getBoolean(Constants.islogin, false)) {
                    signInGoogle();
                }
            }
        });*/

        dDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dDialog.show();
    }

    private void getSelectedRelation(Chip chip){
        if (chip.isChecked()){
            selectedRelation = chip.getText().toString();
        }
    }
}
