package com.bokecc.video.ui.questionnaire;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bokecc.sdk.mobile.live.DWLive;
import com.bokecc.sdk.mobile.live.eventbus.CCEventBus;
import com.bokecc.sdk.mobile.live.pojo.QuestionnaireInfo;
import com.bokecc.sdk.mobile.live.socket.SocketQuestionnaireHandler;
import com.bokecc.video.R;
import com.bokecc.video.route.ResultMessage;
import com.bokecc.video.utils.CommonUtils;
import com.bokecc.video.widget.SubmitButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * 问卷：可能有多道题目。
 * 而每一道题目可能是(多选题，单选题，填空题，简答题，等等)。
 * 每一种类型的题界面都可能不一样。
 * 答完一题如果还有下一题，应该切换的下一题。在答题过程中界面不应该消失。
 */
public class QuestionnaireView extends DialogFragment implements View.OnClickListener, SocketQuestionnaireHandler.QuestionnaireListener {

    private static final String ANSWER_NOT_COMPLETE = "answer_not_complete";

    private View mRootView;

    private PagerAdapter mPagerAdapter;

    private ViewPager mViewPager;

    //关闭按钮
    private ImageView mCloseView;

    //问卷数据
    private QuestionnaireInfo questionInfo;

    //提交按钮
    private SubmitButton mSubmitBtn;

    //习题数据
    public List<QuestionnaireInfo.Subject> subjects = new ArrayList<>();

    //习题视图缓存
    public SparseArray<SubjectView> subjectViews = new SparseArray<>();

    //是否正在提交答案中
    private boolean submitting;

    //标记当前界面是作答还是显示答案
    private boolean answerFlag = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.layout_questionnaire, container);
        initView();
        initData();
        return mRootView;
    }

    private void initData() {
    }

    private void initView() {
        mCloseView = mRootView.findViewById(R.id.id_close);
        mViewPager = mRootView.findViewById(R.id.id_subject_container);
        mSubmitBtn = mRootView.findViewById(R.id.id_submit_btn);
        mSubmitBtn.setOnClickListener(this);
        mCloseView.setOnClickListener(this);

        mPagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return subjects.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return view == o;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View view = getSubjectView(position, subjects.get(position));
                container.addView(view);
                return view;
            }

            @NonNull
            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        };
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

            }
        });
    }

    @Override
    public void onResume() {
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        boolean isLandscape = getContext().getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        if (isLandscape) {
            params.width = CommonUtils.getScreenHeightPixels(getActivity());
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            params.width = CommonUtils.getScreenWidthPixels(getActivity());
            params.height = CommonUtils.getScreenHeightPixels(getActivity()) * 5 / 7;
        }
        getDialog().getWindow().setAttributes(params);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);

        if (answerFlag) {
            mSubmitBtn.setText("我知道了");
//            mCloseRunnable = new CloseRunnable();
//            mHandler.postDelayed(mCloseRunnable, 3000);
        } else {
            mSubmitBtn.setText("确定提交");
        }

        super.onResume();
    }


    public void setQuestionInfo(QuestionnaireInfo info) {
        questionInfo = info;
        subjects.clear();
        subjects.addAll(info.getSubjects());
        if (mPagerAdapter != null) {
            mPagerAdapter.notifyDataSetChanged();
        }

    }

    public void setAnswerFlag(boolean answerFlag) {
        this.answerFlag = answerFlag;
        if (!answerFlag) {
            subjectViews.clear();
        }
    }


    /**
     * 获取对应位置下的习题view
     */
    private View getSubjectView(int pos, QuestionnaireInfo.Subject subject) {
        SubjectView subjectView = subjectViews.get(pos);
        if (subjectView == null) {
            subjectView = createSubjectView(subject);
            subjectViews.put(pos, subjectView);
        }
        subjectView.setAnswerFlag(answerFlag);
        return subjectView.getView();
    }


    /**
     * 生成对应类型习题视图
     */
    private SubjectView createSubjectView(QuestionnaireInfo.Subject subject) {
        switch (subject.getType()) {
            case 0://选择题
            case 1:
                return new SelectSubjectView(getContext(), subject);
            default:
                throw new IllegalArgumentException("couldn't create subject view for this type:" + subject.getType());
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.id_submit_btn) {
            if (answerFlag) {
                dismiss();
                return;
            }
            //获取全部
            submitAnswer();
        } else if (v.getId() == R.id.id_close) {
            if (questionInfo.getForcibly() == 1) {
                return;
            }
            dismiss();
        }
    }


    private void submitAnswer() {
        if (submitting) return;
        String result = createQuestionnaireAnswer();
        if (ANSWER_NOT_COMPLETE.equals(result)) {
            return;
        }
        submitting = true;
        mSubmitBtn.startSubmitAnim();
        DWLive.getInstance().sendQuestionnaireAnswer(this, questionInfo.getId(), result);
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    /* 获取问卷的答案*/
    private String createQuestionnaireAnswer() {
        JSONArray answerArray = new JSONArray();  // 问卷习题的全部答案
        int size = subjectViews.size();
        for (int i = 0; i < size; i++) {
            SubjectView subjectView = subjectViews.get(i);
            if (!subjectView.answerComplete()) return ANSWER_NOT_COMPLETE;
            List<JSONObject> answer = subjectView.getAnswer();
            for (JSONObject ans : answer) {
                answerArray.put(ans);
            }
        }
        JSONObject answerObject = new JSONObject();
        try {
            answerObject.put("subjectsAnswer", answerArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return answerObject.toString();
    }

    @Override
    public void onSubmitResult(final boolean isSucceed, final String msg) {
        mSubmitBtn.post(new Runnable() {
            @Override
            public void run() {
                submitting = false;
                if (isSucceed) {
                    int code;
                    if (questionInfo.getSubmitedAction() == 0) { //0，提交成功后关闭，1：提交成功后显示答案
                        code = 0;
                    } else {
                        code = 1;
                    }
                    dismiss();
                    CCEventBus.getDefault().post(new ResultMessage(ResultMessage.SUCCESS, "恭喜您,提交成功", code, questionInfo));
                } else {
                    //提交失败
                    mSubmitBtn.reset();
                }
            }
        });
    }
}
