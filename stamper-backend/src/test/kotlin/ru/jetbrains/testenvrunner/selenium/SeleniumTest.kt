package ru.jetbrains.testenvrunner.selenium

import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.Selenide.*
import com.codeborne.selenide.SelenideElement
import org.openqa.selenium.By

/**
 * Abstract base class for Selenide testing
 */
abstract class SeleniumTest {
    fun get(selector: String): SelenideElement {
        return `$`(selector)
    }

    fun get(selector: By): SelenideElement {
        return `$`(selector)
    }

    fun all(xpathSelector: String): ElementsCollection {
        return `$$`(By.xpath(xpathSelector))
    }

    companion object {
        init {
            System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver")
            System.setProperty("selenide.browser", "Chrome")
        }
    }

    protected fun openMainPage() = open("http://localhost:8080")
}