package net.krisr.current.domain

import org.joda.time.LocalDate

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = 'chart')
class ChartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(nullable = false)
    @NotNull
    LocalDate date

    @OneToMany(fetch = FetchType.LAZY, mappedBy = 'chart')
    List<PlacementEntity> placements

}
