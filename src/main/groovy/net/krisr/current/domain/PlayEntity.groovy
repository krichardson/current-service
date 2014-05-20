package net.krisr.current.domain

import org.joda.time.LocalDateTime

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = 'play')
class PlayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    SongEntity song

    @Column(name = 'play_time', nullable = false)
    @NotNull
    LocalDateTime playTime

}
