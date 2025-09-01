package pl.pietrzak.services

import pl.pietrzak.entity.github.FileDiff
import pl.pietrzak.entity.github.GitHubPullRequestPayload

object WebhookHandlerService {
    suspend fun handlePullRequest(payload: GitHubPullRequestPayload) {
        val pr = payload.pullRequest
        val repo = payload.repository

        println("Received PR: ${pr.id} on ${repo.name}")

        // If needed, fetch PR diff with a service:
        // val diff = githubApiService.fetchPullRequestDiff(repo.owner.login, repo.name, pr.number, accessToken)
        // println(diff)
    }

    fun extractFileDiffs(payload: GitHubPullRequestPayload): List<FileDiff> {
//        payload.pullRequest.diffUrl
        return emptyList()
    }
}