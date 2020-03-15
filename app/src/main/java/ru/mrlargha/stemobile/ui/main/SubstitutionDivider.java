package ru.mrlargha.stemobile.ui.main;

class SubstitutionDivider {

    private String mDividerText;
    private int mDividerType;

    SubstitutionDivider(String divideReason, int dividerType) {
        mDividerText = divideReason;
        mDividerType = dividerType;
    }

    String getDividerText() {
        return mDividerText;
    }

    public int getDividerType() {
        return mDividerType;
    }
}
