package dev.conorthedev.mediamod.parties.responses;

import com.google.gson.annotations.SerializedName;
import dev.conorthedev.mediamod.parties.meta.PartyMediaInfo;

public class PartyStatusResponse {
    @SerializedName("track")
    public PartyMediaInfo info;
}
