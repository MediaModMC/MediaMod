package dev.cbyrne.mediamod.services.media

/*interface Album {

}

interface TrackInfo {
    var album: Album
    var progress_ms
}

interface Track {
    var progress: Int
    var playing: Boolean
}
 */

data class AlbumImage(val url: String)
data class Artist(val name: String)
data class Album(val artists: List<Artist>, val images: List<AlbumImage>, val name: String)
data class TrackInfo(val album: Album, val duration: Int, val name: String)
data class Track(val progress: Int, val playing: Boolean, val info: TrackInfo)