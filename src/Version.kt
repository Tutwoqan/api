package io.github.totwoqan

/**
 * Optional annotation for [TaskMetaFactory] and for `.kts` files.
 * If the maximum supported api version less than specified, build will be aborted.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FILE, AnnotationTarget.CLASS)
annotation class MinimumApiVersionRequired(val version: String)

class Version private constructor(
    private val language: Language,
    private val ordinal: UInt,
    private val type: Type,
) {
    private enum class Language(val raw: String) {
        KotlinJVM("kj")
    }

    private enum class Type(val raw: String) {
        Draft("d"),
        Fixes("f"),
        NewFeatures("")
    }

    @Suppress("RemoveRedundantQualifierName")
    companion object {
        private val pattern = Regex("""([a-z]+)(\d+)([a-z]*)""")

        @Suppress("LocalVariableName")
        @JvmStatic
        fun fromString(raw: String): Version {
            val match = this.pattern.find(raw)
                ?: throw IllegalArgumentException("Version representation is invalid")
            val language = Version.Language.values().firstOrNull { L -> L.raw == match.groupValues[1] }
                ?: throw IllegalArgumentException("Unknown language")
            val type = Version.Type.values().firstOrNull { T -> T.raw == match.groupValues[3] }
                ?: throw IllegalArgumentException("Unknown version type")
            return Version(language, match.groupValues[2].toUInt(), type)
        }

        val compileApiVersion = Version.fromString("kj1d")
    }

    override fun toString() = "${this.language.raw}${this.ordinal}${this.type.raw}"

    /**
     * Checks if this version have all features of [parent] version.
     */
    fun implements(parent: Version): Boolean {
        return this.ordinal == parent.ordinal // now only one version available
    }

    override fun equals(other: Any?): Boolean {
        if (other === null || other !is Version) return false
        return this.ordinal == other.ordinal
    }

    override fun hashCode(): Int {
        return this.ordinal.hashCode()
    }
}
