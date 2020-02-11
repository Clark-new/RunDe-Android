package com.bokecc.video.ui.questionnaire;

import android.view.View;

import org.json.JSONObject;

import java.util.List;

/**
 * 习题view抽象
 */
public interface SubjectView {

    void setAnswerFlag(boolean isAnswer);

    boolean answerComplete();

    int subjectType();

    List<JSONObject> getAnswer();

    View getView();
}
