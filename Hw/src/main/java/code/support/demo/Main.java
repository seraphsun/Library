package code.support.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import code.support.demo.widget.BodyView;

public class Main extends AppCompatActivity {

    private BodyView mBodyView;
    private TextView mChoose;
    private RadioGroup mRadioGroup;
    private CheckBox mCheckBox;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main);

        mBodyView = (BodyView) findViewById(R.id.bodyView);
        mChoose = (TextView) findViewById(R.id.tv_choose_symptom);
        mRadioGroup = (RadioGroup) findViewById(R.id.rgGuide);
        mCheckBox = (CheckBox) findViewById(R.id.cbFront);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbMale:
                        mBodyView.setCrowd(BodyView.Crowd.MAN);
                        break;
                    case R.id.rbFemale:
                        mBodyView.setCrowd(BodyView.Crowd.WOMAN);
                        break;
                    case R.id.rbChild:
                        mBodyView.setCrowd(BodyView.Crowd.KID);
                        break;
                }
            }
        });

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBodyView.setBack(isChecked);
            }
        });

        mChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("返回".equals(mChoose.getText().toString())) {
                    mBodyView.setHead(false);
                    mChoose.setText("选择其他症状");
                    mRadioGroup.setVisibility(View.VISIBLE);
                    mCheckBox.setVisibility(View.VISIBLE);
                }
            }
        });

        mBodyView.setBodySelectedListener(new BodyView.BodyViewListener() {
            @Override
            public void onClickBodyPart(BodyView.BodyPart bodyPart) {
                if (bodyPart == BodyView.BodyPart.UNKNOWN) {
                    mChoose.setText("返回");
                    mRadioGroup.setVisibility(View.INVISIBLE);
                    mCheckBox.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ("返回".equals(mChoose.getText().toString())) {
                mChoose.performClick();
            } else {
                finish();
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
