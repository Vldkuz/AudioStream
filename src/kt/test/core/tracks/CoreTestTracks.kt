package kt.test.core.tracks

import kt.main.core.Track
import org.testng.Assert.assertEquals
import org.testng.Assert.assertFalse
import org.testng.annotations.Test
import java.util.*

class CoreTestTracks {
    @Test
    fun testRestoredId() {
        val track = TestTrackWithRestoredId() as Track
        assertEquals(track.id, UUID.fromString("dd5e0c5f-0707-406e-b0e8-c51c6cfb3728"))
    }

    @Test
    fun testReflexiveEqual() {
        val track = TestTrackWithRestoredId() as Track
        assertEquals(track, track)
    }

    @Test
    fun testIdentityByUUID() {
        val restoredIdTrack = TestTrackWithRestoredId() as Track
        val randomIdTrack = TestTrackWithRandomId() as Track
        assertFalse(restoredIdTrack == randomIdTrack)
    }

    @Test
    fun testHash() {
        val track = TestTrackWithRestoredId() as Track
        assertEquals(track.hashCode(), 105561605)
    }

    @Test
    fun testHashEquality() {
        assertEquals(TestTrackWithRestoredId().hashCode(), TestTrackWithRestoredId().hashCode())
    }
}