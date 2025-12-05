package by.bsu.bookstore

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private var navListenerEnabled = true

    // карта menuId -> целевая Activity (класс)
    private val navMap = mapOf(
        R.id.nav_home to MainActivity::class.java,
        R.id.nav_favorites to FavoritesActivity::class.java,
        R.id.nav_cart to CartActivity::class.java,
        R.id.nav_profile to ProfileActivity::class.java
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Базовый layout устанавливаем всегда — он содержит contentContainer и bottomNavigationView
        super.setContentView(R.layout.layout_activity_base)

        FavoritesManager.init(this)
        CartManager.init(this)

        bottomNav = findViewById(R.id.bottomNavigationView)

        // ставим слушатель один раз, но он проверяет флаг navListenerEnabled
        bottomNav.setOnItemSelectedListener { item ->
            if (!navListenerEnabled) return@setOnItemSelectedListener true

            val targetCls = navMap[item.itemId]
            if (targetCls == null) return@setOnItemSelectedListener false

            // если сейчас мы уже в нужной активити — ничего не делаем
            if (this::class.java == targetCls) {
                // ensure visual selection remains
                selectNavItem(item.itemId)
                return@setOnItemSelectedListener true
            }

            // стартуем новую активити (без finish) — чтобы back работал естественно
            val intent = Intent(this, targetCls)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)

            // анимация перехода
            when (item.itemId) {
                R.id.nav_cart, R.id.nav_favorites -> overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                else -> overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
            true
        }
    }

    /**
     * Вставляет содержимое в contentContainer.
     * Делать всегда ПЕРЕД findViewById(...) для элементов контента.
     */
    protected fun inflateContent(layoutRes: Int) {
        val container = findViewById<FrameLayout>(R.id.contentContainer)
        layoutInflater.inflate(layoutRes, container, true)
    }

    /**
     * Безопасно устанавливаем выбранный пункт меню: временно отключаем слушатель,
     * выставляем selectedItemId и восстанавливаем слушатель.
     * Вызывать ДО того, как пользователь начнёт взаимодействовать с меню.
     */
    protected fun selectNavItem(menuId: Int) {
        navListenerEnabled = false
        try {
            bottomNav.selectedItemId = menuId
        } finally {
            navListenerEnabled = true
        }
    }

    /**
     * Обновить бейдж уведомлений (если есть)
     */
    protected fun updateNotificationBadge() {
        val count = NotificationStorage.unreadCount()
        val badge = bottomNav.getOrCreateBadge(R.id.nav_profile)
        badge.isVisible = count > 0
        badge.number = count
    }
}
