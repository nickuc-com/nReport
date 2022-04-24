package com.nickuc.report.management;

import com.nickuc.report.bootstrap.Platform;
import com.nickuc.report.model.Report;
import com.nickuc.report.model.User;
import lombok.Cleanup;

import java.io.*;
import java.util.*;

public class UserManagement {

    private static final int VERSION = 1;

    private final Map<String, User> userCacheByUuid = new HashMap<>();
    private final File userDataFolder;

    public UserManagement(Platform<?> platform) {
        userDataFolder = new File(platform.getDataFolder(), "userdata");
        if (!userDataFolder.exists() && !userDataFolder.mkdirs()) {
            throw new RuntimeException("Could not create " + userDataFolder + "!");
        }
    }

    public synchronized User getOrLoadFromCache(UUID uniqueId) {
        User user = userCacheByUuid.get(uniqueId.toString());
        if (user == null) {
            File file = new File(userDataFolder, uniqueId + ".data");
            if (file.exists()) {
                try {
                    @Cleanup FileInputStream fileInputStream = new FileInputStream(file);
                    @Cleanup DataInputStream dataInputStream = new DataInputStream(fileInputStream);

                    Map<String, Integer> reportQuantity = new HashMap<>();

                    // header
                    int version = dataInputStream.readInt();

                    // data
                    int reportCount = dataInputStream.readInt();

                    List<Report> reportList = new ArrayList<>();
                    for (int i = 0; i < reportCount; i++) {
                        String author = dataInputStream.readUTF();
                        String reason = dataInputStream.readUTF();
                        long time = dataInputStream.readLong();
                        reportList.add(new Report(author, reason, time));
                    }
                    user = new User(uniqueId, reportList);
                } catch (IOException exception) {
                    throw new RuntimeException("Could not load " + uniqueId + " from " + file + " file!", exception);
                }
            } else {
                user = new User(uniqueId, new ArrayList<>());
            }
            userCacheByUuid.put(uniqueId.toString(), user);
        }
        return user;
    }

    public synchronized void save(User user) {
        UUID uniqueId = user.getUniqueId();
        File file = new File(userDataFolder, uniqueId + ".data");
        if (file.exists()) {
            try {
                @Cleanup FileOutputStream fileOutputStream = new FileOutputStream(file);
                @Cleanup DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

                // header
                dataOutputStream.writeInt(VERSION);

                // data
                List<Report> reportList = user.getReportList();
                dataOutputStream.writeInt(reportList.size());
                for (Report report : reportList) {
                    dataOutputStream.writeUTF(report.getAuthor());
                    dataOutputStream.writeUTF(report.getReason());
                    dataOutputStream.writeLong(report.getTime());
                }
            } catch (IOException exception) {
                throw new RuntimeException("Could not load " + uniqueId + " from " + file + " file!", exception);
            }
        }
    }

    public synchronized void delete(UUID uniqueId) {
        File file = new File(userDataFolder, uniqueId + ".data");
        if (file.exists() && !file.delete()) {
            throw new RuntimeException("Could not delete " + file + " file!");
        }
        userCacheByUuid.remove(uniqueId.toString());
    }

    public synchronized void invalidate(UUID uniqueId) {
        userCacheByUuid.remove(uniqueId.toString());
    }
}
