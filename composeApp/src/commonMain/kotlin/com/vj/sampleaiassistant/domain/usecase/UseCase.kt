package com.vj.sampleaiassistant.domain.usecase

import kotlinx.coroutines.flow.Flow

/**
 * Created by Vijay on 03/03/2026.
 * https://github.com/thenameisvijay
 */

typealias FlowUseCase<R> = suspend () -> Flow<R>