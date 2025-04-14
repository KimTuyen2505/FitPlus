package com.example.firstproject;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.firstproject.dao.UserDAO;
import com.example.firstproject.models.User;
import com.google.android.material.textfield.TextInputEditText;
// Thay thế import CircleImageView
import com.google.android.material.imageview.ShapeableImageView;
// Thêm import cho việc xuất file
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import android.os.Environment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

// Thêm import mới
import java.io.ByteArrayOutputStream;
import android.util.Log;
import androidx.core.content.FileProvider;

public class PersonalInfoFragment extends Fragment {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_CALL_PERMISSION = 2;
    // Thêm hằng số cho request code
    private static final int REQUEST_STORAGE_PERMISSION = 3;

    private PersonalInfoListener listener;
    // Thêm các biến cho các trường nhập liệu mới
    private TextInputEditText editName, editBirthdate, editHeight, editWeight, editHeartRate, editDoctorName, editDoctorPhone, editMedications;
    private TextInputEditText editAddress, editEmergencyContact, editMedicalHistory, editAllergies, editInsurance;
    private RadioGroup radioGender;
    private RadioButton radioMale, radioFemale;
    private Spinner spinnerBloodType;
    // Thay đổi khai báo biến CircleImageView thành ShapeableImageView
    private ShapeableImageView profileImage;
    private ImageButton btnChangeAvatar, btnCallDoctor;
    private Button btnSaveInfo;
    // Thêm biến cho nút xuất thông tin
    private Button btnExportInfo;
    private UserDAO userDAO;
    private User currentUser;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Uri selectedImageUri;

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
        userDAO.open();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_info, container, false);

        // Initialize views
        editName = view.findViewById(R.id.edit_name);
        editBirthdate = view.findViewById(R.id.edit_birthdate);
        editHeight = view.findViewById(R.id.edit_height);
        editWeight = view.findViewById(R.id.edit_weight);
        editHeartRate = view.findViewById(R.id.edit_heart_rate);
        editDoctorName = view.findViewById(R.id.edit_doctor_name);
        editDoctorPhone = view.findViewById(R.id.edit_doctor_phone);
        editMedications = view.findViewById(R.id.edit_medications);
        // Ánh xạ các trường mới
        editAddress = view.findViewById(R.id.edit_address);
        editEmergencyContact = view.findViewById(R.id.edit_emergency_contact);
        editMedicalHistory = view.findViewById(R.id.edit_medical_history);
        editAllergies = view.findViewById(R.id.edit_allergies);
        editInsurance = view.findViewById(R.id.edit_insurance);

        radioGender = view.findViewById(R.id.radio_gender);
        radioMale = view.findViewById(R.id.radio_male);
        radioFemale = view.findViewById(R.id.radio_female);

        spinnerBloodType = view.findViewById(R.id.spinner_blood_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.blood_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodType.setAdapter(adapter);

        // Trong phương thức onCreateView, thay đổi ánh xạ view
        profileImage = view.findViewById(R.id.profile_image);
        btnExportInfo = view.findViewById(R.id.btn_export_info);
        btnChangeAvatar = view.findViewById(R.id.btn_change_avatar);
        btnCallDoctor = view.findViewById(R.id.btn_call_doctor);
        btnSaveInfo = view.findViewById(R.id.btn_save_info);

        // Set click listeners
        btnSaveInfo.setOnClickListener(v -> savePersonalInfo());

        btnChangeAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });

        btnCallDoctor.setOnClickListener(v -> {
            String phoneNumber = editDoctorPhone.getText().toString().trim();
            if (!phoneNumber.isEmpty()) {
                callDoctor(phoneNumber);
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập số điện thoại bác sĩ", Toast.LENGTH_SHORT).show();
            }
        });

        // Thêm sự kiện click cho nút xuất thông tin
        btnExportInfo.setOnClickListener(v -> exportPersonalInfo());

        // Set up date picker for birthdate
        editBirthdate.setOnClickListener(v -> showDatePickerDialog());

        // Load existing data
        loadPersonalInfo();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        userDAO.open();
    }

    @Override
    public void onPause() {
        super.onPause();
        userDAO.close();
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // If there's already a date, use it
        String currentDate = editBirthdate.getText().toString();
        if (!currentDate.isEmpty()) {
            try {
                Date date = dateFormat.parse(currentDate);
                calendar.setTime(date);
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    editBirthdate.setText(dateFormat.format(calendar.getTime()));
                },
                year, month, day
        );

        // Set max date to today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    // Cập nhật phương thức loadPersonalInfo để hiển thị dữ liệu từ các trường mới
    private void loadPersonalInfo() {
        currentUser = userDAO.getFirstUser();

        if (currentUser != null) {
            editName.setText(currentUser.getName());

            if (currentUser.getBirthdate() != null) {
                editBirthdate.setText(dateFormat.format(currentUser.getBirthdate()));
            }

            if ("Nam".equals(currentUser.getGender())) {
                radioMale.setChecked(true);
            } else if ("Nữ".equals(currentUser.getGender())) {
                radioFemale.setChecked(true);
            }

            if (currentUser.getHeight() > 0) {
                editHeight.setText(String.valueOf(currentUser.getHeight()));
            }

            if (currentUser.getWeight() > 0) {
                editWeight.setText(String.valueOf(currentUser.getWeight()));
            }

            if (currentUser.getHeartRate() > 0) {
                editHeartRate.setText(String.valueOf(currentUser.getHeartRate()));
            }

            // Set blood type in spinner
            String bloodType = currentUser.getBloodType();
            if (bloodType != null && !bloodType.isEmpty()) {
                ArrayAdapter adapter = (ArrayAdapter) spinnerBloodType.getAdapter();
                int position = adapter.getPosition(bloodType);
                if (position >= 0) {
                    spinnerBloodType.setSelection(position);
                }
            }

            editDoctorName.setText(currentUser.getDoctorName());
            editDoctorPhone.setText(currentUser.getDoctorPhone());
            editMedications.setText(currentUser.getMedications());

            // Hiển thị dữ liệu cho các trường mới
            editAddress.setText(currentUser.getAddress());
            editEmergencyContact.setText(currentUser.getEmergencyContact());
            editMedicalHistory.setText(currentUser.getMedicalHistory());
            editAllergies.setText(currentUser.getAllergies());
            editInsurance.setText(currentUser.getInsuranceNumber());

            // Load profile image if exists
            if (currentUser.getProfileImageUri() != null && !currentUser.getProfileImageUri().isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(currentUser.getProfileImageUri());

                    // Kiểm tra xem URI có phải là từ FileProvider không
                    if (imageUri.toString().contains(getContext().getPackageName() + ".fileprovider")) {
                        // Đây là URI từ FileProvider, có thể truy cập trực tiếp
                        InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        profileImage.setImageBitmap(selectedImage);
                        selectedImageUri = imageUri;
                    } else {
                        Log.e("PersonalInfoFragment", "Lỗi URI hình ảnh");
                        profileImage.setImageResource(R.drawable.default_avatar);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("PersonalInfoFragment", "Lỗi khi tải hình ảnh: " + e.getMessage());
                    // Đặt hình ảnh mặc định
                    profileImage.setImageResource(R.drawable.default_avatar);
                }
            }
        }
    }

    // Cập nhật phương thức savePersonalInfo để lưu dữ liệu từ các trường mới
    private void savePersonalInfo() {
        String name = editName.getText().toString().trim();
        String birthdateStr = editBirthdate.getText().toString().trim();
        String heightStr = editHeight.getText().toString().trim();
        String weightStr = editWeight.getText().toString().trim();
        String heartRateStr = editHeartRate.getText().toString().trim();
        String doctorName = editDoctorName.getText().toString().trim();
        String doctorPhone = editDoctorPhone.getText().toString().trim();
        String medications = editMedications.getText().toString().trim();

        // Lấy giá trị từ các trường mới
        String address = editAddress.getText().toString().trim();
        String emergencyContact = editEmergencyContact.getText().toString().trim();
        String medicalHistory = editMedicalHistory.getText().toString().trim();
        String allergies = editAllergies.getText().toString().trim();
        String insurance = editInsurance.getText().toString().trim();

        String gender = radioMale.isChecked() ? "Nam" : (radioFemale.isChecked() ? "Nữ" : "");

        String bloodType = "";
        if (spinnerBloodType.getSelectedItemPosition() > 0) {
            bloodType = spinnerBloodType.getSelectedItem().toString();
        }

        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create or update user
        if (currentUser == null) {
            currentUser = new User();
        }

        currentUser.setName(name);

        try {
            if (!birthdateStr.isEmpty()) {
                currentUser.setBirthdate(dateFormat.parse(birthdateStr));
            }
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Định dạng ngày sinh không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.setGender(gender);

        if (!heightStr.isEmpty()) {
            currentUser.setHeight(Float.parseFloat(heightStr));
        }

        if (!weightStr.isEmpty()) {
            currentUser.setWeight(Float.parseFloat(weightStr));
        }

        if (!heartRateStr.isEmpty()) {
            currentUser.setHeartRate(Integer.parseInt(heartRateStr));
        }

        currentUser.setBloodType(bloodType);
        currentUser.setDoctorName(doctorName);
        currentUser.setDoctorPhone(doctorPhone);
        currentUser.setMedications(medications);

        // Cập nhật các trường mới
        currentUser.setAddress(address);
        currentUser.setEmergencyContact(emergencyContact);
        currentUser.setMedicalHistory(medicalHistory);
        currentUser.setAllergies(allergies);
        currentUser.setInsuranceNumber(insurance);

        if (selectedImageUri != null) {
            currentUser.setProfileImageUri(selectedImageUri.toString());
        }

        // Save to database
        if (currentUser.getId() > 0) {
            userDAO.updateUser(currentUser);
        } else {
            long id = userDAO.insertUser(currentUser);
            currentUser.setId(id);
        }

        if (listener != null) {
            listener.onPersonalInfoSaved();
        }

        Toast.makeText(getContext(), "Đã lưu thông tin cá nhân", Toast.LENGTH_SHORT).show();
    }

    private void callDoctor(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            startCall(phoneNumber);
        }
    }

    private void startCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    // Thêm phương thức xuất thông tin cá nhân
    private void exportPersonalInfo() {
        // Kiểm tra quyền ghi vào bộ nhớ ngoài
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
            return;
        }

        // Tạo nội dung file
        StringBuilder content = new StringBuilder();
        content.append("THONG TIN Y TE CA NHAN\n\n");
        content.append("Ho va ten: ").append(currentUser.getName()).append("\n");

        if (currentUser.getBirthdate() != null) {
            content.append("Ngay sinh: ").append(dateFormat.format(currentUser.getBirthdate())).append("\n");
        }

        String gender = currentUser.getGender();
        if (gender.equals("Nữ")) {
            content.append("Gioi tinh: ").append("Nu").append("\n");
        } else {
            content.append("Gioi tinh: ").append("Nam").append("\n");
        }


        if (currentUser.getHeight() > 0) {
            content.append("Chieu cao: ").append(currentUser.getHeight()).append(" cm\n");
        }

        if (currentUser.getWeight() > 0) {
            content.append("Can nang: ").append(currentUser.getWeight()).append(" kg\n");
        }

        if (currentUser.getHeartRate() > 0) {
            content.append("Nhip tim: ").append(currentUser.getHeartRate()).append(" nhip/phut\n");
        }

        if (currentUser.getBloodType() != null && !currentUser.getBloodType().isEmpty()) {
            content.append("Nhom mau: ").append(currentUser.getBloodType()).append("\n");
        }

        if (currentUser.getAddress() != null && !currentUser.getAddress().isEmpty()) {
            content.append("Dia chi: ").append(currentUser.getAddress()).append("\n");
        }

        if (currentUser.getEmergencyContact() != null && !currentUser.getEmergencyContact().isEmpty()) {
            content.append("Lien he khan cap: ").append(currentUser.getEmergencyContact()).append("\n");
        }

        if (currentUser.getMedicalHistory() != null && !currentUser.getMedicalHistory().isEmpty()) {
            content.append("\nTien su benh an:\n").append(currentUser.getMedicalHistory()).append("\n");
        }

        if (currentUser.getAllergies() != null && !currentUser.getAllergies().isEmpty()) {
            content.append("\nDi ung:\n").append(currentUser.getAllergies()).append("\n");
        }

        if (currentUser.getInsuranceNumber() != null && !currentUser.getInsuranceNumber().isEmpty()) {
            content.append("\nSo the bao hiem y te: ").append(currentUser.getInsuranceNumber()).append("\n");
        }

        if (currentUser.getDoctorName() != null && !currentUser.getDoctorName().isEmpty()) {
            content.append("\nThong tin bac si:\n");
            content.append("Ten: ").append(currentUser.getDoctorName()).append("\n");
            if (currentUser.getDoctorPhone() != null && !currentUser.getDoctorPhone().isEmpty()) {
                content.append("So dien thoai: ").append(currentUser.getDoctorPhone()).append("\n");
            }
        }

        if (currentUser.getMedications() != null && !currentUser.getMedications().isEmpty()) {
            content.append("\nThuoc dang su dung:\n").append(currentUser.getMedications()).append("\n");
        }

        // Lưu file
        try {
            File folder = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), "HealthManagement");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            File file = new File(folder, "ThongTinYTe_" +
                    new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".txt");

            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");
            writer.write(content.toString());
            writer.close();
            fos.close();

            Toast.makeText(getContext(),
                    "Đã xuất thông tin vào " + file.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(),
                    "Lỗi khi xuất thông tin: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String phoneNumber = editDoctorPhone.getText().toString().trim();
                if (!phoneNumber.isEmpty()) {
                    startCall(phoneNumber);
                }
            } else {
                Toast.makeText(getContext(), "Quyền gọi điện bị từ chối", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportPersonalInfo();
            } else {
                Toast.makeText(getContext(), "Quyền ghi bộ nhớ bị từ chối", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // Thay đổi phương thức onActivityResult để sao chép hình ảnh vào thư mục ứng dụng
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            Uri sourceUri = data.getData();
            try {
                // Đọc bitmap từ URI nguồn
                InputStream imageStream = getContext().getContentResolver().openInputStream(sourceUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                // Hiển thị hình ảnh
                profileImage.setImageBitmap(selectedImage);

                // Lưu hình ảnh vào thư mục ứng dụng
                File profileImagesDir = new File(getContext().getFilesDir(), "profile_images");
                if (!profileImagesDir.exists()) {
                    profileImagesDir.mkdirs();
                }

                // Tạo tên file duy nhất
                String fileName = "profile_" + System.currentTimeMillis() + ".jpg";
                File destinationFile = new File(profileImagesDir, fileName);

                // Lưu bitmap vào file
                FileOutputStream fos = new FileOutputStream(destinationFile);
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();

                // Tạo URI từ file sử dụng FileProvider
                selectedImageUri = FileProvider.getUriForFile(
                        getContext(),
                        getContext().getPackageName() + ".fileprovider",
                        destinationFile
                );

                // Cấp quyền đọc cho URI
                getContext().grantUriPermission(
                        getContext().getPackageName(),
                        selectedImageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );

                Log.d("PersonalInfoFragment", "Image saved to: " + selectedImageUri.toString());
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Không thể lưu ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
