package com.example.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.utils.isValidEmail
import com.example.storyapp.data.remote.Result


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(80, systemBars.top, 80, systemBars.bottom)
            insets
        }

        playAnimation()

        binding.apply {
            btnRegister.setOnClickListener {
                register()
            }
        }
    }

    private fun register() {
        val edRegisterName = binding.edRegisterName.text
        val edRegisterEmail = binding.edRegisterEmail.text
        val edRegisterPassword = binding.edRegisterPassword.text

        if (edRegisterName!!.isEmpty() || edRegisterEmail!!.isEmpty() || edRegisterPassword!!.isEmpty()) {
            showToast(R.string.empty_form)
        } else if (!isValidEmail(edRegisterEmail.toString()) || edRegisterPassword.length < 8) {
            showToast(R.string.invalid_form)
        } else {
            viewModel.register(
                edRegisterName.toString(),
                edRegisterEmail.toString(),
                edRegisterPassword.toString()
            ).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE

                            showToast(R.string.register_success)

                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE

                            showToast(result.error)
                        }
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        val ivLogo = ObjectAnimator.ofFloat(binding.ivLogo, View.ALPHA, 1f).setDuration(1500)
        val tvSignUp = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1f).setDuration(500)
        val tvEnter = ObjectAnimator.ofFloat(binding.tvEnter, View.ALPHA, 1f).setDuration(500)
        val tvName = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(500)
        val edRegisterName = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(500)
        val tvEmail = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val edRegisterEmail = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val tvPassword = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val edRegisterPassword = ObjectAnimator.ofFloat(binding.edLayoutRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val linearLayout = ObjectAnimator.ofFloat(binding.linearLayout, View.ALPHA, 1f).setDuration(500)
        val btnSignUp = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(ivLogo, tvSignUp, tvEnter, tvName, edRegisterName, tvEmail, edRegisterEmail, tvPassword, edRegisterPassword, linearLayout, btnSignUp)
            startDelay = 50
            start()
        }
    }

    private fun showToast(message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}