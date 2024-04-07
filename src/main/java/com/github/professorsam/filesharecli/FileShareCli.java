package com.github.professorsam.filesharecli;

import okhttp3.*;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class FileShareCli {

    @Option(name = "-e", aliases = "--expire", usage = "Set the expire date")
    private String date;
    @Option(name = "-d", aliases = "--downloads", usage = "Max download amount")
    private int downloads;
    @Option(name = "-f", aliases = "--file", usage = "File to upload", required = true)
    private File file;
    public static void main(String[] args) {
        new FileShareCli().entry(args);
    }

    private void entry(String[] args){
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);

        } catch(CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java SampleMain [options...] arguments...");
            parser.printUsage(System.err);
            return;
        }
        Instant expire = date != null ? parseDate(date) : Instant.now().plus(3, ChronoUnit.DAYS);
        if(downloads == 0){
            downloads = -1;
        }
        if(file == null || !file.exists() || !file.isFile() || !file.canExecute()){
            throw new IllegalArgumentException("File does not exist or was not found");
        }
        upload(file, expire, downloads);
    }

    private void upload(File file, Instant expire, int downloads){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://file.professorsam.de/upload")
                .addHeader("File-Expires", String.valueOf(expire.getEpochSecond()))
                .addHeader("File-Downloads", String.valueOf(downloads))
                .addHeader("File-Name", file.getName())
                .post(RequestBody.create(file, MediaType.get("application/octet-stream")))
                .build();
        try(Response response = client.newCall(request).execute()){
            if(response.isSuccessful()){
                System.out.println("Download link: " + response.body().string());
                return;
            }
            System.err.println("Could not upload file (Server error) " + response.code() + " " + response.message());
        }  catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not upload file");
        }
    }

    private Instant parseDate(String date) {
        Instant instant = Instant.now();
        StringBuilder amount = new StringBuilder();
        for(char c : date.toCharArray()){
            if(Character.isDigit(c)){
                amount.append(c);
                continue;
            }
            instant = instant.plus(Long.parseLong(amount.toString()), parseUnit(c));
            amount.setLength(0);
        }
        return instant;
    }

    private ChronoUnit parseUnit(char c){
        return switch (c){
            case 'd', 'D': yield ChronoUnit.DAYS;
            case 'w', 'W': yield ChronoUnit.WEEKS;
            case 'h', 'H': yield ChronoUnit.HOURS;
            case 'm', 'M': yield ChronoUnit.MINUTES;
            default: throw new IllegalArgumentException("Could not parse time unit '" + c +"'! Only: m,h,d,w are supported!");
        };
    }
}
