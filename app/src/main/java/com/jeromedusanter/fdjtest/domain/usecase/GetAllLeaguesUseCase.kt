package com.jeromedusanter.fdjtest.domain.usecase

import com.jeromedusanter.fdjtest.domain.model.League
import com.jeromedusanter.fdjtest.domain.repository.SportsRepository
import javax.inject.Inject

class GetAllLeaguesUseCase @Inject constructor(
    private val repository: SportsRepository
) {
    suspend operator fun invoke(): Result<List<League>> {
        return repository.getAllLeagues()
    }
}
