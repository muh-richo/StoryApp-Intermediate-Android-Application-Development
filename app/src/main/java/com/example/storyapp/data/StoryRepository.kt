package com.example.storyapp.data

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.*
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.local.room.remotemediator.StoryRemoteMediator
import com.example.storyapp.data.remote.Result
import com.example.storyapp.data.remote.response.*
import com.example.storyapp.data.remote.retrofit.ApiService
import com.google.gson.Gson
import okhttp3.*
import retrofit2.HttpException

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> =
        liveData {
            emit(Result.Loading)

            try {
                val response = apiService.register(name, email, password)
                emit(Result.Success(response))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.i("errorBody", errorBody!!)
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                val errorMessage = errorResponse.message ?: "Unknown error"
                emit(Result.Error(errorMessage))
            }
        }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> =
        liveData {
            emit(Result.Loading)

            try {
                val response = apiService.login(email, password)
                emit(Result.Success(response))
            }  catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.i("errorBody", errorBody!!)
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                val errorMessage = errorResponse.message ?: "Unknown error"
                emit(Result.Error(errorMessage))
            }
        }

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getStoriesWithLocation(token: String, location: Int): LiveData<Result<StoryResponse>> =
        liveData {
            emit(Result.Loading)

            try {
                val response = apiService.getStoriesWithLocation(token, location)
                emit(Result.Success(response))
            } catch (e: Exception) {
                Log.d("Get Stories with Location", e.message.toString())
                emit(Result.Error(e.message.toString()))
            }
        }

    fun addStories(
        token: String,
        uri: MultipartBody.Part,
        desc: RequestBody,
        lat: Double?,
        lon: Double?
    ): LiveData<Result<AddStoryResponse>> =
        liveData {
            emit(Result.Loading)

            try {
                val response = apiService.addStories(token, uri, desc, lat, lon)
                emit(Result.Success(response))
            } catch (e: Exception) {
                Log.d("Add Stories", e.message.toString())
                emit(Result.Error(e.message.toString()))
            }
        }
}