package ru.jetbrains.testenvrunner.selenium

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.text
import org.junit.Test
import org.openqa.selenium.By
import kotlin.test.assertTrue

/**
 * Test elements of main page
 */
class MainPageTest : SeleniumTest() {
    @Test
    fun testHeader() {
        openMainPage()
        get(By.id("app-logo")).`is`(Condition.appear)
        get(By.id("login-btn")).`is`(Condition.appear)
    }

    @Test
    fun testAvailableTemplates() {
        openMainPage()
        get(By.id("template-list-name")).`is`(Condition.appear)
        get(By.id("template-list-name")).`is`(text("Available Templates:"))
        val templates = all("//*[@id=\"template-list\"]/li/span")
        templates.shouldHaveSize(4)
        val texts = templates.texts()
        assertTrue(texts.containsAll(listOf("ubuntu", "mysql")))
    }
}