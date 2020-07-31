package com.mcsunnyside.gitcommitcreator;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
        String discordWebhook = args[0];
        String buildId = args[1];
        String buildUrl = args[2];
        String gitBranch = args[3];
        StringBuilder gitCommit = new StringBuilder();
        String jobUrl = args[4];

        InputStream stream;
        try {
            stream = Runtime.getRuntime().exec("git log -1").getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String readed = reader.readLine();
            while (readed != null) {
                gitCommit.append(readed).append("\n");
                readed = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Loading threads...");
        WebhookClientBuilder builder = new WebhookClientBuilder(discordWebhook); // or id, token
//        builder.setThreadFactory((job) -> {
//            Thread thread = new Thread(job);
//            thread.setName("Discord Webhook");
//            thread.setDaemon(true);
//            return thread;
//        });
        System.out.println("Building discord webhook client...");
        builder.setWait(true);
        WebhookClient client = builder.build();
        System.out.println("Sending");
        client.send(new WebhookEmbedBuilder()
                .setAuthor(new WebhookEmbed.EmbedAuthor("CodeMC.io", "https://docs.codemc.io/assets/img/CodeMCLogoV2.png", "https://ci.codemc.io"))
                .setColor(15258703)
                .setDescription("New build now available!")
                .setTitle(new WebhookEmbed.EmbedTitle("CodeMC.io CI Build Successfully!", jobUrl))
                .addField(new WebhookEmbed.EmbedField(true, "Branch", gitBranch))
                .addField(new WebhookEmbed.EmbedField(true, "BuildID", "#" + buildId))
                .addField(new WebhookEmbed.EmbedField(false, "Commit Message", gitCommit.toString().trim()))
                .addField(new WebhookEmbed.EmbedField(false, "Download", buildUrl))
                .setFooter(new WebhookEmbed.EmbedFooter("QuickShop provided as-is - we are not responsible for data loss or corruption. You are encouraged to back up your server before any updates!", null))
                .build()
        ).whenComplete((k, v) -> {
            System.out.println("Done");
            System.out.println(k.getChannelId());
            System.exit(0);
        });
    }

}
