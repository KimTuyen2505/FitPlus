package com.example.firstproject.doctor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstproject.R;
import com.example.firstproject.doctor.adapters.DoctorAdapter;
import com.example.firstproject.doctor.models.Doctor;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class DoctorConsultationActivity extends AppCompatActivity implements DoctorAdapter.OnDoctorClickListener {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_MICROPHONE_PERMISSION = 101;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private RecyclerView recyclerDoctors;
    private DoctorAdapter doctorAdapter;
    private List<Doctor> doctorList;
    private TextView textNoDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_consultation);

        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tư vấn với bác sĩ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        tabLayout = findViewById(R.id.tab_layout);
        recyclerDoctors = findViewById(R.id.recycler_doctors);
        textNoDoctor = findViewById(R.id.text_no_doctor);

        // Set up RecyclerView
        recyclerDoctors.setLayoutManager(new LinearLayoutManager(this));

        // Initialize doctor list
        doctorList = new ArrayList<>();
        loadDoctors();

        // Set up adapter
        doctorAdapter = new DoctorAdapter(this, doctorList, this);
        recyclerDoctors.setAdapter(doctorAdapter);

        // Set up tabs
        setupTabs();
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Tất cả"));
        tabLayout.addTab(tabLayout.newTab().setText("Tim mạch"));
        tabLayout.addTab(tabLayout.newTab().setText("Nội tiết"));
        tabLayout.addTab(tabLayout.newTab().setText("Da liễu"));
        tabLayout.addTab(tabLayout.newTab().setText("Sản phụ khoa"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterDoctorsBySpecialty(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed
            }
        });
    }

    private void filterDoctorsBySpecialty(int position) {
        List<Doctor> filteredList = new ArrayList<>();

        if (position == 0) {
            // "Tất cả" tab
            filteredList.addAll(doctorList);
        } else {
            String specialty = tabLayout.getTabAt(position).getText().toString();
            for (Doctor doctor : doctorList) {
                if (doctor.getSpecialty().equals(specialty)) {
                    filteredList.add(doctor);
                }
            }
        }

        doctorAdapter.updateDoctors(filteredList);

        if (filteredList.isEmpty()) {
            textNoDoctor.setVisibility(View.VISIBLE);
            recyclerDoctors.setVisibility(View.GONE);
        } else {
            textNoDoctor.setVisibility(View.GONE);
            recyclerDoctors.setVisibility(View.VISIBLE);
        }
    }

    private void loadDoctors() {
        // Sample data - in a real app, this would come from a database or API
        doctorList.add(new Doctor(1, "BS. Nguyễn Văn An", "Tim mạch", "Bệnh viện Bạch Mai", 4.8f, 120, true));
        doctorList.add(new Doctor(2, "BS. Trần Thị Bình", "Sản phụ khoa", "Bệnh viện Phụ sản Trung ương", 4.9f, 98, false));
        doctorList.add(new Doctor(3, "BS. Lê Minh Cường", "Nội tiết", "Bệnh viện Nội tiết Trung ương", 4.7f, 85, true));
        doctorList.add(new Doctor(4, "BS. Phạm Thị Dung", "Da liễu", "Bệnh viện Da liễu Trung ương", 4.6f, 76, true));
        doctorList.add(new Doctor(5, "BS. Hoàng Văn Em", "Tim mạch", "Bệnh viện Việt Đức", 4.5f, 110, false));
        doctorList.add(new Doctor(6, "BS. Ngô Thị Phương", "Sản phụ khoa", "Bệnh viện Phụ sản Hà Nội", 4.9f, 130, true));
        doctorList.add(new Doctor(7, "BS. Vũ Minh Quân", "Nội tiết", "Bệnh viện Đại học Y Hà Nội", 4.8f, 95, true));
        doctorList.add(new Doctor(8, "BS. Đỗ Thị Hương", "Da liễu", "Bệnh viện Bạch Mai", 4.7f, 88, false));
    }

    @Override
    public void onDoctorClick(Doctor doctor) {
        // Check for camera and microphone permissions before starting video call
        if (checkAndRequestPermissions()) {
            startVideoCall(doctor);
        }
    }

    private boolean checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), REQUEST_CAMERA_PERMISSION);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            boolean allPermissionsGranted = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // Permissions granted, start video call
                Doctor selectedDoctor = doctorAdapter.getSelectedDoctor();
                if (selectedDoctor != null) {
                    startVideoCall(selectedDoctor);
                }
            } else {
                Toast.makeText(this, "Cần cấp quyền camera và microphone để thực hiện cuộc gọi video", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startVideoCall(Doctor doctor) {
        Intent intent = new Intent(this, VideoCallActivity.class);
        intent.putExtra("doctor_id", doctor.getId());
        intent.putExtra("doctor_name", doctor.getName());
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
