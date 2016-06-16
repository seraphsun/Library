package code.support.demo.Test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.ButterKnife;
import butterknife.OnClick;
import code.support.demo.R;
import code.support.demo.activity.Activity_01;
import code.support.demo.activity.Activity_02;
import code.support.demo.activity.Activity_12;
import code.support.demo.activity.Activity_04;
import code.support.demo.activity.Activity_05;
import code.support.demo.activity.Activity_06;
import code.support.demo.activity.Activity_07;
import code.support.demo.activity.Activity_08;
import code.support.demo.activity.Activity_09;
import code.support.demo.activity.Activity_10;
import code.support.demo.activity.Activity_11;
import code.support.demo.activity.Activity_03;

/**
 * Created by Design on 2016/5/10.
 */
public class TestActivity_3 extends Activity {

    Intent mIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_03);

        ButterKnife.bind(this);
        mIntent = getIntent();
    }

    @OnClick(R.id.cv_one)
    public void jump1() {
        mIntent = new Intent(TestActivity_3.this, Activity_01.class);
        jumpToActivity(mIntent);
    }

    @OnClick(R.id.cv_two)
    public void jump2() {
        mIntent = new Intent(TestActivity_3.this, Activity_02.class);
        jumpToActivity(mIntent);
    }

    @OnClick(R.id.cv_three)
    public void jump3() {
        mIntent = new Intent(TestActivity_3.this, Activity_03.class);
        jumpToActivity(mIntent);
    }

    @OnClick(R.id.cv_four)
    public void jump4() {
        mIntent = new Intent(TestActivity_3.this, Activity_04.class);
        jumpToActivity(mIntent);
    }

    @OnClick(R.id.cv_five)
    public void jump5() {
        mIntent = new Intent(TestActivity_3.this, Activity_05.class);
        jumpToActivity(mIntent);
    }

    @OnClick(R.id.cv_six)
    public void jump6() {
        mIntent = new Intent(TestActivity_3.this, Activity_06.class);
        jumpToActivity(mIntent);
    }

    @OnClick(R.id.cv_seven)
    public void jump7() {
        mIntent = new Intent(TestActivity_3.this, Activity_07.class);
        jumpToActivity(mIntent);
    }

    @OnClick(R.id.cv_eight)
    public void jump8() {
        mIntent = new Intent(TestActivity_3.this, Activity_08.class);
        jumpToActivity(mIntent);
    }

    @OnClick(R.id.cv_nine)
    public void jump9() {
        mIntent = new Intent(TestActivity_3.this, Activity_09.class);
        jumpToActivity(mIntent);
    }

    @OnClick(R.id.cv_ten)
    public void jump10() {
        mIntent = new Intent(TestActivity_3.this, Activity_10.class);
        jumpToActivity(mIntent);
    }

    @OnClick(R.id.cv_eleven)
    public void jump11() {
        mIntent = new Intent(TestActivity_3.this, Activity_11.class);
        jumpToActivity(mIntent);
    }

    @OnClick(R.id.cv_twelve)
    public void jump12() {
        mIntent = new Intent(TestActivity_3.this, Activity_12.class);
        jumpToActivity(mIntent);
    }

    private void jumpToActivity(Intent mIntent) {
        startActivity(mIntent);
    }
}
