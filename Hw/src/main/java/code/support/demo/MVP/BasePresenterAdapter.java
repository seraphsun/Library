package code.support.demo.MVP;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by Design on 2016/4/8.
 */
public abstract class BasePresenterAdapter<T extends BasePresenter> extends BaseAdapter {

    protected T mPresenter;

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            try {
                mPresenter = getPresenterClass().newInstance();
                mPresenter.init(inflater, parent);
                convertView = mPresenter.getView();
                convertView.setTag(mPresenter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mPresenter = (T) convertView.getTag();
        }
        if (convertView != null) {
            onBindItemPresenter(position);
        }

        return convertView;
    }

    protected abstract Class<T> getPresenterClass();

    protected abstract void onBindItemPresenter(int position);
}
