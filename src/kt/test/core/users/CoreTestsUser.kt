package kt.test.core.users

import org.testng.Assert.assertEquals
import org.testng.Assert.assertNotEquals
import org.testng.annotations.Test
import java.util.*


class CoreTestsUser {
    private fun getSetupList(): List<BaseTestUserWithID> {
        return listOf(
            TestUserWithAllFields(), TestUserWithRestoredId(), TestUserWithNullAge(),
            TestUserWithNullSecondName(), TestUserWithNullLastName(), TestUserWithNullAllFields(),
            TestUserWithUpperLogin()
        )
    }

    @Test
    fun testCreateInstanceUsers() {
        assertEquals(getSetupList().size, 7)
    }

    @Test
    fun testEqualId() {
        assertEquals(TestUserWithRestoredId().id, UUID.fromString("1c05c129-bfa2-466c-9b4c-896237749385"))
    }

    @Test
    fun testEqualsRestoredId() {
        assertEquals(TestUserWithRestoredId(), TestUserWithRestoredId())
    }

    @Test
    fun testEqualsOneToOne() {
        getSetupList().forEach {
            assertEquals(it, it)
        }
    }

    @Test
    fun testFirstNames() {
        val listName = listOf("Vovan", "VovanWithGlasses", "Purp", "Petr", "Pavel", "Vanya", "Vanya")
        val gotListNames = getSetupList().map { user -> user.uProfile.firstName }
        assertEquals(listName, gotListNames)
    }

    @Test
    fun testSecondNames() {
        val listSecondNames = listOf("Sidorov", "Biba", "Jebko", null, "Grafanya", null, "Carlos")
        val gotListSecondNames = getSetupList().map { user -> user.uProfile.secondName }
        assertEquals(listSecondNames, gotListSecondNames)
    }

    @Test
    fun testLastNames() {
        val listLastNames = listOf("Petrovich", "Popovich", "Valentinovich", "Vladimirovich", null, null, "Bombovich")
        val gotListLastNames = getSetupList().map { user -> user.uProfile.lastName }
        assertEquals(listLastNames, gotListLastNames)
    }

    @Test
    fun testAges() {
        val listAges = listOf(22, 22, null, 23, 28, null, 22)
        val gotListAges = getSetupList().map { user -> user.uProfile.age }
        assertEquals(listAges, gotListAges)
    }

    @Test
    fun testLogins() {
        val listLogins =
            listOf("sidoroff", "Popovka", "JebkoTheBest", "petrr", "pavelDurovYa", "VanyaPupZemli", "UPPERCASELOGIN")
        val gotListLogins = getSetupList().map { user -> user.auth.login }
        assertEquals(listLogins, gotListLogins)
    }

    @Test
    fun testOneHashAfterRecreate() {
        val oldHash = TestUserWithAllFields().auth.hashPass
        val newHash = TestUserWithAllFields().auth.hashPass

        assertEquals(oldHash, newHash)
    }

    @Test
    fun testHashPasswords() {
        val listHashes = listOf(
            "30e77ecef3c84f90635dd950ffbce429", "c2e55e75e592023cdcbe029816c14e07",
            "c6b4e242e15bf8044ee90b50fa35b696", "238dc5c84f170023e594b68713e047c2",
            "140ab7f7888a78ef2a868a1c01de279d", "c3cfa11a4c3b83edc63a7d6dc68e7863",
            "30452ddb1eacc38932df941d0dc91bb8"
        )

        val gotListHashes = getSetupList().map { user -> user.auth.hashPass }
        assertEquals(listHashes, gotListHashes)
    }

    @Test
    fun testNotEqualAfterRecreate() {
        val oldUser = TestUserWithAllFields()
        val newUser = TestUserWithAllFields()

        assertNotEquals(oldUser, newUser)
    }
}