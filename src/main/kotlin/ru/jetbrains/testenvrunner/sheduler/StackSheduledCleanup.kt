package ru.jetbrains.testenvrunner.sheduler

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.jetbrains.testenvrunner.model.Stack
import ru.jetbrains.testenvrunner.service.StackService

@Component
class ScheduledCleanup constructor(val stackService: StackService,
                                   val sender: JavaMailSender,
                                   @Value("\${web_address}") val webAddress: String) {

    val relativeAddress = "/stack/%s/prolong"

    @Scheduled(cron = "0 0 * * * *")
    fun cleanupStacks() {
        notifyUsers()
        destroyExpiredStacks()
    }

    private fun sendEmail(stack: Stack) {
        val message = sender.createMimeMessage()
        val helper = MimeMessageHelper(message)

        helper.setTo(stack.user.email)
        helper.setSubject(
                "Terraform Script Executor. The stack ${stack.name} will be deleted soon.\n")
        helper.setText("The stack ${stack.name} will be deleted soon.\n" +
                "Please follow the next link " +
                "$webAddress${relativeAddress.format(
                        stack.name)} to prolong the stack for ${stackService.expireDate} days")

        sender.send(message)
    }

    fun notifyUsers() {
        val notifiedStacks = stackService.getNotifyStacks()
        notifiedStacks.forEach { sendEmail(it); stackService.updateNotificationDates(it) }
    }

    fun destroyExpiredStacks() {
        val expiredStacks = stackService.getExpiredStacks()
        expiredStacks.forEach { stackService.destroyStack(it.name) }
    }
}