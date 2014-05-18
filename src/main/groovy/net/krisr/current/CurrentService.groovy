package net.krisr.current

import com.google.common.collect.ImmutableList
import com.yammer.dropwizard.Service
import com.yammer.dropwizard.assets.AssetsBundle
import com.yammer.dropwizard.config.Bootstrap
import com.yammer.dropwizard.config.Environment
import com.yammer.dropwizard.db.DatabaseConfiguration
import com.yammer.dropwizard.hibernate.HibernateBundle
import com.yammer.dropwizard.hibernate.SessionFactoryFactory
import com.yammer.dropwizard.migrations.MigrationsBundle
import net.krisr.current.dao.ArtistDAO
import net.krisr.current.dao.ChartDAO
import net.krisr.current.dao.SongDAO
import net.krisr.current.domain.ArtistEntity
import net.krisr.current.domain.ChartEntity
import net.krisr.current.domain.PlacementEntity
import net.krisr.current.domain.SongEntity
import net.krisr.current.modules.ArtistModule
import net.krisr.current.modules.ChartModule
import net.krisr.current.modules.SongModule
import net.krisr.current.resources.ChartResource
import org.dozer.DozerBeanMapper
import org.dozer.Mapper

class CurrentService extends Service<CurrentConfiguration> {
    public static void main(String[] args) throws Exception {
        new CurrentService().run(args)
    }

    public static final List<Class<?>> SERVICE_ENTITIES = [
            ArtistEntity, ChartEntity, PlacementEntity, SongEntity
    ]

    HibernateBundle<CurrentConfiguration> hibernateBundle =
        new HibernateBundle<CurrentConfiguration>(
                ImmutableList.copyOf(SERVICE_ENTITIES),
                new SessionFactoryFactory()) {
            @Override
            public DatabaseConfiguration getDatabaseConfiguration(CurrentConfiguration configuration) {
                return configuration.databaseConfiguration
            }
        }

    MigrationsBundle<CurrentConfiguration> migrationsBundle =
        new MigrationsBundle<CurrentConfiguration>() {
            @Override
            public DatabaseConfiguration getDatabaseConfiguration(CurrentConfiguration configuration) {
                return configuration.databaseConfiguration
            }
        }

    AssetsBundle assetsBundle = new AssetsBundle()

    void addResources(CurrentConfiguration configuration, Environment environment) {

        Mapper beanMapper = new DozerBeanMapper(['dozer.xml']);

        ArtistDAO artistDAO = new ArtistDAO(hibernateBundle.sessionFactory)
        SongDAO songDAO = new SongDAO(hibernateBundle.sessionFactory)
        ChartDAO chartDAO = new ChartDAO(hibernateBundle.sessionFactory)

        ArtistModule artistModule = new ArtistModule(beanMapper, artistDAO)
        SongModule songModule = new SongModule(beanMapper, songDAO)
        ChartModule chartModule = new ChartModule(beanMapper, chartDAO, artistModule, songModule)

        environment.addResource(new ChartResource(chartModule))
    }

    @Override
    public void initialize(Bootstrap<CurrentConfiguration> bootstrap) {
        bootstrap.with {
            name = 'ChartShow'
            addBundle migrationsBundle
            addBundle hibernateBundle
        }
    }

    @Override
    public void run(CurrentConfiguration configuration,
                    Environment environment) throws ClassNotFoundException {
        addResources(configuration, environment)
    }
}
