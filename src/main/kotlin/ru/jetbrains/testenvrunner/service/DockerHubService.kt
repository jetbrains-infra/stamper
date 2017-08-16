package ru.jetbrains.testenvrunner.service

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.jetbrains.testenvrunner.model.TerraformScript

@Service
class DockerHubService {
    /**
     * fill list of available tags for docker image if the terraform script has link
     * @param terraformScript which available value params will be filled
     */
    fun fillAvailableDockerTags(terraformScript: TerraformScript) {
        terraformScript.params.forEach {
            if (it.dockerHub != "") it.availableValues = getAvailableTags(it.dockerHub)
        }
    }

    /**
     * Get available tags from Docker registry
     * @param dockerImage - name of image
     * @return list of available tags
     */
    private fun getAvailableTags(dockerImage: String): List<String> {
        return try {
            if (dockerImage.startsWith("http")) {
                getFromPrivateV2Repo(dockerImage)
            } else {
                getFromV1DockerHubRepo(dockerImage)
            }
        } catch (e: Exception) {
            return emptyList()
        }
    }

    private fun getFromV1DockerHubRepo(dockerImage: String): List<String> {
        val restTemplate = RestTemplate()
        val resourceUrl = getUrl(dockerImage)
        val response = restTemplate.getForEntity(resourceUrl, String::class.java)

        val json = Parser().parse(StringBuilder(response.body)) as JsonArray<*>

        return json.toList().map {
            val item = it as JsonObject
            item["name"].toString()
        }
    }

    private fun getFromPrivateV2Repo(dockerImageTagsUrl: String): List<String> {
        val restTemplate = RestTemplate()
        val response = restTemplate.getForEntity(dockerImageTagsUrl, String::class.java)

        val json = Parser().parse(StringBuilder(response.body)) as JsonObject
        val tags = json["tags"] as JsonArray<*>

        return tags.toList().map { it as String }
    }

    private fun getUrl(dockerImage: String): String {
        var imageName = dockerImage
        if (!imageName.contains("/")) {
            imageName = "library/$imageName"
        }
        return "https://registry.hub.docker.com/v1/repositories/$imageName/tags"
    }
}