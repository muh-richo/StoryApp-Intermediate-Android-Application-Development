package com.example.storyapp.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.data.DataDummy
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.remote.response.LoginResponse
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import com.example.storyapp.data.remote.Result
import com.example.storyapp.getOrAwaitValue
import org.junit.Assert
import org.junit.Before
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {
    @get: Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var loginViewModel: LoginViewModel
    private val dummyLogin = DataDummy.generateDummyLoginResponse()

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(storyRepository)
    }

    @Test
    fun `when Login Should Not Null and Return Success`() = runTest {
        val expectedUser = MutableLiveData<Result<LoginResponse>>()
        expectedUser.value = Result.Success(dummyLogin)
        `when`(loginViewModel.login(EMAIL, PASSWORD)).thenReturn(expectedUser)

        val actualUser = loginViewModel.login(EMAIL, PASSWORD).getOrAwaitValue()

        Mockito.verify(storyRepository).login(EMAIL, PASSWORD)
        Assert.assertNotNull(actualUser)
        Assert.assertTrue(actualUser is Result.Success)
    }

    companion object {
        private const val EMAIL = "email"
        private const val PASSWORD = "password"
    }
}