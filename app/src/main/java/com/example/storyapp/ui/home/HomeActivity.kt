package com.example.storyapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.data.local.User
import com.example.storyapp.data.local.UserPreferences
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.ActivityHomeBinding
import com.example.storyapp.ui.addstory.AddStoryActivity
import com.example.storyapp.ui.detail.DetailStoryActivity
import com.example.storyapp.ui.login.LoginActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var userModel: User
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue_900)

        userPreferences = UserPreferences(this)
        userModel = userPreferences.getUser()

        viewModel.getStories(userModel.token!!)

        topBar()

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        addStory()
    }

    override fun onResume() {
        super.onResume()

        viewModel.getStories(userModel.token!!)

        viewModel.listStory.observe(this) {
            getStoryData(it)
        }
    }

    private fun getStoryData(story: List<ListStoryItem>) {
        val layoutInflater = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutInflater

        val adapter = StoryAdapter(story)
        adapter.submitList(story)
        binding.rvStory.adapter = adapter

        adapter.setOnItemClickCallBack(object: StoryAdapter.OnItemClickCallBack {
            override fun onItemClicked(data: ListStoryItem) {
                showSelectedStory(data)
            }
        })
    }

    private fun showSelectedStory(data: ListStoryItem) {
        val moveWithParcelableIntent = Intent(this, DetailStoryActivity::class.java)
        moveWithParcelableIntent.putExtra(DetailStoryActivity.EXTRA_STORY_NAME, data.name)
        moveWithParcelableIntent.putExtra(DetailStoryActivity.EXTRA_STORY_IMAGE, data.photoUrl)
        moveWithParcelableIntent.putExtra(DetailStoryActivity.EXTRA_STORY_CREATED_AT, data.createdAt)
        moveWithParcelableIntent.putExtra(DetailStoryActivity.EXTRA_STORY_DESCRIPTION, data.description)
        startActivity(moveWithParcelableIntent)
    }

    private fun topBar() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.translate -> {
                    val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                    startActivity(intent)
                    true
                }
                R.id.logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }
    }

    private fun logout() {
        userModel.token = ""
        userPreferences.clear()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun addStory() {
        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility =
            if (isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }
}