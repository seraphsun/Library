package code.support.demo.MVP;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Design on 2016/4/8.
 */
public abstract class BasePresenterFragment<T extends BasePresenter> extends Fragment {

    protected T mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        try {
            mPresenter = getPresenterClass().newInstance();
            mPresenter.init(inflater, container);
            onBindPresenter();
            view = mPresenter.getView();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onDestroyPresenter();
        mPresenter = null;
    }

    /**
     * 子类fragment必须通过重写此方法指定一个presenter的实现类
     *
     * @return 返回一个Presenter的实现
     */
    protected abstract Class<T> getPresenterClass();

    /**
     * 重写此方法表示子类的mPresenter所要进行的操作，具体实现见Presenter实现类
     */
    protected void onBindPresenter() {
    }

    /**
     * 重写此方法表示子类的mPresenter所要进行的操作，具体实现见Presenter实现类
     */
    protected void onDestroyPresenter() {
    }
}
