package cn.yuntk.radio.play;

import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import cn.yuntk.radio.R;
import cn.yuntk.radio.bean.FMBean;
import cn.yuntk.radio.service.PlayService;
import cn.yuntk.radio.utils.Lg;


/**
 * Created by hzwangchenyan on 2017/8/8.
 */
public class MediaSessionManager {
    private static final String TAG = "MediaSessionManager";
    private static final long MEDIA_SESSION_ACTIONS = PlaybackStateCompat.ACTION_PLAY
            | PlaybackStateCompat.ACTION_PAUSE
            | PlaybackStateCompat.ACTION_PLAY_PAUSE
            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            | PlaybackStateCompat.ACTION_STOP
            | PlaybackStateCompat.ACTION_SEEK_TO;

    private PlayService playService;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;

    public static MediaSessionManager get() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static MediaSessionManager instance = new MediaSessionManager();
    }

    private MediaSessionManager() {
    }

    public void init(PlayService playService) {
        this.playService = playService;
        setupMediaSession();
    }

    private void setupMediaSession() {
        mediaSession = new MediaSessionCompat(playService, TAG);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        //设置监听回调
        mediaSession.setCallback(callback);
        //监听的事件（播放，暂停，上一曲，下一曲）
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(MEDIA_SESSION_ACTIONS);
        mediaSession.setPlaybackState(stateBuilder.build());
        //必须设置为true，这样才能开始接收各种信息
        mediaSession.setActive(true);
    }

    public void updatePlaybackState(int playState) {
        Lg.e("MediaSessionManager updatePlaybackState");

        int state = (playService.isPlaying() || playService.isPreparing()) ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;

        stateBuilder.setState(state, playService.getCurrentIndex(), 1.0f);
        mediaSession.setPlaybackState(stateBuilder.build());
    }

    public void updateMetaData(FMBean fmBean) {
        if (fmBean == null) {
            mediaSession.setMetadata(null);
            return;
        }

        MediaMetadataCompat.Builder metaData = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, fmBean.getName())
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, BitmapFactory.decodeResource(playService.getResources(), R.mipmap.ic_launcher));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            metaData.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, playService.getPageList().size());
        }

        mediaSession.setMetadata(metaData.build());
    }

    private MediaSessionCompat.Callback callback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            Lg.e("MediaSessionCompat callback onPlay");
            playService.playPause();

        }

        @Override
        public void onPause() {
            Lg.e("MediaSessionCompat callback onPause");
            playService.playPause();

        }

        @Override
        public void onSkipToNext() {
            Lg.e("MediaSessionCompat callback onSkipToNext");
            playService.next(null);

        }

        @Override
        public void onSkipToPrevious() {
            Lg.e("MediaSessionCompat callback onSkipToPrevious");
            playService.pre(null);

        }

        @Override
        public void onStop() {
            Lg.e("MediaSessionCompat callback onStop");
            playService.stop();

        }

        @Override
        public void onSeekTo(long pos) {

        }
    };
}
