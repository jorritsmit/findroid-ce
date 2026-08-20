package dev.jdtech.jellyfin.presentation.player

import android.app.Dialog
import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.jdtech.jellyfin.player.local.R
import dev.jdtech.jellyfin.player.local.presentation.PlayerViewModel
import java.lang.IllegalStateException

class QualitySelectionDialogFragment : DialogFragment() {
    private val viewModel: PlayerViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Bitrate ladder from jellyfin-web's quality options. 0 = no cap: the
        // server direct-plays the original file whenever the profile allows it.
        val qualityTexts = listOf(
            getString(R.string.video_quality_original),
            "4K - 120 Mbps",
            "4K - 80 Mbps",
            "1080p - 40 Mbps",
            "1080p - 20 Mbps",
            "1080p - 10 Mbps",
            "720p - 8 Mbps",
            "720p - 6 Mbps",
            "720p - 4 Mbps",
            "480p - 3 Mbps",
            "480p - 1.5 Mbps",
            "360p - 720 kbps",
        )
        val qualityBitrates = listOf(
            0,
            120_000_000,
            80_000_000,
            40_000_000,
            20_000_000,
            10_000_000,
            8_000_000,
            6_000_000,
            4_000_000,
            3_000_000,
            1_500_000,
            720_000,
        )

        return activity?.let { activity ->
            val builder = MaterialAlertDialogBuilder(activity)
            builder.setTitle(getString(R.string.select_video_quality)).setSingleChoiceItems(
                qualityTexts.toTypedArray(),
                qualityBitrates.indexOf(viewModel.maxBitrate),
            ) { dialog, which ->
                viewModel.selectMaxBitrate(qualityBitrates[which])
                dialog.dismiss()
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDestroy() {
        super.onDestroy()
        // Fix for hiding the system bars on API < 30
        activity?.window?.let {
            WindowCompat.getInsetsController(it, it.decorView).apply {
                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                hide(WindowInsetsCompat.Type.systemBars())
            }
        }
    }
}
