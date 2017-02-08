
照例先来一波动态演示：
![环信及时聊天.gif](http://upload-images.jianshu.io/upload_images/4043475-d16a88926805236a.gif?imageMogr2/auto-orient/strip)

>功能很简单，注册用户 --> 用户登录 --> 选择聊天对象 --> 开始聊天

>使用到的知识点：
>
1. `RecyclerView`
2. `CardView`
3. 环信的API的简单使用

>依赖的库
>        
>     compile 'com.android.support:appcompat-v7:24.2.1'
    compile 'com.android.support:cardview-v7:24.1.1'
    compile 'com.android.support:recyclerview-v7:24.0.0'    
## 1、聊天页面 ##
*首先是看了郭神的《第二行代码》做了聊天界面，用的是RecyclerView*
#### a.  消息类的封装 ####
---
    public class MSG {
	    public static final int TYPE_RECEIVED = 0;//消息的类型:接收
	    public static final int TYPE_SEND = 1;    //消息的类型:发送

	    private String content;//消息的内容
	    private int type;	   //消息的类型
	
	    public MSG(String content, int type) {
	        this.content = content;
	        this.type = type;
	    }
	
	    public String getContent() {
	        return content;
	    }
	
	    public int getType() {
	        return type;
	    }
    }
#### b.	RecyclerView子项的布局 ####
---
	<LinearLayout
        android:id="@+id/ll_msg_left"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
		<!-- 设置点击效果为水波纹(5.0以上) -->
        android:background="?android:attr/selectableItemBackground"
        android:clickable="true"
        android:focusable="true"
        android:orientation="horizontal"
        android:padding="2dp">

        <android.support.v7.widget.CardView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:cardCornerRadius="20dp"
            app:cardPreventCornerOverlap="false"
            app:cardUseCompatPadding="true">

            <ImageView
                android:layout_width="50dp"
                android:layout_height="50dp"
                android:scaleType="centerCrop"
                android:src="@mipmap/man" />
        </android.support.v7.widget.CardView>

        <LinearLayout
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:background="@drawable/message_left"
            android:orientation="horizontal">

            <TextView
                android:id="@+id/tv_msg_left"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="center"
                android:layout_margin="10dp"
                android:textColor="#fff" />
        </LinearLayout>

    </LinearLayout>
> 这是左边的部分，至于右边应该也就简单了。我用CardView把ImageView包裹起来，这样比较好看。效果如下：

![item布局.png](http://upload-images.jianshu.io/upload_images/4043475-76ea5370b4d09d89.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### c.	RecyclerView适配器 ####
---
		public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.MyViewHolder> {
	
	    private List<MSG> mMsgList;
	
	    public MsgAdapter(List<MSG> mMsgList) {
	        this.mMsgList = mMsgList;
	    }
	
	    @Override
	    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
	        View view = View.inflate(parent.getContext(), R.layout.item_msg, null);
	        MyViewHolder holder = new MyViewHolder(view);
	        return holder;
	    }
	
	    @Override
	    public void onBindViewHolder(MyViewHolder holder, int position) {
	        MSG msg = mMsgList.get(position);
	        if (msg.getType() == MSG.TYPE_RECEIVED){
				//如果是收到的消息，显示左边布局，隐藏右边布局
	            holder.llLeft.setVisibility(View.VISIBLE);
	            holder.llRight.setVisibility(View.GONE);
	            holder.tv_Left.setText(msg.getContent());
	        } else if (msg.getType() == MSG.TYPE_SEND){
				//如果是收到的消息，显示右边布局，隐藏左边布局
	            holder.llLeft.setVisibility(View.GONE);
	            holder.llRight.setVisibility(View.VISIBLE);
	            holder.tv_Right.setText(msg.getContent());
	        }
	    }
	
	    @Override
	    public int getItemCount() {
	        return mMsgList.size();
	    }
	
	    static class MyViewHolder extends RecyclerView.ViewHolder{
	
	        LinearLayout llLeft;
	        LinearLayout llRight;
	
	        TextView tv_Left;
	        TextView tv_Right;
	
	        public MyViewHolder(View itemView) {
	            super(itemView);
	
	            llLeft = (LinearLayout) itemView.findViewById(R.id.ll_msg_left);
	            llRight = (LinearLayout) itemView.findViewById(R.id.ll_msg_right);
	
	            tv_Left = (TextView) itemView.findViewById(R.id.tv_msg_left);
	            tv_Right = (TextView) itemView.findViewById(R.id.tv_msg_right);
	
	        }
	    }
	}
>这部分应该也没什么问题，就是适配器的创建，我之前的文章也讲过 传送门：[简单粗暴----RecyclerView](http://www.jianshu.com/p/60819de9eb42)

#### d.	RecyclerView初始化 ####
---
就是一些基本的初始化，我就不赘述了，讲一下添加数据的细节处理

		btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etInput.getText().toString().trim();
                if (!TextUtils.isEmpty(content)){

					...//环信部分的发送消息

        			MSG msg = new MSG(content, MSG.TYPE_SEND);
                    mList.add(msg);
					//当有新消息时，刷新RecyclerView中的显示
                    mAdapter.notifyItemInserted(mList.size() - 1);
					//将RecyclerView定位到最后一行
                    mRecyclerView.scrollToPosition(mList.size() - 1);
                    etInput.setText("");
                }
            }
        });
