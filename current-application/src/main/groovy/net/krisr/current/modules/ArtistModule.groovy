package net.krisr.current.modules

import net.krisr.current.api.Artist
import net.krisr.current.dao.ArtistDAO

class ArtistModule {

    private final ArtistDAO artistDAO

    ArtistModule(ArtistDAO artistDAO) {
        this.artistDAO = artistDAO
    }

    List<Artist> listArtists(Integer limit = 50, Integer offset = 0) {
        return artistDAO.listArtists(limit, offset)
    }

    Artist findById(Long id) {
        return artistDAO.findById(id)
    }

    Artist findByName(String name) {
        return artistDAO.findByName(name)
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
