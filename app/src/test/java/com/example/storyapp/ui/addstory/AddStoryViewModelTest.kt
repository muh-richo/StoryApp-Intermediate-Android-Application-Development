package com.example.storyapp.ui.addstory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.data.DataDummy
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.remote.response.AddStoryResponse
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import com.example.storyapp.data.remote.Result
import com.example.storyapp.getOrAwaitValue
import org.junit.Assert
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.mock
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class AddHomeViewModelTest {
    @get: Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var addStoryViewModel: AddStoryViewModel
    private val dummyAddStory = DataDummy.generateDummyAddStoryResponse()

    @Before
    fun setUp() {
        addStoryViewModel = AddStoryViewModel(storyRepository)
    }

    @Test
    fun `when Add Story Should Not Null and Return Success`() = runTest {
        val description = "Ini deskripsi cerita".toRequestBody("text/plain".toMediaType())
        val file = mock(File::class.java)
        val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            "nameFile",
            requestImageFile
        )

        val expectedStory = MutableLiveData<Result<AddStoryResponse>>()
        expectedStory.value = Result.Success(dummyAddStory)
        Mockito.`when`(addStoryViewModel.addStory(TOKEN, imageMultipart, description, LAT, LON)).thenReturn(expectedStory)

        val actualStory = addStoryViewModel.addStory(TOKEN, imageMultipart, description, LAT, LON).getOrAwaitValue()

        Mockito.verify(storyRepository).addStories(TOKEN, imageMultipart, description, LAT, LON)
        Assert.assertNotNull(actualStory)
        Assert.assertTrue(actualStory is Result.Success)
    }

    companion object {
        private const val TOKEN = "token"
        private const val LAT = 1.2345
        private const val LON = 1.2345
    }
}