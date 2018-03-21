package com.robotmonsterlabs.ping.utility;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.robotmonsterlabs.ping.R;

import java.util.jar.Attributes;

/**
 * Created by joduplessis on 15/05/29.
 * Generic popup method to be used application wide
 */
public class PopupTooltip extends View implements View.OnClickListener {

    View view ;
    TextView label ;
    FrameLayout.LayoutParams params ;
    Context con ;
    ViewGroup parentViewGroup ;
    int startHoverYPosition ;

    // intialize the view and vars
    public PopupTooltip(Context context, AttributeSet attr) {
        super(context, attr);
        view = inflate(getContext(), R.layout.view_tooltip, null) ;
        label = (TextView) view.findViewById(R.id.label) ;
        params = new FrameLayout.LayoutParams(0,0);
        con = context ;
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        parentViewGroup = (ViewGroup) v.getParent() ;
        parentViewGroup.removeView(v) ;
    }

    // return the layout parameter
    public FrameLayout.LayoutParams getParams() {
        return params ;
    }

    // set height
    public PopupTooltip setHeight(int h) {
        params.height = convertToPx(h) ;
        return this ;
    }

    // set width
    public PopupTooltip setWidth(int w) {
        params.width = convertToPx(w) ;
        // params.width = FrameLayout.LayoutParams.WRAP_CONTENT ;
        return this ;
    }

    // set the top margin
    public PopupTooltip setY(int t) {
        params.topMargin = convertToPx(t);
        startHoverYPosition = t ;
        return this ;
    }

    // set the left margin
    public PopupTooltip setX(int l) {
        params.leftMargin = convertToPx(l);
        // params.gravity = Gravity.CENTER ;
        return this ;
    }

    // set the text
    public PopupTooltip setText(String str) {
        label.setText(str);
        return this ;
    }

    // set the animation
    public PopupTooltip setHover(int speed) {
        // AnimatorSet hoverSet = (AnimatorSet) AnimatorInflater.loadAnimator(con, R.animator.hover);
        // hoverSet.setTarget(view);
        // hoverSet.setDuration(speed);
        // hoverSet.start();
        ObjectAnimator hoverMovement = ObjectAnimator.ofFloat(view,"y",convertToPx(startHoverYPosition),convertToPx(startHoverYPosition-10));
        hoverMovement.setDuration(speed) ;
        hoverMovement.setRepeatCount(ValueAnimator.INFINITE);
        hoverMovement.setRepeatMode(ValueAnimator.REVERSE);
        hoverMovement.start();
        return this ;
    }

    // get the view we've made
    public View getView() {
        // get the view
        return view ;
    }

    // utility function
    private int convertToDp(int px) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    // utility function
    private int convertToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

}
