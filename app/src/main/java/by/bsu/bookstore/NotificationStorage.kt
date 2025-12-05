package by.bsu.bookstore

object NotificationStorage {
    fun notifications(): List<NotificationItem> = NotificationsManager.all()
    fun unreadCount(): Int = NotificationsManager.all().count { !it.read }
    fun markAllRead() = NotificationsManager.markAllRead()
    fun add(title: String, text: String) = NotificationsManager.addNotification(title, text)
    fun clearUnread() {
        TODO("Not yet implemented")
    }
}
