package pl.pietrzak.services

import pl.pietrzak.webhook.GitHubPullRequestPayload

object WebhookHandlerService {
    suspend fun handlePullRequest(payload: GitHubPullRequestPayload) {
        val pr = payload.pullRequest
        val repo = payload.repository

        println("Received PR: ${pr.id} by ${pr.head.ref} on ${repo.full_name}")

        // If needed, fetch PR diff with a service:
        // val diff = githubApiService.fetchPullRequestDiff(repo.owner.login, repo.name, pr.number, accessToken)
        // println(diff)
    }
}