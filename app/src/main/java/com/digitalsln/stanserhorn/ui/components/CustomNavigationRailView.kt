package com.digitalsln.stanserhorn.ui.components

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.core.view.doOnNextLayout
import androidx.core.view.forEach
import androidx.core.view.setPadding
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.digitalsln.stanserhorn.R
import com.digitalsln.stanserhorn.databinding.ItemMenuRailBinding

class CustomNavigationRailView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var selectedItem: View? = null
    private var navController: NavController? = null
    private val statusCard by lazy {
        ItemMenuRailBinding.inflate(LayoutInflater.from(context), this, false).root.apply {
            setPadding(0)
            Glide.with(context)
                .load(R.drawable.state_not_connected)
                .into(this)
            setOnClickListener {

            }
        }
    }

    init {
        orientation = VERTICAL

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomNavigationRailView, 0, 0)
            val menuResId = typedArray.getResourceId(R.styleable.CustomNavigationRailView_menu, 0)
            if (menuResId != 0) {
                setMenu(menuResId)
            }
            typedArray.recycle()
        }

        val bottomLayout = LinearLayout(context).apply {
            orientation = VERTICAL
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            setVerticalGravity(Gravity.BOTTOM)
            addView(statusCard)
        }

        addView(bottomLayout)
    }

    fun setNavController(navController: NavController) {
        this.navController = navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            post {
                itemSelect(findViewById(destination.id) ?: return@post)
            }
        }
    }

    fun setMenu(menuResId: Int) {
        val menu = PopupMenu(context, null).menu
        MenuInflater(context).inflate(menuResId, menu)

        menu.forEach { menuItem ->
            ItemMenuRailBinding.inflate(
                LayoutInflater.from(context), this, true
            ).root.apply {
                id = menuItem.itemId
                setImageDrawable(menuItem.icon)
                setOnClickListener {
                    itemSelect(it)
                    navController?.navigate(menuItem.itemId)
                }
            }
        }

    }

    private fun itemSelect(view: View) {
        selectedItem?.isSelected = false
        view.isSelected = true
        selectedItem = view
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putInt("selectedItemId", selectedItem?.id ?: -1)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var viewState = state
        if (viewState is Bundle) {
            val selectedItemId = viewState.getInt("selectedItemId", -1)
            if (selectedItemId != -1) {
                post {
                    findViewById<View>(selectedItemId)?.performClick()
                }
            }
            viewState = viewState.getParcelable("superState")
        }
        super.onRestoreInstanceState(viewState)
    }

    fun setStateToNotConnected() {

        Glide.with(context)
            .load(R.drawable.state_not_connected)
            .into(statusCard)
//        statusCard.setImageResource(R.drawable.state_not_connected)
    }

    fun setStateToConnected() {

        Glide.with(context)
            .load(R.drawable.state_connected)
            .into(statusCard)
//        statusCard.setImageResource(R.drawable.state_connected)
    }

    fun setStateToConnectedSyncing() {
        Glide.with(context)
            .load(R.drawable.state_connected_syncing_arrows)
            .into(statusCard)
//        statusCard.setImageResource(R.drawable.state_connected_syncing_arrows)
    }

    fun setStateToSyncOutdated() {

        Glide.with(context)
            .load(R.drawable.state_not_connected)
            .into(statusCard)
//        statusCard.setImageResource(R.drawable.state_sync_outdated)
    }
}