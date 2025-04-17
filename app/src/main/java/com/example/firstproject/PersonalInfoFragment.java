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
// Sử dụng ShapeableImageView thay thế cho CircleImageView
import com.google.android.material.imageview.ShapeableImageView;
// Import các lớp xử lý file
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

// Import thêm các lớp xử lý bitmap, ghi log, FileProvider
import java.io.ByteArrayOutputStream;
import android.util.Log;
import androidx.core.content.FileProvider;

/**
 * Fragment này thực hiện hiển thị và xử lý thông tin cá nhân của người dùng.
 * Các chức năng bao gồm: hiển thị thông tin, thay đổi ảnh đại diện, gọi điện cho bác sĩ, lưu và xuất thông tin.
 */
public class PersonalInfoFragment extends Fragment {

    // Request code cho việc chọn ảnh, quyền gọi điện và quyền truy cập bộ nhớ ngoài
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_CALL_PERMISSION = 2;
    private static final int REQUEST_STORAGE_PERMISSION = 3;

    // Interface callback khi lưu thông tin thành công
    public interface PersonalInfoListener {
        void onPersonalInfoSaved();
    }
//chinh lai html cho xuat file
    private PersonalInfoListener listener; // Callback listener cho Fragment
    // Khai báo các trường nhập liệu dùng để hiển thị thông tin cá nhân
    private TextInputEditText editName, editBirthdate, editHeight, editWeight, editHeartRate, editDoctorName, editDoctorPhone, editMedications;
    // Các trường nhập liệu mới được thêm vào
    private TextInputEditText editAddress, editEmergencyContact, editMedicalHistory, editAllergies, editInsurance;
    // Nhóm radio cho giới tính
    private RadioGroup radioGender;
    private RadioButton radioMale, radioFemale;
    // Spinner hiển thị nhóm máu
    private Spinner spinnerBloodType;
    // Hiển thị ảnh đại diện (sử dụng ShapeableImageView thay vì CircleImageView)
    private ShapeableImageView profileImage;
    // Các nút chức năng: thay đổi ảnh, gọi điện, lưu thông tin, xuất thông tin
    private ImageButton btnChangeAvatar, btnCallDoctor;
    private Button btnSaveInfo;
    private Button btnExportInfo;

    // DAO và đối tượng User hiện tại
    private UserDAO userDAO;
    private User currentUser;
    // Định dạng ngày được sử dụng cho trường ngày sinh
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    // URI chứa hình ảnh được chọn, lưu khi thay đổi ảnh đại diện
    private Uri selectedImageUri;

    /**
     * Phương thức onAttach được gọi khi Fragment được gắn với Activity.
     * Ở đây, kiểm tra Activity có implement interface PersonalInfoListener hay không,
     * sau đó khởi tạo kết nối đến database thông qua UserDAO.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PersonalInfoListener) {
            listener = (PersonalInfoListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement PersonalInfoListener");
        }

        // Khởi tạo và mở kết nối DAO
        userDAO = new UserDAO(context);
        userDAO.open();
    }

    /**
     * Phương thức onCreateView thực hiện việc inflate layout, ánh xạ các view, thiết lập spinner và các sự kiện click.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout fragment_personal_info.xml
        View view = inflater.inflate(R.layout.fragment_personal_info, container, false);

        // Ánh xạ các trường nhập liệu cơ bản
        editName = view.findViewById(R.id.edit_name);
        editBirthdate = view.findViewById(R.id.edit_birthdate);
        editHeight = view.findViewById(R.id.edit_height);
        editWeight = view.findViewById(R.id.edit_weight);
        editHeartRate = view.findViewById(R.id.edit_heart_rate);
        editDoctorName = view.findViewById(R.id.edit_doctor_name);
        editDoctorPhone = view.findViewById(R.id.edit_doctor_phone);
        editMedications = view.findViewById(R.id.edit_medications);
        // Ánh xạ các trường nhập liệu mới
        editAddress = view.findViewById(R.id.edit_address);
        editEmergencyContact = view.findViewById(R.id.edit_emergency_contact);
        editMedicalHistory = view.findViewById(R.id.edit_medical_history);
        editAllergies = view.findViewById(R.id.edit_allergies);
        editInsurance = view.findViewById(R.id.edit_insurance);

        // Ánh xạ group và radio button cho giới tính
        radioGender = view.findViewById(R.id.radio_gender);
        radioMale = view.findViewById(R.id.radio_male);
        radioFemale = view.findViewById(R.id.radio_female);

        // Ánh xạ Spinner và thiết lập adapter để hiển thị các loại nhóm máu
        spinnerBloodType = view.findViewById(R.id.spinner_blood_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.blood_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodType.setAdapter(adapter);

        // Ánh xạ các view liên quan đến ảnh đại diện và các nút chức năng
        profileImage = view.findViewById(R.id.profile_image);
        btnExportInfo = view.findViewById(R.id.btn_export_info);
        btnChangeAvatar = view.findViewById(R.id.btn_change_avatar);
        btnCallDoctor = view.findViewById(R.id.btn_call_doctor);
        btnSaveInfo = view.findViewById(R.id.btn_save_info);

        // Thiết lập sự kiện cho nút lưu thông tin
        btnSaveInfo.setOnClickListener(v -> savePersonalInfo());

        // Sự kiện thay đổi ảnh đại diện: mở Gallery để chọn ảnh
        btnChangeAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });

        // Sự kiện gọi điện cho bác sĩ: lấy số điện thoại và gọi điện nếu đã nhập
        btnCallDoctor.setOnClickListener(v -> {
            String phoneNumber = editDoctorPhone.getText().toString().trim();
            if (!phoneNumber.isEmpty()) {
                callDoctor(phoneNumber);
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập số điện thoại bác sĩ", Toast.LENGTH_SHORT).show();
            }
        });

        // Sự kiện xuất thông tin cá nhân ra file txt
        btnExportInfo.setOnClickListener(v -> exportPersonalInfo());

        // Thiết lập sự kiện hiển thị DatePickerDialog khi click vào trường ngày sinh
        editBirthdate.setOnClickListener(v -> showDatePickerDialog());

        // Tải dữ liệu thông tin cá nhân (nếu đã lưu trước đó)
        loadPersonalInfo();

        return view;
    }

    /**
     * Mở kết nối UserDAO khi Fragment resume
     */
    @Override
    public void onResume() {
        super.onResume();
        userDAO.open();
    }

