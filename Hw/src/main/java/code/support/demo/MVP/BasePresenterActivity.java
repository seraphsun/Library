package code.support.demo.MVP;

import android.app.Activity;
import android.os.Bundle;

/**
 * 这是一个Acitivty的基类，它包含了Presenter接口，简化了acitivty中对视图的操作
 * Created by Design on 2016/4/8.
 */
public abstract class BasePresenterActivity<T extends BasePresenter> extends Activity {

    protected T mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mPresenter = getPresenterClass().newInstance();
            mPresenter.init(getLayoutInflater(), null);
            setContentView(mPresenter.getView());
            onBindPresenter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyPresenter();
        mPresenter = null;
    }

    /**
     * 子类activity必须通过重写此方法指定一个presenter的实现类
     *
     * @return 返回一个Presenter的实现
     */
    protected abstract Class<T> getPresenterClass();

    protected void onBindPresenter() {
    }

    protected void onDestroyPresenter() {
    }
}
