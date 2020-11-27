package it.unibo.oop.lab.lambda.ex02;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        final Stream<String> s = this.songs.stream().map(song -> song.getSongName());
        return s.sorted();
    }

    @Override
    public Stream<String> albumNames() {
        final Stream<String> a = this.albums.keySet().stream();
        return a;
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        final Stream<String> s = this.albums.entrySet().stream().filter(t -> t.getValue().equals(year)).map(t -> t.getKey());
        return s;
    }

    @Override
    public int countSongs(final String albumName) {
        final int n = (int) this.songs.stream().filter(s -> s.getAlbumName().isPresent()).filter(s -> s.getAlbumName().get().equals(albumName)).count();
        return n;
    }

    @Override
    public int countSongsInNoAlbum() {
        final int n = (int) this.songs.stream().filter(s -> s.getAlbumName().isEmpty()).count();
        return n;
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        final OptionalDouble dub = this.songs.stream().filter(s -> s.getAlbumName().isPresent()).filter(s -> s.getAlbumName().get().equals(albumName)).mapToDouble(s -> s.getDuration()).average();
        return dub;
    }

    @Override
    public Optional<String> longestSong() {
        final Optional<String> s = this.songs.stream().max((i, j) -> Double.compare(i.getDuration(), j.getDuration())).map(t -> t.getSongName());
        return s;
    }

    @Override
    public Optional<String> longestAlbum() {
        Map<String, Double> d = new HashMap<>();

        for (String album: albums.keySet()) {
            d.put(album, songs.stream().filter(t -> t.getAlbumName().isPresent()).filter(t -> t.getAlbumName().get().equals(album)).mapToDouble(t -> t.getDuration()).sum());
        }
        Optional<String> op = d.entrySet().stream().max((v1, v2) -> Double.compare(v1.getValue(), v2.getValue())).map(e -> e.getKey());
        return op;
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
