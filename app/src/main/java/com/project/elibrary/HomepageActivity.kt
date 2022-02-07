package com.project.elibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.elibrary.databinding.ActivityHomepageBinding

class HomepageActivity : AppCompatActivity() {

    private var binding: ActivityHomepageBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        checkRole()

    }

    private fun checkRole() {

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}