>至此界面已经结束了，接下来就是数据的读取

## 2. 环信API的简单应用 ##
*官网有详细的API介绍 [环信及时通讯V3.0](http://docs.easemob.com/im/start "集成")，我这里就简单介绍如何简单集成*

#### a.	环信开发账号的注册 ####
---
[环信官网](http://www.easemob.com/)
>创建应用得到Appkey后面要用
>![环信注册.PNG](http://upload-images.jianshu.io/upload_images/4043475-e4dd45e05060467f.PNG?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### b.	SDK导入 ####
---
你可以直接下载然后拷贝工程的libs目录下

Android Studio可以直接添加依赖

>将以下代码放到项目根目录的build.gradle文件里
>
	repositories {
   		 maven { url "https://raw.githubusercontent.com/HyphenateInc/Hyphenate-SDK-Android/master/repository" }
	}
>在你的module的build.gradle里加入以下代码
>
	android {
	    //use legacy for android 6.0
	    useLibrary 'org.apache.http.legacy'
	}
	dependencies {
	    compile 'com.android.support:appcompat-v7:23.4.0'
	    //Optional compile for GCM (Google Cloud Messaging).
	    compile 'com.google.android.gms:play-services-gcm:9.4.0'
	    compile 'com.hyphenate:hyphenate-sdk:3.2.3'
	}
>如果想使用不包含音视频通话的sdk，用`compile 'com.hyphenate:hyphenate-sdk-lite:3.2.3'`

#### c.	清单文件配置 ####
---
	<?xml version="1.0" encoding="utf-8"?>
	<manifest xmlns:android="http://schemas.android.com/apk/res/android"
	    package="Your Package"
	    android:versionCode="100"
	    android:versionName="1.0.0">
   
	    <!-- Required -->
	    <uses-permission android:name="android.permission.VIBRATE" />
	    <uses-permission android:name="android.permission.INTERNET" />
	    <uses-permission android:name="android.permission.RECORD_AUDIO" />
	    <uses-permission android:name="android.permission.CAMERA" />
	    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	    <uses-permission android:name="android.permission.ACCESS_MOCK_LOCATION" />
	    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>  
	    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
	    <uses-permission android:name="android.permission.GET_TASKS" />
	    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
	    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
	    <uses-permission android:name="android.permission.WAKE_LOCK" />
	    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
	    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
	    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
	  
	    <application
	        android:icon="@drawable/ic_launcher"
	        android:label="@string/app_name"
	        android:name="Your Application">
	   
	    <!-- 设置环信应用的AppKey -->
	        <meta-data android:name="EASEMOB_APPKEY"  android:value="Your AppKey" />
	        <!-- 声明SDK所需的service SDK核心功能-->
	        <service android:name="com.hyphenate.chat.EMChatService" android:exported="true"/>
	        <service android:name="com.hyphenate.chat.EMJobService"
	            android:permission="android.permission.BIND_JOB_SERVICE"
	            android:exported="true"
	            />
	        <!-- 声明SDK所需的receiver -->
	        <receiver android:name="com.hyphenate.chat.EMMonitorReceiver">
	            <intent-filter>
	                <action android:name="android.intent.action.PACKAGE_REMOVED"/>
	                <data android:scheme="package"/>
	            </intent-filter>
	            <!-- 可选filter -->
	            <intent-filter>
	                <action android:name="android.intent.action.BOOT_COMPLETED"/>
	                <action android:name="android.intent.action.USER_PRESENT" />
	            </intent-filter>
	        </receiver>
	    </application>
	</manifest>
APP打包混淆

    -keep class com.hyphenate.** {*;}
    -dontwarn  com.hyphenate.**
#### d.	初始化SDK ####
---
*在自定义Application的onCreate中初始化*

	public class MyApplication extends Application {

	    private Context appContext;
	
	    @Override
	    public void onCreate() {
	        super.onCreate();
	        EMOptions options = new EMOptions();
	        options.setAcceptInvitationAlways(false);
	
	        appContext = this;
	        int pid = android.os.Process.myPid();
	        String processAppName = getAppName(pid);
	        // 如果APP启用了远程的service，此application:onCreate会被调用2次
	        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
	        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
	
	        if (processAppName == null || !processAppName.equalsIgnoreCase(appContext.getPackageName())) {
	            Log.e("--->", "enter the service process!");
	
	            // 则此application::onCreate 是被service 调用的，直接返回
	            return;
	        }
	
	        //初始化
	        EMClient.getInstance().init(getApplicationContext(), options);
	        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
	        EMClient.getInstance().setDebugMode(true);
	    }
	
	    private String getAppName(int pID) {
	        String processName = null;
	        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
	        List l = am.getRunningAppProcesses();
	        Iterator i = l.iterator();
	        PackageManager pm = this.getPackageManager();
	        while (i.hasNext()) {
	            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
	            try {
	                if (info.pid == pID) {
	                    processName = info.processName;
	                    return processName;
	                }
	            } catch (Exception e) {
	                // Log.d("Process", "Error>> :"+ e.toString());
	            }
	        }
	        return processName;
	    }
	}

#### e.	注册和登陆 ####
---
*注册要在子线程中执行*

    //注册失败会抛出HyphenateException
    EMClient.getInstance().createAccount(username, pwd);//同步方法

	EMClient.getInstance().login(userName,password,new EMCallBack() {//回调
	    @Override
	    public void onSuccess() {
	        EMClient.getInstance().groupManager().loadAllGroups();
	        EMClient.getInstance().chatManager().loadAllConversations();
	            Log.d("main", "登录聊天服务器成功！");        
	    }
	 
	    @Override
	    public void onProgress(int progress, String status) {
	 
	    }
	 
	    @Override
	    public void onError(int code, String message) {
	        Log.d("main", "登录聊天服务器失败！");
	    }
	});
#### f.	发送消息 ####
---
    //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
    EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
    //发送消息
    EMClient.getInstance().chatManager().sendMessage(message);
#### g.	接收消息 ####
---
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

>接收消息的监听器分别需要在OnResume()和OnDestory()方法中注册和取消注册

    EMClient.getInstance().chatManager().addMessageListener(msgListener);//注册

    EMClient.getInstance().chatManager().removeMessageListener(msgListener);//取消注册

>需要注意的是，当接收到消息，需要在**主线程**中更新适配器，否则会不能及时刷新出来

到此，一个简单的及时聊天Demo已经完成，功能很简单，如果需要添加额外功能的话，可以自行参考官网，官网给出的教程还是很不错的！

最后希望大家能多多支持我，需要你们的支持喜欢！！