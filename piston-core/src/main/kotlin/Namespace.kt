package dev.sleepyswords.piston

data class Resource(val namespace: Namespace, val path: String) {
    override fun toString(): String {
        return "${namespace.namespace}:$path"
    }
}

@JvmInline
value class Namespace(val namespace: String) {
    fun createResource(path: String): Resource = Resource(namespace = this, path)
}
