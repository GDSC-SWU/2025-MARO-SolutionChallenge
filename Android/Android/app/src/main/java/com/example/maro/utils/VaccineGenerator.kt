package com.example.maro.utils

import com.example.maro.model.VaccineEntity
import java.time.LocalDate

object VaccineGenerator {
    fun generateSchedule(months: Int, childId: Int): List<VaccineEntity> {
        val today = LocalDate.now()
        val schedule = listOf(
            Triple("BCG", 0, 1),
            Triple("HepB 1st", 0, 1),
            Triple("HepB 2nd", 1, 2),
            Triple("DTaP 1st", 2, 3),
            Triple("Polio 1st", 2, 3),
            Triple("MMR", 12, 15),
            Triple("Japanese Encephalitis", 12, 24),
            Triple("HepA 1st", 12, 23)
        )

        return schedule.filter { months in it.second..it.third }.map {
            VaccineEntity(
                childId = childId,
                title = it.first,
                dateRange = "$today ~ ${today.plusMonths(1)}",
                status = "Coming up"
            )
        }
    }
}
