package com.mitra

import android.util.Log
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.mitra.data.dtos.BaseReposnseWithError
import com.mitra.data.dtos.MessageResponse
import com.mitra.data.dtos.SuccessResponse
import com.mitra.data.dtos.UserDto
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

class ApiRetrofit {
    private val retrofitService: ChatRetrofit

    init {
        val retrofit = Retrofit
            .Builder()
            .baseUrl(BuildConfig.HTTP_BASE_URL)
            .addConverterFactory(
                GsonConverterFactory
                    .create(
                        GsonBuilder()
                            .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                            .create()
                    )
            )
            .client(
                OkHttpClient
                    .Builder()
                    .addInterceptor(
                        HttpLoggingInterceptor { message -> Log.e("OkHttp", message) }
                            .setLevel(HttpLoggingInterceptor.Level.BODY)
                    )
                    .build()
            )
            .build()
        retrofitService = retrofit.create(ChatRetrofit::class.java)
    }

    suspend fun checkUser(deviceId: String): UserDto? =
        retrofitService.checkUser(JsonObject().apply {
            addProperty("deviceId", deviceId)
        }).success

    suspend fun sendFirebaseToken(firebaseToken: String, deviceId: String) =
        retrofitService.sendFirebaseToken(JsonObject().apply {
            addProperty("firebase_token", firebaseToken)
            addProperty("device_id", deviceId)
        })

    suspend fun getUnreadedMessages(deviceId: String) =
        retrofitService.getUnreadedMessages(
            JsonObject().apply {
                addProperty("device_id", deviceId)
            }
        ).success

    suspend fun approveLicense(deviceId: String) =
        retrofitService.approveLicense(
            JsonObject().apply {
                addProperty("device_id", deviceId)
            }
        ).success?.success

}

interface ChatRetrofit {
    @POST("/check_user")
    suspend fun checkUser(@Body json: JsonObject): BaseReposnseWithError<UserDto>

    @POST("/firabase_update")
    suspend fun sendFirebaseToken(@Body json: JsonObject): BaseReposnseWithError<Boolean>

    @POST("/get_unreaded_messages")
    suspend fun getUnreadedMessages(@Body json: JsonObject): BaseReposnseWithError<MessageResponse>

    @POST("/approve_license")
    suspend fun approveLicense(@Body json: JsonObject): SuccessResponse
}