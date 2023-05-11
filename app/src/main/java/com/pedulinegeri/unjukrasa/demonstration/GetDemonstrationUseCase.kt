package com.pedulinegeri.unjukrasa.demonstration

import com.pedulinegeri.unjukrasa.util.Resource
import javax.inject.Inject

class GetDemonstrationUseCase @Inject constructor(private val demonstrationRepository: DemonstrationRepository) {

    suspend operator fun invoke(id: String): Resource<Demonstration> = try {
        val demonstration = demonstrationRepository.getDemonstration(id)
        Resource.success(demonstration)
    } catch (e: Exception) {
        Resource.error(e.message.toString())
    }
}