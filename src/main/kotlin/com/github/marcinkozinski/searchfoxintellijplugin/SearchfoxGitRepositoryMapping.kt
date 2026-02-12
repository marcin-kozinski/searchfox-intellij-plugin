package com.github.marcinkozinski.searchfoxintellijplugin

import git4idea.remote.GitRemoteUrlCoordinates
import git4idea.remote.hosting.HostedGitRepositoryMapping

/**
 * Maps a git repository to a Searchfox-indexed repository
 */
data class SearchfoxGitRepositoryMapping(
    override val repository: SearchfoxRepositoryCoordinates,
    override val remote: GitRemoteUrlCoordinates
) : HostedGitRepositoryMapping {

    val repositoryName: String
        get() = repository.repositoryName

    companion object {
    /**
     * Creates a mapping if the remote URL corresponds to a Mozilla repository
     */
        fun create(remote: GitRemoteUrlCoordinates): SearchfoxGitRepositoryMapping? {
            val repoName = detectSearchfoxRepository(remote.url) ?: return null
            val coordinates = SearchfoxRepositoryCoordinates(repoName)
            return SearchfoxGitRepositoryMapping(coordinates, remote)
        }

        /**
         * Detects Searchfox repository name from git remote URL
         */
        private fun detectSearchfoxRepository(remoteUrl: String): String? {
            val url = remoteUrl.lowercase()

            return when {
                url.contains("https://github.com/mozilla-firefox/firefox") -> "firefox-main"
                url.contains("git@github.com:mozilla-firefox/firefox") -> "firefox-main"
                else -> null
            }
        }
    }
}
