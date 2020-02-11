package com.bokecc.video.ui.questionnaire;

import com.bokecc.sdk.mobile.live.pojo.QuestionnaireInfo;

/**
 * 选择题选项实体类
 */
public class SelectOption {
    QuestionnaireInfo.Option entity;
    private boolean select;
    public SelectOption(QuestionnaireInfo.Option option) {
        this.entity = option;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
