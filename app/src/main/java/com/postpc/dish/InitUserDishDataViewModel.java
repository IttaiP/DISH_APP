package com.postpc.dish;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class InitUserDishDataViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<OtherUser>> otherUserLiveData;
    private MutableLiveData<List<String>> emailOtherUserLiveData;
    private MutableLiveData<List<OtherUser>> otherUserCalcSimLiveData;
    private MutableLiveData<List<String>> emailOtherUserCalcSimLiveData;

    public MutableLiveData<List<OtherUser>> getotherUserLiveData() {
        if (otherUserLiveData == null) {
            otherUserLiveData = new MutableLiveData<List<OtherUser>>();
        }
        return otherUserLiveData;
    }

    public MutableLiveData<List<String>> getemailOtherUserLiveData() {
        if (emailOtherUserLiveData == null) {
            emailOtherUserLiveData = new MutableLiveData<List<String>>();
        }
        return emailOtherUserLiveData;
    }


    public MutableLiveData<List<OtherUser>> getotherUserCalcSimLiveData() {
        if (otherUserCalcSimLiveData == null) {
            otherUserCalcSimLiveData = new MutableLiveData<List<OtherUser>>();
        }
        return otherUserCalcSimLiveData;
    }

    public MutableLiveData<List<String>> getemailOtherUserCalcSimLiveData() {
        if (emailOtherUserCalcSimLiveData == null) {
            emailOtherUserCalcSimLiveData = new MutableLiveData<List<String>>();
        }
        return emailOtherUserCalcSimLiveData;
    }
}