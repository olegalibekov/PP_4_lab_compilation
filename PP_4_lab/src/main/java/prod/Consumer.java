package prod;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import static java.util.stream.Collectors.toList;

public final class Consumer {

    public static final String INPUT_TOPIC = "streams-plaintext-input";
    public static final ArrayList<Integer> resArr = new ArrayList<>();
    public static final HashMap<Integer, Integer> resMap = new HashMap<>();
    static List<String> nums;

    static Properties getStreamsConfig(final String[] args) throws IOException {
        final Properties props = new Properties();
        if (args != null && args.length > 0) {
            try (final FileInputStream fis = new FileInputStream(args[0])) {
                props.load(fis);
            }
            if (args.length > 1) {
                System.out.println("Warning: Some command line arguments were ignored. This demo only accepts an optional configuration file.");
            }
        }
        props.putIfAbsent(StreamsConfig.APPLICATION_ID_CONFIG, "streams-wordcount");
        props.putIfAbsent(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.putIfAbsent(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.putIfAbsent(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.putIfAbsent(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        // setting offset reset to earliest so that we can re-run the demo code with the same pre-loaded data
        // Note: To re-run the demo, you need to use the offset reset tool:
        // https://cwiki.apache.org/confluence/display/KAFKA/Kafka+Streams+Application+Reset+Tool
        props.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    static void insertSorting(int word) {
        for (int i = 0; i < resArr.size(); i++) {
            if (word - (resArr.get(i)) <= 0) {
                resArr.add(i, word);
                return;
            }
        }
        resArr.add(word);
    }

    public static List<Map.Entry<Integer, Integer>> showFirstMapValues(Map<Integer, Integer> map) {
        return map.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(toList());
    }

    static void createWordCountStream(final StreamsBuilder builder) {
        final KStream<String, String> source = builder.stream(INPUT_TOPIC);

        BarChartAWT chart = new BarChartAWT();

        source.flatMapValues(value -> {
            System.out.println("=================================================");
            nums = Arrays.asList(value.toLowerCase(Locale.getDefault()).split("\\W+"));
            return nums;
        }).filter((k, v) -> {
            String regex = "\\d+";
            System.out.println(v);

            if (v.matches(regex)) {
                int currentKey = Integer.parseInt(v);
                resMap.merge(currentKey, 1, ((oldValue, newValue) -> oldValue + 1));
                System.out.println(resMap);

                List<Map.Entry<Integer, Integer>> chartValues = showFirstMapValues(resMap);
                chart.updateDataset(chartValues);

                insertSorting(Integer.parseInt(v));
                System.out.println(resArr);
            }
            return true;
        });
    }

    public static void main(final String[] args) throws IOException {
        final Properties props = getStreamsConfig(args);

        final StreamsBuilder builder = new StreamsBuilder();
        createWordCountStream(builder);
        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        final CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread("streams-wordcount-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (final Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }

}