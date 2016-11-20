package net.krisr.current

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import io.dropwizard.Application
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.servlets.tasks.Task
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.dropwizard.jdbi.DBIFactory
import io.dropwizard.migrations.MigrationsBundle
import io.federecio.dropwizard.swagger.SwaggerBundle
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration
import net.krisr.current.dao.ArtistDAO
import net.krisr.current.dao.ChartDAO
import net.krisr.current.dao.PlacementDAO
import net.krisr.current.dao.PlayDAO
import net.krisr.current.dao.SongDAO
import net.krisr.current.dao.PlaySummaryDAO
import net.krisr.current.modules.ArtistModule
import net.krisr.current.modules.ChartModule
import net.krisr.current.modules.PlaylistModule
import net.krisr.current.modules.SongModule
import net.krisr.current.resources.ArtistResource
import net.krisr.current.resources.ChartResource
import net.krisr.current.resources.PlaylistResource
import net.krisr.current.resources.TopPlaysResource
import net.krisr.current.tasks.PlaylistTask
import net.krisr.jdbi.JodaLocalDateArgumentFactory
import net.krisr.jdbi.JodaLocalDateMapper
import net.krisr.jdbi.JodaLocalDateTimeArgumentFactory
import net.krisr.jdbi.JodaLocalDateTimeMapper
import org.skife.jdbi.v2.DBI

class CurrentService extends Application<CurrentConfiguration> {
    public static void main(String[] args) throws Exception {
        new CurrentService().run(args)
    }

    private final MigrationsBundle<CurrentConfiguration> migrationsBundle =
        new MigrationsBundle<CurrentConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(CurrentConfiguration configuration) {
                return configuration.database
            }
        }

    private final SwaggerBundle<CurrentConfiguration> swaggerBundle = new SwaggerBundle<CurrentConfiguration>() {
        @Override
        protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(CurrentConfiguration configuration) {
            return configuration.swaggerBundleConfiguration
        }
    }

    @Override
    public void initialize(Bootstrap<CurrentConfiguration> bootstrap) {
        bootstrap.with {
            addBundle migrationsBundle
            addBundle swaggerBundle
        }
    }

    @Override
    public void run(CurrentConfiguration configuration,
                    Environment environment) throws ClassNotFoundException {

        environment.objectMapper.registerModule(new JodaModule())
        environment.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        environment.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        DBIFactory factory = new DBIFactory()
        DBI jdbi = factory.build(environment, configuration.database, 'postgresql')
        jdbi.registerArgumentFactory(new JodaLocalDateArgumentFactory())
        jdbi.registerMapper(new JodaLocalDateMapper())
        jdbi.registerArgumentFactory(new JodaLocalDateTimeArgumentFactory())
        jdbi.registerMapper(new JodaLocalDateTimeMapper())

        //DOAs
        ArtistDAO artistDAO = jdbi.onDemand(ArtistDAO)
        SongDAO songDAO = jdbi.onDemand(SongDAO)
        ChartDAO chartDAO = jdbi.onDemand(ChartDAO)
        PlacementDAO placementDAO = jdbi.onDemand(PlacementDAO)
        PlayDAO playDAO = jdbi.onDemand(PlayDAO)
        PlaySummaryDAO playSummaryDAO = jdbi.onDemand(PlaySummaryDAO)

        //Modules
        ArtistModule artistModule = new ArtistModule(artistDAO)
        SongModule songModule = new SongModule(songDAO)
        ChartModule chartModule = new ChartModule(chartDAO, placementDAO, artistModule, songModule)
        PlaylistModule playlistModule = new PlaylistModule(playDAO, playSummaryDAO, artistModule, songModule)

        //Tasks
        Task playlistTask = new PlaylistTask(playlistModule)

        environment.jersey().register(new ChartResource(chartModule))
        environment.jersey().register(new ArtistResource(artistModule))
        environment.jersey().register(new PlaylistResource(playlistModule))
        environment.jersey().register(new TopPlaysResource(playlistModule))
        environment.admin().addTask(playlistTask)
    }
}
