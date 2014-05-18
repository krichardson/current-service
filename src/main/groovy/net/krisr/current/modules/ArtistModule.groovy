package net.krisr.current.modules

import net.krisr.current.dao.ArtistDAO
import net.krisr.current.domain.ArtistEntity
import org.dozer.Mapper

class ArtistModule {

    Mapper beanMapper
    ArtistDAO artistDAO

    ArtistModule(Mapper beanMapper, ArtistDAO artistDAO) {
        this.beanMapper = beanMapper
        this.artistDAO = artistDAO
    }

    protected findOrCreateArtist(String name) {
        ArtistEntity artistEntity = artistDAO.findByName(name)
        if (!artistEntity) {
            artistEntity = new ArtistEntity(name: name)
            artistDAO.createOrUpdate(artistEntity)
        }
        return artistEntity
    }
}
