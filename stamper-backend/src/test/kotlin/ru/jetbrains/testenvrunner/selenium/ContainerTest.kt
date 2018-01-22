package ru.jetbrains.testenvrunner.selenium

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Selenide.`$`
import com.codeborne.selenide.Selenide.open
import com.codeborne.selenide.WebDriverRunner
import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.environment.EnvironmentUtils
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.remote.DesiredCapabilities
import org.testcontainers.containers.BrowserWebDriverContainer
import org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.SKIP
import java.io.File
import java.net.NetworkInterface

class KBrowserWebDriverContainer : BrowserWebDriverContainer<KBrowserWebDriverContainer>()
class GoogleTestWithDockerNoVideo {
    @Rule
    @JvmField
    var chrome: KBrowserWebDriverContainer = KBrowserWebDriverContainer()
            .withDesiredCapabilities(DesiredCapabilities.chrome())
            .withRecordingMode(SKIP, File("/home/nashikhmin/Documents"))

    @Before
    fun setUp() {
        val vncAddress = chrome.vncAddress.split("@")[1]
        //потому что я рукожоп
        val file = File(
                "src/test/resources/scripts/run_vcn.sh")
        val command = CommandLine(file.absolutePath)
        command.addArgument(vncAddress)
        val executor = DefaultExecutor()
        executor.execute(command, EnvironmentUtils.getProcEnvironment())

        val driver = chrome.webDriver
        WebDriverRunner.setWebDriver(driver)
    }

    @After
    fun tearDown() {
        WebDriverRunner.closeWebDriver()
    }

    fun getAddress() = NetworkInterface.getNetworkInterfaces().toList().filter { it.name == "docker0" }.last().inetAddresses.toList().get(
            1).hostAddress

    @Test
    fun search() {
        val address = "http://${getAddress()}:3000"
        open(address)
        `$`(By.id("template-list-name")).`is`(Condition.appear)
    }
}