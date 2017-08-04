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
        val restTemplate = RestTemplate()
        val fooResourceUrl = "https://registry.hub.docker.com/v1/repositories/$dockerImage/tags"
        val response = restTemplate.getForEntity(fooResourceUrl, String::class.java)

        val json = Parser().parse(StringBuilder(response.body)) as JsonArray<*>

        val availableVersions = mutableListOf<String>()
        json.forEach {
            val item = it as JsonObject
            availableVersions.add(item["name"].toString())
        }
        return availableVersions
    }
}