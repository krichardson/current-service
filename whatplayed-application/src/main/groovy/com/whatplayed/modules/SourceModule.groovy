package com.whatplayed.modules

import com.whatplayed.api.Source
import com.whatplayed.dao.SourceDAO

class SourceModule {

    private final SourceDAO sourceDAO

    SourceModule(final SourceDAO sourceDAO) {
        this.sourceDAO = sourceDAO
    }

    List<Source> listSources(Integer limit, Integer offset) {
        return sourceDAO.listSources(limit, offset)
    }

    Source getSource(Long id) {
        return sourceDAO.findById(id)
    }

    Source findSourceByName(String name) {
        return sourceDAO.findByName(name)
    }

}
