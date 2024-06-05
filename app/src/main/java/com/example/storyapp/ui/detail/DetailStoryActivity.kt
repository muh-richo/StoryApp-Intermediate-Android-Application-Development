package com.example.storyapp.ui.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.utils.withDateFormat

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setDataDetailStory()
        backButton()
    }

    private fun setDataDetailStory() {
        val ivStory = intent.extras!!.getString(EXTRA_STORY_IMAGE)
        val tvFullName = intent.extras!!.getString(EXTRA_STORY_NAME)
        val tvCreatedAt = intent.extras!!.getString(EXTRA_STORY_CREATED_AT)
        val tvDescription = intent.extras!!.getString(EXTRA_STORY_DESCRIPTION)

        Glide.with(this@DetailStoryActivity)
            .load(ivStory)
            .into(binding.ivStory)
        binding.tvFullName.text = tvFullName
        binding.tvCreatedAt.text = tvCreatedAt!!.withDateFormat()
        binding.tvDesc.text = tvDescription
    }

    private fun backButton() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val EXTRA_STORY_IMAGE = "extra_story_image"
        const val EXTRA_STORY_NAME = "extra_story_name"
        const val EXTRA_STORY_CREATED_AT = "extra_story_created_at"
        const val EXTRA_STORY_DESCRIPTION = "extra_story_description"
    }
}