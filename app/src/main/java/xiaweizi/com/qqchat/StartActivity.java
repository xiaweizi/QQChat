package xiaweizi.com.qqchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class StartActivity extends AppCompatActivity {

    private EditText etChatFriend;
    private Button btStartChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        etChatFriend = (EditText) findViewById(R.id.et_chat_friend);
        btStartChat = (Button) findViewById(R.id.bt_start_chat);

        btStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String friend = etChatFriend.getText().toString().trim();
                if (TextUtils.isEmpty(friend))
                    return;

                Intent intent = new Intent(StartActivity.this, ChatActivity.class);
                intent.putExtra("friend", friend);
                startActivity(intent);
                finish();


            }
        });
    }
}
