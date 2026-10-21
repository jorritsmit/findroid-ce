package dev.jdtech.jellyfin.core.presentation.downloader

import dev.jdtech.jellyfin.utils.Downloader
import dev.jdtech.jellyfin.utils.download.DownloadStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class DownloadProgressEstimateTest {
    @Test
    fun `capped transcode estimates progress from bitrate and duration`() {
        val snapshot = Downloader.Progress(DownloadStatus.RUNNING, 0, 324_000_000L, -1L)
        val result = snapshot.withEstimatedTotal(3_763_917_725L, 720_000, 72_000_000_000L)

        assertEquals(648_000_000L, result.totalBytes)
        assertEquals(50, result.progress)
    }

    @Test
    fun `server reported size takes precedence over the estimate`() {
        val snapshot = Downloader.Progress(DownloadStatus.RUNNING, 25, 500_000L, 2_000_000L)

        assertSame(snapshot, snapshot.withEstimatedTotal(3_763_917_725L, 720_000, 72_000_000_000L))
    }

    @Test
    fun `reaching or exceeding the estimate holds progress at 99 percent`() {
        for (downloaded in listOf(648_000_000L, 700_000_000L)) {
            val snapshot = Downloader.Progress(DownloadStatus.RUNNING, 0, downloaded, -1L)
            val result = snapshot.withEstimatedTotal(3_763_917_725L, 720_000, 72_000_000_000L)

            assertEquals(648_000_000L, result.totalBytes)
            assertEquals(99, result.progress)
        }
    }

    @Test
    fun `uncapped downloads and missing duration fall back to original size`() {
        val snapshot = Downloader.Progress(DownloadStatus.RUNNING, 0, 500_000L, -1L)
        val uncapped = snapshot.withEstimatedTotal(1_000_000L, null, 72_000_000_000L)
        val missingDuration = snapshot.withEstimatedTotal(1_000_000L, 720_000, 0L)

        assertEquals(1_000_000L, uncapped.totalBytes)
        assertEquals(50, uncapped.progress)
        assertEquals(uncapped, missingDuration)
    }

    @Test
    fun `a cap above the source bitrate does not inflate the estimate`() {
        val snapshot = Downloader.Progress(DownloadStatus.RUNNING, 0, 500_000L, -1L)
        val result = snapshot.withEstimatedTotal(1_000_000L, 6_000_000, 72_000_000_000L)

        assertEquals(1_000_000L, result.totalBytes)
        assertEquals(50, result.progress)
    }

    @Test
    fun `bitrate estimate works without an original size`() {
        val snapshot = Downloader.Progress(DownloadStatus.RUNNING, 0, 324_000_000L, -1L)
        val result = snapshot.withEstimatedTotal(0L, 720_000, 72_000_000_000L)

        assertEquals(648_000_000L, result.totalBytes)
        assertEquals(50, result.progress)
    }

    @Test
    fun `missing size and duration leave progress unknown`() {
        val snapshot = Downloader.Progress(DownloadStatus.RUNNING, 0, 500_000L, -1L)

        assertSame(snapshot, snapshot.withEstimatedTotal(0L, 720_000, 0L))
    }
}
