package xiaweizi.com.qqchat;

/**
 * Created by ljkj on 2017/1/21.
 */

public class MSG {
    public static final int TYPE_RECEIVED = 0;//消息的类型:接收
    public static final int TYPE_SEND = 1;    //消息的类型:发送

    private String content;

    private int type;

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
