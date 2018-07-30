package cn.yuntk.radio.manager

import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.service.PlayService

/**
 * Author : Gupingping
 * Date : 2018/7/16
 * Mail : gu12pp@163.com
 */
class MediaSessionManager(playService: PlayService) {

    private val TAG = "MediaSessionManager"
    private val MEDIA_SESSION_ACTIONS = (PlaybackStateCompat.ACTION_PLAY
            or PlaybackStateCompat.ACTION_PAUSE
            or PlaybackStateCompat.ACTION_PLAY_PAUSE
            or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            or PlaybackStateCompat.ACTION_STOP
            or PlaybackStateCompat.ACTION_SEEK_TO)

    private var mPlayService = playService
    private var mMediaSession: MediaSessionCompat? = null

    init {
        setupMediaSession()

    }

    private fun setupMediaSession() {
        mMediaSession = MediaSessionCompat(mPlayService, TAG)
        mMediaSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS or MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS)
        mMediaSession!!.setCallback(callback)
        mMediaSession!!.isActive = true
    }

    fun updatePlaybackState() {
//        val state = if (mPlayService.isPlaying() || mPlayService.isPreparing()) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
//        mMediaSession!!.setPlaybackState(
//                PlaybackStateCompat.Builder()
//                        .setActions(MEDIA_SESSION_ACTIONS)
//                        .setState(state, mPlayService.getCurrentPosition(), 1f)
//                        .build())
    }

    fun updateMetaData(music: FMBean?) {
//        if (music == null) {
//            mMediaSession!!.setMetadata(null)
//            return
//        }
//
//        val metaData = MediaMetadataCompat.Builder()
//                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, music!!.getTitle())
//                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, music!!.getArtist())
//                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, music!!.getAlbum())
//                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, music!!.getArtist())
//                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, music!!.getDuration())
//                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, CoverLoader.getInstance().loadThumbnail(music))
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            metaData.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, AppCache.getMusicList().size())
//        }
//
//        mMediaSession!!.setMetadata(metaData.build())
    }

    fun release() {
        mMediaSession!!.setCallback(null)
        mMediaSession!!.isActive = false
        mMediaSession!!.release()
    }

    private val callback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            mPlayService.playPause()
        }

        override fun onPause() {
            mPlayService.playPause()
        }

        override fun onSkipToNext() {
//            mPlayService.next()
        }

        override fun onSkipToPrevious() {
//            mPlayService.prev()
        }

        override fun onStop() {
            mPlayService.stop()
        }

        override fun onSeekTo(pos: Long) {
//            mPlayService.seekTo(pos.toInt())
        }
    }

}