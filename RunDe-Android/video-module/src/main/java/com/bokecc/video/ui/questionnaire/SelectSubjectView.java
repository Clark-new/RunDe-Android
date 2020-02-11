package com.bokecc.video.ui.questionnaire;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bokecc.sdk.mobile.live.pojo.QuestionnaireInfo;
import com.bokecc.video.R;
import com.bokecc.video.adapter.CommonAdapter;
import com.bokecc.video.adapter.base.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择题view，包括单选和多选
 */
public class SelectSubjectView extends LinearLayout implements SubjectView {

    public boolean isAnswer;
    //是否是单向选择
    public boolean singleSelect;

    //当前题目
    public QuestionnaireInfo.Subject mSubject;

    public TextView mFlagTitle;
    public TextView mTitleTv;
    public RecyclerView mRecyclerView;
    public CommonAdapter<SelectOption> mAdapter;
    public List<SelectOption> mDataList;
    public ViewHolder mLastSelect;
    public SelectOption mLastSelectOption;
    public List<SelectOption> answerList = new ArrayList<>();

    public SelectSubjectView(Context context, QuestionnaireInfo.Subject subject) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.layout_select_subject, this, true);
        initData(subject);
        initView();
    }

    private void initData(QuestionnaireInfo.Subject subject) {
        mSubject = subject;
        mDataList = new ArrayList<>();
        SelectOption option;
        for (int i = 0; i < subject.getOptions().size(); i++) {
            option = new SelectOption(subject.getOptions().get(i));
            mDataList.add(option);
        }
        //设置问题标题
        singleSelect = subject.getType() == 0;
        mTitleTv = findViewById(R.id.id_select_title);
        mFlagTitle = findViewById(R.id.id_flag_title);
        mTitleTv.setText(subject.getContent());
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.id_recycler_view);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new CommonAdapter<SelectOption>(getContext(), R.layout.item_select_option, mDataList) {
            @Override
            protected void convert(final ViewHolder holder, final SelectOption option, int position) {
                holder.setText(R.id.id_index, getIndexTitle(option.entity.getIndex()));
                holder.setText(R.id.id_content_tv, option.entity.getContent());
                if (isAnswer) {
                    if (option.entity.getCorrect() == 1) { //正确答案显示绿色
                        holder.setBackgroundRes(R.id.id_option_view, R.drawable.item_right_select_bg);
                    } else if (option.isSelect()) {  //选择错误的选项
                        holder.setBackgroundRes(R.id.id_option_view, R.drawable.item_error_select_bg);
                    } else {
                        holder.setBackgroundRes(R.id.id_option_view, R.drawable.item_normal_select_bg);
                    }
                } else {
                    holder.setBackgroundRes(R.id.id_option_view, R.drawable.item_normal_select_bg);
                    if (option.isSelect()) {
                        holder.setTextColor(R.id.id_index, 0xFFFF454B);
                        holder.setTextColor(R.id.id_content_tv, 0xFFFF454B);
                    } else {
                        holder.setTextColor(R.id.id_index, 0xFF333333);
                        holder.setTextColor(R.id.id_content_tv, 0xFF333333);
                    }
                    holder.setOnClickListener(R.id.id_option_view, new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (singleSelect) {   //单向选择，点击事件
                                option.setSelect(true);
                                holder.setTextColor(R.id.id_index, 0xFFFF454B);
                                holder.setTextColor(R.id.id_content_tv, 0xFFFF454B);
                                if (mLastSelectOption != null) {
                                    mLastSelectOption.setSelect(false);
                                }
                                if (mLastSelect != null) {
                                    mLastSelect.setTextColor(R.id.id_index, 0xFF333333);
                                    mLastSelect.setTextColor(R.id.id_content_tv, 0xFF333333);
                                }
                                mLastSelect = holder;
                                mLastSelectOption = option;
                            } else { //多选题点击事件
                                if (option.isSelect()) {
                                    holder.setTextColor(R.id.id_index, 0xFF333333);
                                    holder.setTextColor(R.id.id_content_tv, 0xFF333333);
                                    option.setSelect(false);
                                    answerList.remove(option);
                                } else {
                                    holder.setTextColor(R.id.id_index, 0xFFFF454B);
                                    holder.setTextColor(R.id.id_content_tv, 0xFFFF454B);
                                    option.setSelect(true);
                                    answerList.add(option);
                                }
                            }
                        }
                    });
                }
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setAnswerFlag(boolean isAnswer) {
        this.isAnswer = isAnswer;

        if (isAnswer) {
            mFlagTitle.setText("答题结果");
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mFlagTitle.setText("请选择正确答案");
        }
    }

    @Override
    public boolean answerComplete() {
        if (singleSelect) {
            return mLastSelectOption != null;
        } else {
            return answerList.size() != 0;
        }
    }

    @Override
    public int subjectType() {
        return mSubject.getType();
    }

    @Override
    public List<JSONObject> getAnswer() {
        List<JSONObject> answer = new ArrayList<>();
        if (singleSelect && mLastSelectOption != null) {
            JSONObject ans = new JSONObject();
            try {
                ans.put("subjectId", mSubject.getId());
                ans.put("selectedOptionId", mLastSelectOption.entity.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            answer.add(ans);
        } else {
            JSONObject ans;
            for (SelectOption option : answerList) {
                ans = new JSONObject();
                try {
                    ans.put("subjectId", mSubject.getId());
                    ans.put("selectedOptionIds", option.entity.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                answer.add(ans);
            }
        }
        return answer;
    }

    @Override
    public View getView() {
        return this;
    }

    private String getIndexTitle(int index) {
        int a = 'A';
        char result = (char) (a+index);
        return result+". ";
    }

}
