package com.ice.data.repo

import android.util.Log
import com.ice.data.mappers.GPSInfoMapper
import com.ice.data.preferences.PreferenceHelper
import com.ice.domain.models.GPSInfoModel
import com.ice.domain.repositories.CredentialsRepo
import javax.inject.Inject

class CredentialsRepoImpl @Inject constructor(
    private val preferenceHelper: PreferenceHelper,
    private val gpsInfoMapper: dagger.Lazy<GPSInfoMapper>
) : CredentialsRepo {

    override fun getFCMToken(): String {
        return preferenceHelper.getFCMToken()
    }

    override fun getEmail(): String {
        return preferenceHelper.getEmail()
    }

    override fun getUserId(): String {
        return preferenceHelper.getUserId()
    }

    override fun getWebLink(): String {
        return if (preferenceHelper.getWebContentLink().isEmpty())
            "https://www.gov.pl/web/koronawirus/wiadomosci"
        else {
            preferenceHelper.getWebContentLink()
        }
    }

    override fun getLastGPSInfo(): GPSInfoModel? {
        gpsInfoMapper.get().toGPSInfo(preferenceHelper.getLastGPSInfoJson())?.let {
            if (System.currentTimeMillis() - it.time < 10*1000) { // return null if older than 10 seconds
                Log.d("Nearby", "return GPS info")
                return it
            } else {
                Log.d("Nearby", "older than 10 seconds, return null")
                return null
            }
        }
        return null
    }

    override fun setFCMToken(fcmToken: String) {
        preferenceHelper.setFCMToken(fcmToken)
    }

    override fun setEmail(email: String) {
        preferenceHelper.setEmail(email)
    }

    override fun setUserId(userId: String) {
        preferenceHelper.setUserId(userId)
    }

    override fun setWebLink(webLink: String) {
        preferenceHelper.setWebContentLink(webLink)
    }

    override fun setLastGPSInfo(geographicCoordinateX: Float?, geographicCoordinateY: Float?, time: Long) {
        if (geographicCoordinateX != null && geographicCoordinateY != null) {
            val gpsString = gpsInfoMapper.get().toStringJson(
                GPSInfoModel(
                    geographicCoordinateX = geographicCoordinateX,
                    geographicCoordinateY = geographicCoordinateY,
                    time = time
                )
            )
            preferenceHelper.setLastGPSInfoJson(gpsString ?: "")
        } else {
            preferenceHelper.setLastGPSInfoJson("")
        }

    }
}