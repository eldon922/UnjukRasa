package com.pedulinegeri.unjukrasa.demonstration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DemonstrationPageViewModel @Inject constructor(private val getDemonstrationUseCase: GetDemonstrationUseCase) :
    ViewModel() {

    var nsvScrollPosition = 0
    var vpImagesCurrentItem = 0

    fun getDemonstration(id: String): LiveData<Resource<Demonstration>> {
        return liveData {
            emit(getDemonstrationUseCase(id))
        }
    }
}