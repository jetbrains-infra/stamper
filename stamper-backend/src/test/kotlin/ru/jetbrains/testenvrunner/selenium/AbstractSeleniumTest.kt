package ru.jetbrains.testenvrunner.selenium

import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.Selenide.*
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.WebDriverRunner
import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.environment.EnvironmentUtils
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.ClassRule
import org.openqa.selenium.By
import org.openqa.selenium.remote.DesiredCapabilities
import org.testcontainers.containers.BrowserWebDriverContainer
import java.io.File
import java.net.NetworkInterface

class KBrowserWebDriverContainer : BrowserWebDriverContainer<KBrowserWebDriverContainer>()

/**
 * Abstract base class for Selenide testing
 */
abstract class AbstractSeleniumTest {
    companion object {
        @ClassRule
        @JvmField
        var browserContainer: KBrowserWebDriverContainer = KBrowserWebDriverContainer()
                .withDesiredCapabilities(DesiredCapabilities.chrome())
                .withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.SKIP, File("/home/nashikhmin/Documents"))

        init {
            System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver")
            System.setProperty("selenide.browser", "Chrome")
        }

        @AfterClass
        @JvmStatic
        fun after() {
            close()
        }

        @BeforeClass
        @JvmStatic
        fun before() {
            initWebDriver()
            startVcn()
        }

        private fun initWebDriver() {
            val driver = browserContainer.webDriver
            WebDriverRunner.setWebDriver(driver)
        }

        private fun startVcn() {
            val vncAddress = browserContainer.vncAddress.split("@")[1]
            //потому что я рукожоп
            val file = File(
                    "src/test/resources/scripts/run_vcn.sh")
            val command = CommandLine(file.absolutePath)
            command.addArgument(vncAddress)
            val executor = DefaultExecutor()
            executor.execute(command, EnvironmentUtils.getProcEnvironment())
        }
    }

    private fun getHostAddress() = NetworkInterface.getNetworkInterfaces().toList().filter { it.name == "docker0" }.last().inetAddresses.toList().get(
            1).hostAddress

    fun getAppAddress() = "http://${getHostAddress()}:3000"
    fun get(xpath: String): SelenideElement {
        return `$`(By.xpath(xpath))
    }

    fun get(selector: By): SelenideElement {
        return `$`(selector)
    }

    fun all(xpathSelector: String): ElementsCollection {
        return `$$`(By.xpath(xpathSelector))
    }

    /**
     * Go to main page of the App
     */
    protected fun openMainPage() {
        open(getAppAddress())
    }

    /**
     * Go to run Stack form
     */
    protected fun openRunStack() = open("${getAppAddress()}/template/mysql/run")

    /**
     * Login to app by Google oauth2
     */
    protected fun loginByGoogle() {
        get(By.id("login-btn")).click()
        get("//*[@id=\"identifierId\"]").sendKeys("stamper.app.test")
        get("//*[@id=\"identifierNext\"]/content").click()
        get("//input[@type='password']").sendKeys("stamperapptest")
        get("//div[@role='button']").click()
    }
}