package net.krisr.current.tasks

import com.google.common.collect.ImmutableMultimap
import com.yammer.dropwizard.tasks.Task
import net.krisr.current.modules.PlaylistModule
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.context.internal.ManagedSessionContext

class PlaylistTask extends Task {

    private static final String name = 'playlist'
    private final PlaylistModule playlistModule
    private final SessionFactory sessionFactory

    PlaylistTask(SessionFactory sessionFactory, PlaylistModule playlistModule) {
        super(name)
        this.sessionFactory = sessionFactory
        this.playlistModule = playlistModule
    }

    @Override
    public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) {
        output.println('running playlist task')
        //Need to manage our own sessions here
        Session session = sessionFactory.openSession()
        try {
            ManagedSessionContext.bind(session)
            playlistModule.importPlaylist()
        } finally {
            session.close()
        }
    }

}
