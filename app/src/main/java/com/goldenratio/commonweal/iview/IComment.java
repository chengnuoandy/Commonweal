package com.goldenratio.commonweal.iview;

import android.widget.ListView;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * Created by 冰封承諾Andy on 2016/8/20 0020.
 * 评论的接口
 */
public interface IComment {

    //评论发送逻辑回调
    void upComment(String objID, String content);

    //评论拉取回调
    void Show(ListView listView, BGARefreshLayout refreshLayout);
}
