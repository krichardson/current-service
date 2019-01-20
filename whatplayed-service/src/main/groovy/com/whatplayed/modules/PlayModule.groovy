package com.whatplayed.modules

import com.whatplayed.api.PlayRequest
import com.whatplayed.api.Source
import com.whatplayed.exception.PlayAlreadyRecordedException
import groovy.util.logging.Slf4j
import com.whatplayed.api.Artist
import com.whatplayed.api.Play
import com.whatplayed.api.PlaySummary
import com.whatplayed.api.PlaylistRequest
import com.whatplayed.api.Song
import com.whatplayed.api.TopPlaysRequest
import com.whatplayed.dao.PlayDAO
import com.whatplayed.dao.PlaySummaryDAO

@Slf4j
class PlayModule {

    private final PlayDAO playDAO
    private final PlaySummaryDAO playSummaryDAO
    private final SourceModule sourceModule
    private final ArtistModule artistModule
    private final SongModule songModule

    PlayModule(PlayDAO playDAO,
               PlaySummaryDAO playSummaryDAO,
               SourceModule sourceModule,
               ArtistModule artistModule,
               SongModule songModule) {
        this.playDAO = playDAO
        this.playSummaryDAO = playSummaryDAO
        this.sourceModule = sourceModule
        this.artistModule = artistModule
        this.songModule = songModule
    }

    List<Play> getPlays(PlaylistRequest request) {
        return playDAO.findPlaysBetween(request.source.id, request.rangeStartTime, request.rangeEndTime)
    }

    List<PlaySummary> getTopPlays(TopPlaysRequest request) {
        if (request.source) {
            return playSummaryDAO.findTopPlaysBetween(request.source.id, request.rangeStartTime, request.rangeEndTime,
                    request.limit, request.offset)
        }
        return playSummaryDAO.findTopPlaysBetween(request.rangeStartTime, request.rangeEndTime,
                request.limit, request.offset)
    }

    Play getMostRecentPlay(Source source) {
        playDAO.findLatestPlay(source.id)
    }

    Play recordPlay(Source source, PlayRequest request) {
        //Check if the source already has a play recorded for that time
        Play play = playDAO.findPlayByTime(source.id, request.playTime)
        if (play) {
            throw new PlayAlreadyRecordedException("${play.song.title} by ${play.song.artist.name} is already" +
                    " recorded at ${request.playTime} for ${source.name}")
        }
        Song song = getOrCreateSong(request.artistName, request.songTitle)
        Long playId = playDAO.create(request.playTime, song.id, source.id)
        return new Play(id: playId, song: song, source: source, playTime: request.playTime)
    }

    private Song getOrCreateSong(String artistName, String songTitle) {
        Artist artist = artistModule.findOrCreateArtist(artistName)
        Song song = songModule.findOrCreateSong(artist, songTitle)
        return song
    }

}
