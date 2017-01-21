package xiaweizi.com.qqchat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ljkj on 2017/1/21.
 */

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
            holder.llLeft.setVisibility(View.VISIBLE);
            holder.llRight.setVisibility(View.GONE);
            holder.tv_Left.setText(msg.getContent());
        } else if (msg.getType() == MSG.TYPE_SEND){
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
