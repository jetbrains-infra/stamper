package ru.jetbrains.testenvrunner.controller

import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import ru.jetbrains.testenvrunner.service.AppService

@Controller
@RequestMapping("/")
class IndexController(val appService: AppService) {
    @RequestMapping(method = [RequestMethod.GET])
    fun indexGet(model: Model, auth: OAuth2Authentication?): String {
        return "redirect:${appService.frontendAddress}"
    }
}