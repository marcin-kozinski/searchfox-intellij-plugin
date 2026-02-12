package com.github.marcinkozinski.searchfoxintellijplugin

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import git4idea.remote.hosting.HostedGitRepositoriesManager
import git4idea.remote.hosting.gitRemotesFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

/**
 * Manages known Searchfox repositories in a project.
 * This is a project-level service that maintains a flow of detected Mozilla repositories.
 */
@Service(Service.Level.PROJECT)
class SearchfoxHostedRepositoriesManager(
    project: Project,
    cs: CoroutineScope
) : HostedGitRepositoriesManager<SearchfoxGitRepositoryMapping> {

    override val knownRepositoriesState: StateFlow<Set<SearchfoxGitRepositoryMapping>> =
        gitRemotesFlow(project)
            .distinctUntilChanged()
            .map { remotes ->
                remotes.mapNotNull { remote ->
                    SearchfoxGitRepositoryMapping.create(remote)
                }.toSet()
            }
            .onEach { repos ->
                LOG.debug("Detected Searchfox repositories: ${repos.map { it.repositoryName }}")
            }
            .stateIn(cs, SharingStarted.Eagerly, emptySet())

    companion object {
        private val LOG = logger<SearchfoxHostedRepositoriesManager>()
    }
}
