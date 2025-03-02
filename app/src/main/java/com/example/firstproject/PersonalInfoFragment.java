package com.example.firstproject;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.firstproject.R;
import com.example.firstproject.dao.UserDAO;
import com.example.firstproject.models.User;
import com.google.android.material.textfield.TextInputEditText;

public class PersonalInfoFragment extends Fragment {

    private PersonalInfoListener listener;
    private TextInputEditText editName, editAge, editGender, editHeight, editWeight;
    private Button btnSaveInfo;
    private UserDAO userDAO;
    private User currentUser;

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
        userDAO = new UserDAO(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        userDAO.open();
        loadUserData();
    }

    @Override
    public void onPause() {
        super.onPause();
        userDAO.close();
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
        btnSaveInfo.setOnClickListener(v -> savePersonalInfo());

        return view;
    }

    private void loadUserData() {
        currentUser = userDAO.getCurrentUser();
        if (currentUser != null) {
            editName.setText(currentUser.getName());
            editAge.setText(String.valueOf(currentUser.getAge()));
            editGender.setText(currentUser.getGender());
            editHeight.setText(String.valueOf(currentUser.getHeight()));
            editWeight.setText(String.valueOf(currentUser.getWeight()));
        }
    }

    private void savePersonalInfo() {
        String name = editName.getText().toString().trim();
        String ageStr = editAge.getText().toString().trim();
        String gender = editGender.getText().toString().trim();
        String heightStr = editHeight.getText().toString().trim();
        String weightStr = editWeight.getText().toString().trim();

        if (name.isEmpty() || ageStr.isEmpty() || gender.isEmpty() ||
                heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            float height = Float.parseFloat(heightStr);
            float weight = Float.parseFloat(weightStr);

            if (currentUser == null) {
                currentUser = new User(name, age, gender, height, weight);
                long id = userDAO.insertUser(currentUser);
                currentUser.setId(id);
            } else {
                currentUser.setName(name);
                currentUser.setAge(age);
                currentUser.setGender(gender);
                currentUser.setHeight(height);
                currentUser.setWeight(weight);
                userDAO.updateUser(currentUser);
            }

            if (listener != null) {
                listener.onPersonalInfoSaved();
            }

            // Calculate and show BMI
            float bmi = currentUser.calculateBMI();
            String bmiStatus = currentUser.getBMIStatus();
            Toast.makeText(getContext(),
                    String.format("BMI: %.1f - %s", bmi, bmiStatus),
                    Toast.LENGTH_LONG).show();

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Vui lòng nhập đúng định dạng số", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}

