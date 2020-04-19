package dev.cbyrne.mediamod.language

import net.minecraftforge.fml.common.FMLModContainer
import net.minecraftforge.fml.common.ILanguageAdapter
import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.fml.relauncher.Side
import org.apache.logging.log4j.LogManager
import java.lang.reflect.Field
import java.lang.reflect.Method

@Suppress("unused")
class KotlinLanguageAdapter : ILanguageAdapter {
    private val log = LogManager.getLogger()

    override fun supportsStatics(): Boolean {
        return false
    }

    override fun setProxy(target: Field, proxyTarget: Class<*>, proxy: Any) {
        log.debug("Setting proxy: ${target.declaringClass.simpleName}.${target.name} -> $proxy")
        target.set(proxyTarget.kotlin.objectInstance, proxy)
    }

    override fun getNewInstance(
        container: FMLModContainer,
        objectClass: Class<*>,
        classLoader: ClassLoader,
        factoryMarkedAnnotation: Method?
    ): Any {
        log.debug("FML has asked for ${objectClass.simpleName} to be constructed")
        return objectClass.kotlin.objectInstance ?: objectClass.newInstance()
    }


    override fun setInternalProxies(mod: ModContainer?, side: Side?, loader: ClassLoader?) {
        // Nothing to do; FML's got this covered for Kotlin
    }
}