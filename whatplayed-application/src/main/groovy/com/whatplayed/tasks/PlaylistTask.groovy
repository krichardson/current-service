package com.whatplayed.tasks

import com.google.common.collect.ImmutableMultimap
import io.dropwizard.servlets.tasks.Task
import com.whatplayed.modules.PlayModule

class PlaylistTask extends Task {

    private static final String name = 'playlist'
    private final PlayModule playModule

    PlaylistTask(PlayModule playModule) {
        super(name)
        this.playModule = playModule
    }

    @Override
    public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) {
        output.println('running playlist task')
        playModule.importPlaylist()
    }

}
