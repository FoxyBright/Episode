package ru.rikmasters.gilty.notification

import ru.rikmasters.gilty.notification.paginator.Paginator
import ru.rikmasters.gilty.shared.model.notification.NotificationModel

// источник пагинации для уведомлений
class NotificationPagingSource(
    manager: NotificationManager,
): Paginator<NotificationModel, NotificationManager>(manager)