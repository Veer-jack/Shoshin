package com.shoshin.app

import com.shoshin.app.ui.screens.getBadgeIconRes
import org.junit.Assert.assertEquals
import org.junit.Test

class BadgeLogicTest {

    @Test
    fun `getBadgeIconRes returns correct resource for known icons`() {
        assertEquals(R.drawable.ic_flame, getBadgeIconRes("streak_7"))
        assertEquals(R.drawable.ic_flame, getBadgeIconRes("streak_365"))
        assertEquals(R.drawable.ic_check, getBadgeIconRes("milestone"))
        assertEquals(R.drawable.ic_sun, getBadgeIconRes("sun"))
    }

    @Test
    fun `getBadgeIconRes returns trophy for unknown or random strings`() {
        assertEquals(R.drawable.ic_trophy, getBadgeIconRes("unknown_icon"))
        assertEquals(R.drawable.ic_trophy, getBadgeIconRes("12345"))
        assertEquals(R.drawable.ic_trophy, getBadgeIconRes(""))
        assertEquals(R.drawable.ic_trophy, getBadgeIconRes("!!!"))
    }
}
