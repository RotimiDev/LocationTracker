package com.olamachia.maptrackerweekeighttask.model

import com.olamachia.maptrackerweekeighttask.model.MoveLearnMethod
import com.olamachia.maptrackerweekeighttask.model.VersionGroup

data class VersionGroupDetail(
    val level_learned_at: Int,
    val move_learn_method: MoveLearnMethod,
    val version_group: VersionGroup
)
