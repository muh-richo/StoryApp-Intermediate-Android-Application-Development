package com.example.storyapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _storyPagingData = MutableLiveData<PagingData<ListStoryItem>>()
    val storyPagingData: LiveData<PagingData<ListStoryItem>> get() = _storyPagingData

    private var currentToken: String? = null

    fun getStory(token: String) {
        if (currentToken != token) {
            currentToken = token
            viewModelScope.launch {
                storyRepository.getStories(token)
                    .cachedIn(viewModelScope)
                    .collectLatest { pagingData ->
                        _storyPagingData.value = pagingData
                    }
            }
        }
    }
}