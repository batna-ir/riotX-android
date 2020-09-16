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
    public static ImageView close;
    private static Handler myHandler = new Handler();
    private static boolean isRemainderVoice = true;
    public static String fileName;
    private static Runnable UpdateVoiceTime = new Runnable() {
        public void run() {
            try {
                int startTime = mp.getCurrentPosition();
                seekBar.setProgress(startTime);
                if (isRemainderVoice)
                    myHandler.postDelayed(this, 100);
            } catch (Exception e) {
                isRemainderVoice=false;
            }
        }
    };

    public static void startMediaPlayer(File file, Context context) {
        try {
            assert pause != null;
            pause.setVisibility(View.VISIBLE);
            assert play != null;
            play.setVisibility(View.GONE);
            mp = null;
            mp = MediaPlayer.create(context, Uri.parse(file.getPath()));
            mp.start();
            fileName=file.getName();
            isRemainderVoice = true;
            assert seekBar != null;
            seekBar.setMax(mp.getDuration());
            myHandler.postDelayed(UpdateVoiceTime, 100);
            assert fileImageView != null;
            fileImageView.setImageResource(R.drawable.ic_pause);
            assert layout != null;
            layout.setVisibility(View.VISIBLE);

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    mp = null;
                    isRemainderVoice = false;
                    assert layout != null;
                    layout.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            mp.release();
            mp = null;
        }
        assert close != null;
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert layout != null;
                layout.setVisibility(View.GONE);
                mp.stop();
            }
        });
        assert pause != null;
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.pause();
                assert play != null;
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
            }
        });
        assert play != null;
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
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
    }
}