package com.king.app.coolg.view.widget.video;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.coolg.utils.FormatUtil;

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
public class CoolVideoView extends VideoView {

    private OnVideoListener onVideoListener;

    private boolean isInitVideo = true;

    private boolean isSeekToAfterPrepared;

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

}
