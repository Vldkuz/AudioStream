package kt.test

import kt.main.utils.Configs

open class TestConfigs: Configs() {
    override val dbUser: String = System.getenv("TEST_USER")
    override val dbPassword: String = System.getenv("TEST_PASSWORD")
    override val dbUrl: String = System.getenv("TEST_DB_URL")
    override val webDavToken: String = System.getenv("TEST_WEB_DAV_TOKEN")
}