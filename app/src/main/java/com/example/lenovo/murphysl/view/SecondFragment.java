package com.example.lenovo.murphysl.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.lenovo.murphysl.ChatActivity;
import com.example.lenovo.murphysl.NewChatActivity;
import com.example.lenovo.murphysl.NewFriendActivity;
import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.SearchUserActivity;
import com.example.lenovo.murphysl.adapter.ContactAdapter;
import com.example.lenovo.murphysl.adapter.OnRecyclerViewListener;
import com.example.lenovo.murphysl.adapter.base.MyMutlipleItem;
import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.example.lenovo.murphysl.base.ParentWithNaviFragment;
import com.example.lenovo.murphysl.bean.Friend;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.event.RefreshEvent;
import com.example.lenovo.murphysl.model.UserModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

/**
 * SecondFragment
 *
 * @author: lenovo
 * @time: 2016/8/12 21:00
 */

public class SecondFragment extends ParentWithNaviFragment {

    @Bind(R.id.rc_view)
    RecyclerView rc_view;

    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout sw_refresh;

    ContactAdapter adapter;
    LinearLayoutManager layoutManager;

    @Override
    protected String title() {
        return "联系人";
    }

    @Override
    public Object right() {
        return R.drawable.base_action_bar_add_bg_selector;
    }

    @Override
    public ParentWithNaviActivity.ToolBarListener setToolBarListener() {
        return new ParentWithNaviActivity.ToolBarListener() {
            @Override
            public void clickLeft() {

            }

            @Override
            public void clickRight() {
                startActivity(SearchUserActivity.class,null);
            }
        };
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView =inflater.inflate(R.layout.fragment_s, container, false);
        initNaviView();
        ButterKnife.bind(this, rootView);
        MyMutlipleItem<Friend> mutlipleItem = new MyMutlipleItem<Friend>() {

            @Override
            public int getItemViewType(int postion, Friend friend) {
                if(postion==0){
                    return ContactAdapter.TYPE_NEW_FRIEND;
                }else{
                    return ContactAdapter.TYPE_ITEM;
                }
            }

            @Override
            public int getItemLayoutId(int viewtype) {
                if(viewtype== ContactAdapter.TYPE_NEW_FRIEND){
                    return R.layout.header_new_friend;
                }else{
                    return R.layout.item_contact;
                }
            }

            @Override
            public int getItemCount(List<Friend> list) {
                return list.size()+1;
            }
        };
        adapter = new ContactAdapter(getActivity(),mutlipleItem,null);
        rc_view.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(getActivity());
        rc_view.setLayoutManager(layoutManager);
        sw_refresh.setEnabled(true);
        setListener();
        return rootView;
    }

    private void setListener(){
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                sw_refresh.setRefreshing(true);
                query();
            }
        });
        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query();
            }
        });
        adapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                if (position == 0) {//跳转到新朋友页面
                    startActivity(NewFriendActivity.class, null);
                } else {
                    Friend friend = adapter.getItem(position);
                    UserBean user = friend.getFriendUser();
                    BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar());
                    //启动一个会话，实际上就是在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
                    BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, null);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("c", c);
                    startActivity(NewChatActivity.class, bundle);
                }
            }

            @Override
            public boolean onItemLongClick(final int position) {
                log("长按" + position);
                if(position==0){
                    return true;
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("警告");
                dialog.setMessage("确定要删除该好友吗？");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserModel.getInstance().deleteFriend(adapter.getItem(position), new DeleteListener() {
                            @Override
                            public void onSuccess() {
                                toast("好友删除成功");
                                adapter.remove(position);
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                toast("好友删除失败：" + i + ",s =" + s);
                            }
                        });
                    }
                });
                dialog.setNegativeButton("NO" ,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();

                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        sw_refresh.setRefreshing(true);
        query();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**注册自定义消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(RefreshEvent event){
        //重新刷新列表
        log("---联系人界面接收到自定义消息---");
        adapter.notifyDataSetChanged();
    }

    /**
     查询本地会话
     */
    public void query(){
        UserModel.getInstance().queryFriends(new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                adapter.bindDatas(list);
                adapter.notifyDataSetChanged();
                sw_refresh.setRefreshing(false);
            }

            @Override
            public void onError(int i, String s) {
                adapter.bindDatas(null);
                adapter.notifyDataSetChanged();
                sw_refresh.setRefreshing(false);
            }
        });
    }
}
