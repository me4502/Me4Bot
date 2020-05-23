/*
 * Copyright (c) Me4502 (Matthew Miller)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.me4502.me4bot.discord.module;

import com.me4502.me4bot.discord.Me4Bot;
import com.me4502.me4bot.discord.util.PermissionRoles;
import com.me4502.me4bot.discord.util.PunishmentUtil;
import com.me4502.me4bot.discord.util.StringUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;

import javax.annotation.Nonnull;

public class PingWarning extends ListenerAdapter implements Module {

    private final HashMap<String, Integer> spamTimes = new HashMap<>();

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        // Don't check for people who are allowed to ping
        if (Me4Bot.isAuthorised(event.getMember(), PermissionRoles.TRUSTED) || event.getMember() == null) {
            return;
        }

        boolean mentionsDev = event.getMessage().getMentionedMembers()
                .stream()
                .anyMatch(user -> Me4Bot.isAuthorised(user, PermissionRoles.MODERATOR));

        if (mentionsDev) {
            event.getChannel().sendMessage("Hey " + StringUtil.annotateUser(
                    event.getAuthor()
            ) + "! It's against the rule to ping the developers, make sure to read the rules!").queue();
            int spamTime = spamTimes.getOrDefault(event.getAuthor().getId(), 0) + 1;
            spamTimes.put(event.getAuthor().getId(), spamTime);
            if (spamTime >= 4) {
                // Do the ban.
                PunishmentUtil.banUser(event.getGuild(), event.getAuthor(), "Repeatedly pinging developers.");
            } else if (spamTime >= 3) {
                // Kick
                PunishmentUtil.kickUser(event.getGuild(), event.getMember(), "Repeatedly pinging developers.");
            }
        }
    }
}
