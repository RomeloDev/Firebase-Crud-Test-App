package com.example.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    private DatabaseReference usersRef;
    private ListView listView;
    private Button logoutBtn;
    private ArrayList<userModel> usersList;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        logoutBtn = findViewById(R.id.logoutBtn);
        listView = findViewById(R.id.listView);
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        usersList = new ArrayList<>();

        loadUsers();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            userModel selectedUser = usersList.get(position);
            showUpdateDeleteDialog(selectedUser);
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadUsers() {
        // Retrieve all users from Firebase Database
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                ArrayList<String> usersNameList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    userModel user = snapshot.getValue(userModel.class);
                    usersList.add(user);
                    usersNameList.add(user.getName());
                }
                arrayAdapter = new ArrayAdapter<>(Dashboard.this, android.R.layout.simple_list_item_1, usersNameList);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Dashboard.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUpdateDeleteDialog(userModel selectedUser) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_update_dialog, null);
        dialogBuilder.setView(dialogView);

        EditText updateName = dialogView.findViewById(R.id.updateName);
        Button updateBtn = dialogView.findViewById(R.id.updateBtn);
        Button deleteBtn = dialogView.findViewById(R.id.deleteBtn);

        updateName.setText(selectedUser.getName());

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        // Update action
        updateBtn.setOnClickListener(v -> {
            String newName = updateName.getText().toString();
            if (!TextUtils.isEmpty(newName)) {
                updateUser(selectedUser.getId(), newName);
                dialog.dismiss();
            } else {
                Toast.makeText(Dashboard.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete action
        deleteBtn.setOnClickListener(v -> {
            deleteUser(selectedUser.getId());
            dialog.dismiss();
        });
    }

    // Method to update the user's name
    private void updateUser(String userId, String newName) {
        usersRef.child(userId).child("name").setValue(newName)
                .addOnSuccessListener(aVoid -> Toast.makeText(Dashboard.this, "User updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(Dashboard.this, "Update failed", Toast.LENGTH_SHORT).show());
    }

    // Method to delete a user
    private void deleteUser(String userId) {
        usersRef.child(userId).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(Dashboard.this, "User deleted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(Dashboard.this, "Delete failed", Toast.LENGTH_SHORT).show());
    }
}