/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.ittiger.player.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;


/**
 * Tools for managing files.  Not for public consumption.
 *
 * @hide
 */
public class FileUtils {
    public static final int S_IRWXU = 00700;
    public static final int S_IRUSR = 00400;
    public static final int S_IWUSR = 00200;
    public static final int S_IXUSR = 00100;

    public static final int S_IRWXG = 00070;
    public static final int S_IRGRP = 00040;
    public static final int S_IWGRP = 00020;
    public static final int S_IXGRP = 00010;

    public static final int S_IRWXO = 00007;
    public static final int S_IROTH = 00004;
    public static final int S_IWOTH = 00002;
    public static final int S_IXOTH = 00001;
    /**
     * Regular expression for safe filenames: no spaces or metacharacters
     */
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("[\\w%+,./=_-]+");

    /**
     * Get the status for the given path. This is equivalent to the POSIX stat(2) system call.
     *
     * @param path   The path of the file to be stat'd.
     * @param status Optional argument to fill in. It will only fill in the status if the file exists.
     * @return true if the file exists and false if it does not exist. If you do not have permission to stat
     * the file, then this method will return false.
     */
    public static native boolean getFileStatus(String path, FileStatus status);

    public static native int setPermissions(String file, int mode, int uid, int gid);

    public static native int getPermissions(String file, int[] outPermissions);

    /**
     * returns the FAT file system volume ID for the volume mounted at the given mount point, or -1 for
     * failure
     *
     * @param mount point for FAT volume
     * @return volume ID or -1
     */
    public static native int getFatVolumeId(String mountPoint);

    /**
     * Perform an fsync on the given FileOutputStream.  The stream at this point must be flushed but not yet
     * closed.
     */
    public static boolean sync(FileOutputStream stream) {
        try {
            if (stream != null) {
                stream.getFD().sync();
            }
            return true;
        } catch (IOException e) {
        }
        return false;
    }

    /**
     * Check if a filename is "safe" (no metacharacters or spaces).
     *
     * @param file The file to check
     */
    public static boolean isFilenameSafe(File file) {
        // Note, we check whether it matches what's known to be safe,
        // rather than what's known to be unsafe.  Non-ASCII, control
        // characters, etc. are all unsafe by default.
        return SAFE_FILENAME_PATTERN.matcher(file.getPath()).matches();
    }

    /**
     * File status information. This class maps directly to the POSIX stat structure.
     *
     * @hide
     */
    public static final class FileStatus {
        public int dev;
        public int ino;
        public int mode;
        public int nlink;
        public int uid;
        public int gid;
        public int rdev;
        public long size;
        public int blksize;
        public long blocks;
        public long atime;
        public long mtime;
        public long ctime;
    }
}
