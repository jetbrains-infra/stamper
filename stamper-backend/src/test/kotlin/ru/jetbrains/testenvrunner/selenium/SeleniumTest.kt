package ru.jetbrains.testenvrunner.selenium

import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.Selenide.*
import com.codeborne.selenide.SelenideElement
import org.openqa.selenium.By

/**
 * Abstract base class for Selenide testing
 */
abstract class SeleniumTest {
    fun get(xpath: String): SelenideElement {
        return `$`(By.xpath(xpath))
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

    /**
     * Go to main page of the App
     */
    protected fun openMainPage() = open("http://localhost:8080")

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