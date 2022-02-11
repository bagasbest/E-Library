package com.project.elibrary

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.himanshurawat.hasher.HashType
import com.himanshurawat.hasher.Hasher
import com.project.elibrary.databinding.ActivityRegisterBinding


class RegisterActivity : AppCompatActivity() {

    private var binding: ActivityRegisterBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        binding?.registerAsUser?.setOnClickListener {
            formValidation()
        }
    }

    private fun formValidation() {
        val email = binding!!.email.text.toString().trim()
        val password = binding!!.password.text.toString().trim()
        val name = binding!!.name.text.toString().trim()
        when {
            email.isEmpty() -> {
                Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            password.isEmpty() -> {
                Toast.makeText(this, "Kata Sandi tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            name.isEmpty() -> {
                Toast.makeText(this, "Nama Lengkap tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            else -> {
                binding!!.progressCircular.visibility = View.VISIBLE
                /// create account
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            /// save user bio in database
                            val uid = FirebaseAuth.getInstance().currentUser!!.uid
                            val data: HashMap<String, Any> = HashMap()
                            data["uid"] = uid
                            data["name"] = name
                            data["email"] = email
                            data["password"] = Hasher.hash(password, HashType.SHA_512)
                            data["role"] = "user"
                            FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(uid)
                                .set(data)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        binding!!.progressCircular.visibility = View.GONE
                                        showSuccessDialog()
                                    } else {
                                        binding!!.progressCircular.visibility = View.GONE
                                        showFailureDialog()
                                    }
                                }
                        } else {
                            binding!!.progressCircular.visibility = View.GONE
                            showFailureDialog()
                        }
                    }
            }
        }
    }

    /// munculkan dialog ketika gagal registrasi
    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal melakukan registrasi")
            .setMessage("Silahkan mendaftar kembali dengan informasi yang benar")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ -> dialogInterface.dismiss() }
            .show()
    }

    /// munculkan dialog ketika sukses registrasi
    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Berhasil melakukan registrasi")
            .setMessage("Silahkan login")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
                onBackPressed()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}