/*
 * Copyright 2019 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.riotx.features.home.room.detail.timeline.item

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.riotx.BuildConfig
import im.vector.riotx.R
import im.vector.riotx.features.home.room.detail.timeline.helper.ContentDownloadStateTrackerBinder
import im.vector.riotx.features.home.room.detail.timeline.helper.ContentUploadStateTrackerBinder
import ir.batna.messaging.MediaPlayer.MediaPlayerBatna

@EpoxyModelClass(layout = R.layout.item_timeline_event_base)
abstract class MessageFileItem : AbsMessageItem<MessageFileItem.Holder>() {

    @EpoxyAttribute
    var filename: CharSequence = ""

    @EpoxyAttribute
    var mxcUrl: String = ""

    @EpoxyAttribute
    @DrawableRes
    var iconRes: Int = 0

//    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
//    var clickListener: View.OnClickListener? = null

    @EpoxyAttribute
    var izLocalFile = false

    @EpoxyAttribute
    var izDownloaded = false

    @EpoxyAttribute
    lateinit var contentUploadStateTrackerBinder: ContentUploadStateTrackerBinder

    @EpoxyAttribute
    lateinit var contentDownloadStateTrackerBinder: ContentDownloadStateTrackerBinder

    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("SetTextI18n")
    override fun bind(holder: Holder) {
        super.bind(holder)
        renderSendState(holder.fileLayout, holder.filenameView)
        if (!attributes.informationData.sendState.hasFailed()) {
            contentUploadStateTrackerBinder.bind(attributes.informationData.eventId, izLocalFile, holder.progressLayout)
        } else {
            holder.fileImageView.setImageResource(R.drawable.ic_cross)
            holder.progressLayout.isVisible = false
        }
        holder.filenameView.text = filename
        /**
         * BATNA => Set text color file name
         */
        if (BuildConfig.IS_BATNA){
            holder.filenameView.setTextColor(Color.BLACK)
        }else{
            holder.filenameView.setTextColor(Color.WHITE)
        }
        if (attributes.informationData.sendState.isSending()) {
            holder.fileImageView.setImageResource(iconRes)
        } else {
            if (izDownloaded) {
                holder.fileImageView.setImageResource(iconRes)
                holder.fileDownloadProgress.progress = 100
            } else {
                contentDownloadStateTrackerBinder.bind(mxcUrl, holder)
                holder.fileImageView.setImageResource(R.drawable.ic_download)
                holder.fileDownloadProgress.progress = 0
            }
        }
        if (BuildConfig.IS_BATNA){
            try{
            if (holder.filenameView.text.contains(".aac")) {
                holder.fileImageView.setImageResource(R.drawable.ic_play_arrow)
                holder.fileImageView.scaleY= 1.5F
                holder.fileImageView.scaleX= 1.5F
                if (MediaPlayerBatna.fileNameIsPlay==holder.filenameView.text && MediaPlayerBatna.mp.isPlaying){
                    holder.fileImageView.setImageResource(R.drawable.ic_baseline_pause_24)
                    MediaPlayerBatna.fileImageView=holder.fileImageView
                } else if (MediaPlayerBatna.fileNameIsPlay==holder.filenameView.text){
                    MediaPlayerBatna.fileImageView=holder.fileImageView
                }
                holder.filenameView.visibility=View.GONE
                if (attributes.informationData.sentByMe) {
                    holder.layoutItemTimeLineBase.layoutDirection = View.LAYOUT_DIRECTION_RTL
                    holder.memberNameView.visibility = View.GONE
                    holder.viewStubContainer.setBackgroundResource(R.drawable.in_audio_message_shape)
                } else {
                    holder.layoutItemTimeLineBase.layoutDirection = View.LAYOUT_DIRECTION_LTR
                    holder.viewStubContainer.setBackgroundResource(R.drawable.out_message_shape)
                }
            }
            }catch (e:Exception){
            }
        }
        holder.filenameView.setOnClickListener(attributes.itemClickListener)
        holder.filenameView.setOnLongClickListener(attributes.itemLongClickListener)
        holder.fileImageWrapper.setOnClickListener(attributes.itemClickListener)
        holder.fileImageWrapper.setOnLongClickListener(attributes.itemLongClickListener)
        holder.filenameView.paintFlags = (holder.filenameView.paintFlags or Paint.UNDERLINE_TEXT_FLAG)
        /**
         * BATNA => Change layout direction and background via 'attributes.informationData.sentByMe'
         */
        if (BuildConfig.IS_BATNA && !holder.filenameView.text.contains(".aac")) {
            if (attributes.informationData.sentByMe) {
                holder.layoutItemTimeLineBase.layoutDirection = View.LAYOUT_DIRECTION_RTL
                holder.memberNameView.visibility = View.GONE
                holder.viewStubContainer.setBackgroundResource(R.drawable.in_message_shape)
            } else {
                holder.layoutItemTimeLineBase.layoutDirection = View.LAYOUT_DIRECTION_LTR
                holder.viewStubContainer.setBackgroundResource(R.drawable.out_message_shape)
            }
        }
    }

    override fun unbind(holder: Holder) {
        super.unbind(holder)
        contentUploadStateTrackerBinder.unbind(attributes.informationData.eventId)
        contentDownloadStateTrackerBinder.unbind(mxcUrl)
    }

    override fun getViewType() = STUB_ID

    class Holder : AbsMessageItem.Holder(STUB_ID) {
        val progressLayout by bind<ViewGroup>(R.id.messageFileUploadProgressLayout)
        val fileLayout by bind<ViewGroup>(R.id.messageFileLayout)
        val fileImageView by bind<ImageView>(R.id.messageFileIconView)
        val fileImageWrapper by bind<ViewGroup>(R.id.messageFileImageView)
        val fileDownloadProgress by bind<ProgressBar>(R.id.messageFileProgressbar)
        val filenameView by bind<TextView>(R.id.messageFilenameView)
    }

    companion object {
        private const val STUB_ID = R.id.messageContentFileStub
    }
}
