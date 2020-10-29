package me.dreamhopping.mediamod.media.services.file;

import com.goxr3plus.streamplayer.enums.Status;
import com.goxr3plus.streamplayer.stream.StreamPlayer;
import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
import com.goxr3plus.streamplayer.stream.StreamPlayerException;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;
import me.dreamhopping.mediamod.media.core.IServiceHandler;
import me.dreamhopping.mediamod.media.core.api.MediaInfo;
import me.dreamhopping.mediamod.media.core.api.track.Artist;
import me.dreamhopping.mediamod.media.core.api.track.Track;

import java.io.File;
import java.util.Map;

public class LocalFileService extends StreamPlayer implements StreamPlayerListener, IServiceHandler {
    private MediaInfo mediaInfo = null;
    public File source = new File("D:\\Music\\glaive\\touche\\01. glaive - touche.mp3");

    @Override
    public String displayName() {
        return "Local Files";
    }

    @Override
    public boolean load() {
        return true;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public MediaInfo getCurrentMediaInfo() {
        return mediaInfo;
    }

    @Override
    public int getEstimatedProgress() {
        return mediaInfo == null ? 0 : mediaInfo.timestamp;
    }

    @Override
    public boolean supportsSkipping() {
        return true;
    }

    @Override
    public boolean supportsPausing() {
        return true;
    }

    @Override
    public boolean skipTrack() {
        stop();
        return true;
    }

    @Override
    public boolean pausePlayTrack() {
        if (isUnknown() || isStopped()) {
            addStreamPlayerListener(this);
            try {
                open(source);
                play();
            } catch (StreamPlayerException e) {
                e.printStackTrace();
            }
        } else {
            if (!isPaused()) {
                pause();
            } else {
                resume();
            }
        }

        return true;
    }

    @Override
    public void opened(Object dataSource, Map<String, Object> properties) {
        Track track = new Track();
        track.name = source.getName();

        Artist artist = new Artist();
        artist.name = "Unknown";
        track.artists = new Artist[]{artist};

        mediaInfo = new MediaInfo();
        mediaInfo.track = track;
        mediaInfo.isPlaying = true;
    }

    @Override
    public void progress(int nEncodedBytes, long microsecondPosition, byte[] pcmData, Map<String, Object> properties) {
        mediaInfo.timestamp = (int) microsecondPosition / 1000;
        mediaInfo.track.duration = (int) getDurationInMilliseconds();
    }

    @Override
    public void statusUpdated(StreamPlayerEvent event) {
        final Status status = event.getPlayerStatus();

        if (status == Status.STOPPED) {
            mediaInfo = null;
        }
    }
}
