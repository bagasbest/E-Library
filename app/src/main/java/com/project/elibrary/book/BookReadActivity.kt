package com.project.elibrary.book

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.project.elibrary.databinding.ActivityBookReadBinding
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class BookReadActivity : AppCompatActivity() {

    private var binding: ActivityBookReadBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookReadBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val pdf = intent.getStringExtra(PDF)
        binding!!.progressBar.visibility = View.VISIBLE
        RetrievePDFStream().execute(pdf)


        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

    }

    @SuppressLint("StaticFieldLeak")
    inner class RetrievePDFStream : AsyncTask<String, Void, InputStream>() {
        override fun doInBackground(vararg p0: String?): InputStream? {
            var inputStream: InputStream? = null
            try {
                val url = URL(p0[0])
                val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
                if (urlConnection.responseCode == 200) {
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                }
            } catch (e: IOException) {
                return null
            }
            return inputStream!!
        }


        override fun onPostExecute(result: InputStream?) {
            super.onPostExecute(result)
            binding?.pdfView?.fromStream(result)?.onLoad {
                binding?.progressBar?.visibility = View.GONE
            }?.load()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val PDF = "pdf"
    }
}