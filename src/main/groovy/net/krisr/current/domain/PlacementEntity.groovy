package net.krisr.current.domain

import groovy.transform.EqualsAndHashCode

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = 'placement')
@EqualsAndHashCode(includes = ['chart','position'])
class PlacementEntity implements Serializable {

    @Id
    @Column
    @NotNull
    Integer position

    @Id
    @ManyToOne
    @NotNull
    ChartEntity chart

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    SongEntity song



}
