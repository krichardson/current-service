package net.krisr.current.tasks

import com.google.common.collect.ImmutableMultimap
import io.dropwizard.servlets.tasks.Task
import net.krisr.current.modules.PlaylistModule

class PlaylistTask extends Task {

    private static final String name = 'playlist'
    private final PlaylistModule playlistModule

    PlaylistTask(PlaylistModule playlistModule) {
        super(name)
        this.playlistModule = playlistModule
    }

    @Override
    public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) {
        output.println('running playlist task')
        playlistModule.importPlaylist()
    }

}
