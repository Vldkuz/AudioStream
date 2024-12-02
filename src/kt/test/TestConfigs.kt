package kt.test

open class TestConfigs {
    val dbUser: String = System.getenv("TEST_USER")
    val dbPassword: String = System.getenv("TEST_PASSWORD")
    val dbUrl: String = System.getenv("TEST_DB_URL")
}