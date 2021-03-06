/*
 *     MediaMod is a mod for Minecraft which displays information about your current track in-game
 *     Copyright (C) 2021 Conor Byrne
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.mediamod.launch

import net.minecraftforge.fml.common.FMLModContainer
import net.minecraftforge.fml.common.ILanguageAdapter
import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.fml.relauncher.Side
import org.apache.logging.log4j.LogManager
import java.lang.reflect.Field
import java.lang.reflect.Method

@Suppress("UNUSED")
class KotlinLanguageAdapter : ILanguageAdapter {
    private val logger = LogManager.getLogger("ILanguageAdapter/Kotlin")

    override fun setProxy(target: Field, proxyTarget: Class<*>, proxy: Any) {
        logger.debug("Setting proxy on target: {}.{} -> {}", target.declaringClass.simpleName, target.name, proxy)

        val instanceField = findInstanceFieldOrThrow(proxyTarget)
        val modObject = findModObjectOrThrow(instanceField)

        target.set(modObject, proxy)
    }

    override fun getNewInstance(
        container: FMLModContainer?,
        objectClass: Class<*>,
        classLoader: ClassLoader,
        factoryMarkedAnnotation: Method?
    ): Any {
        logger.debug("Constructing new instance of {}", objectClass.simpleName)

        val instanceField = findInstanceFieldOrThrow(objectClass)
        return findModObjectOrThrow(instanceField)
    }

    override fun supportsStatics() = false
    override fun setInternalProxies(mod: ModContainer?, side: Side?, loader: ClassLoader?) = Unit

    private fun findInstanceFieldOrThrow(targetClass: Class<*>): Field {
        return try {
            targetClass.getField("INSTANCE")
        } catch (exception: NoSuchFieldException) {
            throw noInstanceFieldException(exception)
        } catch (exception: SecurityException) {
            throw instanceSecurityException(exception)
        }
    }

    private fun findModObjectOrThrow(instanceField: Field): Any {
        return try {
            instanceField.get(null)
        } catch (exception: IllegalArgumentException) {
            throw unexpectedInitializerSignatureException(exception)
        } catch (exception: IllegalAccessException) {
            throw wrongVisibilityOnInitializerException(exception)
        }
    }

    private fun noInstanceFieldException(exception: Exception) =
        KotlinAdapterException("Couldn't find INSTANCE singleton on Kotlin @Mod container", exception)

    private fun instanceSecurityException(exception: Exception) =
        KotlinAdapterException("Security violation accessing INSTANCE singleton on Kotlin @Mod container", exception)

    private fun unexpectedInitializerSignatureException(exception: Exception) =
        KotlinAdapterException("Kotlin @Mod object has an unexpected initializer signature, somehow?", exception)

    private fun wrongVisibilityOnInitializerException(exception: Exception) =
        KotlinAdapterException("Initializer on Kotlin @Mod object isn't `public`", exception)

    private class KotlinAdapterException(message: String, exception: Exception) :
        RuntimeException("Kotlin adapter error - do not report to Forge! $message", exception)
}