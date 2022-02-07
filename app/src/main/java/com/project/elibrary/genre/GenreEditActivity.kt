package com.project.elibrary.genre

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.elibrary.databinding.ActivityGenreEditBinding

class GenreEditActivity : AppCompatActivity() {

    private var binding: ActivityGenreEditBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenreEditBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}