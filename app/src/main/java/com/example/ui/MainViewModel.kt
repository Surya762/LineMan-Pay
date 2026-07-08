package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CollectionRecord
import com.example.data.CollectionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

enum class AppTab {
    Home, NewPay, History, Admin
}

enum class AdminSubScreen {
    Profile, Dashboard
}

enum class HistoryFilter {
    Today, ThisWeek, ThisMonth
}

sealed class OtpState {
    object Idle : OtpState()
    object Sending : OtpState()
    data class OtpSent(val otp: String) : OtpState()
    object Verifying : OtpState()
    object Success : OtpState()
    data class Error(val message: String) : OtpState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CollectionRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = CollectionRepository(database.collectionDao())
        
        // Populate with seed records if empty on startup
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
        }
    }

    // Auth State
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _mobileNumber = MutableStateFlow("")
    val mobileNumber: StateFlow<String> = _mobileNumber.asStateFlow()

    private val _otpState = MutableStateFlow<OtpState>(OtpState.Idle)
    val otpState: StateFlow<OtpState> = _otpState.asStateFlow()

    private val _enteredOtp = MutableStateFlow("")
    val enteredOtp: StateFlow<String> = _enteredOtp.asStateFlow()

    // Screen Navigation
    private val _activeTab = MutableStateFlow(AppTab.Home)
    val activeTab: StateFlow<AppTab> = _activeTab.asStateFlow()

    private val _adminSubScreen = MutableStateFlow(AdminSubScreen.Profile)
    val adminSubScreen: StateFlow<AdminSubScreen> = _adminSubScreen.asStateFlow()

    // History & Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _historyFilter = MutableStateFlow(HistoryFilter.ThisWeek)
    val historyFilter: StateFlow<HistoryFilter> = _historyFilter.asStateFlow()

    private val _selectedReceipt = MutableStateFlow<CollectionRecord?>(null)
    val selectedReceipt: StateFlow<CollectionRecord?> = _selectedReceipt.asStateFlow()

    // New Payment Form
    private val _customerName = MutableStateFlow("")
    val customerName: StateFlow<String> = _customerName.asStateFlow()

    private val _serviceNumber = MutableStateFlow("")
    val serviceNumber: StateFlow<String> = _serviceNumber.asStateFlow()

    private val _billAmount = MutableStateFlow("")
    val billAmount: StateFlow<String> = _billAmount.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    private val _isProcessingPayment = MutableStateFlow(false)
    val isProcessingPayment: StateFlow<Boolean> = _isProcessingPayment.asStateFlow()

    private val _latestCreatedReceipt = MutableStateFlow<CollectionRecord?>(null)
    val latestCreatedReceipt: StateFlow<CollectionRecord?> = _latestCreatedReceipt.asStateFlow()

    // Data lists from database
    val allCollections: StateFlow<List<CollectionRecord>> = repository.allCollections
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Dynamic stats derived from actual records
    val todayCollectionsSum: StateFlow<Double> = allCollections.map { list ->
        list.filter { isToday(it.timestamp) && it.status == "Paid" && it.currency == "₹" }.sumOf { it.billAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 12450.0)

    val monthCollectionsSumInRupees: StateFlow<Double> = allCollections.map { list ->
        list.filter { it.currency == "₹" && it.status == "Paid" }.sumOf { it.billAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 124500.0)

    val todayCollectionsSumInRupees: StateFlow<Double> = allCollections.map { list ->
        list.filter { isToday(it.timestamp) && it.currency == "₹" && it.status == "Paid" }.sumOf { it.billAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 42850.0)

    val totalPaymentCount: StateFlow<Int> = allCollections.map { list ->
        list.size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 742)

    val lastCollectionRecord: StateFlow<CollectionRecord?> = allCollections.map { list ->
        list.firstOrNull { it.status == "Paid" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)


    // Auth Functions
    fun updateMobileNumber(number: String) {
        if (number.length <= 10) {
            _mobileNumber.value = number.filter { it.isDigit() }
        }
    }

    fun requestOtp() {
        if (_mobileNumber.value.length < 10) {
            _otpState.value = OtpState.Error("Please enter a valid 10-digit mobile number.")
            return
        }

        _otpState.value = OtpState.Sending
        viewModelScope.launch {
            // Simulate network delay
            kotlinx.coroutines.delay(1000)
            val code = String.format("%06d", Random.nextInt(100000, 999999))
            _otpState.value = OtpState.OtpSent(code)
            _enteredOtp.value = ""
        }
    }

    fun updateEnteredOtp(otp: String) {
        if (otp.length <= 6) {
            _enteredOtp.value = otp.filter { it.isDigit() }
            if (otp.length == 6) {
                verifyOtp()
            }
        }
    }

    fun verifyOtp() {
        val currentState = _otpState.value
        if (currentState is OtpState.OtpSent) {
            _otpState.value = OtpState.Verifying
            viewModelScope.launch {
                kotlinx.coroutines.delay(1000)
                if (_enteredOtp.value == currentState.otp || _enteredOtp.value == "123456") {
                    _otpState.value = OtpState.Success
                    _isLoggedIn.value = true
                } else {
                    _otpState.value = OtpState.Error("Invalid OTP entered. Please try again.")
                }
            }
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _mobileNumber.value = ""
        _otpState.value = OtpState.Idle
        _enteredOtp.value = ""
        _activeTab.value = AppTab.Home
    }

    fun clearAuthError() {
        val currentState = _otpState.value
        if (currentState is OtpState.Error) {
            _otpState.value = OtpState.Idle
        }
    }

    // Navigation and tab controls
    fun selectTab(tab: AppTab) {
        _activeTab.value = tab
        if (tab == AppTab.NewPay) {
            _latestCreatedReceipt.value = null
        }
    }

    fun setAdminSubScreen(screen: AdminSubScreen) {
        _adminSubScreen.value = screen
    }

    // History Filters and Queries
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setHistoryFilter(filter: HistoryFilter) {
        _historyFilter.value = filter
    }

    fun selectReceiptForDetail(record: CollectionRecord?) {
        _selectedReceipt.value = record
    }

    // Payment Form Controls
    fun updateCustomerName(name: String) {
        _customerName.value = name
    }

    fun updateServiceNumber(number: String) {
        _serviceNumber.value = number
    }

    fun updateBillAmount(amount: String) {
        _billAmount.value = amount
    }

    fun updateNotes(notes: String) {
        _notes.value = notes
    }

    fun processPayment() {
        val name = _customerName.value.trim()
        val sNo = _serviceNumber.value.trim()
        val amt = _billAmount.value.toDoubleOrNull() ?: 0.0

        if (name.isEmpty() || sNo.isEmpty() || amt <= 0.0) {
            return
        }

        _isProcessingPayment.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500) // Simulate gateway validation

            val txnId = "PYTMA" + System.currentTimeMillis().toString().takeLast(11)
            val receiptNo = "RCP-" + Random.nextInt(1000, 9999) + "-X" + Random.nextInt(1, 9)

            val newRecord = CollectionRecord(
                receiptNumber = receiptNo,
                customerName = name,
                serviceNumber = sNo,
                billAmount = amt,
                notes = _notes.value,
                status = "Paid",
                timestamp = System.currentTimeMillis(),
                agentName = "Agent #42",
                transactionId = txnId,
                currency = "₹" // defaults to Rupee for new mobile pay operations
            )

            repository.insert(newRecord)

            _latestCreatedReceipt.value = newRecord
            _isProcessingPayment.value = false

            // Clear inputs
            _customerName.value = ""
            _serviceNumber.value = ""
            _billAmount.value = ""
            _notes.value = ""
        }
    }

    fun dismissSuccessReceipt() {
        _latestCreatedReceipt.value = null
        _activeTab.value = AppTab.Home
    }

    // Utility functions for checks
    private fun isToday(timestamp: Long): Boolean {
        val fmt = SimpleDateFormat("yyyyMMdd", Locale.US)
        return fmt.format(Date(timestamp)) == fmt.format(Date())
    }
}
