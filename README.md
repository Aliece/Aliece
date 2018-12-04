public class ChronicleMapRepository<V> extends BaseRepository<V> {

    private static final int DEFAULT_KEY_SIZE_BYTES = 128;
    private static final int DEFAULT_CHUNK_SIZE_BYTES = 512;
    private static final double DEFAULT_VALUE_SIZE_BYTES = 512;
    private static final long DEFAULT_ENTRY_COUNT = 1024 * 1024;
    public static final String DEFAULT_DIR = "/local-store/chronicle-map/";

    private ChronicleMapRepository(final ChronicleMap<String, V> chronicleMap) {
        super(chronicleMap);
    }

    @Override
    public V put(String key, V value) {
        V result = null;
        try {
            result = super.put(key, value);
        } catch (ChronicleHashClosedException e) {
            logger.warn("could not put on closed state repository", e);
        }
        return result;
    }

    @Override
    public Optional<V> get(String key) {
        Optional<V> result = Optional.empty();
        try {
            result = super.get(key);
        } catch (ChronicleHashClosedException e) {
            logger.warn("could not get on closed state repository", e);
        }
        return result;
    }

    @Override
    public long size() {
        try {
            return super.size();
        } catch (ChronicleHashClosedException e) {
            logger.warn("could not get size on closed state repository", e);
            return 0;
        }
    }

    public static <V> Builder<V> builder(Class<V> clazz) {
        return new Builder<>(clazz);
    }

    public static final class Builder<V> {

        private boolean withoutFile = true;
        private String filePath = DEFAULT_DIR;
        private String key = "data";
        private final Class<V> clazz;
        private ChronicleMapBuilder<String, V> chronicleMapBuilder;

        private Builder(Class<V> clazz) {
            this.clazz = clazz;
        }

        public Builder<V> withoutFile(boolean val) {
            withoutFile = val;
            return this;
        }

        public Builder<V> withFilePath(String val) {
            filePath = val;
            return this;
        }

        public Builder<V> withKey(String val) {
            key = val;
            return this;
        }

        public Builder<V> withMapBuilder(ChronicleMapBuilder<String, V> val) {
            chronicleMapBuilder = val;
            return this;
        }

        public ChronicleMapRepository<V> build() {

            try {
                if (chronicleMapBuilder == null) {
                    chronicleMapBuilder = ChronicleMapBuilder.of(String.class, clazz)
                            .averageKeySize(DEFAULT_KEY_SIZE_BYTES)
                            .checksumEntries(true)
                            .removeReturnsNull(true)
                            .actualChunkSize(DEFAULT_CHUNK_SIZE_BYTES)
                            .averageValueSize(DEFAULT_VALUE_SIZE_BYTES)
                            .entries(DEFAULT_ENTRY_COUNT);
                }

                boolean doesClassNeedToBeSerialized = clazz != String.class;
                if (doesClassNeedToBeSerialized) {
                    chronicleMapBuilder.valueMarshaller(
                            new ChronicleMapKryoBytesMarshaller<>());
                }
                if (withoutFile) {
                    return new ChronicleMapRepository<>(chronicleMapBuilder.create());
                } else {
                    boolean mkDirRst = LocalMemManager.checkAndMkdirs(filePath);
                    if (!mkDirRst) {
                        throw new LocalMemException("LocalStore store mkdirs error.");
                    }
                    File file = new File(filePath + File.separator + key);
                    return new ChronicleMapRepository<>(
                            chronicleMapBuilder.createOrRecoverPersistedTo(file));
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException("ChronicleMapRepository build error");
            }
        }
    }
}

public class BaseRepository<V> implements AutoCloseable {

    public static final Logger logger = LoggerFactory.getLogger(BaseRepository.class);

    private ConcurrentMap<String, V> concurrentMap;

    public BaseRepository(final ConcurrentMap<String, V> concurrentMap) {
        this.concurrentMap = concurrentMap;
    }

    public BaseRepository() {
    }

    public V compute(final String key,
            final BiFunction<? super String, ? super Optional<V>, ? extends V> remappingFunction) {
        return concurrentMap.compute(key, (k, v) -> remappingFunction.apply(k, ofNullable(v)));
    }

    public V put(final String key,
            final V value) {
        return concurrentMap.put(key, value);
    }

    public void remove(final String key) {
        concurrentMap.remove(key);
    }

    public void clear() {
        concurrentMap.clear();
    }

    public Optional<V> get(final String key) {
        return ofNullable(concurrentMap.get(key));
    }

    public Set<String> keySet() {
        return concurrentMap.keySet();
    }

    public long size() {
        return concurrentMap.size();
    }

    @Override
    public void close() throws Exception {
        logger.info("Closing StateRepository.");
        if (concurrentMap instanceof AutoCloseable) {
            ((AutoCloseable) concurrentMap).close();
        }
    }
}

public class ChronicleMapKryoBytesMarshaller<V> implements
        BytesWriter<V>,
        BytesReader<V>,
        ReadResolvable<ChronicleMapKryoBytesMarshaller> {

    public ChronicleMapKryoBytesMarshaller() {
    }

    @NotNull
    @Override
    public ChronicleMapKryoBytesMarshaller readResolve() {
        return this;
    }

    @NotNull
    @Override
    public V read(Bytes in, @Nullable V v) {
        if (v != null) {
            throw new UnsupportedOperationException();
        }

        try (InputStream is = in.inputStream()) {
            return KryoUtils.readObjectFromInputStream(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(Bytes out, @NotNull V v) {
        try (OutputStream os = out.outputStream()) {
            KryoUtils.writeToObject(os, v);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
