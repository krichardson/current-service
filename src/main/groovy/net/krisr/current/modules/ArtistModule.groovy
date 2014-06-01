package net.krisr.current.modules

import net.krisr.current.api.Artist
import net.krisr.current.dao.ArtistDAO

class ArtistModule {

    private final ArtistDAO artistDAO

    ArtistModule(ArtistDAO artistDAO) {
        this.artistDAO = artistDAO
    }

    protected Artist findOrCreateArtist(String name) {
        Artist artist = artistDAO.findByName(name)
        if (!artist) {
            artist = new Artist(name: name)
            artist.id = artistDAO.create(name)
        }
        return artist
    }
}
