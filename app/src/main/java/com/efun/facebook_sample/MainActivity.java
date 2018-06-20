package com.efun.facebook_sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.widget.GameRequestDialog;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LoginManager mLoginManager;
    private CallbackManager mCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoginManager = LoginManager.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        mLoginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile.fetchProfileForCurrentAccessToken();
                Profile p = Profile.getCurrentProfile();
                Log.i("fb","id : " + p.getId());
                Log.i("fb","name : " + p.getName());
                Toast.makeText(MainActivity.this,"Login Success! \nid : " + p.getId() +"\nname : " + p.getName(),Toast.LENGTH_LONG ).show();
            }

            @Override
            public void onCancel() {
                Log.i("fb","onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("fb","onError : " + error.getMessage());
                error.printStackTrace();
            }
        });

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginManager.logInWithReadPermissions(MainActivity.this, Arrays.asList(new String[]{"public_profile"}));
            }
        });

        findViewById(R.id.request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final GameRequestDialog requestDialog = new GameRequestDialog(MainActivity.this);
                requestDialog.registerCallback(mCallbackManager,
                        new FacebookCallback<GameRequestDialog.Result>() {
                            @Override
                            public void onSuccess(GameRequestDialog.Result result) {
                                Log.i("fb","RequestId : " + result.getRequestId());
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("Game Request Success!");
                                List<String> requestRecipients = result.getRequestRecipients();
                                if (requestRecipients != null && !requestRecipients.isEmpty() && requestRecipients.size()>0){
                                    for (String id :requestRecipients){
                                        stringBuilder.append("\n fbid: " + id);
                                        Log.i("fb","fbId : " + id);
                                    }
                                }
                                Toast.makeText(MainActivity.this,stringBuilder.toString(),Toast.LENGTH_LONG ).show();
                            }

                            @Override
                            public void onCancel() {
                                Log.i("fb","onCancel" );
                            }

                            @Override
                            public void onError(FacebookException error) {
                                Log.i("fb","onError : " + error.getMessage());
                                error.getMessage();
                            }
                        }
                );
                GameRequestContent.Builder builder = new GameRequestContent.Builder();
                builder.setMessage("游戏好好玩！");
                builder.setTitle("邀请");
                builder.setFilters(GameRequestContent.Filters.APP_NON_USERS);
                requestDialog.show(builder.build());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mCallbackManager != null){
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoginManager != null) {
            mLoginManager.logOut();
        }
    }
}
