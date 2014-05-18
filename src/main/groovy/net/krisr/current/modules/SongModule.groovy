package net.krisr.current.modules

import net.krisr.current.dao.SongDAO
import net.krisr.current.domain.ArtistEntity
import net.krisr.current.domain.SongEntity
import org.dozer.Mapper

class SongModule {

    Mapper beanMapper
    SongDAO songDAO

    SongModule(Mapper beanMapper, SongDAO songDAO) {
        this.beanMapper = beanMapper
        this.songDAO = songDAO
    }

    protected SongEntity findOrCreateSong(ArtistEntity artistEntity, String title) {
        SongEntity songEntity = songDAO.findByArtistAndTitle(artistEntity, title)
        if (!songEntity) {
            songEntity = new SongEntity(artist: artistEntity, title: title)
            songDAO.createOrUpdate(songEntity)
        }
        return songEntity
    }

}
