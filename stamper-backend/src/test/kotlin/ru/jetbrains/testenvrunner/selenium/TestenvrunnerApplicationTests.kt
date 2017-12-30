package ru.jetbrains.testenvrunner.selenium

import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.environment.EnvironmentUtils
import org.junit.Rule
import org.junit.Test
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import org.rnorth.visibleassertions.VisibleAssertions.assertTrue
import org.testcontainers.containers.BrowserWebDriverContainer
import org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL
import java.io.File

class KBrowserWebDriverContainer : BrowserWebDriverContainer<KBrowserWebDriverContainer>()

/**
 * Simple example of plain Selenium usage.
 */
class SeleniumContainerTest {
    @Rule
    @JvmField
    var browser: KBrowserWebDriverContainer = KBrowserWebDriverContainer()
            .withDesiredCapabilities(DesiredCapabilities.chrome())
            .withRecordingMode(RECORD_ALL, File("/home/nashikhmin/git"))

    init {

    }

    @Test
    fun simplePlainSeleniumTest() {
        val vncAddress = browser.vncAddress.split("@")[1]
        val file = File("/home/nashikhmin/git/stamper/stamper-backend/src/test/kotlin/ru/jetbrains/testenvrunner/selenium/run_vcn.sh")
        file.writeText("#!/usr/bin/env bash\n" +
                "nohup /bin/sh -c \"echo secret | /usr/bin/vncviewer  -autopass $vncAddress\"  > /dev/null 2>&1 &")
        val command = CommandLine(
                file)
        val executor = DefaultExecutor()
        executor.execute(command, EnvironmentUtils.getProcEnvironment())

        val driver: RemoteWebDriver = browser.webDriver
        driver.get("https://wikipedia.org")
        val t = browser.vncAddress
        val searchInput = driver.findElementByName("search")
        searchInput.sendKeys("Rick Astley")
        searchInput.submit()

        val otherPage = driver.findElementByLinkText("Rickrolling")
        otherPage.click()

        val expectedTextFound = driver.findElementsByCssSelector("p")
                .stream()
                .anyMatch { element -> element.text.contains("meme") }

        assertTrue("The word 'meme' is found on a page about rickrolling", expectedTextFound)
    }
}