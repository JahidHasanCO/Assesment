package dev.jahidhasanco.assesment.presentation.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.coroutineScope
import dagger.hilt.android.AndroidEntryPoint
import dev.jahidhasanco.assesment.R
import dev.jahidhasanco.assesment.data.local.getCountryToCity
import dev.jahidhasanco.assesment.data.model.User
import dev.jahidhasanco.assesment.databinding.ActivityEditUserBinding
import dev.jahidhasanco.assesment.presentation.viewmodel.StorageViewModel
import dev.jahidhasanco.assesment.utils.temp.UserTempData

@AndroidEntryPoint
class EditUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditUserBinding
    private val storageViewModel: StorageViewModel by viewModels()

    private var country: String? = null
    private var cities: List<String> = emptyList()
    private var skills: ArrayList<String> = arrayListOf()
    private var selectedCity: String? = null
    private var selectedDate: String? = null
    private var resumeURI: String? = null
    private var resumeTitle: String? = null
    private var previousResume = ""
    private var uid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_user)

        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), PackageManager.PERMISSION_GRANTED
        )

        lifecycle.coroutineScope.launchWhenCreated {
            storageViewModel.updateUserStatus.collect {
                if (it.isLoading) {
                    binding.progressBar.visibility = View.VISIBLE
                }
                if (it.error.isNotEmpty()) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@EditUserActivity, it.error, Toast.LENGTH_SHORT).show()
                }
                it.data?.let {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@EditUserActivity,
                        "User Update Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    Intent(this@EditUserActivity, MainActivity::class.java).also { intent ->
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }


        UserTempData.getUser()?.let {
            setupData(it)
        }

        binding.ccp.setOnCountryChangeListener {
            country = binding.ccp.selectedCountryName
            cities = getCountryToCity(binding.ccp.selectedCountryName, this@EditUserActivity)
            setCity()
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.submitBtn.setOnClickListener {
            nullCheckData()
        }

        binding.resumePicker.setOnClickListener {
            selectPdfAndDocs()
        }

    }

    private fun setupData(user: User) {
        uid = user.id
        previousResume = user.resumeTitle
        binding.ccp.setCountryForNameCode(user.countryCode)
        binding.citySpinner.setText(user.city, false)
        selectedCity = user.city
        country = user.country
        binding.apply {
            nameEtLayout.editText?.setText(user.name)
            user.skill.forEach {
                when (it) {
                    "C" -> cCheckBox.isChecked = true
                    "Python" -> pythonCheckBox.isChecked = true
                    "Kotlin" -> kotlinCheckBox.isChecked = true
                    "Java" -> javaCheckBox.isChecked = true
                }
            }
            selectedDate = user.dateOfBirth
            val date = user.dateOfBirth.split("/")
            datePicker.init(
                date[2].toInt(),
                date[1].toInt() - 1,
                date[0].toInt()
            ) { _, year, month, day ->
                selectedDate = "$day/${month + 1}/$year"
            }
            resumePicker.text = user.resumeTitle
            resumeTitle = user.resumeTitle
            resumeURI = user.resume
        }

    }

    private fun nullCheckData() {
        with(binding) {
            if (nameEtLayout.editText?.text.isNullOrEmpty()) {
                nameEtLayout.error = "Name is required"
                return
            } else {
                nameEtLayout.error = null
            }
            if (country.isNullOrEmpty()) {
                Toast.makeText(this@EditUserActivity, "Country is required", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            if (selectedCity.isNullOrEmpty()) {
                Toast.makeText(this@EditUserActivity, "City is required", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            if (!selectOneLanguage()) {
                Toast.makeText(this@EditUserActivity, "Select one language", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            if (selectedDate.isNullOrEmpty()) {
                Toast.makeText(this@EditUserActivity, "Date is required", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            if (resumeURI.isNullOrEmpty()) {
                Toast.makeText(this@EditUserActivity, "Resume is required", Toast.LENGTH_SHORT)
                    .show()
                return
            }

        }

        uploadData()
    }

    private fun uploadData() {
        var resumeChange = false
        if (previousResume != resumeTitle) {
            resumeChange = true
        }
        storageViewModel.updateUser(
            User(
                id = uid,
                name = binding.nameEtLayout.editText?.text.toString(),
                country = country!!,
                city = selectedCity!!,
                skill = skills,
                dateOfBirth = selectedDate!!,
                resume = resumeURI!!,
                resumeTitle = resumeTitle!!
            ), resumeChange
        )
    }

    private fun selectOneLanguage(): Boolean {
        skills.clear()
        with(binding) {
            if (javaCheckBox.isChecked) {
                skills.add("Java")
            }
            if (kotlinCheckBox.isChecked) {
                skills.add("Kotlin")
            }
            if (cCheckBox.isChecked) {
                skills.add("C")
            }
            if (pythonCheckBox.isChecked) {
                skills.add("Python")
            }
        }
        return skills.size != 0
    }

    private fun setCity() {
        binding.citySpinner.visibility = View.VISIBLE
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, cities
        )
        binding.citySpinner.setAdapter(adapter)

        binding.citySpinner.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                selectedCity = cities[position]
            }
    }

    private fun selectPdfAndDocs() {
        val pdfIntent = Intent()
        pdfIntent.action = Intent.ACTION_OPEN_DOCUMENT
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
        pdfIntent.type = "*/*"
        val extraMimeType = arrayOf(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        )
        pdfIntent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeType)
        pdfIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        resultLauncher.launch(pdfIntent)
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                data?.data?.let {
                    resumeURI = it.toString()
                    val file = DocumentFile.fromSingleUri(this, it)
                    resumeTitle = file?.name ?: "Unknown"
                    binding.resumePicker.text = resumeTitle
                }

            }
        }


}