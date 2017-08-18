package ru.jetbrains.testenvrunner.service

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.jetbrains.testenvrunner.model.TerraformScript
import ru.jetbrains.testenvrunner.model.TerraformScriptParam

@Service
class DockerService {
    private val DOCKER_HUB_PREFIX: String = "docker-hub:"
    private val DOCKER_REGISTRY_PREFIX: String = "docker-registry:"

    /**
     * fill list of available tags for docker image if the terraform script has link
     * @param terraformScript which available value params will be filled
     */
    fun fillAvailableDockerTags(terraformScript: TerraformScript) {
        terraformScript.params.forEach {
            it.availableValues = getTags(it)
        }
    }

    private fun getTags(scriptParam: TerraformScriptParam): List<String> {
        val lowerDescription = scriptParam.description.toLowerCase()
        try {
            if (lowerDescription.startsWith(DOCKER_HUB_PREFIX)) {
                return getDockerHubTags(lowerDescription.substringAfter(DOCKER_HUB_PREFIX))
            }
            if (lowerDescription.startsWith(DOCKER_REGISTRY_PREFIX)) {
                return getRegistryTags(lowerDescription.substringAfter(DOCKER_REGISTRY_PREFIX))
            }
        } catch (e: Exception) {
            scriptParam.msg = "Tag list for ${scriptParam.description} is unavailable"
        }
        return emptyList()
    }

    private fun getDockerHubTags(repo: String): List<String> {
        val url = getDockerHubUrl(repo)
        return getTagsFromDockerHub(url)

    }

    private fun getRegistryTags(tag: String): List<String> {
        val url = getRegistryUrl(tag)
        return getTagsFromRegistry(url)
    }

    private fun getRegistryUrl(tag: String): String {
        val params = tag.split("/")
        val host = params[0]
        val user = params[1]
        val repo = params[2]

        return "http://$host/v2/$user/$repo/tags/list"
    }

    private fun getTagsFromDockerHub(url: String): List<String> {
        val restTemplate = RestTemplate()
        val response = restTemplate.getForEntity(url, String::class.java)

        val json = Parser().parse(StringBuilder(response.body)) as JsonArray<*>

        return json.toList().map {
            val item = it as JsonObject
            item["name"].toString()
        }
    }

    private fun getTagsFromRegistry(url: String): List<String> {
        val restTemplate = RestTemplate()
        val response = restTemplate.getForEntity(url, String::class.java)

        val json = Parser().parse(StringBuilder(response.body)) as JsonObject
        val tags = json["tags"] as JsonArray<*>

        return tags.toList().map { it as String }
    }

    private fun getDockerHubUrl(dockerImage: String): String {
        var imageName = dockerImage
        if (!imageName.contains("/")) {
            imageName = "library/$imageName"
        }
        return "https://registry.hub.docker.com/v1/repositories/$imageName/tags"
    }
}