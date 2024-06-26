package com.example.storyapp.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.adapter.StoryAdapter
import com.example.storyapp.data.local.preference.User
import com.example.storyapp.data.local.preference.UserPreferences
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.ActivityHomeBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.addstory.AddStoryActivity
import com.example.storyapp.ui.detail.DetailStoryActivity
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.ui.maps.MapsActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomeBinding
    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var userModel: User
    private lateinit var userPreferences: UserPreferences

    private lateinit var adapter: StoryAdapter
    private var recyclerViewState: Parcelable? = null

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

        topBar()

        binding.apply {
            fabAddStory.setOnClickListener {
                addStory()
            }
        }

        getStoryData(userModel)
    }

    override fun onResume() {
        super.onResume()
        binding.rvStory.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    override fun onPause() {
        super.onPause()
        recyclerViewState = binding.rvStory.layoutManager?.onSaveInstanceState()
    }

    private fun getStoryData(userModel: User) {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        adapter = StoryAdapter()
        binding.rvStory.adapter = adapter

        viewModel.getStory("Bearer ${userModel.token}").observe(this) { result ->
            binding.progressBar.visibility = View.GONE
            adapter.submitData(lifecycle, result)
        }

        adapter.setOnItemClickCallBack(object: StoryAdapter.OnItemClickCallBack {
            override fun onItemClicked(data: ListStoryItem) {
                showSelectedStory(data)
            }
        })
    }

    private fun showSelectedStory(data: ListStoryItem) {
        val moveWithParcelableIntent = Intent(this, DetailStoryActivity::class.java).apply {
            putExtra(DetailStoryActivity.EXTRA_STORY_NAME, data.name)
            putExtra(DetailStoryActivity.EXTRA_STORY_IMAGE, data.photoUrl)
            putExtra(DetailStoryActivity.EXTRA_STORY_CREATED_AT, data.createdAt)
            putExtra(DetailStoryActivity.EXTRA_STORY_DESCRIPTION, data.description)
        }
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
                R.id.maps -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
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
        val intent = Intent(this, AddStoryActivity::class.java)
        addStoryResultLauncher.launch(intent)
    }

    private val addStoryResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            adapter.refresh()
            binding.rvStory.scrollToPosition(0)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}