package com.qhy040404.libraryonetap.utils

import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

object MarkdownUtils {
  /**
   * Parse markdown to HTML
   *
   * @param src markdown
   * @return parsed HTML string
   */
  fun fromString(src: String): String {
    val flavour = CommonMarkFlavourDescriptor()
    val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(src)
    return HtmlGenerator(src, parsedTree, flavour).generateHtml()
  }
}
