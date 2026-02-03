package com.github.marcinkozinski.searchfoxintellijplugin.actions

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ide.CopyPasteManager
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager
import java.awt.datatransfer.StringSelection

/**
 * Action that opens the selected file in Searchfox.
 * This action appears in the "Open In" submenu in editor, project view, and navigation bar.
 */
class OpenInSearchfoxAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.isInFirefoxRepository()
    }

    override fun actionPerformed(e: AnActionEvent) {
        buildSearchfoxUrl(e)?.let { BrowserUtil.browse(it) }
    }
}

class CopySearchfoxUrlAction : AnAction() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.isInFirefoxRepository()
    }

    override fun actionPerformed(e: AnActionEvent) {
        buildSearchfoxUrl(e)?.let {
            CopyPasteManager.getInstance().setContents(StringSelection(it))
        }
    }
}
private fun AnActionEvent.isInFirefoxRepository(): Boolean {
    val project = this.project ?: return false

    val virtualFile = getData(CommonDataKeys.VIRTUAL_FILE)
    val gitRepository =
        GitRepositoryManager.getInstance(project).getRepositoryForFile(virtualFile)
    return gitRepository != null && gitRepository.isFirefoxRepository()
}

private fun buildSearchfoxUrl(e: AnActionEvent): String? {
    val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return null
    val project = e.project ?: return null
    val gitRepository =
        GitRepositoryManager.getInstance(project).getRepositoryForFile(virtualFile) ?: return null

    val relativePath = virtualFile.path.removePrefix(gitRepository.root.path).removePrefix("/")
    val lineFragment = buildLineFragment(e.getData(CommonDataKeys.EDITOR))

    // TODO: Make repository configurable
    val repository = "firefox-main"
    return "https://searchfox.org/$repository/source/$relativePath$lineFragment"
}

private fun buildLineFragment(editor: Editor?) = buildString {
    if (editor != null) {
        append("#")
        val selectionModel = editor.selectionModel
        val document = editor.document

        if (selectionModel.hasSelection()) {
            val startLine = document.getLineNumber(selectionModel.selectionStart)
            append(startLine + 1)

            val endLine = document.getLineNumber(selectionModel.selectionEnd)
            if (startLine != endLine) {
                append("-")
                append(endLine + 1)
            }
        } else {
            val currentLine = document.getLineNumber(editor.caretModel.offset)
            append(currentLine + 1)
        }
    }
}

private fun GitRepository.isFirefoxRepository(): Boolean {
    val firefoxPatterns = listOf(
        "https://github.com/mozilla-firefox/firefox",
        "git@github.com:mozilla-firefox/firefox",
    )

    return remotes.map { it.urls }
        .flatten()
        .any { url ->
            firefoxPatterns.any { pattern ->
                url.contains(pattern, ignoreCase = true)
            }
        }
}
