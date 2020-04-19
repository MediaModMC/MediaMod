package dev.cbyrne.mediamod

import net.minecraftforge.fml.client.FMLClientHandler
import java.io.File

val dataDirectory: File = File(FMLClientHandler.instance().getClient().mcDataDir, "mediamod")
const val apiEndpoint: String = "http://localhost:3000"