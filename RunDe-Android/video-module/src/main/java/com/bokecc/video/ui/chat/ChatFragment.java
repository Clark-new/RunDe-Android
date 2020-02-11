package com.bokecc.video.ui.chat;

import android.annotation.SuppressLint;
import android.text.SpannableString;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bokecc.sdk.mobile.live.eventbus.CCEventBus;
import com.bokecc.sdk.mobile.live.eventbus.Subscribe;
import com.bokecc.sdk.mobile.live.eventbus.ThreadMode;
import com.bokecc.sdk.mobile.live.pojo.ChatMessage;
import com.bokecc.video.R;
import com.bokecc.video.adapter.MultiItemTypeAdapter;
import com.bokecc.video.adapter.base.ItemViewDelegate;
import com.bokecc.video.adapter.base.ViewHolder;
import com.bokecc.video.api.HDApi;
import com.bokecc.video.route.ChatMsgEntity;
import com.bokecc.video.route.DanmuMessage;
import com.bokecc.video.route.EndStreamMsg;
import com.bokecc.video.route.GiftMsg;
import com.bokecc.video.route.OnVideoSwitchMsg;
import com.bokecc.video.route.StatusChangeMsg;
import com.bokecc.video.utils.SpannableCache;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChatFragment extends GiftFragment {

    public static final String TEACHER_ROLE = "publisher";
    //内存缓存最大的消息数据
    private static int MAX_MESSAGE_NUM = 300;
    //超过最大消息数目后，一次删除的消息数据
    private static int EXCEED_REMOVE_NUM = 50;

    private RecyclerView mRecyclerView;
    private MultiItemTypeAdapter<ChatMessage> mAdapter;
    private LinkedList<ChatMessage> mMessageList;
    private LinkedList<ChatMessage> mShowList;
    private String mSelfId;

    //是否只显示老师
    private boolean isOnlyShowTeacher = false;
    //判断是否自动滚动到底部，当最后一条聊天数据显示时，自动滚动到底部，否则不自动滚动到底部
    private boolean shouldAutoScroll = true;

    @Override
    public int getRootResource() {
        return R.layout.layout_chat_fragment;
    }

    @Override
    protected void initData() {
        super.initData();
        if (!CCEventBus.getDefault().isRegistered(this)) {
            CCEventBus.getDefault().register(this);
        }
        mMessageList = new LinkedList<>();
        mShowList = new LinkedList<>();
        mSelfId = HDApi.get().getSelfId();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {
        super.initView();
        mRecyclerView = findViewById(R.id.id_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(null);
        mAdapter = new MultiItemTypeAdapter<>(getContext(), mShowList);
        mAdapter.addItemViewDelegate(new OtherChatItem());
        mAdapter.addItemViewDelegate(new SelfChatItem());
        mAdapter.addItemViewDelegate(new EmptyLayout());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (InputState.IS_INPUT_STATE) {
                    closeInputPlane();
                }
                return false;
            }
        });


        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //当停止滑动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的ItemPosition
                    int lastVisibleItem = manager.findLastVisibleItemPosition();
                    //所有条目,数量值
                    int totalItemCount = manager.getItemCount();
                    // 判断是否滚动到底部
                    if (lastVisibleItem == (totalItemCount - 1)) {
                        shouldAutoScroll = true;
                    }
                } else {
                    shouldAutoScroll = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy == 0) {
                    return;
                }
                shouldAutoScroll = false;

            }
        });

    }

    @Override
    protected void sendMsg(String msg) {
        if ("".equals(msg)) return;
        if (msg.length() >= 200) {
            Toast.makeText(getContext(), "文字消息过长", Toast.LENGTH_SHORT).show();
            return;
        }
        HDApi.get().sendPublicChatMsg(msg);
    }

    @Override
    protected void onlyShowTeacher(boolean onlyShowTeacher) {
        isOnlyShowTeacher = onlyShowTeacher;
        if (isOnlyShowTeacher) {
            List<ChatMessage> msgTmp = new LinkedList<>();
            for (ChatMessage msg : mMessageList) {
                if (TEACHER_ROLE.equals(msg.getUserRole()) || msg.getUserId().equals(mSelfId)) {
                    msgTmp.add(msg);
                }
            }
            mShowList.clear();
            mShowList.addAll(msgTmp);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(mShowList.size() - 1);
        } else {
            mShowList.clear();
            mShowList.addAll(mMessageList);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(mShowList.size() - 1);
        }
    }


    class OtherChatItem implements ItemViewDelegate<ChatMessage> {


        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_other_chat;
        }

        @Override
        public boolean isForViewType(ChatMessage msg, int position) {
            if (msg.getStatus().equals("1")) return false;
            if (msg.getUserId() != null && !"".equals(msg.getUserId())) {
                return !msg.getUserId().equals(mSelfId);
            }
            return false;
        }

        @Override
        public void convert(ViewHolder holder, ChatMessage message, int position) {
            holder.setImageGlideLoadUrl(R.id.id_head_img, message.getAvatar());
            String userName = message.getUserName();
            CharSequence content = userName + ":  " + message.getMessage();
            if (TEACHER_ROLE.equals(message.getUserRole())) {
                content = "老师  " + content;
                content = SpannableCache.get().setTeacherTag(content, getContext());
            }
            TextView contentView = holder.getView(R.id.id_text_content);
            SpannableString emotionContent = SpannableCache.get().getEmotionContent(-1, getContext(), contentView, content);
            SpannableCache.get().setUrlImageSpannable(getContext(), emotionContent, contentView, content);
            contentView.setText(emotionContent);
            holder.setOnTouchListener(R.id.id_item_container, new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (InputState.IS_INPUT_STATE) {
                        closeInputPlane();
                    }
                    return false;
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEndStream(EndStreamMsg msg) {
        //因为直播聊天是按着相对时间进行的，当老师出现一次上下课时
        //为了保留上一次直播的聊天信息，我们将上一次直播的聊天最后一条信息时间设置成负值
        if (mShowList.size() > 0) {
            mShowList.get(mShowList.size() - 1).setPrivIndex(-1);
        }
    }


    class SelfChatItem implements ItemViewDelegate<ChatMessage> {


        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_self_chat;
        }


        @Override
        public boolean isForViewType(ChatMessage message, int position) {
            if (message.getUserId() != null && !"".equals(message.getUserId())) {
                return message.getUserId().equals(mSelfId);
            }
            return false;
        }


        @Override
        public void convert(ViewHolder holder, ChatMessage message, int position) {
            holder.setImageGlideLoadUrl(R.id.id_head_img, message.getAvatar());
            String content = message.getMessage();
            TextView contentView = holder.getView(R.id.id_text_content);
            SpannableString emotionContent = SpannableCache.get().getEmotionContent(-1, getContext(), contentView, content);
            SpannableCache.get().setUrlImageSpannable(getContext(), emotionContent, contentView, content);
            contentView.setText(emotionContent);
            holder.setOnTouchListener(R.id.id_item_container, new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (InputState.IS_INPUT_STATE) {
                        closeInputPlane();
                    }
                    return false;
                }
            });
        }
    }


    class EmptyLayout implements ItemViewDelegate<ChatMessage> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_empy;
        }

        @Override
        public boolean isForViewType(ChatMessage message, int position) {
            if (message.getUserId() != null && !"".equals(message.getUserId())) {
                if (message.getUserId().equals(mSelfId)) {
                    return false;
                }
            }
            if ("1".equals(message.getStatus())) {
                return true;
            }
            return false;
        }

        @Override
        public void convert(ViewHolder holder, ChatMessage message, int position) {
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatMessageStatusChange(StatusChangeMsg changeMsg) {
        ArrayList<String> chatIds = changeMsg.chatIds;
        if (mMessageList != null && mMessageList.size() > 0 && chatIds != null && chatIds.size() > 0) {
            for (String chatId : chatIds) {
                for (int i = mMessageList.size() - 1; i > 0; --i) {
                    ChatMessage message = mMessageList.get(i);
                    if (chatId.equals(message.getChatId())) {
                        message.setStatus(changeMsg.status);
                        break;
                    }
                }
            }
            addApprovedDataToUi(chatIds, changeMsg.status);
        }
    }


    /**
     * 添加审核通过的聊天
     *
     * @param msgList msgList
     */
    private void addApprovedDataToUi(ArrayList<String> msgList, String status) {
        for (String chatId : msgList) {
            int index = mShowList.size() - 1;
            for (; index >= 0; --index) {
                ChatMessage m = mShowList.get(index);
                if (m.getChatId().equals(chatId)) {
                    m.setStatus(status);
                    if (m.getStatus().equals("0")) {
                        CCEventBus.getDefault().post(new DanmuMessage(m));
                    }
                    break;
                }
            }

            //添加显示
            mAdapter.notifyDataSetChanged();
            if (shouldAutoScroll) {
                mRecyclerView.scrollToPosition(mShowList.size() - 1);
            }

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveChatMsg(ChatMsgEntity msg) {
        if (msg.getType() == ChatMsgEntity.PUBLIC_CHAT) {
            ChatMessage message = (ChatMessage) msg.extra;
            int exceed = mMessageList.size() - MAX_MESSAGE_NUM;
            if (exceed > 0) {
                for (int i = 0; i < EXCEED_REMOVE_NUM; i++) {
                    mMessageList.pollFirst();
                }
                mAdapter.notifyItemRangeRemoved(0, EXCEED_REMOVE_NUM);
            }
            mMessageList.offerLast(message);
            addUiToShow(message);

        } else if (msg.getType() == ChatMsgEntity.HISTORY_CHAT) { //历史聊天消息

            if (msg.extra instanceof List) {
                List<ChatMessage> list = (List<ChatMessage>) msg.extra;
                //增量消息
                LinkedList<ChatMessage> addList = new LinkedList<>();
                //从缓冲消息id从后向前查找，直到相等的消息id
                if (mMessageList.size() != 0) {
                    ChatMessage last = mMessageList.getLast();
                    for (int i = list.size() - 1; i > 0; i--) {
                        ChatMessage message = list.get(i);
                        if (last.getChatId() != null
                                && last.getChatId().equals(message.getChatId())) {
                            break;
                        }
                        addList.offerFirst(message);
                    }
                } else {
                    addList.addAll(list);
                }

                int exceed = mMessageList.size() + addList.size() - MAX_MESSAGE_NUM;
                //缓存溢出，删除缓存
                if (exceed > 0) {
                    for (int i = 0; i < exceed; i++) {
                        mMessageList.removeFirst();
                    }
                }
                //添加新消息
                for (int i = 0; i < addList.size(); i++) {
                    mMessageList.offerLast(addList.get(i));
                }
                addUiToShow(addList);
            }
        }
    }

    /**
     * 将聊天消息添加到UI显示出来
     */
    private void addUiToShow(Object o) {
        if (o instanceof List) {
            List<ChatMessage> addList = (List<ChatMessage>) o;
            int exceed = mShowList.size() + addList.size() - MAX_MESSAGE_NUM;
            if (exceed > 0) {
                for (int i = 0; i < exceed; i++) {
                    mShowList.removeFirst();
                }
                mAdapter.notifyItemRangeRemoved(0, exceed);
            }
            //添加新消息
            int growth = 0;  //记录增加的数据
            for (int i = 0; i < addList.size(); i++) {
                ChatMessage message = addList.get(i);
                if (isOnlyShowTeacher && !TEACHER_ROLE.equals(message.getUserRole())) {
                    continue;
                }
                growth++;
                mShowList.offerLast(message);
            }
            mAdapter.notifyItemRangeInserted(mShowList.size() - 1, growth);
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.scrollToPosition(mShowList.size() - 1);
                }
            });
        } else if (o instanceof ChatMessage) {
            ChatMessage message = (ChatMessage) o;
            if (isOnlyShowTeacher && (!TEACHER_ROLE.equals(message.getUserRole()) && !message.getUserId().equals(mSelfId))) {
                return;
            }
            //开启聊天审核，同时也不是自己发的消息，则不显示在界面上
            //检测showList是否超出最大值，如果超出最大值，则从头删除一部分在添加数据
            int exceed = mShowList.size() - MAX_MESSAGE_NUM;
            if (exceed > 0) {
                for (int i = 0; i < EXCEED_REMOVE_NUM; i++) {
                    mShowList.pollFirst();
                }
                mAdapter.notifyItemRangeRemoved(0, MAX_MESSAGE_NUM);
            }

            mShowList.offerLast(message);
            mAdapter.notifyItemInserted(mShowList.size() - 1);

            if (shouldAutoScroll) {
                mRecyclerView.scrollToPosition(mShowList.size() - 1);
            }

            //检测是否是礼物消息
            GiftMsg msg = SpannableCache.extractGift(message.getMessage());
            if (msg != null) {
                msg.userName = message.getUserName();
                addGiftMsg(msg);
                return;
            }
            //发送到弹幕显示，礼物消息不显示
            if (message.getStatus().equals("1")) return;
            CCEventBus.getDefault().post(new DanmuMessage(message));
        }
    }


    /**
     * 不同的课程发生切换的回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoSwitch(OnVideoSwitchMsg message) {
        if (message.getType() == OnVideoSwitchMsg.PREPARE) {
            //不同课程视频切换前，聊天界面需要做的准备工作
        } else if (message.getType() == OnVideoSwitchMsg.START) {
            updateChatUi();
        }
    }


    @Override
    protected void updateChatUi() {
        super.updateChatUi();
        mMessageList.clear();
        mShowList.clear();
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (CCEventBus.getDefault().isRegistered(this)) {
            CCEventBus.getDefault().unregister(this);
        }
    }
}
