package com.synrgyseveneight.bcarevamp.viewmodel

import android.content.Context
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synrgyseveneight.bcarevamp.data.model.mutation.MutationResponseData
import com.synrgyseveneight.bcarevamp.data.model.mutation.MutationRequest
import com.synrgyseveneight.bcarevamp.data.repository.MutationRepository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class MutationViewModel(private val repository: MutationRepository) : ViewModel() {

    private val _mutationResponseData = MutableLiveData<List<MutationResponseData>>()
    val mutationResponseData: LiveData<List<MutationResponseData>> get() = _mutationResponseData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _downloadSuccess = MutableLiveData<Boolean>()
    val downloadSuccess: LiveData<Boolean> get() = _downloadSuccess

    private val _pdfFilePath = MutableLiveData<String>()
    val pdfFilePath: LiveData<String> get() = _pdfFilePath

    fun fetchMutations(page: Int, size: Int, token: String, requestBody: MutationRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getAllMutations(page, size, token, requestBody)
                if (response.isSuccessful) {
                    _mutationResponseData.value = response.body()?.data?.mutation_responses ?: emptyList()
                } else {
                    _error.value = response.code().toString()
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun downloadMutationReport(token: String, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.downloadMutationReport(token)
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        savePdfToStorage(body, context)
                    }
                } else {
                    _error.value = response.code().toString()
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun savePdfToStorage(body: ResponseBody, context: Context) {
        try {
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Laporan_Mutasi.pdf")
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                inputStream = body.byteStream()
                outputStream = FileOutputStream(file)
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.flush()
                _pdfFilePath.value = file.absolutePath
                _downloadSuccess.value = true
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to save the file"
        }
    }
}
