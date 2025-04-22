package com.jis_citu.furrevercare.model

import java.util.UUID

data class Pet(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val species: String,
    val breed: String,
    val age: Int,
    val gender: String,
    val weight: Double,
    val imageRes: Int,
    val allergies: List<String> = emptyList(),
    val medications: List<Medication> = emptyList(),
    val vaccinations: List<Vaccination> = emptyList(),
    val vetVisits: List<VetVisit> = emptyList()
)

data class Medication(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val dosage: String,
    val frequency: String,
    val startDate: String,
    val endDate: String,
    val notes: String = ""
)

data class Vaccination(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val date: String,
    val nextDueDate: String,
    val notes: String = ""
)

data class VetVisit(
    val id: String = UUID.randomUUID().toString(),
    val date: String,
    val vetName: String,
    val reason: String,
    val diagnosis: String = "",
    val treatment: String = "",
    val notes: String = ""
)

data class Schedule(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val date: String,
    val time: String,
    val type: ScheduleType,
    val notes: String = ""
)

enum class ScheduleType {
    MEDICATION, VACCINATION, VET_VISIT, GROOMING, OTHER
}
