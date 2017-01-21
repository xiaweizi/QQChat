package xiaweizi.com.qqchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class RegistActivity extends AppCompatActivity {

    private static final String TAG = "--->";

    private EditText etName;
    private EditText etPassword;
    private Button btRegister;
    private Button btLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        etName = (EditText) findViewById(R.id.et_register_name);
        etPassword = (EditText) findViewById(R.id.et_register_password);

        btLogin = (Button) findViewById(R.id.bt_login);
        btRegister = (Button) findViewById(R.id.bt_register);

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            String name = etName.getText().toString().trim();
                            String password = etPassword.getText().toString().trim();

                            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)){
                                return;
                            }

                            EMClient.getInstance().createAccount(name, password);
                            toast("注册成功");

                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            Log.i(TAG, "run: " + e);
                            toast("注册失败" + e.getDescription());
                        }
                    }
                }).start();
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etName.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)){
                    return;
                }

                EMClient.getInstance().login(name,password,new EMCallBack() {//回调
                    @Override
                    public void onSuccess() {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegistActivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();
                                //进入聊天界面
                                startActivity(new Intent(RegistActivity.this, StartActivity.class));
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }

                    @Override
                    public void onError(int code, String message) {
                        toast("登陆失败" + message);
                    }
                });

            }
        });


    }

    private void toast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegistActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
