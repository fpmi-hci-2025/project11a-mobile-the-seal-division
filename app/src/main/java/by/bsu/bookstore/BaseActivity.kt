package by.bsu.bookstore

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import by.bsu.bookstore.managers.CartManager
import by.bsu.bookstore.managers.FavoritesManager
import by.bsu.bookstore.managers.FiltersManager
import by.bsu.bookstore.managers.NotificationsManager
import by.bsu.bookstore.managers.SubscriptionManager
import by.bsu.bookstore.model.Book
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private var navListenerEnabled = true
    private var loadingAnimation: LottieAnimationView? = null
    private var loadingOverlay: View? = null

    private val navMap = mapOf(
        R.id.nav_home to MainActivity::class.java,
        R.id.nav_favorites to FavoritesActivity::class.java,
        R.id.nav_cart to CartActivity::class.java,
        R.id.nav_profile to ProfileActivity::class.java
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.layout_activity_base)

        FavoritesManager.init(this)
        CartManager.init(this)
        NotificationsManager.init(this)
        //ReviewManager.init(this)
        SubscriptionManager.init(this)
        FiltersManager.init(this)
        bottomNav = findViewById(R.id.bottomNavigationView)

        bottomNav.setOnItemSelectedListener { item ->
            if (!navListenerEnabled) return@setOnItemSelectedListener true

            val targetCls = navMap[item.itemId]
            if (targetCls == null) return@setOnItemSelectedListener false

            if (this::class.java == targetCls) {
                // ensure visual selection remains
                selectNavItem(item.itemId)
                return@setOnItemSelectedListener true
            }

            val intent = Intent(this, targetCls)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)

            when (item.itemId) {
                R.id.nav_cart, R.id.nav_favorites -> overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
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
        loadingAnimation = findViewById(R.id.loadingAnimationBase)
        loadingOverlay = findViewById(R.id.loadingOverlayBase)

    }

    protected fun showLoading(show: Boolean) {
        loadingAnimation?.visibility = if (show) View.VISIBLE else View.GONE
        loadingOverlay?.visibility = if (show) View.VISIBLE else View.GONE
        loadingOverlay?.isClickable = show
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
        val count = NotificationsManager.getUnreadCount()
        val badge = bottomNav.getOrCreateBadge(R.id.nav_profile)
        badge.isVisible = count > 0
        badge.number = count
    }

    fun setPriceWithDiscount(book: Book, priceView: TextView, oldPriceView: TextView) {
        val discount = book.discountObj
        if (discount != null && discount.percentage > 0) {
            val oldPrice = book.price
            val newPrice = oldPrice * (1 - discount.percentage / 100.0)

            priceView.text = String.format("%.2f BYN", newPrice)

            oldPriceView.visibility = View.VISIBLE
            oldPriceView.text = String.format("%.2f BYN", oldPrice)
            oldPriceView.paintFlags = oldPriceView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            priceView.text = String.format("%.2f BYN", book.price)
            oldPriceView.visibility = View.GONE
        }
    }
}
