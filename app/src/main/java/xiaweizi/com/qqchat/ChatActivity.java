package xiaweizi.com.qqchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity--->";

    private MsgAdapter mAdapter;

    private RecyclerView mRecyclerView;
    private EditText etInput;
    private Button btSend;

    private List<MSG> mList;

    private String friend;
    private EMMessageListener msgListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);

        friend = getIntent().getStringExtra("friend");
        Log.i(TAG, "onCreate: " + friend);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_chat);
        etInput = (EditText) findViewById(R.id.et_input);
        btSend = (Button) findViewById(R.id.bt_send);

        initData();
        mAdapter = new MsgAdapter(mList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etInput.getText().toString().trim();
                if (!TextUtils.isEmpty(content)){

                    //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
                    EMMessage message = EMMessage.createTxtSendMessage(content, friend);
                    //发送消息
                    EMClient.getInstance().chatManager().sendMessage(message);
                    Log.i(TAG, "发送成功！");

                    MSG msg = new MSG(content, MSG.TYPE_SEND);
                    mList.add(msg);
                    mAdapter.notifyItemInserted(mList.size() - 1);
                    mRecyclerView.scrollToPosition(mList.size() - 1);
                    etInput.setText("");
                }
            }
        });

        //收到消息
        //收到透传消息
        //消息状态变动
        msgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //收到消息
                String result = messages.get(0).getBody().toString();
                String msgReceived = result.substring(5, result.length() - 1);

                Log.i(TAG, "onMessageReceived: " + msgReceived);
                final MSG msg = new MSG(msgReceived, MSG.TYPE_RECEIVED);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mList.add(msg);
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.scrollToPosition(mList.size() - 1);
                    }
                });

            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @Override
            public void onMessageRead(List<EMMessage> list) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> list) {

            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(msgListener);


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    private void initData() {
        mList = new ArrayList<>();
        mList.add(new MSG("日照香炉生紫烟，你与何人在聊天" , MSG.TYPE_SEND));
        mList.add(new MSG("黄河之水天上来，就是普通一女孩" , MSG.TYPE_RECEIVED));
        mList.add(new MSG("万水千山只等闲，微信闲扯这么甜" , MSG.TYPE_SEND));
        mList.add(new MSG("日出江花红胜火，我俩只是谈工作" , MSG.TYPE_RECEIVED));
        mList.add(new MSG("曾经沧海难为水，你俩肯定有一腿" , MSG.TYPE_SEND));
        mList.add(new MSG("除却巫山不是云，谁要骗你不是人" , MSG.TYPE_RECEIVED));
    }
}
