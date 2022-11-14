package com.pedulinegeri.unjukrasa

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

class MainViewModel : ViewModel() {

    var bottomNavState: Int = -1
}