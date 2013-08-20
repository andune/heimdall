TODO LIST
=========
Here is a list of TODOs that I had in mind for the project and never got around to implementing.

If someone wanted to contribute, it would be great to open these as Github issue enhancement requests in the form of user stories and implement code tied to each ticket.

* Implementation of an autoban engine that responded to grief events
* Ability for admins to choose a grief threshold for autoban engine
* Abstract ban engine, with different implementations
* One Ban Engine using built-in Heimdall code. This means if Heimdall were uninstalled, all bans from Heimdall would disappear with the plugin
* One Ban Engine implementing stock ban functionality (using "/ban" command). This could be used to ensure bans wouldn't disappear if Heimdall were uninstalled, it also assures faster ban rejection since Bukkit handles it automatically at client connect
* Admin can define multiple ban engines for use. This could allow Heimdall-native, stock and MCBans all at once, for example.
* A Heimdall unban will unban for all defined engines

* A ban review system, such that moderators logging in can run a command to review all unreviewed bans, and perhaps are reminded to do so when they first login
* Moderators can review the circumstances around a Heimdall autoban and either accept them (thus marking the ban as reviewed and accepted) or reject the ban, thus marking it as invalid and resetting the players banPoints

* Additional logblock plugins for block owner lookup (such as CoreProtect)

* Integration with BlockOwner, if it is ever completed, so that no block logging plugin is required at all. BlockOwner also would facilate a much simpler Heimdall design since all checks could be done synchronously instead of async.

* Async cleanup. While Heimdall does a lot of async work to keep the DB lookup penalty off of the main thread, it has a few cases where it unsafely accesses Bukkit APIs. While, in practice, this works out fine, and Heimdall is designed to fail in a way that it wouldn't affect plugin operations any, in rare cases these calls could cause ConcurrentModificationException on the Bukkit side and cause a server crash. Although I've never seen it happen in almost 18 months of running Heimdall, that doesn't mean it won't ever happen and shouldn't be fixed. One considered implementation fix would be pushing the sync queries back onto the sync thread for final processing, so events would have a 'sync->async->sync' flow if required (where async is the DB lookup).

* Additional tuning to the autofriend routines. Some initial reports indicate that it might be too aggressive in friending new players. Though I'm generally happy with the implementation and the cases I've seen it working, it should probably be tuned from a broader set of inputs than my own limited observations during early testing.

* Move over to anduneCommonLib. Heimdall used some early shared routines from some of my other plugins before I moved them all to a commonLib. Now that the commonLib exists, Heimdall should use that rather than maintaining it's own copy of those utility methods.
* Review of circular buffer error handilng. I haven't reviewed this in a long time, but I had made a mental note to review the error handling to be sure it was sane in the event the circular buffers wrapped. In theory, they should be large enough that they don't ever wrap, but on a large enough server with enough events happening and a slow DB query that locked up async thread processing, it could certainly happen and the behavior should be deterministic and sane (even if sane means old events get dropped).

* Fix exceptions being thrown regularly while Heimdall runs. Some are rare but seem related to changes in Bukkit/Minecraft events since Heimdall was first written, others are related to commands such as /hdi
* Fix new player logging in the grief log. Heimdall is supposed to log when a new player logs in for the first time, but the routine is messed up and logs every login as new player.

* Implement some admin-defined cutoff for antiGriefPoints. That is, if someone accumulates enough antiGriefPoints, Heimdall can be told to simply stop monitoring them. This is to overcome an issue I've seen on my server where a person hasn't been promoted to a rank that Heimdall ignores yet, but they have so many antiGriefPoints that they obviously aren't griefers. Nonetheless, the mods get notifications when they are helping on shared builds until they are finally promoted to an ignored Heimdall rank.

* Implement integration with WorldGuard or possibly even self-recorded regions using WorldEdit integration for marking, to allow ignored areas to be defined. This would allow an admin to tell Heimdall to ignore events from public farms, for example, while still looking out for private farm griefing.
