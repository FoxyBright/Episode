package ru.rikmasters.gilty.viewmodel

import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.translations.repository.TranslationRepository

class TranslationViewModel : ViewModel() {

    private val translationRepository: TranslationRepository by inject()
}
