package com.kondie.pm_mechanic;

import android.app.Activity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

public class CoolLoading {

    public static LinearLayout loadingGeoLay, coolLoadingLay;
    static View toTheLeft, toTheRight;
    static Activity activity;

    public CoolLoading(Activity activity){
        this.activity = activity;
        coolLoadingLay = activity.findViewById(R.id.geo_cool_loading_lay);
        toTheLeft = activity.findViewById(R.id.geo_to_the_left);
        toTheRight = activity.findViewById(R.id.geo_to_the_right);
    }

    public void startCoolLoadingAnim() {

        try {
            coolLoadingLay.setVisibility(View.VISIBLE);
            toTheRight.startAnimation(getMoveRightAnim());
            toTheLeft.startAnimation(getMoveLeftAnim());
        } catch (Exception e) {
        }
    }

    public Animation getBreathingAnim(){

        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.bouncy_anim);
        Interpolator interpolator = new AccelerateDecelerateInterpolator();
        anim.setInterpolator(interpolator);

        return (anim);
    }

    public Animation getShowLessAnim(){

        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.show_less);
        Interpolator interpolator = new AccelerateDecelerateInterpolator();
        anim.setInterpolator(interpolator);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                MainActivity.showIcon.setRotation(0);
                MainActivity.orderList.setVisibility(View.GONE);
                Animation rotAnim = AnimationUtils.loadAnimation(activity, R.anim.rotate_0);
                Interpolator rotInterpolator = new AccelerateDecelerateInterpolator();
                rotAnim.setInterpolator(rotInterpolator);
                MainActivity.showIcon.startAnimation(rotAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        return (anim);
    }

    public Animation getShowMoreAnim(){

        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.show_more);
        Interpolator interpolator = new AccelerateDecelerateInterpolator();
        anim.setInterpolator(interpolator);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                MainActivity.orderList.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                MainActivity.showIcon.setRotation(180);
                Animation rotAnim = AnimationUtils.loadAnimation(activity, R.anim.rotate_180);
                Interpolator rotInterpolator = new AccelerateDecelerateInterpolator();
                rotAnim.setInterpolator(rotInterpolator);
                MainActivity.showIcon.startAnimation(rotAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        return (anim);
    }

    public Animation getMoveRightAnim() {

        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.move_right_from_center);

        return (anim);
    }

    public Animation getMoveLeftAnim() {

        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.move_left_from_center);

        return (anim);
    }

    public void stopCoolLoadingAnim() {

        toTheLeft.clearAnimation();
        toTheRight.clearAnimation();
        coolLoadingLay.setVisibility(View.GONE);
    }
}
