package cn.yuntk.radio.manager;

import java.util.Stack;

import cn.yuntk.radio.base.BaseActivity;

/**
 * Author : Gupingping
 * Date : 2018/7/24
 * Mail : gu12pp@163.com
 */
public class ActivityStack {
    private static ActivityStack instance;

    private Stack<BaseActivity> activityStack;

    private ActivityStack() {
        activityStack = new Stack<>();
    }

    /**
     * 获得堆栈实例
     * @return
     */
    public static ActivityStack getInstance() {
        if (instance == null) {
            instance = new ActivityStack();
        }
        return instance;
    }

    /**
     * 当前activity
     * @return
     */
    public BaseActivity currentActivity() {
        if (activityStack == null || activityStack.size() == 0) {
            return null;
        }
        return activityStack.lastElement();
    }
}
