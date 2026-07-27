package com.example.hobbymate.ui.hobby

import com.example.hobbymate.model.Hobby

data class HobbyUiState(
    val recommendations: List<Hobby> = emptyList(),
    val allHobbies: List<Hobby> = emptyList(),
    val selectedHobby: Hobby? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
