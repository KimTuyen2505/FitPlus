package com.example.firstproject.doctor;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firstproject.R;

public class VideoCallActivity extends AppCompatActivity {

    private TextView textDoctorName;
    private TextView textCallStatus;
    private TextView textCallDuration;
    private ImageButton btnToggleCamera;
    private ImageButton btnToggleMic;
    private ImageButton btnEndCall;
    private View layoutControls;
    private View layoutConnecting;
    private View layoutVideoContainer;

    private boolean isMicMuted = false;
    private boolean isCameraOff = false;
    private boolean isCallConnected = false;
    private CountDownTimer callTimer;
    private long callDurationSeconds = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        // Get doctor info from intent
        String doctorName = getIntent().getStringExtra("doctor_name");

        // Initialize views
        textDoctorName = findViewById(R.id.text_doctor_name);
        textCallStatus = findViewById(R.id.text_call_status);
        textCallDuration = findViewById(R.id.text_call_duration);
        btnToggleCamera = findViewById(R.id.btn_toggle_camera);
        btnToggleMic = findViewById(R.id.btn_toggle_mic);
        btnEndCall = findViewById(R.id.btn_end_call);
        layoutControls = findViewById(R.id.layout_call_controls);
        layoutConnecting = findViewById(R.id.layout_connecting);
        layoutVideoContainer = findViewById(R.id.layout_video_container);

        // Set doctor name
        textDoctorName.setText(doctorName);

        // Set up click listeners
        btnToggleCamera.setOnClickListener(v -> toggleCamera());
        btnToggleMic.setOnClickListener(v -> toggleMicrophone());
        btnEndCall.setOnClickListener(v -> endCall());

        // Start connecting to call
        connectToCall();
    }

    private void connectToCall() {
        // Show connecting layout
        layoutConnecting.setVisibility(View.VISIBLE);
        layoutVideoContainer.setVisibility(View.GONE);
        layoutControls.setVisibility(View.GONE);

        // Simulate connecting delay
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textCallStatus.setText("Đang kết nối...");
            }

            @Override
            public void onFinish() {
                // Call connected
                isCallConnected = true;
                layoutConnecting.setVisibility(View.GONE);
                layoutVideoContainer.setVisibility(View.VISIBLE);
                layoutControls.setVisibility(View.VISIBLE);
                textCallStatus.setText("Đang gọi");

                // Start call timer
                startCallTimer();
            }
        }.start();
    }

    private void startCallTimer() {
        callTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                callDurationSeconds++;
                updateCallDurationText();
            }

            @Override
            public void onFinish() {
                // This won't be called unless the timer is canceled
            }
        };

        callTimer.start();
    }

    private void updateCallDurationText() {
        long hours = callDurationSeconds / 3600;
        long minutes = (callDurationSeconds % 3600) / 60;
        long seconds = callDurationSeconds % 60;

        String durationText;
        if (hours > 0) {
            durationText = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            durationText = String.format("%02d:%02d", minutes, seconds);
        }

        textCallDuration.setText(durationText);
    }

    private void toggleCamera() {
        isCameraOff = !isCameraOff;

        if (isCameraOff) {
            btnToggleCamera.setImageResource(R.drawable.ic_camera_off);
            Toast.makeText(this, "Đã tắt camera", Toast.LENGTH_SHORT).show();
        } else {
            btnToggleCamera.setImageResource(R.drawable.ic_camera);
            Toast.makeText(this, "Đã bật camera", Toast.LENGTH_SHORT).show();
        }

        // In a real app, this would toggle the camera in the video call SDK
    }

    private void toggleMicrophone() {
        isMicMuted = !isMicMuted;

        if (isMicMuted) {
            btnToggleMic.setImageResource(R.drawable.ic_mic_off);
            Toast.makeText(this, "Đã tắt microphone", Toast.LENGTH_SHORT).show();
        } else {
            btnToggleMic.setImageResource(R.drawable.ic_mic);
            Toast.makeText(this, "Đã bật microphone", Toast.LENGTH_SHORT).show();
        }

        // In a real app, this would toggle the microphone in the video call SDK
    }

    private void endCall() {
        if (callTimer != null) {
            callTimer.cancel();
        }

        Toast.makeText(this, "Cuộc gọi đã kết thúc", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        // Show confirmation dialog before ending call
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Kết thúc cuộc gọi")
                .setMessage("Bạn có chắc muốn kết thúc cuộc gọi với bác sĩ?")
                .setPositiveButton("Kết thúc", (dialog, which) -> {
                    endCall();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (callTimer != null) {
            callTimer.cancel();
        }
    }
}
