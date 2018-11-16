package com.king.app.coolg.view.widget.video;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.king.app.coolg.R;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.coolg.utils.FormatUtil;
import com.king.app.coolg.utils.ScreenUtils;

import tcking.github.com.giraffeplayer2.GiraffePlayer;
import tcking.github.com.giraffeplayer2.PlayerListener;
import tcking.github.com.giraffeplayer2.VideoView;
import tv.danmaku.ijk.media.player.IjkTimedText;

/**
 * Desc: 扩展GiraffePlayer开源框架的VideoView，扩展各控制按钮的事件
 *
 * @author：Jing Yang
 * @date: 2018/11/15 15:22
 */
public class CoolVideoView extends VideoView implements View.OnClickListener {

    private OnVideoListener onVideoListener;

    private OnVideoListListener onVideoListListener;

    private boolean isInitVideo = true;

    private boolean isSeekToAfterPrepared;

    private boolean showRichTools;
    
    private boolean showFullScreen = true;

    public CoolVideoView(@NonNull Context context) {
        super(context);
        expandParent();
    }

    public CoolVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        expandParent();
    }

    public void setOnVideoListener(OnVideoListener onVideoListener) {
        this.onVideoListener = onVideoListener;
    }

    public void setOnVideoListListener(OnVideoListListener onVideoListListener) {
        this.onVideoListListener = onVideoListListener;
    }

    public void setShowRichTools(boolean showRichTools) {
        this.showRichTools = showRichTools;
    }

    public void setShowFullScreen(boolean showFullScreen) {
        this.showFullScreen = showFullScreen;
    }

    private void expandParent() {
        // 覆盖videoView里封装的播放按键的监听事件，处理恢复播放时间的功能
        findViewById(R.id.app_video_play).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GiraffePlayer player = getPlayer();
                if (player.isPlaying()) {
                    pause();
                } else {
                    play();
                }
            }
        });
    }

    public void play() {
        GiraffePlayer player = getPlayer();
        if (onVideoListener == null) {
            player.start();
        }
        else {
            if (isInitVideo && onVideoListener.getStartSeek() > 0) {
                restartOrRestore(player);
            }
            else {
                player.start();
            }
        }
    }

    private void pause() {
        getPlayer().pause();
    }

    private void restartOrRestore(GiraffePlayer player) {
        String message = "This video has been played to " + FormatUtil.formatTime(onVideoListener.getStartSeek()) + " last time. Restore or restart?";
        new AlertDialog.Builder(getContext())
                .setTitle(null)
                .setMessage(message)
                .setPositiveButton("Restore", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isSeekToAfterPrepared = true;
                        isInitVideo = false;
                        player.start();
                    }
                })
                .setNegativeButton("Restart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isSeekToAfterPrepared = false;
                        isInitVideo = false;
                        player.start();
                    }
                })
                .show();
    }

    public void prepare() {
        if (showRichTools) {
            showRichToolIcons();
        }
        if (!showFullScreen) {
            hideFullScreen();
        }
        setPlayerListener(new PlayerListener() {
            @Override
            public void onPrepared(GiraffePlayer giraffePlayer) {
                DebugLog.e("current=" + giraffePlayer.getCurrentPosition() + ", total=" + giraffePlayer.getDuration());

                if (onVideoListener != null) {
                    // seekTo放在这里调比较准确，如果放在setVideoPath或者start()之后不准确
                    if (isSeekToAfterPrepared) {
                        giraffePlayer.seekTo(onVideoListener.getStartSeek());
                    }
                }
            }

            @Override
            public void onBufferingUpdate(GiraffePlayer giraffePlayer, int percent) {
                DebugLog.e("percent " + percent);
                // 按返回键退出页面时只能监听到onRelease，但是在onRelease里获取CurrentPosition已经是0了
                // onBufferingUpdate是一直在执行的，所以在这里仅在内存中更新当前播放位置，再在onRelease时保存到数据库中updateToDb
                // 但是，调试发现如果通过任务栏划掉程序，onRelease不会被执行，所以在activity的onDestroy里再执行一次updateToDb
                // 三重保证最大限度优化记录播放的位置
                if (onVideoListener != null) {
                    onVideoListener.updatePlayPosition(giraffePlayer.getCurrentPosition());
                }
            }

            @Override
            public boolean onInfo(GiraffePlayer giraffePlayer, int what, int extra) {
                DebugLog.e("what " + what + ", extra " + extra);
                return false;
            }

            @Override
            public void onCompletion(GiraffePlayer giraffePlayer) {
                DebugLog.e("current=" + giraffePlayer.getCurrentPosition() + ", total=" + giraffePlayer.getDuration());
                // 播放完毕重置播放位置记录
                if (onVideoListener != null) {
                    onVideoListener.onPlayComplete();
                }
            }

            @Override
            public void onSeekComplete(GiraffePlayer giraffePlayer) {
                DebugLog.e("");
            }

            @Override
            public boolean onError(GiraffePlayer giraffePlayer, int what, int extra) {
                DebugLog.e("what " + what + ", extra " + extra);
                return false;
            }

            @Override
            public void onPause(GiraffePlayer giraffePlayer) {
                DebugLog.e("current=" + giraffePlayer.getCurrentPosition() + ", total=" + giraffePlayer.getDuration());
                if (onVideoListener != null) {
                    onVideoListener.updatePlayPosition(giraffePlayer.getCurrentPosition());
                    onVideoListener.onPause();
                }
            }

            @Override
            public void onRelease(GiraffePlayer giraffePlayer) {
                DebugLog.e("current=" + giraffePlayer.getCurrentPosition() + ", total=" + giraffePlayer.getDuration());
                if (onVideoListener != null) {
                    onVideoListener.onDestroy();
                }
            }

            @Override
            public void onStart(GiraffePlayer giraffePlayer) {
                DebugLog.e("current=" + giraffePlayer.getCurrentPosition() + ", total=" + giraffePlayer.getDuration());
            }

            @Override
            public void onTargetStateChange(int oldState, int newState) {
                DebugLog.e("oldState " + oldState + ", newState " + newState);
            }

            @Override
            public void onCurrentStateChange(int oldState, int newState) {
                DebugLog.e("oldState " + oldState + ", newState " + newState);
            }

            @Override
            public void onDisplayModelChange(int oldModel, int newModel) {
                DebugLog.e("oldModel " + oldModel + ", newModel " + newModel);
            }

            @Override
            public void onPreparing(GiraffePlayer giraffePlayer) {
                DebugLog.e("");
            }

            @Override
            public void onTimedText(GiraffePlayer giraffePlayer, IjkTimedText text) {
                DebugLog.e("");
            }

            @Override
            public void onLazyLoadProgress(GiraffePlayer giraffePlayer, int progress) {
                DebugLog.e("progress " + progress);
            }

            @Override
            public void onLazyLoadError(GiraffePlayer giraffePlayer, String message) {
                DebugLog.e("message " + message);
            }
        });
    }

    private void hideFullScreen() {

        // 想不通为啥下面的代码不起作用
        // 全屏按钮左侧的设置按钮修改为alignParentRight
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) findViewById(R.id.app_video_clarity).getLayoutParams();
//        params.removeRule(RelativeLayout.LEFT_OF);
//        params.removeRule(RelativeLayout.START_OF);
//        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        params.addRule(RelativeLayout.ALIGN_PARENT_END);
//        findViewById(R.id.app_video_clarity).setLayoutParams(params);

        findViewById(R.id.app_video_fullscreen).setVisibility(INVISIBLE);
    }

    private void showRichToolIcons() {
        RelativeLayout bottomBar = findViewById(R.id.app_video_bottom_box);

        LinearLayout tools = new LinearLayout(getContext());
        tools.setId(R.id.cvv_group_rich_tools);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        bottomBar.addView(tools, params);
        params.addRule(RelativeLayout.CENTER_VERTICAL);

        // insert between end time and setting icon
        params.addRule(RelativeLayout.LEFT_OF, R.id.app_video_clarity);
        params.addRule(RelativeLayout.START_OF, R.id.app_video_clarity);
        params = (RelativeLayout.LayoutParams) findViewById(R.id.app_video_endTime).getLayoutParams();
        params.removeRule(RelativeLayout.LEFT_OF);
        params.removeRule(RelativeLayout.START_OF);
        params.addRule(RelativeLayout.LEFT_OF, R.id.cvv_group_rich_tools);
        params.addRule(RelativeLayout.START_OF, R.id.cvv_group_rich_tools);

        // previous
        addIcon(R.drawable.ic_skip_previous_white_24dp, R.id.cvv_iv_previous, tools);
        addIcon(R.drawable.ic_skip_next_white_24dp, R.id.cvv_iv_next, tools);
        addIcon(R.drawable.ic_playlist_play_white_24dp, R.id.cvv_iv_list, tools);
    }

    private void addIcon(int srcId, int id, LinearLayout container) {
        ImageView view = new ImageView(getContext());
        view.setImageResource(srcId);
        view.setPadding(ScreenUtils.dp2px(8), 0, 0 , 0);
        view.setId(id);
        view.setOnClickListener(this);
        container.addView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvv_iv_next:
                if (onVideoListListener != null) {
                    onVideoListListener.playNext();
                }
                break;
            case R.id.cvv_iv_previous:
                if (onVideoListListener != null) {
                    onVideoListListener.playPrevious();
                }
                break;
            case R.id.cvv_iv_list:
                if (onVideoListListener != null) {
                    onVideoListListener.showPlayList();
                }
                break;
        }
    }
}
