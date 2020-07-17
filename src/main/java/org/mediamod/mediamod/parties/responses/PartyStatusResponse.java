package org.mediamod.mediamod.parties.responses;

import com.google.gson.annotations.SerializedName;
import org.mediamod.mediamod.parties.meta.PartyMediaInfo;

public class PartyStatusResponse {
    @SerializedName("track")
    public PartyMediaInfo info;
}
