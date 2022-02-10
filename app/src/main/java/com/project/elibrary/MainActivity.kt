package com.project.elibrary

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.project.elibrary.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        autoLogin()

        binding!!.login.setOnClickListener { formValidation() }

        binding!!.register.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    RegisterActivity::class.java
                )
            )
        }


    }


    private fun autoLogin() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this@MainActivity, HomepageActivity::class.java))
        }
    }

    private fun formValidation() {
        val username: String = binding?.username?.text.toString().trim()
        val password = binding!!.password.text.toString().trim()
        when {
            username.isEmpty() -> {
                Toast.makeText(this, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            password.isEmpty() -> {
                Toast.makeText(this, "Kata Sandi tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            else -> {
                login(username, password)
            }
        }
    }

    private fun login(username: String, password: String) {
        binding!!.progressCircular.visibility = View.VISIBLE
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .whereEqualTo("username", username)
            .limit(1)
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.result.size() == 0) {
                    /// jika tidak terdapat di database dan email serta password, maka tidak bisa login
                    binding!!.progressCircular.visibility = View.GONE
                    showFailureDialog()
                    return@addOnCompleteListener
                }

                /// jika terdaftar maka ambil email di database, kemudian lakukan autentikasi menggunakan email & password dari user
                for (snapshot in task.result) {
                    val email = "" + snapshot["email"]

                    /// fungsi untuk mengecek, apakah email yang di inputkan ketika login sudah terdaftar di database atau belum
                    FirebaseAuth
                        .getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                /// jika terdapat di database dan email serta password sama, maka masuk ke homepage
                                binding!!.progressCircular.visibility = View.GONE
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        HomepageActivity::class.java
                                    )
                                )
                            } else {
                                /// jika tidak terdapat di database dan email serta password, maka tidak bisa login
                                binding!!.progressCircular.visibility = View.GONE
                                showFailureDialog()
                            }
                        }
                }
            }
    }

    /// munculkan dialog ketika gagal login
    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal melakukan login")
            .setMessage("Silahkan periksa data diri maupun koneksi internet anda!")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ -> dialogInterface.dismiss() }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}