    /**
     * Đóng kết nối UserDAO khi Fragment pause
     */
    @Override
    public void onPause() {
        super.onPause();
        userDAO.close();
    }

    /**
     * Hiển thị DatePickerDialog để chọn ngày sinh, với ngày tối đa là ngày hiện tại.
     * Nếu đã có ngày được nhập, DatePicker sẽ khởi tạo với ngày đó.
     */
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Nếu đã có ngày nhập, sử dụng ngày đó làm giá trị khởi tạo
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
                    // Định dạng và hiển thị ngày sinh được chọn vào EditText
                    editBirthdate.setText(dateFormat.format(calendar.getTime()));
                },
                year, month, day
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
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
            editAddress.setText(currentUser.getAddress());
            editEmergencyContact.setText(currentUser.getEmergencyContact());
            editMedicalHistory.setText(currentUser.getMedicalHistory());
            editAllergies.setText(currentUser.getAllergies());
            editInsurance.setText(currentUser.getInsuranceNumber());
            if (currentUser.getProfileImageUri() != null && !currentUser.getProfileImageUri().isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(currentUser.getProfileImageUri());
                    if (imageUri.toString().contains(getContext().getPackageName() + ".fileprovider")) {
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

                    profileImage.setImageResource(R.drawable.default_avatar);
                }
            }
        }
    }
    private void savePersonalInfo() {
        String name = editName.getText().toString().trim();
        String birthdateStr = editBirthdate.getText().toString().trim();
        String heightStr = editHeight.getText().toString().trim();
        String weightStr = editWeight.getText().toString().trim();
        String heartRateStr = editHeartRate.getText().toString().trim();
        String doctorName = editDoctorName.getText().toString().trim();
        String doctorPhone = editDoctorPhone.getText().toString().trim();
        String medications = editMedications.getText().toString().trim();

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

        currentUser.setAddress(address);
        currentUser.setEmergencyContact(emergencyContact);
        currentUser.setMedicalHistory(medicalHistory);
        currentUser.setAllergies(allergies);
        currentUser.setInsuranceNumber(insurance);

        if (selectedImageUri != null) {
            currentUser.setProfileImageUri(selectedImageUri.toString());
        }

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

    private void exportPersonalInfo() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
            return;
        }

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
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            Uri sourceUri = data.getData();
            try {
                InputStream imageStream = getContext().getContentResolver().openInputStream(sourceUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                profileImage.setImageBitmap(selectedImage);

                File profileImagesDir = new File(getContext().getFilesDir(), "profile_images");
                if (!profileImagesDir.exists()) {
                    profileImagesDir.mkdirs();
                }
                String fileName = "profile_" + System.currentTimeMillis() + ".jpg";
                File destinationFile = new File(profileImagesDir, fileName);

                FileOutputStream fos = new FileOutputStream(destinationFile);
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();

                selectedImageUri = FileProvider.getUriForFile(
                        getContext(),
                        getContext().getPackageName() + ".fileprovider",
                        destinationFile
                );

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
