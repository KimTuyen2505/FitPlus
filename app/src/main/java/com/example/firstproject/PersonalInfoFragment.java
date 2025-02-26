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

import com.example.firstproject.database.UserDAO;
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_info, container, false);

        // Khởi tạo DAO
        userDAO = new UserDAO(getContext());
        userDAO.open();

        // Khởi tạo views
        editName = view.findViewById(R.id.edit_name);
        editAge = view.findViewById(R.id.edit_age);
        editGender = view.findViewById(R.id.edit_gender);
        editHeight = view.findViewById(R.id.edit_height);
        editWeight = view.findViewById(R.id.edit_weight);
        btnSaveInfo = view.findViewById(R.id.btn_save_info);

        // Đặt sự kiện click cho nút lưu
        btnSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePersonalInfo();
            }
        });

        // Tải thông tin người dùng hiện tại
        loadPersonalInfo();

        return view;
    }

    private void loadPersonalInfo() {
        // Lấy thông tin người dùng từ cơ sở dữ liệu
        currentUser = userDAO.getUser();

        if (currentUser != null) {
            // Hiển thị thông tin người dùng
            editName.setText(currentUser.getName());
            editAge.setText(String.valueOf(currentUser.getAge()));
            editGender.setText(currentUser.getGender());
            editHeight.setText(String.valueOf(currentUser.getHeight()));
            editWeight.setText(String.valueOf(currentUser.getWeight()));
        } else {
            // Nếu chưa có thông tin người dùng, tạo một người dùng mới với dữ liệu mẫu
            currentUser = new User("Nguyễn Văn A", 30, "Nam", 175, 70);

            // Hiển thị thông tin mẫu
            editName.setText(currentUser.getName());
            editAge.setText(String.valueOf(currentUser.getAge()));
            editGender.setText(currentUser.getGender());
            editHeight.setText(String.valueOf(currentUser.getHeight()));
            editWeight.setText(String.valueOf(currentUser.getWeight()));
        }
    }

    private void savePersonalInfo() {
        // Lấy dữ liệu từ các trường nhập liệu
        String name = editName.getText().toString().trim();
        String ageStr = editAge.getText().toString().trim();
        String gender = editGender.getText().toString().trim();
        String heightStr = editHeight.getText().toString().trim();
        String weightStr = editWeight.getText().toString().trim();

        // Kiểm tra dữ liệu
        if (name.isEmpty() || ageStr.isEmpty() || gender.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            float height = Float.parseFloat(heightStr);
            float weight = Float.parseFloat(weightStr);

            // Cập nhật đối tượng User
            currentUser.setName(name);
            currentUser.setAge(age);
            currentUser.setGender(gender);
            currentUser.setHeight(height);
            currentUser.setWeight(weight);

            // Lưu vào cơ sở dữ liệu
            long result;
            if (currentUser.getId() > 0) {
                // Cập nhật người dùng hiện tại
                result = userDAO.updateUser(currentUser);
            } else {
                // Thêm người dùng mới
                result = userDAO.insertUser(currentUser);
                if (result > 0) {
                    currentUser.setId(result);
                }
            }

            if (result > 0) {
                // Thông báo thành công
                Toast.makeText(getContext(), "Thông tin cá nhân đã được lưu thành công", Toast.LENGTH_SHORT).show();

                // Thông báo cho activity
                if (listener != null) {
                    listener.onPersonalInfoSaved();
                }
            } else {
                // Thông báo lỗi
                Toast.makeText(getContext(), "Lỗi khi lưu thông tin cá nhân", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Vui lòng nhập đúng định dạng số", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Đóng kết nối cơ sở dữ liệu
        if (userDAO != null) {
            userDAO.close();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}

