package com.example.ajoan.events.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.ajoan.maps.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class InputPanelFragment extends Fragment {

    private Listener mListener;

    private EditText inputET;
    private ImageView searchButtonIMV;

    public InputPanelFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_input_panel, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputET=(EditText)view.findViewById(R.id.input_et);
        inputET.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchButtonIMV.setEnabled(s.length() > 0); }
            @Override public void afterTextChanged(Editable s) {}
        });
        mListener.setInputPanelEditTextTHint(inputET);

        searchButtonIMV = (ImageView)view.findViewById(R.id.button_imv);
        searchButtonIMV.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { mListener.onInputPanelButtonClick(inputET); }});
        mListener.setInputPanelButtonIMVImage(searchButtonIMV);

        ImageView swapButtonIMV = (ImageView)view.findViewById(R.id.swap_button_imv);
        swapButtonIMV.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { mListener.onInputPanelSwapButtonClick(); }});
        mListener.setInputPanelSwapButtonIMVImage(swapButtonIMV);
    }


    public interface Listener {
        void setInputPanelEditTextTHint(EditText et);
        void setInputPanelButtonIMVImage(ImageView bimv);
        void onInputPanelButtonClick(EditText et);
        void setInputPanelSwapButtonIMVImage(ImageView bimv);
        void onInputPanelSwapButtonClick();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener)
            mListener = (Listener) context;
        else
            throw new RuntimeException(context.toString()
                    + " must implement Listener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}