package xyz.teamgravity.swipetoaction

import java.util.UUID

data class NameModel(
    val id: UUID = UUID.randomUUID(),
    val name: String
)