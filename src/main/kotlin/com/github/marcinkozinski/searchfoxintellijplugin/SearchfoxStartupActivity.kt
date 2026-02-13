package com.github.marcinkozinski.searchfoxintellijplugin

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

/**
 * Initializes the Searchfox plugin on project startup.
 * This ensures that repository mappings are detected before the user interacts with VCS log.
 */
class SearchfoxStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        // Access the service to trigger its initialization
        project.service<SearchfoxHostedRepositoriesManager>()
    }
}
