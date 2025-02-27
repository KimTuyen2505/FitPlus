package com.example.firstproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

public class PersonalInfoFragment extends Fragment {

    private PersonalInfoListener listener;
    private TextInputEditText editName, editAge, editGender, editHeight, editWeight;
    private Button btnSaveInfo;

    public interface PersonalInfoListener {
        void onPersonalInfoSaved();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PersonalInfoListener) {
            listener = (PersonalInfoListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement PersonalInfoListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_info, container, false);

        // Initialize views
        editName = view.findViewById(R.id.edit_name);
        editAge = view.findViewById(R.id.edit_age);
        editGender = view.findViewById(R.id.edit_gender);
        editHeight = view.findViewById(R.id.edit_height);
        editWeight = view.findViewById(R.id.edit_weight);
        btnSaveInfo = view.findViewById(R.id.btn_save_info);

        // Set click listener for save button
        btnSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePersonalInfo();
            }
        });

        // Load existing data (in a real app, this would come from a database or shared preferences)
        loadPersonalInfo();

        return view;
    }

    private void loadPersonalInfo() {
        editName.setText("Nguyễn Văn A");
        editAge.setText("30");
        editGender.setText("Nam");
        editHeight.setText("175");
        editWeight.setText("70");
    }

    private void savePersonalInfo() {
        // In a real app, this would save data to a database or shared preferences
        // For now, we'll just notify the listener
        if (listener != null) {
            listener.onPersonalInfoSaved();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}

