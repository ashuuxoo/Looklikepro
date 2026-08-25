package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.api.TruecallerApiService
import com.example.data.model.PhoneData
import com.example.data.model.PhoneLookupResponse
import com.example.ui.PhoneLookupUiState
import com.example.ui.PhoneLookupViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Phone Lookup", appName)
    }

    @Test
    fun `search with invalid short number shows error`() {
        val viewModel = PhoneLookupViewModel()
        viewModel.search("12")
        val state = viewModel.uiState.value
        assertTrue(state is PhoneLookupUiState.Error)
        assertEquals("Please enter a valid phone number", (state as PhoneLookupUiState.Error).message)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `search with mock service success returns data`() = runTest {
        val fakeService = object : TruecallerApiService {
            override suspend fun lookupPhoneNumber(phoneNumber: String): PhoneLookupResponse {
                return PhoneLookupResponse(
                    success = true,
                    data = PhoneData(
                        name = "Asish Bhai",
                        address = "All Circles",
                        number = "8847840627"
                    )
                )
            }
        }
        val viewModel = PhoneLookupViewModel(apiService = fakeService)
        viewModel.search("8847840627")
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertTrue(state is PhoneLookupUiState.Success)
        val successState = state as PhoneLookupUiState.Success
        assertEquals("Asish Bhai", successState.data.name)
        assertEquals("All Circles", successState.data.address)
    }
}

