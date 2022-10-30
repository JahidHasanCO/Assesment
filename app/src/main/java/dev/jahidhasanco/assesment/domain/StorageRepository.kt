package dev.jahidhasanco.assesment.domain

import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dev.jahidhasanco.assesment.data.model.User
import dev.jahidhasanco.assesment.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject


class StorageRepository @Inject constructor() {

    private val fireStoreDatabase = FirebaseFirestore.getInstance()
    private var storageReference = FirebaseStorage.getInstance().reference

    fun addUser(user: User): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        try {
            val path = storageReference.child("Document/${user.resumeTitle}")
            val uploadTask = path.putFile(user.resume.toUri()).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()

            val data = User(
                user.id,
                user.name,
                user.country,
                user.city,
                user.skill,
                user.dateOfBirth,
                downloadUrl.toString(),
                user.resumeTitle
            )

            fireStoreDatabase.collection("User")
                .document(user.id)
                .set(data).await()

            emit(Resource.Success(data = "User Added"))

        } catch (e: HttpException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Unknown Error"))
        } catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Check Your Internet Connection"))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: ""))
        }

    }

    fun getUser(): Flow<Resource<List<User>>> = flow {
        emit(Resource.Loading())
        val userList: ArrayList<User> = arrayListOf()
        try {
            val response = fireStoreDatabase.collection("User")
                .get().await()

            if (!response.isEmpty) {
                response.forEach {
                    val data = it.toObject(User::class.java)
                    userList.add(data)
                }
            }
            emit(Resource.Success(data = userList.toList()))

        } catch (e: HttpException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Unknown Error"))
        } catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Check Your Internet Connection"))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: ""))
        }

    }

    fun deleteUser(user: User): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        try {
            val path = storageReference.storage.getReferenceFromUrl(user.resume)
            path.delete().await()
            fireStoreDatabase.collection("User")
                .document(user.id)
                .delete().await()
            emit(Resource.Success(data = "User Deleted"))

        } catch (e: HttpException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Unknown Error"))
        } catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "Check Your Internet Connection"))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: ""))
        }

    }

}
