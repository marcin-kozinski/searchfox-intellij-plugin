package com.github.marcinkozinski.searchfoxintellijplugin.actions

import com.github.marcinkozinski.searchfoxintellijplugin.SearchfoxBundle
import com.github.marcinkozinski.searchfoxintellijplugin.SearchfoxHostedRepositoriesManager
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import git4idea.remote.hosting.HostedGitRepositoriesManager
import git4idea.remote.hosting.action.GlobalHostedGitRepositoryReferenceActionGroup
import git4idea.remote.hosting.action.HostedGitRepositoryReference
import java.awt.datatransfer.StringSelection
import java.net.URI

/**
 * Action that opens the selected file in Searchfox.
 * This action appears in the "Open In" submenu in editor, project view, and navigation bar.
 */
class OpenInSearchfoxActionGroup : GlobalHostedGitRepositoryReferenceActionGroup(
    SearchfoxBundle.messagePointer("open.in.searchfox.action"),
    SearchfoxBundle.messagePointer("open.in.searchfox.action.description"),
    null
) {

    override fun repositoriesManager(project: Project): HostedGitRepositoriesManager<*> {
        return project.service<SearchfoxHostedRepositoriesManager>()
    }

    override fun getUri(repository: URI, revisionHash: String): URI {
        return buildSearchfoxUrl(repository, revisionHash)
    }

    override fun getUri(
        repository: URI,
        revisionHash: String,
        relativePath: String,
        lineRange: IntRange?,
    ): URI {
        thisLogger().info(repository.toString())
        return buildSearchfoxUrl(repository, relativePath, lineRange)
    }

    override fun handleReference(reference: HostedGitRepositoryReference) {
        reference.buildWebURI()?.let { BrowserUtil.browse(it) }
    }
}
/**
 * Action that copies a Searchfox link to the selected file.
 */
class SearchfoxCopyLinkActionGroup : GlobalHostedGitRepositoryReferenceActionGroup(
    SearchfoxBundle.messagePointer("copy.searchfox.link.action"),
    SearchfoxBundle.messagePointer("copy.searchfox.link.action.description"),
    null,
    ) {

    override fun repositoriesManager(project: Project): HostedGitRepositoriesManager<*> {
        return project.service<SearchfoxHostedRepositoriesManager>()
    }

    override fun getUri(repository: URI, revisionHash: String): URI {
        return buildSearchfoxUrl(repository, revisionHash)
    }

    override fun getUri(
        repository: URI,
        revisionHash: String,
        relativePath: String,
        lineRange: IntRange?,
    ): URI {
        return buildSearchfoxUrl(repository, relativePath, lineRange)
    }

    override fun handleReference(reference: HostedGitRepositoryReference) {
        reference.buildWebURI()?.let {
            CopyPasteManager.getInstance().setContents(StringSelection(it.toString()))
        }
    }
}

private fun buildSearchfoxUrl(repository: URI, revisionHash: String): URI {
    return repository.resolve("rev/$revisionHash")
}

private fun buildSearchfoxUrl(
    repository: URI,
    relativePath: String,
    lineRange: IntRange?,
): URI {
    val lineFragment = lineRange?.toLineFragment() ?: ""
    return repository.resolve("source/$relativePath$lineFragment")
}

private fun IntRange.toLineFragment() = buildString {
    append("#")
    append(first + 1)
    if (last != first) {
        append("-")
        append(last + 1)
    }
}
