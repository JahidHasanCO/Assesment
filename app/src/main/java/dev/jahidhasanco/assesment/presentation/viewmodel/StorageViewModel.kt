package dev.jahidhasanco.assesment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jahidhasanco.assesment.data.model.User
import dev.jahidhasanco.assesment.domain.StorageRepository
import dev.jahidhasanco.assesment.utils.Resource
import dev.jahidhasanco.assesment.utils.networkState.StringState
import dev.jahidhasanco.assesment.utils.networkState.UserDataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(private val repository: StorageRepository) :
    ViewModel() {

    private val _addUserStatus = MutableStateFlow(StringState())
    val addUserStatus: StateFlow<StringState> = _addUserStatus

    private val _deleteUserStatus = MutableStateFlow(StringState())
    val deleteUserStatus: StateFlow<StringState> = _deleteUserStatus

    private val _getUserDataStatus = MutableStateFlow(UserDataState())
    val getUserDataStatus: StateFlow<UserDataState> = _getUserDataStatus

    fun addUser(user: User) {
        repository.addUser(user).onEach {
            when (it) {
                is Resource.Loading -> {
                    _addUserStatus.value = StringState(isLoading = true)
                }
                is Resource.Error -> {
                    _addUserStatus.value = StringState(error = it.message ?: "")
                }
                is Resource.Success -> {
                    _addUserStatus.value = StringState(data = it.data)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getUser() {
        repository.getUser().onEach {
            when (it) {
                is Resource.Loading -> {
                    _getUserDataStatus.value = UserDataState(isLoading = true)
                }
                is Resource.Error -> {
                    _getUserDataStatus.value = UserDataState(error = it.message ?: "")
                }
                is Resource.Success -> {
                    _getUserDataStatus.value = UserDataState(data = it.data)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun deleteUser(user: User) {
        repository.deleteUser(user).onEach {
            when (it) {
                is Resource.Loading -> {
                    _deleteUserStatus.value = StringState(isLoading = true)
                }
                is Resource.Error -> {
                    _deleteUserStatus.value = StringState(error = it.message ?: "")
                }
                is Resource.Success -> {
                    _deleteUserStatus.value = StringState(data = it.data)
                }
            }
        }.launchIn(viewModelScope)
    }

}
