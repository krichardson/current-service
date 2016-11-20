package com.whatplayed.modules

import com.whatplayed.api.Artist
import com.whatplayed.api.Song
import com.whatplayed.dao.SongDAO

class SongModule {

    private final SongDAO songDAO

    SongModule(SongDAO songDAO) {
        this.songDAO = songDAO
    }

    protected Song findOrCreateSong(Artist artist, String title) {
        Song song = songDAO.findByArtistAndTitle(artist.id, title)
        if (!song) {
            song = new Song(artist: artist, title: title)
            song.id = songDAO.create(title, artist.id)
        }
        return song
    }

}
