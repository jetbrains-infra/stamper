package ru.jetbrains.testenvrunner.selenium

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.*
import com.codeborne.selenide.SelenideElement
import org.junit.Test
import org.openqa.selenium.By
import ru.jetbrains.testenvrunner.utils.generateRandomWord
import kotlin.test.assertTrue

/**
 * Test elements of main page
 */
class MainPageTest : AbstractSeleniumTest() {
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

    @Test
    fun testLogin() {
        openMainPage()
        get(By.id("logout-btn")).`is`(disappear)
        get(By.id("login-btn")).`is`(appear)

        loginByGoogle()

        get(By.id("username-span")).shouldHave(text("stamper.app.test"))
        get(By.id("logout-btn")).should(exist)
        get(By.id("logout-btn")).click()
        get(By.id("logout-btn")).should(disappear)
        get(By.id("username-span")).should(disappear)
        get(By.id("login-btn")).should(appear)
    }
}

/**
 * Test elements of main page
 */
class RunStackPageTest : AbstractSeleniumTest() {
    @Test
    fun testRunStackForm() {
        openRunStack()
        arrayListOf("name", "version", "external_port").forEach {
            get("//*[@name='$it']").should(exist)
            get("//label[text()='$it']").should(exist)
        }
        //check avaliable versions
        arrayListOf("latest", "5.5", "8.0").forEach {
            get("//select/option[@value=\"latest\"]").should(exist)
        }

        get(By.id("run-script-btn")).click()
        get("//span[text()=\"You should be authenticated in the system\"]").should(appear)
    }
}

/**
 * Test use cases
 */
class UseCaseTest : AbstractSeleniumTest() {
    @Test
    fun testRunAndDestroyStack() {
        openMainPage()
        loginByGoogle()
        get("//li[@id='ubuntu-template']/a").click()
        val name = generateRandomWord()
        val version = "16.04"
        inputStackParam(name, version)

        val stackStatusElement = get("//*[@id=\"stack-status\"]/span")
        stackStatusElement.shouldHave(text("IN_PROGRESS"))
        stackStatusElement.waitUntil(text("APPLIED"), 60 * 1000)

        validateMainStackForm()
        validateDetailInfo(name, version)
        validateStateInfo()
        validateLogs()

        clickDestroy(stackStatusElement)
    }

    private fun clickDestroy(stackStatusElement: SelenideElement) {
        get("//button[text()='Destroy']").click()
        stackStatusElement.shouldHave(text("DESTROYED"))
    }

    private fun validateMainStackForm() {
        get(By.id("user-info")).shouldHave(text("stamper.app.test@gmail.com"))
        get(By.id("createdDate")).should(appear)
        get(By.id("notificationDate")).should(appear)
        get(By.id("expiredDate")).should(appear)
    }

    private fun validateDetailInfo(name: String, version: String) {
        get("//li[text()='Detailed Info']").click()
        get(By.id("detailed-info-tab")).shouldBe(visible)
        get("//p[@id='name']/span[1]").shouldBe(text("name"))
        get("//p[@id='name']/span[2]").shouldBe(text(name))
        get("//p[@id='version']/span[1]").shouldBe(text("version"))
        get("//p[@id='version']/span[2]").shouldBe(text(version))
    }

    private fun validateStateInfo() {
        get("//li[text()='Stack State Info']").click()
        val stateInfo = get(By.id("state-info-tab"))
        stateInfo.shouldBe(visible)
        stateInfo.shouldHave(text("docker_container.ubuntu"))
    }

    private fun validateLogs() {
        get("//li[text()='Logs']").click()
        val logs = get(By.id("logs-tab"))
        logs.shouldBe(visible)
        get("//*[@id='logs-tab']//a[text()='terraform apply']").click()
        val log = get("//*[@id='logs-tab']//pre")
        log.shouldBe(visible)
        log.shouldHave(text("Apply complete!"))
    }

    private fun inputStackParam(name: String, version: String) {
        get("//input[@name='name']").clear()
        get("//input[@name='name']").sendKeys(name)
        get("//select[@name='version']").selectOption(version)
        get(By.id("run-script-btn")).click()
        get(By.id("stack-name-title")).should(appear)
    }
}