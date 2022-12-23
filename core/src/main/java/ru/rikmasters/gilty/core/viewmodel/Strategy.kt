package ru.rikmasters.gilty.core.viewmodel

/**
 * Стратегия поведения при коллизии одиночных задач
 */
enum class Strategy {
    
    /**
     * Отмена выполняемой
     */
    CANCEL,
    
    /**
     * Ошибка в этой
     * @throws [IllegalStateException]
     */
    THROW,
    
    /**
     * Ожидание результата выполняемой
     */
    JOIN
}