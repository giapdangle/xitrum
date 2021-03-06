package xitrum.routing

object RouteCompiler {
  /**
   * Given the pattern "/articles/:id<[0-9]+>", compiles to
   * Seq(RouteToken("articles", true, None), RouteToken("id", false, Some("[0-9]+".r)))
   */
  def compile(pattern: String): Seq[RouteToken] = {
    // See PathInfo#tokens
    // Remove slash prefix if any so that app developers can write
    // "/articles" or "articles" and the resuls are the same
    val noSlashPrefix = if (pattern.startsWith("/")) pattern.substring(1) else pattern
    val fragments     = noSlashPrefix.split("/", -1)
    fragments.map(compilePatternFragment _)
  }

  def decompile(routeTokens: Seq[RouteToken], forSwagger: Boolean = false): String = {
    if (routeTokens.isEmpty) {
      "/"
    } else {
      routeTokens.foldLeft("") { (acc, t) => acc + "/" + t.decompile(forSwagger) }
    }
  }

  //----------------------------------------------------------------------------

  private def compilePatternFragment(fragment: String): RouteToken = {
    val parts = fragment.split("\\.:")
    if (parts.length == 1) {
      compileNonDotPatternFragment(fragment)
    } else {
      val nonDotRouteTokens =
        compileNonDotPatternFragment(parts.head) +:
        parts.tail.map { part => compileNonDotPatternFragment(':' + part) }
      DotRouteToken(nonDotRouteTokens)
    }
  }

  private def compileNonDotPatternFragment(fragment: String): NonDotRouteToken = {
    val isPlaceholder         = fragment.startsWith(":")
    val regexMarkerStartIndex = fragment.indexOf("<")

    val value =
      if (regexMarkerStartIndex < 0) {
        if (isPlaceholder) fragment.substring(1) else fragment
      } else {
        val valueStartIndex = if (isPlaceholder) 1 else 0
        fragment.substring(valueStartIndex, regexMarkerStartIndex)
      }

    val regex =
      if (regexMarkerStartIndex < 0) {
        None
      } else {
        val regexString = fragment.substring(regexMarkerStartIndex + 1, fragment.length - 1)
        Some(("^" + regexString + "$").r)
      }
    NonDotRouteToken(value, isPlaceholder, regex)
  }
}
