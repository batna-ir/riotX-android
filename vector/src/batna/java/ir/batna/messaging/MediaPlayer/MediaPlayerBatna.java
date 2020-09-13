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

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.LinearLayout;

import org.jetbrains.annotations.Nullable;

import java.io.File;

public class MediaPlayerBatna {
    public static MediaPlayer mp;
    @Nullable
    public LinearLayout layout;


    public static void startMediaPlayer(File file, Context context) {
        try {
//            mp.release();
            mp = null;
            mp = MediaPlayer.create(context, Uri.parse(file.getPath()));
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
//                    mp.stop();
                    mp.release();
                    mp = null;
                }
            });
        } catch (Exception e) {
            mp.release();
            mp = null;
        }
    }
}

//        mediaPlayer.setDataSource(context,context.cacheDir.path + "/recording.aac").toUri();
//        mediaPlayer.release();
//        mediaPlayer = MediaPlayer.create(context, Uri.parse(file.getPath()));
//        mediaPlayer.setDataSource(file.getPath());
//                if (mediaPlayer == null) {
////                    mediaPlayer = MediaPlayer.create(context, file.toUri())
//
//
//                    mediaPlayer?.start()
//                    mediaPlayer?.setOnCompletionListener(MediaPlayer.OnCompletionListener {
////            layout.visibility = View.GONE
//                        mediaPlayer?.reset()
//                        mediaPlayer?.release()
//                    })
//                }
//               else {
//            if (mediaPlayer.isPlaying()) {
//                mediaPlayer.stop();
//                mediaPlayer.reset();
//                mediaPlayer.release();
//            }
//                }
//                mediaPlayer.setDataSource(file.path)
//        layout.visibility = View.VISIBLE
//        mediaPlayer.start();
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                mediaPlayer.reset();
//                mediaPlayer.release();
//            }
//        });


