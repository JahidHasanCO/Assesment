package dev.jahidhasanco.assesment.presentation.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.jahidhasanco.assesment.R
import dev.jahidhasanco.assesment.data.model.User
import dev.jahidhasanco.assesment.databinding.ActivityMainBinding
import dev.jahidhasanco.assesment.databinding.UserItemDialogBinding
import dev.jahidhasanco.assesment.presentation.adapter.OnUserClickListener
import dev.jahidhasanco.assesment.presentation.adapter.UserAdapter
import dev.jahidhasanco.assesment.presentation.viewmodel.StorageViewModel
import dev.jahidhasanco.assesment.utils.temp.UserTempData

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
        userAdapter.setOnItemClickListener(this)

        lifecycle.coroutineScope.launchWhenCreated {
            storageViewModel.getUserDataStatus.collect {
                if (it.isLoading) {
                    binding.progressBar.visibility = View.VISIBLE
                }
                if (it.error.isNotBlank()) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@MainActivity, it.error, Toast.LENGTH_SHORT).show()
                }
                it.data?.let { data ->
                    binding.progressBar.visibility = View.GONE
                    userList.clear()
                    userList.addAll(data)
                    userAdapter.setUserList(userList.toMutableList())
                }
            }
        }

        lifecycle.coroutineScope.launchWhenCreated {
            storageViewModel.deleteUserStatus.collect {
                if (it.isLoading) {
                    binding.progressBar.visibility = View.VISIBLE
                }
                if (it.error.isNotBlank()) {
                    Toast.makeText(this@MainActivity, it.error, Toast.LENGTH_SHORT).show()
                }
                it.data?.let { data ->
                    Toast.makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                    storageViewModel.getUser()
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
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .create()
        val binding: UserItemDialogBinding = UserItemDialogBinding.inflate(layoutInflater)
        val button = binding.closeDialog
        binding.apply {
            name.text = user.name
            address.text = "Country: ${user.country}, City: ${user.city}"
            var skillsWithComma = ""
            user.skill.forEach {
                skillsWithComma += "$it, "
            }
            skillsWithComma.dropLast(2)
            skills.text = skillsWithComma
            dob.text = user.dateOfBirth
            resume.text = user.resumeTitle
        }
        builder.setView(binding.root)
        button.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
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
        Intent(this, EditUserActivity::class.java).also {
            UserTempData.clear()
            UserTempData.addUser(user)
            startActivity(it)
        }
    }

    override fun onItemResumeClick(user: User) {
        viewPdf(user.resume)
    }

    private fun viewPdf(value: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(value))
        startActivity(intent)
    }
}