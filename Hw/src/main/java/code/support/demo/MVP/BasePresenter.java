package code.support.demo.MVP;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 这是一个对视图初始化及处理的接口
 * Created by Design on 2016/4/8.
 */
public interface BasePresenter {

    void init(LayoutInflater inflater, ViewGroup container);

    View getView();
}
