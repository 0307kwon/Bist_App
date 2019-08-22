package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        requestMe();
        requestUpdateProfile();
    }

    public void onClickLogout(View a) {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                redirectLoginActivity();
            }
        });
    }
    public void requestMe() {
        List<String> keys = new ArrayList<>();
        keys.add("properties.nickname");
        keys.add("properties.profile_image");

        UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Log.d("kwon",message);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onSuccess(MeV2Response response) {
                TextView nick = findViewById(R.id.nickName);
                nick.setText(response.getNickname());

                ImageView profile = findViewById(R.id.profile);
                String imageUrl = response.getProfileImagePath();
                Glide.with(SignupActivity.this).load(imageUrl).into(profile);
            }

        });
    }

    public void redirectLoginActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void requestUpdateProfile() { // 사용자 정보수정
        final Map<String, String> properties = new HashMap<String, String>();
        properties.put("A_score", "30");
        UserManagement.getInstance().requestUpdateProfile(new ApiResponseCallback<Long>() {
            @Override
                public void onSuccess(Long userId) {
                UserProfile profile = UserProfile.loadFromCache();
                profile.updateUserProfile(properties);
                if (profile != null) {
                    profile.saveUserToCache();
                }
                Log.d("kwon","succeeded to update user profile" + profile);
            }
            @Override
            public void onFailure(ErrorResult errorResult) {
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

        }, properties);
    }
}
