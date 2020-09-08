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

package ir.batna.messaging.MediaPlayer

import android.media.MediaPlayer
import android.view.View
import android.widget.LinearLayout
import androidx.core.net.toUri
import im.vector.riotx.features.settings.VectorLocale.context
import timber.log.Timber
import java.io.File

class MediaPlayerBatna {

    private lateinit var mediaPlayer: MediaPlayer
    fun startMediaPlayer(file: File, play_layout: LinearLayout) {
        try {
            play_layout.visibility = View.VISIBLE
            mediaPlayer = MediaPlayer.create(context, file.toUri())
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener(MediaPlayer.OnCompletionListener {
                play_layout.visibility = View.GONE
                mediaPlayer.reset()
                mediaPlayer.release()
            })
        } catch (e: Exception) {
            Timber.e(e, "crash")
        }
    }
    fun startMediaPlayer(file: File) {
        try {
            mediaPlayer = MediaPlayer.create(context, file.toUri())
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener(MediaPlayer.OnCompletionListener {
                mediaPlayer.reset()
                mediaPlayer.release()
            })
        } catch (e: Exception) {
            Timber.e(e, "crash")
        }
    }
}
