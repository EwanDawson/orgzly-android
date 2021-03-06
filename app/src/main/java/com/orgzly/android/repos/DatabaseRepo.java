package com.orgzly.android.repos;

import android.net.Uri;

import com.orgzly.android.data.DbRepoBookRepository;
import com.orgzly.android.util.MiscUtils;
import com.orgzly.android.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Repo which stores all its files in a local database.
 * Used for testing by {@link com.orgzly.android.repos.MockRepo}.
 */
public class DatabaseRepo implements SyncRepo {
    private final Uri repoUri;

    private DbRepoBookRepository dbRepo;

    public DatabaseRepo(DbRepoBookRepository dbRepo, String url) {
        this.dbRepo = dbRepo;
        this.repoUri = Uri.parse(url);
    }

    @Override
    public boolean requiresConnection() {
        return false;
    }

    @Override
    public Uri getUri() {
        return repoUri;
    }

    @Override
    public List<VersionedRook> getBooks() throws IOException {
        return dbRepo.getDbRepoBooks(repoUri);
    }

    @Override
    public VersionedRook retrieveBook(String fileName, File file) throws IOException {
        Uri uri = repoUri.buildUpon().appendPath(fileName).build();
        return dbRepo.retrieveDbRepoBook(repoUri, uri, file);
    }

    @Override
    public VersionedRook storeBook(File file, String fileName) throws IOException {
        String content = MiscUtils.readStringFromFile(file);

        String rev = "MockedRevision-" + System.currentTimeMillis();
        long mtime = System.currentTimeMillis();

        Uri uri = repoUri.buildUpon().appendPath(fileName).build();

        VersionedRook vrook = new VersionedRook(repoUri, uri, rev, mtime);

        return dbRepo.createDbRepoBook(vrook, content);
    }

    @Override
    public VersionedRook renameBook(Uri fromUri, String name) throws IOException {
        Uri toUri = UriUtils.getUriForNewName(fromUri, name);
        return dbRepo.renameDbRepoBook(fromUri, toUri);
    }

    @Override
    public void delete(Uri uri) throws IOException {

    }

    @Override
    public String toString() {
        return repoUri.toString();
    }
}
