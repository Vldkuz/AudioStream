package kt.test.core

import kt.main.core.Entity
import org.testng.AssertJUnit.assertEquals

interface InstanceGenerator<T> {
    fun createInstance(): T
}

@Suppress("UNCHECKED_CAST")
open class CollectionsBaseTest<out T>(private val objGen: InstanceGenerator<T>) where T : Entity {
    fun <R> testAddToCollection(tMutableCollection: MutableCollection<R>) {
        val obj = objGen.createInstance()
        tMutableCollection.add(obj as R)
        assertEquals(listOf(obj), tMutableCollection)
    }

    fun <R> testRemoveFromCollection(tMutableCollection: MutableCollection<R>) {
        val obj = objGen.createInstance()
        val prevSize = tMutableCollection.size
        tMutableCollection.add(obj as R)
        tMutableCollection.remove(obj as R)
        assertEquals(tMutableCollection.size, prevSize)
    }


    fun <R> testHashCollections(tMutableCollection: MutableSet<R>) {
        val obj = objGen.createInstance()
        tMutableCollection.add(obj as R)
        val size = tMutableCollection.size
        tMutableCollection.add(obj as R)
        assertEquals(size, tMutableCollection.size)
    }

    fun <R> testMapCollection(tMutableMap: MutableMap<R, Int>) {
        val obj = objGen.createInstance()
        val magicNumber = 1
        tMutableMap[obj as R] = magicNumber
        assertEquals(tMutableMap[obj], magicNumber)
    }

    fun <R> testReverseMap(tMap: MutableMap<Int, R>) {
        val obj = objGen.createInstance()
        val magicNumber = 1
        tMap[magicNumber] = obj as R
        assertEquals(tMap[magicNumber], obj)
    }
}