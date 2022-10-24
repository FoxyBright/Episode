package ru.rikmasters.gilty.core.module

class ModuleIterator(
    val root: FeatureDefinition
): Iterator<FeatureDefinition> {

    private val includeIterator = root.include().filterIsInstance<FeatureDefinition>().iterator()
    private var current: Iterator<FeatureDefinition>? = null

    var isRootReturned = false

    override fun hasNext(): Boolean = synchronized(this) {
        if(!isRootReturned) return true

        if(current == null)
            return includeIterator.hasNext()

        return if(current!!.hasNext())
            true
        else
            includeIterator.hasNext()
    }


    override fun next(): FeatureDefinition = synchronized(this) {
        if(!isRootReturned) {
            isRootReturned = true
            return root
        }

        if(current == null)
            current = includeIterator.next().iterator()

        return if(current!!.hasNext())
            current!!.next()
        else {
            current = includeIterator.next().iterator()
            current!!.next()
        }
    }
}