package com.github.marcinkozinski.searchfoxintellijplugin

import com.intellij.collaboration.api.ServerPath
import java.net.URI

/**
 * Represents Searchfox server (searchfox.org)
 */
class SearchfoxServerPath private constructor() : ServerPath {

    override fun toURI(): URI = URI("https://searchfox.org")

    override fun toString(): String = "Searchfox"

    override fun equals(other: Any?): Boolean = other is SearchfoxServerPath

    override fun hashCode(): Int = javaClass.hashCode()

    companion object {
        val DEFAULT = SearchfoxServerPath()
    }
}
