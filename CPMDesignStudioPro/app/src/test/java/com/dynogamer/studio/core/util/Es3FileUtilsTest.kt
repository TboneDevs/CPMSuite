package com.dynogamer.studio.core.util

import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Es3FileUtilsTest {

    @Test
    fun `isEs3File returns true for es3 extension`() {
        assertTrue(Es3FileUtils.isEs3File("slot1.es3"))
        assertTrue(Es3FileUtils.isEs3File("SLOT2.ES3"))
    }

    @Test
    fun `isEs3File returns false for non-es3 extension`() {
        assertFalse(Es3FileUtils.isEs3File("slot1.json"))
        assertFalse(Es3FileUtils.isEs3File("backup.zip"))
        assertFalse(Es3FileUtils.isEs3File("readme.txt"))
    }

    @Test
    fun `generateProjectName returns non-empty string`() {
        val name = Es3FileUtils.generateProjectName("slot1.es3", "CPM1")
        assertTrue(name.isNotEmpty())
        assertTrue(name.contains("CPM1") || name.contains("slot1"))
    }

    @Test
    fun `formatFileSize returns correct human-readable sizes`() {
        assertEquals("512 B", Es3FileUtils.formatFileSize(512))
        assertEquals("1.0 KB", Es3FileUtils.formatFileSize(1024))
        assertEquals("1.0 MB", Es3FileUtils.formatFileSize(1024 * 1024))
    }

    @Test
    fun `isCpm1Es3Path detects CPM1 path correctly`() {
        assertTrue(Es3FileUtils.isCpm1Es3Path("/Android/data/com.olzhass.carparking.multipler/files/"))
        assertFalse(Es3FileUtils.isCpm1Es3Path("/Android/data/com.olzhass.carparking.multipler2/files/"))
    }

    @Test
    fun `isCpm2Es3Path detects CPM2 path correctly`() {
        assertTrue(Es3FileUtils.isCpm2Es3Path("/Android/data/com.olzhass.carparking.multipler2/files/"))
        assertFalse(Es3FileUtils.isCpm2Es3Path("/Android/data/com.olzhass.carparking.multipler/files/"))
    }

    @Test
    fun `getSlotNumber extracts slot number from filename`() {
        assertEquals(1, Es3FileUtils.getSlotNumber("slot1.es3"))
        assertEquals(5, Es3FileUtils.getSlotNumber("slot5.es3"))
        assertEquals(-1, Es3FileUtils.getSlotNumber("unknown.es3"))
    }
}
