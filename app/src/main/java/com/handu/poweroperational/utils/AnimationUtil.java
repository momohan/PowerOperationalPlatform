package com.handu.poweroperational.utils;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

import com.handu.poweroperational.ui.widget.view.FloatingDraftButton;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by 柳梦 on 2016/12/1.
 * 动画工具类
 */

public class AnimationUtil {

    public static final LinearOutSlowInInterpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new LinearOutSlowInInterpolator();

    private static void slideView(final View view, final float p1, final float p2, final float p3, final float p4, long durationMillis, long delayMillis, final boolean startVisible, final boolean endVisible) { /*如果处在动画阶段则不允许再次运行动画*/
        if (view.getTag() != null && "-1".equals(view.getTag().toString())) return;
        TranslateAnimation animation = new TranslateAnimation(p1, p2, p3, p4);
        animation.setDuration(durationMillis);
        animation.setStartOffset(delayMillis);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (startVisible) view.setVisibility(View.VISIBLE);
                else view.setVisibility(View.INVISIBLE);
                view.setTag("-1");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                int left = view.getLeft() + (int) (p2 - p1);
                int top = view.getTop() + (int) (p4 - p3);
                int width = view.getWidth();
                int height = view.getHeight();
                view.layout(left, top, left + width, top + height);/*重新设置位置*/
                if (endVisible) view.setVisibility(View.VISIBLE);
                else view.setVisibility(View.INVISIBLE);
                view.setTag("1");
            }
        });
        if (endVisible) view.startAnimation(animation);
        else {/*如果关闭则加渐变效果*/
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.setDuration(durationMillis);
            animationSet.setStartOffset(delayMillis);
            animationSet.addAnimation(animation);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            animationSet.addAnimation(alphaAnimation);
            view.startAnimation(animationSet);
        }

    }

    /**
     * 移动Buttons展开或者关闭
     *
     * @param context
     * @param button
     */
    public static void slideButtons(Context context, final FloatingDraftButton button) {

        int size = button.getButtonSize();
        if (size == 0) {
            return;
        }
        int buttonLeft = button.getLeft();
        int screenWidth = ScreenUtils.getScreenWidth(context);
        int buttonRight = screenWidth - button.getRight();
        int buttonTop = button.getTop();
        int buttonBottom = ScreenUtils.getScreenHeight(context) - button.getBottom();
        int buttonWidth = button.getWidth();
        int radius = 7 * buttonWidth / 4;
        int gap = 5 * buttonWidth / 4;

        ArrayList<FloatingActionButton> buttons = button.getButtons();

        if (!button.isExpand()) {//展开Buttons
            showRotateAnimation(button, 0, 225);
            //可围成圆形
            if (buttonLeft >= radius && buttonRight >= radius
                    && buttonTop >= radius && buttonBottom >= radius) {
                double angle = 360.0 / size;
                int randomDegree = new Random().nextInt(180);
                for (int i = 0; i < size; i++) {
                    FloatingActionButton faButton = buttons.get(i);
                    slideView(faButton, 0, radius * (float) Math.cos(Math.toRadians(randomDegree + angle * i)), 0, radius * (float) Math.sin(Math.toRadians(randomDegree + angle * i)), 500, 0, true, true);
                }

            } else if (size * gap < screenWidth && (buttonTop > 3 * buttonBottom || buttonBottom > 3 * buttonTop)) {//如果太靠上边或下边
                int leftNumber = buttonLeft / gap;
                int rightNumber = buttonRight / gap;
                if (buttonTop >= radius && buttonBottom >= radius) {
                    if (buttonTop > buttonBottom) {
                        FloatingActionButton faButton = buttons.get(0);
                        slideView(faButton, 0, 0, 0, -radius, 500, 0, true, true);
                        for (int i = 1; i <= leftNumber && i < size; i++) {
                            faButton = buttons.get(i);
                            slideView(faButton, 0, -gap * i, 0, -radius, 500, 0, true, true);
                        }
                        for (int i = 1; i <= rightNumber && i < size - leftNumber; i++) {
                            faButton = buttons.get(i + leftNumber);
                            slideView(faButton, 0, gap * i, 0, -radius, 500, 0, true, true);
                        }
                    } else {
                        FloatingActionButton faButton = buttons.get(0);
                        slideView(faButton, 0, 0, 0, radius, 500, 0, true, true);
                        for (int i = 1; i <= leftNumber && i < size; i++) {
                            faButton = buttons.get(i);
                            slideView(faButton, 0, -gap * i, 0, radius, 500, 0, true, true);
                        }
                        for (int i = 1; i <= rightNumber && i < size - leftNumber; i++) {
                            faButton = buttons.get(i + leftNumber);
                            slideView(faButton, 0, gap * i, 0, radius, 500, 0, true, true);
                        }
                    }
                } else if (buttonTop >= radius) {
                    FloatingActionButton faButton = buttons.get(0);
                    slideView(faButton, 0, 0, 0, -radius, 500, 0, true, true);
                    for (int i = 1; i <= leftNumber && i < size; i++) {
                        faButton = buttons.get(i);
                        slideView(faButton, 0, -gap * i, 0, -radius, 500, 0, true, true);
                    }
                    for (int i = 1; i <= rightNumber && i < size - leftNumber; i++) {
                        faButton = buttons.get(i + leftNumber);
                        slideView(faButton, 0, gap * i, 0, -radius, 500, 0, true, true);
                    }
                } else if (buttonBottom >= radius) {
                    FloatingActionButton faButton = buttons.get(0);
                    slideView(faButton, 0, 0, 0, radius, 500, 0, true, true);
                    for (int i = 1; i <= leftNumber && i < size; i++) {
                        faButton = buttons.get(i);
                        slideView(faButton, 0, -gap * i, 0, radius, 500, 0, true, true);
                    }
                    for (int i = 1; i <= rightNumber && i < size - leftNumber; i++) {
                        faButton = buttons.get(i + leftNumber);
                        slideView(faButton, 0, gap * i, 0, radius, 500, 0, true, true);
                    }
                }
            } else {
                int upNumber = buttonTop / gap;
                int belowNumber = buttonBottom / gap;
                if ((upNumber + belowNumber + 1) >= size) {
                    upNumber = upNumber * (size - 1) / (upNumber + belowNumber);
                    belowNumber = size - 1 - upNumber;
                    if (buttonLeft >= radius) {
                        FloatingActionButton faButton = buttons.get(0);
                        slideView(faButton, 0, -radius, 0, 0, 500, 0, true, true);
                        for (int i = 1; i <= upNumber && i < size; i++) {
                            faButton = buttons.get(i);
                            slideView(faButton, 0, -radius, 0, -gap * i, 500, 0, true, true);
                        }
                        for (int i = 1; i <= belowNumber && i < size - upNumber; i++) {
                            faButton = buttons.get(i + upNumber);
                            slideView(faButton, 0, -radius, 0, gap * i, 500, 0, true, true);
                        }
                    } else if (buttonRight >= radius) {
                        FloatingActionButton faButton = buttons.get(0);
                        slideView(faButton, 0, radius, 0, 0, 500, 0, true, true);
                        for (int i = 1; i <= upNumber && i < size; i++) {
                            faButton = buttons.get(i);
                            slideView(faButton, 0, radius, 0, -gap * i, 500, 0, true, true);
                        }
                        for (int i = 1; i <= belowNumber && i < size - upNumber; i++) {
                            faButton = buttons.get(i + upNumber);
                            slideView(faButton, 0, radius, 0, gap * i, 500, 0, true, true);
                        }
                    }
                }

            }
            button.setExpand(true);
        } else { //关闭Buttons
            showRotateAnimation(button, 225, 0);
            for (FloatingActionButton faButton : buttons) {
                int faButtonLeft = faButton.getLeft();
                int faButtonTop = faButton.getTop();
                slideView(faButton, 0, buttonLeft - faButtonLeft, 0, buttonTop - faButtonTop, 500, 0, true, false);
            }
            button.setExpand(false);
        }
    }

    /**
     * 旋转的动画
     *
     * @param mView        需要选择的View
     * @param startDegrees 初始的角度【从这个角度开始】
     * @param degrees      当前需要旋转的角度【转到这个角度来】
     */
    private static void showRotateAnimation(View mView, int startDegrees, int degrees) {
        mView.clearAnimation();
        float centerX = mView.getWidth() / 2.0f;
        float centerY = mView.getHeight() / 2.0f;
        //这个是设置需要旋转的角度（也是初始化），我设置的是当前需要旋转的角度
        RotateAnimation rotateAnimation = new RotateAnimation(startDegrees, degrees, centerX, centerY);//centerX和centerY是旋转View时候的锚点
        //这个是设置动画时间的
        rotateAnimation.setDuration(500);
        rotateAnimation.setInterpolator(new AccelerateInterpolator());
        //动画执行完毕后是否停在结束时的角度上
        rotateAnimation.setFillAfter(true);
        //启动动画
        mView.startAnimation(rotateAnimation);
    }

    // 显示view
    public static void scaleShow(View view, ViewPropertyAnimatorListener viewPropertyAnimatorListener) {
        view.setVisibility(View.VISIBLE);
        ViewCompat.animate(view)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .alpha(1.0f)
                .setDuration(800)
                .setListener(viewPropertyAnimatorListener)
                .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                .start();
    }

    // 隐藏view
    public static void scaleHide(View view, ViewPropertyAnimatorListener viewPropertyAnimatorListener) {
        ViewCompat.animate(view)
                .scaleX(0.0f)
                .scaleY(0.0f)
                .alpha(0.0f)
                .setDuration(800)
                .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                .setListener(viewPropertyAnimatorListener)
                .start();
    }

    //显示动画
    public static Animation getShowAnimation() {
        AnimationSet set = new AnimationSet(true);
        RotateAnimation rotate = new RotateAnimation(0, 720, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        set.addAnimation(rotate);
        TranslateAnimation translate = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 2f
                , TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, 0);
        set.addAnimation(translate);
        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        set.addAnimation(alpha);
        set.setDuration(500);
        return set;
    }

    //隐藏动画
    public static Animation getHiddenAnimation() {
        AnimationSet set = new AnimationSet(true);
        RotateAnimation rotate = new RotateAnimation(0, 720, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        set.addAnimation(rotate);
        TranslateAnimation translate = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, 2f
                , TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, 0);
        set.addAnimation(translate);
        AlphaAnimation alpha = new AlphaAnimation(1, 0);
        set.addAnimation(alpha);
        set.setDuration(500);
        return set;
    }
}
