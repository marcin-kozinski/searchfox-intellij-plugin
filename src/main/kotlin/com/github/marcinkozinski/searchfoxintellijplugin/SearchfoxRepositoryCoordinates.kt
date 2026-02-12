package com.github.marcinkozinski.searchfoxintellijplugin

import git4idea.remote.hosting.HostedRepositoryCoordinates
import java.net.URI

/**
 * Coordinates for a Searchfox-indexed repository
 * @param repositoryName The Searchfox repository name (e.g., "mozilla-central", "comm-central")
 */
data class SearchfoxRepositoryCoordinates(
    val repositoryName: String,
) : HostedRepositoryCoordinates {

    override val serverPath: SearchfoxServerPath
        get() = SearchfoxServerPath.DEFAULT

    override fun getWebURI(): URI {
        return serverPath.toURI().resolve("$repositoryName/")
    }

    override fun toString(): String = repositoryName
}
