package prod;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public final class Producer {

    public static final String INPUT_TOPIC = "streams-plaintext-input";

    static Properties getStreamsConfig() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }

    static Random rand = new Random();

    static void createWordCountStream(final Properties props) {
        org.apache.kafka.clients.producer.Producer<String, String> producer = new KafkaProducer<>(props);

        for (int i = 1; i < 1000; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            producer.send(new ProducerRecord<>(INPUT_TOPIC, Integer.toString(rand.nextInt() % 10)));
        }

        producer.close();
    }

    public static void main(final String[] args) {
        final Properties props = getStreamsConfig();
        createWordCountStream(props);
        System.exit(0);
    }
}
