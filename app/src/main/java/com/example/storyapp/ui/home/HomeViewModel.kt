package com.example.storyapp.ui.home

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository

class HomeViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun getStory(token: String) =
        storyRepository.getStories(token)
}