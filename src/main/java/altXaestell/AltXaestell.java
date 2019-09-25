package altXaestell;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.InviteAction;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class AltXaestell extends ListenerAdapter {
    private JDA jda;
    private GuildChannel channel;
    private User admin;
    private PrivateChannel adminChannel;
    private User commandStarter;

    private String finishedMessageTemplate = "*%s* **Finished!**";
    private String messageTemplate = "__**You've been granted beta-testing!**__\n\nYour key is:  %s\nInvite: %s\n";
    private Map<String, String> usersKeywords;
    private Map<String, User> usersMap;
    private Stage stage;

    private enum Stage  {NORMAL, SET_CHANNEL, SET_USERS, SET_ADMIN}

    public void setJda(JDA jda) {
        this.jda = jda;
    }

    public static void main(String[] args) throws Exception  {
        System.out.println("Inside CodeBuster's main");

        BufferedReader br = new BufferedReader(new InputStreamReader(
                    AltXaestell.class.getResourceAsStream("/tokens/AltXaestell.token")));
        String token = br.readLine();
        br.close();

        System.out.println("token: " + token);

        AltXaestell altXaestell = new AltXaestell();

        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(token);
        JDA jda = builder.build();
        jda.awaitReady();

        jda.addEventListener(altXaestell);
        altXaestell.setJda(jda);
    }

    public AltXaestell()  {
        usersKeywords = new HashMap<>();
        usersMap = new HashMap<>();
        stage = Stage.NORMAL;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)  {
        if (event.getAuthor().isBot())  {
            return;
        }

        String message = event.getMessage().getContentRaw();

        if (message.equalsIgnoreCase("!set users"))  {
            stage = Stage.SET_USERS;
            commandStarter = event.getAuthor();
            event.getChannel().sendMessage("*Please, send the list of users that you would like to DM*").queue();
        }
        else if (message.equalsIgnoreCase("!set channel"))  {
            stage = Stage.SET_CHANNEL;
            commandStarter = event.getAuthor();
            event.getChannel().sendMessage("*Please, send an id of a channel you'd like to generate an invite link for.\n" +
                    "Keep in mind, that the bot needs to be a member of the server that channel is in and also have permissions to " +
                    "generate invites*").queue();
        }
        else if (message.equalsIgnoreCase("!set admin"))  {
            stage = Stage.SET_ADMIN;
            commandStarter = event.getAuthor();
            event.getChannel().sendMessage("**Please, send an admin's username#numbers!**").queue();
        }
        else if (message.equalsIgnoreCase("!start"))  {
            String errorReply = "";
            if (usersMap.size()==0)  {
                errorReply += "**You need to first set users. Please use the ```!set users``` command!**\n";
            }

            if (channel==null)  {
                errorReply += "**You need to first set the invite channel. Please use the ```!set channel``` command!**";
            }

            if (!errorReply.equalsIgnoreCase(""))  {
                event.getChannel().sendMessage(errorReply).queue();
                return;
            }

            for (String username: usersMap.keySet())  {
                User user = usersMap.get(username);
                AtomicReference<Invite> userInvite = new AtomicReference<>();

                InviteAction inviteAction = null;
                try  {
                     inviteAction = channel.createInvite().setMaxUses(1);
                }
                catch (Exception e)  {
                    e.printStackTrace();
                    event.getChannel().sendMessage("Couldn't generate an invite link for channel " + channel.getName() +
                            ". Please, check bot's permissions!**").queue();
                }

                if (inviteAction!=null) {
                    userInvite.set(inviteAction.complete());

                    user.openPrivateChannel().queue(new Consumer<PrivateChannel>() {
                        @Override
                        public void accept(PrivateChannel channel) {
                            try {
                                channel.sendMessage(String.format(messageTemplate, usersKeywords.get(username), userInvite.get().getUrl())).queue();
                            } catch (Exception e) {
                                e.printStackTrace();
                                channel.sendMessage("**Couldn't send message to user " + username + " please, check your input!**").queue();
                            }
                        }
                    });
                }

                try  {
                    adminChannel.sendMessage(String.format(finishedMessageTemplate, username)).queue();
                }
                catch (Exception e)  {
                    e.printStackTrace();
                }
            }
        }
        else if (message.equalsIgnoreCase("!view admin"))  {
            if (admin!=null)  {
                event.getChannel().sendMessage("**Progress reports are forwarded to " + admin.getAsTag() + "!**").queue();
            }
            else  {
                event.getChannel().sendMessage("**There is no admin user. You can set one with ```!set admin``` command!**").queue();
            }
        }
        else if (message.equalsIgnoreCase("!view users"))  {
            String result = "";

            for (String username: usersKeywords.keySet())  {
                result += username + ":" + usersKeywords.get(username) + "\n";
            }

            if (!result.equalsIgnoreCase(""))  {
                event.getChannel().sendMessage(result).queue();
            }
            else  {
                event.getChannel().sendMessage("**There are no users in the list!**").queue();
            }
        }
        else if (message.equalsIgnoreCase("!view channel"))  {
            if (channel!=null)  {
                event.getChannel().sendMessage("**Users are invited to this channel: " + channel.getName() + "**").queue();
            }
            else  {
                event.getChannel().sendMessage("**Invite channel was not set!**").queue();
            }
        }
        else if (message.equalsIgnoreCase("!help"))  {
            String resultMessage = "";
            resultMessage += "*You can use these commands to interact with the bot:* \n";
            resultMessage += "```\n";
            resultMessage += "!set users: to set a list of users the bot messages to\n";
            resultMessage += "!view users: to view the list of users\n";
            resultMessage += "!set channel: to set a channel the bot invites users to\n";
            resultMessage += "!view channel: to view the channel\n";
            resultMessage += "!set admin: to set an admin user to forward progress reports to\n";
            resultMessage += "!view admin: to view the admin user\n";
            resultMessage += "!start: to send messages to all users in the list\n";
            resultMessage += "```";

            event.getChannel().sendMessage(resultMessage).queue();
        }
        /**Managing input**/
        else if (stage==Stage.SET_ADMIN)  {
            if (!event.getAuthor().getAsTag().equalsIgnoreCase(commandStarter.getAsTag()))  {
                return;
            }
            try  {
                admin = jda.getUserByTag(message);
            }
            catch (Exception e)  {
                e.printStackTrace();
                event.getChannel().sendMessage("**Couldn't find user " + message + "!**").queue();
            }

            try {
                AtomicReference<PrivateChannel> adminChannelTemp = new AtomicReference<>();
                adminChannel = admin.openPrivateChannel().complete();

                event.getChannel().sendMessage("**Admin user " + admin.getAsTag() + " added successfully!**").queue();
            }
            catch (Exception e)  {
                e.printStackTrace();
                event.getChannel().sendMessage("**Couldn't open a private channel with user " + message + "!**").queue();
            }

            stage = Stage.NORMAL;
        }
        else if (stage==Stage.SET_CHANNEL)  {
            if (!event.getAuthor().getAsTag().equalsIgnoreCase(commandStarter.getAsTag()))  {
                return;
            }
            try  {
                channel = jda.getGuildChannelById(message);
                stage = Stage.NORMAL;
                event.getChannel().sendMessage("**Channel " + channel.getName() + " added successfully!**").queue();
            }
            catch (Exception e)  {
                e.printStackTrace();
                stage = Stage.NORMAL;
                event.getChannel().sendMessage("**Couldn't set the channel! Please check bot's permissions!**").queue();
            }
        }
        else if (stage==Stage.SET_USERS)  {
            if (!event.getAuthor().getAsTag().equalsIgnoreCase(commandStarter.getAsTag()))  {
                return;
            }
            try {
                String[] lines = message.split("\n");

                Map<String, String> usersKeywordsTemp = new HashMap<>();
                Map<String, User> usersMapTemp = new HashMap<>();

                for (String line : lines) {
                    if (line.indexOf(":")<0)  {
                        event.getChannel().sendMessage("**You need to include a message, in this format:** ```username#numbers:message```").queue();
                        return;
                    }

                    System.out.println("line: " + line);
                    String username = line.substring(0, line.indexOf(":"));
                    String keyword = line.substring(line.indexOf(":")+1);

                    User userJda =  jda.getUserByTag(username);

                    if (userJda==null)  {
                        event.getChannel().sendMessage("**Couldn't find user " + username + "!**").queue();
                    }
                    else {
                        usersKeywordsTemp.put(username, keyword);
                        usersMapTemp.put(username, userJda);
                    }
                }

                usersKeywords = usersKeywordsTemp;
                usersMap = usersMapTemp;

                stage = Stage.NORMAL;

                event.getChannel().sendMessage("**User list has been updated!**").queue();
            }
            catch (Exception e)  {
                e.printStackTrace();
                stage = Stage.NORMAL;
                event.getChannel().sendMessage("**Something went wrong. Please, check your input**").queue();
            }
        }
    }
}
