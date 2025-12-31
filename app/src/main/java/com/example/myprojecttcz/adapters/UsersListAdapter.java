package com.example.myprojecttcz.adapters;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UsersListAdapter extends ArrayAdapter<User> {

    public UsersListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public UsersListAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public UsersListAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<User> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public UsersListAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull User[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public UsersListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
    }

    public UsersListAdapter(@NonNull Context context, int resource, @NonNull User[] objects) {
        super(context, resource, objects);
    }
}
