package net.krisr.current.modules

import net.krisr.current.api.Artist
import net.krisr.current.api.Song
import net.krisr.current.dao.SongDAO

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
