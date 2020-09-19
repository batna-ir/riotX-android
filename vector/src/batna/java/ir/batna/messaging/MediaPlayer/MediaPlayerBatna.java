/*
 * Copyright (c) 2020 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ir.batna.messaging.MediaPlayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Random;

import im.vector.riotx.R;

public class MediaPlayerBatna {
    public static MediaPlayer mp;
    @SuppressLint("StaticFieldLeak")
    @Nullable
    public static LinearLayout layout;
    @SuppressLint("StaticFieldLeak")
    @Nullable
    public static SeekBar seekBar;
    @SuppressLint("StaticFieldLeak")
    @Nullable
    public static ImageView pause;
    @SuppressLint("StaticFieldLeak")
    @Nullable
    public static ImageView play;
    @SuppressLint("StaticFieldLeak")
    @Nullable
    public static ImageView fileImageView;
    @SuppressLint("StaticFieldLeak")
    public static ImageView previousFileImageView;
    @SuppressLint("StaticFieldLeak")
    public static ImageView close;
    private static Handler myHandler = new Handler();
    private static boolean isRemainderVoice = true;
    public static String fileNameIsPlay;
    public static String fileNameIsClick;
    private static Runnable UpdateVoiceTime = new Runnable() {
        public void run() {
            try {
                int startTime = mp.getCurrentPosition();
                seekBar.setProgress(startTime);
                if (isRemainderVoice)
                    myHandler.postDelayed(this, 100);
            } catch (Exception e) {
                isRemainderVoice = false;
            }
        }
    };

    public static void startMediaPlayer(File file, Context context) {
        try {
            if (previousFileImageView != null) {
                previousFileImageView.setImageResource(R.drawable.ic_play_arrow);
            }
            mp = null;
            mp = MediaPlayer.create(context, Uri.parse(file.getPath()));
            mp.start();
            fileNameIsPlay = file.getName();
            isRemainderVoice = true;
            assert seekBar != null;
            seekBar.setMax(mp.getDuration());
            myHandler.postDelayed(UpdateVoiceTime, 100);
            assert fileImageView != null;
            if (mp.isPlaying()) {
                assert pause != null;
                pause.setVisibility(View.VISIBLE);
                assert play != null;
                play.setVisibility(View.GONE);
                fileImageView.setImageResource(R.drawable.ic_pause);
            }
            assert layout != null;
            layout.setVisibility(View.VISIBLE);

            mp.setOnCompletionListener(mp -> {
                mp.release();
                fileImageView.setImageResource(R.drawable.ic_play_arrow);
                isRemainderVoice = false;
                assert layout != null;
                layout.setVisibility(View.GONE);
                Random r = new Random();
                fileNameIsPlay = String.valueOf(r.nextInt() + r.nextDouble());
            });
        } catch (Exception e) {
            mp.release();
            mp = null;
        }
        assert close != null;
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    assert layout != null;
                    layout.setVisibility(View.GONE);
                    mp.stop();
                    Random r = new Random();
                    fileNameIsPlay = String.valueOf(r.nextInt() + r.nextDouble());
                    assert fileImageView != null;
                    fileImageView.setImageResource(R.drawable.ic_play_arrow);
                } catch (Exception ignored) {

                }
            }
        });
        assert pause != null;
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPause();
            }
        });
        assert play != null;
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mp.start();
                    pause.setVisibility(View.VISIBLE);
                    play.setVisibility(View.GONE);
                    assert fileImageView != null;
                    fileImageView.setImageResource(R.drawable.ic_pause);
                } catch (Exception ignore) {

                }
            }
        });
        assert seekBar != null;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mp.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        previousFileImageView = fileImageView;
    }

    public static void setPause() {
        try {
            mp.pause();
            assert play != null;
            play.setVisibility(View.VISIBLE);
            assert pause != null;
            pause.setVisibility(View.GONE);
            assert fileImageView != null;
            fileImageView.setImageResource(R.drawable.ic_play_arrow);
        } catch (Exception ignored) {

        }
    }

    public static void setPlay() {
        try {
            mp.start();
            assert pause != null;
            pause.setVisibility(View.VISIBLE);
            assert play != null;
            play.setVisibility(View.GONE);
            assert fileImageView != null;
            fileImageView.setImageResource(R.drawable.ic_pause);
        } catch (Exception ignore) {

        }
    }

}