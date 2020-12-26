package com.ice.data.mappers

import com.ice.data.models.UserJson
import com.ice.domain.models.UserModel
import javax.inject.Inject

class UserMapper @Inject constructor() {

    fun toUserModel(userJson: UserJson): UserModel {
        return UserModel(
            email = userJson.email,
            userId = userJson.userId,
            fcmToken = userJson.fcmToken,
            mainWebSiteUrl = userJson.mainWebSiteUrl
        )
    }

}