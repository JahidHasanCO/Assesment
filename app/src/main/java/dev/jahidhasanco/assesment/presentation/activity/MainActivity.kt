package dev.jahidhasanco.assesment.presentation.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.jahidhasanco.assesment.R
import dev.jahidhasanco.assesment.data.model.User
import dev.jahidhasanco.assesment.databinding.ActivityMainBinding
import dev.jahidhasanco.assesment.presentation.adapter.OnUserClickListener
import dev.jahidhasanco.assesment.presentation.adapter.UserAdapter
import dev.jahidhasanco.assesment.presentation.viewmodel.StorageViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnUserClickListener {

    private lateinit var binding: ActivityMainBinding
    private val storageViewModel: StorageViewModel by viewModels()

    private val userList: ArrayList<User> = arrayListOf()
    private var userAdapter: UserAdapter = UserAdapter(userList.toMutableList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        storageViewModel.getUser()

        lifecycle.coroutineScope.launchWhenCreated {
            storageViewModel.getUserDataStatus.collect {
                if (it.isLoading) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.userRecView.visibility = View.GONE
                }
                if (it.error.isNotBlank()) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@MainActivity, it.error, Toast.LENGTH_SHORT).show()
                }
                it.data?.let { data ->
                    binding.progressBar.visibility = View.GONE
                    binding.userRecView.visibility = View.VISIBLE
                    userList.clear()
                    userList.addAll(data)
                    userAdapter.setUserList(userList.toMutableList())
                }
            }
        }

        lifecycle.coroutineScope.launchWhenCreated {
            storageViewModel.deleteUserStatus.collect {
                if (it.error.isNotBlank()) {
                    Toast.makeText(this@MainActivity, it.error, Toast.LENGTH_SHORT).show()
                }
                it.data?.let { data ->
                    Toast.makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.addBtn.setOnClickListener {
            Intent(this, AddDetailsActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.userRecView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
            setHasFixedSize(false)
        }
    }

    override fun onUserItemClick(user: User) {

    }

    override fun onItemDeleteClick(user: User) {
        //build alert dialog
        AlertDialog.Builder(this)
            .setTitle("Delete")
            .setMessage("Are you sure you want to delete this user?")
            .setPositiveButton("Yes") { dialog, which ->
                storageViewModel.deleteUser(user)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onItemEditClick(user: User) {
        Toast.makeText(this, "Edit", Toast.LENGTH_SHORT).show()
    }

    override fun onItemResumeClick(user: User) {
        Toast.makeText(this, "Resume Clicked ${user.resumeTitle}", Toast.LENGTH_SHORT).show()
    }
}