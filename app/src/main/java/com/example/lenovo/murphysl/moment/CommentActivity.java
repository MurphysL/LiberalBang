package com.example.lenovo.murphysl.moment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.lenovo.murphysl.MyApplication;
import com.example.lenovo.murphysl.R;
import com.example.lenovo.murphysl.UserInfoActivity;
import com.example.lenovo.murphysl.adapter.CommentAdapter;
import com.example.lenovo.murphysl.base.ParentWithNaviActivity;
import com.example.lenovo.murphysl.bean.Comment;
import com.example.lenovo.murphysl.bean.QiangYu;
import com.example.lenovo.murphysl.bean.UserBean;
import com.example.lenovo.murphysl.model.UserModel;
import com.example.lenovo.murphysl.util.ActivityUtil;
import com.example.lenovo.murphysl.util.Constant;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class CommentActivity extends ParentWithNaviActivity {

    @Bind(R.id.commit_scroll)
    ScrollView commitScroll;
    @Bind(R.id.area_commit)
    LinearLayout areaCommit;
    @Bind(R.id.loadmore)
    TextView footer;
    @Bind(R.id.item_action_comment)
    TextView itemActionComment;
    @Bind(R.id.comment_content)
    EditText commentContent;
    @Bind(R.id.item_action_love)
    TextView love;
    @Bind(R.id.user_name)
    TextView userName;
    @Bind(R.id.content_text)
    TextView commentItemContent;
    @Bind(R.id.content_image)
    ImageView commentItemImage;
    @Bind(R.id.comment_list)
    ListView commentList;
    @Bind(R.id.comment_commit)
    Button commentCommit;
    @Bind(R.id.user_logo)
    ImageView userLogo;

    private CommentAdapter mAdapter;

    private UserBean user = UserModel.getInstance().getUser();
    private QiangYu qiangYu;
    private String commentEdit = "";
    private List<Comment> comments = new ArrayList<Comment>();
    private int pageNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        qiangYu = (QiangYu) getIntent().getSerializableExtra("data");
        initListView();
        initMoodView(qiangYu);
        fetchComment();
    }

    private void initListView() {
        mAdapter = new CommentAdapter(CommentActivity.this, comments);
        commentList.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(commentList);
        commentList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                ActivityUtil.show(CommentActivity.this, "po" + position);
            }
        });
        commentList.setCacheColorHint(0);
        commentList.setScrollingCacheEnabled(false);
        commentList.setScrollContainer(false);
        commentList.setFastScrollEnabled(true);
        commentList.setSmoothScrollbarEnabled(true);
    }

    private void initMoodView(QiangYu mood2) {
        if (mood2 == null) {
            return;
        }
        userName.setText(qiangYu.getAuthor().getUsername());
        commentItemContent.setText(qiangYu.getContent());
        if (null == qiangYu.getContentfigureurl()) {
            commentItemImage.setVisibility(View.GONE);
        } else {
            commentItemImage.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(
                    qiangYu.getContentfigureurl().getFileUrl(
                            CommentActivity.this) == null ? "" : qiangYu
                            .getContentfigureurl().getFileUrl(
                                    CommentActivity.this),
                    commentItemImage,
                    MyApplication.getINSTANCE().getOptions(
                            R.drawable.bg_pic_loading),
                    new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingComplete(String imageUri,
                                                      View view, Bitmap loadedImage) {
                            // TODO Auto-generated method stub
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            float[] cons = ActivityUtil.getBitmapConfiguration(CommentActivity.this,
                                    loadedImage, commentItemImage, 1.0f);
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                    (int) cons[0], (int) cons[1]);
                            layoutParams.addRule(RelativeLayout.BELOW,
                                    R.id.content_text);
                            commentItemImage.setLayoutParams(layoutParams);
                        }

                    });
        }

        love.setText(qiangYu.getLove() + "");
        if (qiangYu.getMyLove()) {
            love.setTextColor(Color.parseColor("#D95555"));
        } else {
            love.setTextColor(Color.parseColor("#000000"));
        }

        UserBean user = qiangYu.getAuthor();
        String avatar = user.getAvatar();
        if (null != avatar) {
            ImageLoader.getInstance().displayImage(
                    avatar, userLogo,
                    MyApplication.getINSTANCE().getOptions(
                            R.drawable.content_image_default),
                    new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingComplete(String imageUri,
                                                      View view, Bitmap loadedImage) {
                            // TODO Auto-generated method stub
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            log("load personal icon completed.");
                        }

                    });
        }
    }


    @OnClick({R.id.user_logo, R.id.item_action_comment, R.id.item_action_love, R.id.loadmore, R.id.comment_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_logo:
                onClickUserLogo();
                break;
            case R.id.item_action_comment:
                onClickComment();
                break;
            case R.id.item_action_love:
                onClickLove();
                break;
            case R.id.loadmore:
                fetchComment();
                break;
            case R.id.comment_commit:
                publishComment();
                break;
        }
    }

    /**
     * 获取评论
     */
    private void fetchComment() {
        BmobQuery<Comment> query = new BmobQuery<Comment>();
        query.addWhereRelatedTo("relation", new BmobPointer(qiangYu));
        query.include("user");
        query.order("createdAt");
        query.setLimit(Constant.NUMBERS_PER_PAGE);
        query.setSkip(Constant.NUMBERS_PER_PAGE * (pageNum++));
        query.findObjects(this, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> data) {
                log("get comment success!" + data.size());

                if (data.size() != 0 && data.get(data.size() - 1) != null) {

                    if (data.size() < Constant.NUMBERS_PER_PAGE) {
                        toast("暂无更多评论");
                        footer.setText("暂无更多评论");
                    }

                    mAdapter.getDataList().addAll(data);
                    mAdapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(commentList);
                    log("refresh");
                } else {
                    toast("暂无更多评论");
                    footer.setText("暂无更多评论");
                    pageNum--;
                }
            }

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                ActivityUtil.show(CommentActivity.this, "获取评论失败，请检查网络");
                pageNum--;
            }
        });
    }

    private void onClickUserLogo() {
        // 跳转到个人信息界面
        UserBean currentUser = BmobUser.getCurrentUser(this, UserBean.class);
        if (currentUser != null) {
            startActivity(new Intent(CommentActivity.this, UserInfoActivity.class));
        }
    }

    private void publishComment() {
        commentEdit = commentContent.getText().toString().trim();
        if (TextUtils.isEmpty(commentEdit)) {
            toast("评论内容不能为空");
            return;
        }

        final Comment comment = new Comment();
        comment.setUser(user);
        comment.setCommentContent(commentEdit);
        comment.save(this, new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                ActivityUtil.show(CommentActivity.this, "评论成功。");
                if (mAdapter.getDataList().size() < Constant.NUMBERS_PER_PAGE) {
                    mAdapter.getDataList().add(comment);
                    mAdapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(commentList);
                }
                commentContent.setText("");
                hideSoftInput();

                // 将该评论与强语绑定到一起
                BmobRelation relation = new BmobRelation();
                relation.add(comment);
                qiangYu.setRelation(relation);
                qiangYu.update(CommentActivity.this, new UpdateListener() {

                    @Override
                    public void onSuccess() {
                        log("更新评论成功");
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        log("更新评论失败。" + arg1);
                    }
                });

            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                ActivityUtil.show(CommentActivity.this, "评论失败。请检查网络~");
            }
        });
    }

    private void onClickLove() {
        if (qiangYu.getMyLove()) {
            toast("您已经赞过啦");
            return;
        }
        qiangYu.setLove(qiangYu.getLove() + 1);
        love.setTextColor(Color.parseColor("#D95555"));
        love.setText(qiangYu.getLove() + "");
        qiangYu.increment("love", 1);
        qiangYu.update(CommentActivity.this, new UpdateListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                qiangYu.setMyLove(true);
                //DatabaseUtil.getInstance(CommentActivity.this).insertFav(qiangYu);

                ActivityUtil.show(CommentActivity.this, "点赞成功~");
            }

            @Override
            public void onFailure(int arg0, String arg1) {
            }
        });
    }

    private void onClickComment() {
        commentContent.requestFocus();
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(commentContent, 0);
    }

    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(commentContent.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.PUBLISH_COMMENT:
                    // 登录完成
                    commentCommit.performClick();
                    break;
                case Constant.GO_SETTINGS:
                    userLogo.performClick();
                    break;
                default:
                    break;
            }
        }

    }

    /***
     * 动态设置listview的高度 item 总布局必须是linearLayout
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1))
                + 15;
        listView.setLayoutParams(params);
    }

    @Override
    protected String title() {
        return "评论";
    }

    @Override
    public Object left() {
        return R.drawable.base_action_bar_back_bg_selector;
    }

    @Override
    public ParentWithNaviActivity.ToolBarListener setToolBarListener() {
        return new ParentWithNaviActivity.ToolBarListener() {
            @Override
            public void clickLeft() {
                finish();
            }

            @Override
            public void clickRight() {
            }
        };
    }
}
