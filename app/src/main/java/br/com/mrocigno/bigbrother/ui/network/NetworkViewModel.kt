package br.com.mrocigno.bigbrother.ui.network

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mrocigno.bigbrother.R
import br.com.mrocigno.bigbrother.di.DI
import br.com.mrocigno.bigbrother.network.MutableResponseFlow
import br.com.mrocigno.bigbrother.network.ResponseFlow
import br.com.mrocigno.bigbrother.repository.GithubRepository
import br.com.mrocigno.bigbrother.repository.model.ApiBase
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class NetworkViewModel(
    private val githubRepository: GithubRepository = DI.githubRepository
) : ViewModel() {

    private var clientId: Int = R.id.okhttp_3

    private val _list = MutableResponseFlow<ApiBase>()
    val list: ResponseFlow<ApiBase> get() = _list

    fun fetchList() = _list.sync(githubRepository.getList(clientId))

    fun fetchListSlowly() = _list.sync(githubRepository.getListSlowly(clientId))

    fun fetchError() = _list.sync(githubRepository.getError(clientId))

    fun fetchPost() = _list.sync(githubRepository.simulatePost(clientId).map {
        ApiBase(0, false, emptyList())
    })

    fun uploadImg(context: Context) = viewModelScope.launch {
        val fileName = "upload-example.jpg"
        val tempFile = File.createTempFile("tmp_", fileName, context.cacheDir)
        context.assets.open(fileName).use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }

        val requestBody = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("image", tempFile.name, requestBody)
        runCatching {
            githubRepository.uploadImage(part)
        }.getOrNull()
    }

    fun fetchXmlApi() = viewModelScope.launch {
        runCatching {
            githubRepository.xmlApi(clientId)
        }.getOrNull()
    }

    fun setClientById(checkedId: Int) {
        clientId = checkedId
    }
}
