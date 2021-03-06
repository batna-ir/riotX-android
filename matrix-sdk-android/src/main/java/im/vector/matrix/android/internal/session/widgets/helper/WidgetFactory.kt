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

package im.vector.matrix.android.internal.session.widgets.helper

import im.vector.matrix.android.api.session.events.model.Event
import im.vector.matrix.android.api.session.events.model.toModel
import im.vector.matrix.android.api.session.room.sender.SenderInfo
import im.vector.matrix.android.api.session.widgets.model.Widget
import im.vector.matrix.android.api.session.widgets.model.WidgetContent
import im.vector.matrix.android.api.session.widgets.model.WidgetType
import im.vector.matrix.android.internal.di.SessionDatabase
import im.vector.matrix.android.internal.di.UserId
import im.vector.matrix.android.internal.session.room.membership.RoomMemberHelper
import im.vector.matrix.android.internal.session.user.UserDataSource
import io.realm.Realm
import io.realm.RealmConfiguration
import java.net.URLEncoder
import javax.inject.Inject

internal class WidgetFactory @Inject constructor(@SessionDatabase private val realmConfiguration: RealmConfiguration,
                                                 private val userDataSource: UserDataSource,
                                                 @UserId private val userId: String) {

    fun create(widgetEvent: Event): Widget? {
        val widgetContent = widgetEvent.content.toModel<WidgetContent>()
        if (widgetContent?.url == null) return null
        val widgetId = widgetEvent.stateKey ?: return null
        val type = widgetContent.type ?: return null
        val senderInfo = if (widgetEvent.senderId == null || widgetEvent.roomId == null) {
            null
        } else {
            Realm.getInstance(realmConfiguration).use {
                val roomMemberHelper = RoomMemberHelper(it, widgetEvent.roomId)
                val roomMemberSummaryEntity = roomMemberHelper.getLastRoomMember(widgetEvent.senderId)
                SenderInfo(
                        userId = widgetEvent.senderId,
                        displayName = roomMemberSummaryEntity?.displayName,
                        isUniqueDisplayName = roomMemberHelper.isUniqueDisplayName(roomMemberSummaryEntity?.displayName),
                        avatarUrl = roomMemberSummaryEntity?.avatarUrl
                )
            }
        }
        val isAddedByMe = widgetEvent.senderId == userId
        val computedUrl = widgetContent.computeURL(widgetEvent.roomId)
        return Widget(
                widgetContent = widgetContent,
                event = widgetEvent,
                widgetId = widgetId,
                senderInfo = senderInfo,
                isAddedByMe = isAddedByMe,
                computedUrl = computedUrl,
                type = WidgetType.fromString(type)
        )
    }

    private fun WidgetContent.computeURL(roomId: String?): String? {
        var computedUrl = url ?: return null
        val myUser = userDataSource.getUser(userId)
        computedUrl = computedUrl
                .replace("\$matrix_user_id", userId)
                .replace("\$matrix_display_name", myUser?.displayName ?: userId)
                .replace("\$matrix_avatar_url", myUser?.avatarUrl ?: "")

        if (roomId != null) {
            computedUrl = computedUrl.replace("\$matrix_room_id", roomId)
        }
        for ((key, value) in data) {
            if (value is String) {
                computedUrl = computedUrl.replace("$$key", URLEncoder.encode(value, "utf-8"))
            }
        }
        return computedUrl
    }
}
