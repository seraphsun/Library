package code.support.demo.widget.refresh.refresh_11;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import code.support.demo.R;

/**
 * Created by Design on 2016/5/12.
 */
public class FooterView extends LinearLayout {

    private Context mContext;
    private RelativeLayout footerContent;

    private LinearLayout layoutProgress;
    private ProgressBar progressbar;
    private TextView progressText;

    private TextView footerText;

    public enum STATE {
        normal,
        ready,
        loading
    }

    public FooterView(Context context) {
        super(context);
        init(context);
    }

    public FooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View moreView = LayoutInflater.from(mContext).inflate(R.layout.refresh_11_footer, null);
        addView(moreView);
        moreView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        footerContent = (RelativeLayout) moreView.findViewById(R.id.footer_content);

        layoutProgress = (LinearLayout) moreView.findViewById(R.id.progressLayout);
        progressbar = (ProgressBar) moreView.findViewById(R.id.progressbar);
        progressText = (TextView) moreView.findViewById(R.id.progressText);

        footerText = (TextView) moreView.findViewById(R.id.footerText);
    }

    public void setState(STATE state) {
        layoutProgress.setVisibility(View.INVISIBLE);
        footerText.setVisibility(View.INVISIBLE);

        if (state == STATE.ready) {
            footerText.setVisibility(View.VISIBLE);
            footerText.setText("松开载入更多");
        } else if (state == STATE.loading) {
            layoutProgress.setVisibility(View.VISIBLE);
        } else {
            footerText.setVisibility(View.VISIBLE);
            footerText.setText("查看更多");
        }
    }

    public void setBottomMargin(int height) {
        if (height < 0) return;
        LayoutParams lp = (LayoutParams) footerContent.getLayoutParams();
        lp.bottomMargin = height;
        footerContent.setLayoutParams(lp);
    }

    public int getBottomMargin() {
        LayoutParams lp = (LayoutParams) footerContent.getLayoutParams();
        return lp.bottomMargin;
    }

    public void hide() {
        LayoutParams lp = (LayoutParams) footerContent.getLayoutParams();
        lp.height = 0;
        footerContent.setLayoutParams(lp);
    }

    public void show() {
        LayoutParams lp = (LayoutParams) footerContent.getLayoutParams();
        lp.height = LayoutParams.WRAP_CONTENT;
        footerContent.setLayoutParams(lp);
    }

}
